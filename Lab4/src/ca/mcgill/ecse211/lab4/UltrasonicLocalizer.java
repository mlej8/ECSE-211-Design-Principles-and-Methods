package ca.mcgill.ecse211.lab4;

/**
 * Class that uses the ultrasonic sensor to orientate itself
 * assuming the robot will start with its center of rotation in the bottom left tile of the field.
 * Its initial orientation (heading) is assumed to be unknown
 */
public class UltrasonicLocalizer extends UltrasonicController {

	/**
	 * first step to accurately localizing is to determine the initial orientation of the robot
	 */
	
	public void fallingEdge() {
		
	}
	
	public void risingEdge() {
		
	}

	@Override
	public void processUSData(int distance) {
		filter(distance);		
	}

	@Override
	public int readUSDistance() {
		return this.distance; 
	}
}
