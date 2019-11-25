package pf.geometry;

import pf.representations.*;
import static java.lang.Math.*;

public class LinearAlgebra {
  public static Point scale(Point p, double scalar) {
    return new Point(p.x * scalar, p.y * scalar, p.z * scalar);
  }

  public static Point pointSubtract(Point a, Point b) {
    return new Point(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Point pointAdd(Point a, Point b) {
    return new Point(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static double[] vectorAdd(double[] a, double[] b) {
    if (a.length != b.length) return null;
    double[] result = new double[a.length];
    for (int i = 0; i < a.length; i++) result[i] = a[i] + b[i];
    return result;
  }

  public static double[] vectorSubtract(double[] a, double[] b) {
    if (a.length != b.length) return null;
    double[] result = new double[a.length];
    for (int i = 0; i < a.length; i++) result[i] = a[i] - b[i];
    return result;
  }

  public static Point unitVector(Point p) {
    double len = magnitude(p);
    return new Point(p.x / len, p.y / len, p.z / len);
  }

  public static double magnitude(Point p) {
    return sqrt(pow(p.x, 2) + pow(p.y, 2) + pow(p.z, 2));
  }

  public static double pointDistance(Point a, Point b) {
    return magnitude(pointSubtract(a, b));
  }

  public static double residueDistance(Residue a, Residue b) {
    return pointDistance(a.getCenter(), b.getCenter());
  }

  public static double dotProduct(Point a, Point b) {
    return a.x * b.x + a.y * b.y + a.z * b.z;
  }

  public static Point crossProduct(Point a, Point b) {
    Point p = new Point();
    p.x = a.y * b.z - a.z * b.y;
    p.y = a.z * b.x - a.x * b.z;
    p.z = a.x * b.y - a.y * b.x;
    return p;
  }

  public static double planarAngle(Point a, Point b, Point c) {
    Point u = pointSubtract(b, a);
    Point v = pointSubtract(b, c);
    double theta = dotProduct(u, v) / (magnitude(u) * magnitude(v));
    return acos(theta);
  }

  public static double dihedralAngle(Point a, Point b, Point c, Point d) {
    Point origin = new Point();
    Point u = unitVector(pointSubtract(b, a));
    Point v = unitVector(pointSubtract(c, b));
    Point t = unitVector(pointSubtract(d, c));
    Point n1 = crossProduct(u, v);
    Point n2 = crossProduct(v, t);
    Point m = crossProduct(n1, v);
    double x = dotProduct(n1, n2);
    double y = dotProduct(m, n2);
    return -1 * atan2(y, x);
  }

  public static double determinant(Point[] M) {
    // Matrix M must exist, be non-empty, and be square (3x3 in our case)
    if (M == null || M.length != 3) {
      throw new IllegalArgumentException("Invalid Matrix");
    }
    double det = M[0].x * (M[1].y * M[2].z - M[1].z * M[2].y);
    det += -1 * M[0].y * (M[1].x * M[2].z - M[1].z * M[2].x);
    return det + M[0].z * (M[1].x * M[2].y - M[1].y * M[2].x);
  }

  public static double[][] adjacencyMatrix(Residue[] residues) {
    Cartesian[] carts = Converter.residuesToCarts(residues);
    double[][] adj = new double[carts.length][carts.length];
    for (int i = 0; i < carts.length; i++) {
      for (int j = i+1; j < carts.length; j++) {
        double dist = pointDistance(carts[i].ca, carts[j].ca);
        adj[i][j] = dist;
        adj[j][i] = dist;
      }
    }
    return adj;
  }

  public static Point getCentroid(Point[] points) {
    double x = 0.0;
    double y = 0.0;
    double z = 0.0;
    int len = points.length;
    for (int i = 0 ; i < len; i++) {
      x += points[i].x;
      y += points[i].y;
      z += points[i].z;
    }
    return new Point(x / len, y / len, z / len);
  }
}
