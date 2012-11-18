package jig.engine.none;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.PriorityQueue;

import jig.engine.CursorResource;
import jig.engine.FontResource;
import jig.engine.GameClock;
import jig.engine.GameFrame;
import jig.engine.ImageResource;
import jig.engine.KeyInfo;
import jig.engine.ResourceFactory;
import jig.engine.GameClock.Alarm;
import jig.engine.util.Vector2D;

/**
 * A ResourceFactory for NOT rendering -- which is to say that if this ResourceFactory
 * is used, JIG can be used in a headless environment and have graphics rendered
 * to a series of jpeg images that can latter be used as 'snapshots'.
 * 
 * Most users won't ever need this ResourceFactory.
 * 
 * DESIGN: perhaps this resource factory should go into jig.misc.none since it usage is atypical/special case
 * @author Scott Wallace
 *
 */
public final class NoneResourceFactory extends ResourceFactory {

	/** The single J2D game frame, if one has been created. */
	private NoneGameFrame frame;

	Alarm renderingAlarm;
	long renderingPeriod;
	int framesToRender;
	int renderedCount;
	
	/* rendering will either take place to memory or to file (or not all) */
	String renderFile;
	ArrayList<BufferedImage> renderedFrames;
	PriorityQueue<FutureKeyEvent> futureKeys;

	
	/**
	 * Creates the singleton instance of the factory.
	 */
	private NoneResourceFactory() {
		super();
		frame = null;
		renderingAlarm = GameClock.getClock().getSentinelAlarm(false);
		futureKeys = new PriorityQueue<FutureKeyEvent>();
		framesToRender = 0;
		renderedCount = 0;
	};
	

	public static void resetKeyboard() {
		try {
			NoneResourceFactory nrf = (NoneResourceFactory)ResourceFactory.getFactory();
			if (nrf == null) return;
			nrf.frame.theKeyboard.reset();
		} catch (ClassCastException cce) {}
	}
	public BufferedImage getRenderedFrame(int i) {
		if (i < 0 || i >= renderedFrames.size()) return null;
		return renderedFrames.get(i);
	}
	public static void scheduleKeyEvent(long delayMs, int key, boolean pressed) {
		
		try {
			NoneResourceFactory nrf = (NoneResourceFactory) ResourceFactory.getFactory();
		
			nrf.futureKeys.add(new FutureKeyEvent(delayMs*GameClock.NANOS_PER_MS, key, pressed));
			
		}
		catch (ClassCastException cce) {
			ResourceFactory.jigLog.warning("Can't scheduleKeyEvent, the NoneResourceFactory has not been selected...");
		}
	}

	public static void setRendering(long pauseBeforeStartMS, long periodMS, int maxFrames, String filename) {
		
		try {
			NoneResourceFactory nrf = (NoneResourceFactory) ResourceFactory.getFactory();
		
		
			nrf.renderingAlarm = GameClock.getClock().setAlarm(pauseBeforeStartMS*GameClock.NANOS_PER_MS);
			nrf.renderingPeriod = periodMS * GameClock.NANOS_PER_MS;
			nrf.framesToRender = maxFrames;
			nrf.renderedCount = 0;
			nrf.renderFile = filename;
			if (nrf.renderFile == null) {
				nrf.renderedFrames = new ArrayList<BufferedImage>();
			} else {
				nrf.renderedFrames = null;
			}
		}
		catch (ClassCastException cce) {
			ResourceFactory.jigLog.warning("Can't setRendering(), the NoneResourceFactory has not been selected...");
		}
	}

	/**
	 * Makes an instance of the J2DResourceFactory the current,
	 * canonical, resource factory. Once the canonical factory
	 * is set, it cannot be changed for the life of the application.
	 *
	 * @see ResourceFactory#getFactory()
	 * @see ResourceFactory#setCurrentResourceFactory(ResourceFactory)
	 */
	public static void makeCurrentResourceFactory() {
		ResourceFactory.setCurrentResourceFactory(new NoneResourceFactory());
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

		ImageResource r = new NoneImage(originalImg, transparency, w, h, -xoffset,
				-yoffset, frame);
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
			frame = NoneGameFrame.getGameFrame(title, w, h, preferredFullScreen);
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
	public FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor) {
		return getBitmapFont(f, fontColor, backgroundColor);
	}
	
	@Override
	public FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor, boolean prioritzeSpeed) {
		return getBitmapFont(f, fontColor, backgroundColor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CursorResource makeCursor(final String rscName, 
			final Vector2D hotspot, final long delay) {

		return new NoneCursor(getFrames(rscName), rscName, hotspot, delay);

	}
	
	static class FutureKeyEvent implements Comparable<FutureKeyEvent>{
		long time;
		int key;
		boolean pressed;
		Alarm alarm;
		public FutureKeyEvent(long time, int key, boolean pressed) {
			this.time = time;
			this.key = key;
			this.pressed = pressed;
			alarm = GameClock.getClock().setAlarm(time);
		}

		public int compareTo(FutureKeyEvent o) {
			if (time < o.time) return -1;
			if (time == o.time) return 0;
			return 1;
		}
		public KeyInfo getKeyInfo() {
			return new KeyInfo(KeyInfo.NO_CHAR, key, pressed?KeyInfo.State.PRESSED:KeyInfo.State.RELEASED);
		}
	}

	}
