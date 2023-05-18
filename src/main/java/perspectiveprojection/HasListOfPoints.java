package perspectiveprojection;

import java.util.List;
import org.ejml.simple.SimpleMatrix;

public interface HasListOfPoints {
	/**
	 * Return a list of points in world space.
	 * @return 
	 */
	public List<SimpleMatrix> getListOfPoints();
}
