package perspectiveprojection;

public class BooleanAndDistance {
	public boolean bool;
	public double t;
	
	public BooleanAndDistance(boolean bool) {
		this(bool, -1);
	}
	
	public BooleanAndDistance(boolean bool, double t) {
		this.bool = bool;
		this.t = t;
	}
}
