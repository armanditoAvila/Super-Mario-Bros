/*
 * AudioState.java
 * 
 */

package jig.engine.audio;

import jig.engine.audio.jsound.ClipPlayback;


/**
 * Purpose: Define all the possible states that <code>ClipPlayback</code>,
 * <code>AudioStream</code>, and <code>ALPlayback</code> can be in.
 * <p>
 * 
 * @author Christian Holton
 * @version 1.0 Date: 06/01/2007
 * 
 * @see ClipPlayback
 * @see AudioStream
 * @see ALPlayback
 */

public enum AudioState {
	PRE, // The AudioClip or AudioStream has been created but a
	// play() or loop() method has not yet been called on it. This state does
	// not apply to ALPlayback.
	PLAYING, STOPPED, PAUSED,
}
