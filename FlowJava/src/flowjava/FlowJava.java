
package flowjava;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.script.ScriptException;

/**
 * FlowJava is an application currently in development - It is designed to be an
 * application that lets you create basic java programs from flowcharts
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
    
    private ProgramRunner progRunner;
    
    private VBox rightSidebarVb;
    private Text defaultRSTxt;
    
    private Thread runProgThread;
    
    private Button editVertexBtn;
    
    @Override
    public void start(Stage primaryStage) throws ScriptException {
        
        progRunner = new ProgramRunner();
        
        //instantiate flowchart
        flowchart = new Flowchart();
        
        //instantiate root node
        VBox root = new VBox();
        
        //instantiate menus
        MenuBar primarySceneMb = new MenuBar();
        Menu fileMenu = new Menu("File");
        primarySceneMb.getMenus().add(fileMenu);
       
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
        
        //add tools to toolbar
        File toolbarImgFile = new File("images/StartImg.png");
        Image runImg = new Image(toolbarImgFile.toURI().toString());
        ImageView runImgView = new ImageView();
        runImgView.setPreserveRatio(true);
        runImgView.setFitHeight(75);
        runImgView.setImage(runImg);
        
        //add tools to toolbar
        toolbarImgFile = new File("images/ToJavaImg.png");
        Image convertImg = new Image(toolbarImgFile.toURI().toString());
        ImageView convertImgView = new ImageView();
        convertImgView.setPreserveRatio(true);
        convertImgView.setFitHeight(75);
        convertImgView.setImage(convertImg);
        
        runImgView.setOnMouseClicked((MouseEvent e) -> {
            /*Runnable runnable = () -> {
                progRunner.runProgram(flowchart);
                runImgView.setOpacity(1);
                runImgView.setDisable(false);
                convertImgView.setOpacity(1);
                convertImgView.setDisable(false);
            };
            runImgView.setOpacity(0.5);
            runImgView.setDisable(true);
            convertImgView.setOpacity(0.5);
            convertImgView.setDisable(true);
            runProgThread = new Thread(runnable);
            runProgThread.start();*/
            progRunner.runProgram(flowchart);
        });
        
        convertImgView.setOnMouseClicked((MouseEvent e) -> {
            if(progRunner.validateStructure(flowchart)){
                FileChooser fileChooser = new FileChooser();

                //Set extension filter for text files
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java file (*.java)", "*.java");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save newJavaFile dialog
                File newJavaFile = fileChooser.showSaveDialog(primaryStage);

                if (newJavaFile != null) {
                    String javaProgram = progRunner.convertToJava(flowchart, newJavaFile.getName().substring(0,newJavaFile.getName().length()-5));
                    saveTextToFile(javaProgram, newJavaFile);
                    showAlert(Alert.AlertType.INFORMATION, "Program converted to java file");
                }
            } else {
               showAlert(Alert.AlertType.ERROR, "Program structure is invalid");
            }
           
        });
        
        Tooltip.install(runImgView, new Tooltip("Run Program"));
        Tooltip.install(convertImgView, new Tooltip("Convert Program to Java File"));
        
        toolbarHb.getChildren().addAll(runImgView, convertImgView);
        
        //instantiate HBox for the canvas
        HBox canvasHb = new HBox();
        VBox.setVgrow(canvasHb, Priority.ALWAYS);
        
        //instantiate left sidebar
        ScrollPane leftSidebarSp = new ScrollPane();
        leftSidebarSp.setMinWidth(200);
        leftSidebarSp.setMaxWidth(200);
        VBox leftSidebarVb = new VBox();
        
        //instantiate new flowchart component buttons
        connectionBtn = new Button("Connection");
        connectionBtn.setPrefSize(198, 50);
        Button varDeclarationBtn = new Button("Variable Declaration");
        varDeclarationBtn.setPrefSize(198, 50);
        Button userInToVarBtn = new Button("User Input to Variable");
        userInToVarBtn.setPrefSize(198, 50);
        Button outputBtn = new Button("Output");
        outputBtn.setPrefSize(198, 50);
        Button varAssignmentBtn = new Button("Variable Assignment");
        varAssignmentBtn.setPrefSize(198, 50);
        Button ifStmtBtn = new Button("If Statement");
        ifStmtBtn.setPrefSize(198, 50);
        Button whileBtn = new Button("While Loop");
        whileBtn.setPrefSize(198, 50);
        
        //add buttons to left sidebar
        leftSidebarVb.getChildren().addAll(connectionBtn, varDeclarationBtn, userInToVarBtn, outputBtn, varAssignmentBtn, ifStmtBtn, whileBtn);
        
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
            String varDecDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String varDecSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newVarDeclaration = new Vertex(new VarDecController(), parallelogram, varDecDefStyle, varDecSelStyle);
            VarDecController newVarDecController = (VarDecController)newVarDeclaration.getController();
            newVarDecController.setVertex(newVarDeclaration);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Variable Declaration",flowchart.getVariables(), false, null);
            boolean valuesNotNull = true;
            int i = 0;
            
            //check if given values are null
            while(valuesNotNull && i < dialogResults.length-1){
                if(dialogResults[i] == null){
                    valuesNotNull = false;
                }
                i++;
            }
            
            //only instantiate vertex if the values are not null
            if(valuesNotNull){
                //set contoller values
                newVarDecController.setType((VarType)dialogResults[0]);
                newVarDecController.setName((String)dialogResults[1]);
                newVarDecController.setValue((String)dialogResults[2]);
                newVarDecController.setExprHbx((ExpressionHBox)dialogResults[3]);
                newVarDecController.setUsingExpr((boolean)dialogResults[4]);
                
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
                if(label.length() > 18){
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
            String userInDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String userInSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newUserInToVar = new Vertex(new UserInToVarController(), parallelogram, userInDefStyle, userInSelStyle);
            UserInToVarController newUserInToVarController = (UserInToVarController)newUserInToVar.getController();
            newUserInToVarController.setVertex(newUserInToVar);
            
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("User Input to Variable", flowchart.getVariables(), false, null);
            
            boolean valuesNotNull = true;
            int i = 0;
            
            //check if given values are null
            while(valuesNotNull && i < dialogResults.length-1){
                if(dialogResults[i] == null){
                    valuesNotNull = false;
                }
                i++;
            }
            
            //only instantiate vertex if the values are not null
            if(valuesNotNull){
                //set contoller values
                newUserInToVarController.setType((VarType)dialogResults[0]);
                newUserInToVarController.setName((String)dialogResults[1]);
                
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
                if(label.length() > 18){
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
            String outputDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String outputSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newOutput = new Vertex(new OutputController(), parallelogram, outputDefStyle, outputSelStyle);
            OutputController newOutputController = (OutputController)newOutput.getController();
            newOutputController.setVertex(newOutput);
            
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Output", null, false, null);
            
            boolean isValid = (boolean)dialogResults[2];
            
            //only instantiate vertex if the values are not null
            if(isValid){
                //set contoller values
                newOutputController.setValue((String)dialogResults[0]);
                newOutputController.setExprHbx((ExpressionHBox)dialogResults[1]);
                newOutputController.setUsingExpr((boolean)dialogResults[3]);
                
                //set up vertex
                setUpNewVertex(newOutput);
                
                //add vertex view to canvas
                mainZc.getChildren().addAll(newOutput.getView());
                
                //add vertex to flowchart
                flowchart.addVertex(newOutput);
                
                //update vertex view label
                String label = newOutputController.getVertexLabel();
                if(label.length() > 18){
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
            String varAssignDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String varAssignSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newVarAssign = new Vertex(new VarAssignController(), rectangle, varAssignDefStyle, varAssignSelStyle);
            VarAssignController newVarAssignController = (VarAssignController)newVarAssign.getController();
            newVarAssignController.setVertex(newVarAssign);
            
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Variable Assignment", null, false, null);
            
            boolean isValid = (boolean)dialogResults[3];
            
            //only instantiate vertex if the values are not null
            if(isValid){
                //set contoller values
                newVarAssignController.setVarName((String)dialogResults[0]);
                newVarAssignController.setValue((String)dialogResults[1]);
                newVarAssignController.setExprHbx((ExpressionHBox)dialogResults[2]);
                newVarAssignController.setUsingExpr((boolean)dialogResults[4]);
                
                //set up vertex
                setUpNewVertex(newVarAssign);
                
                //add vertex view to canvas
                mainZc.getChildren().addAll(newVarAssign.getView());
                
                //add vertex to flowchart
                flowchart.addVertex(newVarAssign);
                
                //update vertex view label
                String label = newVarAssignController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 19) + "...";
                }
                newVarAssign.getView().updateLabel(label);
                
            }
            
        });
        
        //set event handler for new variable declaration button
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
            String ifStmtDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String ifStmtSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newIfStmt = new Vertex(new IfStmtController(), ifDiamond, ifStmtDefStyle, ifStmtSelStyle, true);
            IfStmtController newIfStmtController = (IfStmtController)newIfStmt.getController();
            newIfStmtController.setVertex(newIfStmt);
            
            Vertex newEndIf = new Vertex(new EndIfController(), endIfRect, ifStmtDefStyle, ifStmtSelStyle);
            EndIfController newEndIfController = (EndIfController)newEndIf.getController();
            newEndIfController.setVertex(newEndIf);
            
            newIfStmtController.setEndIf(newEndIfController);
            newEndIfController.setIfStmt(newIfStmtController);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("If Statement", null, false, null);
            
            boolean isValid = (boolean)dialogResults[2];
            
            //only instantiate vertex if the values are not null
            if(isValid){
                
                //set contoller values
                newIfStmtController.setExpr((String)dialogResults[0]);
                newIfStmtController.setExprHbx((ExpressionHBox)dialogResults[1]);
                
                //set up vertex
                setUpNewVertex(newIfStmt);
                setUpNewVertex(newEndIf);
                
                //add vertex view to canvas
                mainZc.getChildren().addAll(newEndIf.getView(),newIfStmt.getView());
                
                //add vertex to flowchart
                flowchart.addVertex(newIfStmt);
                flowchart.addVertex(newEndIf);
                
                //update vertex view label
                String label = newIfStmtController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 18) + "...";
                }
                newIfStmt.getView().updateLabel(label);
                newEndIf.getView().updateLabel(newEndIfController.getVertexLabel());
                
                newEndIf.getView().setTranslateY(newEndIf.getView().getTranslateY() + 200);
                
                
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
                    newEdgeView.setX2(newEndIf.getView().getTranslateX()+(newEndIf.getView().getWidth()/2));
                    //deselect previous component
                    deselectComponent();
                    //select this component
                    selectedComponent = newEdgeView;
                    newEdgeView.select();
                    
                    newEdgeView.makeSubtle();
                    newEdgeView.setIsDeletable(false);
                    
            }
            
        });
        
        //set event handler for new variable declaration button
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
            String whileDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String whileSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            
            //instantiate Vertex model view and controller
            Vertex newWhile = new Vertex(new WhileController(), whileDiamond, whileDefStyle, whileSelStyle, false);
            WhileController newWhileController = (WhileController)newWhile.getController();
            newWhileController.setVertex(newWhile);
            
            Vertex newEndWhile = new Vertex(new EndWhileController(), endWhileRect, whileDefStyle, whileSelStyle);
            EndWhileController newEndWhileController = (EndWhileController)newEndWhile.getController();
            newEndWhileController.setVertex(newEndWhile);
            
            newWhileController.setEndWhile(newEndWhileController);
            newEndWhileController.setWhileCtrl(newWhileController);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("While Loop", null, false, null);
            
            boolean isValid = (boolean)dialogResults[2];
            
            //only instantiate vertex if the values are not null
            if(isValid){
                
                //set contoller values
                newWhileController.setExpr((String)dialogResults[0]);
                newWhileController.setExprHbx((ExpressionHBox)dialogResults[1]);
                
                //set up vertex
                setUpNewVertex(newWhile);
                setUpNewVertex(newEndWhile);
                
                //add vertex view to canvas
                mainZc.getChildren().addAll(newEndWhile.getView(),newWhile.getView());
                
                //add vertex to flowchart
                flowchart.addVertex(newWhile);
                flowchart.addVertex(newEndWhile);
                
                //update vertex view label
                String label = newWhileController.getVertexLabel();
                if(label.length() > 18){
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
                    newEdgeView.setX2(newEndWhile.getView().getTranslateX()+(newEndWhile.getView().getWidth()/2));
                    //deselect previous component
                    deselectComponent();
                    //select this component
                    selectedComponent = newEdgeView;
                    newEdgeView.select();
                    
                    newEdgeView.makeSubtle();
                    newEdgeView.setIsDeletable(false);
            }
            
        });
        
        
        //set event handler for new connection button
        connectionBtn.setOnAction(e -> {
            resetCanvasSize();
            
            //deselect any currently selected node
            deselectComponent();
            //set chosen parent to null
            chosenParent = null;
            //negate making connection boolean
            makingConnection = !makingConnection;
            
            if(makingConnection){
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
                if(selectedComponent instanceof VertexView){
                    VertexView vView = (VertexView) selectedComponent;
                    if(!(vView.getVertex().getController() instanceof Terminal)){
                        deleteVertex(vView.getVertex());
                    }
                }
                //if an edge is selected, delete it
                if(selectedComponent instanceof EdgeView){
                    if(((EdgeView)selectedComponent).isDeletable()){
                        EdgeView eView = (EdgeView)selectedComponent;
                        mainZc.getChildren().remove(eView);
                        flowchart.removeEdge(eView.getEdge());
                        updateRSidebar();
                    }
                }
            }
        });
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W){
                if(selectedComponent instanceof VertexView){
                    VertexView vView = (VertexView) selectedComponent;
                    for(Pair c: vView.getVertex().getConnections()){
                        if(!(boolean)c.getValue()){
                            deselectComponent();
                            selectedComponent = ((Edge)c.getKey()).getView();
                            updateRSidebar((Edge)c.getKey());
                            ((Edge)c.getKey()).getView().select();
                            e.consume();
                            break;
                        }
                    }
                } else if(selectedComponent instanceof EdgeView){
                    EdgeView eView = (EdgeView)selectedComponent;
                    deselectComponent();
                    selectVertex(eView.getEdge().getController().getParent());
                    e.consume();
                }
            } else if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S){
                if(selectedComponent instanceof VertexView){
                    VertexView vView = (VertexView) selectedComponent;
                    for(Pair c: vView.getVertex().getConnections()){
                        if((boolean)c.getValue()){
                            deselectComponent();
                            selectedComponent = ((Edge)c.getKey()).getView();
                            updateRSidebar((Edge)c.getKey());
                            ((Edge)c.getKey()).getView().select();
                            e.consume();
                            break;
                        }
                    }
                } else if(selectedComponent instanceof EdgeView){
                    EdgeView eView = (EdgeView)selectedComponent;
                    deselectComponent();
                    selectVertex(eView.getEdge().getController().getChild());
                    e.consume();
                }
            }
        });
        
        editVertexBtn = new Button("Edit");
        editVertexBtn.setOnAction(e -> {
            CreateVertexDialog nVD = new CreateVertexDialog();
            VertexController currentController = ((VertexView)selectedComponent).getVertex().getController();
            if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                Object[] dialogResults = nVD.display("If Statement", null, true, new Object[]{currIfStmt.getExpr(), currIfStmt.getExprHbx()});
                
                boolean isValid = (boolean)dialogResults[2];
            
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
                
            } else if (currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
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
            } else if (currentController instanceof OutputController){
                OutputController currOutput = (OutputController)currentController;
                Object[] dialogResults = nVD.display("Output", null, true, new Object[]{currOutput.getValue(), currOutput.getExprHbx()});
                boolean isValid = (boolean) dialogResults[2];

                //only instantiate vertex if the values are not null
                if (isValid) {
                    //set contoller values
                    currOutput.setValue((String) dialogResults[0]);
                    currOutput.setExprHbx((ExpressionHBox) dialogResults[1]);
                    currOutput.setUsingExpr((boolean) dialogResults[3]);

                    //update vertex view label
                    String label = currOutput.getVertexLabel();
                    if (label.length() > 18) {
                        label = label.substring(0, 15) + "...";
                    }
                    currOutput.getVertex().getView().updateLabel(label);
                    updateRSidebar();
                }

            } else if (currentController instanceof UserInToVarController){
                UserInToVarController currUserInToVar = (UserInToVarController)currentController;
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

            } else if (currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController) currentController;
                Object[] dialogResults = nVD.display("Variable Assignment", null, true,  new Object[]{currVarAssign.getVarName(), currVarAssign.getValue(), currVarAssign.getExprHbx()});

                boolean isValid = (boolean) dialogResults[3];

                //only instantiate vertex if the values are not null
                if (isValid) {
                    //set contoller values
                    currVarAssign.setVarName((String) dialogResults[0]);
                    currVarAssign.setValue((String) dialogResults[1]);
                    currVarAssign.setExprHbx((ExpressionHBox) dialogResults[2]);
                    currVarAssign.setUsingExpr((boolean) dialogResults[4]);

                    //update vertex view label
                    String label = currVarAssign.getVertexLabel();
                    if (label.length() > 18) {
                        label = label.substring(0, 19) + "...";
                    }
                    currVarAssign.getVertex().getView().updateLabel(label);

                }
            
            }else if (currentController instanceof VarDecController){
                VarDecController currVarDec = (VarDecController) currentController;
                ArrayList<Var> currVars = flowchart.getVariables();
                currVars.remove(currVarDec.getVar());
                Object[] dialogResults = nVD.display("Variable Declaration", currVars, true, new Object[]{currVarDec.getType(), currVarDec.getName(), currVarDec.getValue(), currVarDec.getExprHbx()});

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
                    currVarDec.setValue((String) dialogResults[2]);
                    currVarDec.setExprHbx((ExpressionHBox) dialogResults[3]);
                    currVarDec.setUsingExpr((boolean) dialogResults[4]);

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
            }
            
            
                
            
            
        });
        
        //set up and show primary stage 
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.show();
        
        //instantiate start vertex
        Ellipse startEllipse = new Ellipse(60.0f, 30.f);
        String startDefStyle = "-fx-fill: lightgreen; -fx-stroke: black; -fx-stroke-width: 2;";
        String startSelStyle = "-fx-fill: lightgreen; -fx-stroke: red; -fx-stroke-width: 2;";
        Vertex startVertex = new Vertex(new Terminal(true), startEllipse, startDefStyle, startSelStyle);
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
        Vertex stopVertex = new Vertex(new Terminal(false), stopEllipse, stopDefStyle, stopSelStyle);
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
    private void selectVertex(Vertex v){
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
                canvasSp.setCursor(Cursor.CROSSHAIR);
            } else if (!chosenParent.equals(v)) {
                if(chosenParent.getController().getMaxChildren()!=chosenParent.getChildVertices().size() &&
                        v.getController().getMaxParents() != v.getParentVertices().size()){
                    //create edge
                    Boolean isTrueEdge = null;
                    Boolean isIfConnection = false;
                    if(chosenParent.getController() instanceof IfStmtController){
                        IfEdgeDialog IED = new IfEdgeDialog();
                        isTrueEdge = IED.display();
                        if(isTrueEdge == null){
                            makingConnection = false;
                            canvasSp.setCursor(Cursor.DEFAULT);
                            connectionBtn.setStyle("");
                            deselectVertex();
                            showAlert(Alert.AlertType.ERROR, "Add connection cancelled!");
                            v.getView().setOpacity(1);
                            return;
                        } else if(isTrueEdge && ((IfStmtController)chosenParent.getController()).getTrueEdge() != null) {
                            makingConnection = false;
                            canvasSp.setCursor(Cursor.DEFAULT);
                            connectionBtn.setStyle("");
                            deselectVertex();
                            showAlert(Alert.AlertType.ERROR, "True connection already set!");
                            v.getView().setOpacity(1);
                            return;
                        } else if (!isTrueEdge && ((IfStmtController)chosenParent.getController()).getFalseEdge() != null) {
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
                        selectedComponent = (EdgeView)e.getSource();
                        updateRSidebar(newEdgeView.getEdge());
                        ((EdgeView)e.getSource()).select();
                    });
                    //add edge to canvas
                    mainZc.getChildren().add(newEdgeView);
                    makingConnection = false;
                    canvasSp.setCursor(Cursor.DEFAULT);
                    connectionBtn.setStyle("");
                    
                    if(isIfConnection){
                        if(isTrueEdge){
                            ((IfStmtController)chosenParent.getController()).setTrueEdge(newEdgeView);
                        } else {
                            ((IfStmtController)chosenParent.getController()).setFalseEdge(newEdgeView);
                        }   
                        v.getView().setOpacity(1);
                    } else if(chosenParent.getController() instanceof WhileController) {
                        ((WhileController)chosenParent.getController()).setTrueEdge(newEdgeView);
                    }
                    
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
    
    private void setUpNewVertex(Vertex v){
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
            }
            //deselect previous component
            deselectComponent();
            //select this component
            selectVertex(v);
        });
        //move new Vertex to center of scroll pane view port
        vView.setTranslateX((canvasGroup.getBoundsInLocal().getWidth() - canvasSp.getViewportBounds().getWidth()) * 
                (canvasSp.getHvalue() / canvasSp.getHmax()) - (vView.getBoundsInLocal().getWidth()/2) + canvasSp.getViewportBounds().getWidth() / 2);
        vView.setTranslateY((canvasGroup.getBoundsInLocal().getHeight() - canvasSp.getViewportBounds().getHeight()) * 
                (canvasSp.getVvalue() / canvasSp.getVmax()) - (vView.getBoundsInLocal().getHeight()/2) + canvasSp.getViewportBounds().getHeight() / 2);
    }
    
    /**
     * set canvas size to 1000 + furthest vertex position in both directions
     */
    private void resetCanvasSize(){
        mainZc.setPrefSize(Math.max(1000,(vertexGestures.getFurthestVertexCoordinates().getKey() + 1000)), 
                Math.max(1000,(vertexGestures.getFurthestVertexCoordinates().getValue() + 1000)));
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
        //if the vertex controller is an if statement or EndIf, ensure the correlating vertices and edges are also deleted
        if(vController instanceof IfStmtController) {
            ArrayList<Vertex> removedVertices = new ArrayList<>();
            removedVertices.add(v);
            Vertex endIfV = ((IfStmtController)vController).getEndIf().getVertex();
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
            Vertex ifV = ((EndIfController)vController).getIfStmt().getVertex();
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
            Vertex endWhileV = ((WhileController)vController).getEndWhile().getVertex();
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
            Vertex whileV = ((EndWhileController)vController).getWhileCtrl().getVertex();
            removedVertices.add(whileV);
            flowchart.removeVertices(removedVertices);
            whileV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(whileV.getView());
        } else {
            //remove the vertex from the flow chart
            flowchart.removeVertex(v);
        }
        
        //remove the views from the canvas
        mainZc.getChildren().removeAll(removedViews);
        
        //remove the edges from the flowchart
        flowchart.removeEdges(removedEdges);
        
        //delete corresponding vars from flow chart for variable creating nodes
        if(v.getController() instanceof VarDecController){
            VarDecController vVarDecController = (VarDecController) v.getController();
            flowchart.removeVar(vVarDecController.getVar());
        }
        if(v.getController() instanceof UserInToVarController) {
            UserInToVarController vInToVarController = (UserInToVarController) v.getController();
            flowchart.removeVar(vInToVarController.getVar());
        }
        
        updateRSidebar();
    }
    
    /**
     * delete a given edge
     * @param e edge to be deleted
     */
    private void deleteEdge(Edge e) {
        //remove edges view from the canvas
        mainZc.getChildren().remove(e.getView());
        
        //remove edge from flowchart
        flowchart.removeEdge(e);
        updateRSidebar();
    }
    
    private void updateRSidebar(Vertex v){
        rightSidebarVb.getChildren().clear();
        TextArea vLblTxtArea = new TextArea(v.getController().getVertexLabel());
        vLblTxtArea.setPrefSize(225, 50);
        vLblTxtArea.setEditable(false);
        rightSidebarVb.getChildren().add(new Text("Vertex Type:"));
        
        if(v.getController() instanceof EndIfController){
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
        }
        
        rightSidebarVb.getChildren().addAll(new Text("\nVertex Description:"), vLblTxtArea);
        
        if(v.getController() instanceof EndIfController || v.getController() instanceof EndWhileController){
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(Integer.toString(v.getController().getMaxParents() - 1)), 
                new Text("\nRequired Children:"), new Text(v.getController().getMaxChildren().toString()));
        } else if(v.getController() instanceof IfStmtController || v.getController() instanceof WhileController) {
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(v.getController().getMaxParents().toString()), 
                new Text("\nRequired Children:"), new Text(Integer.toString(v.getController().getMaxChildren() - 1)));
        } else {
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(v.getController().getMaxParents().toString()), 
                new Text("\nRequired Children:"), new Text(v.getController().getMaxChildren().toString()));
        }
        TextArea javaDescTxtArea = new TextArea(v.getController().getJavaDescription());
        javaDescTxtArea.setPrefWidth(225);
        javaDescTxtArea.setPrefHeight((new Text(javaDescTxtArea.getText())).getLayoutBounds().getHeight() + 30);
        javaDescTxtArea.setEditable(false);
        rightSidebarVb.getChildren().addAll(new Text("\nJava Equivalent:"), javaDescTxtArea);
        
    }
    
    private void updateRSidebar(){
        rightSidebarVb.getChildren().clear();
        rightSidebarVb.getChildren().add(defaultRSTxt);
    }
    
    private void updateRSidebar(Edge e){
        rightSidebarVb.getChildren().clear();
        rightSidebarVb.getChildren().add(new Text("Connection"));
        
        if(!e.getView().isDeletable()){
            rightSidebarVb.getChildren().add(new Text("\nNot deletable: shows a relationship \nbetween two nodes"));
        }
        
    }
    
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
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
        
    }
}
