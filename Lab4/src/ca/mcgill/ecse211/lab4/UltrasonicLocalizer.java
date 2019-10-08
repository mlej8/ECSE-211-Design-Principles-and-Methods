package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

import lejos.hardware.Sound;

/**
 * Class that uses the ultrasonic sensor to orient itself assuming the robot
 * will start with its center of rotation in the bottom left tile of the field.
 * Its initial orientation (heading) is assumed to be unknown. Angles will be
 * measured clockwise w.r.t. the positive y-axis, as seen on the right.
 * Use the ultrasonic sensor to measure the distance to the two walls nearest
 * the robot to determine the initial orientation of the robot by rotating 360
 * degrees on itself.
 */
public class UltrasonicLocalizer extends UltrasonicController {

	/**
	 * Method that orients the robot by detecting falling edges.
	 */
	public void fallingEdge() {
		
		// falling edge is the point at which the mesured distance is smaller than d -
		// NOISE_MARGIN
		double backWall, leftWall, correctionAngle;
		
		// Get angle at which the back and left walls are detected.
		backWall = findFallingEdgeA();
		leftWall = findFallingEdgeB();
		
		// Get the angle to be added to the heading reported by the odometer to orient
		// the robot correctly
		correctionAngle = getCorrectionAngle(backWall, leftWall);
		
		// Correct current orientation reported by odometer
		System.out.println("Correction angle: " + correctionAngle);
		System.out.println("Current orientation: " + odometer.getXYT()[2]);
		
		// Correct odometer's current orientation
		odometer.setTheta(correctionAngle + odometer.getXYT()[2]);
		
		System.out.println("Current orientation after correction: " + odometer.getXYT()[2]);
 	
		// Turn to 0 degrees w.r.t. the Y-axis.
		navigator.turnTo(360 - odometer.getXYT()[2]);
		System.out.println("Final Angle: " + odometer.getXYT()[2]);
	}
	
	/**
	 * Method that orients the robot by detecting rising edges
	 */
	public void risingEdge() {

		// rising edge is the point at which the measured distances rises above d +
		// NOISE_MARGIN
		double backWall, leftWall, correctionAngle;

		backWall = findRisingEdgeA();
		leftWall = findRisingEdgeB();

		// Get the angle to be added to the heading reported by the odometer to orient
		// the robot correctly
		correctionAngle = getCorrectionAngle(backWall, leftWall);
		
		// Correct current odometer's orientation
		odometer.setTheta(odometer.getXYT()[2] + correctionAngle);
		
		System.out.println("Current Angle");
		
		if (odometer.getXYT()[2] > 180) {
			navigator.turnTo(360 - odometer.getXYT()[2]);
		} else {
			navigator.turnTo(0 - odometer.getXYT()[2]);
		}

		// Rotate to correct the orientation
//		navigator.turnTo(correctionAngle);

	}


	/**
	 * Method that returns the angle at which the back wall is detected for the
	 * falling edge implementation
	 * 
	 * @return average of the angle at which the robot enters the noise margin and
	 *         the angle at which the falling edge is detected.
	 */
	private double findFallingEdgeA() {

		double enteredNoiseMargin, fallingEdge;

		// Rotate counter clockwise until the robot is out of the noise margin zone.
		while (readUSDistance() < d + NOISE_MARGIN) {
			navigator.rotateLeft(ROTATION_LEFT);
		}

		// Turn right until the robot enters noise margin for the back wall.
		while (readUSDistance() > d + NOISE_MARGIN) {
			navigator.rotateRight(ROTATION_RIGHT);
		}

		// Stop robot when it enters noise margin
		navigator.stop();

		// Make a sound when the robot enter noise margin.
		Sound.beep();

		// Store the angle at which Sthe robot enter noise margin.
		enteredNoiseMargin = odometer.getXYT()[2];

		// Turn right until the robot detects the falling edge for the back wall.
		while (readUSDistance() > d - NOISE_MARGIN) {
			navigator.rotateRight(ROTATION_RIGHT);
		}

		// Stop robot when it detects the falling edge.
		navigator.stop();

		// Make a sound when falling edge is detected.
		Sound.beep();

		// Store the angle at which the falling edge is detected.
		fallingEdge = odometer.getXYT()[2];

		return ((fallingEdge + enteredNoiseMargin) / 2);
	}

	/**
	 * Method that returns the angle at which the left wall is detected for the
	 * falling edge implementation
	 * 
	 * @return average of the angle at which the robot enter the noise margin and
	 *         the angle at which the falling edge is detected.
	 */	
	private double findFallingEdgeB() {

		double enteredNoiseMargin, fallingEdge;

		// Rotate counterclockwise to get out of noise margin.
		while (readUSDistance() < d + NOISE_MARGIN) {
			navigator.rotateLeft(ROTATION_LEFT);
		}

		// Turn left until the robot enters noise margin for the left wall.
		while (readUSDistance() > d + NOISE_MARGIN) {
			System.out.println(this.distance);
			navigator.rotateLeft(ROTATION_LEFT);
		}

		// Stop robot when it enters noise margin
		navigator.stop();

		// Make a sound when the robot enter noise margin.
		Sound.beep();

		// Store the angle at which the robot enter noise margin.
		enteredNoiseMargin = odometer.getXYT()[2];
		
		// Turn left until the falling edge is detected for the left wall.
		while (readUSDistance() > d - NOISE_MARGIN) {
			System.out.println(this.distance);
			navigator.rotateLeft(ROTATION_LEFT);
		}

		// Stop robot when it detects the falling edge.
		navigator.stop();

		// Make a sound when falling edge is detected.
		Sound.beep();

		// Store the angle at which the falling edge is detected.
		fallingEdge = odometer.getXYT()[2];
		
		return ((fallingEdge + enteredNoiseMargin) / 2);
	}

	/**
	 * Method that returns the angle at which the back wall is detected for the
	 * rising edge implementation
	 * 
	 * @return average of the angle at which the robot enter the noise margin and
	 *         the angle at which the rising edge is detected.
	 */
	private double findRisingEdgeA() {

		double enteredNoiseMargin, risingEdge;
		
		// Turn right until the robot is facing the wall 
		while (readUSDistance() > d - NOISE_MARGIN) {
			navigator.rotateRight(ROTATION_RIGHT);
		}

		// Turn right left the robot enters noise margin for the back wall.
		while (readUSDistance() < d - NOISE_MARGIN) {
			System.out.println(this.distance);
			navigator.rotateLeft(ROTATION_LEFT);
		}
		
		// Stop robot when it enters noise margin
		navigator.stop();

		// Make a sound when the robot enter noise margin.
		Sound.beep();

		// Store the angle at which the robot enter noise margin.
		enteredNoiseMargin = odometer.getXYT()[2];

		// Turn left until the robot detects the rising edge for the back wall.
		while (readUSDistance() < d + NOISE_MARGIN) {
			navigator.rotateLeft(ROTATION_LEFT);
		}

		// Stop robot when it detects the rising edge.
		navigator.stop();

		// Make a sound when rising edge is detected.
		Sound.beep();

		// Store the angle at which the rising edge is detected.
		risingEdge = odometer.getXYT()[2];

		return ((risingEdge + enteredNoiseMargin) / 2);
	}

	/**
	 * Method that returns the angle at which the left wall is detected for the
	 * rising edge implementation
	 * 
	 * @return average of the angle at which the robot enter the noise margin and
	 *         the angle at which the rising edge is detected.
	 */
	private double findRisingEdgeB() {

		double enteredNoiseMargin, risingEdge;
		
		// Turn right until the robot is under distance + NOISE_MARGIN of the back wall 
		while (readUSDistance() > d - NOISE_MARGIN) {
			System.out.println(this.distance);
			navigator.rotateRight(ROTATION_RIGHT);
		}
		
		// Turn right until the robot enters noise margin for the left wall.
		while (readUSDistance() < d - NOISE_MARGIN) {
			System.out.println(this.distance);
			navigator.rotateRight(ROTATION_RIGHT);
		}
		
		// Stop robot when it enters noise margin
		navigator.stop();

		// Make a sound when the robot enter noise margin.
		Sound.beep();

		// Store the angle at which the robot enter noise margin.
		enteredNoiseMargin = odometer.getXYT()[2];

		// Turn right until the robot detects the rising edge for the left wall.
		while (readUSDistance() < d + NOISE_MARGIN) {
			navigator.rotateRight(ROTATION_RIGHT);
		}

		// Stop robot when it detects the rising edge.
		navigator.stop();

		// Make a sound when rising edge is detected.
		Sound.beep();

		// Store the angle at which the rising edge is detected.
		risingEdge = odometer.getXYT()[2];

		return ((risingEdge + enteredNoiseMargin) / 2);
	}

	@Override
	public void processUSData(int distance) {
		filter(distance);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

	/**
	 * Method that computes the angle to be added to the heading reported by the
	 * odometer to orient the robot correctly.
	 * 
	 * @param a: angle at which the back wall is detected.
	 * @param b: angle at which the left wall is detected.
	 * @return
	 */
	public double getCorrectionAngle(double a, double b) {
		System.out.println("a: " + a + " b: " + b);
		if (a < b) {
			return (45 - ((a + b) / 2.0));
		} else {
			return (222.5 - ((a + b) / 2.0)); // bigger value, smaller turn
		}
	}
}
