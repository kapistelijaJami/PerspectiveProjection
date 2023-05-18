package perspectiveprojection;

import java.awt.Canvas;
import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static double DEFAULT_LIGHT_INTENSITY = 10000;
	
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
	private boolean cameraOrbit = false;
	private Double newYaw = null;
	private Double newPitch = null;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int FOV = 60; //def: 60, vertical FOV
	
	private GameObject selected;
	
	private final List<Ray> rays = new ArrayList<>();
	
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
		Point3D orbitPoint = getOrbitPoint();
		if (up) {
			//cam.pitch(speed);
			//cam.orbitAroundPointVertical(orbitPoint, speed);
		}
		if (down) {
			//cam.pitch(-speed);
			//cam.orbitAroundPointVertical(orbitPoint, -speed);
		}
		if (left) {
			//cam.turn(-speed);
			//cam.orbitAroundPointHorizontal(orbitPoint, -speed);
		}
		if (right) {
			//cam.turn(speed);
			//cam.orbitAroundPointHorizontal(orbitPoint, speed);
		}
		
		speed *= 5;
		
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
			//cam.moveUp(speed);
			cam.moveUpLocal(speed);
		}
		if (ctrl) {
			//cam.moveUp(-speed);
			cam.moveUpLocal(-speed);
		}
		
		if (cameraRotated) {
			cam.setYawAndPitch(newYaw, newPitch);
			cameraRotated = false;
		}
		if (cameraOrbit) {
			/*cam.orbitAroundPointHorizontal(orbitPoint, newYaw - cam.getYaw()); //Horizontal should be before vertical
			cam.orbitAroundPointVertical(orbitPoint, newPitch - cam.getPitch());*/
			
			cam.orbitAroundPoint(orbitPoint, newYaw - cam.getYaw(), newPitch - cam.getPitch());
			
			cameraOrbit = false;
		}
		
		for (int i = 0; i < rays.size(); i++) {
			Ray ray = rays.get(i);
			if (ray.creationTime + 5000 < System.currentTimeMillis()) {
				rays.remove(i);
				i--;
			}
		}
	}
	
	private Point3D getOrbitPoint() {
		Point3D p = null;
		if (selected != null) {
			if (selected instanceof HasBoundingBox) {
				p = ((HasBoundingBox) selected).getBoundingBox().getMiddle();
			}
		}
		if (p == null) {
			p = cam.getLoc().add(cam.getForward().mult(1000));
		}
		return p;
	}
	
	@Override
	protected void render() {
		Graphics2D g = window.getGraphics2D();
		
		renderAxis(g);
		
		List<Renderable> transformed = projection.projectFaces(cube.getWorldSpaceFaces(), lights);
		transformed.addAll(projection.projectFaces(smallCube.getWorldSpaceFaces(), lights));
		
		for (Light light : lights) {
			Point3D p = projection.project(light.location);
			
			Double size = projection.getProjectedSize(light.location, light.size);
			if (size == null) {
				continue;
			}
			transformed.add(new Light(p, size));
		}
		
		transformed.sort(null);
		
		for (Renderable obj : transformed) {
			obj.render(g);
		}
		
		for (Ray ray : rays) {
			LineSegment r = ray.ray;
			r = projection.projectLineSegment(r);
			if (r == null) {
				continue;
			}
			r.render(g, Color.YELLOW, 10, 10);
		}
		
		/*cube.renderWireframe(g, projection);
		smallCube.renderWireframe(g, projection);*/
		
		if (selected != null) {
			selected.renderSelected(g, projection);
		}
		
		window.display(g);
	}
	
	//Right hand rule, X is red (thumb, to right), Y is green (index, to up), Z is blue (middle, towards cam)
	private void renderAxis(Graphics2D g) {
		int axisLength = 10000;
		
		double pointSize = 5;
		
		Point3D start = new Point3D(0, 0, 0);
		
		Point3D xAxis = new Point3D(axisLength, 0, 0);
		LineSegment x = projection.projectLineSegment(start, xAxis);
		
		if (x != null) {
			x.render(g, Color.red, pointSize, pointSize);
		}
		
		
		Point3D yAxis = new Point3D(0, axisLength, 0);
		LineSegment y = projection.projectLineSegment(start, yAxis);
		
		if (y != null) {
			y.render(g, Color.green, pointSize, pointSize);
		}
		
		
		Point3D zAxis = new Point3D(0, 0, axisLength);
		LineSegment z = projection.projectLineSegment(start, zAxis);
		
		if (z != null) {
			z.render(g, Color.blue, pointSize, pointSize);
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
		newPitch = HelperFunctions.clamp(pitch, -89, 89);
		cameraRotated = true;
	}

	public void orbit(double yaw, double pitch) {
		newYaw = yaw;
		newPitch = HelperFunctions.clamp(pitch, -89, 89);
		cameraOrbit = true;
	}

	public double getCurrentYaw() {
		if (newYaw == null) {
			return cam.getYaw();
		}
		return newYaw;
	}

	public double getCurrentPitch() {
		if (newPitch == null) {
			return cam.getPitch();
		}
		return newPitch;
	}
	
	//Performs raycasting to select objects.
	public void click(int x, int y, boolean renderRay) {
		double rayLength = 10000;
		
		Point3D start = cam.getLoc();
		Point3D rayAtNearPlane = ViewportTransformation.fromScreenSpaceToClipSpace(new Point2D(x, y), WIDTH, HEIGHT);
		rayAtNearPlane = Point3D.fromMatrixDivideByW(projection.fromClipSpaceToWorldSpace(rayAtNearPlane));
		Point3D dir = rayAtNearPlane.subtract(start).normalize();
		if (renderRay) {
			rays.add(new Ray(new LineSegment(start, start.add(dir.mult(rayLength)))));
		}
		
		List<HasBoundingBox> objects = getObjectsAsBoundingBoxes();
		for (HasBoundingBox object : objects) {
			BoundingBox bounds = object.getBoundingBox();
			if (bounds.lineIntersection(start, dir, rayLength)) { //TODO: get t value and keep track of closest
				if (object instanceof GameObject) {
					selected = (GameObject) object;
					return;
				}
			}
		}
		
		selected = null;
	}
	
	private List<HasBoundingBox> getObjectsAsBoundingBoxes() {
		List<HasBoundingBox> objects = new ArrayList<>();
		
		objects.add(cube);
		objects.add(smallCube);
		objects.addAll(Arrays.asList(lights));
		
		return objects;
	}

	public void focusSelected() {
		if (selected == null) {
			cam.lookAt(new Point3D());
			cam.setLoc(cam.getForward().negated().mult(700));
		} else {
			BoundingBox bounds = selected.getBoundingBox();
			Point3D middle = bounds.getMiddle();
			double size = bounds.size;
			
			double dist = size / Math.sin(Math.toRadians(FOV / 2.0));
			
			cam.lookAt(middle);
			cam.setLoc(middle.subtract(cam.getForward().mult(dist)));
		}
		newYaw = cam.getYaw();
		newPitch = cam.getPitch();
	}
}
