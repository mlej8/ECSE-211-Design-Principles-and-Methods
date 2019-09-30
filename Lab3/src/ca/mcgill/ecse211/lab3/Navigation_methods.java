package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import static ca.mcgill.ecse211.lab3.Navigation_methods.State.*;
import static ca.mcgill.ecse211.lab3.ObstacleAvoidance.State.EMERGENCY;
import static ca.mcgill.ecse211.lab3.ObstacleAvoidance.State.INIT;
import static ca.mcgill.ecse211.lab3.ObstacleAvoidance.State.TRAVELING;
import static ca.mcgill.ecse211.lab3.ObstacleAvoidance.State.TURNING;

public class Navigation_methods implements Runnable {

  private static Navigation_methods navigator;

  /**
   * Variable storing current route
   */
  private static int[][] currentWaypoints;

  /**
   * The possible states that the robot could be in.
   */
  enum State {
    /** The initial state. */
    INIT,
    /** The turning state. */
    TURNING,
    /** The traveling state. */
    TRAVELING,
    /** The emergency state. */
    EMERGENCY
  };

  /**
   * Navigation class implements the singleton pattern
   */
  private Navigation_methods() {

  }

  /**
   * Get instance of the Navigation class. Only allows one thread at a time calling this method.
   */
  public synchronized static Navigation_methods getNavigator() {
    if (navigator == null) {
      navigator = new Navigation_methods();
    }
    return navigator;
  }

  /**
   * The current state of the robot.
   */
  static State state = INIT;

  /**
   * {@code true} when robot is traveling.
   */
  public static boolean traveling = false; // false by default

  /**
   * {@code true} when obstacle is avoided.
   */
  public static boolean safe;

  /**
   * The destination x.
   */
  public static double destx;

  /**
   * The destination y.
   */
  public static double desty;
  
  public static double ERRORBAND=1;

  @Override
  public void run() {
    // TODO Auto-generated method stub
    int selectedRoute = 0;
    switch (selectedRoute) {
      case 1:
        currentWaypoints = waypoints1;
        break;
      case 2:
        currentWaypoints = waypoints2;
        break;
      case 3:
        currentWaypoints = waypoints3;
        break;
      case 4:
        currentWaypoints = waypoints4;
        break;
    }
    state = INIT;
    while (true) {
      if (state == INIT) {
        if (traveling) {
          state = TURNING;
        }
      } else if (state == TURNING) {
        double destAngle = Navigation.getDestAngle(destx, desty);
        Navigation.turnTo(destAngle, true);
        if (Navigation.facingDest(destAngle)) {
          Navigation.setSpeeds(0, 0);
          state = TRAVELING;
        }
      } else if (state == TRAVELING) {
        checkEmergency();
        if (state == EMERGENCY) { // order matters!
        
        } else if (!isDoneTravelling(destx, desty)) {
          updateTravel();
        } else { // Arrived!
          Navigation.setSpeeds(0, 0);
          traveling = false;
          state = INIT;
        }
      } else if (state == EMERGENCY) {
        if (safe) {
          state = TURNING;
        }
      }
      try {
        
      }catch(Exception e) {
        
      }
    }
  }

  private void travelTo(double x, double y, boolean avoid) {
    /**
     * This method causes the robot to travel to the absolute field location (x, y), specified in tile points. This
     * method should continuously call turnTo(double theta) and then set the motor speed to forward (straight). This
     * will make sure that your heading is updated until you reach your exact goal. This method will poll the odometer
     * for information.
     */
    // TODO: Implement this method
    double minAngle=findMinAngle(x,y);
    turnTo(minAngle);
    while(!isDoneTravelling(x,y)) {
      leftMotor.forward();
      rightMotor.forward();
      leftMotor.setSpeed(MOTOR_SPEED);
      rightMotor.setSpeed(MOTOR_SPEED);
    }
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0);
    traveling = true;
  }

  private boolean isNavigating() {
    /**
     * This method returns true if another thread has called travelTo() or turnTo() and the method has yet to return;
     * false otherwise.
     */
    return traveling;
  }

  private double findMinAngle(double destx, double desty) {
    double[] vector1 = new double[] {Math.cos(Math.toRadians(90 - odometer.getXYT()[2])),
        Math.sin(Math.toRadians(90 - odometer.getXYT()[2]))};
    double[] vector2 = new double[] {destx - odometer.getXYT()[0], desty - odometer.getXYT()[1]};

    double minAngle = Math
        .acos((vector1[0] * vector2[0] + vector1[1] * vector2[1]) / (vectorLength(vector1) * vectorLength(vector2)));
    double difference = Math.atan2(vector1[1], vector1[0]) - Math.atan2(vector2[1], vector2[0]);
    if (Math.atan2(vector1[1], vector1[0]) > 0) {
      if (difference > Math.PI || difference < 0)
        return -minAngle;
    } else {
      if (difference > Math.PI || difference < 0)
        return -minAngle;
    }
    return minAngle;
  }

  private double vectorLength(double[] vector) {
    return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
  }

  private void turnTo(double theta) {
    /**
     * This method causes the robot to turn (on point) to the absolute heading theta. This method should turn a MINIMAL
     * angle to its target.
     */
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    if (theta < 0) { // LeftTurn
      leftMotor.rotate(-convertAngle(-theta),false);
      rightMotor.rotate(convertAngle(-theta),false);
    } else { // RightTurn
      leftMotor.rotate(convertAngle(theta),false);
      rightMotor.rotate(-convertAngle(theta),false);
    }
    traveling = true;
  }

  public boolean isDoneTravelling(double destx, double desty) {
    return vectorLength(new double[]{destx-odometer.getXYT()[0],desty=odometer.getXYT()[1]})<ERRORBAND;
  }
  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
  
  /**
   * Sets emergency state when robot is too close to a wall.
   */
  public static void checkEmergency() {
    if (usPoller.getDistance() < 10) {
      state = EMERGENCY;
    }
  }
}
