package jig.engine.physics;

import java.util.Iterator;

import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;

/**
 * A BodyLayer contains <code>Body</code> objects. The BodyLayer is
 * a minor extension to ViewableLayer which provides some methods 
 * similar to those found in java.util.Collection.  The key difference
 * between ViewableLayer and BodyLayer, is that BodyLayer is intended 
 * to provide a container for Bodies that are related in the following
 * ways: 
 * 
 * <ol>
 * <li> They are all rendered in the same plane (z-depth).
 * <li> From the perspective of the physics engine, they are all treated
 * according to the same general rules...typically, the physics engine will be
 * asked to update some (but not necessarily all) ViewableLayers
 * </ol>
 * 
 * ViewableLayers, in contrast, need not encapsulate any Body elements,
 * and may specify their own interface to constituent elements if desired.
 * 
 * Layers attempt to simplify the rendering and updating process
 * by grouping elements together in a visually meaningful way. Thus
 * entire layers can easily be rendered and updated; because Layers
 * are also Viewable, they can quickly be made inactive.
 *
 * 
 * @param <V>
 *            the type of <code>Body</code> objects contained in this
 *            Layer
 * 
 * @author Scott Wallace
 * 
 * @see jig.engine.ViewableLayer
 *  
 */
public interface BodyLayer<V extends Body> extends
		ViewableLayer, Iterable<V> {

	/**
	 * Renders all of the Viewable objects in the layer. It is generally
	 * preferred to call render and update on the Layers, as opposed
	 * to on all objects individually.
	 * 
	 * @param rc
	 *            the rendering context of Game Frame
	 */
	void render(RenderingContext rc);

	/**
	 * Updates objects in the layer.
	 * 
	 * If the layer is meant to be managed and updated by the physics engine,
	 * this method should be empty. Otherwise it should iterate through all
	 * <code>Viewable</code> elements in the layer updating each one
	 * appropriately.
	 * 
	 * @param deltaMs
	 *            the time since the last update
	 */
	void update(long deltaMs);

	/**
	 * Creates an iterator over all the elements in the layer.
	 * 
	 * @return a new iterator
	 */
	Iterator<V> iterator();

	/**
	 * Gets a member from the layer.
	 * 
	 * @param i
	 *            the member's index (in the range [0,size()])
	 * @return a reference to the requested member
	 */
	V get(int i);

	/**
	 * Adds a new element to the layer.
	 * 
	 * @param e
	 *            the new element to add
	 */
	void add(V e);

	/**
	 * Clears all bodies from this layer. Calling render and update
	 * on a cleared layer should <b>not</b> result in an error and should
	 * not rely on activation state to prevent such errors (i.e., update
	 * and render should not result in errors even if the cleared layer is in
	 * the active state).
	 * 
	 */
	void clear();

	/**
	 * Gets the number of elements currently in this layer.
	 * 
	 * @return the number of elements in the layer
	 */
	int size();

}
