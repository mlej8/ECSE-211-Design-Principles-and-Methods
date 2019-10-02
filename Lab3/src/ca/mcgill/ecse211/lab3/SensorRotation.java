package ca.mcgill.ecse211.lab3;
import static ca.mcgill.ecse211.lab3.PController.state;
import static ca.mcgill.ecse211.lab3.Resources.*;

import ca.mcgill.ecse211.lab3.PController.State;

public class SensorRotation implements Runnable{
	
	public SensorRotation(){
		
	}
	
	public void run() {
		int limitAngle = SWIPE;
		while (true) {
			if (state == State.INIT) {
    		rotateMotor.rotateTo(limitAngle, false); 
    		limitAngle = -limitAngle;
    	}
		}
	}
}
