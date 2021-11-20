/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class UserInDialog {
    //data type
    private VarType type;
    //variable name and value
    private String name;
    
    private ComboBox<VarType> typeCmbx;
    
    public Object[] display(ArrayList<Var> variables) {
        //set the fields to empty values
        type = null;
        name = "";
        
        ArrayList<String> varNames = new ArrayList<>();
        for(Var v: variables){
            varNames.add(v.getName());
        }
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //instantiate combo box for choosing a data type
        typeCmbx = new ComboBox<>();
        typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
        typeCmbx.setPromptText("Variable Type");
        
        //instantiate text field for entering variable name
        TextField nameTxtFld = new TextField();
        nameTxtFld.setPromptText("Variable Name");
        
        //instantiate button for confirming input
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e -> {
            //validate input
            if(typeCmbx.getValue() == null || nameTxtFld.getText().equals("")){
                showAlert(Alert.AlertType.ERROR, "empty values!");
            } else if (varNames.contains(nameTxtFld.getText())){
                showAlert(Alert.AlertType.ERROR, "variable name already used!");
            } else if(!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")){
                showAlert(Alert.AlertType.ERROR, "variable name is invalid!");
            } else {
                type = (VarType)typeCmbx.getValue();
                name = nameTxtFld.getText();
                stage.close();   
            }
        });
        
        //instantiate HBox for positioning confirm button
        HBox confirmHBox = new HBox();
        confirmHBox.setAlignment(Pos.CENTER);
        confirmHBox.setPadding(new Insets(10, 50, 50, 50));
        confirmHBox.getChildren().add(confirmBtn);
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(typeCmbx, nameTxtFld, confirmHBox);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 150);          
        stage.setTitle("User Input to Variable");
        stage.setScene(scene);
        stage.showAndWait();
        
        //return input
        return new Object[]{type,name};
        
    }
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
    }
}
