package jig.engine.physics.vpe;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
/**
 * An object modeled as a Sphere (or circle, really). Not to be confused
 * with an elliptical model, this class, and its subclasses should respect
 * the assumption that collisions can be detected simply with the center
 * position and the radius of the object.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public abstract class VanillaSphere extends Body {

	protected double radius;

	protected double rotation;

	protected List<ImageResource> imgBoundingSphere;

	protected static final int ORIGIN_IMG_RADIUS = 3;

	protected List<ImageResource> imgOrigin;

	protected boolean renderMarkup = false;
		
	protected Vector2D offset;

	/**
	 * Creates a new spherical body with the specified image
	 * resource.
	 * 
	 * Note that the representative image need not be circular or even
	 * square. This class will create the circle inscribed by the object's
	 * bounding square (note this is not necessarily 
	 * the circumscribing circle).
	 * 
	 * @param frameset
	 *            a list of ImageResource objects which together make
	 *            up all the frames this Box is capable of displaying.
	 * 
	 */
	public VanillaSphere(final List<ImageResource> frameset) {
		super(frameset);
	

		double imgHalfHeight = height / 2.0;
		double imgHalfWidth = width / 2.0;

		radius = Math.max(imgHalfHeight, imgHalfWidth);
		offset = new Vector2D(radius - imgHalfWidth, 
				radius - imgHalfHeight);

		// from here on out, this is treated like a circle...
		height = Math.max(height, width);
		width = height;

		rotation = 0.0;

	}
	
	
	/**
	 * Creates a new spherical body with the specified image
	 * resource.
	 * 
	 * Note that the representative image need not be circular or even
	 * square. This class will create the circle inscribed by the object's
	 * bounding square (note this is not necessarily 
	 * the circumscribing circle).
	 * 
	 * @param rsc
	 *            the name of the image resource to load.
	 */
	public VanillaSphere(final String rsc) {
		this(ResourceFactory.getFactory().getFrames(rsc));
	}

	/**
	 * 
	 * @return the angle of rotation for this object.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Turns additional rending information about this object on or off.
	 * 
	 * @param yes <code>true</code> iff the bounding surface and location
	 * information should be rendered for this object.
	 */
	public void renderMarkup(final boolean yes) {
		if (yes) {
			if (imgBoundingSphere == null) {
				int d = (int) (2 * radius);
				
				String rscName = PaintableCanvas
						.loadStandardFrameResource(d, d, JIGSHAPE.CIRCLE, Color.red);
				imgBoundingSphere = ResourceFactory.getFactory().getFrames(rscName);

				rscName = PaintableCanvas
						.loadStandardFrameResource(ORIGIN_IMG_RADIUS,
								ORIGIN_IMG_RADIUS, JIGSHAPE.CIRCLE, Color.red);
	
				imgOrigin = ResourceFactory.getFactory().getFrames(rscName);
			}
			renderMarkup = true;
		} else {
			renderMarkup = false;
		}

	}

	/**
	 * Sets the rotational angle of this object.
	 * 
	 * @param r the angle in radians.
	 */
	public void setRotation(final double r) {
		rotation = r;
	}

	/**
	 * 
	 * @return the radius of this object.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Performs a simple distance check to see if two VanillaSpheres
	 * are in contact with one another.
	 * 
	 * @param s
	 *            the object to test against for intersection
	 *
	 * @return <code>true</code> iff objects intersect.
	 */
	public boolean intersects(final VanillaSphere s) {
		Vector2D myCenter = getCenterPosition();
		Vector2D theirCenter = s.getCenterPosition();

		return (myCenter.distance2(theirCenter) 
				<= ((radius + s.radius) * (radius + s.radius)));
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}

		Vector2D p = position;
		AffineTransform at = AffineTransform.getTranslateInstance(p.getX()
				+ radius, p.getY() + radius);

		at.rotate(rotation);
		at.translate(-radius + offset.getX(), -radius + offset.getY());
		
		render(rc, at);
		if (renderMarkup) {
			at = AffineTransform.getTranslateInstance(p.getX(), p.getY());
			imgBoundingSphere.get(0).render(rc, at);
			at = AffineTransform.getTranslateInstance(p.getX()
					- (ORIGIN_IMG_RADIUS - 1) / 2.0, p.getY()
					- (ORIGIN_IMG_RADIUS - 1) / 2.0);
			imgOrigin.get(0).render(rc, at);
			Vector2D cp = getCenterPosition();
			at = AffineTransform.getTranslateInstance(cp.getX()
					- (ORIGIN_IMG_RADIUS - 1) / 2.0, cp.getY()
					- (ORIGIN_IMG_RADIUS - 1) / 2.0);
			imgOrigin.get(0).render(rc, at);
			
		}
	}
}
