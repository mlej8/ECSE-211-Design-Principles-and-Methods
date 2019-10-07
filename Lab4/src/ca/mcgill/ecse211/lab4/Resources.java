package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.lab4.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Class that stores all constants for lab 4 (localization).
 */
public class Resources {
    
    /**
     * The wheel radius in centimeters.
     */
    public static final double WHEEL_RAD = 2.103; 
    
    /**
     * The robot's width in centimeters.
     */
    public static final double TRACK = 15.008; 

    /**
     * The distance from the robot's wheelBase center to light light sensor
     */
    public static final double DIST_CENTRE_TO_LIGHT_SENSOR = 10;
    
    /**
     * Turning 90 degrees to start position parallel the wall 
     */
    public static final double RIGHT_ANGLE = 90.0;
    
    /**
     * Fixed Motor Speed.
     */
    public static final int MOTOR_SPEED = 200;

    /**
     * The speed at which the robot rotates in degrees per second.
     */
    public static final int ROTATE_SPEED = 50;

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
     * The ultrasonic sensor.
     */
    public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

    /**
     * The light sensor.
     */
    public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
    
    /**
     * The ultrasonic poller.
     */
    public static SensorPoller sensorPoller = new SensorPoller();

    /**
     * Instance of the Navigation class.
     */
    public static Navigation navigator = Navigation.getNavigator();

    /**
     * The odometer.
     */
    public static Odometer odometer = Odometer.getOdometer();

    /**
     * Motor that rotates the sensor.
     */
    public static final EV3MediumRegulatedMotor rotateMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));

    /**
     * Error margin from destination waypoint.
     */
    public static final double ERROR_MARGIN = 0.5;
    
    /**
     * Instance of US Localizer
     */
    public static UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer();
    
    /** 
     * Instance of Light Localizer
     */
    public static LightLocalizer lightLocalizer = new LightLocalizer();
}
