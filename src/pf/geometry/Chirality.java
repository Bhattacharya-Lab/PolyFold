package pf.geometry;

import static java.lang.Math.*;

import pf.representations.*;
import static pf.geometry.LinearAlgebra.*;

public class Chirality {
  public static boolean hasCorrectChirality(Cartesian[] carts) {
    Point[] chiralAlpha = new Point[]{new Point(), new Point(), new Point()};
    Point[] chiralBeta = new Point[]{new Point(), new Point(), new Point()};
    // distances between residues
    double[] dist = new double[carts.length-1];
    dist[0] = magnitude(pointSubtract(carts[0].ca, carts[1].ca));
    dist[1] = magnitude(pointSubtract(carts[1].ca, carts[2].ca));
    // track percentages of right handed helices and left handed strands
    int rightHandedHelix = 0;
    int totalHelix = 0;
    int leftHandedStrand = 0;
    int totalStrand = 0;
    for (int i = 3; i < carts.length; i++) {
      dist[i-1] = magnitude(pointSubtract(carts[i-1].ca, carts[i].ca));
      if (carts[i].ss == 'H' && carts[i-1].ss == 'H' && carts[i-2].ss == 'H' &&
          carts[i-3].ss == 'H') {
        chiralAlpha[0] = pointSubtract(carts[i-2].ca, carts[i-3].ca);
        chiralAlpha[1] = pointSubtract(carts[i-1].ca, carts[i-2].ca);
        chiralAlpha[2] = pointSubtract(carts[i].ca, carts[i-1].ca);
        double alpha = determinant(chiralAlpha) /
            (dist[i-1] * dist[i-2] * dist[i-3]);
        if (alpha > 0) rightHandedHelix++;
        totalHelix++;
      }
      if (carts[i].ss == 'E' && carts[i-1].ss == 'E' && carts[i-2].ss == 'E' &&
          carts[i-3].ss == 'E') {
        chiralBeta[0] = pointSubtract(carts[i-2].ca, carts[i-3].ca);
        chiralBeta[1] = pointSubtract(carts[i-1].ca, carts[i-2].ca);
        chiralBeta[2] = pointSubtract(carts[i].ca, carts[i-1].ca);
        double beta = determinant(chiralBeta) /
            (dist[i-1] * dist[i-2] * dist[i-3]);
        // implies right handed beta strand
        if (beta < 0) leftHandedStrand++;
        totalStrand++;
      }
    }
    // if both score > 50%, do nothing, otherwise invert
    double alphaScore, betaScore;
    if (totalHelix != 0 && totalStrand != 0) {
      alphaScore = rightHandedHelix / (double) totalHelix;
      betaScore = leftHandedStrand / (double) totalStrand;
      return alphaScore > 0.5 && betaScore > 0.5;
    } else if (totalHelix == 0) {
      betaScore = leftHandedStrand / (double) totalStrand;
      return betaScore > 0.5;
    } else {
      alphaScore = rightHandedHelix / (double) totalHelix;
      return alphaScore > 0.5;
    }
  }
}
