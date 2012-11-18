package jig.engine.util;

/**
 * 
 * A 2 dimensional Matrix with the form below.
 * <pre>
 *          |  a    b  |
 *          |  c    d  |
 * </pre>
 * 
 * @author wallaces
 *
 */
public class Matrix22 {

	/** Element in upper left corner. */
	public double a;
	/** Element in upper right corner. */
	public double b;
	/** Element in lower left corner. */
	public double c;
	/** Element in lower right corner. */
	public double d;

	/** 
	 * Creates a new matrix with the specified elements below.
	 * 
	 *  | a   b |
	 *  |       |
	 *  | c   d |
	 * 
	 * @param a the upper left corner element
	 * @param b the upper right corner element
	 * @param c the lower left corner element
	 * @param d the lower right corner element
	 */
	public Matrix22(final double a, final double b, final double c,
					final double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	/**
	 * Creates the rotation transformation matrix for the specified angle.
	 * 
	 * <pre>
	 * | cos( theta )    -sin( theta ) |
	 * |                               |
	 * | sin( theta )     cos( theta ) | 
	 * </pre>
	 * 
	 * @param theta the desired angle (in radians)
	 */
	public Matrix22(final double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);

		a = cosTheta;
		b = -sinTheta;
		c = sinTheta;
		d = cosTheta;
	}

	/**
	 * Creates the transponse of the current matrix.
	 * 
	 * | a    c |
	 * |        |
	 * | b    d |
	 * 
	 * @return the transposed matrix
	 */
	public Matrix22 transpose() {
		return new Matrix22(a, c, b, d);
	}

	/**
	 * Creates the inverse of the current matrix or throws a
	 * an <code>IllegalArgumentException</code> if the determinate
	 * (det) is 0.
	 * 
	 * |  d/det    -b/det |
	 * |                  |
	 * | -c/det    a/det  |
	 * 
	 * @return the inverted matrix
	 */
	public Matrix22 invert() {
		double det = a * d - b * c;
		if (det == 0.0) {
			throw new IllegalArgumentException("Matrix is not invertable");
		}
		det = 1.0 / det;
		return new Matrix22(det * d, -det * b, -det * c, det * a);

	}

	/**
	 * Creates a new matrix by adding the specified matrix to this one. 
	 * 
	 * @param o the matrix to add
	 * @return a new matrix which is the sum of <code>o</code> and this matrix
	 */
	public Matrix22 add(final Matrix22 o) {
		return new Matrix22(a + o.a, b + o.b, c + o.c, d + o.d);

	}

	/**
	 * Creates a new 2D Vector my multiply this matrix with the specified
	 * 2D Vector.
	 * 
	 * @param f the vector to which this matrix will be multiplied
	 * @return a new <code>Vector2D</code> with the results of the operation.
	 * 
	 * 
	 */
	public Vector2D multiply(final Vector2D f) {

		return new Vector2D(a * f.getX() + b * f.getY(), 
							c * f.getX() + d * f.getY());
	}

	/**
	 * Creates a new matrix by multiplying this matrix with the specified one.
	 * 
	 * <pre>
	 *  | a * m.a + b * m.c         a * m.b + b * m.d |
	 *  |                                             |
	 *  | c * m.a + d * m.c         c * m.b + d * m.d |
	 * </pre>
	 *  
	 * @param m the matrix against which this one will be multiplied
	 * @return a new <code>Matrix22</code> with the results of the operation.
	 */
	public Matrix22 multiply(final Matrix22 m) {

		return new Matrix22(a * m.a + b * m.c, a * m.b + b * m.d, c * m.a + d
				* m.c, c * m.b + d * m.d);
	}

	/**
	 * Creates a new matrix by taking the absolute value of each element
	 * in the current matrix.
	 * 
	 * @return the newly created matrix.
	 */
	public Matrix22 abs() {
		return new Matrix22(Math.abs(a), Math.abs(b), Math.abs(c), Math.abs(d));
	}

	
	/**
	 * Gets a string representation of the matrix.
	 * 
	 * @return the string representing this matrix.
	 */
	@Override
	public String toString() {
		final int defaultBufferLength = 20;
		
		StringBuffer sb = new StringBuffer(defaultBufferLength);
		sb.append("[Matrix: ");
		sb.append(a);
		sb.append(',');
		sb.append(b);
		sb.append(',');
		sb.append(c);
		sb.append(',');
		sb.append(d);
		sb.append(']');
		return sb.toString();
	}
}
