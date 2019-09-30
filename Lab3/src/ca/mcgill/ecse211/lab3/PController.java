package ca.mcgill.ecse211.lab3;


import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * This class implements the Wall Follower using Proportional Control controller for Lab 1 on the EV3 platform
 * 
 * @author Michael Li and Cecilia Jiang
 */
public class PController extends UltrasonicController {

  private static final int PROPORTION_GAIN_SCALE = 4;

  public PController() {
    leftMotor.setSpeed(MOTOR_SPEED);
    rightMotor.setSpeed(MOTOR_SPEED);
    leftMotor.forward();
    rightMotor.forward();
  }

  /**
   * Perform an action based on the US data input using the proportional control scheme, where the gain is proportional
   * to the error. The magnitude of change in rotation is proportional to the magnitude of the error. Scaling the
   * correction, i.e. DELTASPD, according to error (this is called proportional control).
   * 
   * @param distance: the distance between the US sensor and an obstacle in cm.
   */
  @Override
  public void processUSData(int distance) {
    filter(distance);

    int error = BAND_CENTER - this.distance; // (distance between the US sensor and an obstacle in cm) - (Standard
                                             // offset from the wall cm). We need to tweak BAND_CENTER and
                                             // BAND_WIDTH in order to make the robot smooth.

    // Compute low and high speed using the calcGain function.
    int lowSpeed = MOTOR_SPEED - calcGain(error);
    int highSpeed = MOTOR_SPEED + calcGain(error);

    if (Math.abs(error) <= BAND_WIDTH) {
      leftMotor.setSpeed(MOTOR_SPEED);
      rightMotor.setSpeed(MOTOR_SPEED);
      leftMotor.forward();
      rightMotor.forward();
    } else if (error > 0) { // if error is bigger than 0, this means that the vehicle is too close from the
                            // wall which
                            // means we need to turn right
      leftMotor.setSpeed(highSpeed);
      rightMotor.setSpeed(highSpeed);
      leftMotor.forward();
      rightMotor.backward();
    } else if (error < 0) { // if error is smaller than 0, this means that the vehicle is too far from the
                            // wall, which
                            // means we need to turn left
      leftMotor.setSpeed(lowSpeed);
      rightMotor.setSpeed(highSpeed);
      leftMotor.forward();
      rightMotor.forward();
    }
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
   * Calculates and returns the proportional gain according to proportion control scheme A correction is applied to the
   * controlled variable (gain) which is corresponds to the difference between the offset and the measured measure
   * distance. The gain constant is proportional to the error (r - y). This gain will then be used to correct the
   * rotation of the EV3 Robot.
   * 
   * @return proportional gain
   */
  private static int calcGain(int error) {

    // Filter out error
    if (error < -200) {
      error = -200;
    }

    // Proportional control: Scaling the correction, i.e. DELTASPD, according to error
    error = Math.abs(error);

    // Compute correction using ERROR_ROTATION SCALE
    int DELTASPD = error * PROPORTION_GAIN_SCALE;

    // Set a maximum correction, filter out corrections that are too big to an upper bound
    if (DELTASPD > 55) {
      DELTASPD = 55;
    }
    return DELTASPD;
  }
}
