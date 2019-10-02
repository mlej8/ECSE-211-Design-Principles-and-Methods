package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

import ca.mcgill.ecse211.lab3.Display;
import lejos.hardware.Button;

/**
 * The main driver class for the navigation and obstacleAvoidance lab.
 */
public class Main {
	public static void main(String args[]) {
		
		int buttonChoice = chooseObstaclesOrNot();

		if (buttonChoice == Button.ID_LEFT) {
			// run with obstacles
			new Thread(usPoller).start(); // Running a thread running ultrasonic to sensor to keep detecting the walls
			new Thread(odometer).start(); //Running a continuous thread for odometer
			new Thread(navigatorObstacle).start();  //Running a navigation thread to guide motor to waypoints
			new Thread(sensorRotator).start(); //Running a Motor thread to control sensor movement
		} else {
			// run without obstacles
			new Thread(navigator).start();
			new Thread(odometer).start();

		}

		// Display information on LCD screen
		new Thread(new Display()).start();

		// Do nothing until exit button is pressed, then exit.
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

	/**
	 * Choose with obstacles or not.
	 * @return Button choice
	 */
	private static int chooseObstaclesOrNot() {
		int buttonChoice;
		Display.showText("< Left  | Right >", "        |        ", "  With  | Without", "Obstacle|Obstacle",
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
