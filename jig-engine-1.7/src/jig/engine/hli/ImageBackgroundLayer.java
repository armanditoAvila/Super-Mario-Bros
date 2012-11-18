package jig.engine.hli;
import java.awt.geom.AffineTransform;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;

/**
 * The <code>ImageBackgroundLayer</code> takes an 
 * <code>ImageResource</code> and centers or 
 * tiles it within a rectangular region (the layer) of a specified
 * size.
 * 
 * @author Scott Wallace
 *
 */
public class ImageBackgroundLayer implements ViewableLayer {
	
	public static final int SCALE_IMAGE = 1;
	public static final int CENTER_IMAGE = 2;
	public static final int TILE_IMAGE = 3;
	
	private int layerWidth;
	private int layerHeight;
	
	ImageResource image;
	
	int tileAction;
	
	private int imgHeight;
	
	private int imgWidth;
	boolean active;
	
	/**
	 * Creates an <code>ImageBackgroundLayer</code> of a specified size, but
	 * without an actual image (the image must be specified at a later point in
	 * time). The layer is initially in an <code>inactive</code> state.
	 * 
	 * @param layerWidth
	 *            the width of the layer
	 * @param layerHeight
	 *            the height of the layer
	 * 
	 * @see #setBackground(ImageResource, boolean)
	 */
	public ImageBackgroundLayer(final int layerWidth, final int layerHeight) {
		active = false;
		this.layerHeight = layerHeight;
		this.layerWidth = layerWidth;
	}

	/**
	 * Creates an <code>ImageBackgroundLayer</code> which is immediately
	 * ready to be viewed. The layer is initially in an <code>active</code> 
	 * state.
	 * 
	 * @param img
	 *            the image resource to display on this layer
	 * @param layerWidth
	 *            the width of the layer
	 * @param layerHeight
	 *            the height of the layer
	 * @param mode
	 *            one of <code>SCALE_IMAGE</code>, 
	 *            <code>CENTER_IMAGE</code>, or <code>TILE_IMAGE</code>
	 *            describing what to do with images that are smaller
	 *            than the layer itself. 
	 * 
	 * @see #setBackground(ImageResource, boolean)
	 */
	public ImageBackgroundLayer(final ImageResource img,
			final int layerWidth, final int layerHeight,
			final int mode) {
		
		image = img;
		tileAction = mode;
		imgHeight = img.getHeight();
		imgWidth = img.getWidth();
		this.layerHeight = layerHeight;
		this.layerWidth = layerWidth;
		active = true;
	}

	/**
	 * Sets the background image and/or mode.
	 * 
	 * @param img
	 *            the image resource to display on this layer
	 * @param mode
	 *            one of <code>SCALE_IMAGE</code>, 
	 *            <code>CENTER_IMAGE</code>, or <code>TILE_IMAGE</code>
	 *            describing what to do with images that are smaller
	 *            than the layer itself. 
	 */
	public void setBackground(final ImageResource img, final int mode) {
		image = img;
		imgHeight = img.getHeight();
		imgWidth = img.getWidth();
		tileAction = mode;	
	}

	/**
	 * Renders the layer on to the game frame.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 */
	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}
		
		int x, y;
		AffineTransform at;
		switch (tileAction) {
		
		case TILE_IMAGE:
			at = AffineTransform.getTranslateInstance(0, 0);
			
			for (x = 0; x < layerWidth; x += imgWidth) {
				for (y = 0; y < layerHeight; y += imgHeight) {
					at.setToTranslation(x, y);
					image.render(rc, at);
				}
			}
			break;
		
		case SCALE_IMAGE:
			at = AffineTransform.getScaleInstance(
					((double) layerWidth) / imgWidth,
					((double) layerHeight) / imgHeight);
			image.render(rc, at);
			break;
			
		case CENTER_IMAGE:
		default:
			
			at = AffineTransform.getTranslateInstance(
					(layerWidth - imgWidth) / 2,
					(layerHeight - imgHeight) / 2);
			image.render(rc, at);
			break;
		}
	}

	/**
	 * Updates the layer on each iteration of the game loop.
	 * This method is empty, as this image background layer is static.
	 *
	 * @param deltaMs
	 *               ignored
	 */
	public void update(final long deltaMs) {
		// do nothing, it's just a static image.
	}

	/**
	 * @return <code>true</code> iff the layer is in the active (visible)
	 * state.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the activation state of this layer.
	 * 
	 * @param a <code>true</code> iff the layer should be made active
	 */
	public void setActivation(final boolean a) {
		active = a;
	}
}
