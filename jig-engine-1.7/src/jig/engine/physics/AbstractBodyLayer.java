package jig.engine.physics;

import java.util.ArrayList;
import java.util.Iterator;

import jig.engine.RenderingContext;

/**
 * An abstract, but mostly complete, implementation of the
 * <code>ViewableLayer</code> interface with layer membership backed by an
 * <code>ArrayList</code>.
 * 
 * 
 * @param <V>
 *            the type of <code>Viewable</code> objects contained in this
 *            Layer
 * @see jig.engine.physics.BodyLayer
 * 
 * @author Scott Wallace
 * 
 */
public abstract class AbstractBodyLayer<V extends Body> implements
		BodyLayer<V> {

	/** A list of the Viewable objects in this layer. */
	protected ArrayList<V> members;
	
	protected boolean active;

	/** Creates a new ViewableLayer. */
	public AbstractBodyLayer() {
		members = new ArrayList<V>();
		active = true;
	}

	/**
	 * Creates a new ViewableLayer with specified initial size.
	 * 
	 * @param n
	 *            the intitial size
	 */
	public AbstractBodyLayer(final int n) {
		members = new ArrayList<V>(n);
	}

	/**
	 * Renders all of the Viewable objects in the layer.
	 * 
	 * @param rc
	 *            the rendering context of Game Frame
	 *            
	 * BUG: This probably should check active before rendering
	 */
	public void render(final RenderingContext rc) {
		for (Iterator<V> i = members.iterator(); i.hasNext();) {
			i.next().render(rc);
		}
	}

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
	public abstract void update(long deltaMs);

	/**
	 * Creates an iterator over all the elements in the layer.
	 * 
	 * @return a new iterator
	 */
	public Iterator<V> iterator() {
		return members.iterator();
	}

	/**
	 * Gets a member from the layer.
	 * 
	 * @param i
	 *            the member's index (in the range [0,size()])
	 * @return a reference to the requested member
	 */
	public V get(final int i) {
		return members.get(i);
	}

	/**
	 * Adds a new element to the layer.
	 * 
	 * @param e
	 *            the new element to add
	 */
	public void add(final V e) {
		members.add(e);
	}

	/**
	 * Gets the number of elements currently in this layer.
	 * 
	 * @return the number of elements in the layer
	 */
	public int size() {
		return members.size();
	}

	/**
	 * Clears all bodies from this layer.
	 */
	public void clear() {
		members.clear();
	}

	/**
	 * Sets the activation of the layer to on or off.
	 * 
	 * @param yes <code>true</code> iff the layer should be active
	 */
	public void setActivation(final boolean yes) {
		active = yes;
	}
	
	/**
	 * @return <code>true</code> iff the layer is active.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * An AbstractViewableLayer with an empty update method. Suitable for use
	 * when the objects in this layer will be updated by a physics engine.
	 * 
	 * @author Scott Wallace
	 * 
	 */
	public static class NoUpdate<V extends Body> extends
			AbstractBodyLayer<V> {

		/**
		 * This method is empty and does nothing.
		 * 
		 * @param deltaMs
		 *            ignored
		 */
		public void update(final long deltaMs) {
		}
	}
	
	/**
	 * An AbstractViewableLayer with an update method that simply
	 * iterates through the members of this layer and calls
	 * their update() methods. 
	 * 
	 * @author Scott Wallace
	 * 
	 */
	public static class IterativeUpdate<V extends Body> extends
			AbstractBodyLayer<V> {

		/**
		 * This method is empty and does nothing.
		 * 
		 * @param deltaMs
		 *            ignored
		 */
		public void update(final long deltaMs) {
			for (Body b : members) {
				b.update(deltaMs);
			}
		}
	}
}
