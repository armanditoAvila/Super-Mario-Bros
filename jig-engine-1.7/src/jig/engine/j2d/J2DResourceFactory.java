package jig.engine.j2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import jig.engine.CursorResource;
import jig.engine.FontResource;
import jig.engine.GameFrame;
import jig.engine.ImageResource;
import jig.engine.ResourceFactory;
import jig.engine.util.Vector2D;

/**
 * A ResourceFactory for Java2D.  This ResourceFactory is 
 * 100% pure Java and does not rely on any other third
 * party native dlls for operation.
 * 
 * The only method that should be called by JIG users is 
 * makeCurrentResourceFactory()
 * 
 * 
 * @author Scott Wallace
 *
 */
public final class J2DResourceFactory extends ResourceFactory {

	/** The single J2D game frame, if one has been created. */
	private J2DGameFrame frame;

	/**
	 * Creates the singleton instance of the factory.
	 */
	private J2DResourceFactory() {
		super();
		frame = null;
		
	};

	/**
	 * Makes an instance of the J2DResourceFactory the current,
	 * canonical, resource factory. Once the canonical factory
	 * is set, it cannot be changed for the life of the application.
	 *
	 * @see ResourceFactory#getFactory()
	 * @see ResourceFactory#setCurrentResourceFactory(ResourceFactory)
	 */
	public static void makeCurrentResourceFactory() {
		ResourceFactory.setCurrentResourceFactory(new J2DResourceFactory());
	}
	/**
	 * Creates a new ImageResource that has the 'best chance'
	 * of being hardware accelerated.  Specifically, this
	 * method creates an image that is compatible with the
	 * graphics devices used by the game frame.
	 * 
	 * @param originalImg the original image
	 * @param transparency the desired transparency mode
	 * @param w the width of the resulting image resource
	 * @param h the height of the resulting image resource
	 * @param xoffset the xoffset of the resulting image with
	 *        respect to the original image
	 * @param yoffset the yoffset of the resulting image with
	 *        respect to the original image
	 * @return a new 'internally formatted' image resource
	 * 
	 */
	@Override
	protected ImageResource createImageResource(final BufferedImage originalImg,
			final int transparency, final int w, final int h, 
			final int xoffset, final int yoffset) {

		long st = System.nanoTime();
		ImageResource r = new J2DImage(originalImg, transparency, w, h, -xoffset,
				-yoffset, frame);
		long t = ((System.nanoTime()-st)/(jig.engine.GameClock.NANOS_PER_MS));
		if (t > 300) {
		//	System.out.println("Created Image in " + t + " ms");
		}
		return r;
	}

	/**
	 * Creates a container for displaying the game.
	 *  
	 * @param title the name to display on the frame (if applicable)
	 * @param cfg a JIG-E graphics configuration
	 * 
	 * @return a container within which the game can be rendered
	 */
	@Override
	public GameFrame getGameFrame(final String title, final int w, final int h,
			final boolean preferredFullScreen) {
		if (frame == null) {
			frame = J2DGameFrame.getGameFrame(title, w, h, preferredFullScreen);
			return frame;
		} else {

			throw new RuntimeException("Only one game frame can exist...");
		}

	}
	
	/**
	 * Creates a <code>FontResource</code> wrapper over
	 * a standard system vector based font (e.g., a true type font).
	 *
	 * @param f the system font requested
	 * @return a font resource for the specified system font.
	 */
	@Override
	public FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor)
	{
		if (frame != null) {
			return new J2DVectorFont(f, frame, fontColor, backgroundColor);
		} else {
			throw new IllegalStateException("Font Resources cannot be acquired until after a game frame has been created.");
		}
	}
	
	@Override
	public FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor, boolean prioritzeSpeed) {
		if (prioritzeSpeed)
			return new J2DVectorFont(f, frame, fontColor, backgroundColor);
		else
			return getBitmapFont(f, fontColor, backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CursorResource makeCursor(final String rscName, 
			final Vector2D hotspot, final long delay) {

		return new J2DCursor(getFrames(rscName), rscName, hotspot, delay);

	}
}
