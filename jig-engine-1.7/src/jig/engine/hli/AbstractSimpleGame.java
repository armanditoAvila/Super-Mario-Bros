package jig.engine.hli;

import jig.engine.ConsoleCommandHandler;
import jig.engine.GameClock;
import jig.engine.GameFrame;
import jig.engine.Keyboard;
import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.GameClock.SleepIfNeededTimeManager;
import jig.engine.util.Vector2D;

/**
 * An abstract base class for simple game types in the jig.engine.hli package.
 * Games that subclass AbstractSimpleGame contain a FrameRateElement that can
 * optionally be displayed by using the console command 'fps'.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public abstract class AbstractSimpleGame {
	
	private static final double MAX_FPS = 200;

	protected GameFrame gameframe;

	protected Keyboard keyboard;

	protected Mouse mouse;

	protected FrameRateElement fre;

	protected boolean running = false;
	
	protected GameClock theClock;
	
	protected SleepIfNeededTimeManager timeManager;
	

	/**
	 * Creates a new game with the desired configuration.
	 * 
	 * @param desiredWidth
	 *            the desired width of the game's visible surface
	 * @param desiredHeight
	 *            the desired height of the game's visible surface
	 * @param preferFullScreen
	 *            <code>true</code> iff it is preferred to run the game in
	 *            fullscreen exclusive mode.
	 */
	protected AbstractSimpleGame(final int desiredWidth,
			final int desiredHeight, final boolean preferFullScreen) {
		
		
		theClock = GameClock.getClock();
		
		if (!theClock.timeManagerDefined()) {
			timeManager = new GameClock.SleepIfNeededTimeManager(MAX_FPS);
			theClock.setTimeManager(timeManager);
			ResourceFactory.getJIGLogger().info("Capping the frame rate at approximately: " + MAX_FPS + " fps");
		}

		boolean truePreferFullScreen = preferFullScreen;
				
		gameframe = ResourceFactory.getFactory()
				.getGameFrame("Super Mario Bros", desiredWidth, desiredHeight, truePreferFullScreen);
		
		//frame rate element and an 'fps' command to toggles it
		fre = new FrameRateElement(new Vector2D(gameframe.getWidth() - 150, gameframe
				.getHeight() - 40));
		fre.initializeHistogram(70, 2);
		fre.setActivation(false); // hide the frame rate by default
		gameframe.getConsole().addCommandHandler("fps", fre,
				"toggles the frame rate display, add ' histogram' to print a histogram to stdout");
		
		//full screen toggle console command
		ConsoleCommandHandler commandHandler = new ConsoleCommandHandler() {
			public boolean handle(final String cmd, final String args) {
				if (cmd.equals("fs")) {
					if(!gameframe.toggleFullscreenMode())
					{
						ResourceFactory.getJIGLogger().warning("Cannot toggle to/from fullscreen");
					}
					return true;
				}
				return false;
			}
		};
		gameframe.getConsole().addCommandHandler("fs", commandHandler, "toggles fullscreen mode");
		
		
		//keyboard and mouse
		keyboard = gameframe.getKeyboard();
		mouse = gameframe.getMouse();

	}

	/**
	 * Renders the game.
	 * 
	 * @param rc
	 *            the game frame's rendering context.
	 */
	public void render(final RenderingContext rc) {
		fre.render(rc);
	}

	/**
	 * An abstract method to perform game-specific updates on each iteration of
	 * the game's main loop. This includes things like handling input and moving
	 * objects about.
	 * 
	 * @param deltaMs
	 *            the time elapsed since the last iteration of the game loop.
	 */
	public abstract void update(long deltaMs);

	/**
	 * A simple but complete game loop.
	 * <ol>
	 * <li>Display the back buffer.</li>
	 * <li>Call the abstract <code>update()</code> method to update game
	 * objects.</li>
	 * <li>Render objects to the back buffer.</li>
	 * </ol>
	 * 
	 * Game specific details such as handling input should be done in the
	 * <code>update</code> method. Prologue and Postlogue functionality
	 * can be put into <code>beforeRunning</code> and <code>afterRunning</code>
	 * methods.
	 * 
	 * @see #update(long)
	 * @see #beforeRunning()
	 * @see #afterRunning()
	 */
	public void run() {
		beforeRunning();
				
		// do two updates to warm up the JVM
		// ...then establish the global time
		gameLoop(0);
		gameLoop(0);
		
		theClock.begin();
		
		running = true;
		long partialMs = 0;
		long deltaTime;
		long deltaMs;
		while (running && !gameframe.isExitAndCloseRequested()) {
			theClock.tick();
			// if we're running really fast, then each frame may complete
			// in a very small fraction of a second (less than 1 ms).
			// in this case, we've got a bit of a problem, since the naive
			// approach would simply convert deltaTime to deltaMs by dividing
			// by NANOS_PER_MS.  That, would case deltaMS to always equal 0.
			// So instead, we'll keep track of the remainder and add that the
			// next time through.  This way we're not loosing those 'partial' 
			// milliseconds.
			deltaTime = theClock.getDeltaGameTime() + partialMs;
			deltaMs = deltaTime / GameClock.NANOS_PER_MS;
			partialMs = deltaTime % GameClock.NANOS_PER_MS;
			gameLoop(deltaMs);			
		}
		gameframe.closeAndExit();
	}
	
	private void gameLoop(long deltaMs)
	{
		//render
		RenderingContext rc = gameframe.getRenderingContext();
		render(rc);
		gameframe.displayBackBuffer();
		gameframe.clearBackBuffer();

		//update
		update(deltaMs);	
	}

	/**
	 * This method is called by <code>run</code> before
	 * entering the game loop.  One time initialization
	 * should be done here, and this method is provided
	 * as a hook for subclasses.
	 * 
	 * This default implementation makes the game frame
	 * visible.
	 * 
	 * @see #run()
	 */
	protected void beforeRunning() {
		gameframe.setVisible(true);
	}
}
