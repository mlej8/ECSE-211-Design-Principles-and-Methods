package ca.mcgill.ecse211.lab4;
import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Sound;

public class LightLocalizer {
  private static final int MINIMUM_NONBLACK_INTENSITY = 25; //BlackLine at 21,20
  private static final double INTENSITY_RATIO = 1.3;
  private double[] intersectionDegrees=new double[5];
  private int lineCount=0;
  private boolean lineTouched=false;
  private double lastIntensity=-1;
    
    /**
     * This method controls how the robot moves when using the light sensor. The robot would turn around for 360 
     * degrees and then stop. After that, it will adjust the odometer readings based on theta reading when black 
     * lines are met.
     */
    public void localize() {
      LEFT_MOTOR.setSpeed(ROTATE_SPEED);
      RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
      LEFT_MOTOR.rotate(convertAngle(360), true);
      RIGHT_MOTOR.rotate(-convertAngle(360), false);
      
      adjustOdometer(intersectionDegrees);
    }
    
    public void processData(int curIntensity){
      // Trigger correction when a black line is detected
      if (curIntensity < MINIMUM_NONBLACK_INTENSITY) {
        lineTouched = true;
        Sound.beep();
      } else if (lastIntensity/(double) curIntensity > INTENSITY_RATIO){
        lineTouched = true;
        Sound.beep();
      } else {
        lineTouched = false;
      }
      lastIntensity=curIntensity;
      
      if(lineTouched) {
        intersectionDegrees[lineCount]=odometer.getXYT()[2];
        System.out.println(odometer.getXYT()[2]);
        lineCount++;
      }
    }
    
    /**
     * THe input is a double array which stores the odometer's theta reading when black lines are met. This method
     * will calculate delta theta, delta x and delta y.
     * @param intersectionDegrees
     */
    private void adjustOdometer(double[] intersectionDegrees) {
        //Calculate and adjust theta
        double deltaTheta = -intersectionDegrees[3]+270+(intersectionDegrees[3]-intersectionDegrees[1])/2;
          System.out.println("DeltaTheta: " + deltaTheta);
        double adjustedTheta = odometer.getXYT()[2]+deltaTheta;
        if (adjustedTheta > 360)
          adjustedTheta -= 360;
        else if(adjustedTheta < 0)
          adjustedTheta += 360;
        odometer.setTheta(adjustedTheta);
        
        //Calculate delta x and delta y.
        double angle_posXToNegX = intersectionDegrees[2] - intersectionDegrees[0];
        double angle_negYToPosY = intersectionDegrees[3] - intersectionDegrees[1];
        double[] deltaXY = new double[2];
        deltaXY[1] = Math.cos(normalizeTheta(angle_posXToNegX)/2.0)*DIST_CENTRE_TO_LIGHT_SENSOR;
        deltaXY[0] = Math.cos(normalizeTheta(angle_negYToPosY)/2.0)*DIST_CENTRE_TO_LIGHT_SENSOR;
        //Find quadrant
        int quadrant = findQuadrant(angle_posXToNegX, angle_negYToPosY);
          System.out.println("The robot's quadrant is: "+quadrant);
        switch (quadrant) {
          case 1:
            odometer.setX(TILE_SIZE+deltaXY[0]);
            odometer.setY(TILE_SIZE+deltaXY[1]);
            break;
          case 2:
            odometer.setX(TILE_SIZE+deltaXY[0]);
            odometer.setY(TILE_SIZE-deltaXY[1]);
            break;
          case 3:
            odometer.setX(TILE_SIZE-deltaXY[0]);
            odometer.setY(TILE_SIZE-deltaXY[1]);
            break;
          case 4:
            odometer.setX(TILE_SIZE-deltaXY[0]);
            odometer.setY(TILE_SIZE+deltaXY[1]);
            break;
        }
    }
    
    /**
     * Find which quadrant the robot's center is currently in.
     * @param angleX
     * @param angleY
     * @return
     */
    private int findQuadrant(double angleX, double angleY) {
        //Wheel Center in 4th quadrant
        if (!isWithin180Degrees(angleX) && !isWithin180Degrees(angleY))
          return 1;
        //Wheel Center in 2rd quadrant
        if (isWithin180Degrees(angleX) && !isWithin180Degrees(angleY))
          return 2;
        //Wheel Center in 3rd quadrant
        if (isWithin180Degrees(angleX) && isWithin180Degrees(angleY))
          return 3;
        //Wheel Center in 4th quadrant
        return 4;
    }
 
    /**
     * Determine if an angle is within the range 0~180, important for finding out which quardrant robot is in.
     * @param angle
     * @return
     */
    private boolean isWithin180Degrees(double angle) {
        return (angle > 0&&angle <= 180);
    }
    
    /**
     * Change angle that is either less than 0 or greater than 180 degrees to a range 0~180.
     * @param angle
     * @return
     */
    private double normalizeTheta(double angle) {       //Based on cos property it may not be necessary
      if (angle < 0)
        return -angle;
      if (angle >180)
        return 360 - angle;
      return -1;
    }
    
    /**
     * Converts input distance to the total rotation of each wheel needed to cover
     * that distance.
     * 
     * @param distance
     * @return the wheel rotations necessary to cover the distance
     */
    public static int convertDistance(double distance) {
        return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
    }

    /**
     * Converts input angle to the total rotation of each wheel needed to rotate the
     * robot by that angle.
     * 
     * @param angle
     * @return the wheel rotations necessary to rotate the robot by the angle
     */
    
    public static int convertAngle(double angle) {
        return convertDistance(Math.PI * TRACK * angle / 360.0);
    }
}
