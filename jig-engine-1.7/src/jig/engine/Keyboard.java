package jig.engine;


/**
 * An interface for a generic, pollable, keyboard. Concrete instances are
 * provided for each of the JIG graphics backends.
 * 
 * Concrete Keyboard classes should perform two functions in addition to 
 * implementing the methods below.  Namely, they should:
 * <ol>
 * <li> Intercept the backquote (`) key press, and toggle the display of
 * the game frame's console.</li>
 * <li> Track whether or not the console is being displayed.</li>
 * <li> Intercept all keystrokes when the console is displayed and forward
 * them to the game frame's console instance.</li>
 * </ol>
 
 * @author Scott Wallace
 *
 */
public interface Keyboard {
	/**
	 * 
	 * @param key a key code as provided by java.awt.event.KeyEvent
	 * @return <code>true</code> iff the key is currently pressed
	 * 
	 * @see java.awt.event.KeyEvent
	 */
	boolean isPressed(int key);
	
	
	/**
	 * Polls the keyboard for its current state. Ensures that the
	 * isPressed method is using the most current state of the keyboard,
	 * and that the get method event queue is updated.
	 * 
	 */
	void poll();
	
	/**
	 * Gets an event off the event queue so it can be processed during 
	 * game play. All events returned are instances of 
	 * <code>java.awt.event.KeyEvent</code>.
	 * 
	 * @return the next event or <code>null</code> if the queue is
	 * empty.
	 *
	 */
	KeyInfo get();

}
