package jig.engine;

import java.awt.geom.AffineTransform;

/**
 * 
 * An object that exists for the duration required to draw a single frame.
 * Typically, this is also a single iteration of the game loop. Between two
 * frames, the rendering context may become invalid (i.e., it may be out of
 * date) or its internal characteristics may change. Typically, this won't
 * matter to the user, as the RenderingContext will be just be passed around and
 * not manipulated directly.
 * 
 * The implementation details of the RenderingContext are specified by the
 * graphics backend.
 * 
 * @author Scott Wallace
 * 
 */
public interface RenderingContext {

	/**
	 * Composes an affine transform with the current global transform
	 * according to the rule last-specified-first-applied. The new
	 * global transform is applied to all visible objects.
	 * 
	 * @param at the transform to be composed with the current transform
	 */
	void transform(AffineTransform at);

	/**
	 * Sets the global transform used to render all visible objects 
	 * to the game frame's drawing surface.
	 * 
	 * @param at the new affine transform
	 */
	void setTransform(AffineTransform at);

	/**
	 * 
	 * @return the current global transform that is applied to all 
	 * visible objects
	 */
	AffineTransform getTransform();

}

