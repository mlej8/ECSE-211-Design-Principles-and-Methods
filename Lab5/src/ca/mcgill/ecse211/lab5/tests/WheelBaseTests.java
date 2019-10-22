package ca.mcgill.ecse211.lab5.tests;
import static ca.mcgill.ecse211.lab5.Resources.*;
import ca.mcgill.ecse211.lab5.Converter;
import lejos.hardware.Button;

public class WheelBaseTests {
    public static void main(String args[]) {
      
      int buttonChoice = -1;
      showText("Press Right      ","Button to start  ");
      while(buttonChoice != Button.ID_RIGHT) {
        buttonChoice = Button.waitForAnyPress();
      }
      
      //Test wheelBase by rotating the robot 360 degrees.
      LEFT_MOTOR.setSpeed(ROTATE_SPEED);
      RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
      LEFT_MOTOR.rotate(Converter.convertAngle(360),true);
      RIGHT_MOTOR.rotate(Converter.convertAngle(360),false);
    }
    
    
    /**
     * Shows the text on the LCD, line by line.
     * 
     * @param strings comma-separated list of strings, one per line
     */
    public static void showText(String... strings) {
      LCDScreen.clear();
      for (int i = 0; i < strings.length; i++) {
        LCDScreen.drawString(strings[i], 0, i);
      }
    }
}
