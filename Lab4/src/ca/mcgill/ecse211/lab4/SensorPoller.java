package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

/**
 * Samples the US sensor and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller
 * thread. The while loop at the bottom executes in a loop. Assuming that the
 * us.fetchSample, and cont.processUSData methods operate in about 20ms, and
 * that the thread sleeps for 50 ms at the end of each loop, then one cycle
 * through the loop is approximately 70 ms. This corresponds to a sampling rate
 * of 1/70ms or about 14 Hz.
 */
public class SensorPoller implements Runnable {

    private float[] usData;
    private float[] lightData;
    private Mode mode;
    
    public enum Mode 
    { 
        ULTRASONIC, LIGHT; 
    } 
      
    public SensorPoller() {
        usData = new float[usSensor.sampleSize()]; // create an array of float of size corresponding to the number of
                                                    // elements in a sample. The number of elements does not change
        lightData = new float[lightSensor.sampleSize()];                                         // during
        
        this.mode = Mode.ULTRASONIC;
    }

    /*
     * Sensors now return floats using a uniform protocol. Need to convert US result
     * to an integer [0,255] (non-Javadoc).
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {

      
        while (true) {
            if(mode==Mode.ULTRASONIC) {
            usSensor.getDistanceMode().fetchSample(usData, 0);  // acquire distance data in meters and store it in
                                                                // usData (an array of float)
            ultrasonicLocalizer.processUSData((int) (usData[0] * 100.0)); // extract from buffer (region of a physical
                                                                          // memory storage used to
                                                                          // temporarily store data while it is being moved from one place to
                                                                          // another), convert to cm, cast to int
            }else if(mode==Mode.LIGHT){
              lightSensor.getRedMode().fetchSample(lightData, 0);
              lightLocalizer.processData((int) (lightData[0] * 100.0));;
            }
            Main.sleepFor(SLEEPINT);
        }
        
        //Switch to lightSensor}
      
    }
    
    public void setMode(Mode selectedMode) {
      mode = selectedMode;
    }
}