package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

public class Navigation implements Runnable {
	
	private static Navigation navigator;

	/**
	 * Variable storing current route
	 */
	private static int[][] currentWaypoints;

  /**
   * The possible states that the robot could be in.
   */
  enum State {
    /** The initial state. */ INIT,
    /** The turning state. */ TURNING, 
    /** The traveling state. */ TRAVELING,
    /** The emergency state. */ EMERGENCY
  };
  
  /** 
   * Navigation class implements the singleton pattern
   */
  private Navigation() {
	  
  }
  
  /**
   *  Get instance of the Navigation class. 
   *  Only allows one thread at a time calling this method.
   */
  public synchronized static Navigation getNavigator() {
	    if (navigator == null) {
	      navigator = new Navigation();
	    }
	    return navigator;
	  }

  /**
   * The current state of the robot.
   */
  static State state;

  /**
   * {@code true} when robot is traveling.
   */
  public static boolean traveling = false; // false by default
  
  /**
   * {@code true} when obstacle is avoided.
   */
  public static boolean safe;

  /**
   * The destination x.
   */
  public static double destx;
  
  /**
   * The destination y.
   */
  public static double desty;

  @Override
  public void run() {
    // TODO Auto-generated method stub
	  int selectedRoute = 0;
  switch(selectedRoute) {
  	  case 1: 
  		  currentWaypoints = waypoints1; 
  		  break;
  	  case 2:
  		  currentWaypoints = waypoints2;
  		  break;
  	  case 3: 
  		  currentWaypoints = waypoints3;
  		  break;
  	  case 4:
  		  currentWaypoints = waypoints4;
  		  break;
  	  default:
  		  System.out.println("Please select a valid circuit");
  		  break;    		  
    }
    
  }
  
  private void travelTo(double x,double y, boolean avoid) {
	  /**
	   * This method causes the robot to travel to the absolute field location (x, y), specified in tile points. 
	   * This method should continuously call turnTo(double theta) and then set the motor speed to forward (straight). 
	   * This will make sure that your heading is updated until you reach your exact goal. 
	   * This method will poll the odometer for information.
	   */
	  // TODO: Implement this method
	  traveling = true;
  }
  
  private boolean isNavigating() {	
	  /**
	   * This method returns true if another thread has called travelTo() or turnTo() and the method has yet to return; false otherwise.
	   */
	return traveling;	  
  }
  
  private void turnTo(double theta) {
	  /**
	   * This method causes the robot to turn (on point) to the absolute heading theta. 
	   * This method should turn a MINIMAL angle to its target.
	   */
	// TODO: Implement this method
	  traveling = true;
  }
  
	/**
   * Completes a course.
   */
//  void completeCourse(int selectedRoute) {
//    
//  
//    for (int[] point : currentWaypoints) {
//      travelTo(point[0]*TILE_SIZE, point[1]*TILE_SIZE);
//      while (Navigation.traveling) {
//        try {
//          Thread.sleep(500);
//        } catch (InterruptedException e) {
//          // There is nothing to be done here
//        }
//      
//      }
//    }
//  }

  
}
