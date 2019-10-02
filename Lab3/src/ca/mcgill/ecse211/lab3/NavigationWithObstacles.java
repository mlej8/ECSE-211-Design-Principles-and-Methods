package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import ca.mcgill.ecse211.lab3.PController.State;
import static ca.mcgill.ecse211.lab3.PController.*;

public class NavigationWithObstacles implements Runnable {

	private static NavigationWithObstacles navigatorObstacle;

	/**
	 * {@code true} when robot is traveling.
	 */
	private static boolean traveling = false; // false by default

	/**
	 * Variable destination's x coordinate.
	 */
	private double destX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double destY;

	/**
	 * Variable storing current route.
	 */
	private static int[][] currentWaypoints;

	/**
	 * Navigation class implements the singleton pattern.
	 */
	private NavigationWithObstacles() {

	}

	/**
	 * Get instance of the Navigation class. Only allows one thread at a time
	 * calling this method.
	 */
	public synchronized static NavigationWithObstacles getNavigatorObstacle() {
		if (navigatorObstacle == null) {
			navigatorObstacle = new NavigationWithObstacles();
		}
		return navigatorObstacle;
	}

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
			// Set current destination's coordinates
			destX = waypoint[0] * TILE_SIZE;
			destY = waypoint[1] * TILE_SIZE;

			while (Math.abs(this.destX - odometer.getXYT()[0]) > ERROR_MARGIN
					|| Math.abs(this.destY - odometer.getXYT()[1]) > ERROR_MARGIN) {

				// Travel to next waypoint
				navigatorObstacle.travelTo(destX, destY);

				// Sleep while it is traveling
				while (navigatorObstacle.isNavigating()) {
					Main.sleepFor(10 * SLEEPINT);
				}
			}
		}
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
		traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		// Calculate the distance to waypoint
		double distance = Math.hypot(dx, dy);

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];

		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = 360.0 - theta;
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Turn to the correct angle
		turnTo(theta);

		// Turn on motor
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.rotate(convertDistance(distance), true);
		RIGHT_MOTOR.rotate(convertDistance(distance), false);

		// Once the destination is reached, stop both motors
		LEFT_MOTOR.stop(true);
		RIGHT_MOTOR.stop(true);
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
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		if (theta < 0) {
			// If angle is negative, turn left
			LEFT_MOTOR.rotate(convertAngle(theta), true);
			RIGHT_MOTOR.rotate(-convertAngle(theta), false);
		} else {
			// If angle is positive, turn right
			LEFT_MOTOR.rotate(convertAngle(theta), true);
			RIGHT_MOTOR.rotate(-convertAngle(theta), false);
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

	/**
	 * @return destination waypoint's x coordinate
	 */
	public double getDestX() {
		return this.destX;
	}

	/**
	 * @return destination waypoint's y coordinate
	 */
	public double getDestY() {
		return this.destY;
	}

	/**
	 * Getters method for traveling.
	 * 
	 * @return traveling
	 */
	public static boolean isTraveling() {
		return traveling;
	}

	/**
	 * Setter method for traveling.
	 * 
	 * @param traveling
	 */
	public static void setTraveling(boolean traveling) {
		NavigationWithObstacles.traveling = traveling;
	}
}
