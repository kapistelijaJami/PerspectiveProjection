package perspectiveprojection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

public class Face implements Renderable, HasListOfPoints {
	public List<SimpleMatrix> points = new ArrayList<>(); //right hand rule, counterclockwise winding direction
	public Color color;
	public double lightMult = 1;
	
	public Face(Point3D... points) {
		this(Color.LIGHT_GRAY, points);
	}
	
	public Face(Color color, Point3D... points) {
		this.color = color;
		for (Point3D p : points) {
			this.points.add(p.asHomogeneousVector());
		}
	}
	
	public Face(SimpleMatrix... points) {
		this.color = Color.LIGHT_GRAY;
		this.points = Arrays.asList(points);
	}
	
	public Face(Color color, double lightMult) {
		this.color = color;
		this.lightMult = lightMult;
	}
	
	public int[] getXPoints() {
		int[] list = new int[points.size()];
		
		for (int i = 0; i < points.size(); i++) {
			list[i] = (int) points.get(i).get(0);
		}
		return list;
	}
	
	public int[] getYPoints() {
		int[] list = new int[points.size()];
		
		for (int i = 0; i < points.size(); i++) {
			list[i] = (int) points.get(i).get(1);
		}
		return list;
	}
	
	@Override
	public void render(Graphics2D g) {
		Color newColor = new Color((int) (color.getRed() * lightMult), (int) (color.getGreen() * lightMult), (int) (color.getBlue() * lightMult));
		
		g.setColor(newColor);
		g.fillPolygon(getXPoints(), getYPoints(), points.size());
	}
	
	public Point3D getAverageLocation() {
		Point3D point = new Point3D();
		for (SimpleMatrix p : points) {
			point = point.add(Point3D.fromMatrix(p));
		}
		
		return point.divide(points.size());
	}
	
	public double getAverageZ() {
		double val = 0;
		for (SimpleMatrix p : points) {
			val += p.get(2);
		}
		
		return val / points.size();
	}
	
	public void addPoint(SimpleMatrix p) {
		points.add(p);
	}
	
	/**
	 * Applies a matrix to all the points and returns a new face (copy).
	 * @param m
	 * @return 
	 */
	public Face applyMatrix(SimpleMatrix m) {
		Face face = new Face(color, lightMult);
		
		for (SimpleMatrix p : points) {
			face.addPoint(m.mult(p));
		}
		
		return face;
	}
	
	public LineSegment[] getLines() {
		LineSegment[] lines = new LineSegment[points.size()];
		
		for (int i = 0; i < points.size(); i++) {
			lines[i] = new LineSegment(Point3D.fromMatrix(points.get(i)), Point3D.fromMatrix(points.get((i + 1) % points.size())));
		}
		
		return lines;
	}
	
	public Point3D getFaceNormal() {
		int count = 0;
		Point3D normal = new Point3D();
		for (int i = 0; i < points.size(); i++) {
			Point3D p = Point3D.fromMatrix(points.get(i));
			int prev = i - 1;
			Point3D dirPrev = Point3D.fromMatrix(points.get(prev < 0 ? prev + points.size() : prev)).subtract(p);
			Point3D dirNext = Point3D.fromMatrix(points.get((i + 1) % points.size())).subtract(p);
			
			Point3D n = dirNext.cross(dirPrev).normalize();
			if (!n.isZero()) {
				normal = normal.add(n);
				count++;
			}
		}
		return normal.divide(count).normalize();
	}

	@Override
	public double getDepth() {
		return getAverageZ();
	}

	@Override
	public List<SimpleMatrix> getListOfPoints() {
		return points;
	}
}
