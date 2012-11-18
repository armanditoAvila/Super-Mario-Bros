package jig.engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Provide an Image resource that can be queried for basic information
 * and rendered.  The underlying data representing the image should
 * be hidden and immutable.
 * 
 * @author Scott Wallace
 *
 */
public interface ImageResource {

	/**
	 * @return the height of the image encapsulated by this resource
	 */
	int getHeight();

	/**
	 * @return the width of the image encapsulated by this resource
	 */
	int getWidth();

	/**
	 * Renders the image using the specified context and transform.
	 *
	 * @param rc	The context upon which to render this image
	 * @param at	The transform which should be applied to the image 
	 * 				before rendering
	 */
	void render(RenderingContext rc, AffineTransform at);
	
	/**
	 * Draws the image resource onto a graphics context, for example
	 * to create a new image resource that is based on this one. 
	 * @param g
	 * @param x
	 * @param y
	 * 
	 * TODO: Implemetation is incomplete! needs to be vetted especially in LWJGL
	 */
	void draw(Graphics2D g, AffineTransform at);
}
