package ca.mcgill.ecse211.lab4;

public class UltrasonicLocalizer extends UltrasonicController {

	
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
