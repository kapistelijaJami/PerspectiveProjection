package perspectiveprojection;

import perspectiveprojection.objects.Light;
import perspectiveprojection.util.HelperFunctions;
import perspectiveprojection.primitives.BoundingBox;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.enums.MoveDirection;
import perspectiveprojection.linear_algebra.Point2D;
import perspectiveprojection.transformations.ViewportTransformation;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.objects.Ray;
import perspectiveprojection.input.KeyInput;
import perspectiveprojection.objects.Cube;
import perspectiveprojection.camera.Camera;
import perspectiveprojection.objects.GameObject;
import perspectiveprojection.interfaces.HasBoundingBox;
import perspectiveprojection.interfaces.Renderable;
import perspectiveprojection.objects.MoveArrows;
import java.awt.Canvas;
import perspectiveprojection.transformations.projections.Projection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import perspectiveprojection.objects.Any3DObject;
import perspectiveprojection.transformations.projections.OrthographicProjection;
import perspectiveprojection.transformations.projections.PerspectiveProjection;
import uilibrary.GameLoop;
import uilibrary.Window;

public class Game extends GameLoop {
	public static int WIDTH = 1280; //This will be the canvas size.
	public static int HEIGHT = 720;
	public static int FOV = 60; //def: 60, vertical FOV
	public static int DEFAULT_RAY_LENGTH = 10000;
	
	private final Camera cam = new Camera(new Point3D(400, 500, 800)); //def: (400, 500, 800)
	private final Cube cube = new Cube(100, true);
	private final Cube smallCube = new Cube(70, false);
	private Any3DObject obj;
	
	private final Window window;
	private Projection projection = new PerspectiveProjection(cam);
	//private Projection projection = new OrthographicProjection(cam);
	
	private final Light[] lights = new Light[] {new Light(900, 500, 700), new Light(-500, 700, -400)};
	public static double AMBIENT_LIGHT = 0.05; //from 0 to 1
	public static double DEFAULT_LIGHT_INTENSITY = 6000;
	
	private KeyInput input;
	
	private boolean cameraRotated = false;
	private boolean cameraOrbit = false;
	private Double newYaw = null;
	private Double newPitch = null;
	
	private final Object lock = new Object();
	private volatile GameObject selected;
	private volatile GameObject hovering;
	
	private final List<Ray> rays = new ArrayList<>();
	
	private boolean initReady = false;
	
	public Game(int fps) {
		super(fps);
		window = new Window(WIDTH, HEIGHT, "Perspective projection");
		window.setCanvasBackground(Color.DARK_GRAY.darker().darker());
	}
	
	@Override
	protected void init() {
		input = new KeyInput(this);
		
		Canvas canvas = window.getCanvas();
		canvas.addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		canvas.addMouseWheelListener(input);
		canvas.addComponentListener(input);
		
		cube.setLocation(new Point3D(50, 50, 50));
		smallCube.setLocation(new Point3D(500, 0, 0));
		
		/*obj = Any3DObject.createFromFile("D:\\Tiedostot\\3D Printing stuff\\Indian.obj", 1);
		obj.rotate(HelperFunctions.getRotationMatrixAroundY4By4(90));
		obj.rotate(HelperFunctions.getRotationMatrixAroundX4By4(-90));*/
		obj = Any3DObject.createFromFile("D:\\Tiedostot\\3D Printing stuff\\Extended wheel.obj", 10);
		obj.rotate(HelperFunctions.getRotationMatrixAroundY4By4(180));
		
		obj.setLocation(new Point3D(0, 0, 500));
		
		/*cube.rotate(HelperFunctions.getRotationMatrixAroundY4By4(45));
		cube.rotate(HelperFunctions.getRotationMatrixAroundX4By4(20));*/
		
		initReady = true;
	}
	
	@Override
	protected void lazyUpdate(int fps) {
		window.setTitle("Perspective projection (" + fps + " fps)");
	}
	
	private Point3D getOrbitPoint() {
		Point3D p = null;
		if (selected != null) {
			p = selected.getBoundingBox().getMiddle();
			cam.orbitPointDistance = p.distanceFrom(cam.getLoc());
		}
		
		if (p == null) {
			if (cam.orbitPointDistance == -1) {
				cam.orbitPointDistance = cam.getLoc().distanceFrom(new Point3D());
			}
			p = cam.getLoc().add(cam.getForward().mult(cam.orbitPointDistance));
		}
		
		return p;
	}
	
	public Canvas getCanvas() {
		return window.getCanvas();
	}
	
	@Override
	protected void update() {
		double speed = 0.5;
		Point3D orbitPoint = getOrbitPoint();
		if (input.isButtonDown(KeyEvent.VK_UP)) {
			cam.pitch(speed);
			//cam.orbitAroundPointVertical(orbitPoint, speed);
		}
		if (input.isButtonDown(KeyEvent.VK_DOWN)) {
			cam.pitch(-speed);
			//cam.orbitAroundPointVertical(orbitPoint, -speed);
		}
		if (input.isButtonDown(KeyEvent.VK_LEFT)) {
			cam.turn(-speed);
			//cam.orbitAroundPointHorizontal(orbitPoint, -speed);
		}
		if (input.isButtonDown(KeyEvent.VK_RIGHT)) {
			cam.turn(speed);
			//cam.orbitAroundPointHorizontal(orbitPoint, speed);
		}
		
		speed *= 5;
		
		if (input.isButtonDown(KeyEvent.VK_W)) {
			cam.moveForward(speed);
		}
		if (input.isButtonDown(KeyEvent.VK_S)) {
			cam.moveForward(-speed);
		}
		if (input.isButtonDown(KeyEvent.VK_A)) {
			cam.moveLeft(speed);
		}
		if (input.isButtonDown(KeyEvent.VK_D)) {
			cam.moveLeft(-speed);
		}
		
		if (input.isButtonDown(KeyEvent.VK_SHIFT) || input.isButtonDown(KeyEvent.VK_SPACE)) {
			cam.moveUp(speed);
		}
		if (input.isButtonDown(KeyEvent.VK_CONTROL)) {
			cam.moveUp(-speed);
		}
		
		if (cameraRotated) {
			cam.setYawAndPitch(newYaw, newPitch);
			cameraRotated = false;
		}
		if (cameraOrbit) {
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
		
		//Update moveArrows
		if (selected != null) {
			selected.moveArrows.setLocation(selected.getBoundingBox().getMiddle());
		}
	}
	
	@Override
	protected void render() {
		Graphics2D g = window.getGraphics2D();
		
		renderAxis(g);
		
		List<Renderable> transformed = projection.projectFaces(cube.getWorldSpaceFaces(lights));
		transformed.addAll(projection.projectFaces(smallCube.getWorldSpaceFaces(lights)));
		transformed.addAll(projection.projectFaces(obj.getWorldSpaceFaces(lights)));
		
		for (Light light : lights) {
			Point3D p = projection.project(light.getLocation(), true);
			if (p == null) {
				continue;
			}
			
			double size = projection.getProjectedSize(light.getLocation(), light.getSize());
			
			transformed.add(new Light(p, size));
		}
		
		transformed.sort(null);
		
		for (Renderable obj : transformed) {
			obj.render(g);
		}
		
		for (int i = 0; i < rays.size(); i++) { //to prevent concurrent modification exception
			Ray ray = rays.get(i);
			ray.render(g, projection);
		}
		
		synchronized (lock) {
			if (selected != null) {
				selected.renderSelected(g, projection);
				selected.moveArrows.render(g, projection);
			}
			
			if (hovering != null && hovering != selected) {
				hovering.renderHover(g, projection);
			}
		}
		
		window.display(g);
	}
	
	//Right hand rule, X is red (thumb, to right), Y is green (index, to up), Z is blue (middle, towards cam)
	private void renderAxis(Graphics2D g) {
		int axisLength = 10000;
		
		double pointSize = 5;
		
		Point3D start = new Point3D(0, 0, 0);
		
		Point3D xAxis = new Point3D(axisLength, 0, 0);
		Optional<LineSegment> x = projection.projectLineSegment(start, xAxis);
		
		if (x.isPresent()) {
			x.get().render(g, Color.red, pointSize, pointSize);
		}
		
		
		Point3D yAxis = new Point3D(0, axisLength, 0);
		Optional<LineSegment> y = projection.projectLineSegment(start, yAxis);
		
		if (y.isPresent()) {
			y.get().render(g, Color.green, pointSize, pointSize);
		}
		
		
		Point3D zAxis = new Point3D(0, 0, axisLength);
		Optional<LineSegment> z = projection.projectLineSegment(start, zAxis);
		
		if (z.isPresent()) {
			z.get().render(g, Color.blue, pointSize, pointSize);
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
		if (!initReady) {
			return;
		}
		
		Ray ray = createRay(x, y);
		if (renderRay) {
			rays.add(ray);
		}
		
		List<GameObjectAndDistance> objects = intersects(ray);
		if (!objects.isEmpty()) {
			selected = objects.get(0).gameObject;
			if (selected.moveArrows == null) {
				Point3D mid = selected.getBoundingBox().getMiddle();
				selected.moveArrows = new MoveArrows(mid);
			}
		} else {
			synchronized (lock) {
				selected = null;
			}
		}
	}
	
	private Ray createRay(int x, int y) {
		return createRay(x, y, DEFAULT_RAY_LENGTH);
	}
	
	private Ray createRay(int x, int y, double length) {
		Point3D start = cam.getLoc();
		Point3D rayAtNearPlane = ViewportTransformation.fromScreenSpaceToClipSpace(new Point2D(x, y), WIDTH, HEIGHT);
		rayAtNearPlane = Point3D.fromMatrixDivideByW(projection.fromClipSpaceToWorldSpace(rayAtNearPlane));
		Point3D dir = rayAtNearPlane.subtract(start).normalize();
		
		return new Ray(new LineSegment(start, start.add(dir.mult(length))));
	}
	
	public List<GameObjectAndDistance> intersects(Point3D start, Point3D dir, double rayLength) {
		return intersects(new Ray(start, dir, rayLength));
	}
	
	public List<GameObjectAndDistance> intersects(Ray ray) {
		List<GameObjectAndDistance> list = new ArrayList<>();
		
		List<HasBoundingBox> objects = getObjectsAsBoundingBoxes();
		for (HasBoundingBox object : objects) {
			BoundingBox bounds = object.getBoundingBox();
			BooleanAndDistance boolAndT = bounds.lineIntersection(ray.getStart(), ray.getEnd());
			if (boolAndT.bool) {
				if (object instanceof GameObject) {
					GameObject obj = (GameObject) object;
					list.add(new GameObjectAndDistance(obj, boolAndT.t));
				}
			}
		}
		
		list.sort(null);
		
		return list;
	}
	
	private List<HasBoundingBox> getObjectsAsBoundingBoxes() {
		List<HasBoundingBox> objects = new ArrayList<>();
		
		objects.add(cube);
		objects.add(smallCube);
		objects.add(obj);
		objects.addAll(Arrays.asList(lights));
		
		return objects;
	}
	
	public void focusSelected() {
		if (selected == null) {
			Point3D p = new Point3D();
			cam.lookAt(new Point3D());
			cam.setLoc(cam.getForward().negated().mult(700));
			cam.orbitPointDistance = p.distanceFrom(cam.getLoc());
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
	
	/**
	 * Checks if clicked location contains move arrows.
	 * Returns true if it did. Also sets the movingDirection to KeyInput.
	 * @param x
	 * @param y
	 * @param input
	 * @return 
	 */
	public boolean clickMoveSelected(int x, int y, KeyInput input) {
		if (selected == null) {
			return false;
		}
		
		Ray ray = createRay(x, y);
		
		MoveDirection direction = intersectsMoveDirection(ray, selected);
		
		if (direction == null) {
			return false;
		}
		
		input.startToMoveObject(direction);
		return true;
	}
	
	private MoveDirection intersectsMoveDirection(Ray ray, GameObject object) {
		Point3D mid = object.getBoundingBox().getMiddle();
		
		BoundingBox boxALL = object.moveArrows.getALLBoundingBox(mid);
		if (boxALL.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.ALL;
		}
		
		BoundingBox boxX = object.moveArrows.getXBoundingBox(mid);
		if (boxX.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.X;
		}
		
		BoundingBox boxY = object.moveArrows.getYBoundingBox(mid);
		if (boxY.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.Y;
		}
		
		BoundingBox boxZ = object.moveArrows.getZBoundingBox(mid);
		if (boxZ.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.Z;
		}
		
		
		BoundingBox boxXZ = object.moveArrows.getXZFaceBoundingBox(mid);
		if (boxXZ.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.XZ;
		}
		
		BoundingBox boxXY = object.moveArrows.getXYFaceBoundingBox(mid);
		if (boxXY.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.XY;
		}
		
		BoundingBox boxYZ = object.moveArrows.getYZFaceBoundingBox(mid);
		if (boxYZ.lineIntersection(ray.getStart(), ray.getDir(), ray.getLength()).bool) {
			return MoveDirection.YZ;
		}
		
		return null;
	}
	
	//TODO: Fixed the double movement directions (plane movement). But the line movement is not perfect, the box doesn't follow the mouse exactly, but it's pretty good now.
	public Point3D projectToMoveDirection(int x, int y, MoveDirection movingDirection) {
		if (selected == null) {
			return new Point3D(x, y, 0);
		}
		
		Point3D mid = selected.getBoundingBox().getMiddle();
		
		Ray ray = createRay(x, y);
		Point3D projected = new Point3D(x, y, 0);
		
		switch (movingDirection) {
			case X:
				//With one direction only, you can choose the plane. Let's calculate one that is towards the camera, but normal is perpendicular to the X axis
				//mid.subtract(cam.getLoc()) I thought about using this instead of forward vector, since it's more accurate towards cam, but when the object moves, the plane moves too.
				Point3D normal = cam.getLoc().copy();
				normal.x = 0;
				normal.normalize();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(mid, normal, ray.getStart(), ray.getDir());
				break;
			case Y:
				//With one direction only, you can choose the plane. Let's calculate one that is towards the camera, but normal is perpendicular to the Y axis
				normal = cam.getLoc().copy();
				normal.y = 0;
				normal.normalize();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(mid, normal, ray.getStart(), ray.getDir());
				break;
			case Z:
				//With one direction only, you can choose the plane. Let's calculate one that is towards the camera, but normal is perpendicular to the Z axis
				normal = cam.getLoc().copy();
				normal.z = 0;
				normal.normalize();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(mid, normal, ray.getStart(), ray.getDir());
				break;
			case XZ:
				//projected = HelperFunctions.intersectionPointWithPlaneInfinite(mid, Point3D.getY(), ray.getStart(), ray.getDir());
				Point3D p = selected.moveArrows.getXZFaceBoundingBox(mid).getMiddle();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(p, Point3D.getY(), ray.getStart(), ray.getDir());
				break;
			case XY:
				p = selected.moveArrows.getXYFaceBoundingBox(mid).getMiddle();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(p, Point3D.getZ(), ray.getStart(), ray.getDir());
				break;
			case YZ:
				p = selected.moveArrows.getYZFaceBoundingBox(mid).getMiddle();
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(p, Point3D.getX(), ray.getStart(), ray.getDir());
				break;
			case ALL:
				//Here we take from camera orientation
				projected = HelperFunctions.intersectionPointWithPlaneInfinite(mid, cam.getForward().negated(), ray.getStart(), ray.getDir());
				break;
		}
		
		return projected;
	}
	
	public void moveSelected(MoveDirection movingDirection, Point3D diff) {
		if (selected == null) {
			return;
		}
		
		switch (movingDirection) {
			case X:
				selected.moveLeft(diff.x);
				break;
			case Y:
				selected.moveUp(diff.y);
				break;
			case Z:
				selected.moveForward(diff.z);
				break;
			case XZ:
				selected.moveLeft(diff.x);
				selected.moveForward(diff.z);
				break;
			case XY:
				selected.moveLeft(diff.x);
				selected.moveUp(diff.y);
				break;
			case YZ:
				selected.moveUp(diff.y);
				selected.moveForward(diff.z);
				break;
			case ALL:
				selected.setLocation(selected.getLocation().add(diff));
				break;
		}
	}
	
	public void hover(int x, int y) {
		if (!initReady) {
			return;
		}
		
		Ray ray = createRay(x, y);
		
		if (selected != null) {
			MoveDirection direction = intersectsMoveDirection(ray, selected);
			if (direction != null) {
				hovering = selected.moveArrows;
				
				selected.moveArrows.setHoverDirection(direction);
				return;
			} else {
				synchronized (lock) {
					hovering = null;
				}
			}
		}
		
		List<GameObjectAndDistance> objects = intersects(ray);
		if (!objects.isEmpty()) {
			hovering = objects.get(0).gameObject;
		} else {
			synchronized (lock) {
				hovering = null;
			}
		}
	}

	public void windowResized(Dimension size) {
		WIDTH = size.width;
		HEIGHT = size.height;
		projection = new PerspectiveProjection(cam);
	}
}
