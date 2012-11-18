package jig.engine.util;

import java.awt.Rectangle;

/**
 * An immutable 2-dimensional vector.
 * 
 * 
 * @author Scott Wallace
 * 
 */
public final class Vector2D implements Cloneable {

	public static final Vector2D ZERO = new Vector2D(0,0);
	public static final Vector2D ONE = new Vector2D(1,1);
	
	/** DESIGN: only for testing, this should be removed prior to release. */
	public static long nCreated = 0;

	/** The x coordinate of the Vector. */
	private final double x;

	/** The y coordinate of the Vector. */
	private final double y;

	/**
	 * Creates a new 2D Vector with random coordinates in the specified range.
	 * 
	 * @param lx
	 *            the lower bound on the x coordinate (inclusive).
	 * @param hx
	 *            the upper bound on the x coordinate (exclusive).
	 * @param ly
	 *            the lower bound on the y coordinate (inclusive).
	 * @param hy
	 *            the upper bound on the y coordinate (exclusive).
	 * @return a new 2D Vector that is randomly initialized
	 */
	public static Vector2D getRandomXY(final double lx, final double hx,
			final double ly, final double hy) {
		return new Vector2D(lx + (Math.random() * (hx - lx)), ly
				+ (Math.random() * (hy - ly)));
	}


	/**
	 * Creates a new vector with magnitude 1 that is oriented at the specified
	 * angle.
	 * 
	 * <pre>
	 *              (0,-1) 
	 *              -PI/2
	 *                 &circ;
	 *                 |
	 * (-1,0) +-PI &lt;-- + --&gt; 0 (1,0) 
	 *                 |
	 *                 v
	 *               PI/2 
	 *               (0,1)
	 * </pre>
	 * 
	 * 
	 * @param angle
	 *            the orientation of the vector in radians
	 * @return the new <code>Vector2D</code> object
	 */
	public static Vector2D getUnitLengthVector(final double angle) {
		return new Vector2D(Math.cos(angle), Math.sin(angle));
	}

	/**
	 * Creates a new <code>Vector2D</code> instance given the x and y
	 * coordinates.
	 * 
	 * @param xcoordinate
	 *            the x coordinate
	 * @param ycoordinate
	 *            the y coordinate
	 */
	public Vector2D(final double xcoordinate, final double ycoordinate) {
		x = xcoordinate;
		y = ycoordinate;
		nCreated++;
	}

	/**
	 * Creates a new vector by scaling this vector with a constant factor.
	 * 
	 * @param f
	 *            the scaling factor
	 * @return a new scaled vector
	 */
	public Vector2D scale(final double f) {
		return new Vector2D(x * f, y * f);
	}

	/**
	 * Creates a new vector by multiplying this vector with the scaling matrix,
	 * S, below.
	 * 
	 * <pre>
	 * S = | s.x 0 | 
	 *     | 0 s.y |
	 * </pre>
	 * 
	 * @param s
	 *            the scaling matrix
	 * @return a new vector scaled appropriately
	 */
	public Vector2D scale(final Vector2D s) {
		return new Vector2D(x * s.x, y * s.y);
	}

	/**
	 * Gets the squared magnitude of this vector.
	 * 
	 * @return the squared magnitude
	 */
	public double magnitude2() {
		return x * x + y * y;
	}

	/**
	 * Creates a new vector which equal to this vector translated by the
	 * specified amount.
	 * 
	 * @param v
	 *            a vector representing the translation in <code>(x,y)</code>
	 *            coordinates
	 * 
	 * @return the new translated vector
	 */
	public Vector2D translate(final Vector2D v) {
		return new Vector2D(x + v.x, y + v.y);
	}

	/**
	 * Gets the x coordinate of this vector.
	 * 
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the y coordinate of this vector.
	 * 
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Creates a new vector by substracting the specified value from both
	 * elements of the current vector.
	 * 
	 * @param d
	 *            the value to subtract
	 * @return a new vector representing the result of the operation
	 */
	public Vector2D difference(final double d) {
		return new Vector2D(x - d, y - d);
	}

	/**
	 * Creates a new vector by subtracting the specified vector from this one.
	 * This is simply a translation by <code>-1d</code>.
	 * 
	 * @param d
	 *            the vector to subtract from this one
	 * @return a new vector representing the result of the operation
	 */
	public Vector2D difference(final Vector2D d) {
		return new Vector2D(x - d.x, y - d.y);
	}

	/**
	 * Gets the squared distance between the end points of this vector and
	 * another.
	 * 
	 * @param b
	 *            the other vector
	 * @return the squared distance between the two
	 */
	public double distance2(final Vector2D b) {
		return (x - b.x) * (x - b.x) + (y - b.y) * (y - b.y);
	}

	/**
	 * Gets the dot product of this vector and another.
	 * 
	 * @param b
	 *            the other vector in the dot product operation.
	 * @return the dot product.
	 */
	public double dot(final Vector2D b) {
		return x * b.x + y * b.y;
	}

	/**
	 * Creates a new Vector that would result from the cross product of a vector
	 * in the Z-dimension with the specified magnitude and this vector. In other
	 * words, this return the following vector:
	 * 
	 * <pre>
	 *  | 0  -s  0 | | x |     | -s*y |
	 *  | s   0  0 | | y |  =  |  s*x |
	 *  | 0   0  0 | | 0 |     |   0  |
	 * </pre>
	 * 
	 * Most useful for rotation operations.
	 * 
	 * @param s
	 *            the magnitude of the vector in the Z-dimension
	 * @return the new Vector that results from apply the above operation
	 */
	public Vector2D dCrossV(final double s) {
		return new Vector2D(-s * y, s * x);
	}

	/**
	 * Calculates the magnitude of the vector in the Z-dimension which would
	 * result from the cross product of 2 vectors in the X-Y plane (i.e., two
	 * <code>Vector2D</code> instances). In other words this performs the
	 * calculation:
	 * 
	 * <pre>
	 * | 0 0  y | | b.x |   | 0 | 
	 * | 0 0 -x | | b.y | = | 0 | 
	 * | -y x 0 | |  0  |   | z |
	 * </pre>
	 * 
	 * and returns that value <code>z</code> as a scalar.
	 * 
	 * @param b
	 *            the other vector in the cross product
	 * @return the magnitude of the resulting 3-dimensional vector as a scalar
	 */
	public double cross(final Vector2D b) {
		return x * b.y - y * b.x;
	}

	/**
	 * Creates a new vector by taking the absolute value of each element in this
	 * vector.
	 * 
	 * @return a new vector as described above
	 */
	public Vector2D abs() {
		return new Vector2D(Math.abs(x), Math.abs(y));

	}

	/**
	 * Gets the string representation of this vector.
	 * 
	 * @return a string representing the vector
	 */
	@Override
	public String toString() {
		return "<" + x + ", " + y + ">";
	}
	/**
	 * Gets the string representation of this vector by subjecting
	 * each vector component to a string format.
	 * 
	 * @return a string representing the vector
	 */
	public String toString(String compFormat) {
		
		return "<" + String.format(compFormat, x) + ", " + 
			String.format(compFormat, y) + ">";
	}

	/**
	 * Find the angle from the end point of this vector to the end point of the
	 * specified vector. Equivilantly, translate the origin to the end point of
	 * this vector and find the angle to the end point of the specified
	 * vector...
	 * 
	 * <pre>
	 *              (0,-1) 
	 *              -PI/2
	 *                 &circ;
	 *                 |
	 * (-1,0) +-PI &lt;-- + --&gt; 0 (1,0) 
	 *                 |
	 *                 v
	 *               PI/2 
	 *               (0,1)
	 * </pre>
	 * 
	 * @param a
	 *            the other vector of interest
	 * @return the angle between endpoints
	 */
	public double angleTo(final Vector2D a) {
		return Math.atan2(a.y - this.y, a.x - this.x);
	}

	/**
	 * Reflects (bounces) the vector off of a surface.
	 * 
	 * @param tangent
	 *            the tangent to the surface in radians
	 * @return a new Vector reflected off the surface.
	 * 
	 */
	public Vector2D bounce(final double tangent) {
		double m = Math.cos(2 * tangent);
		double n = Math.sin(2 * tangent);
		return new Vector2D(m * x + n * y, n * x - m * y);
	}

	/**
	 * Reflects the vector about the angle specified.
	 * 
	 * 
	 * 
	 * @param normalunit
	 *            the vector to reflect about must be unit length
	 * @return a new Vector reflected about the normal.
	 * 
	 */
	public Vector2D reflect(final Vector2D normalunit) {
		Vector2D d = normalunit.scale(2 * normalunit.dot(this));
		return this.difference(d);
	}

	/**
	 * Returns a copy of this vector rotated by the given angle.
	 * 
	 * @param theta
	 *            The angle by which to rotate the vector.
	 * @return a new vector rotated by the angle.
	 */
	public Vector2D rotate(final double theta) {
		double nx = x * Math.cos(theta) - y * Math.sin(theta);
		double ny = x * Math.sin(theta) + y * Math.cos(theta);
		return new Vector2D(nx, ny);
	}

	/**
	 * Checks for epsilon equivilance between this vector and another.
	 * 
	 * @param other
	 *            the vector to check for equivilance
	 * @param e
	 *            the allowed variation in each dimension
	 * @return true iff the vectors are 'nearly' equal
	 */
	public boolean epsilonEquals(final Vector2D other, final double e) {
		if (Math.abs(x - other.x) <= e && Math.abs(y - other.y) <= e) {
			return true;
		}
		return false;
	}

	/**
	 * Checks this vector against another and tests for equality.
	 * 
	 * @param o
	 *            the other vector to test againts
	 * @return true iff the vectors are equal
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Vector2D) {
			return ((Vector2D) o).x == x && ((Vector2D) o).y == y;
		}
		return false;
	}

	/**
	 * Returns the hashcode for this Vector. The hashcode is calculated by first
	 * adding the x and y coordinates (after multiplying each with a unique
	 * small prime) and then using the has function from the <code>Double</code>
	 * object.
	 * 
	 * @return the hashcode.
	 */
	@Override
	public int hashCode() {
		long v = Double.doubleToLongBits(x * 71 + y * 73);
		return (int) (v ^ (v >>> 32));
	}

	/**
	 * Clamps the X coordinate to the specified range.
	 * 
	 * @param low
	 *            the lower bound (inclusive) of the resulting X coordinate
	 * @param high
	 *            the upper bound (inclusive) of the resulting X coordinate
	 * @return a new Vector2D with the X coordinate clamped
	 */
	public Vector2D clampX(final double low, final double high) {
		return new Vector2D(Math.min(high, Math.max(x, low)), y);
	}

	/**
	 * Clamps the Y coordinate to the specified range.
	 * 
	 * @param low
	 *            the lower bound (inclusive) of the resulting Y coordinate
	 * @param high
	 *            the upper bound (inclusive) of the resulting Y coordinate
	 * @return a new Vector2D with the Y coordinate clamped
	 */
	public Vector2D clampY(final double low, final double high) {
		return new Vector2D(x, Math.min(high, Math.max(y, low)));
	}

	/**
	 * Clamps the coordinates to the specified rectangle.
	 * 
	 * @param r
	 *            the rectangular boundary region
	 * 
	 * @return a new Vector2D with the coordinates clamped
	 */
	public Vector2D clamp(final Rectangle r) {
		return new Vector2D(Math.min(r.getMaxX(), Math.max(x, r.getMinX())),
				Math.min(r.getMaxY(), Math.max(y, r.getMinY())));
	}
	
	/**
	 * This method returns a unit length Vector2D based on the 
	 * current Vector. It should be used with care, as it
	 * requires computing the vector's overall magnitude using
	 * a relatively costly square-root function. In many cases
	 * use of square-root can be avoided with a little extra thought.
	 * 
	 * 
	 * @return a unit length Vector2D based on the direction of this vector
	 */
	public Vector2D unitVector() {
		double m = Math.sqrt(magnitude2());
		
		return new Vector2D(x / m, y / m);
	}
	
	
	/**
	 * Computes a new, transformed, vector based on desired
	 * affine transform.
	 * 
	 * <pre>
	 *  [ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
     *	[ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
	 *  [ 1 ]   [   0    0    1   ] [ 1 ]   [         1         ]
     * </pre>
     * 
     * <pre>
	 * { m00 m10 m01 m11 m02 m12 }
	 * </pre>
	 * 
	 * @param t the components of an affine transform
	 * as indicated above passed as a 6 element array of doubles in
	 * the order <code>m00 m10 m01 m11 m02 m12</code>
	 * 
	 * @return a vector appropriately transformed
	 * 
	 * @see java.awt.geom.AffineTransform
	 * @see java.awt.geom.AffineTransform#getMatrix(double[])
	 */
	public Vector2D transform(final double[] t) {
		return new Vector2D(t[0] * x + t[2] * y + t[4],
							t[1] * x + t[3] * y + t[5]);
	}
	
	
}
