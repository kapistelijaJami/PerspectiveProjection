package perspectiveprojection.primitives;

import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.HasListOfPoints;
import perspectiveprojection.interfaces.Renderable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Game;
import perspectiveprojection.objects.Light;
import perspectiveprojection.util.HelperFunctions;

public class Face implements Renderable, HasListOfPoints {
	private List<SimpleMatrix> points = new ArrayList<>(); //right hand rule, counterclockwise winding direction
	public Color color = Color.LIGHT_GRAY;
	public boolean affectedByLights = true;
	private double lightMult = 1;
	
	public Face() {}
	
	public Face(Point3D... points) {
		this(Color.LIGHT_GRAY, points);
	}
	
	public Face(Color color, Point3D... points) {
		this(color);
		for (Point3D p : points) {
			this.points.add(p.asHomogeneousVector());
		}
	}
	
	public Face(SimpleMatrix... points) {
		this(Color.LIGHT_GRAY);
		this.points = Arrays.asList(points);
	}
	
	public Face(Color color) {
		this(color, true);
	}
	
	public Face(Color color, boolean affectedByLights) {
		this.color = color;
		this.affectedByLights = affectedByLights;
	}
	
	public Face copyWithoutPoints() {
		Face f = new Face(this.color, this.affectedByLights);
		f.lightMult = this.lightMult;
		return f;
	}
	
	public void setLightMultiplier(double d) {
		lightMult = d;
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
		Color newColor = color;
		if (affectedByLights) {
			int red = (int) HelperFunctions.clamp(color.getRed() * lightMult, 0, 255);
			int green = (int) HelperFunctions.clamp(color.getGreen() * lightMult, 0, 255);
			int blue = (int) HelperFunctions.clamp(color.getBlue() * lightMult, 0, 255);
			
			newColor = new Color(red, green, blue);
		}
		
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
	
	public void addPoint(Point3D p) {
		addPoint(p.asHomogeneousVector());
	}
	
	/**
	 * Applies a matrix to all the points and returns a new face (copy).
	 * @param m
	 * @return 
	 */
	public Face applyMatrix(SimpleMatrix m) {
		Face face = this.copyWithoutPoints();
		
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
			//Directions from p to previous point and to next point
			Point3D dirPrev = Point3D.fromMatrix(points.get(prev < 0 ? prev + points.size() : prev)).subtract(p);
			Point3D dirNext = Point3D.fromMatrix(points.get((i + 1) % points.size())).subtract(p);
			
			Point3D n = dirNext.cross(dirPrev).normalize();
			if (!n.isZero()) {
				normal = normal.add(n);
				count++;
			}
		}
		return normal.divide(count).normalize(); //Average of all calculated normals (if face is a triangle, they all should be same)
	}

	@Override
	public double getDepth() {
		return getAverageZ();
	}

	@Override
	public List<SimpleMatrix> getListOfPoints() {
		return points;
	}
	
	public void renderLines(Graphics2D g, Color color, double thickness) {
		for (LineSegment line : getLines()) {
			line.renderDots = false;
			line.render(g, color, thickness, 0, 0);
		}
	}
	
	public void calculateColorMultiplier(Light[] lights) {
		//Calculate color multiplier from light source:
		Point3D n = getFaceNormal();
		Point3D loc = getAverageLocation();

		double sum = 0;
		if (lights != null) {
			for (Light light : lights) {
				Point3D lightDir = light.getLocation().subtract(loc);
				double distance = lightDir.magnitude();
				lightDir.normalize();
				double dot = n.dot(lightDir);
				if (dot > 0) {
					sum += dot * (light.getIntensity() / Math.pow(distance, 2)) * Game.DEFAULT_LIGHT_INTENSITY;
				}
			}
		}
		
		sum = HelperFunctions.clamp(sum, Game.AMBIENT_LIGHT, 1);
		//sum = Math.max(sum, Game.AMBIENT_LIGHT); //If this, then have to limit the colors to 255 later. This goes more towards white, but doesn't affect the colors where components are 0, or close.
		setLightMultiplier(sum);
	}
}
