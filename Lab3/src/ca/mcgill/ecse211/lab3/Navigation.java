package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

public class Navigation implements Runnable {

	private static Navigation navigator;

	/**
	 * Variable storing current route
	 */
	private static int[][] currentWaypoints;

	/**
	 * Navigation class implements the singleton pattern
	 */
	private Navigation() {

	}

	/**
	 * Get instance of the Navigation class. Only allows one thread at a time calling this method.
	 */
	public synchronized static Navigation getNavigator() {
		if (navigator == null) {
			navigator = new Navigation();
		}
		return navigator;
	}

	/**
	 * {@code true} when robot is traveling.
	 */
	private static boolean traveling = false; // false by default

	@Override
	public void run() {
		
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
			
			// Sleep until it ends traveling to traveling
			while (navigator.isNavigating()) {
				Main.sleepFor(10 * SLEEPINT);
			}
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
		
		// Calculate the distance to waypoint
		double distance = Math.hypot(dx, dy);

		System.out.println("Angle to destination: " + Math.toDegrees(Math.atan2(dx, dy)));
		
		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];
		
		// Find error
		System.out.println("dx: "+ dx + " dy: " + dy + " theta: " + theta );
		
		// Correct to turn using the minimal angle 
		if (theta > 180.0) {
			theta = 360.0 - theta; 
		} else if (theta < -180.0) {
			theta = 360 + theta;
		}
		
		System.out.println("Change theta: " + theta);
		
		// Turn to the correct angle
		turnTo(theta);

		// Turn on motor 
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
		
		if (theta < 0) {
			// If angle is negative, turn left
			leftMotor.rotate(convertAngle(theta), true);
			rightMotor.rotate(-convertAngle(theta), false);
		} else {
			// If angle is positive, turn right
			leftMotor.rotate(convertAngle(theta), true);
			rightMotor.rotate(-convertAngle(theta), false);
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
