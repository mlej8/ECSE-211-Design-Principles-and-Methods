package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  private static final int MOTOR_SPEED = 200;

  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  /**  
   * Perform an action based on the US data inpu using the proportional control scheme, where the gain is proportional to the error.
   * The magnitude of change in rotation is proportional to the magnitude of the error.
   *  
   * @param distance -- the distance between the US sensor and an obstacle in cm.
   */
  @Override
  public void processUSData(int distance) {
    filter(distance);
    // TODO: process a movement based on the us distance passed in (P style)
    int error = this.distance - BAND_CENTER; // (distance between the US sensor and an obstacle in cm) - (Standard offset from the wall cm). We need to tweak BAND_CENTER and BAND_WIDTH in order to make the robot smooth 
    
    calcGain(error);
    // scale the correction, i.e. DELTASPD, according to error (this is called proportional control).
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
   * Calculates the proportional gain 
   * 
   * @return proportional gain
   */
  
  private static int calcGain(int error) {
    return error*5;
  }
}
