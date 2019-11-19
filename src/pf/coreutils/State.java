/******************************* PACKAGE **************************************/
package pf.coreutils;
/***************************** PF IMPORTS *************************************/
import pf.representations.Angular;

/* Class for encoding the state of a folded protein */
public class State {
  public Angular[] state;

  /* Constructor */
  public State(Angular[] angles) {
    // Avoid shared references
    this.state = new Angular[angles.length];
    for (int i = 0; i < angles.length; i++) {
      // This uses the Angular copy constructor
      Angular a = new Angular(angles[i]);
      this.state[i] = a;
    }
  }
}
