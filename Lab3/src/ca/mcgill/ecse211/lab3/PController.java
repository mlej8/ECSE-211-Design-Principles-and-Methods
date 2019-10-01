package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * This class implements the Wall Follower using Proportional Control controller
 * for Lab 1 on the EV3 platform
 * 
 * @author Michael Li and Cecilia Jiang
 */

public class PController extends UltrasonicController {

	public static enum State {
		INIT, // When robot has not encountered a block
		TURNING, // When robot is turning aroung a block
		FOLLOWING_WALL, // When robot has completed turning
		PASSED
	};

	public static State state = State.INIT;
	private static final int PROPORTION_GAIN_SCALE = 4;

	public PController() {
	}

	/**
	 * Perform an action based on the US data input using the proportional control
	 * scheme, where the gain is proportional to the error. The magnitude of change
	 * in rotation is proportional to the magnitude of the error. Scaling the
	 * correction, i.e. DELTASPD, according to error (this is called proportional
	 * control).
	 * 
	 * @param distance: the distance between the US sensor and an obstacle in cm.
	 */
	@Override
	public void processUSData(int distance) {

		filter(distance);

		System.out.println("Distance: " + distance);

		if (state == State.INIT && this.distance < THRESHOLD) {
			state = State.TURNING;

			// Log current state
			System.out.println("Switched state to TURNING");
			
		} else if (state == State.TURNING) {
			
			// Store variables 
			double x = odometer.getXYT()[0];
			double y = odometer.getXYT()[1];
			double theta = odometer.getXYT()[2];
			
			if (x > 2 * TILE_SIZE && y > 2 * TILE_SIZE) {
				// If robot is on the first quadrant, i.e. top right	
				
				if (theta >= CLOCKWISE_LOWER_BOUND && theta <= CLOCKWISE_UPPER_BOUND) {
				// If robot is traveling clockwise, turn right 
					sharpTurnRight();
				
				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND)  { 
				// If going counter clockwise, turn left
					sharpTurnLeft();
					}

			} else if (x < 2 * TILE_SIZE && y > 2 * TILE_SIZE) {
				// If robot is on the second quadrant, i.e. top left
				
				if (theta >= CLOCKWISE_LOWER_BOUND && theta <= CLOCKWISE_UPPER_BOUND) {
				// If robot is traveling clockwise, turn right 
					sharpTurnRight();
				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND) { 
				// If going counter clockwise, turn left
					sharpTurnLeft();
					}

			} else if (x < 2 * TILE_SIZE && y < 2 * TILE_SIZE) {
				// If robot is on the third quadrant, i.e. bottom left
				
				if (theta >= CLOCKWISE_LOWER_BOUND && theta <= CLOCKWISE_UPPER_BOUND) {
				// If robot is traveling clockwise, turn left
					sharpTurnLeft();
				
				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND) { 
				// If going counter clockwise, turn right
					sharpTurnRight();
					}

			} else if (x > 2 * TILE_SIZE && y < 2 * TILE_SIZE) { 
				// If robot is on the forth quadrant, i.e. bottom right
				
				if (theta >= CLOCKWISE_LOWER_BOUND && theta <= CLOCKWISE_UPPER_BOUND) {
				// If robot is traveling clockwise, turn right 
					sharpTurnRight();
				
				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND) { 
				// If going counter clockwise, turn left
					sharpTurnLeft();	
					}
			}
				
				// Log current state
				System.out.println("Navigation state switched to FOLLOWING_WALL");

				// Switch state to following wall
				state = State.FOLLOWING_WALL;

		} else if (state == State.FOLLOWING_WALL) {

			while (odometer.getXYT()[2] <= navigatorObstacle.getAngle() + DEG_ERR  && navigatorObstacle.getAngle() - DEG_ERR >= odometer.getXYT()[2]) {
				// TOOD: FIX getAngle()
				// Compute error
				int error = BAND_CENTER - this.distance; // (distance between the US sensor and an obstacle in cm) -
															// (Standard offset from the wall cm). We need to tweak
															// BAND_CENTER and BAND_WIDTH in order to make the robot
															// smooth.

				// Compute low and high speed using the calcGain function.
				int lowSpeed = MOTOR_SPEED - calcGain(error);
				int highSpeed = MOTOR_SPEED + calcGain(error);

				if (Math.abs(error) <= BAND_WIDTH) {
					forward();
				} else if (error > 0) {
					turnRight(highSpeed);
				} else if (error < 0) {
					turnLeft(lowSpeed, highSpeed);
				}
			}
			// Switch state back to INIT
			state = State.PASSED;
			
			// Rotate sensor back to looking forward
			rotateMotor.rotate(-NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE));

		} else if (state == State.PASSED) {
			// Turn sensor 90 degrees to focus in front of the robot
			// TODO: Rotate sensor back to 0 
			// Change state back to INIT
			state = State.INIT;
		}
	}

	private static void forward() {
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
	}

	private static void turnRight(int speed) {
		// if error is bigger than 0, this means that the vehicle is too close from the
		// wall which means we need to turn right
		LEFT_MOTOR.setSpeed(speed);
		RIGHT_MOTOR.setSpeed(speed);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.backward();
	}

	private static void turnLeft(int lowSpeed, int highSpeed) {
		// if error is smaller than 0, this means that the vehicle is too far from the
		// wall, which
		// means we need to turn left
		LEFT_MOTOR.setSpeed(lowSpeed);
		RIGHT_MOTOR.setSpeed(highSpeed);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
	}

	/**
	 * Returns the distance between the US sensor and an obstacle in cm.
	 * 
	 * @return the distance between the US sensor and an obstacle in cm
	 */
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	/**
	 * Calculates and returns the proportional gain according to proportion control
	 * scheme A correction is applied to the controlled variable (gain) which is
	 * corresponds to the difference between the offset and the measured measure
	 * distance. The gain constant is proportional to the error (r - y). This gain
	 * will then be used to correct the rotation of the EV3 Robot.
	 * 
	 * @return proportional gain
	 */
	private static int calcGain(int error) {

		// Filter out error
		if (error < -200) {
			error = -200;
		}

		// Proportional control: Scaling the correction, i.e. DELTASPD, according to
		// error
		error = Math.abs(error);

		// Compute correction using ERROR_ROTATION SCALE
		int DELTASPD = error * PROPORTION_GAIN_SCALE;

		// Set a maximum correction, filter out corrections that are too big to an upper
		// bound
		if (DELTASPD > 55) {
			DELTASPD = 55;
		}
		return DELTASPD;
	}
	
	/**
	 * Method that makes a sharp turn right and turns the sensor left to face the wall.
	 */
	private static void sharpTurnRight() {
		
		// Set rotate speed 
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		// Turn right
		LEFT_MOTOR.rotate(NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE), true);
		RIGHT_MOTOR.rotate(-NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE), false);

		// Turn sensor 90 degrees to the left to face the wall
		rotateMotor.rotate(NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE));
	}
	
	/**
	 * Method that makes a sharp turn left and turns the sensor right to face the wall.
	 */
	private static void sharpTurnLeft() {
		
		// Set rotate speed 
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		// Turn left
		LEFT_MOTOR.rotate(-NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE), true);
		RIGHT_MOTOR.rotate(NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE), false);

		// Turn sensor 90 degrees to the right to face the wall
		rotateMotor.rotate(-NavigationWithObstacles.convertAngle(EMERGENCY_TURN_ANGLE));
	}
}
