package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

import ca.mcgill.ecse211.lab4.Display;
import lejos.hardware.Button;

/**
 * The main driver class for the navigation and obstacleAvoidance lab.
 */
public class Main {
	
	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen
	
	public static void main(String args[]) {
		
		int buttonChoice = chooseFaillingEdgeOrRisingEdge();
		
		if (buttonChoice == Button.ID_LEFT) {
			// run Failing edge
			new Thread(usPoller).start(); // Running a thread running ultrasonic to sensor to detect the walls
			new Thread(odometer).start(); // Running a continuous thread for odometer
			ultrasonicLocalizer.fallingEdge();
		} else {
			// run Rising Edge
			new Thread(usPoller).start(); // Running a thread running ultrasonic to sensor to detect the walls
			new Thread(odometer).start(); //Running a continuous thread for odometer
			ultrasonicLocalizer.risingEdge();
		}

		displayThread.start();

		// Wait for input once completing ultrasonic localization before navigating to the origin (1,1)
		while (buttonChoice != Button.ID_ESCAPE) {
			
			// Clear the display
			LCD.clear();

			LCD.drawString("    Press the      ", 0, 0);
			LCD.drawString("    escape button  ", 0, 1);
			LCD.drawString(" |  to start       ", 0, 2);
			LCD.drawString(" |  lightLocalizer ", 0, 3);
			LCD.drawString(" V                 ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		}
		
		// Navigate to origin (1,1)
		Thread nav = new Thread(navigator);
		nav.start();
		
		// Wait till navigation thread is done before executing light sensor localization
		try {
			nav.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Execute light sensor localization
		lightLocalizer.localize();
		
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