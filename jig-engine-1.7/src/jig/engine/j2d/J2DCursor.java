package jig.engine.j2d;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.List;

import jig.engine.CursorResource;
import jig.engine.ImageResource;
import jig.engine.util.Vector2D;

/**
 * This class represent an animated cursor object. Java AWT does not support
 * animated cursors by default, so this class is more complex than its OpenGL
 * counterpart.
 * 
 * @author Aaron Mills
 * 
 */
class J2DCursor implements CursorResource {
	private final Cursor[] cursor;
	private final long delay;
	private Vector2D hotSpot;
	private String name;
	
	/**
	 * Creates a cursor resource from a list of ImageResources. 
	 * @param frames the sequence of images to animate through
	 * @param name the name of the cursor resource
	 * @param hotspot the location of the cursor's hotspot
	 * @param delay the time delay between frames
	 * 
	 */
	J2DCursor(final List<ImageResource> frames, final String name,
			final Vector2D hotspot, final long delay) {

		int i = 0;

		this.delay = delay;
		this.hotSpot = new Vector2D((int) hotspot.getX(), (int) hotspot.getY());
		this.name = name;
		
		
		cursor = new Cursor[frames.size()];
		Image[] ia = new Image[frames.size()];
		for (ImageResource ir : frames) {
			ia[i] = ((J2DImage) ir).image;
			i++;
		}
	
		Point pHot = new Point((int) hotspot.getX(), (int) hotspot.getY());
		Toolkit tk = Toolkit.getDefaultToolkit();
		for (i = 0; i < ia.length; i++) {
			Cursor c = tk.createCustomCursor(ia[i], pHot, name);
			cursor[i] = c;
		}
		
	}

	/**
	 * Returns the delay between frames.
	 * 
	 * @return delay
	 */
	public long getAnimationDelay() {
		return delay;
	}

	/**
	 * 
	 * @return the location of the cursor's hotspot.
	 */
	public Vector2D getHotSpot() {
		return hotSpot;
	}
	/**
	 * Returns the array of cursor objects.
	 * 
	 * @return cursor array
	 */
	Cursor[] getCursor() {
		return cursor;
	}
	
	/**
	 * 
	 * @return the cursor's name.
	 */
	public String getName() {
		return name;
	}
}
