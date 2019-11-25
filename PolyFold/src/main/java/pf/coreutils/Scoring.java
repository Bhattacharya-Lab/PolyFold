package pf.coreutils;

import pf.controllers.Controller;
import pf.representations.Converter;

public class Scoring {
  public static double score;
  public static double maxScore;
  public static double[][][] contactTable;

  /* Main piecewise score function for scoring a protein */
  public static double piecewiseScore(int i, int j, double dist) {
    assert contactTable != null;
    // Lower bound
    double lb = contactTable[i][j][0];
    // Upper bound
    double ub = contactTable[i][j][1];
    if (dist < lb) return Math.pow(dist - lb, 2);
    if (dist <= ub) return 0.0;
    if (dist <= ub + 0.5) return Math.pow(dist - ub, 2);
    return dist - ub - 0.25;
  }

  /* Update current score in PolyFold based on adjacency matrix */
  public static void updateScore(double[][] adj) {
    score = 0.0;
    for (int i = 0; i < adj.length; i++) {
      for (int j = 0; j < adj[0].length; j++) {
        score += piecewiseScore(i, j, adj[i][j]);
      }
    }
  }

  /* Calculate a hypothetical score in PolyFold based on a hypothetical
   * adjacency matrix (particularly useful for Monte Carlo)
   */
  public static double calculateScore(double[][] adj) {
    double currentScore = 0.0;
    for (int i = 0; i < adj.length; i++) {
      for (int j = 0; j < adj[0].length; j++) {
        currentScore += piecewiseScore(i, j, adj[i][j]);
      }
    }
    return currentScore;
  }

  /* Calculate and set maximum score for a given protein */
  public static void setMaxScore() {
    maxScore = 0.0;
    for (int i = 0; i < contactTable.length; i++) {
      for (int j = 0; j < contactTable[0].length; j++) {
        maxScore += Math.max(
            piecewiseScore(i, j, 0),
            piecewiseScore(i, j, Math.abs(j-i) * Converter.BOND_LEN));
      }
    }
  }

  /* Get score as a percentage of the maximum possible score */
  public static double scorePercentage() {
    return (maxScore - score) / maxScore;
  }

  /* Test a new score as a percentage of maximum - mainly useful for Monte
   * Carlo optimization
   */
  public static double scorePercentage(double testScore) {
    return testScore / maxScore;
  }

  /* Return numerator part of fractional score */
  public static int scoreNumerator() { return (int) (maxScore - score); }

  /* Return denominator part of fractional score */
  public static int scoreDenominator() { return (int) maxScore; }
}
