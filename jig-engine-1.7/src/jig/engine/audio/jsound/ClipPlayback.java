/*
 *	ClipPlayback.java
 *
 */
package jig.engine.audio.jsound;

import java.util.concurrent.ExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import jig.engine.ResourceFactory;
import jig.engine.audio.AudioState;

/**
 * Purpose: Represents a playback instance of AudioClip.
 * <p>
 * Description: First, some clarification:
 * <p>
 * AudioClip: Represents a sample of audio. An AudioClip's play and loop methods
 * will begin playback of the audio by creating and then running a ClipPlayback
 * object.
 * <p>
 * ClipPlayback: Represents a playback instance (either a single playing of the
 * audio sample or multiple plays = loops) and executes in its own thread.
 * AudioClip's play/loop methods will return a ClipPlayback instance. From that
 * point on, the audio sample can be paused/resumed/stopped through
 * ClipPlayback's methods.
 * <p>
 * Clip: A Java Sound Line whose audio data can be loaded prior to playback.
 * <p>
 * <li>To play files in mp3 format, the following libraries need to be on the
 * classpath:
 * <li> mp3spi1.9.2.jar
 * <li> jl1.0.jar
 * <li> tritonus_share.jar
 * <p>
 * <li> and are currently available at:
 * <li> <a href="http://www.javazoom.net/">Javazoom</a>
 * <p>
 * <li>To play files in ogg format, the following libraries need to be on the
 * classpath:
 * <li> jogg-0.0.7.jar
 * <li> jorbis-0.0.15.jar
 * <li>	tritonus_jorbis-0.3.6.jar
 * <li> tritonus_share.jar
 * <p> 
 * <li> and are currently available at:
 * <li> <a href="http://www.tritonus.org/plugins.html">tritonus.org</a>
 * <p>
 * <li> The following were excellent resources during design and implementation:
 * <li> <a href=
 * "http://java.sun.com/j2se/1.5.0/docs/guide/sound/programmer_guide/contents.html"
 * >Java Sound Programmers Guide </a>
 * <li> <a href="http://www.jsresources.org/">Java Sound Resources </a>
 * <li> <a href="http://www.javalobby.org/java/forums/t18465.html"> Play MP3s
 * with Javazoom</a>
 * 
 * @version 1.2 Date: 06/01/2007
 * 
 * @author Christian Holton
 * @see AudioClip
 * @see AudioStream
 * 
 */

public class ClipPlayback implements Runnable, LineListener {

	/**
	 * The minimum value for the Line's master-gain and will mute the volume.
	 */
	private static final double MIN_GAIN = 0.0001;

	/**
	 * The maximum value for the Line's master-gain and will produce the loudest
	 * volume.
	 */
	private static final double MAX_GAIN = 2.0;

	/**
	 * A DataLine that is preloaded and has a known length. The audio file will
	 * be loaded into memory once at instantiation and then each call to play()
	 * will create a Clip in its own thread.
	 */
	private Clip clip;

	/**
	 * The value of the master-gain for this AudioStream's Line. The gain acts
	 * as a volume control.
	 */
	private double gain;

	/**
	 * The number of times the Clip will be played in succession.
	 */
	private int numLoops;

	/**
	 * Allows control of the audio over a range of floating-point values.
	 */
	private FloatControl gainCtrl;

	/**
	 * The current state of this <code>ClipPlayback</code>.
	 */
	private AudioState state = AudioState.PRE;

	/**
	 * Flag for continuous looping.
	 */
	private boolean loopContinuously = false;

	/**
	 * Create a new instance of ClipPlayback. ClipPlayback objects will always
	 * be created by an AudioClip object.
	 * 
	 * NOTE: Originally, the fetching of a new Line was done in run(), however
	 * testing revealed that latency is decreased if a Line is acquired here in
	 * the constructor, before the actual play back occurs.
	 * 
	 * @param audioFormat
	 *            An input stream with a specific audio format and length.
	 * @param audioBytes
	 *            Holds the data from which a Clip will be created.
	 * @throws LineUnavailableException Could not acquire a Java Sound Line of
	 *        the desired specifications.
	 */
	protected ClipPlayback(final AudioFormat audioFormat,
			final byte[] audioBytes) {		
		/*
		 * We have to construct an Info object that specifies the desired
		 * properties for the line. First, we have to say which kind of line we
		 * want. The possibilities are: SourceDataLine(for streaming playback),
		 * Clip (for preloaded, repeated playback) and TargetDataLine(for
		 * recording). Here, we are asking for a Clip. Then, we have to pass an
		 * AudioFormat object, so that the Clip knows which format the data
		 * passed to it will have.
		 */
		DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
		try {
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioFormat, audioBytes, 0, audioBytes.length);
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				gainCtrl = (FloatControl) clip
						.getControl(FloatControl.Type.MASTER_GAIN);
			} else {
				ResourceFactory.getJIGLogger().info("No Master-Gain control");
			}
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		clip.addLineListener(this);
	}

	/**
	 * Get the current state of this ClipPlayback (ie playing, stopped, etc).
	 * 
	 * @return The current AudioState of this ClipPlayback.
	 */
	public AudioState getState() {
		return state;
	}

	/**
	 * Pause playback. To resume playback, resume() must be called and playback
	 * will resume at the Clip's last position played .
	 */
	public void pause() {
		if (clip.isRunning() && getState() == AudioState.PLAYING) {
			state = AudioState.PAUSED; // this must be before clip.stop()
			clip.stop();		
		}
	}

	/**
	 * Resume playback after pause() has been called. Playback will resume at
	 * the Clip's last position played.
	 * 
	 * NOTE: According to Java documentation, pausing and resuming a Clip should
	 * only require a simple Clip.stop() and then Clip.start() or Clip.loop().
	 * However, when stopping a Clip during play back that began via loop(),
	 * resuming playback (with loop()) will result in the Clip being played a
	 * seemingly unpredicatable number of times (usually 2 or 3), thus the
	 * numLoops value will become useless. This method provides a solution. One
	 * very small issue with the fix: If a user repeatedly pauses/resumes in
	 * rapid succession during a short duration clip (during one of the loops),
	 * the number of times the clip is played will be one more than numLoops.
	 * For example, numLoops=5, the user pauses/resumes many times during a
	 * single loop, the clip will play 6 times not 5.
	 */
	public void resume() {
		if (!clip.isRunning() && getState() == AudioState.PAUSED) {
			long loopsPlayed = clip.getMicrosecondPosition()
					/ clip.getMicrosecondLength();

			int loopsToGo = (numLoops - (int) loopsPlayed);

			if (loopsToGo <= 0 && !loopContinuously) {
				loopsToGo = 1;
			} else if (loopContinuously) {
				loopsToGo = Clip.LOOP_CONTINUOUSLY;
			}
			state = AudioState.PLAYING;			
			clip.loop(loopsToGo);
		}
	}

	/**
	 * Call Clip.loop() and begin play back.
	 */
	public void run() {		
		state = AudioState.PLAYING;
		clip.loop(numLoops);
		// IMPORTANT: drain() works like a flush on an output stream
		clip.drain();
	}

	/**
	 * Sets the gain value for the Clip.
	 * 
	 * @param newGain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Clip
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 is the loudest playback.
	 *            gainCtrl.setValue() takes a value from -80.00 to 6.0206.
	 */
	public void setGain(double newGain) {				
		newGain = (newGain < MIN_GAIN ? MIN_GAIN : newGain);
		newGain = (newGain > MAX_GAIN ? MAX_GAIN : newGain);				
		gain = newGain;
		float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);

		if (gainCtrl != null) {
			gainCtrl.setValue(dB);
		}
	}

	/**
	 * Initialize gain and numLoops for this ClipPlayback, set the gain of the
	 * Clip, and then call the Executor to begin this play back thread.
	 * 
	 * @param gain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Clip
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 is the loudest playback.
	 *            gainCtrl.setValue() takes a value from -80.00 to 6.0206.
	 * @param numberLoops
	 *            The number of times this AudioClip will be played in
	 *            succession. A value of <= 0 will loop continuously
	 * @param exec
	 *            Manages all the threads needed to run (possibly) multiple
	 *            ClipPlaybacks. Passed in here so that this ClipPlayback object
	 *            can start its thread.
	 */
	protected void start(final double gain, int numberLoops,
			final ExecutorService exec) {		
		this.gain = gain;
		this.numLoops = --numberLoops; // By definition, loop(0) plays once
		if (numberLoops < 0) {
			this.numLoops = Clip.LOOP_CONTINUOUSLY; // (-1)
			loopContinuously = true;
		}
		setGain(gain);
		exec.execute(this);
	}

	/**
	 * Permanently stop play back. Release system resources.
	 */
	public void stop() {
		state = AudioState.STOPPED;
		clip.stop();
		clip.close();	
	}

	/**
	 * Implements update() in the LineListener interface. This method is called
	 * whenever the clip has a line event.
	 * 
	 * @param event
	 *            Encapsulates the line event information sent from clip.
	 */
	public void update(final LineEvent event) {
		if ((event.getType() == LineEvent.Type.STOP)
				&& (state != AudioState.PAUSED)) {
			// only called when the clip is done playing its media.
			
			stop();
			if (loopContinuously) {
				// if this message is thrown, an audio system bug has occurred
				// you can work around it by detecting in your game if the
				// clip is in a state STOPPED and if so, starting a new one
				// so long as this doesn't happen very frequently, you will
				// still have the benefit of 'saving' object & thread creation
				// which is the main benefit to pausing
				ResourceFactory.getJIGLogger().fine("Audio Clip has stopped prematurely...(ref: ClipPlayback)");
			}

		}
	}
}
