package jig.engine.none;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.none.NoneGameFrame.NoneRenderingContext;

/**
 * An immutable ImageResource for the NoneResourceFactory backend.
 * 
 * This class should not be visible to the end user, it should be accessed only
 * via the <code>NoneResourceFactory</code>. Users should interact with
 * instances of this type of object through the <code>ImageResource</code>
 * interface.
 * 
 * @author Scott Wallace
 * 
 * @see jig.engine.ImageResource
 */
final class NoneImage implements ImageResource {

	/* not accessible to the outside world, and created during initialization */
	BufferedImage image;

	/**
	 * Create an 'accelerated' and immutable image from an image sheet that may
	 * comprise multiple frames.
	 * 
	 * @param i
	 *            the image sheet
	 * @param transparency
	 *            one of Transparency.BITMASK, OPAQUE, TRANSPARENT
	 * @param w
	 *            the width of the resulting image
	 * @param h
	 *            the height of the resulting image
	 * @param xoffset
	 *            the x-offset of this frame on the image sheet
	 * @param yoffset
	 *            the y-offset of this frame on the image sheet
	 * @param frame
	 *            a GameFrame (should not be null)
	 */
	NoneImage(final Image i, final int transparency, final int w, final int h,
			final int xoffset, final int yoffset, final NoneGameFrame frame) {

		if (frame == null) {
			throw new NullPointerException(
					"Tried to load ImageResource before creating GameFrame.");
		}

		image = new BufferedImage(w,h,transparency);
		image.getGraphics().drawImage(i, xoffset, yoffset, null);

		Logger l = ResourceFactory.getJIGLogger();
		if (l.isLoggable(Level.FINE)) {
			ResourceFactory.getJIGLogger().fine(
					"Loaded image [" + w + "x" + h + "] NoneImage " );
		}

	}

	/**
	 * @return the height of the image encapsulated by this resource
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * @return the width of the image encapsulated by this resource
	 */
	public int getWidth() {
		return image.getWidth(null);
	}


	/**
	 * None are only rendered when the rendering context is in a special state...
	 * 
	 * @param rc
	 *            The context upon which to render this image
	 * @param at
	 *            The transform which should be applied to the image before
	 *            rendering
	 */
	public void render(final RenderingContext rc, final AffineTransform at) {
		NoneRenderingContext nrc = (NoneRenderingContext) rc;
		
		if (!nrc.rendering) return;

		nrc.theG.drawImage(image, at, null);

		
	}

	/**
	 * TODO: Incomplete!
	 */
	public void draw(Graphics2D g, AffineTransform at) {
		g.drawImage(image, at, null);
	}

}
