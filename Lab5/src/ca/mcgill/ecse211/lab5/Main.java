package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

import ca.mcgill.ecse211.lab5.Display;
import ca.mcgill.ecse211.lab5.SensorPoller.Mode;
import lejos.hardware.Button;

/**
 * The main class controlling the flow of the application.
 */
public class Main {

	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen

	public static void main(String args[]) {

		int buttonChoice = chooseFaillingEdgeOrRisingEdge();

		// Running sensorPoller, odometer and display threads.
		new Thread(sensorPoller).start(); // Running a thread controlling which sensor to fetch from
		new Thread(odometer).start(); // Running a continuous thread for odometer
//		displayThread.start(); // Running a thread to display current odometer's values

		if (buttonChoice == Button.ID_LEFT) {
			// run Failing edge
			ultrasonicLocalizer.fallingEdge();
		} else {
			// run Rising Edge
			ultrasonicLocalizer.risingEdge();
		}

		// Log odometer once US localization is done
		System.out.println("Final Orientation: " + odometer.getXYT()[2]);

		// Assume current orientation is 0 with respect to the Y-axis after US
		// localization
		odometer.setTheta(0);

		// Clear the display
		LCD.clear();

		LCD.drawString("    Press the       ", 0, 0);
		LCD.drawString("    Right Button    ", 0, 1);
		LCD.drawString("    to start        ", 0, 2);
		LCD.drawString("    LightLocalizer  ", 0, 3);
		LCD.drawString("                    ", 0, 4);

		// Wait for input once completing ultrasonic localization 
		while (Button.waitForAnyPress() != Button.ID_RIGHT) {}

		// Stop fetching data from ultrasonic sensor and switch to fetch data from light
		// sensor
		sensorPoller.setMode(Mode.LIGHT);

		// Find current robot's position
		navigator.findRobotPosition();

		// Navigate to origin (1,1)
		navigator.travelToOrigin();

		// Execute light sensor localization
		lightLocalizer.localize();

		// Navigator to origin (1,1) after light localization
		navigator.travelToOrigin();

		// Orient back to 0 degree w.r.t. the Y-axis after correction
		lightLocalizer.reOrient();

		// Do nothing until exit button is pressed, then exit.
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

	/**
	 * Choose with obstacles or not.
	 * 
	 * @return Button choice
	 */
	private static int chooseFaillingEdgeOrRisingEdge() {
		int buttonChoice;
		Display.showText("< Left  | Right >", "        |        ", "Failling| Rising ", "  Edge  |  Edge  ",
				"        |        ");

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

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
