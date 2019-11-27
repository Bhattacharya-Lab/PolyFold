package pf.coreutils;

import static java.lang.Math.*;

public class Limits {
  public static final int INF = 1_000_000_000;
  public static final int NEG_INF = -1_000_000_000;
  public static final double EPS = 0.000_001;

  public static double idealPlanar(char ss) {
    if (ss == 'H') return toRadians(89);
    if (ss == 'E') return toRadians(124);
    return toRadians(110);
  }

  public static double idealDihedral(char ss) {
    if (ss == 'H') return toRadians(50);
    if (ss == 'E') return toRadians(-170);
    return toRadians(-150);
  }

  public static double minPlanar(char ss) {
    if (ss == 'H') return toRadians(77);
    if (ss == 'E') return toRadians(110);
    // Abitrary choice for coils
    return toRadians(30);
  }

  public static double minDihedral(char ss) {
    if (ss == 'H') return toRadians(30);
    if (ss == 'E') return toRadians(145);
    return -PI + EPS;
  }

  public static double planarRange(char ss) {
    if (ss == 'H') return toRadians(24);
    if (ss == 'E') return toRadians(28);
    // Abitrary choice for coils
    return toRadians(120);
  }

  public static double dihedralRange(char ss) {
    if (ss == 'H') return toRadians(40);
    if (ss == 'E') return toRadians(90);
    return 2 * PI;
  }

  public static double angleEditRange(char ss) {
    if (ss == 'E') return toRadians(8);
    if (ss == 'H') return toRadians(12);
    return toRadians(20);
  }

  public static double clipPlanar(double planarAngle, char ss) {
    double minAngle = minPlanar(ss);
    double maxAngle = minAngle + planarRange(ss);
    return max(minAngle, min(planarAngle, maxAngle));
  }

  public static double clipDihedral(double dihedralAngle, char ss) {
    double minAngle = minDihedral(ss);
    double maxAngle = minAngle + dihedralRange(ss);
    dihedralAngle = max(minAngle, min(dihedralAngle, maxAngle));
    // COmpensate for wrap around
    if (dihedralAngle > PI) dihedralAngle -= 2 * PI;
    return dihedralAngle;
  }
}
