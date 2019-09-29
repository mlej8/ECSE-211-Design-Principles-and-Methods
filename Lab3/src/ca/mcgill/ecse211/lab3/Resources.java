package ca.mcgill.ecse211.lab3;

import ca.mcgill.ecse211.lab3.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Resources {
  
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.115;
  
  /**
   * The robot's width in centimeters.
   */
  public static final double TRACK = 15;
  
  /**
   * The acceleration.
   */
  public static final int ACCELERATION = 4000;
  
  /**
   * The degree error.
   */
  public static final double DEG_ERR = 3.0;
  
  /**
   * The cm error.
   */
  public static final double CM_ERR = 1.0;
  
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
  public static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
 
  /**
   * The ultrasonic poller.
   */
  public static UltrasonicPoller usPoller = new UltrasonicPoller();
  
  /**
   * The obstacle avoidance.
   */
  public static ObstacleAvoidance obstacleAvoidance = new ObstacleAvoidance();
  
  /**
   * The odometer.
   */
  public static Odometer odometer =Odometer.getOdometer();
  
}
