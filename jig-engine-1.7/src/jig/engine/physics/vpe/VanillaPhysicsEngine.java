package jig.engine.physics.vpe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jig.engine.PhysicsEngine;
import jig.engine.RenderingContext;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;

/**
 * This is engine provides a basic framework for implementing
 * game specific physics.
 * 
 * Physical objects in the world are stored in a list of 
 * <code>ViewableLayer</code> instances; rules for detecting
 * and resolving collisions are stored in a list of 
 * <code>CollisionHandler</code> instances.
 * 
 * 
 * @author Scott Wallace
 *
 */
public class VanillaPhysicsEngine implements PhysicsEngine<Body> {
	
	/**
	 *  A list of <code>ViewableLayer</code> instances
	 *  storing the bodies under management.
	 */
	protected List<BodyLayer<? extends Body>> bodies;

	/**
	 * A list of <code>CollisionHandler</code> instances
	 * storing the rules for managing collisions.
	 */
	protected ArrayList<CollisionHandler> handlers;

	/**
	 * Creates a new physics engine.
	 */
	public VanillaPhysicsEngine() {
		bodies = new LinkedList<BodyLayer<? extends Body>>();
		handlers = new ArrayList<CollisionHandler>(20);
	}

	/**
	 * Registers a <code>ViewableLayer</code> indicating that
	 * this physics engine will be responsible for updating the
	 * objects in this layer according to the rules implemented by
	 * this engine.
	 * 
	 * @see #registerCollisionHandler(CollisionHandler)
	 * @param v a layer of physical bodies
	 */
	public void manageViewableSet(
			final BodyLayer<? extends Body> v) {

		bodies.add(v);
	}

	/**
	 * Registers a <code>CollisionHandler</code> indicating that
	 * this handler can find and resolve collisions between objects
	 * in the registered <code>ViewableLayer</code>s.
	 * 
	 * @see #manageViewableSet(BodyLayer)
	 * @param h an object capable of handling collsions
	 */
	public void registerCollisionHandler(final CollisionHandler h) {
		handlers.add(h);
	}

	/**
	 * Clears the physics engine, effectively resetting it to its original
	 * state.
	 */
	public void clear() {
		bodies.clear();
		handlers.clear();
	}

	/**
	 * Applies the laws of physics to registered bodies by:
	 * <ol>
	 * <li> Iterating over all physical bodies in the registered layers 
	 * and updating each body.
	 * <li> Iterating over each collision handler to find and reconcile
	 * collisions.
	 * </ol>
	 * 
	 * This implies that the registered <code>ViewableLayer</code> instances
	 * should be disjoint sets (have no physical bodies in common). Typically,
	 * it should also be the case that the <code>CollisionHandler</code> 
	 * instances also deal with disjoint sets of physical bodies.  The easiest
	 * way to do this is create layers based on the type of body and define
	 * collision handlers to deal with interactions between pairs of body
	 * types (or equivilantly pairs of layers). 
	 * 
	 * @see BodyLayer
	 * @see CollisionHandler
	 * @param deltaMs the time since the last update
	 */
	public void applyLawsOfPhysics(final long deltaMs) {
		
		for (BodyLayer<? extends Body> layer : bodies) {
			for (Body b : layer) {
				b.update(deltaMs);				
			}
		}
		for (Iterator<CollisionHandler> i = handlers.iterator(); i.hasNext();) {
			i.next().findAndReconcileCollisions();
		}
	}

	/**
	 * Renders markup for debugging or visualization onto the drawing surface.
	 * 
	 * This default implementation does nothing.
	 * 
	 * @param gc the rendering context of the game
	 */
	public void renderPhysicsMarkup(final RenderingContext gc) {
		/* Empty.  The Vanilla Physics Engine has no markup or annotations
		 * to add to the objects themselves.
		 */
	}

}
