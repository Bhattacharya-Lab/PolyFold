package pf.representations;

import static pf.geometry.LinearAlgebra.*;

public class Converter {
  public static final double BOND_LEN = 3.8;

  public static Angular[] cartsToAngles(Cartesian[] carts) {
    int n = carts.length;
    Angular[] angles = new Angular[n];
    Angular a;
    for (int i = 0; i < n; i++) {
      a = new Angular();
      a.id = i;
      // tao
      if (i == 0 || i == n-1 || i == n-2) a.tao = 2 * Math.PI;
      else a.tao = dihedralAngle(carts[i-1].ca, carts[i].ca, carts[i+1].ca,
                                 carts[i+2].ca);
      // theta
      if (i == 0 || i == n-1) a.theta = 2 * Math.PI;
      else a.theta = planarAngle(carts[i-1].ca, carts[i].ca, carts[i+1].ca);
      a.aa = carts[i].aa;
      a.ss = carts[i].ss;
      angles[i] = a;
    }
    return angles;
  }

  public static Cartesian[] anglesToCarts(Angular[] angles) {
    int n = angles.length;
    Cartesian[] carts = new Cartesian[n];
    // virtual N terminus
    Point N = new Point(0.0 - Math.cos(Math.PI) * BOND_LEN,
                        0.0 - Math.sin(Math.PI) * BOND_LEN, 0.0);
    // first node at origin
    Cartesian c = new Cartesian();
    c.ca = new Point(0.0, 0.0, 0.0);
    c.aa = angles[0].aa;
    c.ss = angles[0].ss;
    carts[0] = c;
    // second node at bond length along x axis
    c = new Cartesian();
    c.ca = new Point(BOND_LEN, 0.0, 0.0);
    c.aa = angles[1].aa;
    c.ss = angles[1].ss;
    carts[1] = c;
    // third node at bond angle and bond length
    c = new Cartesian();
    c.ca = setCoordinate(N, carts[0].ca, carts[1].ca, c.ca, angles[1].theta,
                         angles[1].tao);
    c.aa = angles[2].aa;
    c.ss = angles[2].ss;
    carts[2] = c;
    for (int i = 3; i < n; i++) {
      c = new Cartesian();
      c.ca = setCoordinate(carts[i-3].ca, carts[i-2].ca, carts[i-1].ca, c.ca,
                           angles[i-1].theta, angles[i-2].tao);
      c.aa = angles[i].aa;
      c.ss = angles[i].ss;
      carts[i] = c;
    }
    return carts;
  }

  public static Cartesian[] residuesToCarts(Residue[] residues) {
    Cartesian[] carts = new Cartesian[residues.length];
    for (int i = 0; i < residues.length; i++) {
      carts[i] = new Cartesian();
      carts[i].ca = residues[i].getCenter();
      carts[i].aa = residues[i].aa;
      carts[i].ss = residues[i].ss;
    }
    return carts;
  }

  public static Residue[] anglesToResidues(Angular[] angles) {
    Cartesian[] carts = anglesToCarts(angles);
    Residue[] residues = new Residue[carts.length];
    for (int i = 0; i < carts.length; i++) {
      Residue r = new Residue(carts[i], (i == carts.length-1));
      r.id = i;
      residues[i] = r;
      if (i > 0) residues[i-1].rotateRod(residues[i]);
    }
    return residues;
  }

  public static Point setCoordinate(Point a, Point b, Point c, Point d,
                                    double theta, double tao) {
    double nsa, nca, nct;
    Point u = pointSubtract(b, a);
    Point v = unitVector(pointSubtract(c, b));
    Point norm = unitVector(crossProduct(u, v));
    Point vCrossNorm = unitVector(crossProduct(v, norm));
    vCrossNorm = scale(vCrossNorm, -1 * Math.cos(tao));
    norm = scale(norm, Math.sin(tao));
    v = scale(v, Math.tan(theta - Math.PI / 2.0));
    u = pointAdd(pointAdd(vCrossNorm, norm), v);
    u = scale(unitVector(u), BOND_LEN);
    return pointAdd(u, c);
  }

  public static Angular[] angularCopy(Angular[] angles) {
    Angular[] copy = new Angular[angles.length];
    for (int i = 0; i < angles.length; i++) copy[i] = new Angular(angles[i]);
    return copy;
  }

  public static Cartesian[] cartesianCopy(Cartesian[] carts) {
    Cartesian[] copy = new Cartesian[carts.length];
    for (int i = 0; i < carts.length; i++) copy[i] = new Cartesian(carts[i]);
    return copy;
  }
}


