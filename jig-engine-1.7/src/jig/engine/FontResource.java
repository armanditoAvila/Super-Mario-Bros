package jig.engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * A font usable by the JIG engine.
 * 
 * 
 * @author Scott Wallace
 *
 */
public interface FontResource {

	/**
	 * Renders the string on the specified context using
	 * the desired AffineTransform.
	 *
	 * @param s the string to render
	 * @param rc the context
	 * @param at the affine transform to apply
	 */
	void render(String s, RenderingContext rc, AffineTransform at);

	/**
	 * Draws the font resource onto a graphics context, for example
	 * to draw text onto a image. 
	 * 
	 * @param g the graphics context of the time to be drawn onto
	 * @param at an AffineTransform specifying how/where to draw
	 * 
	 */
	void draw(String s, Graphics2D g, AffineTransform at); 
	
	/**
	 * @return the height (i.e., max ascent plus max descent) of the font.
	 *
	 */
	int getHeight();
	
	/**
	 * @return the font's baseline position.
	 */
	int getBaseline();
	
	/**
	 * 
	 * @param character
	 * @return the width of a character
	 */
	public int getCharWidth(char character);
	
	/**
	 * 
	 * @param s
	 * @return the width of the given string (which may be different than the sum of characters)
	 */
	public int getStringWidth(final String s);
}
