package jig.engine.hli.physics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;



/**
 * A simple collision handler that models elastic collisions between pairs of
 * spheres. Rotation and friction are not dealt with by this model.
 * 
 * @param <A>
 *            the class type of objects in the 'first' layer. This should be the
 *            most specific type that covers all objects in the layer.
 * 
 * @param <B>
 *            the class type of objects in the 'second' layer This should be the
 *            most specific type that covers all objects in the layer.
 * 
 * @author Scott Wallace
 * 
 */
public abstract class RectangleCollisionHandler
       <A extends VanillaAARectangle, B extends VanillaAARectangle>
		implements CollisionHandler {

	// Don't want the user to mess with these, they may try to change
	// the layer during the collision detection process.
	private BodyLayer<A> layer1;

	private BodyLayer<B> layer2;

	protected List<A> layer1Additions;
	protected List<B> layer2Additions;

	/**
	 * Creates a collison handler to deal with interactions between objects in
	 * two layers of spheres.
	 * 
	 * @param layer1
	 *            the 'first' layer of objects
	 * @param layer2
	 *            the 'second' layer of objects
	 */
	public RectangleCollisionHandler(final BodyLayer<A> layer1,
			final BodyLayer<B> layer2) {
		this.layer1 = layer1;
		this.layer2 = layer2;
		layer1Additions = new LinkedList<A>();
		layer2Additions = new LinkedList<B>();
	}

	/**
	 * Finds and reconciles collisions between spheres in each of the two
	 * registered layers.
	 * 
	 * Calls the user-defined method <code>collide</code>
	 * 
	 * @see #collide(VanillaSphere, VanillaSphere)
	 */
	public void findAndReconcileCollisions() {
		int l1s = layer1.size();
		int l2s = layer2.size();
		int i, j;


		A sphereLayer1;
		B sphereLayer2;


		for (i = 0; i < l1s; i++) {
			sphereLayer1 = layer1.get(i);
			if (!sphereLayer1.isActive()) {
				continue;
			}
			

			for (j = 0; j < l2s; j++) {
				sphereLayer2 = layer2.get(j);
				if (!sphereLayer2.isActive()) {
					continue;
				}
				

				if (sphereLayer1.getBoundingBox().intersects(sphereLayer2.getBoundingBox())) {
					collide(sphereLayer1, sphereLayer2);

				}
			}

		}
		for (Iterator<A> iv = layer1Additions.iterator(); iv.hasNext();) {
			layer1.add(iv.next());
			iv.remove();
		}
		for (Iterator<B> iv = layer2Additions.iterator(); iv.hasNext();) {
			layer2.add(iv.next());
			iv.remove();
		}

	}

	/**
	 * NOTE: This method is called from within
	 * <code>findAndReconcileCollisions</code> it is critical that the
	 * <code>ViewableLayers</code> referenced by this handler are not modified
	 * by this method. Otherwise, <code>findAndReconcileCollisions</code> may
	 * throw a Runtime exception.
	 * 
	 * 
	 * @param sphereLayer1
	 *            the sphere in Layer 1 involved in the collision
	 * @param sphereLayer2
	 *            the sphere in Layer 2 involved in the collision
	 */
	public abstract void collide(A sphereLayer1, B sphereLayer2);

}
