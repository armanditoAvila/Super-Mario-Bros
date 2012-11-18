package jig.engine.util;

/**
 * A mutable accessory class to go with <code>Vector2D</code>. 
 * 
 * Like <code>Vector2D</code> this class represents a 2 dimensional vector.
 * In contrast to <code>Vector2D</code> this class provides public
 * access directly to the coordinates and operations that directly manipulate
 * these coordinates.  The end result can be faster operations when signifncant
 * chaining is required at the expense of overall safety.
 * 
 * Methods with a <code>Me</code> suffix directly manipulate the pubicaly
 * visible coordinate values. Methods with a <code>get</code> prefix return
 * a calculation based on the current coordinates.
 * 
 * @author Scott Wallace
 *
 */
public final class VectorOpsBuffer {

	/** The x coordinate of the 2D Vector. */
	public double x;
	/** The y coordinate of the 2D Vector. */
	public double y;
	
	/**
	 * Creates a new <code>VectorOpsBuffer</code> given the specified 
	 * coordinates.
	 * 
	 * @param xcoord the x coordinate of the vector
	 * @param ycoord the y coordinate of the vector
	 */
	public VectorOpsBuffer(final double xcoord, final double ycoord) {
		x = xcoord;
		y = ycoord;
	}
	
	/**
	 * Creates a new <code>VectorOpsBuffer</code> based on the supplied
	 * immutable <code>Vector2D</code> instance.
	 * 
	 * @param v the vector containing the desired x and y coordinates
	 */
	public VectorOpsBuffer(final Vector2D v) {
		x = v.getX();
		y = v.getY();
	}
	
	/**
	 * Translates the current coordinates based on the specified vector.
	 * 
	 * @param v specifies the direction to translate
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer translateMe(final Vector2D v) {
		x += v.getX();
		y += v.getY();
		return this;
	}

	/**
	 * Translates the current coordinates based on the specified vector.
	 * 
	 * @param v specifies the direction to translate
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer translateMe(final VectorOpsBuffer v) {
		x += v.x;
		y += v.y;
		return this;
	}
	

	/**
	 * Sets this vector to the difference of it and the specified vector.
	 * 
	 * @param v the coordinates to subtract from this vector's coordinates
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer differenceMe(final Vector2D v) {
		x -= v.getX();
		y -= v.getY();
		return this;
	}

	/**
	 * Sets this vector to the difference of it and the specified vector.
	 * 
	 * @param v the coordinates to subtract from this vector's coordinates
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer differenceMe(final VectorOpsBuffer v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/**
	 * Scales this vector's coordinates by the specified amount.
	 * 
	 * @param s the scaling factor
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer scaleMe(final double s) {
		x *= s;
		y *= s;
		return this;
	}
	
	/**
	 * Sets each coordinate to its absolute value.
	 * 
	 * @return this object (so manipulations can be chained)
	 */
	public VectorOpsBuffer absMe() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	
	/**
	 * Gets the squared distance to the specified point.
	 * 
	 * @param v the target point
	 * @return the squared distance
	 */
	public double getDistance2(final Vector2D v) {
		return ((x - v.getX()) * (x - v.getX()) 
				+ (y - v.getY()) * (y - v.getY()));
	}

	/**
	 * Gets the squared distance to the specified point.
	 * 
	 * @param v the target point
	 * @return the squared distance
	 */
	public double getDistance2(final VectorOpsBuffer v) {
		return (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y);
	}
	
	/**
	 * Gets the dot product of this vector with the vector specified.
	 * 
	 * @param b the other vector in this dot product operation
	 * @return the dot product
	 */
	public double getDot(final Vector2D b) {
		return x * b.getX() + y * b.getY();
	}
	
	/**
	 * Gets the squared magnitude of this vector.
	 * 
	 * @return the squared magnitude
	 */
	public double getMagnitude2() {
		return x * x + y * y;
	}
	
	/**
	 * Gets an immutable <code>Vector2D</code> with the same coordinates
	 * as contained in this vector.
	 * 
	 * @return a <code>Vector2D</code> representation of the coordinates
	 */
	public Vector2D toVector2D() { 
		return new Vector2D(x, y);
	}
}
