package ca.mcgill.ecse211.lab3;
import static ca.mcgill.ecse211.lab3.PController.state;
import static ca.mcgill.ecse211.lab3.Resources.*;

import ca.mcgill.ecse211.lab3.PController.State;

/**
 * Thread Controlling Sensor's movement.
 */
public class SensorRotation implements Runnable{
	
	public SensorRotation(){
		
	}
	
	public void run() {
		int limitAngle = SWIPE;
		//Keep Sweeping Robot's Sensor when it's not in wall following state. 
		while (true) {
			if (state == State.INIT) {
    		rotateMotor.rotateTo(limitAngle, false); 
    		limitAngle = -limitAngle;
    	}
		}
	}
}
