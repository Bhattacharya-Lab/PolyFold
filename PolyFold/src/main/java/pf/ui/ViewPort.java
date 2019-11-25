/* ***************************** PACKAGE ************************************ */
package pf.ui;
/* ************************* JAVAFX IMPORTS ********************************* */
import javafx.scene.layout.Pane;
import javafx.scene.SubScene;

/* Simple class to allow 3D UI in PolyFold */
public class ViewPort extends Pane {
  public ViewPort(SubScene subscene) {
    super(/*children=*/subscene);
    subscene.widthProperty().bind(widthProperty());
    subscene.heightProperty().bind(heightProperty());
  }
}
