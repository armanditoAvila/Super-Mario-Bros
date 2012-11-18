package jig.engine;

import jig.engine.util.Vector2D;

/**
 * This interface represent a cursor object. The mouse cursor can be changed
 * with {@link Mouse#setCursor(CursorResource))}.
 * 
 * @author Aaron Mills
 * @author Scott Wallace
 */
public interface CursorResource {
	
	/** 
	 * 
	 * @return the cursor's name.
	 */
	String getName();
	
	/**
	 * 
	 * @return the time delay between animation frames.
	 */
	long getAnimationDelay();
	
	/**
	 * 
	 * @return the location of the 'hot'/active spot on the cursor.
	 * 
	 */
	Vector2D getHotSpot();
	
}
