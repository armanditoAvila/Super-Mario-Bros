package jig.engine.util;

import java.awt.geom.Rectangle2D;

/**
 * Utility class for Axis Aligned Bounding Boxes.
 *
 * @author Scott Wallace
 *
 */
public final class AABB {
	public static final int QUAD_NONE = 0x0000;
	public static final int QUAD_MASK =  0x0ff0;
	public static final int QUAD_UPPER = 0x0100;
	public static final int QUAD_LOWER = 0x0200;
	public static final int QUAD_RIGHT = 0x0010;
	public static final int QUAD_LEFT = 0x0020;
	public static final int QUAD_LR = QUAD_LOWER | QUAD_RIGHT;
	public static final int QUAD_UR = QUAD_UPPER | QUAD_RIGHT;
	public static final int QUAD_UL = QUAD_UPPER | QUAD_LEFT;
	public static final int QUAD_LL = QUAD_LOWER | QUAD_LEFT;
	public static final int AXIS_X = 0x1000;
	public static final int AXIS_Y = 0x2000;
	public static final int AXIS_NONE = 0x0000;
	public static final int AXIS_MASK = 0xf000;
	
	/**
	 * Can't instantiate this class currently.
	 *
	 */
	private AABB() { }
	
	
	/**
	 * Uses the Separating Axis Theorem (SAT) to determine:
	 * <ul>
	 * <li> If two rectangles overlap. </li>
	 * <li> The quadrant (with respect to rectangle a) of overlap. </li>
	 * <li> The axis with the minimum overlap. </li>
	 * </ul>
	 * @param a the primary rectangle
	 * @param b the secondary rectangle
	 * @return the bitwise OR of quadrant and axis bits indicating the 
	 * collision surface. Returns 0 (AXIS_NONE|QUAD_NONE) if there is 
	 * no collision.
	 * 
	 */
	public static int minSeparation(final Rectangle2D a, final Rectangle2D b) {
		double ahw = a.getCenterX() - a.getMinX();
		double ahh = a.getCenterY() - a.getMinY();
		
		double bhw = b.getCenterX() - b.getMinX();
		double bhh = b.getCenterY() - b.getMinY();
		
		
		double dcx = a.getCenterX() - b.getCenterX();
		double dcy = a.getCenterY() - b.getCenterY();
		double xsep;
		double ysep;

		int quadrantOfA;
		int direction;
		
		if (dcx > 0) {
			/**
			 * <pre>
			 * B    A     (A is to the right of B)
			 * </pre>
			 */
			xsep = dcx - (bhw + ahw);
			if (xsep > 0) { 
				return QUAD_NONE | AXIS_NONE; 
			}
			
			if (dcy > 0) {
				/**
				 * <pre>
				 *   B
				 *       A    (A is right and below B)
				 * </pre>
				 */
				ysep = dcy - (bhh + ahh);
				quadrantOfA = QUAD_UL;
			} else {
				/**<pre>
				 *        A
				 *   B         (A is right and above B)
				 *   </pre>
				 */
				// dcy is negative, use absolute value...
				ysep = -dcy - (bhh + ahh);
				quadrantOfA = QUAD_LL;
			}
			
		} else {
			/** <pre>
			 * A   B    (A is to the left of B)
			 * </pre>
			 */
			// dcx is negative, use absolute value
			xsep = -dcx - (bhw + ahw);
			if (xsep > 0) { 
				return QUAD_NONE | AXIS_NONE; 
			}

			if (dcy > 0) {
				/** <pre>
				 *    B
				 * A       (A is left and below B)
				 * </pre>
				 */
				ysep = dcy - (bhh + ahh);
				quadrantOfA = QUAD_UR;
			} else {
				/**<pre>
				 * A
				 *     B   (A is left and above B)
				 *     </pre>
				 */
				// dcy is negative, use absolute value...
				ysep = -dcy - (bhh + ahh);
				quadrantOfA = QUAD_LR;
			}
			
		}
		
		if (ysep > 0) { 
			quadrantOfA = QUAD_NONE;
			direction = AXIS_NONE;
		} else {
			
			if (xsep < ysep) {
				// least penetration is Y
				direction = AXIS_Y;
			} else {
				direction = AXIS_X;
			}
		}
		return direction | quadrantOfA;

	
	}
	
	/**
	 * Returns a string representing of the Single Axis Theorem calculation
	 * as returned by a call to <code>minSeparation</code>.
	 * 
	 * @param bitcode a return value from a call to <code>minSeparation</code>
	 * @return a string representation of the bitcode
	 */
	public static String getSATString(final int bitcode) {
		StringBuffer sb = new StringBuffer(20);
		sb.append("SAT: ");

		switch (bitcode & QUAD_MASK) {
		case QUAD_UR:
			sb.append("UR ");
			break;
		case QUAD_LR:
			sb.append("LR ");
			break;
		case QUAD_UL:
			sb.append("UL ");
			break;
		case QUAD_LL:
			sb.append("LL ");
			break;
		case QUAD_NONE:
			sb.append("NO QUADRANT ");
			break;
		default:
			sb.append("INVALID QUADRANT ");
			break;
		}

		switch (bitcode & AXIS_MASK) {
		case AXIS_X:
			sb.append("X AXIS");
			break;
		case AXIS_Y:
			sb.append("Y AXIS");
			break;
		case AXIS_NONE:
			sb.append("NO AXIS");
			break;
		default:
			sb.append("INVALID AXIS");
			break;
		}

		return sb.toString();

	}
}
