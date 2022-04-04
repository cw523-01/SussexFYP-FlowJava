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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Dialog used to add, edit or delete functions of a program
 *
 * @author cwood
 */
public class FunctionManagerDialog {
    //root node of the dialog
    VBox root;
    //scroll pane to display all the functions
    ScrollPane functionsSp;
    //HBox to hold the controls for the functions
    HBox functionsHb;
    //VBox to hold the names of all the functions
    VBox functionNamesVb;
    //VBox to hold the edit buttons of all the functions
    VBox functionEditVb;
    //VBox to hold the delete buttons of all the functions
    VBox functionDeleteVb;
    //list of all the functions in the program
    ArrayList<FunctionFlowchart> functions;
    //button to add a new function to the program
    Button newFunctionBtn;
    //list to hold an archive of vertex views of a function being edited
    ArrayList<VertexView> vertexViewsArchive;
    //list to hold an archive of vertex transient data of a function being edited
    LinkedList<Object[]> vertexTransientDataArchive;
    //list to hold an archive of edge views of a function being edited
    ArrayList<EdgeView> edgeViewsArchive;

    /**
     * constructor for objects of class FunctionManagerDialog
     * 
     * @param functions the existing functions of a program
     */
    public FunctionManagerDialog(ArrayList<FunctionFlowchart> functions) {
        this.functions = functions;
        //initialise UI elements
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
        functionEditVb.setAlignment(Pos.BOTTOM_CENTER);
        functionDeleteVb.setSpacing(5);
        functionsHb.setSpacing(50);
        functionsHb.setPadding(new Insets(5, 5, 5, 5));
        newFunctionBtn = new Button("Add");
        
        newFunctionBtn.setOnAction((ActionEvent e) -> {
            //use a CreateFunctionDialog to allow the user to create a new function
            CreateFunctionDialog cFD = new CreateFunctionDialog();
            FunctionFlowchart newFunction = cFD.display(this.functions, null);
            //if created function is valid, add it to functions list
            if (newFunction != null) {
                this.functions.add(newFunction);
                update();
            }
        });

        update();

        //set up UI elements
        functionsHb.getChildren().addAll(functionNamesVb, functionEditVb, functionDeleteVb);
        functionsSp.setContent(functionsHb);
        root.getChildren().add(functionsSp);
    }

    /**
     * shows the dialog to the user and returns the updated functions list after
     * the user has finished with the dialog.
     * 
     * @return updated functions list
     */
    public ArrayList<FunctionFlowchart> display() {
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //instantiate scene
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Function Manager");
        stage.setScene(scene);
        URL imgURL = getClass().getResource("/images/LogoImg.png");
        stage.getIcons().add(new Image(imgURL.toString()));
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(bounds.getHeight());
        stage.setMaxWidth(bounds.getWidth());
        
        stage.showAndWait();

        //return updated list
        return functions;
    }

    /**
     * use the functions list to update the dialog interface
     */
    private void update() {
        //clear UI elements
        functionNamesVb.getChildren().clear();
        functionEditVb.getChildren().clear();
        functionDeleteVb.getChildren().clear();
        //add components for each function to interface 
        for (FunctionFlowchart f : functions) {
            //button for editing function
            Button editFunctionBtn = new Button("Edit");

            editFunctionBtn.setOnAction((ActionEvent e) -> {
                try {
                    //instantiate archive lists
                    //these archives are needed to save the transient data of functions
                    //JavaFX objects cannot be serialised so they must be transient
                    //using lists to archive this data is suffiecient as alterations wont affect the actual function 
                    vertexViewsArchive = new ArrayList<>();
                    vertexTransientDataArchive = new LinkedList<>();
                    edgeViewsArchive = new ArrayList<>();
                    
                    //create ObjectOutputStream to archive serializable function data to a file
                    URL dataURL = getClass().getResource("/data/function_archive.bin");
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(dataURL.getFile()));
                    //write function f to the file
                    objectOutputStream.writeObject(f);
                    objectOutputStream.close();

                    //for each vertex archive their transient data in the archive lists
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
                    //archive each edge in the edge archive list
                    for (Edge edge : f.getEdges()) {
                        edgeViewsArchive.add(edge.getView());
                    }

                    
                    CreateFunctionDialog cFD = new CreateFunctionDialog();
                    
                    //creat a list of all other functions in the program this function belongs to
                    ArrayList<FunctionFlowchart> otherFunctions = new ArrayList<>();
                    for(FunctionFlowchart fF: functions){
                        if(fF != f){
                            otherFunctions.add(fF);
                        }
                    }
                    
                    //allow user to use CreateFunctionDialog to edit the function
                    FunctionFlowchart editedFfc = cFD.display(otherFunctions, f);

                    //if the edited function is valid then replace it in functions list
                    if (editedFfc != null) {
                        functions.remove(f);
                        functions.add(editedFfc);
                    } // else revert function back to archived version 
                    else {
                        //retrieve archived function from file and replace with current version in functions
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(dataURL.getFile()));
                        functions.remove(f);
                        FunctionFlowchart archivedFf = (FunctionFlowchart) objectInputStream.readObject();
                        //reset the archived transient vertex data from f using the archive lists 
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
                        
                        //reset the archived transient edge data from f using the archive list
                        for (int i = 0; i < edgeViewsArchive.size(); i++) {
                            archivedFf.getEdges().get(i).setView(edgeViewsArchive.get(i));
                            edgeViewsArchive.get(i).setEdge(archivedFf.getEdges().get(i));
                        }
                        
                        functions.add(archivedFf);
                        objectInputStream.close();
                    }

                    update();

                } catch (IOException ex) {
                    //alert users of issues encountered when archiving a function
                    showAlert(Alert.AlertType.ERROR, "Error when storing functions data!");
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                } catch (ClassNotFoundException ex) {
                    //alert users of issues encountered when loading an archived a function
                    showAlert(Alert.AlertType.ERROR, "Error when loading functions data!");
                }
            });

            //button for deleting function
            Button deleteFunctionBtn = new Button("Delete");

            deleteFunctionBtn.setOnAction((ActionEvent e) -> {
                //prompt user for confirmation then delete function if confirmed
                if (showAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the function " + f.getName() + "?")) {
                    functions.remove(f);
                    update();
                }
            });
            //add controls for f to UI
            functionNamesVb.getChildren().add(new Text("Manage function " + f.getName()));
            functionEditVb.getChildren().add(editFunctionBtn);
            functionDeleteVb.getChildren().add(deleteFunctionBtn);
        }
        //add component to add new functions a the end of functionNamesVb and functionEditVb
        functionNamesVb.getChildren().add(new Text("Add new function..."));
        functionEditVb.getChildren().add(newFunctionBtn);
    }

    /**
     * given an alert type and message, display an alert using these parameters for the type and content text 
     * 
     * @param alertType the alert type for the alert to display
     * @param message the content text for the alert to display
     */
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
}
