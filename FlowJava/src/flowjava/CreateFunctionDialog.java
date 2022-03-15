/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author cwood
 */
public class CreateFunctionDialog {
    //boolean for whether the user is currently making a connection between two nodes
    private Boolean makingConnection = false;
    //stores which ever node the user currently has selected 
    private Node selectedComponent = null;
    //vertex chosen as a parent for a new connection
    private Vertex chosenParent = null;
    
    private FunctionFlowchart fFlowchart;
     //canvas that the flowchart is built on
    private ZoomableCanvas dialogZc;
    //event handlers for vertex views
    private VertexViewGestures vertexGestures;
    //button for initiating a new connection between vertices
    private Button connectionBtn;
    //scrollpane for the main vanvas
    private ScrollPane canvasSp;
    //group for the canvas
    private Group canvasGroup;
    
    private VBox rightSidebarVb;
    private Text defaultRSTxt;
    
    private Button editVertexBtn;
    
    private Button saveBtn;
    private Button cancelBtn;
    
    private HBox paramHBox;
    
    private Stage stage = new Stage();
    
    private Button addParamBtn;
    
    private TextField nameTxtFld = new TextField();
    private ComboBox<String> typeCmbx = new ComboBox<>();
    private TextField returnTxtFld = new TextField();
    
    private CreateParameterDialog cPD = new CreateParameterDialog();
    
    private HBox headersHbx;
    
    private Boolean isValid;
    

    public FunctionFlowchart display(ArrayList<FunctionFlowchart> otherFunctions, FunctionFlowchart functFlowchart) {
        
        ArrayList<String> functionNames = new ArrayList<>();
        for(FunctionFlowchart fF: otherFunctions){
            functionNames.add(fF.getName());
        }
        
        nameTxtFld.setPromptText("Function Name");
        typeCmbx.setPromptText("Function Type");
        returnTxtFld.setPromptText("Return Value/Expression");
        
        stage.initModality(Modality.APPLICATION_MODAL);
        VBox root = new VBox();
        
        for(VarType v: FXCollections.observableArrayList(VarType.values())){
            typeCmbx.getItems().add(v.toString());
        }
        
        typeCmbx.getItems().add("VOID");
        
        HBox returnHbx = new HBox();
        returnHbx.setSpacing(10);
        returnHbx.setAlignment(Pos.CENTER_LEFT);
        returnHbx.getChildren().addAll(new Text("Return: "), returnTxtFld);
        
        headersHbx = new HBox();
        headersHbx.setPadding(new Insets(0,5,0,5));
        headersHbx.setSpacing(10);
        headersHbx.setAlignment(Pos.CENTER_LEFT);
        headersHbx.getChildren().addAll(new Text("Function Type: "), typeCmbx, new Text("Function Name: "), nameTxtFld, returnHbx);
        headersHbx.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        
        typeCmbx.setOnAction(e -> {
            returnHbx.getChildren().clear();
            switch (typeCmbx.getValue()) {
                case "VOID":
                    break;
                default:
                    returnHbx.getChildren().addAll(new Text("Return: "), returnTxtFld);
                    break;
            }
        });
        
        saveBtn = new Button("Save");
        
        cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction((ActionEvent e) -> {
            isValid = false;
            fFlowchart = null;
            stage.close();
        });
        
        HBox footersHbx = new HBox();
        footersHbx.setPadding(new Insets(0,5,0,5));
        footersHbx.setSpacing(10);
        footersHbx.setAlignment(Pos.CENTER_RIGHT);
        footersHbx.getChildren().addAll(saveBtn, cancelBtn);
        footersHbx.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        
        addParamBtn = new Button("Add\nParameter");
        addParamBtn.setTextAlignment(TextAlignment.CENTER);
        addParamBtn.setPrefSize(90, 75);
        
        addParamBtn.setOnAction((ActionEvent e) -> {
            Object[] results = cPD.display(fFlowchart.getVariables(), false, null, null);
            if(results != null){
                Var param = new Var((VarType)results[0],(String)results[1],null);
                if(fFlowchart.addParameter(param)){
                    updateParamHBox();
                    fFlowchart.getVariables().add(param);
                }
            }
        });
        
        paramHBox = new HBox();
        paramHBox.getChildren().add(addParamBtn);
        paramHBox.setSpacing(5);
        
        VBox paramVBox = new VBox();
        paramVBox.getChildren().addAll(new Text("Parameters:"), paramHBox);
        
        ScrollPane paramSp = new ScrollPane();
        paramSp.setContent(paramVBox);
        paramSp.setStyle("-fx-border-style: solid inside;"
        + "-fx-border-width: 2;" + "-fx-border-color: lightgrey;");
        paramSp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        paramSp.setPrefHeight(118);
        paramSp.setMinHeight(118);
        
        //instantiate right sidebar
        ScrollPane rightSidebarSp = new ScrollPane();
        rightSidebarSp.setMinWidth(250);
        rightSidebarVb = new VBox();
        rightSidebarVb.setPadding(new Insets(10, 10, 10, 10));
        defaultRSTxt = new Text("-select a component-");
        rightSidebarVb.getChildren().add(defaultRSTxt);
        rightSidebarSp.setContent(rightSidebarVb);
        
        //instantiate canvas scrollpane
        canvasSp = new ScrollPane();
        HBox.setHgrow(canvasSp, Priority.ALWAYS);
        canvasSp.setStyle("-fx-background: lightgrey");
        
        //instantiate canvas
        dialogZc = new ZoomableCanvas();
        
        //instantiate canvas group and add it to canvas scroll pane
        canvasGroup = new Group();
        canvasGroup.getChildren().add(dialogZc);
        canvasSp.setContent(canvasGroup);
        canvasSp.setPannable(true);
        
        //instantiate left sidebar
        ScrollPane leftSidebarSp = new ScrollPane();
        leftSidebarSp.setMinWidth(200);
        leftSidebarSp.setMaxWidth(200);
        VBox leftSidebarVb = new VBox();
        
        HBox canvasHb = new HBox();
        leftSidebarSp.setContent(leftSidebarVb);
        canvasHb.getChildren().addAll(leftSidebarSp, canvasSp);
        HBox mainControlsHb = new HBox();
        VBox paramContainerVb = new VBox();
        paramContainerVb.getChildren().addAll(paramSp, canvasHb);
        mainControlsHb.getChildren().addAll(paramContainerVb, rightSidebarSp);
        root.getChildren().addAll(headersHbx, mainControlsHb, footersHbx);
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 200);         
        stage.setMinWidth(1000);
        stage.setMinHeight(850);
        stage.setTitle("Function Editor");
        stage.setScene(scene);
        
        scene.addEventFilter(ScrollEvent.ANY, dialogZc.getOnScrollEventHandler());
        
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
                        dialogZc.getChildren().remove(eView);
                        fFlowchart.removeEdge(eView.getEdge());
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
            VertexController currentController = ((VertexView) selectedComponent).getVertex().getController();
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
                ForLoopController currFor = (ForLoopController)currentController;
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

            } else if (currentController instanceof UserInToVarController) {
                UserInToVarController currUserInToVar = (UserInToVarController) currentController;
                ArrayList<Var> currVars = fFlowchart.getVariables();
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

                    fFlowchart.removeVar(currUserInToVar.getVar());

                    //update variable in flowchart
                    Var newVar = fFlowchart.addVar(currUserInToVar.getType(), currUserInToVar.getName(), "");
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
                Object[] dialogResults = nVD.display("Variable Assignment", null, true, new Object[]{currVarAssign.getVarName(), currVarAssign.getValue(), currVarAssign.getExprHbx()});

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

            } else if (currentController instanceof VarDecController) {
                VarDecController currVarDec = (VarDecController) currentController;
                ArrayList<Var> currVars = fFlowchart.getVariables();
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
                    Var newVar = fFlowchart.addVar(currVarDec.getType(), currVarDec.getName(), "");
                    currVarDec.setVar(newVar);

                    //update vertex view label
                    String label = currVarDec.getVertexLabel();
                    if (label.length() > 18) {
                        label = label.substring(0, 15) + "...";
                    }
                    currVarDec.getVertex().getView().updateLabel(label);

                }
            }else if (currentController instanceof RecurseController) {
                RecurseController currRec = (RecurseController) currentController;
                
                Object[] dialogResults = nVD.display("Recurse", fFlowchart.getVariables(), true, new Object[]{currRec.getParameterVals(), currRec.getVariableForValue()});

                //set contoller values
                boolean valid = (boolean)dialogResults[0];
                
                if(valid){
                    currRec.setParameterVals((String) dialogResults[1]);
                    currRec.setVariableForValue((String) dialogResults[2]);

                    //update vertex view label
                    String label = currRec.getVertexLabel();
                    if (label.length() > 16) {
                        label = label.substring(0, 17) + "...";
                    }

                    currRec.getVertex().getView().updateLabel(label);
                }

            } else if (currentController instanceof FunctInvokeController) {
                FunctInvokeController currFI = (FunctInvokeController) currentController;
                
                Object[] dialogResults = nVD.display("Invoke Other Function", fFlowchart.getVariables(), true, new Object[]{otherFunctions, currFI.getFunctionName(), currFI.getParameterVals(), currFI.getVariableForValue()});

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
            } else if (currentController instanceof ArrayDecController){
                ArrayDecController currArrDec = (ArrayDecController) currentController;
                ArrayList<Var> currVars = fFlowchart.getVariables();
                currVars.remove(currArrDec.getVar());
                Object[] dialogResults = nVD.display("Array Declaration", currVars, true, new Object[]{currArrDec.getType(), currArrDec.getName(), currArrDec.getValues()});

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
                    
                    //update variable in flowchart
                    Var newVar = fFlowchart.addVar(currArrDec.getType(), currArrDec.getName(), "");
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

        });
        
        //instantiate new flowchart component buttons
        connectionBtn = new Button("Connection");
        connectionBtn.setPrefSize(198, 50);
        
        
        File imgFile = new File("images/ConnectionImg.png");
        Image connectionImg = new Image(imgFile.toURI().toString());
        ImageView connectionImgView = new ImageView();
        connectionImgView.setPreserveRatio(true);
        connectionImgView.setFitHeight(30);
        connectionImgView.setImage(connectionImg);
        
        connectionBtn.setGraphic(connectionImgView);
        connectionBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
        
        Button varDeclarationBtn = new Button("Variable Declaration");
        varDeclarationBtn.setPrefSize(198, 50);
        
        imgFile = new File("images/IoImg.png");
        Image IoImg = new Image(imgFile.toURI().toString());
        ImageView varDecImgView = new ImageView();
        varDecImgView.setPreserveRatio(true);
        varDecImgView.setFitHeight(30);
        varDecImgView.setImage(IoImg);
        
        varDeclarationBtn.setGraphic(varDecImgView);
        varDeclarationBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
            Object[] dialogResults = nVD.display("Variable Declaration", fFlowchart.getVariables(), false, null);
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
                dialogZc.getChildren().addAll(newVarDeclaration.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newVarDeclaration);
                
                //add variable to flowchart
                Var newVar = fFlowchart.addVar(newVarDecController.getType(), newVarDecController.getName(), "");
                newVarDecController.setVar(newVar);
                        
                //update vertex view label
                String label = newVarDecController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 15) + "...";
                }
                newVarDeclaration.getView().updateLabel(label);
                
            }
        });
        
        Button userInToVarBtn = new Button("User Input to\nVariable");
        userInToVarBtn.setPrefSize(198, 50);
        userInToVarBtn.setTextAlignment(TextAlignment.CENTER);
        
        ImageView userIntoVarImgView = new ImageView();
        userIntoVarImgView.setPreserveRatio(true);
        userIntoVarImgView.setFitHeight(30);
        userIntoVarImgView.setImage(IoImg);
        
        userInToVarBtn.setGraphic(userIntoVarImgView);
        userInToVarBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
            Object[] dialogResults = nVD.display("User Input to Variable", fFlowchart.getVariables(), false, null);
            
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
                dialogZc.getChildren().addAll(newUserInToVar.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newUserInToVar);
                
                //add variable to flowchart
                Var newVar = fFlowchart.addVar(newUserInToVarController.getType(), newUserInToVarController.getName(), "");
                newUserInToVarController.setVar(newVar);
                        
                //update vertex view label
                String label = newUserInToVarController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 15) + "...";
                }
                newUserInToVar.getView().updateLabel(label);
                
            }
            
        });
        
        Button outputBtn = new Button("Output");
        outputBtn.setPrefSize(198, 50);
        
        ImageView outputImgView = new ImageView();
        outputImgView.setPreserveRatio(true);
        outputImgView.setFitHeight(30);
        outputImgView.setImage(IoImg);
        
        outputBtn.setGraphic(outputImgView);
        outputBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
                dialogZc.getChildren().addAll(newOutput.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newOutput);
                
                //update vertex view label
                String label = newOutputController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 15) + "...";
                }
                newOutput.getView().updateLabel(label);
            }
            
        });
        
        Button varAssignmentBtn = new Button("Variable Assignment");
        varAssignmentBtn.setPrefSize(198, 50);
        
        imgFile = new File("images/ProcessImg.png");
        Image processImg = new Image(imgFile.toURI().toString());
        ImageView varAssignImgView = new ImageView();
        varAssignImgView.setPreserveRatio(true);
        varAssignImgView.setFitHeight(30);
        varAssignImgView.setImage(processImg);
        
        varAssignmentBtn.setGraphic(varAssignImgView);
        varAssignmentBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
                dialogZc.getChildren().addAll(newVarAssign.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newVarAssign);
                
                //update vertex view label
                String label = newVarAssignController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 19) + "...";
                }
                newVarAssign.getView().updateLabel(label);
                
            }
            
        });
        
        Button ifStmtBtn = new Button("If Statement");
        ifStmtBtn.setPrefSize(198, 50);
        
        imgFile = new File("images/DecisionImg.png");
        Image decisionImg = new Image(imgFile.toURI().toString());
        ImageView ifImgView = new ImageView();
        ifImgView.setPreserveRatio(true);
        ifImgView.setFitHeight(30);
        ifImgView.setImage(decisionImg);
        
        ifStmtBtn.setGraphic(ifImgView);
        ifStmtBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
                dialogZc.getChildren().addAll(newEndIf.getView(),newIfStmt.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newIfStmt);
                fFlowchart.addVertex(newEndIf);
                
                //update vertex view label
                String label = newIfStmtController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 18) + "...";
                }
                newIfStmt.getView().updateLabel(label);
                newEndIf.getView().updateLabel(newEndIfController.getVertexLabel());
                
                newEndIf.getView().setTranslateY(newEndIf.getView().getTranslateY() + 200);
                
                
                Edge newEdge = fFlowchart.addEdge(newIfStmt, newEndIf);
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
                    dialogZc.getChildren().add(newEdgeView);
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
        
        Button whileBtn = new Button("While Loop");
        whileBtn.setPrefSize(198, 50);
        
        ImageView whileImgView = new ImageView();
        whileImgView.setPreserveRatio(true);
        whileImgView.setFitHeight(30);
        whileImgView.setImage(decisionImg);
        
        whileBtn.setGraphic(whileImgView);
        whileBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
                dialogZc.getChildren().addAll(newEndWhile.getView(),newWhile.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newWhile);
                fFlowchart.addVertex(newEndWhile);
                
                //update vertex view label
                String label = newWhileController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 18) + "...";
                }
                newWhile.getView().updateLabel(label);
                newEndWhile.getView().updateLabel(newEndWhileController.getVertexLabel());
                
                newEndWhile.getView().setTranslateY(newEndWhile.getView().getTranslateY() + 200);
                
                Edge newEdge = fFlowchart.addEdge(newWhile, newEndWhile);
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
                dialogZc.getChildren().add(newEdgeView);
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
                newEdgeView.setIsDeletable(false);
            }

        });
        
        Button functBtn = new Button("Invoke Other\nFunction");
        functBtn.setPrefSize(198, 50);
        functBtn.setTextAlignment(TextAlignment.CENTER);
        
        imgFile = new File("images/InvokeImg.png");
        Image invokeImg = new Image(imgFile.toURI().toString());
        ImageView invokeImgView = new ImageView();
        invokeImgView.setPreserveRatio(true);
        invokeImgView.setFitHeight(30);
        invokeImgView.setImage(invokeImg);
        
        functBtn.setGraphic(invokeImgView);
        functBtn.setContentDisplay(ContentDisplay.RIGHT);
        
        functBtn.setOnAction(e -> {
            
            if (otherFunctions.isEmpty()){
                showAlert(Alert.AlertType.WARNING, "Must create other functions before you can invoke them!");
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
            String functDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String functSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newFunctInvoke = new Vertex(new FunctInvokeController(), barredRectangle, functDefStyle, functSelStyle);
            FunctInvokeController newFunctInvokeController = (FunctInvokeController)newFunctInvoke.getController();
            newFunctInvokeController.setVertex(newFunctInvoke);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            
            Object[] dialogResults = nVD.display("Invoke Function", fFlowchart.getVariables(), false, new Object[] {otherFunctions});
            
            boolean isValid = (boolean)dialogResults[3];
            
            if(isValid){
               //set contoller values
                newFunctInvokeController.setFunctionName((String)dialogResults[0]);
                newFunctInvokeController.setParameterVals((String)dialogResults[1]);
                newFunctInvokeController.setVariableForValue((String)dialogResults[2]);
                
                //set up vertex
                setUpNewVertex(newFunctInvoke);
                
                //add vertex view to canvas
                dialogZc.getChildren().addAll(newFunctInvoke.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newFunctInvoke);
                
                //update vertex view label
                String label = newFunctInvokeController.getVertexLabel();
                if(label.length() > 16){
                    label = label.substring(0, 17) + "...";
                }
                
                newFunctInvoke.getView().updateLabel(label);
                
            }
            
        });
        
        Button recurseBtn = new Button("Recurse");
        recurseBtn.setPrefSize(198, 50);
        
        ImageView recurseImgView = new ImageView();
        recurseImgView.setPreserveRatio(true);
        recurseImgView.setFitHeight(30);
        recurseImgView.setImage(invokeImg);
        
        recurseBtn.setGraphic(recurseImgView);
        recurseBtn.setContentDisplay(ContentDisplay.RIGHT);
        
        recurseBtn.setOnAction(e -> {
            
            resetCanvasSize();
            
            //create parallelogram for vertex view background
            Polygon barredRectangle = new Polygon();
            barredRectangle.getPoints().addAll(0.0, 0.0, 
                                         10.0, 0.0, 10.0, 70.0, 10.0, 0.0,
                                         175.0, 0.0, 175.0, 70.0, 175.0, 0.0,
                                         185.0, 0.0, 
                                         185.00, 70.0, 0.0, 70.0);
            
            //instantiate strings for different view styles
            String functDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String functSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newRecurse = new Vertex(new RecurseController(), barredRectangle, functDefStyle, functSelStyle);
            RecurseController newRecurseController = (RecurseController)newRecurse.getController();
            newRecurseController.setVertex(newRecurse);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            
            Object[] dialogResults = nVD.display("Recurse", fFlowchart.getVariables(), false, null);
            
            //set contoller values
            boolean valid = (boolean) dialogResults[0];

            if (valid) {
                //set contoller values
                newRecurseController.setParameterVals((String) dialogResults[1]);
                newRecurseController.setVariableForValue((String) dialogResults[2]);

                //set up vertex
                setUpNewVertex(newRecurse);

                //add vertex view to canvas
                dialogZc.getChildren().addAll(newRecurse.getView());

                //add vertex to flowchart
                fFlowchart.addVertex(newRecurse);

                //update vertex view label
                String label = newRecurseController.getVertexLabel();
                if (label.length() > 16) {
                    label = label.substring(0, 17) + "...";
                }

                newRecurse.getView().updateLabel(label);

            }

        });
        
        Button arrayDeclarationBtn = new Button("Array Variable\nDeclaration");
        arrayDeclarationBtn.setPrefSize(198, 50);
        arrayDeclarationBtn.setTextAlignment(TextAlignment.CENTER);
        
        ImageView arrVarDecImgView = new ImageView();
        arrVarDecImgView.setPreserveRatio(true);
        arrVarDecImgView.setFitHeight(30);
        arrVarDecImgView.setImage(IoImg);
        
        arrayDeclarationBtn.setGraphic(arrVarDecImgView);
        arrayDeclarationBtn.setContentDisplay(ContentDisplay.RIGHT);
        
        arrayDeclarationBtn.setOnAction((ActionEvent e) -> {
            resetCanvasSize();
            
            //create parallelogram for vertex view background
            Polygon parallelogram = new Polygon();
            parallelogram.getPoints().addAll(30.0, 0.0, 185.0, 0.0, 
                                         155.00, 70.0, 0.0, 70.0);
            
            //instantiate strings for different view styles
            String arrDecDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String arrDecSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            //instantiate Vertex model view and controller
            Vertex newArrDeclaration = new Vertex(new ArrayDecController(), parallelogram, arrDecDefStyle, arrDecSelStyle);
            ArrayDecController newArrDecController = (ArrayDecController)newArrDeclaration.getController();
            newArrDecController.setVertex(newArrDeclaration);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("Array Declaration",fFlowchart.getVariables(), false, null);
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
                newArrDecController.setType((VarType)dialogResults[0]);
                newArrDecController.setName((String)dialogResults[1]);
                newArrDecController.setValues((String)dialogResults[2]);
                
                //set up vertex
                setUpNewVertex(newArrDeclaration);
                
                //add vertex view to canvas
                dialogZc.getChildren().addAll(newArrDeclaration.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newArrDeclaration);
                
                //add variable to flowchart
                Var newVar = fFlowchart.addVar(newArrDecController.getType(), newArrDecController.getName(), "");
                newArrDecController.setVar(newVar);
                        
                //update vertex view label
                String label = newArrDecController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 15) + "...";
                }
                newArrDeclaration.getView().updateLabel(label);
                
                showAlert(Alert.AlertType.INFORMATION, "array created, you can access or modify array elements in expressions using " 
                            + newArrDecController.getName() + "[~element index~]");
            }
        });
        
        Button forBtn = new Button("For Loop");
        forBtn.setPrefSize(198, 50);
        
        ImageView forImgView = new ImageView();
        forImgView.setPreserveRatio(true);
        forImgView.setFitHeight(30);
        forImgView.setImage(decisionImg);
        
        forBtn.setGraphic(forImgView);
        forBtn.setContentDisplay(ContentDisplay.RIGHT);
        
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
            String forDefStyle = "-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;";
            String forSelStyle = "-fx-fill: white; -fx-stroke: red; -fx-stroke-width: 2;";
            
            
            //instantiate Vertex model view and controller
            Vertex newFor = new Vertex(new ForLoopController(), forDiamond, forDefStyle, forSelStyle, false);
            ForLoopController newForController = (ForLoopController)newFor.getController();
            newForController.setVertex(newFor);
            
            Vertex newEndFor = new Vertex(new EndForController(), endForRect, forDefStyle, forSelStyle);
            EndForController newEndForController = (EndForController)newEndFor.getController();
            newEndForController.setVertex(newEndFor);
            
            newForController.setEndFor(newEndForController);
            newEndForController.setForCtrl(newForController);
            
            //open variable declaration fields input form dialog
            CreateVertexDialog nVD = new CreateVertexDialog();
            Object[] dialogResults = nVD.display("For Loop", null, false, null);
            
            boolean isValid = (boolean)dialogResults[3];
            
            //only instantiate vertex if the values are not null
            if(isValid){
                
                //set contoller values
                newForController.setInitialExpr((String)dialogResults[0]);
                newForController.setConditionExpr((String)dialogResults[1]);
                newForController.setUpdateExpr((String)dialogResults[2]);
                
                //set up vertex
                setUpNewVertex(newFor);
                setUpNewVertex(newEndFor);
                
                //add vertex view to canvas
                dialogZc.getChildren().addAll(newEndFor.getView(),newFor.getView());
                
                //add vertex to flowchart
                fFlowchart.addVertex(newFor);
                fFlowchart.addVertex(newEndFor);
                
                //update vertex view label
                String label = newForController.getVertexLabel();
                if(label.length() > 18){
                    label = label.substring(0, 18) + "...";
                }
                newFor.getView().updateLabel(label);
                newEndFor.getView().updateLabel(newEndForController.getVertexLabel());
                
                newEndFor.getView().setTranslateY(newEndFor.getView().getTranslateY() + 200);
                
                Edge newEdge = fFlowchart.addEdge(newFor, newEndFor);
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
                dialogZc.getChildren().add(newEdgeView);
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
                newEdgeView.setIsDeletable(false);
            }

        });
        
        isValid = false;
        
        saveBtn.setOnAction(e -> {
            if(typeCmbx.getValue() == null){
                showAlert(Alert.AlertType.ERROR, "Function type is empty");
                return;
            }
            if(functionNames.contains(nameTxtFld.getText())){
                showAlert(Alert.AlertType.ERROR, "Function name already used");
                return;
            }
            if(nameTxtFld.getText().isEmpty()){
                showAlert(Alert.AlertType.ERROR, "Function name is empty");
                return;
            }
            if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                showAlert(Alert.AlertType.ERROR, "Function name is invalid");
                return;
            }
            if(!typeCmbx.getValue().equals("VOID") && returnTxtFld.getText().isEmpty()){
                showAlert(Alert.AlertType.ERROR, "Return value is empty (use \"null\" or void type to return nothing)");
                return;
            } if(nameTxtFld.getText().equals("main") || nameTxtFld.getText().equals("run") || nameTxtFld.getText().equals("start") ){
                showAlert(Alert.AlertType.ERROR, "User created functions can't be named 'main' 'run' or 'start' in Flowjava");
                return;
            }
            
            fFlowchart.setName(nameTxtFld.getText());
            
            switch (typeCmbx.getValue()){
                case "VOID":
                    fFlowchart.setReturnType(null);
                    break;
                case "STRING":
                    fFlowchart.setReturnType(VarType.STRING);
                    break;
                case "BOOLEAN":
                    fFlowchart.setReturnType(VarType.BOOLEAN);
                    break;
                case "CHARACTER":
                    fFlowchart.setReturnType(VarType.CHARACTER);
                    break;
                case "INTEGER":
                    fFlowchart.setReturnType(VarType.INTEGER);
                    break;
                case "DOUBLE":
                    fFlowchart.setReturnType(VarType.DOUBLE);
                    break;
                case "FLOAT":
                    fFlowchart.setReturnType(VarType.FLOAT);
                    break;
                case "LONG":
                    fFlowchart.setReturnType(VarType.LONG);
                    break;
                case "SHORT":
                    fFlowchart.setReturnType(VarType.SHORT);
                    break;
            }
            
            fFlowchart.setRetunVal(returnTxtFld.getText());
            
            ProgramRunner progRunner = new ProgramRunner();
            try {
                if(progRunner.convertThenCompileProgram(fFlowchart, false, true, otherFunctions)){
                    isValid = true;
                    showAlert(Alert.AlertType.INFORMATION, "Function successfully created, use \n'" + fFlowchart.getName() +"(~parameter values~)' to call the function in an expression or use a function invocation node");
                    stage.close();
                }
            } catch (UserCreatedExprException ex) {
                System.out.println(ex.getCause());
            }
        });
        
        Boolean isNewFF = false;
        
        if(functFlowchart == null){
            fFlowchart = new FunctionFlowchart(null, null, new ArrayList<>());
            isNewFF = true;
        } else {
            fFlowchart = functFlowchart;
            nameTxtFld.setText(fFlowchart.getName());
            if(fFlowchart.getReturnType() == null){
                typeCmbx.setValue("VOID");
            } else {
                typeCmbx.setValue(fFlowchart.getReturnType().toString());
            }
            returnTxtFld.setText(fFlowchart.getRetunVal());
            
            updateParamHBox();
            
        }
        
        //add buttons to left sidebar
        leftSidebarVb.getChildren().addAll(connectionBtn, varDeclarationBtn, arrayDeclarationBtn, userInToVarBtn, outputBtn, varAssignmentBtn, ifStmtBtn, whileBtn, forBtn, functBtn, recurseBtn);
        
        //instantiate vertex gestures 
        vertexGestures = new VertexViewGestures(dialogZc, fFlowchart.getVertices());
        
        if(isNewFF){
            //instantiate start vertex
            Ellipse startEllipse = new Ellipse(60.0f, 30.f);
            String startDefStyle = "-fx-fill: lightgreen; -fx-stroke: black; -fx-stroke-width: 2;";
            String startSelStyle = "-fx-fill: lightgreen; -fx-stroke: red; -fx-stroke-width: 2;";

            Vertex startVertex = new Vertex(new Terminal(true), startEllipse, startDefStyle, startSelStyle);
            startVertex.getController().setVertex(startVertex);
            setUpNewVertex(startVertex);
            fFlowchart.addVertex(startVertex);
            fFlowchart.setStartVertex(startVertex);
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
            fFlowchart.addVertex(stopVertex);
            stopVertex.getView().setTranslateX(50);
            stopVertex.getView().setTranslateY(500);
            stopVertex.getView().updateLabel("Stop");

            //add start and stop vertices to canvas
            dialogZc.getChildren().addAll(startVertex.getView(), stopVertex.getView());
        } else {
            for(Edge e: fFlowchart.getEdges()){
                dialogZc.getChildren().add(e.getView());
                e.getView().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent mE) -> {
                        //deselect if ctrl is down
                        if (mE.isShortcutDown()) {
                            if (mE.getSource().equals(selectedComponent)) {
                                deselectComponent();
                                return;
                            }
                        }
                        //deselect previous component
                        deselectComponent();
                        //select this edge
                        selectedComponent = (EdgeView)mE.getSource();
                        updateRSidebar(e.getView().getEdge());
                        ((EdgeView)mE.getSource()).select();
                });
            }
            
            for(Vertex v: fFlowchart.getVertices()){
                dialogZc.getChildren().add(v.getView());
                setUpVertexViewHandlers(v);
            }
        }
        
        stage.showAndWait();
        
        deselectComponent();
        
        if(isValid){
            return fFlowchart;
        } else {
            return null;
        }
    }
    
    private void updateParamHBox(){
        paramHBox.getChildren().clear();
        for(Var p: fFlowchart.getParameters()){
            VBox parameterVBox = new VBox();
            Text pTypeTxt = new Text("Type: " + p.getType().toString());
            Text pNameTxt = new Text("Name: " + p.getName());
            Button editPBtn = new Button("Edit");
            editPBtn.setOnAction((ActionEvent e) -> {
                Object[] results = cPD.display(fFlowchart.getParameters(), true, p.getType(), p.getName());
                if (results != null) {
                    Var param = new Var((VarType) results[0], (String) results[1], null);
                    fFlowchart.getParameters().remove(p);
                    if (fFlowchart.addParameter(param)) {
                        updateParamHBox();
                    }
                }
                updateParamHBox();
            });
            Button deletePBtn = new Button("Delete");
            deletePBtn.setOnAction((ActionEvent e) -> {
                fFlowchart.getParameters().remove(p);
                fFlowchart.getVariables().remove(p);
                updateParamHBox();
            });
            HBox btnsHbx = new HBox();
            btnsHbx.getChildren().addAll(editPBtn, deletePBtn);
            parameterVBox.getChildren().addAll(pTypeTxt, pNameTxt, btnsHbx);
            paramHBox.getChildren().add(parameterVBox);
        }
        paramHBox.getChildren().add(addParamBtn);
    }
    
    private void setUpNewVertex(Vertex v){
        VertexView vView = v.getView();
        //set inital vertex view position
        vView.setTranslateX(0);
        vView.setTranslateY(0);
        setUpVertexViewHandlers(v);
        //move new Vertex to center of scroll pane view port
        vView.setTranslateX((canvasGroup.getBoundsInLocal().getWidth() - canvasSp.getViewportBounds().getWidth()) * 
                (canvasSp.getHvalue() / canvasSp.getHmax()) - (vView.getBoundsInLocal().getWidth()/2) + canvasSp.getViewportBounds().getWidth() / 2);
        vView.setTranslateY((canvasGroup.getBoundsInLocal().getHeight() - canvasSp.getViewportBounds().getHeight()) * 
                (canvasSp.getVvalue() / canvasSp.getVmax()) - (vView.getBoundsInLocal().getHeight()/2) + canvasSp.getViewportBounds().getHeight() / 2);
    }
    
    private void setUpVertexViewHandlers(Vertex v){
        VertexView vView = v.getView();
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
                    Edge newEdge = fFlowchart.addEdge(chosenParent, v);
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
                    dialogZc.getChildren().add(newEdgeView);
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
                    } else if (chosenParent.getController() instanceof ForLoopController) {
                        ((ForLoopController)chosenParent.getController()).setTrueEdge(newEdgeView);
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
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
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
        } else if (v.getController() instanceof FunctInvokeController) { 
            rightSidebarVb.getChildren().addAll(new Text("Invoke Other Function"), new Text("\nUsed to explicitly invoke a \nfunction, however functions\ncan also be invoked in any \nexpression"), 
                    new Text("\nEdit Vertex:"), editVertexBtn);
        } else if (v.getController() instanceof ForLoopController) { 
            rightSidebarVb.getChildren().addAll(new Text("For Loop"), new Text("\nMust be structured properly with \nFor While - see manual"), 
                    new Text("\nEdit Vertex:"), editVertexBtn);
        }
        
        rightSidebarVb.getChildren().addAll(new Text("\nVertex Description:"), vLblTxtArea);
        
        if(v.getController() instanceof EndIfController || v.getController() instanceof EndWhileController || v.getController() instanceof EndForController){
            rightSidebarVb.getChildren().addAll(new Text("\nRequired Parents:"), new Text(Integer.toString(v.getController().getMaxParents() - 1)), 
                new Text("\nRequired Children:"), new Text(v.getController().getMaxChildren().toString()));
        } else if(v.getController() instanceof IfStmtController || v.getController() instanceof WhileController || v.getController() instanceof ForLoopController) {
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
    
    /**
     * set canvas size to 1000 + furthest vertex position in both directions
     */
    private void resetCanvasSize(){
        dialogZc.setPrefSize(Math.max(1000,(vertexGestures.getFurthestVertexCoordinates().getKey() + 1000)), 
                Math.max(1000,(vertexGestures.getFurthestVertexCoordinates().getValue() + 1000)));
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
            fFlowchart.removeVertices(removedVertices);
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
            fFlowchart.removeVertices(removedVertices);
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
            fFlowchart.removeVertices(removedVertices);
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
            fFlowchart.removeVertices(removedVertices);
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
            Vertex endForV = ((ForLoopController)vController).getEndFor().getVertex();
            removedVertices.add(endForV);
            fFlowchart.removeVertices(removedVertices);
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
            Vertex forV = ((EndForController)vController).getForCtrl().getVertex();
            removedVertices.add(forV);
            fFlowchart.removeVertices(removedVertices);
            forV.getConnections().stream().map((c) -> {
                removedViews.add(c.getKey().getView());
                return c;
            }).forEachOrdered((c) -> {
                removedEdges.add(c.getKey());
            });
            removedViews.add(forV.getView());
        } else {
            //remove the vertex from the flow chart
            fFlowchart.removeVertex(v);
        }
        
        //remove the views from the canvas
        dialogZc.getChildren().removeAll(removedViews);
        
        //remove the edges from the flowchart
        fFlowchart.removeEdges(removedEdges);
        
        //delete corresponding vars from flow chart for variable creating nodes
        if(v.getController() instanceof VarDecController){
            VarDecController vVarDecController = (VarDecController) v.getController();
            fFlowchart.removeVar(vVarDecController.getVar());
        }
        if(v.getController() instanceof UserInToVarController) {
            UserInToVarController vInToVarController = (UserInToVarController) v.getController();
            fFlowchart.removeVar(vInToVarController.getVar());
        } 
        if(v.getController() instanceof ArrayDecController){
            ArrayDecController vArrDecController = (ArrayDecController) v.getController();
            fFlowchart.removeVar(vArrDecController.getVar());
        }
        
        updateRSidebar();
    }
}
