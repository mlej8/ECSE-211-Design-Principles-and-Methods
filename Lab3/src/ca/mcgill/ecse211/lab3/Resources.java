package ca.mcgill.ecse211.lab3;

import ca.mcgill.ecse211.lab3.PController;
import ca.mcgill.ecse211.lab3.UltrasonicController;
import ca.mcgill.ecse211.lab3.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Resources {

	/**
	 * If the robot is traveling clockwise lower bound.
	 */
	public static final double CLOCKWISE_LOWER_BOUND = 300.0;
	
	/**
	 * If the robot is traveling clockwise upper bound.
	 */
	public static final double CLOCKWISE_UPPER_BOUND = 120.0;
	
	/**
	 * If the robot is traveling counter clockwise lower bound
	 */
	public static final double COUNTERCLOCKWISE_LOWER_BOUND = 120.0;
	
	/**
	 * If the robot is traveling counter clockwise upper bound
	 */
	public static final double COUNTERCLOCKWISE_UPPER_BOUND = 300.0;
	
	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.115; //2.115

	/**
	 * The robot's width in centimeters.
	 */
	public static final double TRACK = 15.1; //15.1 15.05 14.87

	/**
	 * Turning 90 degrees to start position parallel the wall 
	 */
	public static final double RIGHT_ANGLE = 90.0;
	
	// Ultrasonic sensor properties
	/**
	 * Sensor rotation when navigating.
	 */
	public static int SWIPE = 40;
		
	/**
	 * Rotation of the sensor.
	 */
	public static final double SENSOR_ROTATION = 20.0;
	
	/**
	 * Sensor rotation speed.
	 */
	public static final int SENSOR_ROTATION_SPEED = 150;
	
	/**
	 * Fixed Motor Speed.
	 */
	public static final int MOTOR_SPEED = 200;

	/**
	 * The speed at which the robot rotates in degrees per second.
	 */
	public static final int ROTATE_SPEED = 100;

	/**
	 * The acceleration.
	 */
	public static final int ACCELERATION = 3000;

	/**
	 * Number of times to filter out (ignore) data.
	 */
	public static final int FILTER_OUT = 30;

	/**
	 * Offset (standoff distance) from the wall (cm).
	 */
	public static final int BAND_CENTER = 18;  

	/**
	 * Width of dead band (cm) i.e. error threshold.
	 */
	public static final int BAND_WIDTH = 6;

	/**
	 * Use PController for obstacle avoidance
	 */
	public static UltrasonicController PController = new PController();

	/**
	 * Sleep interval = 50ms = 20 Hz.
	 */
	public static final int SLEEPINT = 50;

	/**
	 * The tile size in centimeters.
	 */
	public static final double TILE_SIZE = 30.48;

	/**
	 * The LCD.
	 */
	public static final TextLCD LCD = LocalEV3.get().getTextLCD();

	/**
	 * The left motor.
	 */
	public static final EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	/**
	 * The right motor.
	 */
	public static final EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	/**
	 * The motor USensor based on
	 */
	public static SensorRotation sensorRotator = new SensorRotation();
	
	/**
	 * The ultrasonic sensor.
	 */
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

	/**
	 * The ultrasonic poller.
	 */
	public static UltrasonicPoller usPoller = new UltrasonicPoller();

	/**
	 * Instance of the Navigation class
	 */
	public static Navigation navigator = Navigation.getNavigator();

	   /**
     * Instance of the Navigation class
     */
    public static NavigationWithObstacles navigatorObstacle = NavigationWithObstacles.getNavigatorObstacle();
	/**
	 * The odometer.
	 */
	public static Odometer odometer = Odometer.getOdometer();

	/**
	 * Motor that rotates the sensor.
	 */
	public static final EV3MediumRegulatedMotor rotateMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));

	/**
	 * Routes
	 */
	public static final int[][] waypoints1 = { { 1, 3 }, { 2, 2 }, { 3, 3 }, { 3, 2 }, { 2, 1 } };
	public static final int[][] waypoints2 = { { 2, 2 }, { 1, 3 }, { 3, 3 }, { 3, 2 }, { 2, 1 } };
	public static final int[][] waypoints3 = { { 2, 1 }, { 3, 2 }, { 3, 3 }, { 1, 3 }, { 2, 2 } };
	public static final int[][] waypoints4 = { { 1, 2 }, { 2, 3 }, { 2, 1 }, { 3, 2 }, { 3, 3 } };
	
	// Thresholds
	/**
	 * Threshold value determining when the robots starts to follow the wall.
	 */
	public static final double THRESHOLD = 20;
	
	/**
	 * Error margin from destination waypoint.
	 */
	public static final double ERROR_MARGIN = 1.0;
	/**
	 * Threshold value determining the smallest angle where to stop following the wall 
	 */
	public static final double STOP_THRESHOLD = 5.0;

}
