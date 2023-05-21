package perspectiveprojection;

import java.awt.Color;
import java.awt.Graphics2D;
import perspectiveprojection.projections.Projection;

public class Ray {
	public LineSegment ray;
	public long creationTime;
	
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
}
