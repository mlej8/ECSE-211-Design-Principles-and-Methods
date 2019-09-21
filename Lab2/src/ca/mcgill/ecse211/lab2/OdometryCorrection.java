package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private static final int MINIMUM_NONBLACK_INTENSITY = 200;
  private static final double CUR_LAST_INTENSITY_RATIO_THRESHOLD = 0.5;
  private float[] colorSensorData;
  private int lastIntensity = 1000;
  private int curIntensity;
  private double worldX = TILE_SIZE;
  private double worldY = TILE_SIZE;
  private Boolean touchedBlackLine;
  private double[] position = new double[3];
  /*
   * Here is where the odometer correction code should be run.
   */

  public void run() {
    long correctionStart, correctionEnd;
    while (true) {
      correctionStart = System.currentTimeMillis();

      // Fetching values from the color sensor
      colorSensor.getRedMode().fetchSample(colorSensorData, 0);

      // TODO Trigger correction (When do I have information to correct?)
      curIntensity = (int) colorSensorData[0] * 1000;
      System.out.println("CurIntensity is:" + curIntensity);
      if (curIntensity < MINIMUM_NONBLACK_INTENSITY) {
        touchedBlackLine = true;
      } else if (curIntensity / lastIntensity < CUR_LAST_INTENSITY_RATIO_THRESHOLD) {
        touchedBlackLine = true;
      } else {
        touchedBlackLine = false;
      }
      // TODO Calculate new (accurate) robot position
      if (touchedBlackLine) {
        position = calculateNewPosition(findRightAngleOrientation(odometer.getXYT()[2]));
        // TODO Update odometer with new calculated (and more accurate) values, eg:
        // odometer.setXYT(0.3, 19.23, 5.0);
        odometer.setXYT(position[0], position[1], position[2]);
      }
      lastIntensity = curIntensity;
      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }


  /**
   * Determine the closest right angle orientation among 0, 90, 180 ,270 for theta
   * 
   * @param theta
   * @return right angle approximated from theta
   */
  private int findRightAngleOrientation(double theta) {
    if (Math.abs(theta) < 45) {
      return 0;
    } else if (Math.abs(90 - theta) < 45) {
      return 90;
    } else if (Math.abs(180 - theta) < 45) {
      return 180;
    } else {
      return 270;
    }
  }

/**
 * Adjust X or Y coordinate to world frame coordinate 
 * @param theta
 * @return double array of new position
 */
  private double[] calculateNewPosition(int theta) {
    double[] position = new double[3];
    switch (theta) {
      case 0:
        position[0] = odometer.getXYT()[0];
        position[1] = worldY;
        worldY += TILE_SIZE;
        break;
      case 90:
        position[0] = worldX;
        position[1] = odometer.getXYT()[1];
        worldX += TILE_SIZE;
        break;
      case 180:
        position[0] = odometer.getXYT()[0];
        position[1] = worldY - TILE_SIZE;
        break;
      case 270:
        position[0] = worldX - TILE_SIZE;
        position[1] = odometer.getXYT()[1];
        break;
    }
    position[2] = odometer.getXYT()[2];
    return position;
  }
}
