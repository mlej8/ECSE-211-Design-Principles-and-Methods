package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private static final int MINIMUM_NONBLACK_INTENSITY = 300;
  private static final double INTENSITY_RATIO = 1.3;
  private float[] colorSensorData = new float[colorSensorSampler.sampleSize()];
  private int lastIntensity = -1; // initialize to -1 at the beginning to avoid misdetection 
  private int curIntensity;
  private double worldX = TILE_SIZE;
  private double worldY = TILE_SIZE;
  private Boolean touchedBlackLine;
  private double[] position = new double[3];

  /*
   * Run odometer correction code 
   */
  public void run() {
    long correctionStart, correctionEnd;
    while (true) {
      correctionStart = System.currentTimeMillis();

      // Fetching values from the color sensor and store it in a float array
      colorSensorSampler.fetchSample(colorSensorData, 0);
      
      // The color sensor returns an intensity value between [0,1]. Times this intensity by 1000 to preserve the
      // floating point numbers
      curIntensity = (int) (colorSensorData[0] * 1000);

      // Trigger correction when a black line is detected
      if (curIntensity < MINIMUM_NONBLACK_INTENSITY) {
        touchedBlackLine = true;
        Sound.beep();
      } else if (lastIntensity/(double) curIntensity > INTENSITY_RATIO){
        touchedBlackLine = true;
        Sound.beep();
      } else {
        touchedBlackLine = false;
      }
       
      
      // Calculate new (accurate) robot position
      if (touchedBlackLine) {
        position = calculateNewPosition(findRightAngleOrientation(odometer.getXYT()[2]));
        
        // Update odometer with new calculated 
        odometer.setXYT(position[0], position[1], position[2]);
      } 
      
      // Save current intensity for next iteration 
      lastIntensity = curIntensity;
      
      // Ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }


  /**
   * Determine the closest right angle orientation among 0, 90, 180 ,270 for theta.
   * 
   * @param theta
   * @return right angle approximated from theta
   */
  private int findRightAngleOrientation(double theta) {
    if (theta > 315.0 || theta < 45.0) {
      return 0;
    } else if (theta > 60.0 && theta < 120.0) {
      return 90;
    } else if (theta > 150.0 && theta < 210.0) {
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
        worldY -=TILE_SIZE;
        position[0] = odometer.getXYT()[0];
        position[1] = worldY;
        break;
      case 270:
        worldX -=TILE_SIZE;
        position[0] = worldX;
        position[1] = odometer.getXYT()[1];
        break;
    }
    position[2] = odometer.getXYT()[2]; 
    return position;
  }
}
