package jig.engine.j2d;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import jig.engine.CursorResource;
import jig.engine.Mouse;

/**
 * 
 * A pollable mouse that facilitates changing the mouse cursor.
 * 
 * @author Scott Wallace
 *
 */
class AWTMouse implements Mouse {
	private Point location = new Point(0, 0);
	private boolean rightButtonDown = false;
	private boolean leftButtonDown = false;
	private final J2DGameFrame gameFrame;
	private J2DCursor cursor;
	CursorAnimatorThread cursorAnimatorThread = null;
	
	public AWTMouse(final J2DGameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	/**
	 * Initialize the mouse handler to listen to all events.
	 */
	public void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new MouseHandler(),
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		
	}

	/**
	 * Initialize the mouse handler to listen to events within a component.
	 * 
	 * @param c
	 *            The component that we will listen to
	 */
	public void init(final Component c) {
		MouseHandler mh = new MouseHandler();
		c.addMouseListener(mh);
		c.addMouseMotionListener(mh);
	}

	/**
	 * 
	 * @return <code>true</code> iff one or more mouse button is pressed.
	 */
	public boolean isButtonPressed() {
		return (rightButtonDown || leftButtonDown);
	}

	/**
	 * 
	 * @return <code>true</code> iff the right mouse button is pressed.
	 */
	public boolean isRightButtonPressed() {
		return rightButtonDown;
	}

	/**
	 * 
	 * @return <code>true</code> iff the left mouse button is pressed.
	 */
	public boolean isLeftButtonPressed() {
		return leftButtonDown;
	}

	/**
	 * 
	 * @return the location of the mouse.
	 */
	public Point getLocation() {
		// we'll return the actual reference.  It won't matter so
		// much if the user modifies it as it will be unlinked and the
		// internal reference will be replaced by the location received
		// in the mouse event.
		return location;
	}

	/**
	 * A class to respond to mouse events.
	 * 
	 */
	private class MouseHandler implements AWTEventListener, MouseListener,
			MouseMotionListener {

		/**
		 * Notification that an event has occurred in the AWT event system.
		 * 
		 * @param e
		 *            The event details
		 */
		public void eventDispatched(final AWTEvent e) {
			if (e.getID() == MouseEvent.MOUSE_PRESSED) {
				mousePressed((MouseEvent) e);
			} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
				mouseReleased((MouseEvent) e);
			} else if (e.getID() == MouseEvent.MOUSE_MOVED) {
				mouseMoved((MouseEvent) e);
			} else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
				mouseDragged((MouseEvent) e);

			}

		}

		/**
		 * Updates the state of the mouse buttons.
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mousePressed(final MouseEvent arg0) {
			if (SwingUtilities.isLeftMouseButton(arg0)) {
				leftButtonDown = true;
			}
			if (SwingUtilities.isRightMouseButton(arg0)) {
				rightButtonDown = true;
			}
			location = arg0.getPoint();

		}

		/**
		 * Updates the state of the mouse buttons.
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseReleased(final MouseEvent arg0) {
			if (SwingUtilities.isLeftMouseButton(arg0)) {
				leftButtonDown = false;
				location = arg0.getPoint();
			}
			if (SwingUtilities.isRightMouseButton(arg0)) {
				rightButtonDown = false;
				location = arg0.getPoint();
			}
		}

		/**
		 * Updates the mouse location (when button is not pressed).
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseMoved(final MouseEvent arg0) {
			location = arg0.getPoint();
		}

		/**
		 * Updates the mouse location (when button is pressed).
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseDragged(final MouseEvent arg0) {
			location = arg0.getPoint();
		}

		/**
		 * This is a high-level event, ignored by our listener.
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseClicked(final MouseEvent arg0) {
		}

		/**
		 * Unused.
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseEntered(final MouseEvent arg0) {
		}

		/**
		 * Unused.
		 * This method is not intended to be called by the user.
		 *  
		 * @param arg0 the mouse event received
		 */
		public void mouseExited(final MouseEvent arg0) {
		}

	}
	/**
	 * {@inheritDoc}
	 */
	public void setCursor(final CursorResource c) {
		if (cursorAnimatorThread != null) {
			cursorAnimatorThread.stopAnimating();
			cursorAnimatorThread = null;
		}
		if (c == null) {
			resetCursor();
			return;
		}
		// DESIGN should avoid this downcast if possible
		cursor = ((J2DCursor) c);
		Cursor[] ca = cursor.getCursor();
		if (ca.length == 1) {
			gameFrame.setCursor(ca[0]);
		} else {
			cursorAnimatorThread = new CursorAnimatorThread();
			cursorAnimatorThread.start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCursorSet() {
		return cursor != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetCursor() {
		if (cursorAnimatorThread != null) {
			cursorAnimatorThread.stopAnimating();
			cursorAnimatorThread = null;
		}
		cursor = null;
		gameFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * {@inheritDoc}
	 */
	public CursorResource getCursor() {
		return cursor;
	}

	/**
	 * This thread will control the animation of the cursor. All attempts have
	 * been made to ensure that at ANY point after
	 * {@link CursorAnimatorThread#stopAnimating()} has been called it will not
	 * change the cursor again and will promptly terminate.
	 * 
	 * @author Aaron Mills
	 * 
	 */
	private class CursorAnimatorThread extends Thread {
		private Boolean animate = true;

		/**
		 * Do not call directly. Your program will hang if you do. Call the
		 * {@link Thread#start()} method instead.
		 */
		public void run() {
			int count = 0;
			while (animate && cursor != null) {
				synchronized (animate) {
					if (animate && cursor != null) {
						Cursor[] c = cursor.getCursor();
						gameFrame.setCursor(c[count % c.length]);
					}
				}
				count++;

				try {
					sleep(cursor.getAnimationDelay());
				} catch (Exception ex) {
					// don't fret.
					continue;
				}
			}
		}

		/**
		 * Set the flag to cease animation and terminate. The cursor will not be
		 * changed again by this thread after this method is finished.
		 */
		public void stopAnimating() {
			synchronized (animate) {
				animate = false;
				interrupt();
			}
		}
	}
}
