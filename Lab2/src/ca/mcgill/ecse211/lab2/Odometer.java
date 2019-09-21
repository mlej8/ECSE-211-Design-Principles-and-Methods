package ca.mcgill.ecse211.lab2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab2.Resources.*;

/**
 * The odometer class keeps track of the robot's (x, y, theta) position.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 * @author Younes Boubekeur
 */

public class Odometer implements Runnable {

  /**
   * The x-axis position in cm.
   */
  private volatile double x; // volatile keyword in Java is a keyword for making class thread safe. Thread safe means
                             // that a method or class instance can be used by multiple threads at the same time without
                             // any problem

  /**
   * The y-axis position in cm.
   */
  private volatile double y;

  /**
   * The orientation in degrees.
   */
  private volatile double theta;

  /**
   * The (x, y, theta) position as an array
   */
  private double[] position;

  // Thread control tools
  /**
   * Fair lock for concurrent writing
   */
  private static Lock lock = new ReentrantLock(true);

  /**
   * Indicates if a thread is trying to reset any position parameters
   */
  private volatile boolean isResetting = false;

  /**
   * Lets other threads know that a reset operation is over.
   */
  private Condition doneResetting = lock.newCondition();

  private static Odometer odo; // Returned as singleton

  // Motor-related variables
  private static int leftMotorTachoCount = 0; // current left wheel's tachometer count
  private static int rightMotorTachoCount = 0; // current right wheel's tachometer count
  private static int lastTachoCountL; // left wheel's last tachometer count
  private static int lastTachoCountR; // right wheel's last tachometer count
  
  /**
   * The odometer update period in ms.
   */
  private static final long ODOMETER_PERIOD = 25;

  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It cannot be accessed
   * externally.
   */
  private Odometer() {
    setXYT(0, 0, 0);
  }

  /**
   * Returns the Odometer Object. Use this method to obtain an instance of Odometer.
   * 
   * @return the Odometer Object
   */
  public synchronized static Odometer getOdometer() {
    if (odo == null) {
      odo = new Odometer();
    }
    return odo;
  }

  /**
   * This method is where the logic for the odometer will run.
   */
  public void run() {
    long updateStart, updateEnd;
    
    // Clear tacho counts and put motors in freewheel mode. 
    // Then initialize tacho count variable to its current state.
    leftMotor.resetTachoCount(); 
    rightMotor.resetTachoCount(); 
    lastTachoCountL = leftMotor.getTachoCount(); 
    lastTachoCountR = rightMotor.getTachoCount();
    
    while (true) {
      updateStart = System.currentTimeMillis();

      leftMotorTachoCount = leftMotor.getTachoCount(); // returns the tachometer count in degrees. A tachometer is a an
                                                       // instrument which measures the working speed of a vehicle,
                                                       // typically in revolutions per minute.
      rightMotorTachoCount = rightMotor.getTachoCount();
      
      double distL, distR, deltaD, deltaT, dX, dY;

      distL = Math.PI*WHEEL_RAD*(leftMotorTachoCount-lastTachoCountL)/180; // compute wheel displacements 
      distR = 3.14159*WHEEL_RAD*(rightMotorTachoCount-lastTachoCountR)/180; 
      
      // save tacho counts for next iteration
      lastTachoCountL = leftMotorTachoCount;
      lastTachoCountR = rightMotorTachoCount; 
      deltaD = 0.5*(distL+distR);                           // compute vehicle displacement
      deltaT = (distL-distR)/WHEEL_RAD;                     // compute change in heading
//      this.theta += Math.toDegrees(deltaT);                 // update heading and transform it to degrees
      dX = deltaD * Math.sin(Math.toRadians(this.theta));   // compute X component of displacement   
      dY = deltaD * Math.cos(Math.toRadians(this.theta));   // compute Y component of displacement X = X + dX; Y = Y + dY;
      
      // update estimates of X and Y position
      
//      this.x = this.x + dX;
//      this.y = this.y + dY;
      
      // TODO Calculate new robot position based on tachometer counts

      // TODO Update odometer values with new calculated values, eg
      odo.update(dX, dY, Math.toDegrees(deltaT));

  // this ensures that the odometer only runs once every period
  updateEnd=System.currentTimeMillis();if(updateEnd-updateStart<ODOMETER_PERIOD)

  {
    try {
      Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
    } catch (InterruptedException e) {
      // there is nothing to be done
    }
  }
  }}

  // IT IS NOT NECESSARY TO MODIFY ANYTHING BELOW THIS LINE

  /**
   * Returns the Odometer data.
   * <p>
   * Writes the current position and orientation of the robot onto the odoData array. {@code odoData[0] =
   * x, odoData[1] = y; odoData[2] = theta;}
   * 
   * @param position the array to store the odometer data
   * @return the odometer data.
   */
  public double[] getXYT() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      position[0] = x;
      position[1] = y;
      position[2] = theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return position;
  }

  /**
   * Adds dx, dy and dtheta to the current values of x, y and theta, respectively. Useful for odometry.
   * 
   * @param dx
   * @param dy
   * @param dtheta
   */
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isResetting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360; // keeps the updates within 360 degrees
      isResetting = false;
      doneResetting.signalAll(); // Let the other threads know we are done resetting
    } finally {
      lock.unlock();
    }

  }

  /**
   * Overrides the values of x, y and theta. Use for odometry correction.
   * 
   * @param x the value of x
   * @param y the value of y
   * @param theta the value of theta in degrees
   */
  public void setXYT(double x, double y, double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites x. Use for odometry correction.
   * 
   * @param x the value of x
   */
  public void setX(double x) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites y. Use for odometry correction.
   * 
   * @param y the value of y
   */
  public void setY(double y) {
    lock.lock();
    isResetting = true;
    try {
      this.y = y;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites theta. Use for odometry correction.
   * 
   * @param theta the value of theta
   */
  public void setTheta(double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

}
