package ca.mcgill.ecse211.lab1;

import lejos.hardware.Button;

import static ca.mcgill.ecse211.lab1.Resources.*;
import javax.swing.JOptionPane;

/**
 * Main class of the program.
 */
public class Main {
  
  /**
   * The US controller selected by the user (bang-bang or P-type).
   */
  public static UltrasonicController selectedController;

  /**
   * Main entry point - instantiate objects used and set up sensor
   * @param args
   */
  public static void main(String[] args) {
    // Set up the display on the EV3 screen and wait for a button press. 
    // The button ID (option) determines what type of control to use
    Printer.printMainMenu();
    int option = Button.waitForAnyPress(3000);  // set 5000 ms timeout (wait for 5 seconds, then continue)

    // Adding an UI for choosing controller type 
    String[] controllers = {"Bang-Bang", "P-Type"};
    int choice = JOptionPane.showOptionDialog(null,
        "Choose controller type",   // pop-up message 
        "Controller selection",     // pop-up message title 
        JOptionPane.DEFAULT_OPTION, 
        JOptionPane.INFORMATION_MESSAGE, 
        null, 
        controllers, 
        controllers[0]);
    
    if (option == Button.ID_LEFT || choice == 0) {
      selectedController = new BangBangController();
    } else if (option == Button.ID_RIGHT || choice == 1) {
      selectedController = new PController();
    } else {
      showErrorAndExit("Error - invalid button!");
    }

    // Start the poller and printer threads
    new Thread(new UltrasonicPoller()).start();
    new Thread(new Printer()).start();

    // Wait here until button pressed to terminate wall follower
    Button.waitForAnyPress();   // if button is pressed, program will go to next line.
    System.exit(0);
  }

  /**
   * Shows error and exits program.
   */
  public static void showErrorAndExit(String errorMessage) {
    TEXT_LCD.clear();
    System.err.println(errorMessage);
    
    // Sleep for 2 seconds so user can read error message
    try {
      Thread.sleep(40*SLEEPINT);
    } catch (InterruptedException e) {
    }
    
    System.exit(-1);
  }
  
}
