package jig.engine.hli;

import java.util.LinkedList;
import java.util.List;

import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;

/**
 * A helper class to support a simple static screen game (i.e., a game where the
 * world size is equal to the size of the game frame's viewable surface).
 * Classic games such as Joust, Asteroids, and Space Invaders are all static
 * screen games.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public class StaticScreenGame extends AbstractSimpleGame {

	/**
	 * A list of ViewableLayers where lower index indicates that greater depth
	 * along the Z-axis.
	 */
	protected List<ViewableLayer> gameObjectLayers;

	/**
	 * Creates a new static screen game.
	 * 
	 * @param desiredWidth
	 *            the desired width of the game's drawing surface
	 * @param desiredHeight
	 *            the desired height of the game's drawing surface
	 * @param preferFullscreen
	 *            <code>true</code> if a fullscreen exclusive presentation is
	 *            preferred
	 */
	public StaticScreenGame(final int desiredWidth, final int desiredHeight,
			final boolean preferFullscreen) {

		super(desiredWidth, desiredHeight, preferFullscreen);

		gameObjectLayers = new LinkedList<ViewableLayer>();
	}

	/**
	 * Updates each viewable layer (the <code>gameObjectsLayers</code>).
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
	 * Renders the <code>gameObjectLayers</code> from bottom to top.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 */
	@Override
	public void render(final RenderingContext rc) {

		for (ViewableLayer v : gameObjectLayers) {
			v.render(rc);
		}
		super.render(rc);

	}

}
