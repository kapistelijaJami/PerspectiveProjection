package perspectiveprojection;

public class Ray {
	public LineSegment ray;
	public long creationTime;
	
	public Ray(LineSegment ray) {
		this.ray = ray;
		creationTime = System.currentTimeMillis();
	}
}
