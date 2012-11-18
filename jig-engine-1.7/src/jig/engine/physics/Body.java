package jig.engine.physics;

import java.util.List;

import jig.engine.ImageResource;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;

/**
 * The base class for all Viewable objects managed by a physics engine.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public abstract class Body extends Sprite {

	protected Vector2D velocity;

	
	/**
	 * Creates a new generic body with the specified image resources.
	 * 
	 * 
	 * @param frameset
	 *            a list of ImageResource objects which together make
	 *            up all the frames this body is capable of displaying.
	 * 
	 */
	public Body(final List<ImageResource> frameset) {
		super(frameset);
		velocity = new Vector2D(0, 0);		
	}
	
	/**
	 * Creates a new generic body with the specified image resource.
	 * 
	 * @param imgrsc
	 *            the name of the image resource to load.
	 */
	public Body(final String imgrsc) {
		super(imgrsc);
		velocity = new Vector2D(0, 0);
	}

	/**
	 * Sets the body's velocity.
	 * 
	 * @param v
	 *            a vector representation of the velocity.
	 */
	public void setVelocity(final Vector2D v) {
		velocity = v;
	}

	/**
	 * 
	 * @return the body's velocity
	 */
	public Vector2D getVelocity() {
		return velocity;
	}

	/**
	 * This method is used to perform some kind of update on a Body, typically
	 * the Body's position or state.
	 * 
	 * @param deltaMs
	 *            the time since the update method was last called
	 */
	public abstract void update(long deltaMs);
}
