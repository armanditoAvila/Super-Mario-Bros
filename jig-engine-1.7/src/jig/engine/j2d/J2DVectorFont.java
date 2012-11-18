package jig.engine.j2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import jig.engine.FontResource;
import jig.engine.RenderingContext;

/**
 * A Vector Font class for fast drawing of smooth font images
 * onto a Java 2D game frame.
 *  
 * @author Scott Wallace
 *
 */
class J2DVectorFont implements FontResource {
	
	Font javaFont;
	Color background;
	Color foreground;
	Rectangle2D maxCharacterBounds;

	/** Used to measure font widths 
	 * See the following web pages for more details
	 * http://java.sun.com/javase/6/docs/api/java/awt/FontMetrics.html
	 * http://www.leepoint.net/notes-java/GUI-appearance/fonts/15fontmetrics.html
	 */
	FontMetrics javaFontMetrics;
	
	/**
	 * A FontResource wrapper for Java AWT fonts.
	 * 
	 * @param f the Java font
	 * 
	 * @param gf the game frame that this font will be drawn on
	 */
	J2DVectorFont(final Font f, final J2DGameFrame gf, Color fontColor, Color backgroundColor) {
		javaFont = f;
		background = backgroundColor;
		foreground = fontColor;
		Graphics2D g2 = (Graphics2D) gf.bufferStrategy.getDrawGraphics();
		javaFontMetrics = g2.getFontMetrics(f);
		maxCharacterBounds = f.getMaxCharBounds(g2.getFontRenderContext());
	}
	
	/**
	 * Renders the font on the game frame.
	 *
	 * @param s the string to render
	 * @param rc the game frame's rendering context
	 * @param at the affine transform to apply to the string glyphs
	 * prior to rendering.
	 * 
	 */
	public void render(final String s, final RenderingContext rc,
			final AffineTransform at) {
		J2DGameFrame.J2DRenderingContext j2drc;
		
		j2drc = (J2DGameFrame.J2DRenderingContext) rc;
		Graphics2D g = j2drc.theG;
		
		draw(s, g, at);
	}
	
	public void draw(String s, Graphics2D g, AffineTransform at) {
		
		
		/* Turn on anti-aliasing? */
		//g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		//		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		GlyphVector gv = javaFont.createGlyphVector(
						g.getFontRenderContext(), s);
		
		Rectangle2D bounds = gv.getLogicalBounds();
		
		

		//                |       | (min Y)
		//                |W     W| 
		//                | W W W |
		//  baseline -----|--W-W--|--------
		//                |       | (max Y)
		
		// The logical bounds are used to layout a glyph.
		// The bounds extend up from the base line to the height of what
		// would be the previous line (this is the height of the tallest glyph
		// plus the max descent, plus probably a bit of padding). The bounds
		// extend below the baseline by the maximum descent of a character.

		AffineTransform originalTransform = g.getTransform();
		AffineTransform textTransform = AffineTransform.getTranslateInstance(
				0, -bounds.getMinY());
		
		textTransform.preConcatenate(at);
		
		g.transform(textTransform);

		if(background != null)
		{
			g.setColor(background);
			g.fillRect(0, -getBaseline(), (int)bounds.getWidth(), (int)bounds.getHeight());
		}
		
		g.setColor(foreground);
		
		g.drawGlyphVector(gv, 0, 0);
		g.setTransform(originalTransform);
		
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		
		return (int) maxCharacterBounds.getHeight();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getBaseline() {
		return (int) -maxCharacterBounds.getMinY();	
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCharWidth(char character) {
		return javaFontMetrics.charWidth(character);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getStringWidth(final String s) {
		return javaFontMetrics.stringWidth(s);
	}
}
