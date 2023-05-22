package perspectiveprojection.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.transformations.projections.Projection;

public class Ray {
	private LineSegment ray;
	public long creationTime;
	
	public Ray(Point3D start, Point3D dir, double rayLength) {
		this(start, start.add(dir.mult(rayLength)));
	}
	
	public Ray(Point3D start, Point3D end) {
		this(new LineSegment(start, end));
	}
	
	public Ray(LineSegment ray) {
		this.ray = ray;
		creationTime = System.currentTimeMillis();
	}
	
	public void render(Graphics2D g, Projection projection) {
		double size = 5; //Size in 3D space.
		int minSize = 5; //Minimum size of the projected size
		double defaultSize = 5; //If null, use this as the projected size
		
		double startSize = Math.max(projection.getProjectedSize(ray.getStart(), size, defaultSize), minSize);
		double endSize = Math.max(projection.getProjectedSize(ray.getEnd(), size, defaultSize), minSize);
		
		LineSegment r = projection.projectLineSegment(ray);
		if (r == null) {
			return;
		}

		r.render(g, Color.YELLOW, startSize, endSize);
	}
	
	public LineSegment getAsLineSegment() {
		return ray;
	}
	
	public Point3D getStart() {
		return ray.getStart();
	}
	
	public Point3D getEnd() {
		return ray.getEnd();
	}
	
	public Point3D getDir() {
		return ray.getDir();
	}
	
	public double getLength() {
		return ray.getLength();
	}
	
	public Point3D getMiddle() {
		return ray.getMiddle();
	}
}
