package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

/**
 * Controller that controls the robot's movements based on UltraSonic data.
 */
public abstract class UltrasonicController {

  int distance; 

  int filterControl;

  /**
   * Perform an action based on the US data input.
   * 
   * @param distance: the distance to the wall in cm
   */
  public abstract void processUSData(int distance);

  /**
   * Returns the distance between the US sensor and an obstacle in cm.
   * 
   * @return the distance between the US sensor and an obstacle in cm
   */
  public abstract int readUSDistance();

  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * 
   * @param distance: distance in cm
   */
  void filter(int distance) {
    if (distance >= 255 && filterControl < FILTER_OUT) {
      filterControl++;
    } else if (distance >= 255) {
      this.distance = distance;
    } else {
      filterControl = 0;
      this.distance = distance;
    }
  }

}
