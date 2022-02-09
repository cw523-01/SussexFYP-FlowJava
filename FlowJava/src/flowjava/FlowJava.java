/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class FlowJava extends Application {
    
    private Boolean makingConnection = false;
    private Node selectedComponent = null;
    private VertexView chosenParent = null;
    private FlowChart flowChart;
    private ZoomableCanvas mainZc;
    private VertexViewGestures vertexViewGestures;
    private Button connectionBtn;
    private ScrollPane canvasSp;
    
    @Override
    public void start(Stage primaryStage) {
        
        flowChart = new FlowChart();
        
        VBox root = new VBox();
        MenuBar primarySceneMb = new MenuBar();
        Menu fileMenu = new Menu("File");
        primarySceneMb.getMenus().add(fileMenu);
        
        HBox mainControlsHb = new HBox();
        mainControlsHb.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        mainControlsHb.setPrefSize(100, 500);
        VBox.setVgrow(mainControlsHb, Priority.ALWAYS);
        
        ScrollPane rightSidebarSp = new ScrollPane();
        rightSidebarSp.setMinWidth(250);
        VBox rightSidebarVb = new VBox();
        
        VBox toolbarContainerVb = new VBox();
        toolbarContainerVb.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        HBox.setHgrow(toolbarContainerVb, Priority.ALWAYS);
        
        HBox toolbarHb = new HBox();
        toolbarHb.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        toolbarHb.setMinHeight(100);
        
        HBox canvasHb = new HBox();
        ScrollPane leftSidebarSp = new ScrollPane();
        leftSidebarSp.setMinWidth(200);
        leftSidebarSp.setMaxWidth(200);
        VBox leftSidebarVb = new VBox();
        VBox.setVgrow(canvasHb, Priority.ALWAYS);
        
        Button varDeclarationBtn = new Button("Variable Declaration");
        varDeclarationBtn.setPrefSize(198, 50);
        connectionBtn = new Button("Connection");
        connectionBtn.setPrefSize(198, 50);
        leftSidebarVb.getChildren().addAll(connectionBtn,varDeclarationBtn);
        
        canvasSp = new ScrollPane();
        HBox.setHgrow(canvasSp, Priority.ALWAYS);
        canvasSp.setStyle("-fx-background: lightgrey");
        
        mainZc = new ZoomableCanvas();
        
        Group canvasGroup = new Group();
        canvasGroup.getChildren().add(mainZc);
        canvasSp.setContent(canvasGroup);
        canvasSp.setPannable(true);
        
        HBox footerHb = new HBox();
        footerHb.setPrefHeight(25);
        
        leftSidebarSp.setContent(leftSidebarVb);
        canvasHb.getChildren().addAll(leftSidebarSp, canvasSp);
        toolbarContainerVb.getChildren().addAll(toolbarHb, canvasHb);
        rightSidebarSp.setContent(rightSidebarVb);
        mainControlsHb.getChildren().addAll(toolbarContainerVb, rightSidebarSp);
        root.getChildren().addAll(primarySceneMb, mainControlsHb, footerHb);
        
        Scene scene = new Scene(root, 1350, 800);
        scene.addEventFilter(ScrollEvent.ANY, mainZc.getOnScrollEventHandler());
        
        vertexViewGestures = new VertexViewGestures(mainZc, flowChart.getVertices());
        
        varDeclarationBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();
            
            Vertex newVarDeclaration = new Vertex(new VarDeclaration());
            VarDecDialog vDD = new VarDecDialog();
            vDD.display();
            setUpNewVertice(newVarDeclaration);
            
            mainZc.getChildren().addAll(newVarDeclaration.getView());
            flowChart.addVertice(newVarDeclaration);
        });
        
        connectionBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();
            
            deselectComponent();
            chosenParent = null;
            makingConnection = !makingConnection;
            if(makingConnection){
                connectionBtn.setStyle("-fx-background-color: lightgrey;");
            }
            else{
                connectionBtn.setStyle("");
                canvasSp.setCursor(Cursor.DEFAULT);
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.show();
        
        
        
    }

    private void deselectVertice() {
        VertexView v = (VertexView) selectedComponent;
        v.setStyle(v.getVertex().getController().getDefaultStyle());
        selectedComponent = null;
    }
    
    private void deselectComponent() {
        if (selectedComponent instanceof VertexView) {
            deselectVertice();
        } else if (selectedComponent instanceof EdgeView) {
            EdgeView eV = (EdgeView) selectedComponent;
            eV.deselect();
            selectedComponent = null;
        }
    }
    
    private void selectVertice(VertexView vView){
        selectedComponent = vView;
        vView.setStyle(vView.getVertex().getController().getSelectedStyle());
        if (makingConnection) {
            if (chosenParent == null) {
                //initiate connection with clicked node as parent
                chosenParent = vView;
                canvasSp.setCursor(Cursor.CROSSHAIR);
            } else if (!chosenParent.equals(vView)) {
                //make connection
                Edge newEdge = flowChart.addEdge(chosenParent, v);
                newEdge.getView().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (e.isShortcutDown()) {
                            if (e.getSource().equals(selectedComponent)) {
                                deselectComponent();
                                return;
                            }
                        }
                        //deselect previous component
                        deselectComponent();
                        //select this component
                        selectedComponent = newEdge.getView();
                        newEdge.getView().select();
                    }
                });
                mainZc.getChildren().add(newEdge.getView());
                makingConnection = false;
                canvasSp.setCursor(Cursor.DEFAULT);
                connectionBtn.setStyle("");
                deselectVertice();
            }
        }
    }
    
    private void setUpNewVertice(Vertice v){
        v.setTranslateX(0);
        v.setTranslateY(0);
        v.addEventFilter(MouseEvent.MOUSE_PRESSED, vertexViewGestures.getOnMousePressedEventHandler());
        v.addEventFilter(MouseEvent.MOUSE_DRAGGED, vertexViewGestures.getOnMouseDraggedEventHandler());
        v.addEventFilter(MouseEvent.MOUSE_RELEASED, vertexViewGestures.getOnMouseReleasedEventHandler());

        v.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.isShortcutDown()) {
                    if (e.getSource().equals(selectedComponent)) {
                        deselectComponent();
                        return;
                    }
                }
                //deselect previous component
                deselectComponent();
                //select this component
                selectVertice(v);
            }
        });
    }
    
    private void resetCanvasSize(){
        mainZc.setPrefSize(Math.max(1000,(vertexViewGestures.getFurthestVerticeCoordinates().getKey() + 1000)), 
                Math.max(1000,(vertexViewGestures.getFurthestVerticeCoordinates().getValue() + 1000)));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
