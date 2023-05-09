package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class HelperFunctions {
	public static double pythagoras(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
	
	public static double pythagoras3D(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double distance(double startX, double startY, double toX, double toY) {
		return pythagoras(toX - startX, toY - startY);
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
	
	public static double clamp(double val, int min, int max) {
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
}
