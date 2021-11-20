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
import javafx.scene.control.ScrollPane;
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
public class CreateExprDialog {
    
    private static ExpressionHBox exprHbx = null;
    private static Text exprTxt;
    private static Button confirmBtn;
    private static Boolean validSubmit = false;
    
    public static Object[] display(ExpressionHBox givenExprHbx) {
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        if(givenExprHbx == null){
            exprHbx = new ExpressionHBox(false); 
        }
        else {
            exprHbx = givenExprHbx;
        }
        
        ScrollPane expressionSp = new ScrollPane();
        expressionSp.setContent(exprHbx);
        expressionSp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        expressionSp.setPrefHeight(102);
        
        exprTxt = new Text("");
        
        confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e ->{
            if(exprHbx.isComplete()){
               validSubmit = true;
               stage.close();
            }
            else{
                Alert nullAlert = new Alert(Alert.AlertType.ERROR);
                nullAlert.setContentText("Created Expression is Incomplete!");
                nullAlert.show();
            }
        });
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(expressionSp, exprTxt, confirmBtn);
        root.setPadding(new Insets(10, 50, 10, 50));
        
        updateExprText();
        
        //instantiate and show scene
        Scene scene = new Scene(root, 800, 200);          
        stage.setTitle("Create Expression");
        stage.setScene(scene);
        stage.showAndWait();
        
        //return null if confirm button not pressed
        return new Object[]{exprHbx, validSubmit};
    }
    
    public static void updateExprText(){
        exprTxt.setText("Expression: " + exprHbx.getExprString());
    }
}
