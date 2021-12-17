/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class VarAssignDialog {
    private TextField nameTxtFld;
    private TextField valueTxtFld;
    
    private ExpressionHBox exprHbx;
    //variable name and assigned value
    private String name, val;
    
    private boolean valid;
    
    private boolean usingExpr;
    
    public Object[] display() {
        name = "";
        valid = false;
        val = "";
        exprHbx = null;
        usingExpr = false;
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        nameTxtFld = new TextField();
        nameTxtFld.setPromptText("Variable name");
        
        //instantiate combo box for choosing how to enter value
        ComboBox<String> valCmbx = new ComboBox<>();
        valCmbx.setPromptText("Value");
        valCmbx.setItems(FXCollections.observableArrayList("Given Value", "Expression"));
        
        //instantiate text field for typing a given value
        valueTxtFld = new TextField();
        valueTxtFld.setPromptText("Value");
        
        Text exprSavedTxt = new Text("  :saved:  ");
        exprSavedTxt.setVisible(false);
        
        //instantiate create expression button
        Button createExprBtn = new Button("Create Expression");
        createExprBtn.setOnAction(e -> {
            Object[] exprResult = CreateExprDialog.display(exprHbx);
            if(((Boolean)exprResult[1])){
                exprHbx = (ExpressionHBox) exprResult[0];
                exprSavedTxt.setVisible(true);
                createExprBtn.setText("Edit Expression");
            }
        });
        
        HBox createExprHbox = new HBox();
        createExprHbox.setAlignment(Pos.CENTER);
        createExprHbox.getChildren().addAll(createExprBtn,exprSavedTxt);
        
        HBox valInputsHBox = new HBox();
        
        valCmbx.setOnAction(e -> {
            val = "";
            valInputsHBox.getChildren().clear();
            switch (valCmbx.getValue()){
                    case "Given Value":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
            }
        });
        
        //instantiate button for confirming input
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e -> {
            //validate input
            if(nameTxtFld.getText().isEmpty()){
                showAlert(Alert.AlertType.ERROR, "please specify variable name!");
                return;
            } 
            if(valCmbx.getValue() == null){
                showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                return;
            }
            switch(valCmbx.getValue()){
                case "Given Value":
                    if(valueTxtFld.getText().isEmpty()){
                        showAlert(Alert.AlertType.ERROR, "please specify the value!");
                        return;
                    }
                    name = nameTxtFld.getText();
                    val = valueTxtFld.getText();
                    valid = true;
                    break;
                    
                case "Expression":
                    if(exprHbx == null){
                        showAlert(Alert.AlertType.ERROR, "please create expression!");
                        return;
                    }
                    name = nameTxtFld.getText();
                    String expr = exprHbx.getExprString();
                    val = expr;
                    valid = true;
                    usingExpr = true;
                    break;
            }
            stage.close();   
        });

        //instantiate HBox for positioning confirm button
        HBox confirmHBox = new HBox();
        confirmHBox.setAlignment(Pos.CENTER);
        confirmHBox.setPadding(new Insets(10, 50, 50, 50));
        confirmHBox.getChildren().add(confirmBtn);
        
        Text nameTxt = new Text("Variable name:");
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(nameTxt,nameTxtFld, valCmbx, valInputsHBox, confirmHBox);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 175);          
        stage.setTitle("Variable Assignment");
        stage.setScene(scene);
        stage.showAndWait();
        
        //return input
        return new Object[]{name,val,exprHbx,valid,usingExpr};
    }
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
    }
}
