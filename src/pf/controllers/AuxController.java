/******************************* PACKAGE **************************************/
package pf.controllers;
/**************************** JAVA IMPORTS ************************************/
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
/*************************** JAVAFX IMPORTS ***********************************/
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.*;
/***************************** PF IMPORTS *************************************/
import pf.coreutils.*;
import pf.optimizations.*;
import pf.representations.*;

/* Auxiliary Controller to load component FXML files in PolyFold */
public class AuxController {
  // Give access to primary controller
  private Controller controller;

  /* Constructor */
  public AuxController(Controller c) {
    this.controller = c;
  }

  /* Clear and update CSS style of Node */
  public void applyStyleClass(Node n, String style) {
    if (n == null || style == null) return;
    n.getStyleClass().clear();
    n.getStyleClass().add(style);
  }

  /* Return Parent node of FXML file */
  public Parent loadFXML(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource(fxmlPath));
      loader.setController(this);
      return loader.load();
    } catch (IOException exc) { exc.printStackTrace(); }
    return null;
  }

  // Fields for About window life cycle
  @FXML Button cancelBtn, licenseBtn, logoBtn, websiteBtn;
  Stage aboutStage;
  /* Create and/or show About window for license and copyright info */
  public void showAbout() {
    // Do not allow multiple instances
    if (aboutStage != null) {
      aboutStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/about.fxml");
    aboutStage = new Stage();
    Scene scene = new Scene(root, 512, 256);
    scene.getStylesheets().add("style/style.css");
    aboutStage.setTitle("About");
    aboutStage.setScene(scene);
    aboutStage.setResizable(false);
    // Handle buttons
    licenseBtn.setOnMouseReleased(e -> handleSaveLicense(scene));
    logoBtn.setOnMouseReleased(e -> handleWebsite());
    websiteBtn.setOnMouseReleased(e -> handleWebsite());
    cancelBtn.setOnMouseReleased(e -> aboutStage = closeStage(aboutStage));
    aboutStage.show();
    aboutStage.setOnCloseRequest(e -> aboutStage = null);
  }

  /* Save GNU public license to file */
  public void handleSaveLicense(Scene scene) {
    try {
      FileChooser fileModal = new FileChooser();
      fileModal.setTitle("Save As...");
      fileModal.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Text File \".txt\"", "*.txt"));
      File f = fileModal.showSaveDialog(scene.getWindow());
      if (f == null) return;
      // Write license to file
      InputStream istream = getClass().getResourceAsStream("/docs/LICENSE");
      byte[] buff = new byte[istream.available()];
      istream.read(buff);
      istream.close();
      OutputStream ostream = new FileOutputStream(f);
      ostream.write(buff);
      ostream.close();
    } catch (IOException exc) { exc.printStackTrace(); }
  }

  /* Open PolyFold website in browser */
  public void handleWebsite() {
    try {
      Desktop d = Desktop.getDesktop();
      d.browse(new URI("http://Bhattacharya-Lab.github.io/PolyFold"));
    } catch (Exception exc) { exc.printStackTrace(); }
  }

  // Fields for optimization completion alert life cycle
  @FXML Button okBtn;
  Stage optCompleteStage;
  /* Show alert notifying completion of optimization */
  public void showOptimizationComplete() {
    if (optCompleteStage != null) {
      optCompleteStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/optimization_complete.fxml");
    optCompleteStage = new Stage();
    Scene scene = new Scene(root, 350, 150);
    scene.getStylesheets().add("style/style.css");
    optCompleteStage.setScene(scene);
    optCompleteStage.setResizable(false);
    // Handle OK button
    okBtn.setOnMouseReleased(e -> {
      optCompleteStage = closeStage(optCompleteStage);
    });
    // Handle enter key press
    scene.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        optCompleteStage = closeStage(optCompleteStage);
      }
    });
    optCompleteStage.show();
    optCompleteStage.setOnCloseRequest(e -> optCompleteStage = null);
  }

  // Fields for gradient descent configure life cycle
  @FXML Button applyBtn;
  @FXML Slider iterSlider, stepSlider;
  Stage gdConfigStage;
  /* Configure parameters of a gradient descent optimization */
  public void showConfigureGradientDescent() {
    // Do not allow multiple instances
    if (gdConfigStage != null) {
      gdConfigStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/configure_gd.fxml");
    // Setup sliders
    final int MULT = 1000;
    iterSlider.setMin(GradientDescent.MIN_ITERS / MULT);
    iterSlider.setMax(GradientDescent.MAX_ITERS / MULT);
    iterSlider.setValue(GradientDescent.iterations / MULT);
    stepSlider.setMin(GradientDescent.MIN_STEP * MULT);
    stepSlider.setMax(GradientDescent.MAX_STEP * MULT);
    stepSlider.setValue(GradientDescent.stepSize * MULT);
    // Build scene
    gdConfigStage = new Stage();
    gdConfigStage.setTitle("Gradient Descent");
    Scene scene = new Scene(root, 450, 310);
    scene.getStylesheets().add("style/style.css");
    gdConfigStage.setScene(scene);
    gdConfigStage.setResizable(false);
    // Handle buttons
    applyBtn.setOnMouseReleased(e -> {
      GradientDescent.setIterations(iterSlider.getValue() * MULT);
      GradientDescent.setStepSize(stepSlider.getValue() / MULT);
      gdConfigStage = closeStage(gdConfigStage);
    });
    cancelBtn.setOnMouseReleased(e -> {
      gdConfigStage = closeStage(gdConfigStage);
    });
    // Handle enter key press
    scene.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        GradientDescent.setIterations(iterSlider.getValue() * MULT);
        GradientDescent.setStepSize(stepSlider.getValue() / MULT);
        gdConfigStage = closeStage(gdConfigStage);
      }
    });
    gdConfigStage.show();
    gdConfigStage.setOnCloseRequest(e -> gdConfigStage = null);
  }

  // Fields for monte carlo configure life cycle
  @FXML Slider decaySlider, startTempSlider, termTempSlider;
  @FXML TextField seed;
  Stage mcConfigStage;
  /* Configure parameters of a monte carlo optimization */
  public void showConfigureMonteCarlo() {
    // Do not allow multiple instances
    if (mcConfigStage != null) {
      mcConfigStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/configure_mc.fxml");
    // Setup sliders
    decaySlider.setMin(0.0);
    decaySlider.setMax(1.0);
    decaySlider.setValue(MonteCarlo.decay);
    startTempSlider.setMin(0.5);
    startTempSlider.setMax(1.0);
    startTempSlider.setValue(MonteCarlo.startTemp);
    termTempSlider.setMin(0.0);
    termTempSlider.setMax(0.2);
    termTempSlider.setValue(MonteCarlo.terminalTemp);
    // Build scene
    mcConfigStage = new Stage();
    mcConfigStage.setTitle("Monte Carlo");
    Scene scene = new Scene(root, 450, 454);
    scene.getStylesheets().add("style/style.css");
    mcConfigStage.setScene(scene);
    mcConfigStage.setResizable(false);
    // Handle buttons
    applyBtn.setOnMouseReleased(e -> {
      MonteCarlo.setDecay(decaySlider.getValue());
      MonteCarlo.setStartTemp(startTempSlider.getValue());
      MonteCarlo.setTerminalTemp(termTempSlider.getValue());
      String seedText = seed.getText();
      if (seedText.length() != 0) {
        try {
          long seedValue = Long.parseLong(seedText);
          MonteCarlo.setSeed(seedValue);
          controller.showOverlay("Seed Set: " + Long.toString(seedValue));
        } catch (Exception exc) {
          controller.showOverlay("Invalid Seed: \"" + seedText + "\"");
        }
      }
      mcConfigStage = closeStage(mcConfigStage);
    });
    cancelBtn.setOnMouseReleased(e -> {
      mcConfigStage = closeStage(mcConfigStage);
    });
    // Handle enter key press
    scene.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        MonteCarlo.setDecay(decaySlider.getValue());
        MonteCarlo.setStartTemp(startTempSlider.getValue());
        MonteCarlo.setTerminalTemp(termTempSlider.getValue());
        String seedText = seed.getText();
        if (seedText.length() != 0) {
          try {
            long seedValue = Long.parseLong(seedText);
            MonteCarlo.setSeed(seedValue);
            controller.showOverlay("Seed Set: " + Long.toString(seedValue));
          } catch (Exception exc) {
            controller.showOverlay("Invalid Seed: \"" + seedText + "\"");
          }
        }
        mcConfigStage = closeStage(mcConfigStage);
      }
    });
    mcConfigStage.show();
    mcConfigStage.setOnCloseRequest(e -> mcConfigStage = null);
  }

  Stage recoverLowestStage;
  public void showRecoverLowest() {
    if (recoverLowestStage != null) {
      recoverLowestStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/recover_lowest.fxml");
    recoverLowestStage = new Stage();
    recoverLowestStage.setTitle("Monte Carlo");
    Scene scene = new Scene(root, 450, 152);
    scene.getStylesheets().add("style/style.css");
    recoverLowestStage.setScene(scene);
    recoverLowestStage.setResizable(false);
    // Handle buttons
    applyBtn.setOnMouseReleased(e -> {
      controller.updateStructure(
          MonteCarlo.recoverLowest(), /*updateZoom=*/true);
      recoverLowestStage = closeStage(recoverLowestStage);
    });
    // Handle cancel
    cancelBtn.setOnMouseReleased(e -> {
      recoverLowestStage = closeStage(recoverLowestStage);
    });
    // Handle keys
    scene.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        controller.updateStructure(
            MonteCarlo.recoverLowest(), /*updateZoom=*/true);
        recoverLowestStage = closeStage(recoverLowestStage);
      }
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        recoverLowestStage = closeStage(recoverLowestStage);
      }
    });
    recoverLowestStage.show();
    recoverLowestStage.setOnCloseRequest(e -> recoverLowestStage = null);
  }

  // Fields for save state life cycle
  @FXML TextField stateName;
  Stage saveStateStage;
  /* Method which creates named state in history */
  public void handleSaveState(Angular[] angles) {
    if (saveStateStage != null) {
      saveStateStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/save_state.fxml");
    Scene scene = new Scene(root, 300, 192);
    scene.getStylesheets().add("style/style.css");
    saveStateStage = new Stage();
    saveStateStage.setScene(scene);
    saveStateStage.setResizable(false);
    // Handle save button
    applyBtn.setOnMouseReleased(e -> {
      History.addNamedSaveState(stateName.getText(), angles);
      controller.showOverlay("Saved State '" + stateName.getText() + "'");
      saveStateStage = closeStage(saveStateStage);
    });
    // Handle cancel
    cancelBtn.setOnMouseReleased(e -> {
      saveStateStage = closeStage(saveStateStage);
    });
    // Handel enter and escape keys
    scene.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        History.addNamedSaveState(stateName.getText(), angles);
        controller.showOverlay("Saved State '" + stateName.getText() + "'");
        saveStateStage = closeStage(saveStateStage);
      }
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        saveStateStage = closeStage(saveStateStage);
      }
    });
    saveStateStage.show();
    saveStateStage.setOnCloseRequest(e -> saveStateStage = null);
  }

  // Fields for load state life cycle
  @FXML ListView<String> stateList;
  Stage loadStateStage;
  /* Method which loads named state from history */
  public void handleLoadState() {
    if (History.namedStates.keySet().isEmpty()) {
      controller.showOverlay("No Named Save States");
      return;
    }
    if (loadStateStage != null) {
      loadStateStage.requestFocus();
      return;
    }
    Parent root = loadFXML("/fxml/load_state.fxml");
    Scene scene = new Scene(root, 400, 300);
    scene.getStylesheets().add("style/style.css");
    loadStateStage = new Stage();
    loadStateStage.setScene(scene);
    loadStateStage.setResizable(false);
    // Set list items to names of states
    stateList.getItems().setAll(History.namedStates.keySet());
    // Handle load button
    applyBtn.setOnMouseReleased(e -> {
      String name = stateList.getSelectionModel().getSelectedItem();
      State s = History.loadNamedSaveState(name);
      if (s != null) controller.updateStructure(s.state, /*updateZoom=*/true);
      controller.showOverlay("Loaded State '" + name + "'");
      loadStateStage = closeStage(loadStateStage);
    });
    // Handle cancel button
    cancelBtn.setOnMouseReleased(e -> {
      loadStateStage = closeStage(loadStateStage);
    });
    loadStateStage.show();
    loadStateStage.setOnCloseRequest(e -> loadStateStage = null);
  }

  /* Utility for closing a window and setting it to null */
  public Stage closeStage(Stage s) {
    if (s == null) return s;
    s.hide();
    return null;
  }
}
