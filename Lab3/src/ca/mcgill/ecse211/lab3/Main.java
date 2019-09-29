package ca.mcgill.ecse211.lab3;
import static ca.mcgill.ecse211.lab3.Resources.*;
import lejos.hardware.Button;

public class Main {
    public static void main(String args[]) {
      int selectedRoute=0;
      Log.setLogging(true, true, false, true);
      new Thread(usPoller).start();
      new Thread(odometer).start();
      new Thread(obstacleAvoidance).start();
      //TODO: Button select
      completeCourse(selectedRoute);

      while (Button.waitForAnyPress() != Button.ID_ESCAPE)
        ; // do nothing
      
      System.exit(0);
    }
    
    /**
     * Completes a course.
     */
    private static void completeCourse(int selectedRoute) {
      int[][] waypoints1 = {{1, 3}, {2, 2}, {3, 3}, {3, 2}, {2, 1}};
      int[][] waypoints2 = {{2, 2}, {1, 3}, {3, 3}, {3, 2}, {2, 1}};
      //TODO: insert waypoints as specified in pdf
      int[][] waypoints3 = {{1,1},{1,2}};
      int[][] waypoints4 = {{2,2},{2,2}};
      
      //TODO: assign waypoints to selected waypoints
      int[][] waypoints=waypoints1; 
      
      for (int[] point : waypoints) {
        Navigation.travelTo(point[0]*TILE_SIZE, point[1]*TILE_SIZE, true);
        while (Navigation.traveling) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            // There is nothing to be done here
          }
        
        }
      }
    }
   
}
