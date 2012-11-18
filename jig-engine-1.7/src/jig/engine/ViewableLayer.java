package jig.engine;


/**
 * A <code>ViewableLayer</code> encapsulates information or objects
 * presented to the user during the game. The idea is similar to layers
 * in Adobe Photoshop, the GIMP, or a set of transparencies:  Layers can
 * have a position along the Z-axis, and can be stacked on top of one another
 * to achieve foreground/background effects.
 * 
 * Generally, layers exist in two broad categories:
 * <ul>
 * <li> <code>ViewableLayer</code>s which need not provide a 
 * mechanism to access objects inside the layer.</li>
 * <li> <code>BodyLayer</code>s in which the layer is composed of
 * zero or more <code>Body</code> instances and the layer is used
 * to define groups of objects the interact with one another by way
 * of a physics engine. This type of layer must provide programmatic
 * access to the bodies which it contains.</li>
 * </ul>
 * 
 * Layers attempt to simplify the rendering and updating process
 * by grouping elements together in a visually meaningful way. Thus
 * entire layers can easily be rendered and updated; because Layers
 * are also Viewable, they can quickly be made inactive.
 * 
 * 
 * 
 * @author Scott Wallace
 * 
 * @see jig.engine.physics.BodyLayer
 *
 
 */
public interface ViewableLayer extends Viewable {

	/**
	 * Renders all of the Viewable objects in the layer.
	 * 
	 * @param rc
	 *            the rendering context of Game Frame
	 */
	void render(RenderingContext rc);

	/**
	 * Updates the layer.
	 * 
	 * If the layer is meant to be managed and updated by the physics engine,
	 * this method should be empty. Otherwise, the method should update the
	 * layer and whatever objects the layer contains in the appropriate
	 * manner.
	 * 
	 * @param deltaMs
	 *            the time since the last update
	 */
	 void update(long deltaMs);

}
