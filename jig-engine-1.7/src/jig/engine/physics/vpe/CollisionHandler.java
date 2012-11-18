package jig.engine.physics.vpe;




/**
 * An interface used to encapsulate the logic for identifying and
 * reconciling collisions between objects in the world.
 * 
 * As the <code>VanillaPhysicsEngine</code> manages objects stored
 * in <code>ViewableLayer</code> instances, it makes sense to create
 * <code>CollisionHandler</code> instances that deal explicitly with these
 * layers. Specifically, it makes sense (and will be computationally efficient
 * given the implementation of the <code>VanillaPhysicsEngine</code>) to
 * place objects of a specific type or function into each layer and
 * create handlers for pairs of layers in which the objects may interact.
 * 
 * 
 * @see VanillaPhysicsEngine#applyLawsOfPhysics(long)
 * @see jig.engine.hli.VanillaSphereCollisionHandler
 * 
 * @author Scott Wallace
 *
 */
public interface CollisionHandler {
	
	/**
	 * Finds and reconciles collisions between objects in the world.
	 * 
	 * @see VanillaPhysicsEngine#applyLawsOfPhysics(long)
	 */
	void findAndReconcileCollisions();
}
