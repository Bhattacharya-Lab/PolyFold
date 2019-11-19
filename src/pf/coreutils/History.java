package pf.coreutils;

import java.util.*;

import pf.coreutils.State;
import pf.representations.Angular;

public class History {
  // Fields for saving and loading states
  public static final int MAX_SAVE_STATES = 10;
  public static State[] quickSaves = new State[MAX_SAVE_STATES];
  public static HashMap<String, State> namedStates = new HashMap<>();
  // Track previous states for undo and redo
  public static State currentState;
  public static Stack<State> undos = new Stack<>();
  public static Stack<State> redos = new Stack<>();

  /* Go back exactly one state and save current state to redo */
  public static State undo() {
    if (undos.empty()) return null;
    redos.push(currentState);
    // Must be very careful with shared references.
    currentState = new State(undos.pop().state);
    return currentState;
  }

  /* Go forward exectly one state and save current state to undo */
  public static State redo() {
    if (redos.empty()) return null;
    undos.push(currentState);
    // Must be very careful with shared references.
    currentState = new State(redos.pop().state);
    return currentState;
  }

  /* Add the given state to our undo stack. This is only done when new states
   * are explored, so redos must be cleared as this is a new timeline of events
   */
  public static void addUndoState(Angular[] angles) {
    redos.clear();
    undos.push(new State(angles));
  }

  /* Allow PolyFold to notify when new states are explored. This prevents async
   * issues from arising.
   */
  public static void setCurrentState(Angular[] angles) {
    if (angles == null) return;
    currentState = new State(angles);
  }

  public static void quickSave(Angular[] angles, int index) {
    if (angles == null) return;
    quickSaves[index] = new State(angles);
  }

  public static State quickLoad(int index) {
    if (quickSaves[index] == null) return null;
    undos.clear();
    return quickSaves[index];
  }

  public static void clear() {
    for (int i = 0; i < quickSaves.length; i++) quickSaves[i] = null;
    namedStates.clear();
    undos.clear();
    redos.clear();
  }

  public static void addNamedSaveState(String name, Angular[] angles) {
    if (angles == null) return;
    namedStates.put(name, new State(angles));
  }

  public static State loadNamedSaveState(String name) {
    if (!namedStates.containsKey(name)) return null;
    return namedStates.get(name);
  }
}
