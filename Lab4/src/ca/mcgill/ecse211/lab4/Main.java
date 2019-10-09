package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

import ca.mcgill.ecse211.lab4.Display;
import ca.mcgill.ecse211.lab4.SensorPoller.Mode;
import lejos.hardware.Button;

/**
 * The main driver class for the navigation and obstacleAvoidance lab.
 */
public class Main {
	
	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen
	
	public static void main(String args[]) {
		
		int buttonChoice = chooseFaillingEdgeOrRisingEdge();

		// Running sensorPoller, odometer and display threads.
		new Thread(sensorPoller).start(); // Running a thread running ultrasonic to sensor to detect the walls
		new Thread(odometer).start(); // Running a continuous thread for odometer
		displayThread.start();
		
		if (buttonChoice == Button.ID_LEFT) {
			// run Failing edge
			ultrasonicLocalizer.fallingEdge();
		} else {
			// run Rising Edge
			ultrasonicLocalizer.risingEdge();
		}
		buttonChoice = Button.waitForAnyPress();
		// Wait for input once completing ultrasonic localization before navigating to the origin (1,1)
		while (buttonChoice != Button.ID_RIGHT) {
			
			// Clear the display
			LCD.clear();

          LCD.drawString("   Press the      ", 0, 0);
          LCD.drawString("   Right Button  ", 0, 1);
          LCD.drawString("   to start       ", 0, 2);
          LCD.drawString("   LightLocalizer ", 0, 3);
          LCD.drawString("                   ", 0, 4);

            buttonChoice = Button.waitForAnyPress();
        }
		
		
        // Stop fetching data from ultrasonic sensor
        sensorPoller.setMode(Mode.PAUSE);
        sensorPoller.setMode(Mode.LIGHT);
        
        // Navigate to origin (1,1)
        navigator.findRobotPosition();
        Thread nav = new Thread(navigator);
        nav.start();
        
        // Wait till navigation thread is done before executing light sensor localization
        try {
            nav.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Set the current angle to 0 assuming ultrasonic localization corrected the odometer
        
        // Execute light sensor localization
        sensorPoller.setMode(Mode.LIGHT);
        lightLocalizer.localize();
        nav = new Thread(navigator);
        nav.start();
        
        // Do nothing until exit button is pressed, then exit.
        while (Button.waitForAnyPress() != Button.ID_ESCAPE)
            System.exit(0);
    }

    /**
     * Choose with obstacles or not.
     * @return Button choice
     */
    private static int chooseFaillingEdgeOrRisingEdge() {
        int buttonChoice;
        Display.showText("< Left  | Right >",
                         "        |        ",   
                         "Failling| Rising ", 
                         "  Edge  |  Edge  ",
                         "        |        ");

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		LCD.clear();
		
		return buttonChoice;
	}
	
	/**
	 * Thread sleeps for a time period specified by sleepFor
	 * @param duration
	 */
	public static void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// There is nothing to be done here
		}
	}

}
