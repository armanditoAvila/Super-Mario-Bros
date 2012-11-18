package jig.engine;

import java.awt.Point;

/**
 * An interface for a generic, pollable, mouse. Concrete instances are provided
 * for each of the JIG graphics backends.
 * 
 * @author Scott Wallace
 * 
 */
public interface Mouse {
	/**
	 * 
	 * @return <code>true</code> iff the left mouse button is pressed
	 */
	boolean isLeftButtonPressed();

	/**
	 * 
	 * @return <code>true</code> iff the right mouse button is pressed
	 */
	boolean isRightButtonPressed();

	/**
	 * 
	 * @return the (x,y) location of the mouse
	 */
	Point getLocation();

	/**
	 * Changes the mouse cursor to the specified {@link CursorResource}.
	 * 
	 * @param c
	 *            the {@link CursorResource} to set the cursor to
	 */
	void setCursor(CursorResource c);

	/**
	 * @return <code>true</code> if the cursor has been set.
	 */
	boolean isCursorSet();

	/**
	 * Resets the mouse cursor to its default state.
	 */
	void resetCursor();

	/**
	 * @return the currently set CursorResource or <code>null</code> if the
	 * cursor has not been set.
	 */
	CursorResource getCursor();

}
