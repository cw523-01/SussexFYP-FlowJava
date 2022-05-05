package flowjava;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.script.ScriptException;

/**
 * FlowJava is an application currently in development - It is designed to be an
 * application that lets you create basic java programs from specialised
 * flowcharts
 *
 *
 * @author cwood
 */
public class FlowJava extends Application {
    //boolean for whether the user is currently making a connection between two nodes
    private Boolean makingConnection = false;
    //stores which ever node the user currently has selected 
    private Node selectedComponent = null;
    //vertex chosen as a parent for a new connection
    private Vertex chosenParent = null;
    //current flowchart being built by the user
    private Flowchart flowchart;
    //canvas that the flowchart is built on
    private ZoomableCanvas mainZc;
    //event handlers for vertex views
    private VertexViewGestures vertexGestures;
    //button for initiating a new connection between vertices
    private Button connectionBtn;
    //scrollpane for the main vanvas
    private ScrollPane canvasSp;
    //group for the canvas
    private Group canvasGroup;
    //program runner to compile and run user created programs
    private ProgramRunner progRunner;
    //VBox for the right sidebar of the UI 
    private VBox rightSidebarVb;
    //default text for the right sidebar
    private Text defaultRSTxt;
    //thread to run user created programs on
    private Thread runProgThread;
    //button to open the create vertex dialog for editing a vertex
    private Button editVertexBtn;

    @Override
    public void start(Stage primaryStage) throws ScriptException, IOException {
        
        //instantiate the program runner
        progRunner = new ProgramRunner();
        
        //instantiate flowchart
        flowchart = new Flowchart();

        //instantiate root node
        VBox root = new VBox();

        //instantiate HBox for flowchart controls
        HBox mainControlsHb = new HBox();
        mainControlsHb.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        mainControlsHb.setPrefSize(100, 500);
        VBox.setVgrow(mainControlsHb, Priority.ALWAYS);

        //instantiate right sidebar
        ScrollPane rightSidebarSp = new ScrollPane();
        rightSidebarSp.setMinWidth(250);
        rightSidebarVb = new VBox();
        rightSidebarVb.setPadding(new Insets(10, 10, 10, 10));
        defaultRSTxt = new Text("-select a component-");
        rightSidebarVb.getChildren().add(defaultRSTxt);

        //instantiate toolbar
        VBox toolbarContainerVb = new VBox();
        toolbarContainerVb.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        HBox.setHgrow(toolbarContainerVb, Priority.ALWAYS);
        HBox toolbarHb = new HBox();
        toolbarHb.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        toolbarHb.setMinHeight(100);
        toolbarHb.setSpacing(10);

        //set up the run image view for running created programs
        URL imgURL = getClass().getResource("/images/StartImg.png");
        Image runImg = new Image(imgURL.openStream());
        ImageView runImgView = new ImageView();
        runImgView.setPreserveRatio(true);
        runImgView.setFitHeight(75);
        runImgView.setImage(runImg);

        //set up the convert image view for converting created programs
        imgURL = getClass().getResource("/images/ToJavaImg.png");
        Image convertImg = new Image(imgURL.openStream());
        ImageView convertImgView = new ImageView();
        convertImgView.setPreserveRatio(true);
        convertImgView.setFitHeight(75);
        convertImgView.setImage(convertImg);

        //set up the halt image view for terminating a running program early
        imgURL = getClass().getResource("/images/HaltImg.png");
        Image haltImg = new Image(imgURL.openStream());
        ImageView haltImgView = new ImageView();
        haltImgView.setPreserveRatio(true);
        haltImgView.setFitHeight(75);
        haltImgView.setImage(haltImg);

        //disable the halt image view
        haltImgView.setOpacity(0.5);
        haltImgView.setDisable(true);

        haltImgView.setOnMouseClicked(e -> {
            //terminate a running program
            progRunner.stopRun();
            //enable the run image view
            runImgView.setOpacity(1);
            runImgView.setDisable(false);
            //enable the convert image view
            convertImgView.setOpacity(1);
            convertImgView.setDisable(false);
            //disable the halt image view
            haltImgView.setOpacity(0.5);
            haltImgView.setDisable(true);
            //notify the user of the termination
            showAlert(Alert.AlertType.INFORMATION, "Run terminated!");
        });

        //set up the functions image view for opening the functions manager dialog
        imgURL = getClass().getResource("/images/FunctionsImg.png");
        Image functionsImg = new Image(imgURL.openStream());
        ImageView functionsImgView = new ImageView();
        functionsImgView.setPreserveRatio(true);
        functionsImgView.setFitHeight(75);
        functionsImgView.setImage(functionsImg);

        runImgView.setOnMouseClicked((MouseEvent e) -> {
            //create a runnable for runProgThread
            Runnable runnable = () -> {
                try {
                    //if the current program is syntactically correct
                    if (progRunner.convertThenCompileProgram(flowchart, false, false, null)) {
                        //run the program
                        progRunner.convertThenCompileProgram(flowchart, true, false, null);
                    }
                //print out any user created errors
                } catch (UserCreatedExprException ex) {
                    System.out.println(ex.getCause());
                }
                //enable the run image view
                runImgView.setOpacity(1);
                runImgView.setDisable(false);
                //enable the convert image view
                convertImgView.setOpacity(1);
                convertImgView.setDisable(false);
                //disable the halt image view
                haltImgView.setOpacity(0.5);
                haltImgView.setDisable(true);
            };
            //disable the halt image view
            runImgView.setOpacity(0.5);
            runImgView.setDisable(true);
            //disable the convert image view
            convertImgView.setOpacity(0.5);
            convertImgView.setDisable(true);
            //enable the halt image view
            haltImgView.setOpacity(1);
            haltImgView.setDisable(false);
            //instantiate and run the runProgThread
            runProgThread = new Thread(runnable);
            runProgThread.start();
        });
        
        convertImgView.setOnMouseClicked((MouseEvent e) -> {
            //if program structure is valid
            if (progRunner.validateStructure(flowchart)) {
                try {
                    //if program is syntactically correct
                    if (progRunner.convertThenCompileProgram(flowchart, false, false, null)) {
                        //use file chooser to save the new java file
                        FileChooser fileChooser = new FileChooser();

                        //set extension filter for text files
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java file (*.java)", "*.java");
                        fileChooser.getExtensionFilters().add(extFilter);

                        //show save newJavaFile dialog
                        File newJavaFile = fileChooser.showSaveDialog(primaryStage);
                        
                        //if user has chosen a valid file location
                        if (newJavaFile != null) {
                            //convert program to java and save to file
                            String javaProgram = progRunner.convertToJava(flowchart, newJavaFile.getName().substring(0, newJavaFile.getName().length() - 5), false, false, null);
                            saveTextToFile(javaProgram, newJavaFile);
                            //inform user of successful conversion
                            showAlert(Alert.AlertType.INFORMATION, "Program converted to java file");
                        }
                    }
                //print out any user created errors
                } catch (UserCreatedExprException ex) {
                    System.out.println(ex.getCause());
                }
            } else {
                //inform user of unsuccessful conversion
                showAlert(Alert.AlertType.ERROR, "Program structure is invalid");
            }

        });
        
        functionsImgView.setOnMouseClicked((MouseEvent e) -> {
            //open function manager dialog with current functions
            FunctionManagerDialog fMD = new FunctionManagerDialog(flowchart.getFunctions());
            flowchart.setFunctions(fMD.display());
        });

        //install tooltips to image views
        Tooltip.install(runImgView, new Tooltip("Run Program"));
        Tooltip.install(haltImgView, new Tooltip("Terminate Program"));
        Tooltip.install(convertImgView, new Tooltip("Convert Program to Java File"));
        Tooltip.install(functionsImgView, new Tooltip("Manage Functions"));

        //add tools to toolbar
        toolbarHb.setAlignment(Pos.CENTER);
        toolbarHb.getChildren().addAll(runImgView, convertImgView, haltImgView, functionsImgView);

        //instantiate HBox for the canvas
        HBox canvasHb = new HBox();
        VBox.setVgrow(canvasHb, Priority.ALWAYS);

        //instantiate left sidebar
        ScrollPane leftSidebarSp = new ScrollPane();
        leftSidebarSp.setMinWidth(200);
        leftSidebarSp.setMaxWidth(200);
        VBox leftSidebarVb = new VBox();

        //instantiate new connection button
        connectionBtn = new Button("Connection");
        connectionBtn.setPrefSize(198, 50);
        
        //add icon for connections to connection button
        imgURL = getClass().getResource("/images/ConnectionImg.png");
        Image connectionImg = new Image(imgURL.openStream());
        ImageView connectionImgView = new ImageView();
        connectionImgView.setPreserveRatio(true);
        connectionImgView.setFitHeight(30);
        connectionImgView.setImage(connectionImg);
        connectionBtn.setGraphic(connectionImgView);
        connectionBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new variable declaration button
        Button varDeclarationBtn = new Button("Variable Declaration");
        varDeclarationBtn.setPrefSize(198, 50);
        
        //add icon for I/O to button
        imgURL = getClass().getResource("/images/VarDecImg.png");
        Image varDecImg = new Image(imgURL.openStream());
        ImageView varDecImgView = new ImageView();
        varDecImgView.setPreserveRatio(true);
        varDecImgView.setFitHeight(30);
        varDecImgView.setImage(varDecImg);
        varDeclarationBtn.setGraphic(varDecImgView);
        varDeclarationBtn.setContentDisplay(ContentDisplay.RIGHT);
        
        //instantiate new array variable declaration button
        Button arrayDeclarationBtn = new Button("Array Variable\nDeclaration");
        arrayDeclarationBtn.setTextAlignment(TextAlignment.CENTER);
        arrayDeclarationBtn.setPrefSize(198, 50);

        
        //add icon for I/O to button
        imgURL = getClass().getResource("/images/ArrDecImg.png");
        Image arrVarDecImg = new Image(imgURL.openStream());
        ImageView arrVarDecImgView = new ImageView();
        arrVarDecImgView.setPreserveRatio(true);
        arrVarDecImgView.setFitHeight(30);
        arrVarDecImgView.setImage(arrVarDecImg);
        arrayDeclarationBtn.setGraphic(arrVarDecImgView);
        arrayDeclarationBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new user input to variable button
        Button userInToVarBtn = new Button("User Input to\nVariable");
        userInToVarBtn.setPrefSize(198, 50);
        userInToVarBtn.setTextAlignment(TextAlignment.CENTER);

        //add icon for I/O to button
        imgURL = getClass().getResource("/images/UserInImg.png");
        Image userInImg = new Image(imgURL.openStream());
        ImageView userIntoVarImgView = new ImageView();
        userIntoVarImgView.setPreserveRatio(true);
        userIntoVarImgView.setFitHeight(30);
        userIntoVarImgView.setImage(userInImg);
        userInToVarBtn.setGraphic(userIntoVarImgView);
        userInToVarBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new output button
        Button outputBtn = new Button("Output");
        outputBtn.setPrefSize(198, 50);

        //add icon for I/O to button
        imgURL = getClass().getResource("/images/OutputImg.png");
        Image outputImg = new Image(imgURL.openStream());
        ImageView outputImgView = new ImageView();
        outputImgView.setPreserveRatio(true);
        outputImgView.setFitHeight(30);
        outputImgView.setImage(outputImg);
        outputBtn.setGraphic(outputImgView);
        outputBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new variable assignment button
        Button varAssignmentBtn = new Button("Variable Assignment");
        varAssignmentBtn.setPrefSize(198, 50);

        //add icon for process to button
        imgURL = getClass().getResource("/images/VarAssignImg.png");
        Image processImg = new Image(imgURL.openStream());
        ImageView varAssignImgView = new ImageView();
        varAssignImgView.setPreserveRatio(true);
        varAssignImgView.setFitHeight(30);
        varAssignImgView.setImage(processImg);
        varAssignmentBtn.setGraphic(varAssignImgView);
        varAssignmentBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new if statement button
        Button ifStmtBtn = new Button("If Statement");
        ifStmtBtn.setPrefSize(198, 50);

        //add icon for decision to button
        imgURL = getClass().getResource("/images/IfStmtImg.png");
        Image ifStmtImg = new Image(imgURL.openStream());
        ImageView ifImgView = new ImageView();
        ifImgView.setPreserveRatio(true);
        ifImgView.setFitHeight(30);
        ifImgView.setImage(ifStmtImg);
        ifStmtBtn.setGraphic(ifImgView);
        ifStmtBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new while loop button
        Button whileBtn = new Button("While Loop");
        whileBtn.setPrefSize(198, 50);

        //add icon for decision to button
        imgURL = getClass().getResource("/images/WhileImg.png");
        Image whileImg = new Image(imgURL.openStream());
        ImageView whileImgView = new ImageView();
        whileImgView.setPreserveRatio(true);
        whileImgView.setFitHeight(30);
        whileImgView.setImage(whileImg);
        whileBtn.setGraphic(whileImgView);
        whileBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new for loop button
        Button forBtn = new Button("For Loop");
        forBtn.setPrefSize(198, 50);

        //add icon for decision to button
        imgURL = getClass().getResource("/images/ForImg.png");
        Image forImg = new Image(imgURL.openStream());
        ImageView forImgView = new ImageView();
        forImgView.setPreserveRatio(true);
        forImgView.setFitHeight(30);
        forImgView.setImage(forImg);
        forBtn.setGraphic(forImgView);
        forBtn.setContentDisplay(ContentDisplay.RIGHT);

        //instantiate new function invocation button
        Button functBtn = new Button("Invoke Function");
        functBtn.setPrefSize(198, 50);

        //add icon for invoke to button
        imgURL = getClass().getResource("/images/FunctInvokeImg.png");
        Image invokeImg = new Image(imgURL.openStream());
        ImageView invokeImgView = new ImageView();
        invokeImgView.setPreserveRatio(true);
        invokeImgView.setFitHeight(30);
        invokeImgView.setImage(invokeImg);
        functBtn.setGraphic(invokeImgView);
        functBtn.setContentDisplay(ContentDisplay.RIGHT);

        //add buttons to left sidebar
        leftSidebarVb.getChildren().addAll(connectionBtn, varDeclarationBtn, arrayDeclarationBtn, userInToVarBtn, outputBtn, varAssignmentBtn, ifStmtBtn, whileBtn, forBtn, functBtn);

        //instantiate canvas scrollpane
        canvasSp = new ScrollPane();
        HBox.setHgrow(canvasSp, Priority.ALWAYS);
        canvasSp.setStyle("-fx-background: lightgrey");

        //instantiate canvas
        mainZc = new ZoomableCanvas();

        //instantiate canvas group and add it to canvas scroll pane
        canvasGroup = new Group();
        canvasGroup.getChildren().add(mainZc);
        canvasSp.setContent(canvasGroup);
        canvasSp.setPannable(true);

        //instantiate footer
        HBox footerHb = new HBox();
        footerHb.setPrefHeight(25);

        //instantiate menu bar
        MenuBar primarySceneMb = new MenuBar();

        //instantiate file menu
        Menu fileMenu = new Menu("File");
        
        //instatiate manage functions menu item
        MenuItem manageFunctionsMenuItem = new MenuItem("Manage Functions");
        manageFunctionsMenuItem.setOnAction(e -> {
            //fire mouse click event on functions image view  
            Event.fireEvent(functionsImgView, new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0, MouseButton.PRIMARY, 1,
                    true, true, true, true, true, true, true, true, true, true, null));
        });

        //add menu items to file menu
        fileMenu.getItems().addAll(manageFunctionsMenuItem);

        //instantiate edit menu
        Menu editMenu = new Menu("Edit");
        //instatiate edit menu items 
        MenuItem deselectMenuItem = new MenuItem("Deselect");
        MenuItem deleteMenuItem = new MenuItem("Delete");

        deselectMenuItem.setOnAction(e -> {
            deselectComponent();
        });

        //add items to edit menu
        editMenu.getItems().addAll(deselectMenuItem, deleteMenuItem);

        //instantiate view menu
        Menu viewMenu = new Menu("View");
        //instantiate view menu items
        MenuItem zoomInMenuItem = new MenuItem("Zoom In");
        MenuItem zoomOutMenuItem = new MenuItem("Zoom Out");

        zoomOutMenuItem.setOnAction(e -> {
            //canvas zoom out by 1.2
            mainZc.setScale(Math.max(.05d, (mainZc.getScaleVal() / 1.2)));
        });

        zoomInMenuItem.setOnAction(e -> {
            //canvas zoom in by 1.2
            mainZc.setScale(Math.min(10.0d, (mainZc.getScaleVal() * 1.2)));
        });

        //add menu items to view menu
        viewMenu.getItems().addAll(zoomInMenuItem, zoomOutMenuItem);

        //instantiate help menu
        Menu helpMenu = new Menu("Help");
        //instantiate help menu item
        MenuItem userManualMenuitem = new MenuItem("User Manual");

        userManualMenuitem.setOnAction(e -> {
            //open user manual file using default pdf viewer
            try {
                Path tmp = Files.createTempFile("FlowJavaUserManual-", ".pdf");
                Files.copy(getClass().getResourceAsStream("/user_manual/FlowJavaUserManual.pdf"), tmp, StandardCopyOption.REPLACE_EXISTING);
                HostServices hostServices = getHostServices();
                hostServices.showDocument(tmp.toString());
                tmp.toFile().deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        //add menu items to help menu
        helpMenu.getItems().addAll(userManualMenuitem);

        //add menus to menu bar
        primarySceneMb.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);

        //compile UI components
        leftSidebarSp.setContent(leftSidebarVb);
        canvasHb.getChildren().addAll(leftSidebarSp, canvasSp);
        toolbarContainerVb.getChildren().addAll(toolbarHb, canvasHb);
        rightSidebarSp.setContent(rightSidebarVb);
        mainControlsHb.getChildren().addAll(toolbarContainerVb, rightSidebarSp);
        root.getChildren().addAll(primarySceneMb, mainControlsHb, footerHb);

        //instantiate scene and add listner for zooming canvas
        Scene scene = new Scene(root, 1350, 800);
        scene.addEventFilter(ScrollEvent.ANY, mainZc.getOnScrollEventHandler());

        deleteMenuItem.setOnAction(e -> {
            //fire delete key press event in scene
            Event.fireEvent(scene, new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.DELETE, false, false, false, false));
        });

        //instantiate vertex gestures 
        vertexGestures = new VertexViewGestures(mainZc, flowchart.getVertices());

        //set event handler for new variable declaration button
        varDeclarationBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon parallelogram = new Polygon();
            parallelogram.getPoints().addAll(30.0, 0.0, 185.0, 0.0,
                    155.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String varDecDefStyle = "-fx-fill: lightcyan; -fx-stroke: black; -fx-stroke-width: 2;";
            String varDecSelStyle = "-fx-fill: lightcyan; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newVarDeclaration = new Vertex(new VarDecController(), parallelogram, varDecDefStyle, varDecSelStyle);
            VarDecController newVarDecController = (VarDecController) newVarDeclaration.getController();
            newVarDecController.setVertex(newVarDeclaration);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Variable Declaration", flowchart.getVariables(), false, null);
            boolean valuesNotNull = true;
            int i = 0;

            //check if given values are null
            while (valuesNotNull && i < dialogResults.length - 1) {
                if (dialogResults[i] == null) {
                    valuesNotNull = false;
                }
                i++;
            }

            //only instantiate vertex if the values are not null
            if (valuesNotNull) {
                //set contoller values
                newVarDecController.setType((VarType) dialogResults[0]);
                newVarDecController.setName((String) dialogResults[1]);
                newVarDecController.setExpr((String) dialogResults[2]);
                newVarDecController.setExprHbx((ExpressionHBox) dialogResults[3]);
                newVarDecController.setUsingExprHbx((boolean) dialogResults[4]);

                //set up vertex
                setUpNewVertex(newVarDeclaration);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newVarDeclaration.getView());

                //add vertex to flowchart
                flowchart.addVertex(newVarDeclaration);

                //add variable to flowchart
                Var newVar = flowchart.addVar(newVarDecController.getType(), newVarDecController.getName(), "");
                newVarDecController.setVar(newVar);

                //update vertex view label
                String label = newVarDecController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 15) + "...";
                }
                newVarDeclaration.getView().updateLabel(label);

            }
        });

        userInToVarBtn.setOnAction(e -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon parallelogram = new Polygon();
            parallelogram.getPoints().addAll(30.0, 0.0, 185.0, 0.0,
                    155.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String userInDefStyle = "-fx-fill: lightsalmon; -fx-stroke: black; -fx-stroke-width: 2;";
            String userInSelStyle = "-fx-fill: lightsalmon; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newUserInToVar = new Vertex(new UserInToVarController(), parallelogram, userInDefStyle, userInSelStyle);
            UserInToVarController newUserInToVarController = (UserInToVarController) newUserInToVar.getController();
            newUserInToVarController.setVertex(newUserInToVar);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("User Input to Variable", flowchart.getVariables(), false, null);

            boolean valuesNotNull = true;
            int i = 0;

            //check if given values are null
            while (valuesNotNull && i < dialogResults.length - 1) {
                if (dialogResults[i] == null) {
                    valuesNotNull = false;
                }
                i++;
            }

            //only instantiate vertex if the values are not null
            if (valuesNotNull) {
                //set contoller values
                newUserInToVarController.setType((VarType) dialogResults[0]);
                newUserInToVarController.setName((String) dialogResults[1]);

                //set up vertex
                setUpNewVertex(newUserInToVar);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newUserInToVar.getView());

                //add vertex to flowchart
                flowchart.addVertex(newUserInToVar);

                //add variable to flowchart
                Var newVar = flowchart.addVar(newUserInToVarController.getType(), newUserInToVarController.getName(), "");
                newUserInToVarController.setVar(newVar);

                //update vertex view label
                String label = newUserInToVarController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 15) + "...";
                }
                newUserInToVar.getView().updateLabel(label);

            }

        });

        outputBtn.setOnAction(e -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon parallelogram = new Polygon();
            parallelogram.getPoints().addAll(30.0, 0.0, 185.0, 0.0,
                    155.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String outputDefStyle = "-fx-fill: moccasin; -fx-stroke: black; -fx-stroke-width: 2;";
            String outputSelStyle = "-fx-fill: moccasin; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newOutput = new Vertex(new OutputController(), parallelogram, outputDefStyle, outputSelStyle);
            OutputController newOutputController = (OutputController) newOutput.getController();
            newOutputController.setVertex(newOutput);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Output", null, false, null);

            boolean isValid = (boolean) dialogResults[2];

            //only instantiate vertex if the values are not null
            if (isValid) {
                //set contoller values
                newOutputController.setExpr((String) dialogResults[0]);
                newOutputController.setExprHbx((ExpressionHBox) dialogResults[1]);
                newOutputController.setUsingExprHbx((boolean) dialogResults[3]);

                //set up vertex
                setUpNewVertex(newOutput);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newOutput.getView());

                //add vertex to flowchart
                flowchart.addVertex(newOutput);

                //update vertex view label
                String label = newOutputController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 15) + "...";
                }
                newOutput.getView().updateLabel(label);
            }

        });

        varAssignmentBtn.setOnAction(e -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon rectangle = new Polygon();
            rectangle.getPoints().addAll(0.0, 0.0, 185.0, 0.0,
                    185.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String varAssignDefStyle = "-fx-fill: lightsteelblue; -fx-stroke: black; -fx-stroke-width: 2;";
            String varAssignSelStyle = "-fx-fill: lightsteelblue; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newVarAssign = new Vertex(new VarAssignController(), rectangle, varAssignDefStyle, varAssignSelStyle);
            VarAssignController newVarAssignController = (VarAssignController) newVarAssign.getController();
            newVarAssignController.setVertex(newVarAssign);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Variable Assignment", null, false, null);

            boolean isValid = (boolean) dialogResults[3];

            //only instantiate vertex if the values are not null
            if (isValid) {
                //set contoller values
                newVarAssignController.setVarName((String) dialogResults[0]);
                newVarAssignController.setExpr((String) dialogResults[1]);
                newVarAssignController.setExprHbx((ExpressionHBox) dialogResults[2]);
                newVarAssignController.setUsingExprHbx((boolean) dialogResults[4]);

                //set up vertex
                setUpNewVertex(newVarAssign);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newVarAssign.getView());

                //add vertex to flowchart
                flowchart.addVertex(newVarAssign);

                //update vertex view label
                String label = newVarAssignController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 19) + "...";
                }
                newVarAssign.getView().updateLabel(label);

            }

        });

        ifStmtBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon ifDiamond = new Polygon();
            ifDiamond.getPoints().addAll(0.0, 50.0, 100.0, 0.0,
                    200.00, 50.0, 100.0, 100.0);

            Polygon endIfRect = new Polygon();
            endIfRect.getPoints().addAll(0.0, 0.0, 75.0, 0.0,
                    75.00, 35.0, 0.0, 35.0);

            //instantiate strings for different view styles
            String ifStmtDefStyle = "-fx-fill: lavenderblush; -fx-stroke: black; -fx-stroke-width: 2;";
            String ifStmtSelStyle = "-fx-fill: lavenderblush; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate vertex model view and controller
            Vertex newIfStmt = new Vertex(new IfStmtController(), ifDiamond, ifStmtDefStyle, ifStmtSelStyle, true);
            IfStmtController newIfStmtController = (IfStmtController) newIfStmt.getController();
            newIfStmtController.setVertex(newIfStmt);
            
            //set up end if vertex
            Vertex newEndIf = new Vertex(new EndIfController(), endIfRect, ifStmtDefStyle, ifStmtSelStyle);
            EndIfController newEndIfController = (EndIfController) newEndIf.getController();
            newEndIfController.setVertex(newEndIf);
            newIfStmtController.setEndIf(newEndIfController);
            newEndIfController.setIfStmt(newIfStmtController);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("If Statement", null, false, null);

            boolean isValid = (boolean) dialogResults[2];

            //only instantiate vertex if the values are not null
            if (isValid) {

                //set contoller values
                newIfStmtController.setExpr((String) dialogResults[0]);
                newIfStmtController.setExprHbx((ExpressionHBox) dialogResults[1]);

                //set up vertex
                setUpNewVertex(newIfStmt);
                setUpNewVertex(newEndIf);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newEndIf.getView(), newIfStmt.getView());

                //add vertex to flowchart
                flowchart.addVertex(newIfStmt);
                flowchart.addVertex(newEndIf);

                //update vertex view label
                String label = newIfStmtController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 18) + "...";
                }
                //set up end if
                newIfStmt.getView().updateLabel(label);
                newEndIf.getView().updateLabel(newEndIfController.getVertexLabel());
                newEndIf.getView().setTranslateY(newEndIf.getView().getTranslateY() + 200);
                
                //add edge to indicate if-end if relationship
                Edge newEdge = flowchart.addEdge(newIfStmt, newEndIf);
                newEdge.getController().setEdge(newEdge);
                EdgeView newEdgeView = newEdge.getView();

                //set edge event handler
                newEdgeView.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent mE) -> {
                    //deselect if ctrl is down
                    if (mE.isShortcutDown()) {
                        if (mE.getSource().equals(selectedComponent)) {
                            deselectComponent();
                            return;
                        }
                    }
                    //deselect previous component
                    deselectComponent();
                    updateRSidebar(newEdgeView.getEdge());
                });
                //add edge to canvas
                mainZc.getChildren().add(newEdgeView);
                makingConnection = false;
                canvasSp.setCursor(Cursor.DEFAULT);
                connectionBtn.setStyle("");
                newEdgeView.toBack();
                newEdgeView.setX2(newEndIf.getView().getTranslateX() + (newEndIf.getView().getWidth() / 2));
                //deselect previous component
                deselectComponent();
                //select this component
                selectedComponent = newEdgeView;
                newEdgeView.select();
                
                //configure relation edge design and functionality
                newEdgeView.makeSubtle();
                newEdgeView.setRemovable(false);

                updateRSidebar(newEdgeView.getEdge());
            }

        });

        whileBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon whileDiamond = new Polygon();
            whileDiamond.getPoints().addAll(0.0, 50.0, 100.0, 0.0,
                    200.00, 50.0, 100.0, 100.0);

            Polygon endWhileRect = new Polygon();
            endWhileRect.getPoints().addAll(0.0, 0.0, 75.0, 0.0,
                    75.00, 35.0, 0.0, 35.0);

            //instantiate strings for different view styles
            String whileDefStyle = "-fx-fill: antiquewhite; -fx-stroke: black; -fx-stroke-width: 2;";
            String whileSelStyle = "-fx-fill: antiquewhite; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newWhile = new Vertex(new WhileController(), whileDiamond, whileDefStyle, whileSelStyle, false);
            WhileController newWhileController = (WhileController) newWhile.getController();
            newWhileController.setVertex(newWhile);

            //setup endwhile vertex
            Vertex newEndWhile = new Vertex(new EndWhileController(), endWhileRect, whileDefStyle, whileSelStyle);
            EndWhileController newEndWhileController = (EndWhileController) newEndWhile.getController();
            newEndWhileController.setVertex(newEndWhile);
            newWhileController.setEndWhile(newEndWhileController);
            newEndWhileController.setWhileCtrl(newWhileController);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("While Loop", null, false, null);

            boolean isValid = (boolean) dialogResults[2];

            //only instantiate vertex if the values are not null
            if (isValid) {

                //set contoller values
                newWhileController.setExpr((String) dialogResults[0]);
                newWhileController.setExprHbx((ExpressionHBox) dialogResults[1]);

                //set up vertex
                setUpNewVertex(newWhile);
                setUpNewVertex(newEndWhile);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newEndWhile.getView(), newWhile.getView());

                //add vertex to flowchart
                flowchart.addVertex(newWhile);
                flowchart.addVertex(newEndWhile);

                //update vertex view label
                String label = newWhileController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 18) + "...";
                }
                newWhile.getView().updateLabel(label);
                newEndWhile.getView().updateLabel(newEndWhileController.getVertexLabel());

                newEndWhile.getView().setTranslateY(newEndWhile.getView().getTranslateY() + 200);

                Edge newEdge = flowchart.addEdge(newWhile, newEndWhile);
                newEdge.getController().setEdge(newEdge);
                EdgeView newEdgeView = newEdge.getView();

                //set edge event handler
                newEdgeView.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent mE) -> {
                    //deselect if ctrl is down
                    if (mE.isShortcutDown()) {
                        if (mE.getSource().equals(selectedComponent)) {
                            deselectComponent();
                            return;
                        }
                    }
                    //deselect previous component
                    deselectComponent();
                    updateRSidebar(newEdgeView.getEdge());
                });
                //add edge to canvas
                mainZc.getChildren().add(newEdgeView);
                makingConnection = false;
                canvasSp.setCursor(Cursor.DEFAULT);
                connectionBtn.setStyle("");
                newEdgeView.toBack();
                newEdgeView.setX2(newEndWhile.getView().getTranslateX() + (newEndWhile.getView().getWidth() / 2));
                //deselect previous component
                deselectComponent();
                //select this component
                selectedComponent = newEdgeView;
                newEdgeView.select();

                newEdgeView.makeSubtle();
                newEdgeView.setRemovable(false);
                
                updateRSidebar(newEdgeView.getEdge());
            }

        });

        forBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon forDiamond = new Polygon();
            forDiamond.getPoints().addAll(0.0, 50.0, 100.0, 0.0,
                    200.00, 50.0, 100.0, 100.0);

            Polygon endForRect = new Polygon();
            endForRect.getPoints().addAll(0.0, 0.0, 75.0, 0.0,
                    75.00, 35.0, 0.0, 35.0);

            //instantiate strings for different view styles
            String forDefStyle = "-fx-fill: aquamarine; -fx-stroke: black; -fx-stroke-width: 2;";
            String forSelStyle = "-fx-fill: aquamarine; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newFor = new Vertex(new ForLoopController(), forDiamond, forDefStyle, forSelStyle, false);
            ForLoopController newForController = (ForLoopController) newFor.getController();
            newForController.setVertex(newFor);

            //setup end for vertex
            Vertex newEndFor = new Vertex(new EndForController(), endForRect, forDefStyle, forSelStyle);
            EndForController newEndForController = (EndForController) newEndFor.getController();
            newEndForController.setVertex(newEndFor);
            newForController.setEndFor(newEndForController);
            newEndForController.setForCtrl(newForController);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("For Loop", null, false, null);

            boolean isValid = (boolean) dialogResults[3];

            //only instantiate vertex if the values are not null
            if (isValid) {

                //set contoller values
                newForController.setInitialExpr((String) dialogResults[0]);
                newForController.setConditionExpr((String) dialogResults[1]);
                newForController.setUpdateExpr((String) dialogResults[2]);

                //set up vertex
                setUpNewVertex(newFor);
                setUpNewVertex(newEndFor);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newEndFor.getView(), newFor.getView());

                //add vertex to flowchart
                flowchart.addVertex(newFor);
                flowchart.addVertex(newEndFor);

                //update vertex view label
                String label = newForController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 18) + "...";
                }
                newFor.getView().updateLabel(label);
                newEndFor.getView().updateLabel(newEndForController.getVertexLabel());

                newEndFor.getView().setTranslateY(newEndFor.getView().getTranslateY() + 200);

                Edge newEdge = flowchart.addEdge(newFor, newEndFor);
                newEdge.getController().setEdge(newEdge);
                EdgeView newEdgeView = newEdge.getView();

                //set edge event handler
                newEdgeView.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent mE) -> {
                    //deselect if ctrl is down
                    if (mE.isShortcutDown()) {
                        if (mE.getSource().equals(selectedComponent)) {
                            deselectComponent();
                            return;
                        }
                    }
                    //deselect previous component
                    deselectComponent();
                    updateRSidebar(newEdgeView.getEdge());
                });
                //add edge to canvas
                mainZc.getChildren().add(newEdgeView);
                makingConnection = false;
                canvasSp.setCursor(Cursor.DEFAULT);
                connectionBtn.setStyle("");
                newEdgeView.toBack();
                newEdgeView.setX2(newEndFor.getView().getTranslateX() + (newEndFor.getView().getWidth() / 2));
                //deselect previous component
                deselectComponent();
                //select this component
                selectedComponent = newEdgeView;
                newEdgeView.select();

                newEdgeView.makeSubtle();
                newEdgeView.setRemovable(false);
                
                updateRSidebar(newEdgeView.getEdge());
            }

        });

        functBtn.setOnAction(e -> {
            //ensure functions exsist that can be invoked
            if (flowchart.getFunctions().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Must create functions before you can invoke them!");
                return;
            }

            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon barredRectangle = new Polygon();
            barredRectangle.getPoints().addAll(0.0, 0.0,
                    10.0, 0.0, 10.0, 70.0, 10.0, 0.0,
                    175.0, 0.0, 175.0, 70.0, 175.0, 0.0,
                    185.0, 0.0,
                    185.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String functDefStyle = "-fx-fill: lavender; -fx-stroke: black; -fx-stroke-width: 2;";
            String functSelStyle = "-fx-fill: lavender; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newFunctInvoke = new Vertex(new FunctInvokeController(), barredRectangle, functDefStyle, functSelStyle);
            FunctInvokeController newFunctInvokeController = (FunctInvokeController) newFunctInvoke.getController();
            newFunctInvokeController.setVertex(newFunctInvoke);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();

            Object[] dialogResults = nVD.display("Invoke Function", flowchart.getVariables(), false, new Object[]{flowchart.getFunctions()});

            boolean isValid = (boolean) dialogResults[3];

            if (isValid) {
                //set contoller values
                newFunctInvokeController.setFunctionName((String) dialogResults[0]);
                newFunctInvokeController.setParameterVals((String) dialogResults[1]);
                newFunctInvokeController.setVariableForValue((String) dialogResults[2]);

                //set up vertex
                setUpNewVertex(newFunctInvoke);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newFunctInvoke.getView());

                //add vertex to flowchart
                flowchart.addVertex(newFunctInvoke);

                //update vertex view label
                String label = newFunctInvokeController.getVertexLabel();
                if (label.length() > 16) {
                    label = label.substring(0, 17) + "...";
                }

                newFunctInvoke.getView().updateLabel(label);

            }

        });

        arrayDeclarationBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();

            //create parallelogram for vertex view background
            Polygon parallelogram = new Polygon();
            parallelogram.getPoints().addAll(30.0, 0.0, 185.0, 0.0,
                    155.00, 70.0, 0.0, 70.0);

            //instantiate strings for different view styles
            String arrDecDefStyle = "-fx-fill: plum; -fx-stroke: black; -fx-stroke-width: 2;";
            String arrDecSelStyle = "-fx-fill: plum; -fx-stroke: red; -fx-stroke-width: 2;";

            //instantiate Vertex model view and controller
            Vertex newArrDeclaration = new Vertex(new ArrayDecController(), parallelogram, arrDecDefStyle, arrDecSelStyle);
            ArrayDecController newArrDecController = (ArrayDecController) newArrDeclaration.getController();
            newArrDecController.setVertex(newArrDeclaration);

            //open create vertex dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Array Declaration", flowchart.getVariables(), false, null);
            boolean valuesNotNull = true;
            int i = 0;

            //check if given values are null
            while (valuesNotNull && i < dialogResults.length - 1) {
                if (dialogResults[i] == null) {
                    valuesNotNull = false;
                }
                i++;
            }

            //only instantiate vertex if the values are not null
            if (valuesNotNull) {
                //set contoller values
                newArrDecController.setType((VarType) dialogResults[0]);
                newArrDecController.setName((String) dialogResults[1]);
                newArrDecController.setValues((String) dialogResults[2]);
                newArrDecController.setLen((String) dialogResults[2]);
                newArrDecController.setDeclaredByValues((Boolean) dialogResults[3]);

                //set up vertex
                setUpNewVertex(newArrDeclaration);

                //add vertex view to canvas
                mainZc.getChildren().addAll(newArrDeclaration.getView());

                //add vertex to flowchart
                flowchart.addVertex(newArrDeclaration);

                //add variable to flowchart
                Var newVar = flowchart.addVar(newArrDecController.getType(), newArrDecController.getName(), "");
                newArrDecController.setVar(newVar);

                //update vertex view label
                String label = newArrDecController.getVertexLabel();
                if (label.length() > 18) {
                    label = label.substring(0, 15) + "...";
                }
                newArrDeclaration.getView().updateLabel(label);
                
                showAlert(Alert.AlertType.INFORMATION, "array created, you can access or modify array elements in expressions using "
                        + newArrDecController.getName() + "[~element index~]");
            }
        });

        //set event handler for new connection button
        connectionBtn.setOnAction(e -> {
            resetCanvasSize();
            canvasSp.setCursor(Cursor.CROSSHAIR);

            //deselect any currently selected node
            deselectComponent();
            //set chosen parent to null
            chosenParent = null;
            //negate making connection boolean
            makingConnection = !makingConnection;

            if (makingConnection) {
                //give visual confirmation of begining connection
                connectionBtn.setStyle("-fx-background-color: lightgrey;");
            } else {
                //give visual confirmation of cancelling connection
                connectionBtn.setStyle("");
                canvasSp.setCursor(Cursor.DEFAULT);
            }
        });

        //set the key pressed event handler for the scene
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                //if a vertex is selected, delete it
                if (selectedComponent instanceof VertexView) {
                    VertexView vView = (VertexView) selectedComponent;
                    if (!(vView.getVertex().getController() instanceof Terminal)) {
                        deleteVertex(vView.getVertex());
                    }
                }
                //if an edge is selected, delete it
                if (selectedComponent instanceof EdgeView) {
                    if (((EdgeView) selectedComponent).isRemovable()) {
                        EdgeView eView = (EdgeView) selectedComponent;
                        mainZc.getChildren().remove(eView);
                        flowchart.removeEdge(eView.getEdge());
                        updateRSidebar();
                    }
                }
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            //if up or w is pressed select the parent of the currently selected vertex/edge
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
                if (selectedComponent instanceof VertexView) {
                    VertexView vView = (VertexView) selectedComponent;
                    for (Pair c : vView.getVertex().getConnections()) {
                        if (!(boolean) c.getValue()) {
                            deselectComponent();
                            selectedComponent = ((Edge) c.getKey()).getView();
                            updateRSidebar((Edge) c.getKey());
                            ((Edge) c.getKey()).getView().select();
                            e.consume();
                            break;
                        }
                    }
                } else if (selectedComponent instanceof EdgeView) {
                    EdgeView eView = (EdgeView) selectedComponent;
                    deselectComponent();
                    selectVertex(eView.getEdge().getController().getParent());
                    e.consume();
                }
            //if down or s is pressed select the child of the currently selected vertex/edge
            } else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
                if (selectedComponent instanceof VertexView) {
                    VertexView vView = (VertexView) selectedComponent;
                    for (Pair c : vView.getVertex().getConnections()) {
                        if ((boolean) c.getValue()) {
                            deselectComponent();
                            selectedComponent = ((Edge) c.getKey()).getView();
                            updateRSidebar((Edge) c.getKey());
                            ((Edge) c.getKey()).getView().select();
                            e.consume();
                            break;
                        }
                    }
                } else if (selectedComponent instanceof EdgeView) {
                    EdgeView eView = (EdgeView) selectedComponent;
                    deselectComponent();
                    selectVertex(eView.getEdge().getController().getChild());
                    e.consume();
                }
            }
        });

        //set up edit button
        editVertexBtn = new Button("Edit");
        editVertexBtn.setOnAction(e -> {
            CreateVertexDialog nVD = new CreateVertexDialog();
            if (selectedComponent instanceof VertexView) {
                VertexController currentController = ((VertexView) selectedComponent).getVertex().getController();
                //open create vertex dialog with the controller of selectedComponent then update based on input form response
                if (currentController instanceof IfStmtController) {
                    IfStmtController currIfStmt = (IfStmtController) currentController;
                    Object[] dialogResults = nVD.display("If Statement", null, true, new Object[]{currIfStmt.getExpr(), currIfStmt.getExprHbx()});

                    boolean isValid = (boolean) dialogResults[2];

                    //only instantiate vertex if the values are not null
                    if (isValid) {
                        //set contoller values
                        currIfStmt.setExpr((String) dialogResults[0]);
                        currIfStmt.setExprHbx((ExpressionHBox) dialogResults[1]);

                        //update vertex view label
                        String label = currIfStmt.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 18) + "...";
                        }
                        currIfStmt.getVertex().getView().updateLabel(label);
                        updateRSidebar();
                    }

                } else if (currentController instanceof WhileController) {
                    WhileController currWhile = (WhileController) currentController;
                    Object[] dialogResults = nVD.display("While Loop", null, true, new Object[]{currWhile.getExpr(), currWhile.getExprHbx()});
                    boolean isValid = (boolean) dialogResults[2];

                    //only instantiate vertex if the values are not null
                    if (isValid) {

                        //set contoller values
                        currWhile.setExpr((String) dialogResults[0]);
                        currWhile.setExprHbx((ExpressionHBox) dialogResults[1]);

                        //update vertex view label
                        String label = currWhile.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 18) + "...";
                        }
                        currWhile.getVertex().getView().updateLabel(label);
                        updateRSidebar();
                    }

                } else if (currentController instanceof ForLoopController) {
                    ForLoopController currFor = (ForLoopController) currentController;
                    Object[] dialogResults = nVD.display("For Loop", null, true, new Object[]{currFor.getInitialExpr(), currFor.getConditionExpr(), currFor.getUpdateExpr()});
                    boolean isValid = (boolean) dialogResults[3];

                    //only instantiate vertex if the values are not null
                    if (isValid) {

                        //set contoller values
                        currFor.setInitialExpr((String) dialogResults[0]);
                        currFor.setConditionExpr((String) dialogResults[1]);
                        currFor.setUpdateExpr((String) dialogResults[2]);

                        //update vertex view label
                        String label = currFor.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 18) + "...";
                        }
                        currFor.getVertex().getView().updateLabel(label);

                        updateRSidebar();
                    }

                } else if (currentController instanceof OutputController) {
                    OutputController currOutput = (OutputController) currentController;
                    Object[] dialogResults = nVD.display("Output", null, true, new Object[]{currOutput.getExpr(), currOutput.getExprHbx()});
                    boolean isValid = (boolean) dialogResults[2];

                    //only instantiate vertex if the values are not null
                    if (isValid) {
                        //set contoller values
                        currOutput.setExpr((String) dialogResults[0]);
                        currOutput.setExprHbx((ExpressionHBox) dialogResults[1]);
                        currOutput.setUsingExprHbx((boolean) dialogResults[3]);

                        //update vertex view label
                        String label = currOutput.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 15) + "...";
                        }
                        currOutput.getVertex().getView().updateLabel(label);
                        updateRSidebar();
                    }

                } else if (currentController instanceof UserInToVarController) {
                    UserInToVarController currUserInToVar = (UserInToVarController) currentController;
                    ArrayList<Var> currVars = flowchart.getVariables();
                    currVars.remove(currUserInToVar.getVar());
                    Object[] dialogResults = nVD.display("User Input to Variable", currVars, true, new Object[]{currUserInToVar.getType(), currUserInToVar.getName()});

                    boolean valuesNotNull = true;
                    int i = 0;

                    //check if given values are null
                    while (valuesNotNull && i < dialogResults.length - 1) {
                        if (dialogResults[i] == null) {
                            valuesNotNull = false;
                        }
                        i++;
                    }

                    //only instantiate vertex if the values are not null
                    if (valuesNotNull) {
                        //set contoller values
                        currUserInToVar.setType((VarType) dialogResults[0]);
                        currUserInToVar.setName((String) dialogResults[1]);

                        flowchart.removeVar(currUserInToVar.getVar());

                        //update variable in flowchart
                        Var newVar = flowchart.addVar(currUserInToVar.getType(), currUserInToVar.getName(), "");
                        currUserInToVar.setVar(newVar);

                        //update vertex view label
                        String label = currUserInToVar.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 15) + "...";
                        }
                        currUserInToVar.getVertex().getView().updateLabel(label);

                    }

                } else if (currentController instanceof VarAssignController) {
                    VarAssignController currVarAssign = (VarAssignController) currentController;
                    Object[] dialogResults = nVD.display("Variable Assignment", null, true, new Object[]{currVarAssign.getVarName(), currVarAssign.getExpr(), currVarAssign.getExprHbx()});

                    boolean isValid = (boolean) dialogResults[3];

                    //only instantiate vertex if the values are not null
                    if (isValid) {
                        //set contoller values
                        currVarAssign.setVarName((String) dialogResults[0]);
                        currVarAssign.setExpr((String) dialogResults[1]);
                        currVarAssign.setExprHbx((ExpressionHBox) dialogResults[2]);
                        currVarAssign.setUsingExprHbx((boolean) dialogResults[4]);

                        //update vertex view label
                        String label = currVarAssign.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 19) + "...";
                        }
                        currVarAssign.getVertex().getView().updateLabel(label);

                    }

                } else if (currentController instanceof VarDecController) {
                    VarDecController currVarDec = (VarDecController) currentController;
                    ArrayList<Var> currVars = flowchart.getVariables();
                    currVars.remove(currVarDec.getVar());
                    Object[] dialogResults = nVD.display("Variable Declaration", currVars, true, new Object[]{currVarDec.getType(), currVarDec.getName(), currVarDec.getExpr(), currVarDec.getExprHbx()});

                    boolean valuesNotNull = true;
                    int i = 0;

                    //check if given values are null
                    while (valuesNotNull && i < dialogResults.length - 1) {
                        if (dialogResults[i] == null) {
                            valuesNotNull = false;
                        }
                        i++;
                    }

                    //only instantiate vertex if the values are not null
                    if (valuesNotNull) {
                        //set contoller values
                        currVarDec.setType((VarType) dialogResults[0]);
                        currVarDec.setName((String) dialogResults[1]);
                        currVarDec.setExpr((String) dialogResults[2]);
                        currVarDec.setExprHbx((ExpressionHBox) dialogResults[3]);
                        currVarDec.setUsingExprHbx((boolean) dialogResults[4]);

                        //update variable in flowchart
                        Var newVar = flowchart.addVar(currVarDec.getType(), currVarDec.getName(), "");
                        currVarDec.setVar(newVar);

                        //update vertex view label
                        String label = currVarDec.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 15) + "...";
                        }
                        currVarDec.getVertex().getView().updateLabel(label);

                    }
                } else if (currentController instanceof FunctInvokeController) {
                    FunctInvokeController currFI = (FunctInvokeController) currentController;
                    Object[] dialogResults = nVD.display("Invoke Function", flowchart.getVariables(), true, new Object[]{flowchart.getFunctions(), currFI.getFunctionName(), currFI.getParameterVals(), currFI.getVariableForValue()});

                    boolean isValid = (boolean) dialogResults[3];

                    if (isValid) {
                        //set contoller values
                        currFI.setFunctionName((String) dialogResults[0]);
                        currFI.setParameterVals((String) dialogResults[1]);
                        currFI.setVariableForValue((String) dialogResults[2]);

                        //update vertex view label
                        String label = currFI.getVertexLabel();
                        if (label.length() > 16) {
                            label = label.substring(0, 17) + "...";
                        }

                        currFI.getVertex().getView().updateLabel(label);

                    }
                } else if (currentController instanceof ArrayDecController) {
                    ArrayDecController currArrDec = (ArrayDecController) currentController;
                    ArrayList<Var> currVars = flowchart.getVariables();
                    currVars.remove(currArrDec.getVar());
                    Object[] dialogResults = nVD.display("Array Declaration", currVars, true, new Object[]{currArrDec.getType(), currArrDec.getName(), currArrDec.getValues(), currArrDec.isDeclaredByValues()});

                    boolean valuesNotNull = true;
                    int i = 0;

                    //check if given values are null
                    while (valuesNotNull && i < dialogResults.length - 1) {
                        if (dialogResults[i] == null) {
                            valuesNotNull = false;
                        }
                        i++;
                    }

                    //only instantiate vertex if the values are not null
                    if (valuesNotNull) {
                        //set contoller values
                        currArrDec.setType((VarType) dialogResults[0]);
                        currArrDec.setName((String) dialogResults[1]);
                        currArrDec.setValues((String) dialogResults[2]);
                        currArrDec.setLen((String) dialogResults[2]);
                        currArrDec.setDeclaredByValues((Boolean) dialogResults[3]);

                        //update variable in flowchart
                        Var newVar = flowchart.addVar(currArrDec.getType(), currArrDec.getName(), "");
                        currArrDec.setVar(newVar);

                        //update vertex view label
                        String label = currArrDec.getVertexLabel();
                        if (label.length() > 18) {
                            label = label.substring(0, 15) + "...";
                        }
                        currArrDec.getVertex().getView().updateLabel(label);

                        showAlert(Alert.AlertType.INFORMATION, "array created, you can access or modify array elements in expressions using "
                                + currArrDec.getName() + "[~element index~]");
                    }
                }

                updateRSidebar(currentController.getVertex());
            }
        });

        //set up and show primary stage 
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.setTitle("FlowJava");
        imgURL = getClass().getResource("/images/LogoImg.png");
        primaryStage.getIcons().add(new Image(imgURL.openStream()));
        
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaxWidth(bounds.getWidth() * 2);
        
        primaryStage.show();

        //instantiate start vertex
        Ellipse startEllipse = new Ellipse(60.0f, 30.f);
        String startDefStyle = "-fx-fill: lightgreen; -fx-stroke: black; -fx-stroke-width: 2;";
        String startSelStyle = "-fx-fill: lightgreen; -fx-stroke: red; -fx-stroke-width: 2;";
        Vertex startVertex = new Vertex(new Terminal(true, false), startEllipse, startDefStyle, startSelStyle);
        startVertex.getController().setVertex(startVertex);
        setUpNewVertex(startVertex);
        flowchart.addVertex(startVertex);
        flowchart.setStartVertex(startVertex);
        startVertex.getView().setTranslateX(50);
        startVertex.getView().setTranslateY(50);
        startVertex.getView().updateLabel("Start");

        //instantiate stop vertex
        Ellipse stopEllipse = new Ellipse(60.0f, 30.f);
        String stopDefStyle = "-fx-fill: pink; -fx-stroke: black; -fx-stroke-width: 2;";
        String stopSelStyle = "-fx-fill: pink; -fx-stroke: red; -fx-stroke-width: 2;";
        Vertex stopVertex = new Vertex(new Terminal(false, false), stopEllipse, stopDefStyle, stopSelStyle);
        stopVertex.getController().setVertex(stopVertex);
        setUpNewVertex(stopVertex);
        flowchart.addVertex(stopVertex);
        stopVertex.getView().setTranslateX(50);
        stopVertex.getView().setTranslateY(500);
        stopVertex.getView().updateLabel("Stop");

        //add start and stop vertices to canvas
        mainZc.getChildren().addAll(stopVertex.getView(), startVertex.getView());

    }

    /**
     * deselect the currently selected vertex
     */
    private void deselectVertex() {
        VertexView vView = (VertexView) selectedComponent;
        vView.getBackgroundShape().setStyle(vView.getDefaultStyle());
        selectedComponent = null;
    }

    /**
     * deselect the currently selected vertex or edge
     */
    private void deselectComponent() {
        if (selectedComponent instanceof VertexView) {
            deselectVertex();
        } else if (selectedComponent instanceof EdgeView) {
            EdgeView eV = (EdgeView) selectedComponent;
            eV.deselect();
            selectedComponent = null;
        }
        rightSidebarVb.getChildren().clear();
        rightSidebarVb.getChildren().add(defaultRSTxt);

    }

    /**
     * select a given vertex
     *
     * @param v vertex to be selected
     */
    private void selectVertex(Vertex v) {
        //update selected component node
        selectedComponent = v.getView();

        //update the right sidebar
        updateRSidebar(v);
        //update selected vertex style
        v.getView().getBackgroundShape().setStyle(v.getView().getSelectedStyle());
        if (makingConnection) {
            if (chosenParent == null) {
                //initiate connection with clicked node as parent
                chosenParent = v;
            } else if (!chosenParent.equals(v)) {
                //if connection is valid
                if (chosenParent.getController().getMaxChildren() != chosenParent.getChildVertices().size()
                        && v.getController().getMaxParents() != v.getParentVertices().size()) {
                    //create edge
                    Boolean isTrueEdge = null;
                    Boolean isIfConnection = false;
                    if (chosenParent.getController() instanceof IfStmtController) {
                        //prompt user for which edge they want to add for the if statement vertex
                        IfEdgeDialog IED = new IfEdgeDialog();
                        isTrueEdge = IED.display();
                        //if no repsonse from user
                        if (isTrueEdge == null) {
                            makingConnection = false;
                            canvasSp.setCursor(Cursor.DEFAULT);
                            connectionBtn.setStyle("");
                            deselectVertex();
                            showAlert(Alert.AlertType.ERROR, "Add connection cancelled!");
                            v.getView().setOpacity(1);
                            return;

                        } //if user chose true edge and true edge is already set 
                        else if (isTrueEdge && ((IfStmtController) chosenParent.getController()).getTrueEdge() != null) {
                            makingConnection = false;
                            canvasSp.setCursor(Cursor.DEFAULT);
                            connectionBtn.setStyle("");
                            deselectVertex();
                            showAlert(Alert.AlertType.ERROR, "True connection already set!");
                            v.getView().setOpacity(1);
                            return;

                        } //if user chose false edge and false edge is already set
                        else if (!isTrueEdge && ((IfStmtController) chosenParent.getController()).getFalseEdge() != null) {
                            makingConnection = false;
                            canvasSp.setCursor(Cursor.DEFAULT);
                            connectionBtn.setStyle("");
                            deselectVertex();
                            showAlert(Alert.AlertType.ERROR, "False connection already set!");
                            v.getView().setOpacity(1);
                            return;
                        } else {
                            isIfConnection = true;
                        }
                    }
                    //add edge to flowchart
                    Edge newEdge = flowchart.addEdge(chosenParent, v);
                    newEdge.getController().setEdge(newEdge);
                    EdgeView newEdgeView = newEdge.getView();
                    //set edge event handler
                    newEdgeView.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                        //deselect if ctrl is down
                        if (e.isShortcutDown()) {
                            if (e.getSource().equals(selectedComponent)) {
                                deselectComponent();
                                return;
                            }
                        }
                        //deselect previous component
                        deselectComponent();
                        //select this edge
                        selectedComponent = (EdgeView) e.getSource();
                        updateRSidebar(newEdgeView.getEdge());
                        ((EdgeView) e.getSource()).select();
                    });
                    //add edge to canvas
                    mainZc.getChildren().add(newEdgeView);
                    makingConnection = false;
                    canvasSp.setCursor(Cursor.DEFAULT);
                    connectionBtn.setStyle("");

                    //if edge is for if statement, while loop or for loop then update the controllers
                    if (isIfConnection) {
                        if (isTrueEdge) {
                            ((IfStmtController) chosenParent.getController()).setTrueEdge(newEdgeView);
                        } else {
                            ((IfStmtController) chosenParent.getController()).setFalseEdge(newEdgeView);
                        }
                        v.getView().setOpacity(1);
                    } else if (chosenParent.getController() instanceof WhileController) {
                        ((WhileController) chosenParent.getController()).setTrueEdge(newEdgeView);
                    } else if (chosenParent.getController() instanceof ForLoopController) {
                        ((ForLoopController) chosenParent.getController()).setTrueEdge(newEdgeView);
                    }

                    //set edge view start position
                    newEdgeView.setX1(chosenParent.getController().getChildEdgeX(newEdgeView));
                    newEdgeView.setY1(chosenParent.getController().getChildEdgeY(newEdgeView));
                    newEdgeView.setX2(v.getController().getParentEdgeX(newEdgeView));
                    newEdgeView.setY2(v.getController().getParentEdgeY(newEdgeView));

                    newEdgeView.toBack();

                    //deselect previous component
                    deselectComponent();
                    //select this component
                    selectedComponent = newEdgeView;
                    newEdgeView.select();
                    updateRSidebar(newEdgeView.getEdge());
                } else {
                    //cancel connection and show error to user
                    makingConnection = false;
                    canvasSp.setCursor(Cursor.DEFAULT);
                    connectionBtn.setStyle("");
                    deselectVertex();
                    showAlert(Alert.AlertType.ERROR, "Invalid Connection");
                    v.getView().setOpacity(1);
                }
            }
        }
    }

    /**
     * given a Vertex v, give the view of v the required event filters and handlers and move it to the centre of the canvas  
     * 
     * @param v Vertex to set up
     */
    private void setUpNewVertex(Vertex v) {
        VertexView vView = v.getView();
        //set inital vertex view position
        vView.setTranslateX(0);
        vView.setTranslateY(0);
        //add event filters from vertexGestures
        vView.addEventFilter(MouseEvent.MOUSE_PRESSED, vertexGestures.getOnMousePressedEventHandler());
        vView.addEventFilter(MouseEvent.MOUSE_DRAGGED, vertexGestures.getOnMouseDraggedEventHandler());
        vView.addEventFilter(MouseEvent.MOUSE_RELEASED, vertexGestures.getOnMouseReleasedEventHandler());
        //add event handler for mouse pressed
        vView.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            //deselect vertex if ctrl is down
            if (e.isShortcutDown()) {
                if (e.getSource().equals(selectedComponent)) {
                    deselectComponent();
                    return;
                }
            } //edit vertex on double click 
            else if (e.getClickCount() >= 2){
                editVertexBtn.fire();
                vView.setOpacity(1);
            }
            //deselect previous component
            deselectComponent();
            //select this component
            selectVertex(v);
        });
        //move new Vertex to center of scroll pane view port
        vView.setTranslateX((canvasGroup.getBoundsInLocal().getWidth() - canvasSp.getViewportBounds().getWidth())
                * (canvasSp.getHvalue() / canvasSp.getHmax()) - (vView.getBoundsInLocal().getWidth() / 2) + canvasSp.getViewportBounds().getWidth() / 2);
        vView.setTranslateY((canvasGroup.getBoundsInLocal().getHeight() - canvasSp.getViewportBounds().getHeight())
                * (canvasSp.getVvalue() / canvasSp.getVmax()) - (vView.getBoundsInLocal().getHeight() / 2) + canvasSp.getViewportBounds().getHeight() / 2);
    }

    /**
     * set canvas size to 1000 + furthest vertex position in both directions
     */
    private void resetCanvasSize() {
        mainZc.setPrefSize(Math.max(1000, (vertexGestures.getFurthestVertexCoordinates().getKey() + 1000)),
                Math.max(1000, (vertexGestures.getFurthestVertexCoordinates().getValue() + 1000)));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * delete a given vertex
     *
     * @param v vertex to be deleted
     */
    private void deleteVertex(Vertex v) {

        //accumulate the vertex view and its connections views into an arrayList
        //and accumulate the edges from the connections into a list
        ArrayList<Node> removedViews = new ArrayList<>();
        ArrayList<Edge> removedEdges = new ArrayList<>();
        v.getConnections().stream().map((c) -> {
            removedViews.add(c.getKey().getView());
            return c;
        }).forEachOrdered((c) -> {
            removedEdges.add(c.getKey());
        });
        removedViews.add(v.getView());

        VertexController vController = v.getController();
        
        //ensure any relational edges or vertices are also deleted
        if (vController instanceof IfStmtController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex endIfV = ((IfStmtController) vController).getEndIf().getVertex();
            removedVertices.add(endIfV);
            flowchart.removeVertices(removedVertices);
            endIfV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(endIfV.getView());
        } else if (vController instanceof EndIfController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex ifV = ((EndIfController) vController).getIfStmt().getVertex();
            removedVertices.add(ifV);
            flowchart.removeVertices(removedVertices);
            ifV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(ifV.getView());
        } else if (vController instanceof WhileController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex endWhileV = ((WhileController) vController).getEndWhile().getVertex();
            removedVertices.add(endWhileV);
            flowchart.removeVertices(removedVertices);
            endWhileV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(endWhileV.getView());
        } else if (vController instanceof EndWhileController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex whileV = ((EndWhileController) vController).getWhileCtrl().getVertex();
            removedVertices.add(whileV);
            flowchart.removeVertices(removedVertices);
            whileV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(whileV.getView());
        } else if (vController instanceof ForLoopController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex endForV = ((ForLoopController) vController).getEndFor().getVertex();
            removedVertices.add(endForV);
            flowchart.removeVertices(removedVertices);
            endForV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(endForV.getView());
        } else if (vController instanceof EndForController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex forV = ((EndForController) vController).getForCtrl().getVertex();
            removedVertices.add(forV);
            flowchart.removeVertices(removedVertices);
            forV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(forV.getView());
        } else {
            //remove the single vertex from the flow chart
            flowchart.removeVertex(v);
        }

        //remove the views from the canvas
        mainZc.getChildren().removeAll(removedViews);

        //remove the edges from the flowchart
        flowchart.removeEdges(removedEdges);

        //delete corresponding vars from flow chart for variable creating nodes
        if (v.getController() instanceof VarDecController) {
            VarDecController vVarDecController = (VarDecController) v.getController();
            flowchart.removeVar(vVarDecController.getVar());
        }
        if (v.getController() instanceof UserInToVarController) {
            UserInToVarController vInToVarController = (UserInToVarController) v.getController();
            flowchart.removeVar(vInToVarController.getVar());
        }
        if (v.getController() instanceof ArrayDecController) {
            ArrayDecController vArrDecController = (ArrayDecController) v.getController();
            flowchart.removeVar(vArrDecController.getVar());
        }

        updateRSidebar();
    }

    /**
     * update the right sidebar to show the details of a given vertex v
     * 
     * @param v the vertex for to describe on the right side bar
     */
    private void updateRSidebar(Vertex v) {
        //clear the right sidebar
        rightSidebarVb.getChildren().clear();
        
        //create a label for the vertex in a text area
        TextArea vLblTxtArea = new TextArea(v.getController().getVertexLabel());
        vLblTxtArea.setPrefSize(225, 50);
        vLblTxtArea.setEditable(false);
        
        rightSidebarVb.getChildren().add(new Text("Vertex Type:"));

        //add vertex information to right sidebar using the controller
        if (v.getController() instanceof EndIfController) {
            rightSidebarVb.getChildren().addAll(new Text("End If"), new Text("\nMust be structured properly with \nIf Statement - see manual"));
        } else if (v.getController() instanceof EndWhileController) {
            rightSidebarVb.getChildren().addAll(new Text("End While"), new Text("\nMust be structured properly with \nWhile Loop - see manual"));
        } else if (v.getController() instanceof IfStmtController) {
            rightSidebarVb.getChildren().addAll(new Text("If Statement"), new Text("\nMust be structured properly with \nEnd If - see manual"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof OutputController) {
            rightSidebarVb.getChildren().addAll(new Text("Output"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof Terminal) {
            rightSidebarVb.getChildren().addAll(new Text(v.getController().getVertexLabel()));
        } else if (v.getController() instanceof UserInToVarController) {
            rightSidebarVb.getChildren().addAll(new Text("User Input To Variable"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof VarAssignController) {
            rightSidebarVb.getChildren().addAll(new Text("Variable Assignment"), new Text("\nVariables must be declared before\nyou can assign them a new value"), editVertexBtn);
        } else if (v.getController() instanceof VarDecController) {
            rightSidebarVb.getChildren().addAll(new Text("Variable Declaration"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof WhileController) {
            rightSidebarVb.getChildren().addAll(new Text("While Loop"), new Text("\nMust be structured properly with \nEnd While - see manual"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof FunctInvokeController) {
            rightSidebarVb.getChildren().addAll(new Text("Function Invoke"), new Text("\nUsed to explicitly invoke a \nfunction, however functions\ncan also be invoked in any \nexpression"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof ArrayDecController) {
            rightSidebarVb.getChildren().addAll(new Text("Array Variable Declaration"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof ForLoopController) {
            rightSidebarVb.getChildren().addAll(new Text("For Loop"), new Text("\nMust be structured properly with \nFor While - see manual"),
                    new Text("\nEdit Vertex:"), editVertexBtn);
        }

        //add the vertex label to the right sidebar
        rightSidebarVb.getChildren().addAll(new Text("\nVertex Description:"), vLblTxtArea);

        //add information on child/parent requirements to right sidebar
        if (v.getController() instanceof EndIfController || v.getController() instanceof EndWhileController || v.getController() instanceof EndForController) {
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(Integer.toString(v.getController().getMaxParents() - 1)),
                    new Text("\nRequired Children:"), new Text(v.getController().getMaxChildren().toString()));
        } else if (v.getController() instanceof IfStmtController || v.getController() instanceof WhileController || v.getController() instanceof ForLoopController) {
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(v.getController().getMaxParents().toString()),
                    new Text("\nRequired Children:"), new Text(Integer.toString(v.getController().getMaxChildren() - 1)));
        } else {
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(v.getController().getMaxParents().toString()),
                    new Text("\nRequired Children:"), new Text(v.getController().getMaxChildren().toString()));
        }
        
        //create and add a text area for the java equilavent of the vertex to the right sidebar
        TextArea javaDescTxtArea = new TextArea(v.getController().getJavaDescription());
        javaDescTxtArea.setPrefWidth(225);
        javaDescTxtArea.setPrefHeight((new Text(javaDescTxtArea.getText())).getLayoutBounds().getHeight() + 30);
        javaDescTxtArea.setEditable(false);
        rightSidebarVb.getChildren().addAll(new Text("\nJava Equivalent:"), javaDescTxtArea);

    }

    /**
     * update the right sidebar to show only the default text
     */
    private void updateRSidebar() {
        rightSidebarVb.getChildren().clear();
        rightSidebarVb.getChildren().add(defaultRSTxt);
    }

    /**
     * update the right sidebar to show the details of a given edge e
     * 
     * @param e the edge for to describe on the right side bar
     */
    private void updateRSidebar(Edge e) {
        rightSidebarVb.getChildren().clear();
        rightSidebarVb.getChildren().add(new Text("Connection"));
        if (!e.getView().isRemovable()) {
            rightSidebarVb.getChildren().add(new Text("\nNot deletable: shows a relationship \nbetween two nodes"));
        }

    }

    /**
     * write a string to a given file
     * @param content the string to write to the file
     * @param file the file to write the string to
     */
    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter pw;
            pw = new PrintWriter(file);
            pw.println(content);
            pw.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Couldn't save to file");
        }
    }

    /**
     * given an alert type and message, display an alert using these parameters for the type and content text
     * 
     * @param alertType the alert type for the alert to display
     * @param message the content text for the alert to display
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
    }
}
