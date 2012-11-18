/*
 *	AudioClip.java
 *
 */
package jig.engine.audio.jsound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import jig.engine.ResourceFactory;

/**
 * Purpose: Preload and playback an audio file using a Java Sound DataLine Clip.
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
 * This class loads a supported (Java Sound + SPI Plugins) audio file COMPLETELY
 * prior to playback at the desired gain (volume). Because the data is
 * pre-loaded rather than streamed, the Clip's duration is known before playback
 * and the Clip can be looped (repeated) a specified number of times or
 * indefinitely. Each time play() is called, a new instance of ClipPlayback is
 * instantiated. An audio file can be played multiple times simultaneously but
 * is loaded into memory only once.
 * <p>
 * Using this class to play sounds that are relatively short in duration and
 * that need to be played multiple times with low latency.
 * <li> For longer duration audio and/or large files ( > 2 MB ?), use
 * AudioStream.
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
 * @see ClipPlayback
 * @see AudioStream
 */

public class AudioClip {

	/**
	 * The maximum size (in bytes) of the clip before a warning is displayed.
	 * Based on java documentation, Clips that are too big, may result in poor
	 * performance.
	 */
	private static final int MAXIMUM_SAFE_SIZE = 2097152;

	/**
	 * The size of the input buffer that will temporarily hold the data read
	 * from audioInStream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The default value of Master-Gain used in absence of a user-supplied gain
	 * value. A value of 1.0 will play the AudioClip at the Java Sound default
	 * volume level.
	 */
	private static final double DEFAULT_GAIN = 1.0;

	/**
	 * An input stream with a specified audio format and length. The length is
	 * expressed in sample frames, not bytes.
	 */
	private AudioInputStream audioInStream;

	/**
	 * Specifies the particular arrangement of data in audioInStream.
	 */
	private AudioFormat audioFormat;

	/**
	 * An array of converted bytes from a ByteArrayOutputStream. This will hold
	 * the data that will be used by a Clip at each call to play().
	 */
	private byte[] audioBytes;

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
	protected String fileName;

	/**
	 * The ClipPlayback that will be used when one of the play() methods are
	 * called. This object will be created during the previous call to a play
	 * method and thus "prepped" for the current playback. Based on test
	 * results, this "prepping" of the ClipPlayBack helped to decrease latency.
	 */
	private ClipPlayback currentPlay;

	/**
	 * The ClipPlayback that will be used for the next playback (call to one of
	 * the play() methods). See currentPlay above.
	 */
	private ClipPlayback nextPlay;

	/**
	 * Create a new instance of <code>AudioClip</code>. For each audio file
	 * that we want played, only one instance of AudioClip is needed.
	 * 
	 * @param fileName
	 *            The path and name of the audio file.
	 */
	protected AudioClip(final String fileName) {
		this.fileName = fileName;
		exec = Executors.newCachedThreadPool();
	}

	/**
	 * A static factory method used to prepare an <code>AudioClip</code>.
	 * 
	 * @param filename
	 *            the name of the file storing the audio data
	 * @return a new <code>AudioClip</code> or null upon failure.
	 */
	public static AudioClip createAudioClip(final String filename) {
		AudioClip newClip = new AudioClip(filename);
		if (!newClip.acquireAudioInputStream() || !newClip.loadDataAndPrep()) {
			return null;
		}
		return newClip;
	}

	/**
	 * Acquire an AudioInputStream based on the audio file data from the file
	 * URL. If the audio file is in mp3 format, a call to mp3ToPCM will be made
	 * and the AudioInputStream will be based on the new audio data.
	 * 
	 * @return <code>true</code> on success
	 */
	private boolean acquireAudioInputStream() {
		try {
			ResourceFactory.getFactory();
			URL fileURL = ResourceFactory.findResource(fileName);
			if (fileURL == null) {
				return false;
			}
			audioInStream = AudioSystem.getAudioInputStream(fileURL);
		} catch (UnsupportedAudioFileException ex) {
			ResourceFactory.getJIGLogger().warning(
					"Unsupported Audio File: could not load " + fileName);
			return false;
		} catch (IOException ex) {
			ResourceFactory.getJIGLogger().warning(
					"IO Exception: could not load " + fileName);
			return false;
		}
		// If we have an mp3 or ogg, decode AudioInputStream to PCM
		if (fileName.endsWith(".mp3") || fileName.endsWith(".ogg")) {
			decodeToPCM();
		}
		// save the format
		audioFormat = audioInStream.getFormat();
		return true;
	}

	/**
	 * Read the audio data from the AudioInputStream in chunks and write it to a
	 * ByteArrayOutputStream which will then be converted into a byte array.
	 * 
	 * @return Success
	 */
	private boolean loadDataAndPrep() {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		byte[] inBuffer = new byte[BUFFER_SIZE
				* audioInStream.getFormat().getFrameSize()];

		// Read ALL the data from the AudioInputStream in chunks until there
		// is no more to read.
		try {
			while (true) {
				int bytesRead = audioInStream.read(inBuffer);
				if (bytesRead == -1) {
					break;
				}
				byteOutStream.write(inBuffer, 0, bytesRead);
			}

		} catch (IOException ex) {
			System.err.println("ERROR: Could not write to output stream");
			ex.printStackTrace();
			return false;
		} finally {
			try {
				// release system resources
				audioInStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if (byteOutStream.size() > MAXIMUM_SAFE_SIZE) {
			ResourceFactory.getJIGLogger().warning(
					"WARNING: AudioClip '" + fileName
							+ "' exceeds the 2MB limit, it may behave poorly.");
		}
		audioBytes = byteOutStream.toByteArray();

		// prep for the initial playback
		nextPlay = new ClipPlayback(audioFormat, audioBytes);

		return true;
	}

	/**
	 * Play back this AudioClip numLoops times at the default gain level.
	 * 
	 * @param numLoops
	 *            The number of times this AudioClip will be played in
	 *            succession. A value of <= 0 will loop continuously
	 * @return The ClipPlayback which represents the current AudioClip playback.
	 */
	public ClipPlayback loop(final int numLoops) {
		return playClip(DEFAULT_GAIN, numLoops);
	}

	/**
	 * Play back this AudioClip numLoops times at the desired gain level.
	 * 
	 * @param numLoops
	 *            The number of times this AudioClip will be played in
	 *            succession. A value of <= 0 will loop continuously
	 * @param gain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Clip
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 is the loudest playback.
	 * @return The ClipPlayback which represents the current AudioClip playback.
	 */
	public ClipPlayback loop(final int numLoops, final double gain) {
		return playClip(gain, numLoops);
	}

	/**
	 * Convert an mp3 or ogg AudioInputStream to PCM. This method is the only
	 * code that pertains specifically to encoded mp3 or ogg files.
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
		// Decode
		audioInStream = AudioSystem.getAudioInputStream(decodedFormat,
				audioInStream);
	}

	/**
	 * Play back this AudioClip once at the default gain level.
	 * 
	 * @return The ClipPlayback which represents the current AudioClip playback.
	 */
	public ClipPlayback play() {
		return playClip(DEFAULT_GAIN, 1);
	}

	/**
	 * Play back this AudioClip once at the desired gain level.
	 * 
	 * NOTE: Depending on the quality of the input audio file, distortion can
	 * occur at high gain values.
	 * 
	 * @param gain
	 *            A double from 0.0001 to 2.0 that controls the gain of the Clip
	 *            and thus the volume. A value of 0.0001 mutes the playback. 1.0
	 *            is the default level, and 2.0 will produce the loudest
	 *            playback.
	 * 
	 * @return The ClipPlayback which represents the current AudioClip playback.
	 */
	public ClipPlayback play(final double gain) {
		return playClip(gain, 1);
	}

	/**
	 * Call the current ClipPlayback to begin playback and create a ClipPlayback
	 * for the next playback. This method is called by all the public play()
	 * methods.
	 * 
	 * NOTE: "Preloading" a new ClipPlayback object for the next play back helps
	 * to decrease latency.
	 * 
	 * @param gain
	 *            The gain value for the current playback.
	 * @param numLoops
	 *            The number of times the current playback will occur.
	 * @return The ClipPlayback which is currently being played.
	 */
	private ClipPlayback playClip(final double gain, final int numLoops) {
		currentPlay = nextPlay;
		currentPlay.start(gain, numLoops, exec);
		nextPlay = new ClipPlayback(audioFormat, audioBytes);
		return currentPlay;
	}
}
