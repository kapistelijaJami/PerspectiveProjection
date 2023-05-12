package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.projections.OrtographicProjection;
import perspectiveprojection.projections.PerspectiveProjection;
import perspectiveprojection.projections.Projection;

public class Main {
	public static void main(String[] args) {
		/*Point3D p = new Point3D(0, 0, 1);
		System.out.println(p);
		p = p.rotateAroundAxis(new Point3D(0, 1, 0), 5);
		System.out.println(p);*/
		
		/*Camera cam = new Camera(0, 0, 1000);
		Point3D p = new Point3D(0, 0, -6000);
		Projection orthProjection = new OrtographicProjection(cam);
		System.out.println(orthProjection.project(p));*/
		
		/*int x = -1;
		int y = 1;
		
		int w = 1280;
		int h = 720;
		x = (w * x + w) / 2;
		y = (h * -y + h) / 2;
		
		System.out.println("(" + x + ", " + y + ")");*/
		
		/*Camera cam = new Camera(0, 0, 1);
		//Projection projection = new PerspectiveProjection(cam);
		Point3D p = new Point3D(0, 0, 0);
		cam.lookAt(p);
		System.out.println("viewMatrix:");
		SimpleMatrix viewMatrix = cam.getViewMatrix();
		HelperFunctions.printSimpleMatrix(viewMatrix);
		
		HelperFunctions.printSimpleMatrix(viewMatrix.mult(p.asHomogeneousSimpleMatrix()));*/
		
		new Game(60).start();
		
		double x = 0.39;
		double z = 0.89;
		System.out.println(Math.atan2(x, z));
		
		//testSimpleMatrix1(); //See this to know which way matrix multiplication is done in this library
		//testSimpleMatrix2();
		//test();
	}
	
	public static void testSimpleMatrix1() {
		System.out.println();
		SimpleMatrix firstMatrix = new SimpleMatrix(new double[][] {
					{1d, 5d},
					{2d, 3d},
					{1d, 7d}
				});

		SimpleMatrix secondMatrix = new SimpleMatrix(new double[][] {
					{1d, 2d, 3d, 7d},
					{5d, 2d, 8d, 1d}
				});
		
		SimpleMatrix res = firstMatrix.mult(secondMatrix); //Multiplication is first on the left and second on the right
		System.out.println("res:");
		HelperFunctions.printMatrix(res);
		//So multiplication is first matrix row times second matrix column etc.
		
		//It's also marked as first * second in math. The SECOND matrix is the transformation which is done FIRST, because they are read right to left.
		
		
		SimpleMatrix A = new SimpleMatrix(new double[][] {
					{1, 5},
					{2, 3}
				});
		
		SimpleMatrix B = new SimpleMatrix(new double[][] {
					{6, 7},
					{3, 4}
				});
		
		//So A * B:
		SimpleMatrix v1 = new SimpleMatrix(new double[][]{{2}, {5}});
		System.out.println("This:");
		HelperFunctions.printMatrix(A.mult(B.mult(v1))); //B * vector first, then A * the result.
		
		//equals:
		SimpleMatrix v2 = new SimpleMatrix(new double[][]{{2}, {5}});
		System.out.println("Equals this:");
		HelperFunctions.printMatrix(A.mult(B).mult(v2)); //Is the same as A * B, and then the result * the vector.
		
		//So:	(A * B) * v			=	A * (B * v)
		//And:	A.mult(B).mult(v)	=	A.mult(B.mult(v)).
	}
	
	public static void testSimpleMatrix2() {
		System.out.println("");
		Point3D p = new Point3D(0, 0, 1);
		int y = 45;
		int x = 45;
		System.out.println(p.rotatedY(45).rotatedX(45) + "\n");
		
		SimpleMatrix v1 = new SimpleMatrix(new double[][]{{0}, {0}, {1}, {1}});
		
		HelperFunctions.printMatrix(HelperFunctions.getRotateYThenXMatrix(x, y).mult(v1));
		
		
		//How simpleMatrix is set up:
		SimpleMatrix v = new SimpleMatrix(new double[][]{
					{2}, //these are rows (but it's just a vector)
					{5},
					{7},
					{1}
				});
		System.out.println("vec:");
		HelperFunctions.printMatrix(v);
		
		SimpleMatrix mat = new SimpleMatrix(new double[][] {
					{  1,  2,  3,  4 }, //this represents a row in a resulting matrix as well
					{  5,  6,  7,  8 },
					{  9, 10, 11, 12 },
					{ 13, 14, 15, 16 }
				});
		System.out.println("mat:");
		HelperFunctions.printMatrix(mat);
		
		System.out.println("mat.mult(vec):");
		HelperFunctions.printMatrix(mat.mult(v));
	}
	
	public static void test() {
		Point3D forward = new Point3D(0, 0.55, -0.83).normalize();
		Point3D left = new Point3D(0, 1, 0).cross(forward).normalized(); //the first up hardcoded, doesn't work when forward is pointing up or down.
		Point3D up = forward.cross(left).normalized();
		
		//translation matrix
		SimpleMatrix T = new SimpleMatrix(new double[][] {
					{ 1, 0, 0, 4 },
					{ 0, 1, 0, 5 },
					{ 0, 0, 1, 6 },
					{ 0, 0, 0, 1 }
				});
		
		//rotation matrix (as column vectors, as row vectors it would be the inverse operation, since transpose is the inverse of rotation matrix.)
		SimpleMatrix R = new SimpleMatrix(new double[][] {
					{ left.x, up.x, forward.x, 0 },
					{ left.y, up.y, forward.y, 0 },
					{ left.z, up.z, forward.z, 0 },
					{      0,    0,         0, 1 }
				});
		
		//If I do R * T, which is translate first, then rotate, the resulting matrix loses the translate
		//information (the last column will be different), but keeps the rotation information (first 3 columns are the same).
		SimpleMatrix RT = R.mult(T);
		System.out.println("T:");
		HelperFunctions.printMatrix(T);
		System.out.println("Last column is different than RT:");
		HelperFunctions.printMatrix(RT);
		
		System.out.println("But R:");
		HelperFunctions.printMatrix(R);
		System.out.println("First 3 columns are still same as RT:");
		HelperFunctions.printMatrix(RT);
		
		System.out.println("--------------\n");
		
		//On the other hand if I do T * R, rotate first, then translate, the resulting matrix keeps the translate
		//information, but IT ALSO KEEPS the rotation information.
		SimpleMatrix TR = T.mult(R);
		System.out.println("T:");
		HelperFunctions.printMatrix(T);
		System.out.println("Last column is the same as TR:");
		HelperFunctions.printMatrix(TR);
		
		System.out.println("But R:");
		HelperFunctions.printMatrix(R);
		System.out.println("First 3 columns are ALSO still same as TR:");
		HelperFunctions.printMatrix(TR);
		
		//This should be because the matrices are homogeneous transformation matrices (i.e., has a 4th row of (0, 0, 0, 1)) 
		
		//So only if I do rotation last it will lose the translate info,
		//otherwise the matrix is just a combination of the matrices and no values will be changed
		//This means I could have matrix for positioning and orienting the camera (it is in that order already if I just place the values as columns),
		//and take the inverse to create the viewMatrix. Of course, taking the inverse is slower than doing some other way.
	}
}
