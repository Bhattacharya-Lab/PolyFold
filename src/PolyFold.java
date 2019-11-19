/**************************** JAVA IMPORTS ************************************/
import java.io.IOException;
/*************************** JAVAFX IMPORTS ***********************************/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
/***************************** PF IMPORTS *************************************/
import pf.controllers.Controller;

public class PolyFold extends Application {
  /* Main function for FX apps */
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/fxml/main.fxml"));
    Controller controller = new Controller(stage);
    loader.setController(controller);
    // Build main window
    Parent root = loader.load();
    Scene scene = new Scene(root, 1024, 768, true);
    scene.getStylesheets().add("style/style.css");
    stage.setTitle("PolyFold (Beta Version)");
    stage.setScene(scene);
    stage.show();
    // Min dimensions must be set after stage is shown
    stage.setMinWidth(stage.getWidth());
    stage.setMinHeight(stage.getHeight());
    // Show splash screen
    controller.showSplash();
  }

  /* Ignored but necessary in FX apps */
  public static void main(String[] args) { launch(args); }
}
