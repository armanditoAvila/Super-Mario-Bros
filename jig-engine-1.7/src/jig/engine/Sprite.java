package jig.engine;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.util.Vector2D;

/**
 * In the JIG Engine, a Sprite is an image and a very minimal amount of state
 * information. Specifically, it has state to indicate whether it is 'active'
 * and which frame it is displaying.
 * 
 * 
 * 
 * @author Scott Wallace
 * 
 */
public class Sprite implements Viewable {

	protected List<ImageResource> frames;

	/** Position in world coordinate system. */
	protected Vector2D position;

	protected boolean active;

	protected int width, height;
	protected int visibleFrame;
	


	/**
	 * Create a new, initially active, Sprite using the specified 
	 * list of ImageResource objects.
	 * 
	 * @param frameset
	 *            a list of ImageResource objects which together make
	 *            up all the frames this sprite is capable of displaying.
	 * 
	 */
	public Sprite(final List<ImageResource> frameset) {
		frames = frameset;
		width = frames.get(0).getWidth();
		height = frames.get(0).getHeight();
		active = true;
		
	}
	
	/**
	 * Create a new, initially active, Sprite using the specified resource name
	 * to load an ImageResource from the current factory.
	 * 
	 * @param rscName
	 *            the name of the ImageResource which will visually represent
	 *            this entity.
	 * 
	 */
	public Sprite(final String rscName) {
		this(ResourceFactory.getFactory().getFrames(rscName));
	}

	/**
	 * @return the number of frames
	 */
	public int getFrameCount() {
		return frames.size();
	}

	/**
	 * 
	 * @return the index of the visible frame.
	 */
	public int getFrame() {
		return visibleFrame;
	}

	/**
	 * Sets the visible frame.
	 * 
	 * @param n
	 *            the index of the desired frame
	 */
	public void setFrame(final int n) {
		visibleFrame = n;
	}

	/**
	 * Increments the currently visible frame, wrapping back to the first frame
	 * as appropriate.
	 * 
	 * @param stepSize the size of step
	 */
	public void incrementFrame(final int stepSize) {
		visibleFrame += stepSize;
		visibleFrame %= frames.size();
	}

	/**
	 * Renders the sprite using the specified affine transform.
	 * 
	 * Unlike {@link #render(RenderingContext)}, this method will not check if
	 * the sprite's state is active before rendering. It is expected that
	 * subclasses invoking this method will perform that check and exit
	 * immediately if the Viewable is not active.
	 * 
	 * 
	 * @param rc
	 *            the rendering context upon which to draw
	 * @param at
	 *            an affine transform to apply to the sprite prior to drawing
	 * 
	 */
	protected void render(final RenderingContext rc, final AffineTransform at) {

		frames.get(visibleFrame).render(rc, at);
	}

	/**
	 * Translate the sprite to its position in the world coordinate system then
	 * render it on the game frame's rendering context.
	 * 
	 * Rendering occurs only if the Sprite is in its active state.
	 * 
	 * @param rc
	 *            the rendering context upon which to draw
	 */
	public void render(final RenderingContext rc,double x, double y) {
		if (active) {
			
			if (rc == null) {
				System.out.println("hjfdsahj");
			}
			
			
			frames.get(visibleFrame).render(
					rc,
					AffineTransform.getTranslateInstance(position.getX()+x,
							position.getY()+y));
		}
	}
	
	public void render(){
		
	}

	/**
	 * 
	 * @return the sprite's width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the sprite's height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the position of the body's upper left corner in the absence of
	 *         any rotation.
	 */
	// @Override
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Sets the sprite's position. i.e., the location of the upper left corner
	 * prior to rotation.
	 * 
	 * @param p
	 *            the position in world coordinates.
	 * 
	 */
	public void setPosition(final Vector2D p) {
		position = p;
	}

	/**
	 * Sets the position of the Sprite's center to the desired coordinate.
	 * Note that this method currently creates a new vector, and so 
	 * is not idea for use within a loop as it may lead to a substantial
	 * increase in the number of small objects created.
	 * 
	 * @param center the new center position
	 * 
	 * @see #getCenterPosition()
	 */
	public void setCenterPosition(final Vector2D center) {
		position = new Vector2D(center.getX() - getWidth() / 2.0,
				center.getY() - getHeight() / 2.0);
	}
	/**
	 * Gets the position of the Sprite's center.
	 * Note that this method currently creates a new vector, and so 
	 * is not idea for use within a loop as it may lead to a substantial
	 * increase in the number of small objects created.
	 * 
	 * @return the Sprite's center position
	 * 
	 * @see #setCenterPosition()
	 * @see #getPosition()
	 */
	public Vector2D getCenterPosition() {
		return new Vector2D(position.getX() + getWidth() / 2.0, position.getY()
				+ getHeight() / 2.0);
	}

	/**
	 * Sets the sprite's state to active, an active Sprite is one that will be
	 * drawn when the {@link #render(RenderingContext)} method is called.
	 * 
	 * @param a
	 *            the desired activation
	 */
	public void setActivation(final boolean a) {
		active = a;
	}

	/**
	 * @return <code>true</code> if the sprite is active.
	 */
	public boolean isActive() {
		return active;
	}

	@Override
	public void render(RenderingContext rc) {
		// TODO Auto-generated method stub
		
	}

}
