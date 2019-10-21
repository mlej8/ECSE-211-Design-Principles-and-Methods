package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Navigation {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false; // false by default

	/**
	 * Variable destination's x coordinate.
	 */
	private double destX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double destY;

	/**
	 * Origin (1,1) coordinates.
	 */
	private static double originX = 1 * TILE_SIZE;
	private static double originY = 1 * TILE_SIZE;
	
	/**
	 * Navigation class implements the singleton pattern
	 */
	private Navigation() {

	}

	/**
	 * Get instance of the Navigation class. Only allows one thread at a time
	 * calling this method.
	 */
	public synchronized static Navigation getNavigator() {
		if (navigator == null) {
			navigator = new Navigation();
		}
		return navigator;
	}

	/**
	 * Method that travels to the origin (1,1).
	 */
	public void travelToOrigin() {

			// Navigate to origin (1,1)
			navigator.travelTo(originX, originY);

			// Sleep while it is traveling
			while (navigator.isNavigating()) {
				Main.sleepFor(10 * SLEEPINT);
			}
	}

	/**
	 * Find current robot position after ultrasonic localization by detecting the
	 * grid lines assuming the robot is currently on the diagonal line (45 degrees)
	 * of a tile.
	 */
	public void findRobotPosition() {

		// Set robot speeds
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);

		// Reset tacho counts
		LEFT_MOTOR.resetTachoCount();
		RIGHT_MOTOR.resetTachoCount();

		// Travel forward until a line is detected
		while (!lightLocalizer.getLineTouched()) {
			LEFT_MOTOR.forward();
			RIGHT_MOTOR.forward();
		}

		// Stop robot once it detects the black line
		stop();

		// Note the distance it traveled
		int tachCountLeft = LEFT_MOTOR.getTachoCount();
		int tachCountRight = RIGHT_MOTOR.getTachoCount();

		// Go Back to starting position
		LEFT_MOTOR.rotate(-tachCountLeft, true);
		RIGHT_MOTOR.rotate(-tachCountRight, false);

		// Position of the center of rotation
		double distToGridLine = Math.PI * WHEEL_RAD * (tachCountLeft) / 180 - DIST_CENTRE_TO_LIGHT_SENSOR;

		// Assuming the robot is on the diagonalize line of the tile, the horizontal
		// distance is equal to the vertical distance
		odometer.setX(TILE_SIZE - distToGridLine);
		odometer.setY(TILE_SIZE - distToGridLine);
		lightLocalizer.setlocalizerStarted(true);
	}

	/**
	 * Method that stops the robot completely.
	 */
	public void stop() {
		LEFT_MOTOR.stop(true);
		RIGHT_MOTOR.stop(false);
	}

	public void travelTo(double x, double y) {
		/**
		 * This method causes the robot to travel to the absolute field location (x, y),
		 * specified in tile points. This method should continuously call turnTo(double
		 * theta) and then set the motor speed to forward (straight). This will make
		 * sure that your heading is updated until you reach your exact goal. This
		 * method will poll the odometer for information.
		 */
		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		// Calculate the distance to waypoint
		double distance = Math.hypot(dx, dy);

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];

		// Turn to the correct angle
		turnTo(theta);

		// Turn on motor
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.rotate(Converter.convertDistance(distance), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), false);

		// Once the destination is reached, stop both motors
		stop();
		this.traveling = false;
	}

	/**
	 * This method returns true if another thread has called travelTo() or turnTo()
	 * and the method has yet to return; false otherwise.
	 */
	public boolean isNavigating() {
		return this.traveling;
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn using the MINIMAL angle to its target.
	 */
	public void turnTo(double theta) {

		// Set traveling to true when the robot is turning
		this.traveling = true;
		
		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = 360.0 - theta;
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Set motors' speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);
	}

	/**
	 * Method that rotates continuously (without waiting for the move to complete)
	 * according to the input theta.
	 * 
	 * @param theta that is the angle at which the robot needs to turn
	 */
	public void rotate(double theta) {
		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);
	}
	
	/**
	 * This method uses the given target position (targetX,targetY) to find the ideal launching
	 * position. x and y are in unit cm. 
	 * 
	 * @param targetX
	 * @param targetY
	 */
	public void findDest(double targetX, double targetY) {
	  double currentX = odometer.getXYT()[0];
	  double currentY = odometer.getXYT()[1];
	  Point2d curPosition = new Point2d(currentX, currentY);
	  Point2d throwTo = new Point2d(targetX, targetY);
	  
	  // let current position and the target position constructs a linear equation: targetY = mx+b
	  double m = (targetY-currentY)/(targetX-currentX);
	  double b = targetY - m*targetX;
	  
	  Vector2d yAxis = new Vector2d(0,1);
	  Vector2d trajectory = new Vector2d((currentY-targetY), (currentX-targetX));
	  double theta = yAxis.angle(trajectory);
	  
	  double launchX, launchY;
	  double dx,dy;
	  // calculate the intersection of the circle and the line
	  if(currentY - targetY > 0) { // when the robot is in 1st/2nd quadrant
	    dy = launchRange * Math.cos(theta);
	    dx = launchRange * Math.sin(theta);
	    launchY = targetY + dy;
	    launchX = targetX + dx;
	  } else {  // in 3rd/4th quadrant
	    dy = - launchRange * Math.cos(theta);
	    dx = - launchRange * Math.sin(theta);
	    launchY = targetY + dy;
        launchX = targetX + dx; // TODO: test later
	  }
	  
	  if(dy <= 15 || dx <= 15) {
	    Point2d target = findCircle(curPosition, throwTo);
	    launchX = target.x;
	    launchY = target.y;
	  }
	  System.out.println("I am going to X position: " + launchX + " Y position: " + launchY);
	  travelTo(launchX, launchY);
	}
	
	private static Point2d findCircle (Point2d curPos, Point2d center) {
	  Point2d target = new Point2d();
	  if(center.x > center.y) { // upper half
	    double tX = center.x;
	    double tY = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos.x - center.x),2)) + center.y;
	    target = new Point2d(tX, tY);
	  }else {  // lower half
	    double tY = curPos.y;
	    double tX = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos.y - center.y),2)) + center.x;
	    target = new Point2d(tX, tY);
	  }
	  return target;
	}

	/**
	 * @return destination waypoint's x coordinate.
	 */
	public double getDestX() {
		return this.destX;
	}

	/**
	 * @return destination waypoint's y coordinate.
	 */
	public double getDestY() {
		return this.destY;
	}

	/**
	 * Getters method for traveling.
	 * 
	 * @return traveling
	 */
	public boolean isTraveling() {
		return this.traveling;
	}

	/**
	 * Setter method for traveling.
	 * 
	 * @param traveling
	 */
	public void setTraveling(boolean traveling) {
		this.traveling = traveling;
	}

}
