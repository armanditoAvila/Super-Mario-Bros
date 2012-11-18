package jig.engine.j2d;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jig.engine.Console;
import jig.engine.GameFrame;
import jig.engine.Keyboard;
import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;

/**
 * A concrete Game Frame class for the Java-2D backend.
 * 
 * 
 * @author Scott Wallace
 * 
 */
@SuppressWarnings("serial")
final class J2DGameFrame extends Frame implements GameFrame {

	public static final int DESIRED_BUFFERS = 2;

	int width, height;

	JigGC configuration;
	
	Canvas viewport;

	volatile BufferStrategy bufferStrategy;

	J2DRenderingContext theRC;

	AWTKeyboard theKeyboard = null;

	AWTMouse theMouse = null;

	J2DGameFrame.J2DConsole console;

	private boolean rcReleased;

	DisplayMode dispModeBeforeFrameCreation;

	private ExitHandler exitHandler;
	
	private volatile boolean closeAndExitOnNextRender = false;
	
		
	/**
	 * Creates a GameFrame consistent with the specified JIG-E configuration.
	 * 
	 * @param title
	 *            the title of the frame (note this is not visible in fullscreen
	 *            mode)
	 * @param gconfig
	 *            a JigGC configuration object
	 * @return a new GameFrame object
	 */
	static J2DGameFrame getGameFrame(final String title, final int w, 
			final int h, final boolean preferredFullScreen) {
		J2DGameFrame frame;
		

		//it appears we need to reconfigure a couple times to make sure
		//that toggling full screen mode works.
		JigGC gconfig = JigGC.attemptConfiguration(w,
				h, preferredFullScreen);
		GraphicsDevice device = gconfig.getDevice();
		boolean fullscreen = gconfig.fullscreenMode()
				&& device.isFullScreenSupported();
		gconfig = JigGC.attemptReconfiguration(gconfig, false);
		frame = new J2DGameFrame(title, gconfig, device
				.getDefaultConfiguration());
		if (fullscreen) {
			frame.toggleFullscreenMode();
		}
				
		
		
		frame.addWindowListener(new ExitOnCloseListener(frame));

		Logger l = ResourceFactory.getJIGLogger();
		
		if (l.isLoggable(Level.CONFIG)) {
			l.info("Buffer Strategy:" 
				+ frame.bufferStrategy.toString());
			l.info("Page Flipping? "
				+ frame.bufferStrategy.getCapabilities().isPageFlipping());
			l.info("Back Buffer capabilities: accelerated?: "
				+ frame.bufferStrategy.getCapabilities()
						.getBackBufferCapabilities().isAccelerated()
				+ " Volatile?: "
				+ frame.bufferStrategy.getCapabilities()
						.getBackBufferCapabilities().isTrueVolatile());
			l.info("Front Buffer capabilities: accelerated?: "
				+ frame.bufferStrategy.getCapabilities()
						.getFrontBufferCapabilities().isAccelerated()
				+ " Volatile?: "
				+ frame.bufferStrategy.getCapabilities()
						.getFrontBufferCapabilities().isTrueVolatile());
		}
		
		return frame;
	}

	/**
	 * Creates the J2DGameFrame in windowed mode.
	 * 
	 * @param title
	 *            the title of the window
	 * @param gconfig
	 *            the Jig Engine graphics configuration
	 * @param awtgconfig
	 *            the Java AWT graphics configuration of the display device.
	 */
	private J2DGameFrame(final String title, final JigGC gconfig,
			final GraphicsConfiguration awtgconfig) {

		super(title, awtgconfig);

		configuration = gconfig;

		dispModeBeforeFrameCreation = gconfig.getDevice().getDisplayMode();
		width = gconfig.getWidth();
		height = gconfig.getHeight();

		// the viewport is the canvas we'll be drawing on
		viewport = new Canvas();
		// setLayout(new GridLayout(1,1));
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		// setLayout(new BorderLayout());
		// add(viewport, BorderLayout.CENTER);
		add(viewport);

		viewport.setIgnoreRepaint(true);
		// since we're not running full screen, make sure the frame
		// and canvas are the correct size...

		viewport.setSize(gconfig.getWidth(), gconfig.getHeight());
		viewport.setPreferredSize(new Dimension(gconfig.getWidth(), gconfig
				.getHeight()));

		// must pack the frame before setting resizeable(false) as of jdk 1.6b69
		pack();

		// this doesn't need to be a Transparent mode image...
		// we can still render this with half alpha ontop of
		// the game frame's back buffer if the console image itself
		// is opaque.  
		// NOTE:  this gives a huge speed up on my machine
		// console in Joust goes from ~10 fps to full speed
		console = new J2DConsole(getGraphicsConfiguration()
				.createCompatibleVolatileImage(width, height / 4,
						Transparency.OPAQUE), this);

		setResizable(false);
	 //	setVisible(true);

		// create the double buffer
		viewport.createBufferStrategy(DESIRED_BUFFERS);
		viewport.setBackground(Color.LIGHT_GRAY);		
		bufferStrategy = viewport.getBufferStrategy();
		theRC = new J2DGameFrame.J2DRenderingContext();
		rcReleased = false;
		ResourceFactory.getJIGLogger().config(
				"Viewport size: " + viewport.getSize().toString()
				+ " requested:" + gconfig.getWidth() + "x"
				+ gconfig.getHeight());
		

		System.out.println("BufferStrategy:" + bufferStrategy.toString());
	}
	

	

	
	/**
	 * Switches the game frame between fullscreen exclusive and windowed mode
	 * if possible.
	 */
	public boolean toggleFullscreenMode() {
		/*
		{
			String ts = Thread.currentThread().toString();
			if (!ts.contains("[main") || ! ts.contains("main]")) {
				Exception e = new IllegalArgumentException();
				System.out.println(e.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
			}
		}
		*/
		final boolean visibility = isVisible();
		GraphicsDevice gdev = configuration.getDevice();
		JigGC newConfig = JigGC.attemptReconfiguration(configuration,
				!configuration.fullscreenMode());

		
		if (newConfig == null) {
			return false;
		}
		
		setVisible(false);

		if (newConfig.fullscreenMode()) {
		
			try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					dispose();
					setUndecorated(true);
						
				}
				
			}); }
			catch (Exception ee) {}
			
			//dispose();
			//setUndecorated(true);
			
			// do this first when switching from windowed --> fs
			gdev.setFullScreenWindow(this);
			
			for (DisplayMode dm : newConfig.bestModes()) {

				try {
					gdev.setDisplayMode(dm);
					ResourceFactory.getJIGLogger().config(
							"Set Mode to: "
							+ JigGC.getDisplayModeString(dm));
					break;
				} catch (IllegalArgumentException ex) {
					ResourceFactory.getJIGLogger().config(
							"Couldn't set mode: "
							+ JigGC.getDisplayModeString(dm));
				}
  
			}		
		} else {
			if (gdev.isDisplayChangeSupported()) {
				
				List<DisplayMode> modes = configuration.bestModes();
				// this is our preferred mode...try it first
				modes.add(0, configuration.getInitialDisplayMode());
				
				for (DisplayMode dm : modes) {
					try {
						gdev.setDisplayMode(dm);
						break;
					} catch (IllegalArgumentException ex) {
						ResourceFactory.getJIGLogger().config(
								"Couldn't set mode: "
								+ JigGC.getDisplayModeString(dm));
					}
					
				}
			} 
			try {
				javax.swing.SwingUtilities.invokeAndWait(new Runnable() {

					public void run() {
						dispose();
						setUndecorated(false);
							
					}
					
				}); }
				catch (Exception ee) {}
			
			//dispose();
			//setUndecorated(false);
			// has to be done at the end when going from fs --> window
			gdev.setFullScreenWindow(null);

		}			
		
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					setVisible(visibility);
					validate();
						
				}
				
			}); }
			catch (Exception ee) {}
		
		//setVisible(visibility);
		//validate();

		// Recreate back buffers and rendering context...
		viewport.createBufferStrategy(DESIRED_BUFFERS);
		viewport.setBackground(Color.LIGHT_GRAY);
		BufferStrategy bs = bufferStrategy;
		bufferStrategy = viewport.getBufferStrategy();
		
		
		System.out.println("Buffer Strategy switched from: " +bs.toString() + " to " + bufferStrategy.toString());

		theRC = new J2DGameFrame.J2DRenderingContext();
		rcReleased = false;
		setVisible(visibility);
		configuration = newConfig;
			

		System.out.println("BufferStrategy:" + bufferStrategy.toString());
		
		return true;
	}
	
	/**
	 * @return a static reference to a pollable keyboard.
	 */
	public Keyboard getKeyboard() {
		if (theKeyboard == null) {
			theKeyboard = new AWTKeyboard(this);
			// This initializer works with both
			// full screen and windowed mode
			theKeyboard.init();
		}
		return theKeyboard;
	}

	/**
	 * @return a static reference to a pollable mouse.
	 */
	public Mouse getMouse() {
		if (theMouse == null) {
			theMouse = new AWTMouse(this);
			// This initializer works with both
			// full screen and windowed mode
			theMouse.init();
		}
		return theMouse;
	}

	/**
	 * Gets the game frame's rendering context which is used by other object
	 * that wish to draw themselves onto the game frame's visible surface.
	 * 
	 * 
	 * @return the rendering context for this game frame
	 */
	public RenderingContext getRenderingContext() {
		/*
		{
			String ts = Thread.currentThread().toString();
			if (!ts.contains("[main") || ! ts.contains("main]")) {
				Exception e = new IllegalArgumentException();
				System.out.println(e.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
			}
		}
		*/
		
		if (rcReleased) {
			return null;
		}

		theRC.prelude();
		rcReleased = true;
		return theRC;
	}

	/**
	 * @return a static reference to the game frame's command console
	 */
	public Console getConsole() {
		return console;
	}

	/**
	 * Display the game frame's back buffer. That is, render the current
	 * viewable state to the screen.
	 * 
	 */
	public void displayBackBuffer() {
		/*
		{
			String ts = Thread.currentThread().toString();
			if (!ts.contains("[main") || ! ts.contains("main]")) {
				Exception e = new IllegalArgumentException();
				System.out.println(e.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
			}
		}
		*/
		
		if (bufferStrategy != null && !bufferStrategy.contentsLost()) {
			// Transfer the contents of the buffer to the screen
			if (rcReleased) {
				theRC.theG.dispose();
				rcReleased = false;
			}
			console.renderToBackBuffer();
			//System.out.println("Display BB: " + bufferStrategy.toString());
			bufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();
			Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
			Color bg = g.getColor();
			g.setColor(Color.green);
			g.clearRect(0, 0, width, height);
			g.setColor(bg);
			g.dispose();
		}
	}

	/**
	 * Clears the back buffer so it can be redrawn.
	 */
	public void clearBackBuffer() {
		/*
		 {
		 
			String ts = Thread.currentThread().toString();
			if (!ts.contains("[main") || ! ts.contains("main]")) {
				Exception e = new IllegalArgumentException();
				System.out.println(e.getStackTrace()[0].toString() + ":: " + Thread.currentThread().toString());				
			}
		}
		*/
		
		bufferStrategy.getDrawGraphics().clearRect(0, 0, width, height);

	}

	/**
	 * An empty method. There's no reason to call this.
	 * 
	 * @param g
	 *            ignored
	 */
	@Override
	public void paint(final Graphics g) {
	}

	/**
	 * An empty method. Updating is active, and repaint is not required.
	 */
	@Override
	public void repaint() {
	}

	/**
	 * An empty method. Updating is done actively in the game frame, not
	 * passively.
	 * 
	 * @param g
	 *            ignored
	 */
	@Override
	public void update(final Graphics g) {
		// displayBackBuffer();
	}

	/**
	 * This method should not be called by the user.
	 * 
	 * @param l
	 *            the mouse listener
	 */
	@Override
	public void addMouseListener(final MouseListener l) {
		if (viewport != null) {
			viewport.addMouseListener(l);
		}
		super.addMouseListener(l);
	}

	/**
	 * This method should not be called by the end user, it's overridden here to
	 * provide the appropriate hook for the J2DKeyboard object.
	 * 
	 * @param l
	 *            the KeyListener
	 */
	@Override
	public void addKeyListener(final KeyListener l) {
		if (viewport != null) {
			viewport.addKeyListener(l);
		}
		super.addKeyListener(l);
	}

	/**
	 * @return the size of the game frame's visible surface
	 */
	@Override
	public Dimension getSize() {
		if (viewport != null) {
			return viewport.getSize();
		} else {
			return new Dimension(640, 480);
		}
	}

	/**
	 * The J2D Game Frame's rendering context.
	 * 
	 * @author Scott Wallace
	 * 
	 */
	public class J2DRenderingContext implements RenderingContext {
		int fontheight;

		Graphics2D theG;

		AffineTransform originalAT;

		/**
		 * Creates a new rendering context instance.
		 * 
		 */
		J2DRenderingContext() {
			FontMetrics fm = getFontMetrics(getFont());
			fontheight = fm.getMaxAscent();
		}

		/**
		 * Prepare the Rendering Context to be used to render the visible
		 * objects in the game. This method is not intended to be called
		 * directly by users.
		 */
		public void prelude() {
			theG = (Graphics2D) bufferStrategy.getDrawGraphics();
			originalAT = theG.getTransform();
		}

		/**
		 * Composes an affine transform with the current global transform
		 * according to the rule last-specified-first-applied. The new
		 * global transform is applied to all visible objects.
		 * 
		 * @param at the transform to be composed with the current transform
		 */
		public void transform(final AffineTransform at) {
			theG.transform(at);
		}

		/**
		 * @return the current global transform applied to all objects
		 */
		public AffineTransform getTransform() {
			return theG.getTransform();
		}

		/**
		 * Sets the global transform to a specified value.
		 * 
		 * @param at
		 *            sets the global transform that is used by the rendering
		 *            context to render all viewable objects.
		 */
		public void setTransform(final AffineTransform at) {
			theG.setTransform(at);
		}

		/**
		 * Renders a string onto the game frame.
		 * 
		 * @param t
		 *            the string to be drawn
		 * @param x
		 *            the x position of the string
		 * @param y
		 *            the y position of the string
		 */
		public void renderString(final String t, final int x, final int y) {
			theG.drawString(t, x, y + fontheight);
		}
	}

	/**
	 * The Console for a J2DGameFrame.
	 * 
	 * @author Scott Wallace
	 * 
	 */
	class J2DConsole extends Console {

		VolatileImage consoleImage;

		Point consoleOffset;

		Font font;

		int renderedBufferVersion;

		int lineHeight;

		int lineDescent = 4;

		int consoleHeight, consoleWidth;

		/**
		 * Creates a new console for the J2DGameFrame.
		 * 
		 * @param img
		 *            a volatileImage appropriately sized to display the console
		 *            contents. The console text will be drawn on the image and
		 *            the image in turn will be rendered (potentially with
		 *            partial transparency) onto the game frame's back buffer.
		 */
		public J2DConsole(final VolatileImage img, GameFrame frame) {
			super(frame);
			consoleImage = img;
			consoleHeight = consoleImage.getHeight();
			consoleWidth = consoleImage.getWidth();
			Graphics g = consoleImage.getGraphics();
			g.setColor(Color.yellow);
			g.fillRect(0, 0, width, height);
			font = new Font("SansSerif", Font.PLAIN, 10);
			lineHeight = font.getSize() + lineDescent;

			// the lowest string will be printed at img.getHeight()-lineDescent
			// find out how many lines will fit in the image... make the buffer
			// this size minus 1 to leave room for the input string...
			setBufferSize(
					((consoleHeight - lineDescent) / lineHeight) - 1);

			renderedBufferVersion = getTextVersion() - 1;
			

		}


		/**
		 * Renders the console to the game frame's back buffer.
		 */
		@Override
		public void renderToBackBuffer() {
			if (visible) {
				Composite c;
				Graphics2D g;
				if (renderedBufferVersion != getTextVersion()) {
					g = (Graphics2D) consoleImage.getGraphics();
					g.setFont(font);
					g.setColor(Color.yellow);
					g.fillRect(0, 0, consoleWidth, consoleHeight);
					g.setColor(Color.black);
					int x;
					int y = consoleHeight - (size() * lineHeight)
							- lineDescent;
					for (Iterator<String> s = lines(); s
							.hasNext();) {
						for (x = 0; x < columns; x++) {
							g.drawString(s.next(), x * (width / columns), y);
						}
						y += lineHeight;
					}
					g.drawString(workingLine(), 0, y);
					g.dispose();
					renderedBufferVersion = getTextVersion();
				}

				g = (Graphics2D) bufferStrategy.getDrawGraphics();
				c = g.getComposite();
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, .75f));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
				g.drawImage(consoleImage, 0, 0, null);
				g.setComposite(c);
				g.dispose();
			}
		}

	}

	public void registerExitHandler(ExitHandler exitHandler) {
		this.exitHandler = exitHandler;
	}


	public boolean getFullScreen() {
		return configuration.fullscreenMode();
	}

	public int getWidth()
	{
		return configuration.getWidth();
	}
	
	public int getHeight()
	{
		return configuration.getHeight();
	}
	
	public synchronized boolean requestExitAndClose(boolean forcedExit) {
		if(!forcedExit && exitHandler != null &&
				!exitHandler.handleRequestedExit())
		{
			return false;
		}
		closeAndExitOnNextRender = true;
		return true;
	}
	
	public synchronized boolean isExitAndCloseRequested() {
		return closeAndExitOnNextRender;
	}
		
	/**
	 * {@inheritDoc}
	 */
	public void closeAndExit() {
		setVisible(false);
		dispose();
		System.exit(0);	
	}
}


/**
 * The Jig Graphics Configuration object contains the configuration information
 * needed to initialize the game environment.
 * 
 * 
 * @author Scott Wallace
 *  
 */
class JigGC {

	
	/** The desired height of the game's frame or window. */
	private int frameHeight;

	/** The desired width of the game's frame or window. */
	private int frameWidth;

	/** <code>true</code> iff the game should be run fullscreen. */
	boolean fullscreen;

	/** The java GraphicsEnvironment where the game frame will be displayed. */
	GraphicsEnvironment gfxEnv;

	/** The java GraphicsDevice where the game frame will be displayed. */
	GraphicsDevice gfxDevice;
	
	/** The DisplayMode at configuration time (a mode known to be useable). */
	DisplayMode configTimeDisplayMode;


	/**
	 * Copy an existing JigGC configuration object.
	 * 
	 * @param src
	 *            the source JigGC to be copied.
	 */
	JigGC(final JigGC src) {
		frameHeight = src.frameHeight;
		frameWidth = src.frameWidth;
		fullscreen = src.fullscreen;

		gfxEnv = src.gfxEnv;
		gfxDevice = src.gfxDevice;
		configTimeDisplayMode = src.configTimeDisplayMode; 
	}

	/**
	 * Creates a new JigGC configuration object based on specified values.
	 * 
	 * @param w
	 *            the desired width of the game's drawing surface
	 * @param h
	 *            the desired height of the game's drawing surface
	 * @param e
	 *            the java GraphicsEnvironment
	 * @param d
	 *            the java GraphicsDevice upon which the game will be displayed
	 */
	JigGC(final int w, final int h, final GraphicsEnvironment e,
			final GraphicsDevice d) {

		frameHeight = h;
		frameWidth = w;
		fullscreen = false;

		gfxEnv = e;
		gfxDevice = d;
		if(gfxDevice != null)
			configTimeDisplayMode = gfxDevice.getDisplayMode();
	}

	/**
	 * Attempts to configure JIGE with the specified parameters. Returns
	 * <code>null</code> if the desired settings or an adequate substitute are
	 * not supported by the underlying hardware.
	 * 
	 * @param w
	 *            the desired width of the game's drawing surface
	 * @param h
	 *            the desired height of the game's drawing surface
	 * @param preferredFullScreen
	 *            <code>true</code> if the desired configuration is fullscreen
	 *            mode
	 * 
	 * @return a finalized JIGE configuration
	 */
	public static JigGC attemptConfiguration(final int w, final int h,
			final boolean preferredFullScreen) {
		
		GraphicsEnvironment genv;
		GraphicsDevice gdev;
		JigGC configuration;
	
		try {
			genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gdev = genv.getDefaultScreenDevice();
		}
		catch(HeadlessException e)
		{
			genv = null;
			gdev = null;
			ResourceFactory.getJIGLogger().warning("Headless environment will fail unless the NoneResourceFactory is used...");
		}
		
		configuration = new JigGC(w, h, genv, gdev);
		configuration.fullscreen = preferredFullScreen;
	
		if(gdev == null)
			return configuration;
	
		if (configuration.getConsistentDisplayModes().size() > 0) {
			return configuration;
		}
		ResourceFactory.getJIGLogger().info(
				"Cannot create " + 
				((preferredFullScreen) ? " fullscreen " : " windowed ") + 
				" configuration, trying " +
				((preferredFullScreen) ? " windowed " : " fullscreen ") + " mode");
		configuration.fullscreen = !preferredFullScreen;
		if (configuration.getConsistentDisplayModes().size() > 0) {
			return configuration;
		}

		// can't find a display mode that /could/ support the desired
		// configuration...
		ResourceFactory.getJIGLogger().warning(
				"Cannot create desired configuration: " + w
				+ "x" + h);
		
		return null;

	}
	
	/**
	 * Modifies the JIGE graphics configuration, if possible.
	 * 
	 * @param orig
	 *            the original configuration
	 * @param fullscreen
	 *            <code>true</code> if the new configuration is fullscreen
	 * @return a new configuration object or null if the change is not possible
	 */
	public static JigGC attemptReconfiguration(final JigGC orig,
			final boolean fullscreen) {
		JigGC newconfig = attemptConfiguration(orig.frameWidth,
				orig.frameHeight, fullscreen);
		
		if (newconfig == null) {
			return null;
		}
		newconfig.configTimeDisplayMode = orig.configTimeDisplayMode;
		return newconfig;
	}

	/**
	 * @return the desired height of the game's drawing surface
	 */
	public int getHeight() {
		return frameHeight;
	}

	/**
	 * @return the desired width of the game's drawing surface
	 */
	public int getWidth() {
		return frameWidth;
	}

	/**
	 * @return <code>true</code> iff the game should run fullscreen
	 */
	public boolean fullscreenMode() {
		return fullscreen;
	}

	/**
	 * @return the java GraphicsDevice upon which the game's drawing surface
	 *         will reside
	 *         
	 */
	public GraphicsDevice getDevice() {
		return gfxDevice;
	}

	/**
	 * @return the DisplayMode that was in use when this configuration was
	 * created.
	 */
	public DisplayMode getInitialDisplayMode() {
		return configTimeDisplayMode;
	}
	/**
	 * Returns a list of <code>DisplayMode</code>s that are consistent with
	 * the desired height and width of the JIG configuration.
	 * 
	 * @return a List of <code>DisplayMode</code>s
	 */
	public LinkedList<DisplayMode> getConsistentDisplayModes() {

		DisplayMode[] displayModes = gfxDevice.getDisplayModes();

		LinkedList<DisplayMode> dml = new LinkedList<DisplayMode>();

		/*
		 * according to GraphicsDevice.isDisplayChangeSupported() documentation,
		 * on some platforms this function's return may change after fullscreen
		 * exclusive mode has been entered. Thus we cannot know for certain
		 * whether the desired fullscreen mode will be supported until we try...
		 * 
		 * In windowed mode, however, we do know a bit more.
		 */
		if (!fullscreen && !gfxDevice.isDisplayChangeSupported()) {
			// we can only use the current mode -- make sure it
			// is consistent...
			DisplayMode dm = gfxDevice.getDisplayMode();
			if (dm.getWidth() > frameWidth && dm.getHeight() > frameHeight) {
				dml.add(dm);
			}
		} else {
			// we may be able to change modes...
			for (int i = 0, n = displayModes.length; i < n; i++) {
				if (fullscreen && displayModes[i].getWidth() == frameWidth
						&& displayModes[i].getHeight() == frameHeight) {

					dml.add(displayModes[i]);

				} else if (!fullscreen
						&& displayModes[i].getWidth() > frameWidth
						&& displayModes[i].getHeight() > frameHeight) {

					dml.add(displayModes[i]);

				}
			}
		}
		return dml;
	}

	/**
	 * <code>DisplayMode.toString()</code> is useless and only yields the
	 * results of <code>Object.toString()</code>. This method creates a
	 * reasonable string representation of a given DisplayMode.
	 * 
	 * @param d
	 *            a DisplayMode
	 * @return a String representation of the DisplayMode
	 */
	public static String getDisplayModeString(final DisplayMode d) {
		return "<Display Mode>: " + d.getWidth() + "x" + d.getHeight() + " @ "
				+ d.getRefreshRate() + " - " + d.getBitDepth() + "bpp";

	}

	/**
	 * Returns a multi-line string with detailed configuration information
	 * including relevant System properties.
	 * 
	 * @return the configuration represented as a String
	 */
	public String detailedConfiguration() {
		StringBuffer sb = new StringBuffer(200);

		Properties p = System.getProperties();
		sb.append("--- Configuration Details ---\n");
		sb.append("java.vm.version");
		sb.append(" --> ");
		sb.append(p.get("java.vm.version"));
		sb.append('\n');
		sb.append("sun.java2d.opengl");
		sb.append(" --> ");
		sb.append(p.get("sun.java2d.opengl"));
		sb.append("sun.java2d.d3d");
		sb.append(" --> ");
		sb.append(p.get("sun.java2d.d3d"));
		sb.append('\n');

		sb.append("Graphics Device ");
		sb.append(gfxDevice.getIDstring());
		sb.append('\n');
		sb.append("  Available accelerated memory: ");
		sb.append(gfxDevice.getAvailableAcceleratedMemory());
		sb.append('\n');
		sb.append("  Fullscreen supported: ");
		sb.append(gfxDevice.isFullScreenSupported());
		sb.append('\n');
		sb.append("  Display change supported: ");
		sb.append(gfxDevice.isDisplayChangeSupported());
		sb.append('\n');

		sb.append("Top two Display Modes:");
		Iterator<DisplayMode> idm = bestModes().iterator();
		for (int i = 0; i < 2 && idm.hasNext(); i++) {
			sb.append("\n  ");
			sb.append(idm.next().toString());
		}
		sb.append('\n');

		return sb.toString();
	}

	/**
	 * @return a <code>List</code> of DisplayModes in "best-first" order.
	 */
	public List<DisplayMode> bestModes() {
		DisplayMode currentBest;
		int cbIndex;
		int i;
		boolean foundSameBitDepth = false;
		
		LinkedList<DisplayMode> modes = getConsistentDisplayModes();		
		LinkedList<DisplayMode> orderedModes = new LinkedList<DisplayMode>();
		System.out.println(modes.size() + " consistent modes");
		
		while (modes.size() > 0) {
			currentBest = modes.getFirst();
			cbIndex = 0;
			i = 0;
			for (DisplayMode dm : modes) {
				if (dm.getBitDepth() 
						== gfxDevice.getDisplayMode().getBitDepth()) {
					currentBest = dm;
					cbIndex = i;
					foundSameBitDepth = true;
				} else if (!foundSameBitDepth 
						&& dm.getBitDepth() > currentBest.getBitDepth()) {
					currentBest = dm;
					cbIndex = i;
				}
				i++;
			}
			orderedModes.add(modes.remove(cbIndex));
		}
		System.out.println(orderedModes.size() + " ordered modes");
		return orderedModes;

	}


}

/**
 * A simple WindowListener that exits when the close button is pressed on the
 * frame/window.
 * 
 * @author Scott Wallace
 * 
 */
class ExitOnCloseListener implements WindowListener {

	J2DGameFrame frame;
	
	public ExitOnCloseListener(J2DGameFrame frame)
	{
		this.frame = frame;
	}
	
	/**
	 * Stops the program when the window is closed.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowClosing(final WindowEvent arg0) {
		frame.requestExitAndClose(false);
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowOpened(final WindowEvent arg0) {
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowClosed(final WindowEvent arg0) {
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowIconified(final WindowEvent arg0) {
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowDeiconified(final WindowEvent arg0) {
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowActivated(final WindowEvent arg0) {
	}

	/**
	 * An empty stub.
	 * 
	 * @param arg0
	 *            ignored
	 */
	public void windowDeactivated(final WindowEvent arg0) {
	}

}


