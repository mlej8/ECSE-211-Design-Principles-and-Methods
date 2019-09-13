package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class BangBangController extends UltrasonicController {

  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {
    /**  
     * Perform an action based on the US data input.
     * 
     * @param distance -- the distance between the US sensor and an obstacle in cm.
     */
    
    filter(distance); // Rudimentary filter - toss out invalid samples corresponding to null signal and assigns distance value for current controller. 
    
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
    
    // instantiate variables 
    final int DELTASPD = MOTOR_HIGH - MOTOR_LOW;    // Bang-bang constant
    int error = this.distance - BAND_CENTER; // (distance between the US sensor and an obstacle in cm) - (Standard offset from the wall cm). We need to tweak BAND_CENTER and BAND_WIDTH in order to make the robot smooth 
    
    if (Math.abs(error) <= BAND_WIDTH) {
      LEFT_MOTOR.setSpeed(MOTOR_HIGH);
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    } else if (error > 0) { // if error is bigger than 0, means that current distance is too far from the obstacle, so we need to turn left
      LEFT_MOTOR.setSpeed(MOTOR_HIGH - DELTASPD); // reduce speed on left motor, in order to turn right 
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    } else if (error < 0) { // if error is smaller than 0, it means that current distance is too close from the obstacle, so we need to turn right 
      LEFT_MOTOR.setSpeed(MOTOR_LOW + DELTASPD);
      RIGHT_MOTOR.setSpeed(MOTOR_LOW);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
      }
      
  }
      
    
  

  @Override
  public int readUSDistance() {
    /**
     * Returns the distance between the US sensor and an obstacle in cm.
     * 
     * @return the distance between the US sensor and an obstacle in cm
     */
    return this.distance; 
  }
}
