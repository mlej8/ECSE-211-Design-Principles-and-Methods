package ca.mcgill.ecse211.lab5.tests;

import static ca.mcgill.ecse211.lab5.Resources.*;
import ca.mcgill.ecse211.lab5.Converter;
import lejos.hardware.Button;

public class WheelRadiusTests {
  public static void main(String args[]) {
    
    int buttonChoice = -1;
    showText("Press Right      ","Button to start  ");
    while(buttonChoice != Button.ID_RIGHT) {
      buttonChoice = Button.waitForAnyPress();
    }
    
    //Test wheelRadius by rotating the robot two tiles forward.
    LEFT_MOTOR.setSpeed(ROTATE_SPEED);
    RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
    LEFT_MOTOR.rotate(Converter.convertDistance(2*TILE_SIZE),true);
    RIGHT_MOTOR.rotate(Converter.convertDistance(2*TILE_SIZE),false);
  }
  
  
  /**
   * Shows the text on the LCD, line by line.
   * 
   * @param strings comma-separated list of strings, one per line
   */
  public static void showText(String... strings) {
    LCD.clear();
    for (int i = 0; i < strings.length; i++) {
      LCD.drawString(strings[i], 0, i);
    }
  }
}
