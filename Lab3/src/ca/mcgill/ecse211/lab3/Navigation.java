package ca.mcgill.ecse211.lab3;



public class Navigation implements Runnable{

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
   * The current state of the robot.
   */
  static State state;

  /**
   * {@code true} when robot is traveling.
   */
  public static boolean traveling; // booleans are false by default
  
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
    
  }
  
  public static void travelTo(double x,double y, boolean avoid) {
  }
}
