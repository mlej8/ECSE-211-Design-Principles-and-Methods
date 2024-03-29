package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

/**
 * This class implements the Wall Follower using Bang-Bang controller for Lab 1 on the EV3 platform
 * 
 * @author Michael Li and Cecilia Jiang
 */
public class BangBangController extends UltrasonicController {

  private static final int DELTASPD_BANGBANG = 30;
  private static final int SHARP_TURN = 55;

  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED);
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  /**
   * Perform an action based on the US data input in (BANG-BANG style).
   * 
   * @param distance -- the distance between the US sensor and an obstacle in cm
   */
  @Override
  public void processUSData(int distance) {
    filter(distance); // Rudimentary filter - toss out invalid samples corresponding to null signal
                      // and assigns distance
                      // value for current controller.

    int error = BAND_CENTER - this.distance; // (distance between the US sensor and an obstacle in cm) - (Standard
                                             // offset from the wall cm). We need to tweak BAND_CENTER and
                                             // BAND_WIDTH in
                                             // order to make the robot smooth

    if (Math.abs(error) <= BAND_WIDTH) { // if the error is smaller than threshold, continue forward.
      LEFT_MOTOR.setSpeed(MOTOR_SPEED);
      RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    } else {
      if (error > 12) { // SHARP TURN RIGHT
        LEFT_MOTOR.setSpeed(MOTOR_SPEED);
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + SHARP_TURN);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.backward();
      } else if (error < -12) { // SHARP TURN LEFT 14
        LEFT_MOTOR.setSpeed(MOTOR_SPEED - SHARP_TURN);
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + SHARP_TURN);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      } else if (error > 0) { // if error is bigger than 0, it means that current distance is too close from the
                              // obstacle,so we need to turn right
        LEFT_MOTOR.setSpeed(MOTOR_SPEED + DELTASPD_BANGBANG);
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED - DELTASPD_BANGBANG);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      } else if (error < 0) { // if error is smaller than 0, means that current distance is too far from the obstacle,
                              // so we need to turn left by reducing speed on left motor, in order to turn right
        LEFT_MOTOR.setSpeed(MOTOR_SPEED - DELTASPD_BANGBANG); 
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + DELTASPD_BANGBANG);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      }
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
}
