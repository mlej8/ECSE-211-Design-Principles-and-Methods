package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

import ca.mcgill.ecse211.lab5.Display;
import ca.mcgill.ecse211.lab5.SensorPoller.Mode;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main class controlling the flow of the application.
 */
public class Main {

	// Cordinates of the Target point destination
	private static double targetX = 3.0 * TILE_SIZE;
	private static double targetY = 3.0 * TILE_SIZE;

//	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen

	public static void main(String args[]) {

		int buttonChoice = chooseStationaryOrMobile();
		if (buttonChoice == Button.ID_LEFT) {
			System.out.println("Not implemented yet.");
			// TODO: Launch catapult 5 times
		} else if (buttonChoice == Button.ID_RIGHT) {
			
			waitToStart();
			
			Display.showText("< Left  | Right >", "        |        ", "Stationa| Mobile ", "   ry   | Launch ",
					" Launch |        ");
			
			// Running sensorPoller, odometer and display threads.
			new Thread(sensorPoller).start(); // Running a thread controlling which sensor to fetch from
			new Thread(odometer).start(); // Running a continuous thread for odometer
			
			
			// TODO: Think about the necessity of a display thread ? yes or no? 
			//			displayThread.start(); // Running a thread to display current odometer's values

			// Execute Falling Edge implementation of ultrasonic localization
			ultrasonicLocalizer.fallingEdge();

			// Assume current orientation is 0 with respect to the Y-axis after US
			// localization
			odometer.setTheta(0);

			// Stop fetching data from ultrasonic sensor and switch to fetch data from light
			// sensor
			sensorPoller.setMode(Mode.LIGHT);

			// Find current robot's position
			navigator.findRobotPosition();

			// Navigate to origin (1,1) approximately
			navigator.travelToOrigin();

			// Execute light sensor localization
			lightLocalizer.localize();

			// Navigator to true origin (1,1) after light localization
			navigator.travelToOrigin();

			// Orient back to 0 degree w.r.t. the Y-axis after correction
			lightLocalizer.orientTo0();

			// Navigate to to target position
			navigator.travelTo(targetX, targetY);
			
			// Once at destination, execute light localization to correct error on the odometer
			lightLocalizer.localize();
			navigator.travelTo(targetX, targetY);
			lightLocalizer.orientTo0();
			
			// Arrived at destination
			Sound.beep();
			
			// TODO: Launch the ball
			for(int i = 0; i < 4; i++ ) {
				initiatePauseToReload();
				// Launch the ball 4 times
			}
			
			// Do nothing until exit button is pressed, then exit.
			while (Button.waitForAnyPress() != Button.ID_ESCAPE)
				System.exit(0);
		}
	}

	/**
	 * Choose with stationary launch or mobile launch.
	 * 
	 * @return Button choice
	 */
	private static int chooseStationaryOrMobile() {
		int buttonChoice;
		Display.showText("< Left  | Right >", "        |        ", "Stationa| Mobile ", "   ry   | Launch ",
				" Launch |        ");

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		LCD.clear();

		return buttonChoice;
	}

	/**
	 * Wait till center button is clicked before localizing and navigating to the
	 * computed launch position.
	 * 
	 * @return Button choice
	 */
	private static int waitToStart() {
		int buttonChoice;
		
		Display.showText(" 	  Press The	   ", 
						 "  Center Button  ", 
						 "     to Start    ",
						 "    Target x: " + targetX,
						 "    Target y: " + targetY);

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);

		LCD.clear();

		return buttonChoice;
	}

	private static int initiatePauseToReload() {
		int buttonChoice;
		
		Display.showText(" 	  Press The	   ", 
						 "  Center Button  ", 
						 "     to Resume   ",
						 "   once catapult ",
						 "    is reloaded  ");

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);

		LCD.clear();

		return buttonChoice;
	}
		
	/**
	 * Thread sleeps for a time period specified by sleepFor
	 * 
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
