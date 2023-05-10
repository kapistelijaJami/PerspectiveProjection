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
		
		Camera cam = new Camera(0, 0, 1000);
		Point3D p = new Point3D(0, 0, -6000);
		Projection orthProjection = new OrtographicProjection(cam);
		System.out.println(orthProjection.project(p));
		
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
		
		//testSimpleMatrix1(); //See this to know which way matrix multiplication is done in this library
		//testSimpleMatrix2();
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
}
