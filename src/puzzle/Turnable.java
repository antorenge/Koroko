package puzzle;

import java.awt.Point;

public interface Turnable {
	
	/*
	public void turnRadians(Point turnPoint, double radians);
	*/
	
	/**
	 * turns this for given degree's around the given point. 
	 */
	public void turnDegrees(Point turnPoint, int degree);

}
