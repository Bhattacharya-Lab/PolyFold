/* ***************************** PACKAGE ************************************ */
package pf.ui;
/* ************************* JAVAFX IMPORTS ********************************* */
import javafx.scene.paint.*;
/* *************************** PF IMPORTS *********************************** */
import pf.controllers.Controller;

/* Class for managing colors, hues, etc. */
public class Colors {
  // Colors and materials
  public final static PhongMaterial RED = new PhongMaterial(Color.web("ec5f66"));
  public final static PhongMaterial YELLOW = new PhongMaterial(Color.web("f9d547"));
  public final static PhongMaterial GREEN = new PhongMaterial(Color.web("99c794"));
  public final static PhongMaterial BLUE = new PhongMaterial(Color.web("6699cc"));
  public final static Color WHITE = Color.WHITE;
  public final static Color BLACK = Color.BLACK;
  public final static Color INVALID = Color.rgb(100, 0, 0);
  public final static Color VIEWPORT = Color.web("0f1417");
  // Hues
  public final static double BLUE_HUE = Color.BLUE.getHue();
  public final static double HUE_RANGE = Color.RED.getHue() - BLUE_HUE;

  /* Convert a value to a color based on current protein */
  public static Color getColorFromValue(double value) {
    double maxValue = Controller.currentSequenceMaxDist;
    if (value < 0) return Color.hsb(BLUE_HUE, 1.0, 1.0);
    if (value > maxValue) return INVALID;
    double hue = BLUE_HUE + HUE_RANGE * value / maxValue;
    return Color.hsb(hue, 1.0, 1.0);
  }
}
