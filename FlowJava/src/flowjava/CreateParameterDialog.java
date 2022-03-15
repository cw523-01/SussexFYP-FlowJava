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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class CreateParameterDialog {
    private ComboBox<VarType> typeCmbx;
    private TextField nameTxtFld;
    private boolean valid;
    private VBox root;
    private Button confirmBtn;
    private VarType type;
    private String name;
    
    public Object[] display(ArrayList<Var> variables, Boolean isEdit, VarType paramType, String paramName) {
        valid = false;
        Text typeTxt = new Text("Parameter Type:");
        Text nameTxt = new Text("Parameter Name:");
        
        ArrayList<String> varNames = new ArrayList<>();
        for(Var p: variables){
            varNames.add(p.getName());
        }
        
        if(isEdit){
            varNames.remove(paramName);
        }
        
        confirmBtn = new Button("Confirm");
        
        //instantiate combo box for choosing a data type
        typeCmbx = new ComboBox<>();
        typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
        typeCmbx.setPromptText("Parameter Type");
        
        nameTxtFld = new TextField();
        nameTxtFld.setPromptText("Parameter Name");
        
        //instantiate the stage
        Stage stage = new Stage();
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
        
        if(isEdit){
            nameTxtFld.setText(paramName);
            typeCmbx.setValue(paramType);
        }
          
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("Parmater");
        stage.setScene(scene);
        stage.showAndWait();
        
        if(valid){
            return new Object[]{type, name};
        } else {
            return null;
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
    }
}
