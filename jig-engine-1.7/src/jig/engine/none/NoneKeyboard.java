package jig.engine.none;

import jig.engine.GameFrame;
import jig.engine.KeyInfo;
import jig.engine.Keyboard;

/**
 * 

 * @author Scott Wallace
 * 
 * Changed methods from static (class methods) to instance methods (non static)
 * 
 * TODO: Currently, there is no emulation of the console.
 * 
 */
class NoneKeyboard implements Keyboard {
	/** The status of the keys on the keyboard. */
	private volatile boolean[] keys = new boolean[1024];

	private java.util.concurrent.ConcurrentLinkedQueue<KeyInfo> q;

	
	/**
	 *
	 * @param gf the GameFrame to which this keyboard is attached.
	 */
	NoneKeyboard(final GameFrame gf) {
		q = new java.util.concurrent.ConcurrentLinkedQueue<KeyInfo>();
	}

	/**
	 * Puts the keyboard into its initial/default state.  This method
	 * typically will only be called in testing code, not in actual game code.
	 */
	public void reset() {
		q.clear();
		for (int i = 0; i < 1024; i++) keys[i] = false;
		
	}
	
	/**
	 * Check if a specified key is pressed.
	 * 
	 * @param key
	 *            The code of the key to check (defined in KeyEvent)
	 * @return True if the key is pressed
     * DESIGN should this return false if console mode is active?
	 */
	public boolean isPressed(final int key) {
		return keys[key];
	}

	void enqueue(KeyInfo ki) {
		q.add(ki);
		keys[ki.getCode()] = ki.wasPressed();

	}
	/**
	 * Set the status of the key.
	 * 
	 * @param key
	 *            The code of the key to set
	 * @param pressed
	 *            The new status of the key
	 */
	public void setPressed(final int key, final boolean pressed) {
		keys[key] = pressed;
	}

		
	public KeyInfo get() {
    	return q.poll();
 	}
	public void poll() {
		// Not required for this implementation	
	}
}
