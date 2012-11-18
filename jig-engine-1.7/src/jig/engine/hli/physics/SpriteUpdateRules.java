package jig.engine.hli.physics;

import jig.engine.Sprite;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

/**
 * A simple set of reusable rules to help govern the behavior of sprites and
 * other moving objects within a fixed-size world...
 * 
 * @author Scott Wallace
 * 
 */
public final class SpriteUpdateRules {

	public static final int SIDE_NORTH = 0x1000;
	public static final int SIDE_EAST = 0x2000;
	public static final int SIDE_SOUTH = 0x4000;
	public static final int SIDE_WEST = 0x8000;
	public static final int SIDE_NSEW = SIDE_NORTH | SIDE_SOUTH | SIDE_EAST
			| SIDE_WEST;
	private int width;
	private int height;

	/**
	 * Creates a new set of update rules for a specific sized world/screen.
	 * 
	 * @param w
	 *            the width of the world
	 * @param h
	 *            the height of the world
	 */
	public SpriteUpdateRules(final int w, final int h) {
		width = w;
		height = h;
	}

	/**
	 * 
	 * Determines if any part of the specified body is "onscreen" (e.g., within
	 * the a box of the specified height and width).
	 * 
	 * @param s
	 *            the sprite/body
	 * @return <code>true</code> iff the sprite is visible on the screen.
	 */
	public boolean isOnscreen(final Sprite s) {
		Vector2D p = s.getPosition();
		if (p.getX() > width || p.getX() < -s.getWidth() || p.getY() > height
				|| p.getY() < -s.getHeight()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * Determines if all of the specified sprite is "onscreen".
	 * 
	 * @param s
	 *            the sprite/body
	 * @return <code>true</code> iff the sprite is entirely visible on the
	 *         screen.
	 * 
	 */
	public boolean isFullyOnscreen(final Sprite s) {
		Vector2D pos = s.getPosition();

		if (pos.getX() < 0) {
			return false;
		}
		if (pos.getY() < 0) {
			return false;
		}
		if (pos.getX() + s.getWidth() > width) {
			return false;
		}
		if (pos.getY() + s.getHeight() > height) {
			return false;
		}

		return true;
	}

	/**
	 * Wraps a body around the edges of the world.
	 * 
	 * @param s
	 *            the body upon which to act
	 * @param sides
	 *            a bitwise-OR of SIDES_XXX values indicating the sides that
	 *            will exhibit wrapping behavior.
	 * 
	 * @return a SIDE_XXX flag indicating the side in which the body escaped or
	 *         tried to escape, or 0 if no event occurred.
	 * 
	 */
	public int wrap(final Body s, final int sides) {
		Vector2D pos = s.getPosition();
		double px = pos.getX();
		double py = pos.getY();
		int event = 0;
		boolean pChange = false;

		if (px < -s.getWidth()) {
			if ((sides & SIDE_WEST) != 0) {
				px = width;
				pChange = true;
			}
			event = SIDE_WEST;
		} else if (px > width) {
			if ((sides & SIDE_EAST) != 0) {
				px = -s.getWidth();
				pChange = true;
			}
			event = SIDE_EAST;
		}

		if (py < -s.getHeight()) {
			if ((sides & SIDE_NORTH) != 0) {
				py = height;
				pChange = true;
			}
			event = SIDE_NORTH;

		} else if (py > height) {
			if ((sides & SIDE_SOUTH) != 0) {
				py = -s.getHeight();
				pChange = true;
			}
			event = SIDE_SOUTH;
		}
		if (pChange) {
			s.setPosition(new Vector2D(px, py));
		}
		return event;

	}

	/**
	 * Wraps or Reflects a body off the edges of the world.
	 * 
	 * @param s
	 *            the body upon which to act
	 * @param sidesToWrap
	 *            a bitwise-OR of SIDES_XXX values indicating the sides that
	 *            will exhibit wrapping behavior (others will exhibit reflecting
	 *            behavior)
	 * 
	 * @return a SIDE_XXX flag indicating the side with which the event occurred
	 *         or 0, if no event occurred.
	 * 
	 */
	public int wrapOrReflect(final Body s, final int sidesToWrap) {

		Vector2D pos = s.getPosition();
		Vector2D vel = s.getVelocity();
		double px = pos.getX();
		double py = pos.getY();
		double vx = vel.getX();
		double vy = vel.getY();
		int event = 0;

		if (px < 0) {
			if ((sidesToWrap & SIDE_WEST) == 0) {
				// reflect
				px = 0;
				vx = -vx;
				event = SIDE_WEST;
			} else if (px < -s.getWidth()) {
				// wrap
				px = width;
				event = SIDE_WEST;
			}
		} else if (px > width - s.getWidth()) {
			if ((sidesToWrap & SIDE_EAST) == 0) {
				// reflect
				px = width - s.getWidth();
				vx = -vx;
				event = SIDE_EAST;
			} else if (px > width) {
				// wrap
				px = -s.getWidth();
				event = SIDE_EAST;
			}
		}
		if (py < 0) {
			if ((sidesToWrap & SIDE_NORTH) == 0) {
				// reflect
				py = 0;
				vy = -vy;
				event = SIDE_NORTH;
			} else if (py < -s.getHeight()) {
				// wrap
				py = height;
				event = SIDE_NORTH;
			}
		} else if (py > height - s.getHeight()) {
			if ((sidesToWrap & SIDE_SOUTH) == 0) {
				// reflect
				py = height - s.getHeight();
				vy = -vy;
				event = SIDE_SOUTH;
			} else if (py > height) {
				// wrap
				py = -s.getHeight();
				event = SIDE_SOUTH;
			}
		}
		if (event != 0) {
			s.setVelocity(new Vector2D(vx, vy));
			s.setPosition(new Vector2D(px, py));
		}
		return event;
	}
}
