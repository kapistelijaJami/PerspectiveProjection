package perspectiveprojection;

import java.awt.Canvas;
import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import perspectiveprojection.projections.OrthographicProjection;
import perspectiveprojection.projections.PerspectiveProjection;
import uilibrary.GameLoop;
import uilibrary.Window;

public class Game extends GameLoop {
	private Window window;
	
	//perspective good location when translate first, then rotate: loc (586.0, 691.0, 1202.0) yaw -26.0 pitch -153.0
	//private Camera cam = new Camera(new Point3D(500, 700, 800)); //(new Point3D(-400, 300, 500), -25, 28) works with ortographic (origo might be behind camera). Should be able to work with others with these settings as well
	private Camera cam = new Camera(new Point3D(400, 500, 800));
	private Cube cube = new Cube(100);
	private Cube cameraObject = new Cube(20);
	private Projection projection = new PerspectiveProjection(cam);
	//private Projection projection = new OrthographicProjection(cam);
	
	public boolean up = false;
	public boolean down = false;
	public boolean left = false;
	public boolean right = false;
	
	public boolean w = false;
	public boolean a = false;
	public boolean s = false;
	public boolean d = false;
	public boolean shift = false;
	public boolean space = false;
	public boolean ctrl = false;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	
	public Game(int fps) {
		super(fps);
		window = new Window(WIDTH, HEIGHT, "Perspective projection");
	}
	
	@Override
	protected void init() {
		KeyInput input = new KeyInput(this);
		
		Canvas canvas = window.getCanvas();
		canvas.addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		canvas.addMouseWheelListener(input);
		
		cube.setLocation(new Point3D(100, 100, 100));
		cameraObject.setLocation(new Point3D(500, 0, 0));
	}
	
	@Override
	protected void lazyUpdate(int fps) {
		window.setTitle("Perspective projection (" + fps + " fps)");
	}
	
	@Override
	protected void update() {
		double speed = 0.5;
		if (up) {
			cam.pitch(speed);
		}
		if (down) {
			cam.pitch(-speed);
		}
		if (left) {
			cam.turn(-speed);
			//cam.turnRelativeToWorld(-speed);
		}
		if (right) {
			cam.turn(speed);
			//cam.turnRelativeToWorld(speed);
		}
		
		speed *= 10;
		
		if (w) {
			cam.moveForward(speed);
		}
		if (s) {
			cam.moveForward(-speed);
		}
		if (a) {
			cam.moveRight(-speed);
		}
		if (d) {
			cam.moveRight(speed);
		}
		
		if (shift || space) {
			cam.moveUp(speed);
		}
		if (ctrl) {
			cam.moveUp(-speed);
		}
	}
	
	@Override
	protected void render() {
		Graphics2D g = window.getGraphics2D();
		g.setColor(Color.red);
		
		//Point3D cubeOffset = new Point3D(100, 100, 100);
		
		cube.render(g, projection);
		
		cameraObject.render(g, projection);
		
		renderAxis(g, new Point3D());
		
		window.display(g);
	}
	
	//Right hand rule, X is red (thumb, to right), Y is green (index, to up), Z is blue (middle, towards cam)
	private void renderAxis(Graphics2D g, Point3D offset) {
		Point3D start = new Point3D(0, 0, 0).add(offset);
		//Point3D s = projection.project(start);
		
		Point3D xAxis = new Point3D(1000, 0, 0).add(offset);
		LineSegment x = projection.projectLineSegment(start, xAxis);
		
		if (x != null) {
			x.render(g, Color.red);
		}
		
		
		Point3D yAxis = new Point3D(0, 1000, 0).add(offset);
		LineSegment y = projection.projectLineSegment(start, yAxis);
		
		if (y != null) {
			y.render(g, Color.green);
		}
		
		
		Point3D zAxis = new Point3D(0, 0, 1000).add(offset);
		LineSegment z = projection.projectLineSegment(start, zAxis);
		
		if (z != null) {
			z.render(g, Color.blue);
		}
	}
	
	public Camera getCamera()  {
		return cam;
	}

	public void lookAt(Point3D point3D) {
		cam.lookAt(point3D);
	}
}
