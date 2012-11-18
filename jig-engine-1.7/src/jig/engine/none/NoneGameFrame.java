package jig.engine.none;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jig.engine.Console;
import jig.engine.ConsoleCommandHandler;
import jig.engine.CursorResource;
import jig.engine.GameClock;
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
final class NoneGameFrame implements GameFrame {

	int width, height;
	
	NoneRenderingContext theRC;

	NoneKeyboard theKeyboard = null;

	NoneMouse theMouse = null;

	NoneGameFrame.NoneConsole console;

	private ExitHandler exitHandler;

	volatile boolean closeAndExitOnNextRender = false;
		
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
	static NoneGameFrame getGameFrame(final String title, final int w, final int h,
			final boolean preferredFullScreen) {
		NoneGameFrame frame = new NoneGameFrame(title, w, h, preferredFullScreen, null);
		
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
	private NoneGameFrame(final String title, final int w, final int h,
			final boolean preferredFullScreen,
			final GraphicsConfiguration awtgconfig) {

		width = w;
		height = h;
		console = new NoneConsole(this);
		theRC = new NoneRenderingContext();
		
		}
	
	
	/**
	 * Switches the game frame between fullscreen exclusive and windowed mode
	 * if possible.
	 */
	public boolean toggleFullscreenMode() {
		return false;
	}
	
	/**
	 * @return a static reference to a pollable keyboard.
	 */
	public Keyboard getKeyboard() {
		if (theKeyboard == null) {
			theKeyboard = new NoneKeyboard(this);
		}
		return theKeyboard;
	}

	/**
	 * @return a static reference to a pollable mouse.
	 */
	public Mouse getMouse() {
		if (theMouse == null) {
			theMouse = new NoneMouse(this);
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
		NoneResourceFactory nrf = (NoneResourceFactory)ResourceFactory.getFactory();
		if (nrf.renderingAlarm.expired()) {
			// Time to render!
			theRC.rendering = true;

			nrf.renderedCount++;

			if (nrf.renderedCount < nrf.framesToRender) {
				nrf.renderingAlarm.reset(nrf.renderingPeriod);
			} else {
				nrf.renderingAlarm = GameClock.getClock().getSentinelAlarm(false);
			}
						
		} else {
			theRC.rendering = false;
			
		}
		theRC.prelude();
		
		// check to see if any events need to be issued
		NoneResourceFactory.FutureKeyEvent fke = nrf.futureKeys.peek();
		//System.out.println("Time is: " + GameClock.getClock().getGameTime() + " ready to issue " + fke.alarm.remainingTime());
		while (fke != null && fke.alarm.expired()) {
			
			System.out.println("Time is: " + GameClock.getClock().getGameTime() + " ready to issue " + fke.toString());
			fke = nrf.futureKeys.poll();
			((NoneKeyboard)getKeyboard()).enqueue(fke.getKeyInfo());
			fke = nrf.futureKeys.peek();
		}
		
		return theRC;
	}
	public void setCursor(CursorResource c) {
		
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
		theRC.release();
		
	}

	/**
	 * Clears the back buffer so it can be redrawn.
	 */
	public void clearBackBuffer() {
		

	}

	



	/**
	 * The J2D Game Frame's rendering context.
	 * 
	 * @author Scott Wallace
	 * 
	 */
	public class NoneRenderingContext implements RenderingContext {
		int fontheight = 10;

		Graphics2D theG;

		AffineTransform myT;
		AffineTransform identityT;
		
		boolean rendering = false;

		BufferedImage renderingBuffer = null;
		
		/**
		 * Creates a new rendering context instance.
		 * 
		 */
		NoneRenderingContext() {
			identityT = myT = AffineTransform.getTranslateInstance(0, 0);
		}

		/**
		 * Prepare the Rendering Context to be used to render the visible
		 * objects in the game. This method is not intended to be called
		 * directly by users.
		 */
		public void prelude() {
			if (rendering) {
				renderingBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				theG = renderingBuffer.createGraphics();
			}
		}

		public void release() {
			if (rendering) {
				NoneResourceFactory nrf = (NoneResourceFactory)ResourceFactory.getFactory();
				
				if (nrf.renderFile == null) {
					nrf.renderedFrames.add(renderingBuffer);
				} else {
					try {	
						String fn = nrf.renderFile + nrf.renderedCount + ".png";
						ImageIO.write(renderingBuffer, "png", new File(fn));
						theG = null;
					} catch (IOException e) {
						ResourceFactory.getJIGLogger().warning("Couldn't write output frame to specified file!");
					}
				}
				rendering = false;
				renderingBuffer = null;
				theG = null;
			}
			myT = identityT;
			
		}
		/**
		 * Composes an affine transform with the current global transform
		 * according to the rule last-specified-first-applied. The new
		 * global transform is applied to all visible objects.
		 * 
		 * @param at the transform to be composed with the current transform
		 */
		public void transform(final AffineTransform at) {
			if (theG == null) {
			  myT.concatenate(at);
			} else {
				theG.transform(at);
			}
		}

		/**
		 * @return the current global transform applied to all objects
		 */
		public AffineTransform getTransform() {
			if (theG == null) {
				return myT;
			} else {
				return theG.getTransform();
			}
		}

		/**
		 * Sets the global transform to a specified value.
		 * 
		 * @param at
		 *            sets the global transform that is used by the rendering
		 *            context to render all viewable objects.
		 */
		public void setTransform(final AffineTransform at) {
			if (theG == null) {
				myT = at;
			} else {
				theG.setTransform(at);
			}
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
		}
	}

	/**
	 * The Console for a J2DGameFrame.
	 * 
	 * @author Scott Wallace
	 * 
	 */
	class NoneConsole extends Console {

		Point consoleOffset;

		
		Font font;

		int renderedBufferVersion;

		int lineHeight;

		int lineDescent = 4;


		/**
		 * Creates a new console for the J2DGameFrame.
		 * 
		 * @param img
		 *            a volatileImage appropriately sized to display the console
		 *            contents. The console text will be drawn on the image and
		 *            the image in turn will be rendered (potentially with
		 *            partial transparency) onto the game frame's back buffer.
		 */
		public NoneConsole(GameFrame frame) {
			super(frame);
			font = new Font("SansSerif", Font.PLAIN, 10);
			lineHeight = font.getSize() + lineDescent;

			//arbitrary size, its not actually visible
			setBufferSize(10);
			
			renderedBufferVersion = getTextVersion() - 1;
			
			ConsoleCommandHandler commandHandler = new ConsoleCommandHandler() {
				/**
				 * Handles console commands.
				 * 
				 * @param cmd
				 *            the command name
				 * @param args
				 *            a string with unparsed command arguments
				 * @return <code>true</code> iff the command was handled.
				 */
				public boolean handle(final String cmd, final String args) {
					boolean handled = false;
					
					if (cmd.equals("fs")) {
						toggleFullscreenMode();
						return true;
					}
					
					return handled;
				}
			};

			addCommandHandler("fs", commandHandler, "toggles fullscreen mode");
		}


		/**
		 * Renders the console to the game frame's back buffer.
		 */
		@Override
		public void renderToBackBuffer() {
		}

	}

	public void registerExitHandler(ExitHandler exitHandler) {
		this.exitHandler = exitHandler;
	}

	public void setTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	public boolean attemptFullScreen(boolean fullscreen) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getFullScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean requestExitAndClose(boolean forcedExit) {
		if(!forcedExit && exitHandler != null &&
				!exitHandler.handleRequestedExit())
		{
			return false;
		}
		closeAndExitOnNextRender = true;
		return true;
	}
	
	public boolean isExitAndCloseRequested() {
		return closeAndExitOnNextRender;
	}
		
	/**
	 * {@inheritDoc}
	 */
	public void closeAndExit() {
		setVisible(false);
		//System.exit(0);	
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

	GameFrame frame;
	
	public ExitOnCloseListener(GameFrame frame)
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
