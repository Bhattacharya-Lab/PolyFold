/* ***************************** PACKAGE ************************************ */
package pf.controllers;
/* ************************** JAVA IMPORTS ********************************** */
import java.io.*;
import static java.lang.Math.*;
import java.text.DecimalFormat;
import java.util.LinkedList;
/* ************************* JAVAFX IMPORTS ********************************* */
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.util.Duration;
/* *************************** PF IMPORTS *********************************** */
import pf.camera.pfCamera;
import pf.coreutils.*;
import pf.geometry.Chirality;
import pf.geometry.LinearAlgebra;
import pf.optimizations.GradientDescent;
import pf.optimizations.MonteCarlo;
import pf.representations.Angular;
import pf.representations.Cartesian;
import pf.representations.Converter;
import pf.representations.Residue;
import pf.ui.Colors;
import pf.ui.ViewPort;

/* Primary Controller class for making PolyFold interactive */
public class Controller {
  // Fields relevant only to controller
  public AuxController aux = new AuxController(this);
  public Stage stage;
  /* Constructor */
  public Controller(Stage s) { this.stage = s; }

  /* Minimize the window */
  public void minimize() { stage.setIconified(true); }

  /* Fullscreen the window */
  public void fullscreen() { stage.setFullScreen(true); }

  /* Show about window */
  public void showAbout() { aux.showAbout(); }

  /* Show gradient descent config window */
  public void showConfigureGradientDescent() {
    aux.showConfigureGradientDescent();
  }

  /* Show monte carlo config window */
  public void showConfigureMonteCarlo() {
    aux.showConfigureMonteCarlo();
  }

  /* Show optimization completion alert */
  public void showOptimizationComplete() { aux.showOptimizationComplete(); }

  /* Show dialog for saving named state */
  public void showSaveState() {
    if (angles == null) return;
    aux.handleSaveState(angles);
  }

  /* Show dialog for loading named state */
  public void showLoadState() {
    if (angles == null) return;
    aux.handleLoadState();
  }

  /* Show recover lowest energy state in Monte Carlo */
  public void showRecoverLowest() {
    aux.showRecoverLowest();
  }

  LinkedList<Label> overlayQ = new LinkedList<>();
  @FXML StackPane stackPane;
  public void showOverlay(String text) {
    stackPane.getChildren().remove(overlayQ.pollFirst());
    Label overlay = new Label(text);
    overlayQ.addLast(overlay);
    stackPane.getChildren().add(overlay);
    FadeTransition ft = new FadeTransition(Duration.seconds(1.4), overlay);
    ft.setFromValue(1);
    ft.setToValue(0);
    ft.setInterpolator(Interpolator.EASE_IN);
    ft.play();
    ft.setOnFinished(e -> stackPane.getChildren().remove(overlay));
  }

  @FXML VBox splashLayout;
  public void showSplash() { splashLayout.setVisible(true); }
  public void hideSplash() { splashLayout.setVisible(false); }

  // 3D view related fields
  public static pfCamera cam = new pfCamera();
  public static ViewPort viewport;
  public static SubScene subscene;
  public static Group world = new Group();
  public static Group sequence = new Group();
  public final int GD_UPDATE_RATE = 256;
  public final int MC_UPDATE_RATE = 8;
  // Residue representation related fields;
  public static Residue[] residues;
  public static Angular[] angles;
  public static Cartesian[] carts;
  // Container for 3D and 2D UI
  @FXML BorderPane app;
  /* Setup for app */
  public void initialize() {
    initViewport();
    handleMouse();
    handleSliders();
    handleKeyPressed();
  }

  /* Build and show 3D viewport within PolyFold */
  public void initViewport() {
    subscene = new SubScene(world, 1024, 768, true, null);
    subscene.setCamera(cam);
    subscene.setFill(Colors.VIEWPORT);
    viewport = new ViewPort(subscene);
    app.setCenter(viewport);
    hideProteinMovementUI();
  }

  /* Toggle camera auto zoom feature */
  public void toggleAutoZoom() {
    cam.toggleAutoZoom(world);
    autoZoomBtn.setSelected(cam.isAutoZoomed);
  }

  /* Initialize and build the protein sequence */
  public void initSequence() {
    initAngularArray();
    residues = updateResidues(/*selectionIndex=*/-1);
    resetResidueLabels();
    buildSequence();
    cam.updateZoom(world);
  }

  /* Initialize angles of sequence based on amino sequence and secondary
   * structure
   */
  public static void initAngularArray() {
    boolean hasSecondaryStructure = (
        RRUtils.secondarySequence != null &&
        RRUtils.secondarySequence.length() == RRUtils.aminoSequence.length());
    // Initialize angles based on info given
    int n = RRUtils.sequenceLen;
    angles = new Angular[n];
    for (int i = 0; i < n; i++) {
      Angular a = new Angular();
      a.id = i;
      a.aa = RRUtils.aminoSequence.charAt(i);
      // 0 and n-1 nodes have no planar angle
      if (i == 0 || i == n-1) a.theta = 2 * PI;
      // 0, n-1, and n-2 nodes have no dihedral angle
      if (i == 0 || i == n-1 || i == n-2) a.tao = 2 * PI;
      if (hasSecondaryStructure) {
        char ss = RRUtils.secondarySequence.charAt(i);
        a.ss = ss;
        if (i != 0 && i != n-1) {
          a.theta = Limits.idealPlanar(ss);
          if (i != n-2) a.tao = Limits.idealDihedral(ss);
        }
      } else {
        if (i != 0 && i != n-1) {
          a.theta = toRadians(110);
          if (i != n-2) a.tao = toRadians(-150.0);
        }
      }
      angles[i] = a;
    }
  }

  public void repairSecondary() {
    boolean hasSecondaryStructure = (
        RRUtils.secondarySequence != null &&
        RRUtils.secondarySequence.length() == RRUtils.aminoSequence.length());
    if (!hasSecondaryStructure) return;
    int n = RRUtils.sequenceLen;
    // Considering tetra-peptides
    for (int i = 1; i < n-1; i++) {
      char ss = angles[i].ss;
      if (ss == 'H' || ss == 'E') {
        angles[i].theta = Limits.clipPlanar(angles[i].theta, ss);
        if (i != n-2) angles[i].tao = Limits.clipDihedral(angles[i].tao, ss);
      }
    }
    updateStructure(angles, /*updateZoom=*/true);
  }

  /* Rebuild residue array based on new cartesian locations */
  public static void setResidueArray() {
    carts = Converter.anglesToCarts(angles);
    int n = RRUtils.sequenceLen;
    residues = new Residue[n];
    for (int i = 0; i < n; i++) {
      residues[i] = new Residue(carts[i], (i == n-1));
      residues[i].id = i;
      if (i > 0) residues[i-1].rotateRod(residues[i]);
    }
  }

  /* Update residue locations and selected residue */
  public Residue[] updateResidues(int selectionIndex) {
    if (angles == null) return null;
    setResidueArray();
    // No selection is indicated by -1
    if (selectionIndex != -1) {
      selectedResidue = residues[selectionIndex];
      selectedResidue.node.setMaterial(Colors.GREEN);
    }
    return residues;
  }

  /* Rebuild the protein sequence with newly updated residues */
  public static void buildSequence() {
    if (angles == null) return;
    if (!world.getChildren().contains(sequence)) {
      world.getChildren().add(sequence);
    }
    sequence.getChildren().clear();
    sequence.setRotationAxis(Rotate.Y_AXIS);
    sequence.getChildren().addAll(residues);
    if (cam.isAutoZoomed) cam.updateZoom(world);
  }

  @FXML public Slider thetaSlider;
  @FXML public Slider taoSlider;
  /* Set boundaries of sliders */
  public void initSliders() {
    // Epsilon defined to allow for small error in floating point precision
    thetaSlider.setMin(0.0 + Limits.EPS);
    thetaSlider.setMax(PI - Limits.EPS);
    taoSlider.setMin(-PI + Limits.EPS);
    taoSlider.setMax(PI - Limits.EPS);
  }

  /* Disable sliders and set selector to mid point */
  public void disableSliders() {
    thetaSlider.setValue(PI / 2.0);
    thetaSlider.setDisable(true);
    taoSlider.setValue(0.0);
    taoSlider.setDisable(true);
    thetaDegreeText.setText("");
    taoDegreeText.setText("");
  }

  /* Update sliders to reflect angles of given residue */
  public void updateSliders(Residue r) {
    int id = r.id;
    disableSliders();
    if (id != 0 && id != angles.length-1) {
      thetaSlider.setDisable(false);
      thetaSlider.setValue(angles[id].theta);
      if (id != angles.length-2) {
        taoSlider.setDisable(false);
        taoSlider.setValue(angles[id].tao);
      }
    }
  }

  // Track selected residue and color of its node
  static Residue selectedResidue;
  static PhongMaterial selectedMaterial;
  /* Select a residue and update the UI with its info */
  public void select(Residue r) {
    if (r == null) return;
    deselect(selectedResidue);
    selectedResidue = r;
    selectedMaterial = (PhongMaterial) r.node.getMaterial();
    r.node.setMaterial(Colors.GREEN);
    updateSliders(r);
    updateResidueLabels(r);
  }

  /* Deselect a residue and update the UI */
  public void deselect(Residue r) {
    if (r == null) return;
    r.node.setMaterial(selectedMaterial);
    selectedResidue = null;
    selectedMaterial = null;
    disableSliders();
    resetResidueLabels();
  }

  // index, amino acid, and secondary structure
  @FXML public Text idLabel;
  @FXML public Text aaLabel;
  @FXML public Text ssLabel;
  /* Set residue info in UI based on selected residue */
  public void updateResidueLabels(Residue r) {
    idLabel.setText("Residue ID: " + (r.id + 1));
    aaLabel.setText("Amino Acid: " + Maps.aaShort.get(r.aa));
    ssLabel.setText("Secondary Structure: " + Maps.ssShort.get(r.ss));
  }

  /* Clear residue info from UI */
  public void resetResidueLabels() {
    idLabel.setText("Residue ID:");
    aaLabel.setText("Amino Acid:");
    ssLabel.setText("Secondary Structure:");
  }

  // Mouse start and end points on mouse events
  double startX, startY, endX, endY;
  // Logo goes to website
  @FXML Button rightLogo;
  /* Handle all mouse input */
  public void handleMouse() {
    rightLogo.setOnMousePressed(e -> aux.handleWebsite());
    // Select node on click
    subscene.setOnMousePressed(e -> {
      endX = e.getSceneX();
      endY = e.getSceneY();
      if (e.isPrimaryButtonDown()) {
        Node result = e.getPickResult().getIntersectedNode();
        if (result instanceof Sphere) {
          Residue r = (Residue) result.getParent();
          select(r);
        } else deselect(selectedResidue);
      }
    });
    subscene.setOnMouseDragged(e -> {
      startX = endX;
      startY = endY;
      endX = e.getSceneX();
      endY = e.getSceneY();
      // Left click moves camera
      if (e.isPrimaryButtonDown()) {
        if (cam.isAutoZoomed) return;
        cam.setTranslateX(cam.getTranslateX() - (endX-startX) * cam.trackSpeed);
        cam.setTranslateY(cam.getTranslateY() + (endY-startY) * cam.trackSpeed);
      }
      // Right click rotates sequence
      if (e.isSecondaryButtonDown()) {
        sequence.setRotate(sequence.getRotate() + (endX - startX));
        if (cam.isAutoZoomed) cam.updateZoom(world);
      }
    });
    // Scroll zooms
    subscene.setOnScroll(e -> {
      if (cam.isAutoZoomed) return;
      double zoom = cam.getTranslateZ() - e.getDeltaY();
      if (!e.isInertia()) {
        if (zoom <= cam.MIN_ZOOM) cam.setMinZoom();
        else if (zoom >= cam.MAX_ZOOM) cam.setMaxZoom();
        else cam.setTranslateZ(cam.getTranslateZ() - e.getDeltaY());
        cam.updateTrackSpeed();
      }
    });
  }

  // Fields for text corresponding to slider values
  @FXML public Text thetaDegreeText;
  @FXML public Text taoDegreeText;
  /* Handle all changes in UI sliders */
  public void handleSliders() {
    DecimalFormat df = new DecimalFormat("0.0");
    // Update residue planar angle by slider value
    thetaSlider.valueProperty().addListener((obs, start, end) -> {
      if (selectedResidue != null) {
        updateScore();
        int id = selectedResidue.id;
        if (id != 0 && id != angles.length-1) {
          thetaDegreeText.setText(df.format(toDegrees(thetaSlider.getValue())) + "\u00b0");
          angles[id].theta = end.doubleValue();
          History.setCurrentState(angles);
          residues = updateResidues(id);
          buildSequence();
        }
      }
    });
    // Update residue dihedral angle by slider value
    taoSlider.valueProperty().addListener((obs, start, end) -> {
      if (selectedResidue != null) {
        updateScore();
        int id = selectedResidue.id;
        if (id != 0 && id != angles.length-2 && id != angles.length-1) {
          taoDegreeText.setText(df.format(toDegrees(taoSlider.getValue())) + "\u00b0");
          angles[id].tao = end.doubleValue();
          History.setCurrentState(angles);
          residues = updateResidues(id);
          buildSequence();
        }
      }
    });
    // Add undo states as soon as mouse goes down
    thetaSlider.setOnMousePressed(e -> History.addUndoState(angles));
    taoSlider.setOnMousePressed(e -> History.addUndoState(angles));
  }

  /* Open a residue to residue file and set amino sequence and secondary
   * structure
   */
  public void openFile() throws IOException {
    FileChooser fileModal = new FileChooser();
    fileModal.setTitle("Open Resource File");
    fileModal.getExtensionFilters().addAll(new ExtensionFilter("RR File \".rr\"", "*.rr"));
    File f = fileModal.showOpenDialog(app.getScene().getWindow());
    if (f != null) {
      if (RRUtils.parseRR(f)) {
        History.clear();
        stage.setTitle("PolyFold (Beta Version) - " + FileUtils.getBaseName(f));
        showProteinMovementUI();
      }
    }
  }

  // contact map fields
  @FXML public GridPane distanceMap;
  @FXML public Text score;

  public static double[][] adjMatrix;
  public static int cellSize;
  public static double currentSequenceMaxDist;
  public static final double DISTANCE_THRESHOLD = 100;
  public static final int DISTANCE_MAP_WIDTH = 280;

  public void generateDistanceMap() {
    distanceMap.getChildren().clear();
    currentSequenceMaxDist = 0.0;
    cellSize = min(DISTANCE_MAP_WIDTH / RRUtils.sequenceLen, 3);
    adjMatrix = LinearAlgebra.adjacencyMatrix(residues);
    for (int i = 0; i < RRUtils.sequenceLen; i++) {
      for (int j = 0; j < RRUtils.sequenceLen; j++) {
        double dist;
        if (i < j) {
          dist = RRUtils.expectedDistance[i][j];
        } else if (i > j) {
          dist = LinearAlgebra.residueDistance(residues[i], residues[j]);
        } else {
          dist = 0.0;
        }
        currentSequenceMaxDist = max(currentSequenceMaxDist, dist);
        adjMatrix[i][j] = dist;
      }
    }
    currentSequenceMaxDist = max(DISTANCE_THRESHOLD, currentSequenceMaxDist);
    buildDistanceMap();
    updateScore();
  }


  Rectangle[][] rectangles;
  @FXML HBox distanceBox;
  ImageView colorBar;
  NumberAxis ticks;
  LineChart<Number, Number> chart;

  public void buildDistanceMap() {
    rectangles = new Rectangle[RRUtils.sequenceLen][RRUtils.sequenceLen];
    for (int i = 0; i < RRUtils.sequenceLen; i++) {
      for (int j = 0; j < RRUtils.sequenceLen; j++) {
        Rectangle rect = new Rectangle(cellSize, cellSize);
        rect.setStyle("-fx-stroke-width: 0;");
        if (i == j) {
          rect.setFill(Colors.BLACK);
        } else {
          double value = adjMatrix[i][j];
          if (value > currentSequenceMaxDist) {
            rect.setFill(Colors.INVALID);
          } else {
            rect.setFill(Colors.getColorFromValue(value));
          }
        }
        distanceMap.add(rect, j, i);
        rectangles[i][j] = rect;
      }
    }
    Scoring.setMaxScore();
    if (colorBar != null || ticks != null) {
      distanceBox.getChildren().remove(colorBar);
      distanceBox.getChildren().remove(ticks);
    }
    colorBar = new ImageView(createColorBar(5, cellSize * RRUtils.sequenceLen));
    distanceBox.getChildren().add(colorBar);
    ticks = new NumberAxis(0, 25 * ((int) currentSequenceMaxDist / 25 + 1), 25);
    ticks.setSide(Side.RIGHT);
    NumberAxis xAxis = new NumberAxis(0, 0, 1);
    chart = new LineChart<>(xAxis, ticks);
    distanceBox.getChildren().add(ticks);
  }

  @FXML public ProgressBar progressBar;
  public void updateScore() {
    if (residues == null) return;
    updateDistanceMap();
    updateMapColors();
    Scoring.updateScore(adjMatrix);
    progressBar.setProgress(Scoring.scorePercentage());
    score.setText(" " + Scoring.scoreNumerator() + " / " + Scoring.scoreDenominator());
  }

  public void updateDistanceMap() {
    for (int i = 0; i < RRUtils.sequenceLen; i++) {
      for (int j = 0; j < RRUtils.sequenceLen; j++) {
        if (i > j) {
          adjMatrix[i][j] = LinearAlgebra.residueDistance(residues[i], residues[j]);
        }
      }
    }
  }

  public void updateMapColors() {
    for (int i = 1; i < RRUtils.sequenceLen; i++) {
      for (int j = 0; j < i; j++) {
        // only run on bottom triangle
        double value = adjMatrix[i][j];
        if (value > currentSequenceMaxDist) {
          rectangles[i][j].setFill(Colors.INVALID);
        } else {
          rectangles[i][j].setFill(Colors.getColorFromValue(value));
        }
      }
    }
  }

  public void saveToPDB() throws IOException {
    if (residues == null) return;
    FileChooser fileModal = new FileChooser();
    fileModal.setTitle("Save As...");
    fileModal.getExtensionFilters().add(new ExtensionFilter("Protein Data Bank \".pdb\"", "*.pdb"));
    File f = fileModal.showSaveDialog(app.getScene().getWindow());
    if (f != null) writeToPDB(f);
  }

  public void writeToPDB(File f) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
    carts = Converter.anglesToCarts(angles);
    int multiplier = Chirality.hasCorrectChirality(carts) ? 1 : -1;
    for (int i = 0; i < residues.length; i++) {
      bw.write(
          String.format("ATOM %6d  CA  %-3s %5d    %8.3f%8.3f%8.3f  1.00  0.00\n",
                        i+1,
                        Maps.aaThreeCharacter.get(residues[i].aa),
                        residues[i].id+1,
                        carts[i].ca.x,
                        carts[i].ca.y * multiplier,
                        carts[i].ca.z));
    }
    bw.close();
  }

  public void startGradientDescent() {
    if (residues == null) return;
    History.addUndoState(angles);
    disableSliders();
    resetResidueLabels();
    unsetKillOptimization();
    Thread thread = new Thread(() -> {
      if (!cam.isAutoZoomed) toggleAutoZoom();
      disableNonCriticalFunctions();
      showCancelOptimization();
      gradientDescent();
      hideCancelOptimization();
      enableNonCriticalFunctions();
      toggleAutoZoom();
    });
    thread.setDaemon(true);
    thread.start();
  }

  public void gradientDescent() {
    carts = Converter.residuesToCarts(residues);
    for (int i = 0; i <= GradientDescent.iterations; i++) {
      if (killOptimization) return;
      carts = GradientDescent.getNextState(carts);
      if (i % GD_UPDATE_RATE == 0) {
        angles = Converter.cartsToAngles(carts);
        try {
          Platform.runLater(() -> updateStructure(angles));
        } catch (Exception exc) {}
      }
    }
    angles = Converter.cartsToAngles(carts);
    try {
      Platform.runLater(() -> {
        updateStructure(angles);
        showOptimizationComplete();
      });
    } catch (Exception exc) { exc.printStackTrace(); }
  }

  public void updateStructure(Angular[] update) {
    for (int i = 0; i < update.length; i++) angles[i] = new Angular(update[i]);
    residues = updateResidues(-1);
    buildSequence();
    updateScore();
    History.setCurrentState(angles);
  }

  public void updateStructure(Angular[] update, boolean updateZoom) {
    angles = update;
    residues = updateResidues(-1);
    buildSequence();
    updateScore();
    History.setCurrentState(angles);
    if (updateZoom) cam.updateZoom(world);
  }

  public void startMonteCarlo() {
    if (residues == null) return;
    History.addUndoState(angles);
    disableSliders();
    resetResidueLabels();
    unsetKillOptimization();
    Thread thread = new Thread(() -> {
      if (!cam.isAutoZoomed) toggleAutoZoom();
      disableNonCriticalFunctions();
      showCancelOptimization();
      monteCarlo();
      hideCancelOptimization();
      enableNonCriticalFunctions();
      toggleAutoZoom();
    });
    thread.setDaemon(true);
    thread.start();
  }

  public void monteCarlo() {
    MonteCarlo.setup();
    int i = 0;
    while (!MonteCarlo.complete()) {
      if (killOptimization) return;
      MonteCarlo.setTemperature(i);
      angles = MonteCarlo.getNextState(angles);
      if (i % MC_UPDATE_RATE == 0) {
        try {
          Platform.runLater(() -> updateStructure(angles));
        } catch (Exception exc) {}
      }
      i++;
    }
    try {
      Platform.runLater(() -> {
        updateStructure(angles);
        if (MonteCarlo.currentEnergy > MonteCarlo.lowestEnergy) {
          showRecoverLowest();
        } else {
          showOptimizationComplete();
        }
      });
    } catch (Exception exc) { exc.printStackTrace(); }
  }

  /* Undo a move */
  public void undo() {
    State previous = History.undo();
    if (previous == null) return;
    deselect(selectedResidue);
    updateStructure(previous.state, /*updateZoom=*/true);
  }

  /* Redo a move */
  public void redo() {
    State next = History.redo();
    if (next == null) return;
    deselect(selectedResidue);
    updateStructure(next.state, /*updateZoom=*/true);
  }

  // Non-critical UI controls
  @FXML Button gradDescentBtn, infoBtn, monteCarloBtn, redoBtn, repairBtn, undoBtn;
  @FXML MenuBar menubar;
  @FXML ToggleButton autoZoomBtn;
  /* Disable all non-critical elements of UI to prevent users from running
   * conflicting UI elements simultaneously (i.e. configuring gradient descent
   * while gradient descent is occurring)
   */
  public void disableNonCriticalFunctions() {
    Node[] nonCritical = new Node[]{
      autoZoomBtn, gradDescentBtn, infoBtn, menubar, monteCarloBtn, redoBtn, repairBtn, undoBtn
    };
    for (Node n : nonCritical) n.setDisable(true);
  }

  /* Enable non-critical UI elements upon completion of volatile function */
  public void enableNonCriticalFunctions() {
    Node[] nonCritical = new Node[]{
      autoZoomBtn, gradDescentBtn, infoBtn, menubar, monteCarloBtn, redoBtn, repairBtn, undoBtn
    };
    for (Node n : nonCritical) n.setDisable(false);
  }

  /* Create a color bar next to the distance map for reference */
  public Image createColorBar(int width, int height) {
    WritableImage wi = new WritableImage(width, height);
    PixelWriter pw = wi.getPixelWriter();
    for (int h = 0; h < height; h++) {
      double value = currentSequenceMaxDist * (height - h) / height;
      for (int w = 0; w < width; w++) {
        pw.setColor(w, h, Colors.getColorFromValue(value));
      }
    }
    return wi;
  }

  // Volatile because can be set or unset by multiple threads
  private volatile boolean killOptimization = false;
  /* Signal to kill all running optimizations */
  public void setKillOptimization() { killOptimization = true; }

  /* Signal to allow optimizations to run */
  public void unsetKillOptimization() { killOptimization = false; }

  @FXML Button stopOptimizationBtn;
  /* Show cancel button for optimizations */
  public void showCancelOptimization() {
    stopOptimizationBtn.setStyle("visibility: visible;");
  }

  /* Hide cancel button for optimizations */
  public void hideCancelOptimization() {
    stopOptimizationBtn.setStyle("visibility: hidden;");
  }

  /* Handle keyboard during PolyFold life cycle */
  public void handleKeyPressed() {
    final Shortcuts sc = new Shortcuts();
    app.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode().equals(KeyCode.SPACE)) e.consume();
      if (e.getCode().equals(KeyCode.ESCAPE)) setKillOptimization();
      if (e.isShortcutDown()) {
        if (angles == null) return;
        if (e.isAltDown()) {
          sc.handleSave(e, angles);
          if (sc.savedTo != -1) showOverlay("Saved to State " + sc.savedTo);
        } else {
          sc.handleLoad(e);
          if (sc.loadedState != null) {
            updateStructure(sc.loadedState.state, /*updateZoom=*/true);
            showOverlay("Loaded State " + sc.loadedFrom);
          } else if (sc.loadedFrom != -1) {
            showOverlay("No Save State in " + sc.loadedFrom);
          }
        }
      } else {
        if (e.getCode().equals(KeyCode.A)) toggleAutoZoom();
      }
    });
  }

  /* Show right info panel on successful load of protein file */
  public void showProteinMovementUI() {
    hideSplash();
    initSequence();
    generateDistanceMap();
    initSliders();
    disableSliders();
    resetResidueLabels();
    app.getRight().setVisible(true);
  }

  /* Hide all protein UI info */
  public void hideProteinMovementUI() {
    app.getRight().setVisible(false);
  }
}
