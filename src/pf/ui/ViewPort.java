package pf.ui;

import javafx.scene.layout.Pane;
import javafx.scene.SubScene;

public class ViewPort extends Pane {
  public ViewPort(SubScene subscene) {
    super(/*children=*/subscene);
    subscene.widthProperty().bind(widthProperty());
    subscene.heightProperty().bind(heightProperty());
  }
}
