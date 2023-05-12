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
}
