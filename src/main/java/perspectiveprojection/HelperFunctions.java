package perspectiveprojection;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.ejml.simple.SimpleMatrix;

public class HelperFunctions {
	public static double pythagoras(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
	
	public static double pythagoras3D(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double pythagoras4D(double x, double y, double z, double w) {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}
	
	public static double distance(Point2D start, Point2D to) {
		return distance(start.x, start.y, to.x, to.y);
	}
	
	public static double distance(double startX, double startY, double toX, double toY) {
		return pythagoras(toX - startX, toY - startY);
	}
	
	public static double distance3D(Point3D start, Point3D to) {
		return distance3D(start.x, start.y, start.z, to.x, to.y, to.z);
	}
	
	public static double distance3D(double startX, double startY, double startZ, double toX, double toY, double toZ) {
		return pythagoras3D(toX - startX, toY - startY, toZ - startZ);
	}
	
	/**
	 * Fixes angle between 0 (inclusive) and 360 (exclusive)
	 * @param angle
	 * @return 
	 */
	public static double fixAngle(double angle) {
		while (angle < 0) {
			angle += 360;
		}
		
		while (angle >= 360) {
			angle -= 360;
		}
		return angle;
	}
	
	/**
	 * Fixes angle between -180 (inclusive) and 180 (exclusive)
	 * @param angle
	 * @return 
	 */
	public static double fixAngle180(double angle) {
		while (angle < -180) {
			angle += 360;
		}
		
		while (angle >= 180) {
			angle -= 360;
		}
		return angle;
	}
	
	public static double clamp(double val, double min, double max) {
		return Math.max(Math.min(val, max), min);
	}
	
	public static SimpleMatrix getRotateYThenXMatrix(double xDegrees, double yDegrees) {
		double rady = Math.toRadians(yDegrees);
		double radx = Math.toRadians(xDegrees);
		
		SimpleMatrix rotateYThenX = new SimpleMatrix(new double[][] {
					{  Math.cos(rady), 0,  Math.sin(rady), 0 },
					{  Math.sin(rady)*Math.sin(radx),  Math.cos(radx),  Math.cos(rady)*(-Math.sin(radx)), 0 },
					{  -Math.sin(rady)*Math.cos(radx), Math.sin(radx),    Math.cos(rady)*Math.cos(radx), 0 },
					{  0, 0, 0, 1 }
				});
		
		return rotateYThenX;
	}
	
	public static SimpleMatrix getRotateYThenXTranslateMatrix(double xDegrees, double yDegrees, Point3D translate) {
		double rady = Math.toRadians(yDegrees);
		double radx = Math.toRadians(xDegrees);
		
		SimpleMatrix rotateYThenX = new SimpleMatrix(new double[][] {
					{  Math.cos(rady), 0,  Math.sin(rady), translate.x },
					{  Math.sin(rady)*Math.sin(radx),  Math.cos(radx),  Math.cos(rady)*(-Math.sin(radx)), translate.y },
					{  -Math.sin(rady)*Math.cos(radx), Math.sin(radx),    Math.cos(rady)*Math.cos(radx), translate.z },
					{  0, 0, 0, 1 }
				});
		
		return rotateYThenX;
	}
	
	public static void printMatrix(SimpleMatrix m) {
		DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
		
		for (int row = 0; row < m.numRows(); row++) {
			System.out.print("[");
			
			for (int col = 0; col < m.numCols(); col++) {
				double val = m.get(row, col);
				if (val == 0) {
					val = 0; //sometimes val was -0.000, this fixes it.
				}
				int chars = getRequiredCharactersFromColumn(m, col);
				if (val >= 0) {
					System.out.print(" ");
				}
				System.out.print(" ");
				
				int digits = howManyDigits((int) val);
				for (int i = 0; i < chars - digits; i++) {
					System.out.print(" ");
				}
				
				System.out.print(df.format(val));
			}
			System.out.println("  ]");
		}
		System.out.println();
	}
	
	private static int getRequiredCharactersFromColumn(SimpleMatrix m, int col) {
		int mostDigits = 0; //most digits in the integer part
		
		for (int row = 0; row < m.numRows(); row++) {
			int val = Math.abs((int) m.get(row, col));
			if (val > mostDigits) {
				mostDigits = val;
			}
		}
		
		return howManyDigits(mostDigits);
	}
	
	public static int howManyDigits(int i) {
		if (i == 0) {
			return 1;
		}
		return (int) Math.log10(i) + 1;
	}
	
	public static boolean isInsideFarAndNear(Point3D p) {
		return p.z >= 0 && p.z <= 1;
	}
	
	public static boolean lineIsVisibleBetweenFarAndNear(Point3D s, Point3D e) {
		return isInsideFarAndNear(s) || isInsideFarAndNear(e);
	}
	
	public static boolean lineIsVisibleBetweenFarAndNear(LineSegment line) {
		return lineIsVisibleBetweenFarAndNear(line.getStart(), line.getEnd());
	}
	
	public static double lerp(double t, double a, double b) {
		return (1 - t) * a + t * b;
	}
	
	/*public static Point3D intersectionPointWithPlane(Point3D pointOnPlane, Point3D normal, Point3D start, Point3D end) {
		// Calculate t value where line intersects camera plane
		double t = (pointOnPlane.z - start.z) / (end.z - start.z);
		
		// Calculate intersection point
		double x = lerp(t, start.x, end.x);
		double y = lerp(t, start.y, end.y);
		double z = pointOnPlane.z;
		
		return new Point3D(x, y, z);
	}*/
	
	/**
	 * Calculates the t value for the intersection with line and plane.
	 * T value will be 0 when intersection is at the start, and 1 when
	 * one dir length forward, and negative when to the opposite direction.
	 * So length of dir matters, t value will be in units of dir length.
	 * Will return null if intersection is not possible because the line and plane are parallel.
	 * @param pointOnPlane
	 * @param normal
	 * @param start
	 * @param dir
	 * @return 
	 */
	public static Double getLinePlaneIntersectionTValue(SimpleMatrix pointOnPlane, SimpleMatrix normal, SimpleMatrix start, SimpleMatrix dir) {
		double denominator = normal.dot(dir);
		if (denominator == 0) { //Line and plane are parallel, no intersection possible
			return null;
		}
		
		SimpleMatrix planeToPoint = pointOnPlane.minus(start);
		return planeToPoint.dot(normal) / denominator; //Calculate t value
	}
	
	public static SimpleMatrix intersectionPointWithPlane(SimpleMatrix pointOnPlane, SimpleMatrix normal, SimpleMatrix start, SimpleMatrix end) {
		SimpleMatrix dir = end.minus(start);
		
		Double t = getLinePlaneIntersectionTValue(pointOnPlane, normal, start, dir);
		
		if (t == null || t < 0 || t > 1) { //Intersection point is outside the line segment
			return null;
		}
		
		return start.plus(dir.scale(t));
	}
	
	public static double distanceToLineSegment(Point3D p, LineSegment line) {
		return distanceToLine(p, line.getStart(), line.getEnd());
	}
	
	public static double distanceToLineSegment(Point3D p, Point3D start, Point3D end) { //Only between start and end. If projection is outside, distance to ends are used.
		Point3D dir = end.subtract(start);
		Point3D startToP = p.subtract(start);
		
		if (startToP.dot(dir) <= 0) { //Use start point
			return startToP.magnitude();
		}
		
		Point3D endToP = p.subtract(end);
		if (endToP.dot(dir) >= 0) { //Use end point
			return endToP.magnitude();
		}
		
		return distanceToLine(p, start, dir);
	}
	
	public static double distanceToLine(Point3D p, Point3D pointOnLine, Point3D dir) { //infinite line, goes through pointOnLine
		dir.normalize();
		double t = p.subtract(pointOnLine).dot(dir);
		Point3D projection = pointOnLine.add(dir.mult(t));
		
		return projection.subtract(p).magnitude();
	}
	
	public static SimpleMatrix normalize4DVector(SimpleMatrix v) {
		double x = v.get(0);
		double y = v.get(1);
		double z = v.get(2);
		double w = v.get(3);
		
		double magnitude = pythagoras4D(x, y, z, w);
		if (magnitude == 0) {
			return v;
		}
		return v.divide(magnitude);
	}
	
	public static SimpleMatrix getRotationMatrixAroundX3By3(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{1, 0, 0},
					{0, Math.cos(rad), -Math.sin(rad)},
					{0, Math.sin(rad), Math.cos(rad)}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundY3By3(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{Math.cos(rad), 0, Math.sin(rad)},
					{0, 1, 0},
					{-Math.sin(rad), 0, Math.cos(rad)}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundZ3By3(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{Math.cos(rad), -Math.sin(rad), 0},
					{Math.sin(rad), Math.cos(rad), 0},
					{0, 0, 1}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundAxis3By3(Point3D axis, double degrees) { //rotates with right hand rule (thumb towards axis positive direction and curled fingers are positive)
		//Uses the Rodrigues' rotation formula
		axis = axis.normalized();
		
		double cosTheta = Math.cos(Math.toRadians(degrees));
		double sinTheta = Math.sin(Math.toRadians(degrees));
		double oneMinusCosTheta = 1 - cosTheta;
		
		double ux = axis.x;
		double uy = axis.y;
		double uz = axis.z;
		
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{cosTheta + ux * ux * oneMinusCosTheta,			ux * uy * oneMinusCosTheta - uz * sinTheta,		ux * uz * oneMinusCosTheta + uy * sinTheta},
					{uy * ux * oneMinusCosTheta + uz * sinTheta,	cosTheta + uy * uy * oneMinusCosTheta,			uy * uz * oneMinusCosTheta - ux * sinTheta},
					{uz * ux * oneMinusCosTheta - uy * sinTheta,	uz * uy * oneMinusCosTheta + ux * sinTheta,		cosTheta + uz * uz * oneMinusCosTheta}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundX4By4(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{1,             0,              0, 0},
					{0, Math.cos(rad), -Math.sin(rad), 0},
					{0, Math.sin(rad),  Math.cos(rad), 0},
					{0,             0,              0, 1}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundY4By4(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{ Math.cos(rad), 0, Math.sin(rad), 0},
					{             0, 1,             0, 0},
					{-Math.sin(rad), 0, Math.cos(rad), 0},
					{             0, 0,             0, 1}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundZ4By4(double degrees) {
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{Math.cos(rad), -Math.sin(rad), 0, 0},
					{Math.sin(rad),  Math.cos(rad), 0, 0},
					{            0,              0, 1, 0},
					{            0,              0, 0, 1}
				});
		return m;
	}
	
	public static SimpleMatrix getTranslationMatrix(Point3D amount) {
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{1, 0, 0, amount.x},
					{0, 1, 0, amount.y},
					{0, 0, 1, amount.z},
					{0, 0, 0,        1}
				});
		return m;
	}
	
	public static SimpleMatrix getRotationMatrixAroundAxis4By4(Point3D axis, double degrees) { //rotates with right hand rule (thumb towards axis positive direction and curled fingers are positive)
		//Uses the Rodrigues' rotation formula
		axis = axis.normalized();
		
		double cosTheta = Math.cos(Math.toRadians(degrees));
		double sinTheta = Math.sin(Math.toRadians(degrees));
		double oneMinusCosTheta = 1 - cosTheta;
		
		double ux = axis.x;
		double uy = axis.y;
		double uz = axis.z;
		
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{cosTheta + ux * ux * oneMinusCosTheta,			ux * uy * oneMinusCosTheta - uz * sinTheta,		ux * uz * oneMinusCosTheta + uy * sinTheta, 0},
					{uy * ux * oneMinusCosTheta + uz * sinTheta,	cosTheta + uy * uy * oneMinusCosTheta,			uy * uz * oneMinusCosTheta - ux * sinTheta, 0},
					{uz * ux * oneMinusCosTheta - uy * sinTheta,	uz * uy * oneMinusCosTheta + ux * sinTheta,		cosTheta + uz * uz * oneMinusCosTheta, 0},
					{            0,              0,               0,             1}
				});
		return m;
	}
}
