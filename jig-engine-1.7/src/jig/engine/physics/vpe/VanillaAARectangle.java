package jig.engine.physics.vpe;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.physics.Body;

/**
 * An object modeled as an Axis-Aligned Rectangular Body. It is assumed that
 * subclasses of this object will respect the axis aligned property, otherwise
 * all bets are off.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public abstract class VanillaAARectangle extends Body {
	public int type;
	private double offset=10;
	private double safenum=0.00001;
	private double sideCollisionBounceOff=0.2;
	protected List<ImageResource> imgBoundingRectangle;

	private boolean renderMarkup = false;

	/**
	 * Creates a new AA-Rectangle with the specified image resources.
	 * 
	 * 
	 * @param frameset
	 *            a list of ImageResource objects which together make
	 *            up all the frames this Box is capable of displaying.
	 * 
	 */
	public VanillaAARectangle(final List<ImageResource> frameset) {
		super(frameset);
	}
	
	/**
	 * Creates a new axis-aligned rectangular body with the specified image
	 * resource.
	 * 
	 * @param rsc
	 *            the name of the image resource to load.
	 */
	public VanillaAARectangle(final String rsc, int type) {
		super(rsc);
		this.type=type;
	}

	/**
	 * Turns additional rending information about this object on or off.
	 * 
	 * @param yes <code>true</code> iff the bounding surface and location
	 * information should be rendered for this object.
	 */
	public void renderMarkup(final boolean yes) {
		if (yes) {
			if (imgBoundingRectangle == null) {
				String rscName = PaintableCanvas
				.loadStandardFrameResource(getWidth(), getHeight(), JIGSHAPE.RECTANGLE,
						Color.blue);
				imgBoundingRectangle = ResourceFactory.getFactory().getFrames(rscName);				
			}
			renderMarkup = true;
		} else {
			renderMarkup = false;
		}

	}

	/**
	 * Currently, intersection tests are best done using this method
	 * to return a bounding box, and then the <code>AABB</code> utility
	 * class to check for intersection details.  Simple intersection
	 * tests can also be done directly with the <code>Rectangle2D</code>
	 * class methods.
	 * 
	 * @return the object's bounding box.
	 * 
	 * @see jig.util.AABB
	 * @see jig.util.AABB#minSeparation(Rectangle2D,Rectangle2D)
	 * @see java.awt.geom.Rectangle2D
	 * @see java.awt.geom.Rectangle2D#intersects(Rectangle2D)
	 */
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D.Double(position.getX(), position.getY(), width,
				height);

	}


	/**
	 * Renders the VanillaRectangle.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 * 
	 */
	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}

		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX(), position.getY());
		super.render(rc, at);

		if (renderMarkup) {
			imgBoundingRectangle.get(0).render(rc, at);
		}
	}
	
	public boolean isOnLeftSide(VanillaAARectangle b){
		if(position.getX()+getWidth() > b.getPosition().getX() && position.getX() < b.getPosition().getX()){
			return true;
		}
		return false;
	}
	
public boolean isOnRightSide(VanillaAARectangle b){
		if(position.getX() < b.getPosition().getX() + b.getWidth() && position.getX() + getWidth() > b.getPosition().getX() + b.getWidth()){
			return true;
		}
		return false;
	}

public boolean isOnTopSide(VanillaAARectangle b){
	if(position.getY()+getHeight() > b.getPosition().getY() && position.getY() < b.getPosition().getY() && position.getY()+getHeight() - b.getPosition().getY() < offset){
		return true;
	}
	return false;
}

public boolean isOnBottomSide(VanillaAARectangle b){
	if(position.getY() < b.getPosition().getY() + b.getHeight() && position.getY() + getHeight() > b.getPosition().getY() + b.getHeight() && b.getPosition().getY() + b.getHeight()-position.getY()<offset){
		return true;
	}
	return false;
}

public double leftCollidingDistance(VanillaAARectangle b){
	return (position.getX()+getWidth()) - b.getPosition().getX() + sideCollisionBounceOff;
}

public double rightCollidingDistance(VanillaAARectangle b){
	return (b.getPosition().getX() + b.getWidth()) - position.getX() + sideCollisionBounceOff;
}

public double topCollidingDistance(VanillaAARectangle b){
	return (position.getY()+getHeight()) - b.getPosition().getY() + safenum;
}

public double bottomCollidingDistance(VanillaAARectangle b){
	return (b.getPosition().getY() + b.getHeight()) - position.getY() + safenum;
}

}
