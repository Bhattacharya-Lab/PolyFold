# define compiler flags
J_FLAGS = -g
JFX_FLAGS = --module-path $(JAVAFX_HOME) --add-modules javafx.controls,javafx.fxml,javafx.swing
JC = javac
J = java 
MAIN = pf.PolyFold

# clear default definitions
.SUFFIXES: .java .class

# build java files into class files
.java.class: 
	$(JC) $(J_FLAGS) $(JFX_FLAGS) $*.java

# set classes to track
CLASSES = \
	pf/PolyFold.java \
	pf/camera/pfCamera.java \
	pf/controllers/AuxController.java \
	pf/controllers/Controller.java \
	pf/coreutils/FileUtils.java \
	pf/coreutils/History.java \
	pf/coreutils/Limits.java \
	pf/coreutils/Maps.java \
	pf/coreutils/RRUtils.java \
	pf/coreutils/Scoring.java \
	pf/coreutils/Shortcuts.java \
	pf/coreutils/State.java \
	pf/geometry/Chirality.java \
	pf/geometry/LinearAlgebra.java \
	pf/optimizations/GradientDescent.java \
	pf/optimizations/MonteCarlo.java \
	pf/representations/Angular.java \
	pf/representations/Cartesian.java \
	pf/representations/Converter.java \
	pf/representations/Point.java \
	pf/representations/Residue.java \
	pf/ui/Colors.java \
	pf/ui/ViewPort.java

# default behavior
default: classes

# behavior for classes
classes: $(CLASSES:.java=.class)

# run after compile
run: classes 
	$(J) $(JFX_FLAGS) $(MAIN)

clean: 
	$(RM) pf/*.class pf/*/*.class
