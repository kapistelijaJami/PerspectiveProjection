package perspectiveprojection.primitives;

import java.awt.Color;
import java.awt.Graphics2D;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.HasListOfPoints;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.BooleanAndDistance;
import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.transformations.projections.Projection;
import perspectiveprojection.util.HelperFunctions;

//Axis aligned bounding box
public class BoundingBox {
	public Point3D minPoint;
	public Point3D maxPoint; //If sphere, min and max are the same
	public double size; //Spherical size (diameter), even for a box shaped bounds (corners diagonally)
	public BoundingBoxType type;
	
	public BoundingBox(Point3D minPoint, Point3D maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		type = BoundingBoxType.AXIS_ALIGNED_BOX;
		
		size = maxPoint.subtract(minPoint).magnitude();
	}
	
	public BoundingBox(Point3D point, double size) {
		this.minPoint = point;
		this.maxPoint = point;
		this.size = size;
		type = BoundingBoxType.SPHERE;
	}
	
	public static BoundingBox createBoundingBox(HasListOfPoints objWithPoints) {
		return createBoundingBox(objWithPoints.getListOfPoints());
	}
	
	public static BoundingBox createBoundingBox(List<SimpleMatrix> points) {
		Point3D minPoint = new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point3D maxPoint = new Point3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		for (SimpleMatrix p : points) {
			updateMinPoint(minPoint, Point3D.fromMatrix(p));
			updateMaxPoint(maxPoint, Point3D.fromMatrix(p));
		}
		
		return new BoundingBox(minPoint, maxPoint);
	}
	
	public static BoundingBox createBoundingBoxFromPoint3Ds(List<Point3D> points) {
		Point3D minPoint = new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point3D maxPoint = new Point3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		for (Point3D p : points) {
			updateMinPoint(minPoint, p);
			updateMaxPoint(maxPoint, p);
		}
		
		return new BoundingBox(minPoint, maxPoint);
	}
	
	private static void updateMinPoint(Point3D minPoint, Point3D p) {
		if (minPoint.x > p.x) {
			minPoint.x = p.x;
		}
		if (minPoint.y > p.y) {
			minPoint.y = p.y;
		}
		if (minPoint.z > p.z) {
			minPoint.z = p.z;
		}
	}
	
	private static void updateMaxPoint(Point3D maxPoint, Point3D p) {
		if (maxPoint.x < p.x) {
			maxPoint.x = p.x;
		}

		if (maxPoint.y < p.y) {
			maxPoint.y = p.y;
		}

		if (maxPoint.z < p.z) {
			maxPoint.z = p.z;
		}
	}
	
	public BooleanAndDistance lineIntersection(Point3D start, Point3D end) {
		Point3D dir = end.subtract(start);
		double length = dir.magnitude();
		
		return lineIntersection(start, dir.normalize(), length);
	}
	
	public BooleanAndDistance lineIntersectionInfinite(Point3D start, Point3D dir) {
		return lineIntersection(start, dir.normalize(), Double.MAX_VALUE);
	}
	
	public BooleanAndDistance lineIntersection(Point3D start, Point3D dir, double maxLength) { //dir is a unit vector
		if (type == BoundingBoxType.AXIS_ALIGNED_BOX) {
			return boxIntersection(start, dir, maxLength);
		} else if (type == BoundingBoxType.SPHERE) {
			double dist = HelperFunctions.distanceToLineSegment(minPoint, start, start.add(dir.mult(maxLength)));
			boolean bool = dist <= size / 2;
			if (bool) {
				return new BooleanAndDistance(bool, minPoint.subtract(start).magnitude() / maxLength);
			}
			return new BooleanAndDistance(false);
		}
		
		return new BooleanAndDistance(false);
	}
	
	private BooleanAndDistance boxIntersection(Point3D start, Point3D dir, double maxLength) {
		double tMin = 0.0f;
		double tMax = maxLength;
		
		for (int i = 0; i < 3; i++) { //x, y and z respectively
			//Check if line is parellel or nearly parallel
			if (Math.abs(dir.get(i)) < Double.MIN_VALUE) {
				//Because dir is basically not moving to this direction, we already have to be inside the box in this direction to hit it
				if (start.get(i) < minPoint.get(i) || start.get(i) > maxPoint.get(i)) { //Start is outside of the box area in this direction
					return new BooleanAndDistance(false); //No intersection with the AABB
				}
				continue;
			}
			
			//t values to hit the axis aligned planes of the box
			double tNear = (minPoint.get(i) - start.get(i)) / dir.get(i);
			double tFar = (maxPoint.get(i) - start.get(i)) / dir.get(i);
			
			//Swaps to keep tNear closest to the start
			if (tNear > tFar) {
				double temp = tNear;
				tNear = tFar;
				tFar = temp;
			}
			
			//Update t min and max closer together based on intersection t values
			tMin = Math.max(tMin, tNear);
			tMax = Math.min(tMax, tFar);
			
			//Moving tMin or tMax caused them to get past each other, which means line doesn't intersect with the box.
			if (tMin > tMax) {
				return new BooleanAndDistance(false);
			}
		}
		
		//Line intersects with the box and tMin and tMax are the points where it enters and exits the box.
		
		//start.plus(dir.scale(tMin)); //Point where it enters the box
		
		return new BooleanAndDistance(true, tMin);
	}
	
	public static BoundingBox createBoundingBoxAroundPoint(Point3D point, double size, BoundingBoxType type) {
		if (type == BoundingBoxType.SPHERE) {
			return new BoundingBox(point, size);
		}
		
		double s = size / 2.0;
		
		Point3D minPoint = point.subtract(s);
		Point3D maxPoint = point.add(s);
		
		return new BoundingBox(minPoint, maxPoint);
	}
	
	public static BoundingBox createBoundingBoxAroundLine(LineSegment line, double thickness, BoundingBoxType type) {
		if (type == BoundingBoxType.SPHERE) {
			return new BoundingBox(line.getMiddle(), line.getLength());
		}
		
		double s = thickness / 2.0;
		
		BoundingBox box = BoundingBox.createBoundingBox(Arrays.asList(line.getStart().asHomogeneousVector(), line.getEnd().asHomogeneousVector()));
		
		Point3D diff = box.maxPoint.subtract(box.minPoint).abs();
		
		if (diff.x > diff.y && diff.x > diff.z) {
			box.minPoint = box.minPoint.subtract(new Point3D(0, s, s));
			box.maxPoint = box.maxPoint.add(new Point3D(0, s, s));
		} else if (diff.y > diff.x && diff.y > diff.z) {
			box.minPoint = box.minPoint.subtract(new Point3D(s, 0, s));
			box.maxPoint = box.maxPoint.add(new Point3D(s, 0, s));
		} else if (diff.z > diff.x && diff.z > diff.y) {
			box.minPoint = box.minPoint.subtract(new Point3D(s, s, 0));
			box.maxPoint = box.maxPoint.add(new Point3D(s, s, 0));
		}
		
		return box;
	}
	
	public final Point3D getMiddle() {
		Point3D p = new Point3D();
		p.x = (minPoint.x + maxPoint.x) / 2.0;
		p.y = (minPoint.y + maxPoint.y) / 2.0;
		p.z = (minPoint.z + maxPoint.z) / 2.0;
		return p;
	}
	
	public void render(Graphics2D g, Projection projection, Color color) {
		if (type == BoundingBoxType.SPHERE) {
			return;
		}
		
		LineSegment[] lines = getLines();
		for (LineSegment line : lines) {
			Point3D start = line.getStart();
			Point3D end = line.getEnd();

			Optional<LineSegment> result = projection.projectLineSegment(line);
			if (result.isEmpty()) {
				continue;
			}
			line = result.get();

			int pointSize = 10;
			double startSize = projection.getProjectedSize(start, pointSize);
			double endSize = projection.getProjectedSize(end, pointSize);

			double sRadius = Math.max(startSize / 2, 5);
			double eRadius = Math.max(endSize / 2, 5);
			
			line.renderDots = false;
			line.render(g, color, sRadius, eRadius);
		}
	}

	private LineSegment[] getLines() {
		if (type == BoundingBoxType.SPHERE) {
			return null;
		}
		
		LineSegment[] lines = new LineSegment[12];
		
		lines[0] = new LineSegment(minPoint, new Point3D(minPoint.x, minPoint.y, maxPoint.z));
		lines[1] = new LineSegment(minPoint, new Point3D(minPoint.x, maxPoint.y, minPoint.z));
		lines[2] = new LineSegment(minPoint, new Point3D(maxPoint.x, minPoint.y, minPoint.z));
		
		lines[3] = new LineSegment(new Point3D(minPoint.x, maxPoint.y, maxPoint.z), new Point3D(minPoint.x, maxPoint.y, minPoint.z));
		lines[4] = new LineSegment(new Point3D(minPoint.x, maxPoint.y, maxPoint.z), new Point3D(minPoint.x, minPoint.y, maxPoint.z));
		lines[5] = new LineSegment(new Point3D(minPoint.x, maxPoint.y, maxPoint.z), new Point3D(maxPoint.x, maxPoint.y, maxPoint.z));
		
		lines[6] = new LineSegment(new Point3D(maxPoint.x, maxPoint.y, minPoint.z), new Point3D(maxPoint.x, minPoint.y, minPoint.z));
		lines[7] = new LineSegment(new Point3D(maxPoint.x, maxPoint.y, minPoint.z), new Point3D(minPoint.x, maxPoint.y, minPoint.z));
		lines[8] = new LineSegment(new Point3D(maxPoint.x, maxPoint.y, minPoint.z), new Point3D(maxPoint.x, maxPoint.y, maxPoint.z));
		
		lines[9] = new LineSegment(new Point3D(maxPoint.x, minPoint.y, maxPoint.z), new Point3D(maxPoint.x, minPoint.y, minPoint.z));
		lines[10] = new LineSegment(new Point3D(maxPoint.x, minPoint.y, maxPoint.z), new Point3D(minPoint.x, minPoint.y, maxPoint.z));
		lines[11] = new LineSegment(new Point3D(maxPoint.x, minPoint.y, maxPoint.z), new Point3D(maxPoint.x, maxPoint.y, maxPoint.z));
		
		return lines;
	}
}
