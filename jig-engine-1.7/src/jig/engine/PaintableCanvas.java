package jig.engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The PaintableCanvas provides methods to programmatically create ImageResources. You can
 * think of this class like a canvas where you draw different shapes and text on it. You switch
 * between frames using the setWorkingFrame method. Once you are done creating your frames,
 * you use the loadFrames method to put them in the ResourceFactory. From then on you can
 * load them from the ResourceFactory with the resource name you gave them.
 * 
 * PaintableCanvas also provides helper methods to get some basic images created quickly.
 * Check out loadStandardFrameResource and loadDefaultFrames
 *  
 * @author James Van Boxtel
 *
 */
public class PaintableCanvas {
	
	/** This is a enum with different predefined shapes you can use to draw */
	public static enum JIGSHAPE { CIRCLE, RECTANGLE, DIAMOND, NORTH,
		EAST, SOUTH, WEST, HEART }
	
	protected BufferedImage[] b = null;
	
	/** The width of the canvas */
	protected int w = 0;
	/** The height of the canvas */
	protected int h = 0;
	/** The current frame being painted on */
	protected int workingFrame = 0;
	
	private static FontResource basicFont;


	/* Default frame colors */
	private static int shadesRequested = 0;
	private static final int NSHADES = 8;
	private static Color[] shadeColors;

	static {
		shadeColors = new Color[NSHADES];
		shadeColors[0] = new Color(255, 255, 128);
		shadeColors[1] = new Color(128, 255, 255);
		shadeColors[2] = new Color(255, 128, 255);
		shadeColors[3] = new Color(128, 255, 128);
		shadeColors[4] = new Color(255, 128, 128);
		shadeColors[5] = new Color(128, 128, 255);
		shadeColors[6] = new Color(255, 255, 255);
		shadeColors[7] = new Color(128, 128, 128);
		
		basicFont = ResourceFactory.getFactory().getFontResource(
				new Font("Sans Serif", Font.PLAIN, 10), Color.black, null);
	}
	
	/**
	 * Gets a color from a built in static pallette. You can use this to come
	 * up with a new default color.
	 * 
	 * @return a color, picked uniformly from a static distribution.
	 */
	private static Color nextShade() {
		shadesRequested++;
		shadesRequested %= NSHADES;

		return shadeColors[shadesRequested];
	}
	
	/**
	 * Creates a 'canvas' that can be drawn on programmatically.
	 * 
	 * @param w
	 *            the width of a frame
	 * @param h
	 *            the height of a frame
	 * @param frames
	 *            the number of frames
	 * @param backgroundColor
	 *            the background color for the image, if null, the image will
	 *            have a transparent background
	 */
	public PaintableCanvas(final int w, final int h, final int frames, final Color backgroundColor) {

		b = new BufferedImage[frames];
		this.w = w;
		this.h = h;

		for (int i = 0; i < frames; i++) {
			b[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) b[i].getGraphics();

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
					0.0f));
			g.fillRect(0, 0, w - 1, h - 1);
			g.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC, 1.0f));
			
			if(backgroundColor != null)
			{
				g.setColor(backgroundColor);
				g.fillRect(0, 0, w - 1, h - 1);
			}
			
			g.dispose();
		}
	}
	
	/**
	 * Creates a PaintableCanvas from a previous resource already loaded into
	 * the ResourceFactory.
	 * 
	 * This canvas will have the same height and width as the resource and
	 * have the frames loaded into the canvas.
	 * 
	 * @param rscName the name of the resource in the ResourceFactory
	 */
	public PaintableCanvas(final String rscName)
	{
		List<ImageResource> cacheHit = ResourceFactory.getFactory().getFrames(rscName);
		if(cacheHit == null)
		{
			throw new IllegalArgumentException("The resource '"+rscName+"' does not exist or hasn't been loaded" );
		}
		
		loadFromFrames(cacheHit, AffineTransform.getTranslateInstance(0, 0));
	}
	
	/**
	 * Creates a PaintableCanvas from a previous list of frames.
	 * 
	 * This canvas will have the same height and width as the frames.
	 * It will also have the frames loaded in.
	 * 
	 * @param oldFrames
	 */
	public PaintableCanvas(final List<ImageResource> oldFrames)
	{
		loadFromFrames(oldFrames, AffineTransform.getTranslateInstance(0, 0));		
	}
	
	/**
	 * Creates a PainableCanvas from a previous list of frames after
	 * applying a specified AffineTransform.
	 * 
	 * Since the size of the frames created is the same as the size
	 * of the source frames, this operation probably makes most 
	 * sense when used to rotate the image or to mirror it.
	 * 
	 * @param oldFrames
	 * @param at
	 */
	public PaintableCanvas(final List<ImageResource> oldFrames, AffineTransform at) {
		loadFromFrames(oldFrames, at);
	}
	
	/**
	 * Loads the current frames into the PaintableCanvas.
	 * 
	 * @param oldFrames
	 */
	private void loadFromFrames(final List<ImageResource> oldFrames, AffineTransform at)
	{
		b = new BufferedImage[oldFrames.size()];

		for (int i = 0; i < oldFrames.size(); i++)
		{
			ImageResource resource = oldFrames.get(i);
		
			b[i] = new BufferedImage(resource.getWidth(), resource.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) b[i].getGraphics();

			resource.draw(g, at);
		}
	}
	
	/**
	 * Draws the given image at the given location on the canvas.
	 * 
	 * @param resource
	 * @param x
	 * @param y
	 */
	public void drawImage(final ImageResource resource, final int x, final int y)
	{		
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();
		resource.draw(g, AffineTransform.getTranslateInstance(x, y));
		g.dispose();
	}
	
	/**
	 * Draws the given image at the given location on the canvas.
	 * 
	 * @param resource
	 * @param x
	 * @param y
	 */
	public void drawImage(final ImageResource resource, final AffineTransform at)
	{		
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();
		resource.draw(g, at);
		g.dispose();
	}
	
	/**
	 * Sets the current frame that you are painting on. For example, if you want
	 * to start drawing on the second frame, you would use this method with an argument
	 * of '1'.
	 * 
	 * @param frame the frame you want to start painting on
	 */
	public void setWorkingFrame(int frame)
	{
		workingFrame = frame;
	}
		
	/**
	 * Returns one of the predefined shapes built into JIG as a
	 * Area object.
	 * 
	 * @param shapeCode one of the JIGSHAPE types
	 * @return a new Area object that represents the JIGSHAPE
	 */
	public Area getJIGShape(final JIGSHAPE shape)
	{
		Area a;
		Shape s;
		Polygon p;

		switch (shape) {
		case CIRCLE:
			s = new Ellipse2D.Float(0, 0, w - 1, h - 1);
			a = new Area(s);
			break;
		case EAST:
			p = new Polygon();
			p.addPoint(0, 0);
			p.addPoint(w, h / 2);
			p.addPoint(0, h - 1);
			a = new Area(p);
			break;
		case NORTH:
			p = new Polygon();
			p.addPoint(0, h - 1);
			p.addPoint(w / 2, 0);
			p.addPoint(w - 1, h - 1);
			a = new Area(p);
			break;
		case SOUTH:
			p = new Polygon();
			p.addPoint(0, 0);
			p.addPoint(w / 2, h - 1);
			p.addPoint(w - 1, 0);
			a = new Area(p);
			break;
		case WEST:
			p = new Polygon();
			p.addPoint(0, h / 2);
			p.addPoint(w - 1, 0);
			p.addPoint(w - 1, h - 1);
			a = new Area(p);
			break;
		case DIAMOND:
			p = new Polygon();
			p.addPoint(    0, h / 2);
			p.addPoint(w / 2, h - 1);
			p.addPoint(w - 1, h / 2);
			p.addPoint(w / 2, 0);
			a = new Area(p);
			break;
		case HEART:
			//Made with diamond and two circles
			//http://www.mathematische-basteleien.de/heart.htm
			p = new Polygon();
			int amount = w / 8 + 1;
			int half = w / 2;
			p.addPoint(half, amount+1); //top
			p.addPoint(w-amount+1, half-1); //right
			p.addPoint(half, h-amount); //bottom
			p.addPoint(amount-1, half-1); // left
			a = new Area(p);
			a.add(new Area(new Ellipse2D.Float(0, 0, w / 2, h / 2)));
			a.add(new Area(new Ellipse2D.Float(w / 2, 0, w / 2, h / 2)));
			break;
		case RECTANGLE:
			s = new Rectangle2D.Float(0, 0, w - 1, h - 1);
			a = new Area(s);
			break;
		default:
			throw new IllegalArgumentException("That is not a valid JIGSHAPE");
		}
		
		return a;
	}
	
	/**
	 * Gets the given JIGSHAPE as an area and then draws it to the current
	 * frame.
	 * 
	 * @param shape
	 * @param color
	 */
	private void drawJIGShape(final JIGSHAPE shape, Color color)
	{
		Shape s = getJIGShape(shape);
		
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.draw(s);
		
		g.dispose();
	}
	
	/**
	 * Gets the given JIGSHAPE as an area and then fills the current frame
	 * with it.
	 * 
	 * @param shape
	 * @param color
	 */
	private void fillJIGShape(final JIGSHAPE shape, Color fillColor)
	{
		Shape s = getJIGShape(shape);
		
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(fillColor);
		g.fill(s);
		
		g.dispose();
	}
	
	/**
	 * TODO: May not be needed...
	 * 
	 * @param shape
	 * @param color
	 */
	@SuppressWarnings("unused")
	private void drawShape(final Shape shape, Color color)
	{		
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.draw(shape);
		
		g.dispose();
	}
	
	/**
	 * TODO: May not be needed...
	 * 
	 * @param shape
	 * @param color
	 */
	@SuppressWarnings("unused")
	private void fillShape(final Shape shape, Color fillColor)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(fillColor);
		g.fill(shape);
		
		g.dispose();
	}
	
	/**
	 * Draws an Area object on the canvas.
	 * 
	 * @param shape
	 * @param color
	 */
	public void drawArea(final Area area, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.draw(area);
		
		g.dispose();
	}
	
	/**
	 * Fills an Area object on the canvas.
	 * 
	 * @param shape
	 * @param color
	 */
	public void fillArea(final Area area, Color fillColor)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(fillColor);
		g.fill(area);
		
		g.dispose();
	}
	
	/**
	 * Draws a rectangle in the given location with the given color.
	 * The rectangle is not filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the color of the rectangle
	 */
	public void drawRectangle(final int x, final int y, final int width, final int height, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.drawRect(x, y, width, height);
		
		g.dispose();
	}
	
	/**
	 * Draws a rectangle in the given location with the given color.
	 * The rectangle is filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the color of the rectangle
	 */
	public void fillRectangle(final int x, final int y, final int width, final int height, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.fillRect(x, y, width, height);
		
		g.dispose();
	}


	/**
	 * Draws the given text on the canvas in the given location. The text is
	 * centered at the given location.
	 * 
	 * @param string the string to draw
	 * @param color the color to draw it in
	 * @param x the x location in pixels the text should be centered at
	 * @param y the y location in pixels the text should be centered at
	 */
	public void drawText(final String string, FontResource font, int x, int y)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();
		
		int width = font.getStringWidth(string);
		int height = font.getHeight();
		
		double xOffset = (w - width) / 2.0 + x;
		double yOffset = (h - height) / 2.0 + y;
		font.draw(string, g, AffineTransform.getTranslateInstance(xOffset, yOffset));
	}
	
	/**
	 * Draws a oval (ellipse) in the given location with the given color.
	 * The oval is not filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the oval
	 * @param height the height of the oval
	 * @param color the color of the oval
	 */
	public void drawOval(final int x, final int y, final int width, final int height, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.drawOval(x, y, width, height);
		
		g.dispose();
	}
	
	/**
	 * Draws a oval (ellipse) in the given location with the given color.
	 * The oval is filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the oval
	 * @param height the height of the oval
	 * @param color the color of the oval
	 */
	public void fillOval(final int x, final int y, final int width, final int height, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.fillOval(x, y, width, height);
		
		g.dispose();
	}
	
	/**
	 * Draws a pixel in the given location.
	 * 
	 * @param x the x location in pixels
	 * @param y the y location in pixels
	 * @param color the color of the pixel
	 */
	public void drawPixel(final int x, final int y, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.drawRect(x, y, 0, 0);
		
		g.dispose();
	}
	
	/**
	 * Draws a line on the canvas with the given start and end locations.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color the color of the line to be drawn
	 */
	public void drawLine(final int x1, final int y1, final int x2, final int y2, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
		
		g.dispose();
	}

	/**
	 * Draws a rectangle with a rounded border.
	 * The rectangle is not filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param arcWidth the width of the arc in pixels
	 * @param arcHeight the height of the arc in pixels
	 * @param color the color of the rectangle
	 */
	public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		
		g.dispose();
	}
	
	/**
	 * Draws a rectangle with a rounded border.
	 * The rectangle is filled in.
	 * 
	 * @param x the x location of the top left corner
	 * @param y the y location of the top left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param arcWidth the width of the arc in pixels
	 * @param arcHeight the height of the arc in pixels
	 * @param color the color of the rectangle
	 */
	public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight, Color color)
	{
		Graphics2D g = (Graphics2D) b[workingFrame].getGraphics();

		g.setColor(color);
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		
		g.dispose();
	}
	
	/**
	 * This method loads the frames of this PaintableCanvas into the ResourceFactory
	 * with the given resource name.
	 * 
	 * After doing this, you can get the frames from the ResourceFactory using the
	 * getFrames method with the resource name you used.
	 * 
	 * @param rscName the name you want to associate with these frames
	 * @return if the frames were successfully loaded
	 */
	public boolean loadFrames(final String rscName)
	{
		ResourceFactory.getFactory().putFrames(rscName, b);
		return ResourceFactory.getFactory().areFramesLoaded(rscName);
	}


	/**
	 * 
	 * 
	 * This is a helper method that simply creates a frame resource with the
	 * following frames:
	 * <ul>
	 * <li> Frame 0: an outline of the shape. </li>
	 * <li> Frame 1: a filled shape with a black outline.</li>
	 * <li> Frame 2: a filled shape with no outline.</li>
	 * </ul>
	 *  
	 * @param w
	 *            the width of a frame
	 * @param h
	 *            the height of a frame
	 * @param shape
	 *            the shape of the object. See getJIGshape
	 * @param preferredColor
	 *            the preferredColor for the outline frame, and for the filled
	 *            frame. If null is given, a default color will be chosen.
	 * @return the resource name of the loaded frames
	 */
	public static String loadStandardFrameResource(final int w,
			final int h, final JIGSHAPE shape, final Color preferredColor) {


		String colorString = "-default";
		if(preferredColor != null)
			colorString = "-" + preferredColor.getRed()
			+ "-" + preferredColor.getGreen()
			+ "-" + preferredColor.getBlue();
		
		String rscName = "[jig.engine.stdresource]-" + w + "x" + h + "-"
							+ shape 
							+ colorString;
		
		if (ResourceFactory.getFactory().areFramesLoaded(rscName)) {
			return rscName;
		}
		
		final int nframes = 3;
		
		Color color = preferredColor;
		if(color == null)
			color = nextShade();
		
		PaintableCanvas p = new PaintableCanvas(w, h, nframes, null);

		for (int i = 0; i < nframes; i++) {
			p.setWorkingFrame(i);
			
			switch (i) {
			case 0:
				p.drawJIGShape(shape, color);
				break;
			case 1:
				p.fillJIGShape(shape, color);
				p.drawJIGShape(shape, Color.black);
				break;
			default:
				p.fillJIGShape(shape, color);
				p.drawJIGShape(shape, color);
			}
		}
		p.loadFrames(rscName);
		return rscName;
	}
	
	/**
	 * Loads some default frames for a resource name until the actual
	 * sprites can be created.
	 * 
	 * The current implementation draws a colored shape with a black border
	 * and the current frame number in the middle.
	 * 
	 * @param rscName the name you want associated with these frames
	 * @param w the width in pixels
	 * @param h the height in pixels
	 * @param numberFrames the total number of frames
	 * @param shapeCode the shape of the object. One of: <code>rcdhnsew</code>
	 * @param preferredColor the color you want the frames filled with
	 * 		this can be null if you want a default color chosen for you
	 * 
	 * TODO: DESIGN:  ShapeCode becomes an Enum
	 */
	public static void loadDefaultFrames(final String rscName, final int w,
			final int h, final int numberFrames, final JIGSHAPE shapeCode, final Color preferredColor) {

		
		if (ResourceFactory.getFactory().areFramesLoaded(rscName) == true) {
			// already have that resource!
			ResourceFactory.getJIGLogger().info("Resources " + rscName + " is already loaded...");
			return;
		}
				
		Color shade = preferredColor;
		Color textColor = Color.black;
		
		if(shade == null)
		{
			shade = nextShade();
		}
		else if(shade.equals(textColor))
		{
			textColor = Color.white;
		}

		PaintableCanvas p = new PaintableCanvas(w, h, numberFrames, null);
		
		for (int i = 0; i < numberFrames; i++) {

			p.setWorkingFrame(i);

			// glyph bias locations
			int gbx = 0;
			int gby = 0;
			
			switch (shapeCode) {
			case EAST:
				gbx = -w / 5;
				break;
			case NORTH:
				gby = -h / 5;
				break;
			case WEST:
				gbx = w / 5;
				break;
			default:
				break;
			}

			// Draw the shape and the border
			p.fillJIGShape(shapeCode, shade);
			p.drawJIGShape(shapeCode, Color.black);
			
			p.drawText(Integer.toString(i), basicFont, gbx, gby);

		}

		p.loadFrames(rscName);
	}
}
