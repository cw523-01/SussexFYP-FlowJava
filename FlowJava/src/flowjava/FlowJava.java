
package flowjava;

import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
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
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
        VBox rightSidebarVb = new VBox();
        
        //instantiate toolbar
        VBox toolbarContainerVb = new VBox();
        toolbarContainerVb.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        HBox.setHgrow(toolbarContainerVb, Priority.ALWAYS);
        HBox toolbarHb = new HBox();
        toolbarHb.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        toolbarHb.setMinHeight(100);
        
        //add tools to toolbar
        File file = new File("images/StartImg.png");
        Image image = new Image(file.toURI().toString());
        ImageView runImgView = new ImageView();
        runImgView.setPreserveRatio(true);
        runImgView.setFitHeight(75);
        runImgView.setImage(image);
        runImgView.setOnMouseClicked((MouseEvent e) -> {
            progRunner.runProgram(flowchart);
        });
        Tooltip.install(runImgView, new Tooltip("Run Program"));
        toolbarHb.getChildren().addAll(runImgView);
        
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
        
        //add buttons to left sidebar
        leftSidebarVb.getChildren().addAll(connectionBtn, varDeclarationBtn, userInToVarBtn, outputBtn, varAssignmentBtn);
        
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
            VarDecDialog vDD = new VarDecDialog();
            Object[] dialogResults = vDD.display(flowchart.getVariables());
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
                newVarDeclaration.getView().updateLabel(newVarDecController.getVertexLabel());
                
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
            UserInVertexDialog uID = new UserInVertexDialog();
            Object[] dialogResults = uID.display(flowchart.getVariables());
            
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
                newUserInToVar.getView().updateLabel(newUserInToVar.getController().getVertexLabel());
                
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
            OutputVertexDialog oVD = new OutputVertexDialog();
            Object[] dialogResults = oVD.display();
            
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
                newOutput.getView().updateLabel(newOutput.getController().getVertexLabel());
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
            VarAssignDialog vAD = new VarAssignDialog();
            Object[] dialogResults = vAD.display();
            
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
                newVarAssign.getView().updateLabel(newVarAssign.getController().getVertexLabel());
                
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
                    EdgeView eView = (EdgeView)selectedComponent;
                    mainZc.getChildren().remove(eView);
                    flowchart.removeEdge(eView.getEdge());
                }
            }
        });
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if(e.getCode() == KeyCode.UP){
                if(selectedComponent instanceof VertexView){
                    VertexView vView = (VertexView) selectedComponent;
                    for(Pair c: vView.getVertex().getConnections()){
                        if(!(boolean)c.getValue()){
                            deselectComponent();
                            selectedComponent = ((Edge)c.getKey()).getView();
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
            } else if(e.getCode() == KeyCode.DOWN){
                if(selectedComponent instanceof VertexView){
                    VertexView vView = (VertexView) selectedComponent;
                    for(Pair c: vView.getVertex().getConnections()){
                        if((boolean)c.getValue()){
                            deselectComponent();
                            selectedComponent = ((Edge)c.getKey()).getView();
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
    }
    
    /**
     * select a given vertex
     * 
     * @param v vertex to be selected
     */
    private void selectVertex(Vertex v){
        //update selected component node
        selectedComponent = v.getView();
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
                    });
                    //add edge to canvas
                    mainZc.getChildren().add(newEdgeView);
                    makingConnection = false;
                    canvasSp.setCursor(Cursor.DEFAULT);
                    connectionBtn.setStyle("");
                    newEdgeView.toBack();
                    newEdgeView.setX2(v.getView().getTranslateX()+(v.getView().getWidth()/2));
                    //deselect previous component
                    deselectComponent();
                    //select this component
                    selectedComponent = newEdgeView;
                    newEdgeView.select();
                } else {
                    makingConnection = false;
                    canvasSp.setCursor(Cursor.DEFAULT);
                    connectionBtn.setStyle("");
                    deselectVertex();
                    showAlert(Alert.AlertType.ERROR, "Invalid Connection");
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
        //remove the vertex from the flow chart
        flowchart.removeVertex(v);
        
        /*accumulate the vertex view and its connections views into an arrayList
         *and accumulate the edges from the connections into a list
         */
        ArrayList<Node> removedViews = new ArrayList<>();
        ArrayList<Edge> removedEdges = new ArrayList<>();
        v.getConnections().stream().map((c) -> {
            removedViews.add(c.getKey().getView());
            return c;
        }).forEachOrdered((c) -> {
            removedEdges.add(c.getKey());
        });
        removedViews.add(v.getView());
        
        //remove the views from the canvas
        mainZc.getChildren().removeAll(removedViews);
        
        //remove the edges from the flowchart
        flowchart.removeEdges(removedEdges);
        
        if(v.getController() instanceof VarDecController){
            VarDecController vController = (VarDecController) v.getController();
            flowchart.removeVar(vController.getVar());
        }
        if(v.getController() instanceof UserInToVarController) {
            UserInToVarController vController = (UserInToVarController) v.getController();
            flowchart.removeVar(vController.getVar());
        }
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
    }
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
    }
    
}
