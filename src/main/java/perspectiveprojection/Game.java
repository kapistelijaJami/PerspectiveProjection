package perspectiveprojection;

import java.awt.Canvas;
import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import perspectiveprojection.projections.OrthographicProjection;
import perspectiveprojection.projections.PerspectiveProjection;
import uilibrary.GameLoop;
import uilibrary.Window;

public class Game extends GameLoop {
	private final Window window;
	
	//perspective good location when translate first, then rotate: loc (586.0, 691.0, 1202.0) yaw -26.0 pitch -153.0
	//private Camera cam = new Camera(new Point3D(500, 700, 800)); //(new Point3D(-400, 300, 500), -25, 28) works with ortographic (origo might be behind camera). Should be able to work with others with these settings as well
	private final Camera cam = new Camera(new Point3D(400, 500, 800)); //def: (400, 500, 800)
	private final Cube cube = new Cube(100, true);
	private final Cube smallCube = new Cube(70, false);
	private final Projection projection = new PerspectiveProjection(cam);
	//private Projection projection = new OrthographicProjection(cam);
	
	private final Light[] lights = new Light[] {new Light(800, 1000, 400), new Light(-500, -300, -400)};
	public static double ambientLight = 0.05; //from 0 to 1
	
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
	
	private boolean cameraRotated = false;
	private double newYaw;
	private double newPitch;
	
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
		
		cube.setLocation(new Point3D(50, 50, 50));
		smallCube.setLocation(new Point3D(500, 0, 0));
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
		
		if (cameraRotated) {
			cam.setYawAndPitch(newYaw, newPitch);
			cameraRotated = false;
		}
	}
	
	@Override
	protected void render() {
		Graphics2D g = window.getGraphics2D();
		
		renderAxis(g);
		
		List<Renderable> transformed = projection.projectFaces(cube.getWorldSpaceFaces(), lights);
		transformed.addAll(projection.projectFaces(smallCube.getWorldSpaceFaces(), lights));
		
		
		for (Light light : lights) {
			Point3D p = projection.project(light.location);
			transformed.add(new Light(p));
		}
		transformed.sort(null);
		
		for (Renderable obj : transformed) {
			obj.render(g);
		}
		
		/*cube.renderWireframe(g, projection);
		smallCube.renderWireframe(g, projection);*/
		
		window.display(g);
	}
	
	//Right hand rule, X is red (thumb, to right), Y is green (index, to up), Z is blue (middle, towards cam)
	private void renderAxis(Graphics2D g) {
		int axisLength = 10000;
		
		Point3D start = new Point3D(0, 0, 0);
		//Point3D s = projection.project(start);
		
		Point3D xAxis = new Point3D(axisLength, 0, 0);
		LineSegment x = projection.projectLineSegment(start, xAxis);
		
		if (x != null) {
			x.render(g, Color.red);
		}
		
		
		Point3D yAxis = new Point3D(0, axisLength, 0);
		LineSegment y = projection.projectLineSegment(start, yAxis);
		
		if (y != null) {
			y.render(g, Color.green);
		}
		
		
		Point3D zAxis = new Point3D(0, 0, axisLength);
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

	public void newYawAndPitch(double yaw, double pitch) {
		newYaw = yaw;
		newPitch = pitch;
		cameraRotated = true;
	}

	public double getCurrentYaw() {
		if (cameraRotated) {
			return newYaw;
		}
		return cam.getYaw();
	}

	public double getCurrentPitch() {
		if (cameraRotated) {
			return newPitch;
		}
		return cam.getPitch();
	}
}
