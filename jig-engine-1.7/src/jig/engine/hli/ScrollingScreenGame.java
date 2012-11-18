package jig.engine.hli;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

/**
 * A helper class to support a simple scrolling screen game (i.e., a game where
 * the world size is greater than the size of the game frame's viewable
 * surface).
 * 
 * The Scrolling Screen Game supports translating between the world and screen
 * coordinate systems using an AffineTransform
 * 
 * @author Aaron Mills
 * 
 */
public class ScrollingScreenGame extends AbstractSimpleGame {

	/**
	 * A list of ViewableLayers where lower index indicates that greater depth
	 * along the Z-axis.
	 */
	protected List<ViewableLayer> gameObjectLayers;

	/**
	 * The transform applied to the rendering context.
	 */
	private final AffineTransform worldToScreenTransform = new AffineTransform();
	private AffineTransform screenToWorldTransform = new AffineTransform();

	private Rectangle worldBounds;

	private Vector2D screenCenter;
	
	/**
	 * Creates a new static screen game.
	 * 
	 * @param screenWidth
	 *            the desired width of the game's drawing surface
	 * @param screenHeight
	 *            the desired height of the game's drawing surface
	 * @param preferFullscreen
	 *            <code>true</code> if a fullscreen exclusive presentation is
	 *            preferred
	 */
	public ScrollingScreenGame(final int screenWidth, final int screenHeight,
			final boolean preferFullscreen) {

		super(screenWidth, screenHeight, preferFullscreen);

		gameObjectLayers = new LinkedList<ViewableLayer>();

		screenCenter = new Vector2D(gameframe.getWidth() / 2, gameframe.getHeight() / 2);
		
		// register a 'show_bounds' command to toggle drawing of the world
		// bounds.
		// gameframe.getConsole().addCommandHandler("show_bounds",
		// new ConsoleCommandHandler() {
		// @Override
		// public boolean handle(String cmd, String rest) {
		// showBounds = !showBounds;
		// return true;
		// }
		// });
	}
	
	/**
	 * Computes a world coordinate from a screen coordinate.
	 * @param s the screen coordinates passed in as a Vector2D
	 * @return the world coordinate as a Vector2D
	 */
	public Vector2D screenToWorld(Vector2D s)
	{
		Point2D p = new Point2D.Double(s.getX(), s.getY());
		screenToWorldTransform.transform(p,p);
		return new Vector2D(p.getX(), p.getY());
	}
	
	/**
	 * Computes a screen coordinate from a world coordinate.
	 * @param w the world coordinates passed in as a Vector2D
	 * @return the screen coordinate as a Vector2D
	 */
	public Vector2D worldToScreen(Vector2D w)
	{
		Point2D p = new Point2D.Double(w.getX(), w.getY());
		worldToScreenTransform.transform(p,p);
		return new Vector2D(p.getX(), p.getY());
	}
		
	/**
	 * Converts a world location to a screen x location.
	 * 
	 * @param wx
	 * 			the world x location
	 * @param wy
	 * 			the world y location
	 * @return
	 * 			the screen x location of this tile
	 */
	public double worldToScreenX(double wx, double wy) {
		double[] worldToScreenMatrix = new double[6];
		worldToScreenTransform.getMatrix(worldToScreenMatrix);
		return ((wx * worldToScreenMatrix[0]) + 
				(wy * worldToScreenMatrix[2]) +
				worldToScreenMatrix[4]);
	}

	/**
	 * Converts a world location to a screen y location.
	 * 
	 * @param wx
	 * 			the world x location
	 * @param wy
	 * 			the world y location
	 * @return
	 * 			the screen y location of this tile
	 */
	public double worldToScreenY(double wx, double wy) {
		double[] worldToScreenMatrix = new double[6];
		worldToScreenTransform.getMatrix(worldToScreenMatrix);
		return ((wx * worldToScreenMatrix[1]) + 
				(wy * worldToScreenMatrix[3]) +
				worldToScreenMatrix[5]);
	}

	/**
	 * Updates each layer in the game.
	 * 
	 * @param deltaMs
	 *            the number of milliseconds that have passed since the last
	 *            iteration through the game loop.
	 */
	@Override
	public void update(final long deltaMs) {
		keyboard.poll();
		
		for (ViewableLayer v : gameObjectLayers) {
			v.update(deltaMs);
		}
		fre.update(deltaMs);

	}

	/**
	 * Renders all Viewable objects.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 */
	@Override
	public void render(final RenderingContext rc) {
		AffineTransform tr = rc.getTransform();
		rc.setTransform(worldToScreenTransform);
		for (ViewableLayer v : gameObjectLayers) {
			v.render(rc);
		}
		rc.setTransform(tr);
		super.render(rc);

	}

	/**
	 * Sets the scrolling boundary of the world. You cannot scroll beyond it.
	 * 
	 * @param x
	 *            the x position upper left corner of the world's bounding
	 *            rectangle
	 * @param y
	 *            the y position of the upper left corner of the world's
	 *            bounding rectangle
	 * @param width
	 *            the width of the world's bounding rectangle
	 * @param height
	 *            the height of the world's bounding rectangle
	 */
	public void setWorldBounds(final int x, final int y, final int width,
			final int height) {
		worldBounds = new Rectangle(x, y, width, height);
	}

	/**
	 * Sets the scrolling boundary of the world. 
	 * You cannot scroll beyond it.
	 * 
	 * @param bounds
	 *            the rectangular region of the world's 'interaction' zone
	 */
	public void setWorldBounds(final Rectangle bounds) {
		this.worldBounds = new Rectangle(bounds);
	}
	
	/**
	 * Returns the bounds of the world as a rectangle
	 * 
	 * @return bounds
	 *            the rectangular region of the world's 'interaction' zone
	 */
	public Rectangle getWorldBounds()
	{
		return worldBounds;
	}

	/**
	 * Will center the screen on a body in the game world.
	 * 
	 * @param b
	 *            The body to center the screen on.
	 */
	public void centerOn(final Body b) {
		centerOnPoint(b.getCenterPosition());
	}

	/**
	 * Will center the screen on a game world point.
	 * 
	 * @param p
	 *            a point in game coordinates.
	 */
	public void centerOnPoint(final Vector2D p) {
		matchPoints(p, getCenter());
	}

	/**
	 * Will center the screen on a game world point.
	 * 
	 * @param x
	 *            x in game coordinates.
	 * @param y
	 *            y in game coordinates.
	 */
	public void centerOnPoint(final int x, final int y) {
		centerOnPoint(new Vector2D(x, y));
	}

	/**
	 * Will sync a screen point with a game world point. If the game point lies
	 * outside of the world bounds it will clamp it to the boundary.
	 * 
	 * @param gamePoint
	 *            the target point (in world coordinates)
	 * @param screenPoint
	 *            the target point (in screen coordinates)
	 */
	private void matchPoints(final Vector2D gamePoint,
			final Vector2D screenPoint) {

		Vector2D n;
		if (worldBounds != null) {
			// clamp the game point to the world's 'interaction' zone
			n = gamePoint.clamp(worldBounds).difference(screenPoint);
		} else {
			// use the unadulterated game point
			n = gamePoint.difference(screenPoint);
		}
		worldToScreenTransform.setToTranslation(-n.getX(), -n.getY());
		try {
			screenToWorldTransform = worldToScreenTransform.createInverse();
		} catch (NoninvertibleTransformException e) {
			System.err.println("Scrolling Screen Game transform must be invertable");
			e.printStackTrace();
		}
	}

	/**
	 * Will return the center of the window, based on it's height and width.
	 * NOTE: This is in screen coordinates, not world coordinates
	 * 
	 * @return The center of the screen.
	 */
	public Vector2D getCenter() {
		return screenCenter;
	}
	
	public AffineTransform getScreenToWorldTransform()
	{
		return screenToWorldTransform;
	}
}
