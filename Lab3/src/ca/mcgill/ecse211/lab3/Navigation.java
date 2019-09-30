package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

public class Navigation implements Runnable {

	private static Navigation navigator;

	/**
	 * Variable storing current route
	 */
	private static int[][] currentWaypoints;

	/**
	 * The possible states that the robot could be in.
	 */
	enum State {
		/** The initial state. */
		INIT,
		/** The turning state. */
		TURNING,
		/** The traveling state. */
		TRAVELING,
		/** The emergency state. */
		EMERGENCY
	};

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
	 * The current state of the robot.
	 */
	static State state;

	/**
	 * {@code true} when robot is traveling.
	 */
	public static boolean traveling = false; // false by default

	/**
	 * {@code true} when obstacle is avoided.
	 */
	public static boolean safe;

	/**
	 * The destination x.
	 */
	public static double destx;

	/**
	 * The destination y.
	 */
	public static double desty;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int selectedRoute = 1;
		switch (selectedRoute) {
		case 1:
			currentWaypoints = waypoints1;
			break;
		case 2:
			currentWaypoints = waypoints2;
			break;
		case 3:
			currentWaypoints = waypoints3;
			break;
		case 4:
			currentWaypoints = waypoints4;
			break;
		default:
			System.out.println("Please select a valid circuit");
			break;
		}
		for (int[] waypoint : currentWaypoints) {
			navigator.travelTo(waypoint[0]*TILE_SIZE, waypoint[1]*TILE_SIZE);
		}

	}

	private void travelTo(double x, double y) {
		/**
		 * This method causes the robot to travel to the absolute field location (x, y),
		 * specified in tile points. This method should continuously call turnTo(double
		 * theta) and then set the motor speed to forward (straight). This will make
		 * sure that your heading is updated until you reach your exact goal. This
		 * method will poll the odometer for information.
		 */
		// Traveling
		traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];
		double distance = Math.sqrt((dx * dx) + (dy * dy));

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];
		
		// Find error
		System.out.println("dx: "+ dx +" dy: " + " theta: " + theta );
		
		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal turning angle
		if (theta > 180.0) {
			theta = 360.0 - theta;
		}	else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Turn to the correct angle
		turnTo(theta);

		// Calculate the distance to waypoint
		leftMotor.setSpeed(MOTOR_SPEED);
		rightMotor.setSpeed(MOTOR_SPEED);
		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), false);

		// Once the destination is reached, stop both motors
		leftMotor.stop(true);
		rightMotor.stop(true);
		traveling = false;

	}

	private boolean isNavigating() {
		/**
		 * This method returns true if another thread has called travelTo() or turnTo()
		 * and the method has yet to return; false otherwise.
		 */
		return traveling;
	}

	private void turnTo(double theta) {
		/**
		 * This method causes the robot to turn (on point) to the absolute heading
		 * theta. This method should turn a MINIMAL angle to its target.
		 */
		// Set rotate speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		if (theta > 0) {
			// If angle is positive, turn right
			leftMotor.rotate(convertAngle(theta), true);
			rightMotor.rotate(-convertAngle(theta), false);
		} else {
			// If angle is negative, turn left
			leftMotor.rotate(-convertAngle(theta), true);
			rightMotor.rotate(convertAngle(theta), false);
		}
	}

	/**
	 * Converts input distance to the total rotation of each wheel needed to cover
	 * that distance.
	 * 
	 * @param distance
	 * @return the wheel rotations necessary to cover the distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the
	 * robot by that angle.
	 * 
	 * @param angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance(Math.PI * TRACK * angle / 360.0);
	}

}
