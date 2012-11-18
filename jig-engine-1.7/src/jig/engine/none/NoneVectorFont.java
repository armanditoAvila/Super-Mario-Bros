package jig.engine.none;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;

/**
 * A stub font class for the non drawing resource factory.
 * 
 * @author Scott Wallace
 *
 */
class NoneVectorFont implements FontResource {
	
	
	/**
	 * {@inheritDoc}
	 */
	NoneVectorFont(final Font f) {
		ResourceFactory.getJIGLogger().warning("System fonts from the None Factory may not render...");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void render(final String s, final RenderingContext rc,
			final AffineTransform at) {
		}
	
	/**
	 * {@inheritDoc}
	 */
	public void draw(String s, Graphics2D g, AffineTransform at) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getBaseline() {
		return 0;	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getCharWidth(char character) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStringWidth(String s) {
		return 0;
	}
}
