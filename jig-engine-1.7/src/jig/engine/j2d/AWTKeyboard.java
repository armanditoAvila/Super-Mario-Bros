package jig.engine.j2d;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import jig.engine.GameFrame;
import jig.engine.KeyInfo;
import jig.engine.Keyboard;

/**
 * 
 * A pollable keyboard system.
 *
 * Based on the Keyboard class by Kevin Glass which can be found at:
 * http://www.cokeandcode.com/spaceinvaderstutorial
 * 
 * Used here and re-released under the licensing terms of the JIG engine with
 * permission of the original author.
 * 
 * @author Kevin Glass
 * @author Scott Wallace
 * 
 * Changed methods from static (class methods) to instance methods (non static)
 * 
 */
class AWTKeyboard implements Keyboard {
	/** 
	 * The status of the keys on the keyboard. 
	 * We expect the keys array to be accessed both by the AWT-Event thread
	 * (which will be receiving key events) and by the 'main' thread (which
	 * will be running the game logic). Since we're not synchronizing, we
	 * need to make sure the array is volatile so fresh values are always
	 * read by the 'main' thread.
	 * 
	 * TODO: validate and implement
	 *   approach 2 is probably fine, but figure out what volatile means ;)
	 *   this /should/ fix the 'fs' command.
	 *   
	 * approach 1: keys[] is volatile, and we toggle the value in AWT thread but
	 *    always see fresh/consistent values in all threads since its volatile
	 *    
	 *   this breaks down (if it even works to begin with) when we deal with console
	 *   commands that run their own things because they may do stuff in the AWT thread
	 *   which should be done on the main thread
	 *   
	 *   So, to avoid that we could buffer console input (chars consumed for the console)
	 *   and check to see if we need to run a console command when poll() is called (which
	 *   should happen every frame)
	 *   
	 * approach 2: buffer everything in the CLQ and put everything including changes to 
	 *    keys[] in poll method()
	 *    
	 * approach 3: synchronize ( I think this is a bad idea)        
	 *
	 * 
	 */
	private volatile boolean[] keys = new boolean[1024];

	private volatile boolean consumeForConsole;
		
	private java.util.concurrent.ConcurrentLinkedQueue<KeyEvent> q;
	private java.util.concurrent.ConcurrentLinkedQueue<Character> consoleBuffer;

	
	private GameFrame gameframe;
	
	/**
	 * Creates a pollable Keyboard using the AWT Event Handling mechanisms.
	 *
	 * @param gf the GameFrame to which this keyboard is attached.
	 */
	AWTKeyboard(final GameFrame gf) {
		gameframe = gf;
		consumeForConsole = false;
		q = new java.util.concurrent.ConcurrentLinkedQueue<KeyEvent>();
		consoleBuffer = new java.util.concurrent.ConcurrentLinkedQueue<Character>();
	}

	/**
	 * Initialize the central keyboard handler.
	 */
	public void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyHandler(),
				AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * Initialize the central keyboard handler.
	 * 
	 * @param c
	 *            The component that we will listen to
	 */
	public void init(final Component c) {
		c.addKeyListener(new KeyHandler());
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

	/**
	 * Set the status of the key.
	 * 
	 * @param key
	 *            The code of the key to set
	 * @param pressed
	 *            The new status of the key
	 */
	//TODO: If we want to allow this, we need to synchronize instead of
	//just volatile
	//public void setPressed(final int key, final boolean pressed) {
	//	keys[key] = pressed;
	//}

	/**
	 * A class to respond to key presses on a global scale.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyHandler extends KeyAdapter implements AWTEventListener {
		/**
		 * Notification of a key press.
		 * 
		 * @param e
		 *            The event details
		 */
		@Override
		public void keyPressed(final KeyEvent e) {
			/*
			{
				String ts = Thread.currentThread().toString();
				if (!ts.contains("[AWT")) {
					Exception ex = new IllegalArgumentException();
					System.out.println(ex.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
				}
			}
*/
			if (e.isConsumed()) {
				return;
			}
			if (e.getKeyChar() == '`') {
				consumeForConsole = gameframe.getConsole().toggleDisplay();
			} else {
				keys[e.getKeyCode()] = true;
			}
		}

		/**
		 * Notification of a key release.
		 * 
		 * @param e
		 *            The event details
		 */
		@Override
		public void keyReleased(final KeyEvent e) {
			/*
			{
				String ts = Thread.currentThread().toString();
				if (!ts.contains("[AWT")) {
					Exception ex = new IllegalArgumentException();
					System.out.println(ex.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
				}
			}
			*/
			if (e.isConsumed()) {
				return;
			}

			KeyEvent nextPress = (KeyEvent) Toolkit.getDefaultToolkit()
					.getSystemEventQueue().peekEvent(KeyEvent.KEY_PRESSED);

			if ((nextPress == null) || (nextPress.getWhen() != e.getWhen())) {
				keys[e.getKeyCode()] = false;
			}

		}

		/**
		 * Notification that an event has occurred in the AWT event system.
		 * 
		 * @param e
		 *            The event details
		 */
		public void eventDispatched(final AWTEvent e) {
			/*
			{
				String ts = Thread.currentThread().toString();
				if (!ts.contains("[AWT")) {
					Exception ex = new IllegalArgumentException();
					System.out.println(ex.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
				}
			}
			*/
			if (consumeForConsole) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					char c = ((KeyEvent) e).getKeyChar();
					if (c == '`') {
						consumeForConsole = gameframe.getConsole().toggleDisplay();
					} else {
						consoleBuffer.add(c);
					}
				}
			} else {

				if (e.getID() == KeyEvent.KEY_PRESSED) {
					keyPressed((KeyEvent) e);
					// :TODO remove if add put back in below
					q.add((KeyEvent) e);

				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					keyReleased((KeyEvent) e);
					// :TODO remove if add put back in below
					q.add((KeyEvent) e);
				}
				// :TODO why do we care about non pressed/released events?
				// (e.g. KeyEvent.KEY_TYPED... others?)
				// if (e.getClass() == KeyEvent.class ) {
				// q.add( (KeyEvent)e );
				// }
			}
		}
	}
	public KeyInfo get() {
    	KeyEvent event = q.poll();
    	if (event == null) return null;
    	
    	KeyInfo.State state;
    	int keyID = event.getID();

    	if (keyID == KeyEvent.KEY_PRESSED)
    		state = KeyInfo.State.PRESSED;
    	else if (keyID == KeyEvent.KEY_RELEASED)
    		state = KeyInfo.State.RELEASED;
    	else
    		return null;
    	
    	int keyCode = event.getKeyCode(); // :TODO can be VK_UNDEFINED
    	char character = event.getKeyChar(); // :TODO can be CHAR_UNDEFINED
    	
    	return new KeyInfo(character, keyCode, state);
	}

	//This method has to be called by the game thread so that certain
	//console commands will work such as 'fs'
	public void poll() {
		
		for(Character c : consoleBuffer)
		{
			gameframe.getConsole().appendToPrompt(c);
		}
		consoleBuffer.clear();
	}
}
