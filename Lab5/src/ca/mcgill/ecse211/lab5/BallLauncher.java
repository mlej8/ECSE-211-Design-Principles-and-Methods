package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

public class BallLauncher{
	
  public BallLauncher() {}
  
/**
 * This method launches the catapult. 
 */
  @SuppressWarnings("deprecation")
public void catapultlaunch() {
	
	// Set motors' acceleration and speed 
	leftLaunchMotor.setAcceleration(LAUNCH_MOTOR_ACCELERATOR);
	rightLaunchMotor.setAcceleration(LAUNCH_MOTOR_ACCELERATOR);
    leftLaunchMotor.setSpeed(LAUNCH_MOTOR_SPEED);
    rightLaunchMotor.setSpeed(LAUNCH_MOTOR_SPEED);
    
    // Make it rotote 
    leftLaunchMotor.rotate(Converter.convertAngle(LAUNCH_ANGLE), true);
    rightLaunchMotor.rotate(Converter.convertAngle(LAUNCH_ANGLE), false);
    
    System.out.println("Maximal speed is: "+ leftLaunchMotor.getMaxSpeed() + "\nAcceleration is: "+ leftLaunchMotor.getAcceleration() + "\nLaunch speed is: "+ leftLaunchMotor.getSpeed());
    System.out.println("Right motor:");
    System.out.println("Maximal speed is: "+ rightLaunchMotor.getMaxSpeed() + "\nAcceleration is: "+ rightLaunchMotor.getAcceleration() + "\nLaunch speed is: "+ rightLaunchMotor.getSpeed());
    
    // Stop it
    leftLaunchMotor.stop(true); 
    rightLaunchMotor.stop(false);
    reload();
    }  
  
  	public void reload() {
  		// Set reload speed 
  		leftLaunchMotor.setAcceleration(ACCELERATION);
  		rightLaunchMotor.setAcceleration(ACCELERATION);
  	    leftLaunchMotor.setSpeed(MOTOR_SPEED);
  	    rightLaunchMotor.setSpeed(MOTOR_SPEED);
  	    
  	    // Rotate catapult back to its original position 
  		leftLaunchMotor.rotate(-Converter.convertAngle(LAUNCH_ANGLE), true);
  	    rightLaunchMotor.rotate(-Converter.convertAngle(LAUNCH_ANGLE), false);
  		Main.sleepFor(7000);
  	}
  
}
