package jig.engine;


/**
 * An object that has a visual representation in the world.
 * <code>Viewable</code> objects, and classes that interact with
 * <code>Viewable</code> objects should obey the activation state contract:
 * when a <code>Viewable</code> is active, it should behave normally (e.g., be
 * rendered, updated, and participate in any physical simulation as
 * appropriate), when it is inactive it should not be rendered and typically not
 * participate in any unnecessary updating.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public interface Viewable {

	/**
	 * Gets the position of the viewable object.
	 * 
	
	 * 
	 * @return the x,y coordinates of the viewable
	 * 
	 */
	//Vector2D getPosition();

	/**
	 * Renders the viewable object using the specified rendering context.
	 * 
	 * @param rc
	 *            the rendering context of the game frame
	 */
	void render(RenderingContext rc);

	/**
	 * 
	 * @return <code>true</code> iff the Viewable is 'active' and
	 *         rendering/physics should be pursued. See the interface
	 *         level documentation for more details on the activation
	 *         state contract.
	 */
	boolean isActive();

	/**
	 * 
	 * @param a
	 *            <code>true</code> iff this Viewable should be made active.
	 *            See the interface level documentation for more details on the
	 *            activation state contract.
	 * 
	 */
	void setActivation(boolean a);

}
