package jig.engine.hli;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import jig.engine.ConsoleCommandHandler;
import jig.engine.FontResource;
import jig.engine.GameClock;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.Viewable;
import jig.engine.util.Vector2D;

/**
 * The FrameRateElement is a user interface object that has two main functions:
 * <ol>
 * <li> It will display the current frame rate of the game </li>
 * <li> It will produce a histogram of frame rate information over a period of
 * time. </li>
 * </ol>
 * 
 * @author Scott Wallace 
 * 
 */
public class FrameRateElement implements Viewable, ConsoleCommandHandler {

	/** The rate at which the fps is calculated and the histogram updated. */
	static final long DEFAULT_NANOS_PER_CALCULATION =
		GameClock.NANOS_PER_SECOND / 10;

	/** The frequency with which calculations become visible. */
	static final long DEFAULT_CALCULATIONS_PER_UPDATE = 5;

	/** Default number of histogram bins. */
	static final int DEFAULT_BINS = 12;

	/** Default size of histogram bins. */
	static final int DEFAULT_BIN_SIZE = 10;

	/** Format for how decimals should be displayed. */
	private static final DecimalFormat DFMT = new DecimalFormat("0.0");

	/** The number of bins in this histogram. */
	private int bins;

	/** The size of each bin in this histogram. */
	private int binsize;

	/** Actual histogram data. */
	private int[] histogram;

	private long lastTime;

	private long frames;

	private int ncalculations;

	private int histogramSamples;
	
	private double fps;

	private double fpssum;

	private long npc, cpu;

	protected boolean active;
	
	protected Vector2D position;
	
	protected FontResource font;
	
	protected String fpsstring;

	protected GameClock theClock;

	/**
	 * Creates a FrameRateElement using the default update and calculation
	 * rates.
	 * 
	 * @param p the element's position
	 */
	public FrameRateElement(final Vector2D p) {
		this(DEFAULT_NANOS_PER_CALCULATION, DEFAULT_CALCULATIONS_PER_UPDATE, p);
	}

	/**
	 * Creates a FrameRateElement using the specified update and calculation
	 * rates.
	 * 
	 * @param npc
	 *            number of NANO Seconds between successive frame rate
	 *            calculations (and updates to the histogram)
	 * 
	 * @param cpu
	 *            the number of calculations per update to the string
	 *            representation of the frame rate
	 *
	 * @param p the element's position
	 * 
	 */
	public FrameRateElement(final long npc, final long cpu, final Vector2D p) {
		position = p;
		initializeHistogram(DEFAULT_BINS, DEFAULT_BIN_SIZE);
		
		theClock = GameClock.getClock();
		
		lastTime = 0;
		histogramSamples = 0;

		font = ResourceFactory.getFactory().getFontResource(
					new Font("Sans Serif", Font.PLAIN, 12),
					Color.black, Color.lightGray);
		
		frames = 0;
		ncalculations = 0;
		fps = 0.0;
		fpssum = 0.0;
		fpsstring = "Frame Rate: ?";
		this.npc = npc;
		this.cpu = cpu;
		active = true;
	}

	/**
	 * Clears histogram data.
	 */
	public final void clearHistogram() {
		for (int i = 0; i < bins; i++) {
			histogram[i] = 0;
		}
	}

	/**
	 * Initializes the histogram with a specified granularity. Bins begin at 0,
	 * each is sized based on the parameter below the final bin has no upper
	 * bound.
	 * 
	 * @param bns
	 *            the number of bins in the histogram
	 * @param bnsize
	 *            the size of each bin
	 */
	public final void initializeHistogram(final int bns, final int bnsize) {
		bins = bns;
		binsize = bnsize;

		histogram = new int[bins + 1];
		clearHistogram();
	}

	/**
	 * Renders the FrameRateElement on the game's drawing surface.
	 * 
	 * @param rc
	 *            the RenderingContext associated with the GameFrame
	 */
	public void render(final RenderingContext rc) {
		if (!active) { return; }
		
		font.render(fpsstring, rc, AffineTransform.getTranslateInstance(
				position.getX(), position.getY()));
		
	}

	/**
	 * Updates the frame rate element. This should be called every time through
	 * the game's event loop.
	 * 
	 * @param deltaMs
	 *            the number of milliseconds since the last call
	 */
	public void update(final long deltaMs) {
		long now;
		long diff;

		now = theClock.getWallTime();
		frames++;
		diff = now - lastTime;
		if (diff >= npc) {
			fps = ((double) frames * GameClock.NANOS_PER_SECOND) / diff;
			ncalculations++;
			lastTime = now;
			frames = 0;
			if ((int) (fps / binsize) < bins) {
				histogram[(int) (fps / binsize)]++;
			} else {
				histogram[bins]++;
			}
			histogramSamples++;
			fpssum += fps;

			if (ncalculations >= cpu) {
				fpsstring = "Frame Rate: "
						+ DFMT.format(fpssum / ncalculations);
				ncalculations = 0;
				fpssum = 0.0;
			}
		}
	}

	/**
	 * @return the position of the FrameRateElement
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * @return <code>true</code> iff the frame rate element is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param a <code>true</code> iff the frame rate element should 
	 * become active
	 */
	public void setActivation(final boolean a) {
		active = a;
	}
	
	/**
	 * Handles a command that toggles the display 
	 * of the frame rate element. Typically, this command would be
	 * called 'fps', although because no check is performed within this
	 * method, any name can be registered with the Console.
	 * 
	 * @param cmd the name of the command being handled
	 * 
	 * @param rest the remaining (unparsed) string argument
	 * 
	 * @return <code>true</code> if the command has handled.
	 */
	public boolean handle(final String cmd, final String rest) {
		if (rest.equals("")) {
			active = !active;
		}
		else
		{
			if (rest.toLowerCase().startsWith(" h"))
			{
				System.out.println(histogramString());
			}
		}
		
		return true;
	}
	
	/**
	 * @return a string representation of the object, including current frame
	 *         rate.
	 */
	public String toString() {
		return "[Current " + fpsstring + "]";
	}
	
	/**
	 * Gets a string representation of the histogram.
	 * 
	 * @return the string representation of this histogram
	 */
	public String histogramString() {
		final int initialStringBufferSize = 100;

		StringBuffer sb = new StringBuffer(initialStringBufferSize);
		sb.append("FPS Histogram: ");
		sb.append(histogramSamples);
		for (int i = 0; i < histogram.length; i++) {
			sb.append('[');
			sb.append(i * binsize);
			sb.append(':');
			sb.append(histogram[i]);
			sb.append("] ");
		}
		return sb.toString();
	}

	
}
