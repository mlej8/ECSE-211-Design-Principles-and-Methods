package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

public class BallLauncher implements Runnable {
  public void run() {
    // TODO: Find a launch Point based on Target Position.
    // Find the Intersection point between currentPoint/circleArc.

    // TODO: Determine 1. Vertical TravelTo Point and 2. Horizontal TravelTo Point

    // TODO: Use ultrasonic localization

    // TODO: Use light localization

    // TODO: Call TravelTo and Do lightCorrection once two Black lines are met

    // TODO: Exhibit a long Beep

    // TODO: Do catapultLaunch five times
  }

/**
 * This method throw the ball by catapult in 45 degree
 */
  public void catapultlaunch() {
    
    turnTo(rotateAngle);
    launchMotor.stop();
    float maxSpeed = launchMotor
        .getMaxSpeed();System.out.println("Maximal speed is: "+maxSpeed+"Acceleration is: "+launchMotor.getAcceleration()+"Launch speed is: "+launchMotor.getSpeed());

  }
  
  private static void turnTo(double t) {
    launchMotor.setAcceleration(30000);
    launchMotor.setSpeed(motorSpeed);
    launchMotor.setAcceleration(30000);
    launchMotor.rotate(Converter.convertAngle(t));
  }
  
}
