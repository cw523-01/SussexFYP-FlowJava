/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog used to help the users create parameters for a function
 *
 * @author cwood
 */
public class CreateParameterDialog {
    //combobox for the data type of the parameter
    private ComboBox<VarType> typeCmbx;
    //text field for the name of the parameter
    private TextField nameTxtFld;
    //whether the parameter is valid
    private boolean valid;
    //root node of the dialog
    private VBox root;
    //button for confirming user input
    private Button confirmBtn;
    //data type of the parameter
    private VarType type;
    //name of the parameter
    private String name;
    
    /**
     * takes user input to create an array of objects used to create a parameter, when provided with
     * a VarType and String for an existing parameter the dialog is used to edit the parameter
     * 
     * @param variables ArrayList of other variables (including parameters) of the function this parameter belongs to
     * @param isEdit whether the dialog is being used to edit a parameter
     * @param paramType data type of an existing parameter to edit
     * @param paramName name of an existing parameter to edit
     * @return VarType for the data type of the new parameter and String for the name of the parameter in an Object list or null if the parameter is invalid
     */
    public Object[] display(ArrayList<Var> variables, Boolean isEdit, VarType paramType, String paramName) {
        //initialise valid
        valid = false;
        //create text labels for input form
        Text typeTxt = new Text("Parameter Type:");
        Text nameTxt = new Text("Parameter Name:");
        
        //create a list of variable names using variables
        ArrayList<String> varNames = new ArrayList<>();
        for(Var p: variables){
            varNames.add(p.getName());
        }
        
        //remove this parameters name from varNames if it is being editied
        if(isEdit){
            varNames.remove(paramName);
        }
        
        //instantiate confirm button
        confirmBtn = new Button("Confirm");
        
        //instantiate combo box for choosing a data type
        typeCmbx = new ComboBox<>();
        typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
        typeCmbx.setPromptText("Parameter Type");
        
        //instantiate nameTxtFld
        nameTxtFld = new TextField();
        nameTxtFld.setPromptText("Parameter Name");
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:images/LogoImg.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //instantiate root node
        root = new VBox();
        root.getChildren().addAll(typeTxt, typeCmbx, nameTxt, nameTxtFld, confirmBtn);
        root.setSpacing(5);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate button for confirming input
        confirmBtn.setOnAction(e -> {
            //validate input
            if (typeCmbx.getValue() == null || nameTxtFld.getText().equals("")) {
                showAlert(Alert.AlertType.ERROR, "empty values!");
            } else if (varNames.contains(nameTxtFld.getText())) {
                showAlert(Alert.AlertType.ERROR, "variable/parameter name already used!");
            } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString")) {
                showAlert(Alert.AlertType.ERROR, "parameters cannot be named \"userInputBr\" or \"userInputString\" in flow java");
            } else if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                showAlert(Alert.AlertType.ERROR, "parameter name is invalid!");
            } else {
                type = (VarType) typeCmbx.getValue();
                name = nameTxtFld.getText();
                valid = true;
                stage.close();
            }
        });
        
        //if parameter is being editied update input form with existing values
        if(isEdit){
            nameTxtFld.setText(paramName);
            typeCmbx.setValue(paramType);
        }
          
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("Parmeter");
        stage.setScene(scene);
        stage.showAndWait();
        
        //if prameter is valid then return its values, else return null
        if(valid){
            return new Object[]{type, name};
        } else {
            return null;
        }
    }
    
    /**
     * given an alert type and message, display an alert using these parameters for the type and content text 
     * @param alertType the alert type for the alert to display
     * @param message the content text for the alert to display
     */
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
    }
}
