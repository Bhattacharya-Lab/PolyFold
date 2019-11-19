package pf.representations;

public class Angular {
  public int id;
  public double theta, tao;
  public char aa, ss;

  public Angular() {
    this.id = 0;
    this.tao = 0;
    this.theta = 0;
    this.aa = 0;
    this.ss = 0;
  }

  public Angular(Angular other) {
    this.id = other.id;
    this.tao = other.tao;
    this.theta = other.theta;
    this.aa = other.aa;
    this.ss = other.ss;
  }
}
