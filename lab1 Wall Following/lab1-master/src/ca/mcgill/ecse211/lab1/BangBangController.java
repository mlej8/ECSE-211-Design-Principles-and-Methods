package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class BangBangController extends UltrasonicController {

  private final int DELTA_SPEED=MOTOR_HIGH-MOTOR_LOW;

  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {
     filter(distance);
     int offset = BAND_CENTER-this.distance;
     if(Math.abs(offset)>BAND_WIDTH){
       if(offset>0){
         LEFT_MOTOR.setSpeed(MOTOR_HIGH+DELTA_SPEED);
         RIGHT_MOTOR.setSpeed(0);
         LEFT_MOTOR.forward();
         RIGHT_MOTOR.forward();
       }else{
         LEFT_MOTOR.setSpeed(0);
         RIGHT_MOTOR.setSpeed(MOTOR_HIGH+DELTA_SPEED));
         LEFT_MOTOR.forward();
         RIGHT_MOTOR.forward();
       }
     }
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
