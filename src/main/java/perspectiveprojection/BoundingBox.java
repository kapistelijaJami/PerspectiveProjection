package perspectiveprojection;

import java.util.Arrays;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

//Axis aligned bounding box
public class BoundingBox {
	public Point3D minPoint;
	public Point3D maxPoint; //If sphere, min and max are the same
	public double size; //Spherical size, even for a box shaped bounds, diameter
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
	
	public boolean lineIntersection(Point3D start, Point3D end) {
		Point3D dir = end.subtract(start);
		double length = dir.magnitude();
		
		return lineIntersection(start, dir.normalize(), length);
	}
	
	public boolean lineIntersectionInfinite(Point3D start, Point3D dir) {
		return lineIntersection(start, dir.normalize(), Double.MAX_VALUE);
	}
	
	public boolean lineIntersection(Point3D start, Point3D dir, double maxLength) { //dir is a unit vector
		if (type == BoundingBoxType.AXIS_ALIGNED_BOX) {
			return boxIntersection(start, dir, maxLength);
		} else if (type == BoundingBoxType.SPHERE) {
			double dist = HelperFunctions.distanceToLineSegment(minPoint, start, start.add(dir.mult(maxLength)));
			return dist <= size / 2;
		}
		
		return false;
	}
	
	private boolean boxIntersection(Point3D start, Point3D dir, double maxLength) {
		double tMin = 0.0f;
		double tMax = maxLength;
		
		for (int i = 0; i < 3; i++) { //x, y and z respectively
			//Check if line is parellel or nearly parallel
			if (Math.abs(dir.get(i)) < Double.MIN_VALUE) {
				//Because dir is basically not moving to this direction, we already have to be inside the box in this direction to hit it
				if (start.get(i) < minPoint.get(i) || start.get(i) > maxPoint.get(i)) { //Start is outside of the box area in this direction
					return false; //No intersection with the AABB
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
				return false;
			}
		}
		
		//Line intersects with the box and tMin and tMax are the points where it enters and exits the box.
		
		//start.plus(dir.scale(tMin)); //Point where it enters the box
		
		return true;
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
}
