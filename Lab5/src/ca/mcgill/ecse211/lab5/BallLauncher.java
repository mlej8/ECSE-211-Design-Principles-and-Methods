package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

public class BallLauncher{
  
  public BallLauncher() {
    
  }  
  
/**
 * This method throw the ball by catapult in 45 degree
 */
  public void catapultlaunch() {
    
    turnTo(rotateAngle);
    launchMotor.stop();
    float maxSpeed = launchMotor.getMaxSpeed();
    System.out.println("Maximal speed is: "+maxSpeed+"Acceleration is: "+launchMotor.getAcceleration()+"Launch speed is: "+launchMotor.getSpeed());

  }
  
  private static void turnTo(double t) {
    launchMotor.setAcceleration(30000);
    launchMotor.setSpeed(motorSpeed);
    launchMotor.setAcceleration(30000);
    launchMotor.rotate(Converter.convertAngle(t));
  }
  
}
