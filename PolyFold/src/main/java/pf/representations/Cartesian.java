package pf.representations;

public class Cartesian {
  public Point ca;
  public char aa, ss;

  public Cartesian() {
    this.ca = new Point();
    this.aa = 0;
    this.ss = 0;
  }

  public Cartesian(Cartesian c) {
    this.ca = c.ca;
    this.aa = c.aa;
    this.ss = c.ss;
  }
}
