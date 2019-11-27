package pf.representations;

import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.geometry.Point3D;

import static pf.geometry.LinearAlgebra.*;
import pf.ui.Colors;

public class Residue extends Group {
  public final double NODE_RADIUS = 0.55;
  public final double ROD_RADIUS = 0.15;
  public final double ROD_HEIGHT = Converter.BOND_LEN;

  public Sphere node;
  public Cylinder rod = new Cylinder(0, 0);
  public char aa; // amino acid
  public char ss; // secondary structure
  public int id;
  public Rotate rotation;

  public Residue(Cartesian cart, boolean end) {
    super();
    this.node = new Sphere(NODE_RADIUS);
    this.aa = cart.aa;
    this.ss = cart.ss;
    this.node.setMaterial(Colors.RED);
    if (this.ss == 'E') this.node.setMaterial(Colors.YELLOW);
    if (this.ss == 'C') this.node.setMaterial(Colors.BLUE);
    if (!end) initRod(ROD_RADIUS, ROD_HEIGHT);
    initRotation(Rotate.Z_AXIS);
    getChildren().addAll(node, rod);
    getTransforms().add(rotation);
    setCenter(cart.ca.x, cart.ca.y, cart.ca.z);
  }

  public void initRod(double radius, double height) {
    rod = new Cylinder(radius, height);
    rod.setTranslateY(0.5 * Converter.BOND_LEN);
  }

  public void initRotation(Point3D axis) {
    rotation = new Rotate();
    rotation.pivotXProperty().bind(node.translateXProperty());
    rotation.pivotYProperty().bind(node.translateYProperty());
    rotation.pivotZProperty().bind(node.translateZProperty());
    rotation.setAxis(axis);
  }

  public void setCenter(double xF, double yF, double zF) {
    double x0 = node.getTranslateX();
    double y0 = node.getTranslateY();
    double z0 = node.getTranslateZ();
    setTranslateX(xF - x0);
    setTranslateY(yF - y0);
    setTranslateZ(zF - z0);
  }

  public void setCenter(Point p) {
    double x0 = node.getTranslateX();
    double y0 = node.getTranslateY();
    double z0 = node.getTranslateZ();
    setTranslateX(p.x - x0);
    setTranslateX(p.y - y0);
    setTranslateX(p.z - z0);
  }

  public Point getCenter() {
    return new Point(getTranslateX(), getTranslateY(), getTranslateZ());
  }

  public void rotateRod(Residue other) {
    Point p0 = new Point(0, 1, 0);
    Point p1 = pointSubtract(getCenter(), other.getCenter());
    // cross product is the rotation axis
    Point axis = crossProduct(p0, p1);
    double r = Math.hypot(p1.x, p1.z);
    double phi = Math.toDegrees(Math.atan2(r, p1.y));
    rotation.setAxis(new Point3D(axis.x, axis.y, axis.z));
    rotation.setAngle(180 + phi);
  }
}
