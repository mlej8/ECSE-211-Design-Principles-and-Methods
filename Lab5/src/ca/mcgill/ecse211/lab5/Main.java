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



//	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen

	public static void main(String args[]) {

		int buttonChoice = chooseStationaryOrMobile();
		if (buttonChoice == Button.ID_LEFT) {
			for(int i = 0; i<5; i++) {
			ballLauncher.catapultlaunch();
			}
		} else if (buttonChoice == Button.ID_RIGHT) {
			navigator.findDestination();			
			
			waitToStart();
			
			// Running sensorPoller and odometer threads.
			new Thread(sensorPoller).start(); // Running a thread controlling which sensor to fetch from
			new Thread(odometer).start(); // Running a continuous thread for odometer

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
			odometer.setXYT(0, 0, 0);
			// Execute light sensor localization
			lightLocalizer.localize();

			// Navigator to true origin (1,1) after light localization
			navigator.travelToOrigin();
			
			// Orient back to 0 degree w.r.t. the Y-axis after correction
			lightLocalizer.orientTo0();
			
			// Find launch position after odometer has been corrected
			navigator.findDestination();
			
			// Navigate to to target position
			navigator.travelToLaunchPoint();
			// Once at destination, execute light localization to correct error on the odometer
			/*lightLocalizer.localize();
			navigator.travelTo(targetX, targetY);
			lightLocalizer.orientTo0();*/
			
			// Arrived at destination
			Sound.twoBeeps();
			
			// turn to face the target
            navigator.findDestination2();
            
			// Launch the ball
			ballLauncher.catapultlaunch();
			
			for(int i = 0; i < 4; i++ ) {
				initiatePauseToReload();
				ballLauncher.catapultlaunch();				
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
						 "    Target X: " + navigator.getTargetX(),
						 "    Target Y: " + navigator.getTargetY(),
						 "    Launch X: " + navigator.getlaunchX(),
						 "    Launch Y: " + navigator.getlaunchY());

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
