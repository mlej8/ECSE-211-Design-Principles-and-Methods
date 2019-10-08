package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

import lejos.hardware.Sound;

/**
 * Class that uses the ultrasonic sensor to orient itself assuming the robot
 * will start with its center of rotation in the bottom left tile of the field.
 * Its initial orientation (heading) is assumed to be unknown. angles will be
 * measured clockwise from the positive y-axis, as seen on the right.
 */
public class UltrasonicLocalizer extends UltrasonicController {
	
	/**
	 * Method that orients the robot by detecting rising edges
	 */
	public void risingEdge() {
		
		// rising edge is the point at which the measured distances rises above d + NOISE_MARGIN
		double backWall, leftWall, correctionAngle;

		// Use the ultrasonic sensor to measure the distance to the two walls nearest
		// the robot to determine the initial orientation of the robot by rotating 360
		// degrees on itself.
		
		backWall = findRisingEdgeA();
		leftWall = findRisingEdgeB();
		System.out.println(backWall + leftWall);
		
		// Get the angle to be added to the heading reported by the odometer to orient the robot correctly
		correctionAngle = getCorrectionAngle(backWall, leftWall);
		
		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (correctionAngle > 180.0) {
			correctionAngle = 360.0 - correctionAngle;
		} else if (correctionAngle < -180.0) {
			correctionAngle = 360.0 + correctionAngle;
		}
		System.out.println("Correction Angle: " + correctionAngle);
		
		// Rotate to correct the orientation
		navigator.turnTo(correctionAngle);
		
	}

	/**
	 * Method that orients the robot by detecting falling edges
	 */
	public void fallingEdge() {
		
		// falling edge is the point at which the mesured distance is smaller than d - NOISE_MARGIN
		double backWall, leftWall, correctionAngle;

		// Use the ultrasonic sensor to measure the distance to the two walls nearest
		// the robot to determine the initial orientation of the robot by rotating 360
		// degrees on itself.
		
		// Get angle at which the back and left walls are detected.
		backWall = findFallingEdgeA();
		leftWall = findFallingEdgeB();
		
		// Get the angle to be added to the heading reported by the odometer to orient the robot correctly
		correctionAngle = getCorrectionAngle(backWall, leftWall);
		
		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (correctionAngle > 180.0) {
			correctionAngle = 360.0 - correctionAngle;
		} else if (correctionAngle < -180.0) {
			correctionAngle = 360.0 + correctionAngle;
		}
		
		System.out.println("Correction Angle: " + correctionAngle);
		
		// Rotate to correct the orientation
		navigator.turnTo(correctionAngle);
		
	}
	
	/**
	 * Method that returns the angle at which the back wall is detected for the falling edge implementation 
	 * @return average of the angle at which the robot enter the noise margin and the angle at which the falling edge is detected.
	 */
	private double findFallingEdgeA() {
		
			double enteredNoiseMargin, fallingEdge;
		
			// Turn right until the robot enters noise margin for the back wall.
			while (readUSDistance() > d + NOISE_MARGIN) {
				System.out.println(this.distance);			
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
			
			return ((fallingEdge + enteredNoiseMargin)/2);
	}

	/**
	 * Method that returns the angle at which the left wall is detected for the falling edge implementation 
	 * @return average of the angle at which the robot enter the noise margin and the angle at which the falling edge is detected.
	 */
	private double findFallingEdgeB() {
		
		double enteredNoiseMargin, fallingEdge;
	
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
			navigator.rotateLeft(ROTATION_LEFT);
		}			
		
		// Stop robot when it detects the falling edge.
		navigator.stop();	
		
		// Make a sound when falling edge is detected.
		Sound.beep();
			
		// Store the angle at which the falling edge is detected.
		fallingEdge = odometer.getXYT()[2];
		
		return ((fallingEdge + enteredNoiseMargin)/2);
	}	
		
	/**
	 * Method that returns the angle at which the back wall is detected for the rising edge implementation 
	 * @return average of the angle at which the robot enter the noise margin and the angle at which the rising edge is detected.
	 */
	private double findRisingEdgeA() {
		
			double enteredNoiseMargin, risingEdge;
		
			// Turn right until the robot enters noise margin for the back wall.
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
			
			// Turn right until the robot detects the rising edge for the back wall.
			while (readUSDistance() < d + NOISE_MARGIN) {
				navigator.rotateLeft(ROTATION_LEFT);
			}	
			
			// Stop robot when it detects the rising edge.
			navigator.stop();	
			
			// Make a sound when rising edge is detected.
			Sound.beep();
				
			// Store the angle at which the rising edge is detected.
			risingEdge = odometer.getXYT()[2];
			
			return ((risingEdge + enteredNoiseMargin)/2);
	}

	/**
	 * Method that returns the angle at which the left wall is detected for the rising edge implementation 
	 * @return average of the angle at which the robot enter the noise margin and the angle at which the rising edge is detected.
	 */
	private double findRisingEdgeB() {
		
			double enteredNoiseMargin, risingEdge;
		
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
			
			return ((risingEdge + enteredNoiseMargin)/2);
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
		if (a < b) {
			return (45 - ((a + b) / 2));
		} else {
			return (225 - ((a + b) / 2));
		}
	}
}
