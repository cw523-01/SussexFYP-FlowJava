/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.net.URL;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Dialog used to help the users create expressions using an ExpressionHBox
 *
 * @author cwood
 */
public class CreateExprDialog {
    
    //expression HBox used to build and store the expression
    private static ExpressionHBox exprHbx = null;
    //textual representation of the expression being built
    private static Text exprTxt;
    //button for confirming input 
    private Button confirmBtn;
    //wether the input is valid
    private Boolean valid = false;
    
    /**
     * takes user input to create an expression using an ExpressionHBox, when provided with
     * an ExpressionHBox the dialog is used to edit the expression
     * 
     * @param givenExprHbx existing ExpressionHBox to edit, null for new expressions 
     * @return Object array containing the created ExpressionHbox and a boolean for whether it is valid
     */
    public Object[] display(ExpressionHBox givenExprHbx) {
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //set the value for exprHbx
        if(givenExprHbx == null){
            exprHbx = new ExpressionHBox(false); 
        }
        else {
            exprHbx = givenExprHbx;
        }
        
        //create scroll pane to hold exprHbx
        ScrollPane expressionSp = new ScrollPane();
        expressionSp.setContent(exprHbx);
        expressionSp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        expressionSp.setPrefHeight(102);
        
        //initialise exprTxt
        exprTxt = new Text("");
        
        //setup confirm button
        confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e ->{
            //check validity of exprHbx
            if(exprHbx.isComplete()){
               valid = true;
               stage.close();
            } else {
                //alert user of invalidity
                Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
                incompleteAlert.setContentText("Created Expression is Incomplete!");
                incompleteAlert.show();
            }
        });
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(expressionSp, exprTxt, confirmBtn);
        root.setPadding(new Insets(10, 50, 10, 50));
        
        //update expression text incase exprHbx is being editted 
        updateExprText();
        
        //instantiate and show scene
        Scene scene = new Scene(root, 800, 200);          
        stage.setTitle("Create Expression");
        stage.setScene(scene);
        URL imgURL = getClass().getResource("/images/LogoImg.png");
        stage.getIcons().add(new Image(imgURL.toString()));
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(bounds.getHeight());
        stage.setMaxWidth(bounds.getWidth());
        stage.showAndWait();
        
        //return null if confirm button not pressed
        return new Object[]{exprHbx, valid};
    }
    
    /**
     * updates the Text used to display the current expression
     */
    public static void updateExprText(){
        exprTxt.setText("Expression: " + exprHbx.getExprString());
    }
}
