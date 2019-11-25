module PolyFold {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;

  opens pf.controllers to javafx.fxml;
  exports pf;
}