/* ***************************** PACKAGE ************************************ */
package pf.camera;
/* ************************* JAVAFX IMPORTS ********************************* */
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;

/* A custom camera class for PolyFold */
public class pfCamera extends PerspectiveCamera {
  public final double INITIAL_Z = 100;
  public final double NEAR_CLIP = 0.1;
  public final double FAR_CLIP = 5000;
  public final double MIN_ZOOM = 30;
  public final double MAX_ZOOM = 5000;
  public double trackSpeed = INITIAL_Z / 1000.0;
  public boolean isAutoZoomed = false;

  /* Constructor */
  public pfCamera() {
    super(/*fixedEyeAtCameraZero=*/true);
    setNearClip(NEAR_CLIP);
    setFarClip(FAR_CLIP);
    setTranslateZ(INITIAL_Z);
    // compensate for counter intuitive x and y direction
    setRotationAxis(Rotate.X_AXIS);
    setRotate(180.0);
  }

  /* Set camera to minimum zoom to enforce bounds. */
  public void setMinZoom() { setTranslateZ(MIN_ZOOM); }

  /* Set camera to maximum zoom to enforce bounds. */
  public void setMaxZoom() { setTranslateZ(MAX_ZOOM); }

  /* Update dynamic track speed based on zoom level */
  public void updateTrackSpeed() {
    trackSpeed = Math.abs(getTranslateZ() / 1000.0);
  }

  /* Update zoom automatically to bound 3D object */
  // TODO auto zoom needs work
  public void updateZoom(Group world) {
    // cam field of view is 15 degrees from either side of center so pi / 12
    double tanTheta = Math.tan(Math.PI / 12.0);
    Bounds b = world.getBoundsInParent();
    double xMid = (b.getMinX() + b.getMaxX()) / 2.0;
    double yMid = (b.getMinY() + b.getMaxY()) / 2.0;
    double halfHeight = (b.getMaxY() - b.getMinY()) / 2.0;
    double halfWidth = (b.getMaxX() - b.getMinX()) / 2.0;
    double margin = 1.5;
    double opposite = Math.max(halfWidth, halfHeight) * margin;
    double zoom = opposite / tanTheta;
    setTranslateX(xMid);
    setTranslateY(yMid);
    setTranslateZ(zoom);
    updateTrackSpeed();
  }

  /* Flip state of auto zoom feature */
  public void toggleAutoZoom(Group world) {
    isAutoZoomed = !isAutoZoomed;
    if (isAutoZoomed) updateZoom(world);
  }
}
