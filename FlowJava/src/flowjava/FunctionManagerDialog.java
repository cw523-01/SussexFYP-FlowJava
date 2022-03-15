/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class FunctionManagerDialog {

    VBox root;
    ScrollPane functionsSp;
    HBox functionsHb;
    VBox functionNamesVb;
    VBox functionEditVb;
    VBox functionDeleteVb;
    ArrayList<FunctionFlowchart> functions;
    Button newFunctionBtn;
    ArrayList<VertexView> vertexViewsArchive;
    LinkedList<Object[]> vertexTransientDataArchive;
    ArrayList<EdgeView> edgeViewsArchive;

    public FunctionManagerDialog(ArrayList<FunctionFlowchart> functions) {
        this.functions = functions;
        root = new VBox();
        root.getChildren().add(new Text("Functions:"));
        root.setPadding(new Insets(10, 50, 50, 50));
        functionsSp = new ScrollPane();
        functionsHb = new HBox();
        functionNamesVb = new VBox();
        functionNamesVb.setAlignment(Pos.CENTER_LEFT);
        functionEditVb = new VBox();
        functionDeleteVb = new VBox();

        functionNamesVb.setSpacing(15);
        functionEditVb.setSpacing(5);
        functionDeleteVb.setSpacing(5);

        functionsHb.setSpacing(50);
        functionsHb.setPadding(new Insets(5, 5, 5, 5));
        newFunctionBtn = new Button("Add");
        
        newFunctionBtn.setOnAction((ActionEvent e) -> {
            CreateFunctionDialog cFD = new CreateFunctionDialog();
            FunctionFlowchart newFunction = cFD.display(this.functions, null);
            if (newFunction != null) {
                this.functions.add(newFunction);
                updateFunctionList();
            }
        });

        updateFunctionList();

        functionsHb.getChildren().addAll(functionNamesVb, functionEditVb, functionDeleteVb);
        functionsSp.setContent(functionsHb);
        root.getChildren().add(functionsSp);
    }

    public ArrayList<FunctionFlowchart> display() {
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //instantiate and show scene
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Function Manager");
        stage.setScene(scene);
        stage.showAndWait();

        return functions;
    }

    private void updateFunctionList() {
        functionNamesVb.getChildren().clear();
        functionEditVb.getChildren().clear();
        functionDeleteVb.getChildren().clear();
        for (FunctionFlowchart f : functions) {
            Button editFunctionBtn = new Button("Edit");

            editFunctionBtn.setOnAction((ActionEvent e) -> {
                try {
                    vertexViewsArchive = new ArrayList<>();
                    vertexTransientDataArchive = new LinkedList<>();
                    edgeViewsArchive = new ArrayList<>();

                    ObjectOutputStream objectOutputStream
                            = new ObjectOutputStream(new FileOutputStream("data/function_archive.bin"));

                    objectOutputStream.writeObject(f);
                    objectOutputStream.close();

                    for (Vertex v : f.getVertices()) {
                        vertexViewsArchive.add(v.getView());
                        if (v.getController() instanceof IfStmtController) {
                            IfStmtController vContr = (IfStmtController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx(), vContr.getTrueEdge(), vContr.getFalseEdge()});
                        } else if (v.getController() instanceof OutputController) {
                            OutputController vContr = (OutputController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx()});
                        } else if (v.getController() instanceof ReturnController) {
                            ReturnController vContr = (ReturnController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx()});
                        } else if (v.getController() instanceof VarAssignController) {
                            VarAssignController vContr = (VarAssignController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx()});
                        } else if (v.getController() instanceof VarDecController) {
                            VarDecController vContr = (VarDecController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx()});
                        } else if (v.getController() instanceof WhileController) {
                            WhileController vContr = (WhileController) v.getController();
                            vertexTransientDataArchive.add(new Object[]{vContr.getExprHbx()});
                        }
                    }
                    for (Edge edge : f.getEdges()) {
                        edgeViewsArchive.add(edge.getView());
                    }

                    CreateFunctionDialog cFD = new CreateFunctionDialog();
                    
                    ArrayList<FunctionFlowchart> otherFunctions = new ArrayList<>();
                    for(FunctionFlowchart fF: functions){
                        if(fF != f){
                            otherFunctions.add(fF);
                        }
                    }
                    
                    FunctionFlowchart editedFfc = cFD.display(otherFunctions, f);

                    if (editedFfc != null) {
                        functions.remove(f);
                        functions.add(editedFfc);
                    } else {
                        ObjectInputStream objectInputStream
                                = new ObjectInputStream(new FileInputStream("data/function_archive.bin"));

                        functions.remove(f);
                        FunctionFlowchart archivedFf = (FunctionFlowchart) objectInputStream.readObject();

                        for (int i = 0; i < vertexViewsArchive.size(); i++) {
                            archivedFf.getVertices().get(i).setView(vertexViewsArchive.get(i));
                            vertexViewsArchive.get(i).setVertex(archivedFf.getVertices().get(i));

                            if (archivedFf.getVertices().get(i).getController() instanceof IfStmtController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((IfStmtController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                                ((IfStmtController)archivedFf.getVertices().get(i).getController()).setTrueEdge((EdgeView)vViewData[1]);
                                ((IfStmtController)archivedFf.getVertices().get(i).getController()).setFalseEdge((EdgeView)vViewData[2]);
                            } else if (archivedFf.getVertices().get(i).getController() instanceof OutputController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((OutputController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                            } else if (archivedFf.getVertices().get(i).getController() instanceof ReturnController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((ReturnController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                            } else if (archivedFf.getVertices().get(i).getController() instanceof VarAssignController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((VarAssignController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                            } else if (archivedFf.getVertices().get(i).getController() instanceof VarDecController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((VarDecController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                            } else if (archivedFf.getVertices().get(i).getController() instanceof WhileController) {
                                Object[] vViewData = vertexTransientDataArchive.poll();
                                ((WhileController)archivedFf.getVertices().get(i).getController()).setExprHbx((ExpressionHBox)vViewData[0]);
                            }
                        }

                        for (int i = 0; i < edgeViewsArchive.size(); i++) {
                            archivedFf.getEdges().get(i).setView(edgeViewsArchive.get(i));
                            edgeViewsArchive.get(i).setEdge(archivedFf.getEdges().get(i));
                        }

                        functions.add(archivedFf);

                        objectInputStream.close();
                    }

                    updateFunctionList();

                } catch (IOException ex) {
                    System.out.println(ex.toString());
                    showAlert(Alert.AlertType.ERROR, "Error when storing functions data!");
                } catch (ClassNotFoundException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error when loading functions data!");
                }
            });

            Button deleteFunctionBtn = new Button("Delete");

            deleteFunctionBtn.setOnAction((ActionEvent e) -> {
                if (showAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the function " + f.getName() + "?")) {
                    functions.remove(f);
                    updateFunctionList();
                }
            });

            functionNamesVb.getChildren().add(new Text("Manage function " + f.getName()));
            functionEditVb.getChildren().add(editFunctionBtn);
            functionDeleteVb.getChildren().add(deleteFunctionBtn);
            //functionEditVb.getChildren().add(deleteFunctionBtn);
        }
        functionNamesVb.getChildren().add(new Text("Add new function..."));
        functionEditVb.getChildren().add(newFunctionBtn);
        functionEditVb.setAlignment(Pos.BOTTOM_CENTER);
    }

    private boolean showAlert(Alert.AlertType alertType, String message) {
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        Optional<ButtonType> result = customAlert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> getFunctionNames() {
        ArrayList<String> functionNames = new ArrayList<>();

        for (FunctionFlowchart f : functions) {
            functionNames.add(f.getName());
        }
        return functionNames;
    }
}
