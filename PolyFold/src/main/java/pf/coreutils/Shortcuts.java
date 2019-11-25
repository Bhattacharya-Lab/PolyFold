/******************************* PACKAGE **************************************/
package pf.coreutils;
/*************************** JAVAFX IMPORTS ***********************************/
import javafx.scene.input.*;
/***************************** PF IMPORTS *************************************/
import pf.controllers.Controller;
import pf.coreutils.History;
import pf.representations.Angular;

/* Class for handling shortcuts for save and load states which can be
 * tedious and verbose in main controller
 */
public class Shortcuts {
  // Index which quick state was saved to
  public int savedTo = -1;
  // Index which quick state was successfully loaded from
  public int loadedFrom = -1;
  // State which was loaded on success
  public State loadedState = null;

  /* Handle the various key codes which would save a state; modifiers are
   * handled in main controller
   */
  public void handleSave(KeyEvent e, Angular[] angles) {
    savedTo = -1;
    if (e.getCode().equals(KeyCode.DIGIT0)) savedTo = 0;
    if (e.getCode().equals(KeyCode.DIGIT1)) savedTo = 1;
    if (e.getCode().equals(KeyCode.DIGIT2)) savedTo = 2;
    if (e.getCode().equals(KeyCode.DIGIT3)) savedTo = 3;
    if (e.getCode().equals(KeyCode.DIGIT4)) savedTo = 4;
    if (e.getCode().equals(KeyCode.DIGIT5)) savedTo = 5;
    if (e.getCode().equals(KeyCode.DIGIT6)) savedTo = 6;
    if (e.getCode().equals(KeyCode.DIGIT7)) savedTo = 7;
    if (e.getCode().equals(KeyCode.DIGIT8)) savedTo = 8;
    if (e.getCode().equals(KeyCode.DIGIT9)) savedTo = 9;
    if (savedTo != -1) History.quickSave(angles, savedTo);
  }

  /* Handle the various key codes which would load a state; modifiers are
   * handled in the main controller; loadedState is null on failure
   */
  public void handleLoad(KeyEvent e) {
    loadedFrom = -1;
    loadedState = null;
    if (e.getCode().equals(KeyCode.DIGIT0)) loadedFrom = 0;
    if (e.getCode().equals(KeyCode.DIGIT1)) loadedFrom = 1;
    if (e.getCode().equals(KeyCode.DIGIT2)) loadedFrom = 2;
    if (e.getCode().equals(KeyCode.DIGIT3)) loadedFrom = 3;
    if (e.getCode().equals(KeyCode.DIGIT4)) loadedFrom = 4;
    if (e.getCode().equals(KeyCode.DIGIT5)) loadedFrom = 5;
    if (e.getCode().equals(KeyCode.DIGIT6)) loadedFrom = 6;
    if (e.getCode().equals(KeyCode.DIGIT7)) loadedFrom = 7;
    if (e.getCode().equals(KeyCode.DIGIT8)) loadedFrom = 8;
    if (e.getCode().equals(KeyCode.DIGIT9)) loadedFrom = 9;
    if (loadedFrom == -1) return;
    loadedState = History.quickLoad(loadedFrom);
  }
}
