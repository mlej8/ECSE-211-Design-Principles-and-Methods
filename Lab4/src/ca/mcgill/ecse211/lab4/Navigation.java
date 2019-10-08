package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

public class Navigation implements Runnable {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false; // false by default
	
	/**
	 * Variable destination's x coordinate.
	 */
	private double destX = 1 * TILE_SIZE; // set default destination to (1,1)

	/**
	 * Variable destination's y coordinate.
	 */
	private double destY = 1 * TILE_SIZE; // set default destination to (1,1)

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

	@Override
	public void run() {

		while (Math.abs(this.destX - odometer.getXYT()[0]) > ERROR_MARGIN
				|| Math.abs(this.destY - odometer.getXYT()[1]) > ERROR_MARGIN) {

			// Naigate to destination
			navigator.travelTo(navigator.destX, navigator.destY);

			// Sleep while it is traveling
			while (navigator.isNavigating()) {
				Main.sleepFor(10 * SLEEPINT);
			}
		}

	}
	
	/**
	 * Method that completely stops the robot from moving. 
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
	 * theta. This method should turn a MINIMAL angle to its target.
	 */
	public void turnTo(double theta) {
		
		// Set traveling to true when the robot is turning
		this.traveling = true;

		if (theta < 0) {
			// If angle is negative, turn left
			rotateRight(theta);
		} else {
			// If angle is positive, turn right
			rotateLeft(theta);
		}
	}

	/**
	 * Method that rotates the motor to the right.
	 */
	public void rotateRight(double theta) {		
		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), true);
	}
	
	public void rotateLeft(double theta) {
		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), true);
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
