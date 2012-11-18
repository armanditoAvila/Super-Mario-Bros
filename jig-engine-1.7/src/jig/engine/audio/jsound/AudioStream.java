/*
 * AudioStream.java
 *
 */
package jig.engine.audio.jsound;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import jig.engine.audio.AudioState;

/**
 * Purpose: Simultaneously load and playback an audio file using a Java Sound
 * DataLine SourceDataLine. "On-the-fly playback".
 * <li> We will be using a Line of type SourceDataLine.
 * <p>
 * Description: This class loads and plays an audio file, most likely (depending
 * on file size) at the same time (streaming audio via buffering). This is
 * advantageous when a large audio file (maybe a large wav or mp3 for backgound
 * music) needs to be played. Preloading such a large file would greatly
 * increase latency and/or use all the heap space.
 * <p>
 * The fundamental differences between AudioStream and AudioClip are:
 * <p>
 * 1) AudioClip preloads all the audio data before playback can occur while
 * AudioStream begins playback during loading as described above.
 * <p>
 * 2) One instance of AudioClip can be created, and then multiple calls to
 * play/loop will allow multiple playbacks. With AudioStream, multiple calls to
 * play will not produce new audible playbacks. So, one AudioClip object can be
 * associated with many playbacks while one AudioStream object is associated
 * with just one playback. If you need multiple, simultaneous playbacks using
 * AudioStream, each playback will need its own AudioStream instance.
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
 * <li> tritonus_jorbis-0.3.6.jar
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
 * @author Christian Holton
 * @version 1.2 Date: 06/01/2007
 * @see AudioClip
 */

public class AudioStream {

	/**
	 * Represents one instance of an AudioStream playback.
	 */
	private final class StreamPlayback implements Runnable {
		/**
		 * Create a new instance of StreamPlayback that will be run in its own
		 * thread.
		 */
		private StreamPlayback() {
			AudioFormat audioFormat = audioInStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFormat);
			try {
				line = (SourceDataLine) AudioSystem.getLine(info);
				if (line != null) {
					line.open(audioFormat);
				}
				if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					gainCtrl = (FloatControl) line
							.getControl(FloatControl.Type.MASTER_GAIN);
				} else {
					System.out.println("No Master-Gain control");
				}
			} catch (LineUnavailableException ex) {
				ex.printStackTrace();
			}
		}

		public void run() {
			setGain(gain);
			// allow the line to engage in data I/O
			int bytesRead;
			byte[] audioData = new byte[BUFFER_SIZE];
			line.start();
			try {
				for (int i = 0; i < numLoops; i++) {
					if (state == AudioState.STOPPED) {
						break;
					}
					bytesRead = 0;
					while ((bytesRead = audioInStream.read(audioData, 0,
							audioData.length)) != -1) {
						if (state == AudioState.PAUSED) {
							if (line.isRunning()) {
								line.stop();
							}
							lock.lock(); // block
							lock.unlock();
						}
						if (!line.isRunning()) {
							line.start();
						}
						// playback
						if (state != AudioState.STOPPED) {
							state = AudioState.PLAYING;
							line.write(audioData, 0, bytesRead);
						}
					}
					acquireAudioInputStream();
				}
				// Important: drain() works like a flush on an output stream
				line.drain();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				stop();
			}
		}
	}

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
	 * The default value of Master-Gain used when no user-supplied gain value is
	 * available. A value of 1.0 will play the AudioClip at the Java Sound
	 * default volume level.
	 */
	private static final double DEFAULT_GAIN = 1.0;

	/**
	 * The size of the input buffer that will temporarily hold data read from
	 * AudioInStream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * An input stream with a specified audio format and length. The length is
	 * expressed in sample frames, not bytes.
	 */
	private AudioInputStream audioInStream;

	/**
	 * A DataLine that receives audio data for playback.
	 */
	private SourceDataLine line;

	/**
	 * Manages the details of thread use and used instead of explicitly creating
	 * threads.
	 * 
	 * NOTE: Based on the test results of this class, the Executor helped to
	 * decrease playback latency.
	 */
	private ExecutorService exec;

	/**
	 * The name and path of the audio file.
	 */
	private String fileName;

	/**
	 * A synchronization lock.
	 */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Allows control of the audio over a range of floating-point values.
	 */
	private FloatControl gainCtrl;

	/**
	 * The value of the master-gain for this AudioStream's Line. The gain acts
	 * as a volume control.
	 */
	private double gain;

	/**
	 * The object created and ran in its own thread for play back.
	 */
	private StreamPlayback playback;

	/**
	 * The current state of this AudioStream.
	 */
	private AudioState state = AudioState.PRE;

	/**
	 * The number of times the AudioStream sample will be played in succession.
	 */
	private int numLoops;;

	/**
	 * Create a new instance of AudioStream. For each audio file that we want
	 * played, only one instance of AudioStream and one instance of the inner
	 * class StreamPlayback will be created.
	 * 
	 * @param fileName
	 *            The path and name of the audio file.
	 */
	public AudioStream(final String fileName) {
		this.fileName = fileName;
		exec = Executors.newSingleThreadExecutor();
		acquireAudioInputStream();
		// prep for the initial playback
		playback = new StreamPlayback();
	}

	/**
	 * Acquire an AudioInputStream based on the audio file data from the file
	 * URL. If the audio file is in mp3 or ogg format, a call to decodeToPCM
	 * will be made and the AudioInputStream will be based on the new audio
	 * data.
	 * 
	 * @throw UnsupportedAudioFileException If the file format is unsupported by
	 *        the Java Sound API and any SPI plugins being used.
	 * @throw IOException IO operation failed.
	 * 
	 * DESIGN: consider throwing some/all of these and/or using a static factory
	 * method to hide them
	 */
	private void acquireAudioInputStream() {
		try {
			URL fileURL = ClassLoader.getSystemResource(fileName);
			audioInStream = AudioSystem.getAudioInputStream(fileURL);
		} catch (UnsupportedAudioFileException ex) {
			System.err.println("ERROR: Could not load " + fileName);
		} catch (IOException ex) {
			System.err.println("ERROR: Could not load " + fileName);
			ex.printStackTrace();
		}
		// If we have an encoded mp3 or ogg file, decode AudioInputStream to PCM
		if (fileName.endsWith(".mp3") || fileName.endsWith(".ogg")) {
			decodeToPCM();
		}
	}

	/**
	 * Get the current state of this AudioStream (ie playing, stopped, etc).
	 * 
	 * @return The current AudioState of this AudioStream.
	 */
	public AudioState getState() {
		return state;
	}

	/**
	 * Convert the mp3 or ogg AudioInputStream to PCM. This method is the only
	 * code that pertains specifically to mp3s or ogg.
	 * 
	 * From the AudioInputStream, i.e. from the sound file, we fetch information
	 * about the format of the audio data. This information includes the
	 * sampling frequency, the number of channels, and the size of the samples.
	 * This information is needed to ask Java Sound for a suitable output line
	 * for this AudioInputStream.
	 */
	private void decodeToPCM() {
		AudioFormat baseFormat = audioInStream.getFormat();

		// Create an AudioFormat with PCM encoding
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
				16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(), false);
		// Convert
		audioInStream = AudioSystem.getAudioInputStream(decodedFormat,
				audioInStream);
	}

	/**
	 * Pause playback. To resume playback, resume() must be called and playback
	 * will resume at the Clip's last position played .
	 */
	public void pause() {
		if (line.isRunning() && getState() == AudioState.PLAYING) {
			lock.lock();
			state = AudioState.PAUSED;
		}
	}

	/**
	 * Play back this AudioStream once at the default gain level.
	 */
	public void play() {
		playStream(DEFAULT_GAIN, 1);
	}

	/**
	 * Play back this AudioStream once at the desired gain level.
	 * 
	 * NOTE: Depending on the quality of the input audio file, distortion can
	 * occur at high gain values.
	 * 
	 * @param gain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Line
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 will produce the loudest
	 *            playback.
	 */
	public void play(final double gain) {
		playStream(gain, 1);
	}

	/**
	 * Play back this AudioStream numLoops times at the desired gain level.
	 * 
	 * @param gain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Clip
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 is the loudest playback.
	 * 
	 * @param numLoops
	 *            The number of times this AudioStream will be played in
	 *            succession. A value of <= 0 will loop continuously
	 */
	public void loop(final double gain, final int numLoops) {
		playStream(gain, numLoops);

	}

	/**
	 * Call StreamPlayback to begin playback This method is called by all the
	 * public play() methods. An AudioStream can only begin play once when it is
	 * in AudioState "PRE".
	 * 
	 * @param gain
	 *            The gain value for the current playback.
	 */
	private void playStream(final double gain, final int numberLoops) {
		if (getState() == AudioState.PRE) {
			setGain(gain);
			this.numLoops = (numberLoops <= 0) ? Integer.MAX_VALUE
					: numberLoops;
			exec.execute(playback);
		}
	}

	/**
	 * Resume playback after pause() has been called. Playback will resume at
	 * the Line's last position played.
	 */
	public void resume() {
		if (!line.isRunning() && getState() == AudioState.PAUSED) {
			lock.unlock();
			state = AudioState.PLAYING;
		}
	}

	/**
	 * Sets the gain value for the Line.
	 * 
	 * @param newGain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Line
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
	 * Permanently stop play back. If stop is called during playback or the end
	 * of the audio sample has been reached, the AudioStream will no longer be
	 * able to play the audio sample.
	 */
	public void stop() {		
		line.stop();
		state = AudioState.STOPPED;
		// release system resources
		line.close();
		try {
			// release system resources
			audioInStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}		
	}
}
