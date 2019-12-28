package pf.optimizations;

import java.util.Random;

import pf.controllers.Controller;
import pf.coreutils.*;
import pf.geometry.LinearAlgebra;
import pf.representations.*;

public class MonteCarlo {
  // Random number generator with random seed
  public static Random random = new Random();
  // Used only in calculations of other parameters
  public static final int MAX_ITERS = 8000;
  public static double startTemp = 1.0;
  public static double terminalTemp = 0.01;
  public static double temperature = 1.0;
  // These values result in roughly MAX_ITERS iterations
  public static double decay = 0.9;
  public static int itersPerDecayLevel = 180;
  // Lowest energy state
  public static Angular[] lowest;
  public static double lowestEnergy;
  public static double currentEnergy;

  /* Set necessary pre-conditions for Monte Carlo */
  public static void setup() {
    temperature = startTemp;
    lowest = null;
    lowestEnergy = Limits.INF;
  }

  /* Set the random seed */
  public static void setSeed(long seed) { random.setSeed(seed); }

  /* Set starting termperature */
  public static void setStartTemp(double temp) { startTemp = temp; }

  /* Set terminal temperature */
  public static void setTerminalTemp(double temp) {
    if (temp <= 0) temp = 0.01;
    terminalTemp = temp;
  }

  /* Utility to test completion condition */
  public static boolean complete() { return temperature < terminalTemp; }

  /* Set decay rate and number of iters to stay in each power of that rate */
  public static void setDecay(double d) {
    if (d >= 1.0) d = 0.99;
    if (d < 0.1) d = 0.1;
    decay = d;
    int itersToCompletion =
        (int) ((Math.log(terminalTemp) - Math.log(startTemp)) / Math.log(decay));
    itersPerDecayLevel = Math.max(1, MAX_ITERS / Math.max(1, itersToCompletion));
  }

  /* Return the temperature of a given iteration with a given initial
   * temperature and decay
   */
  public static void setTemperature(int iter) {
    temperature = startTemp * Math.pow(decay, iter / itersPerDecayLevel);
  }

  /* Return the acceptance probability of a given cost with a given
   * temperature
   */
  public static boolean acceptProbability(double energyPrime, double energy) {
    return random.nextDouble() < Math.exp((energy - energyPrime) / temperature);
  }

  /* Get next state of angles array after applying random to walk to k-Mer */
  public static Angular[] getRandomNeighbor(Angular[] currState) {
    // Avoid shared reference
    Angular[] state = Converter.angularCopy(currState);
    int i = random.nextInt(/*bound=*/state.length);
    char ss = state[i].ss;
    if (i != 0 && i != state.length - 1) {
      double dTheta = temperature * Limits.angleEditRange(ss);
      dTheta = -dTheta / 2.0 + dTheta * random.nextDouble();
      state[i].theta = Limits.clipPlanar(state[i].theta + dTheta, ss);
      if (i != state.length - 2) {
        double dTao = temperature * Limits.angleEditRange(ss);
        dTao = -dTao / 2.0 + dTao * random.nextDouble();
        state[i].tao = Limits.clipDihedral(state[i].tao + dTao, ss);
      }
    }
    return state;
  }

  /* Take one random walk step of MC optimization */
  public static Angular[] getNextState(Angular[] currState) {
    Residue[] residues = Converter.anglesToResidues(currState);
    double[][] adj = LinearAlgebra.adjacencyMatrix(residues);
    double energy = scoreToEnergy(Scoring.calculateScore(adj));
    if (lowest == null) {
      lowest = currState;
      lowestEnergy = energy;
      currentEnergy = energy;
    }
    Angular[] nextState = getRandomNeighbor(currState);
    residues = Converter.anglesToResidues(nextState);
    adj = LinearAlgebra.adjacencyMatrix(residues);
    double energyPrime = scoreToEnergy(Scoring.calculateScore(adj));
    if (energyPrime < energy || acceptProbability(energyPrime, energy)) {
      if (energyPrime < lowestEnergy) {
        lowest = nextState;
        lowestEnergy = energyPrime;
      }
      currentEnergy = energyPrime;
      return nextState;
    }
    return currState;
  }

  /* Convert score percent to energy */
  public static double scoreToEnergy(double score) {
    return Scoring.scorePercentage(score);
  }

  /* Return the lowest energy state */
  public static Angular[] recoverLowest() { return lowest; }
}
