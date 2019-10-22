package ca.mcgill.ecse211.lab5.tests;


public class findDestMethodTests {
  static double currentX;
  static double currentY;
  static double launchRange;
  final static double BOUNDARY=0;
  
  /**
   * This method uses the given target position (targetX,targetY) to find the ideal launching
   * position. x and y are in unit cm. 
   * 
   * @param targetX
   * @param targetY
   */
  public static String findDest(double targetX, double targetY) {
    Point2d curPosition = new Point2d(currentX, currentY);
    Point2d throwTo = new Point2d(targetX, targetY);
    
    Vector2d yAxis = new Vector2d(0,1);
    Vector2d trajectory = new Vector2d( (-currentX+targetX),(-currentY+targetY));
    double theta = yAxis.angle(trajectory);
    
    double launchX, launchY;
    double dx,dy;
    // calculate the intersection of the circle and the line
    if(currentX - targetX > 0) { // when the robot is in 1st/2nd quadrant
      dy = - launchRange * Math.cos(theta);
      dx =   launchRange * Math.sin(theta);
      launchY = targetY + dy;
      launchX = targetX + dx;
    } else {  // in 3rd/4th quadrant
      dy = - launchRange * Math.cos(theta);
      dx = - launchRange * Math.sin(theta);
      launchY = targetY + dy;
      launchX = targetX + dx; // TODO: test later
    }
    
    if(launchX <= BOUNDARY || launchY <= BOUNDARY) {
      Point2d target = findCircle(curPosition, throwTo);
      launchX = target.x;
      launchY = target.y;
    }
    System.out.println("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY));
    return "I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY);
    
  }
  
  /**
   * This method uses the given target position (targetX,targetY) to find the ideal launching
   * position. x and y are in unit cm. 
   * 
   * @param targetX
   * @param targetY
   */
  public static String findDestAtan(double targetX, double targetY) {
    Point2d curPosition = new Point2d(currentX, currentY);
    Point2d throwTo = new Point2d(targetX, targetY);
    
    double theta = Math.atan2(currentX-targetX, currentY-targetY);
    
    double launchX, launchY;
    double dx,dy;
    // calculate the intersection of the circle and the line
    if(theta < 0) { // when the robot is in 2nd/3rd quadrant
      dy =   launchRange * Math.cos(-theta);
      dx = - launchRange * Math.sin(-theta);
      launchY = targetY + dy;
      launchX = targetX + dx;
    } else {  // in 1st/4th quadrant
      dy =   launchRange * Math.cos(theta);
      dx =   launchRange * Math.sin(theta);
      launchY = targetY + dy;
      launchX = targetX + dx; // TODO: test later
    }
    
    if(launchX <= BOUNDARY || launchY <= BOUNDARY) {
      Point2d target = findCircle(curPosition, throwTo);
      launchX = target.x;
      launchY = target.y;
    }
    System.out.println("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY));
    return "I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY);
    
  }
  
  private static Point2d findCircle (Point2d curPos, Point2d center) {
    Point2d target = new Point2d();
    if(center.x > center.y) { // upper half
      double tX = curPos.x;
      double tY = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos.x - center.x),2)) + center.y;
      target = new Point2d(tX, tY);
    }else {  // right half
      double tY = curPos.y;
      double tX = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos.y - center.y),2)) + center.x;
      target = new Point2d(tX, tY);
    }
    return target;
  }
  
  private static double keep3Digits(double number) {
    int temp = (int)(number * 1000);
    //Add 1 if the difference is larger than 0.5
    if (number * 1000 - temp > 0.5) {
      temp += 1;
    }
    number = temp / 1000.0 + 0.000;
    return number;
  }
  
  @Test
  public void testQuadrant1() {
    currentX = 5;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDest(targetX,targetY));
  }
  
  @Test
  public void testQuadrant2() {
    currentX = 1;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDest(targetX,targetY));
  }
  
  @Test
  public void testQuadrant3() {
    currentX = 1;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDest(targetX,targetY));
  }
  
  @Test
  public void testQuadrant4() {
    currentX = 5;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDest(targetX,targetY));
  }
  
  @Test
  public void testQuadrant1ViaAtan() {
    currentX = 5;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant2ViaAtan() {
    currentX = 1;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant3ViaAtan() {
    currentX = 1;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant4ViaAtan() {
    currentX = 5;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testBelowBoundaryGoRight() {
    currentX = 1;
    currentY = 1;
    launchRange = 3 * Math.pow(2, 0.5);
    double targetX = 2;
    double targetY = 3;
    double launchY = currentY;
    double launchX = Math.pow(Math.pow(launchRange, 2) - Math.pow(currentY-targetY, 2),0.5)+targetX;
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testBelowBoundaryGoUp() {
    currentX = 1;
    currentY = 1;
    launchRange = 3 * Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 2;
    double launchY = Math.pow(Math.pow(launchRange, 2) - Math.pow(currentX-targetX, 2),0.5)+targetY;
    double launchX = currentX;
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
}
