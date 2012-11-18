package jig.engine;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;

/**
 * The <code>PhysicsEngine</code> class describes a policy used to update 
 * registered physical bodies during the game. The policy typically 
 * involves moving bodies individually and then checking for and resolving
 * interactions between multiple bodies (e.g., collisions).
 * 
 * @param <B> the base <code>Body</code> type needed by this engine
 * 
 * @author wallaces
 *
 */
public interface PhysicsEngine<B extends Body> {

	
	/**
	 * Registers a <code>ViewableSet</code> so that the objects in the
	 * set will be managed according to the law embodied by this engine.
	 * 
	 * Multiple sets can be managed.
	 * 
	 * @param v the ViewableSet to manage
	 */
	void manageViewableSet(BodyLayer<? extends B> v);

	
	/**
	 * Updates all known (registered) bodies according to the laws of
	 * physics embodied by this model.
	 * 
	 * @param deltaMs 	the elapsed time (in milliseconds) since the last update
	 */
	void applyLawsOfPhysics(long deltaMs);
	

	/**
	 * Renders special markup or annotations for this physical model
	 * 
	 * Actual bodies should normally not be rendered by this method,
	 * but rather rendered by the <code>ViewableLayer</code> to allow 
	 * layering in the z-dimension.
	 * 
	 * In many cases, this method will do nothing, but it can be 
	 * very useful to reveal the underlying mechanics of the physics engine.
	 * This function, for example, is used by the 
	 * <code>CattoPhysicsEngine</code>
	 * to show <code>Arbiter</code> & <code>Joint</code> locations when desired.
	 * 
	 * @param rc the GameFrame's drawing context
	 * 
	 * @see jig.engine.physics.BodyLayer
	 */
	void renderPhysicsMarkup(RenderingContext rc);


	/** Clears the internal state of the physics model. */
	void clear();
}

