/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class IfEdgeDialog {
    private Boolean isTrueEdge;
    
    public Boolean display() {
        isTrueEdge = null;
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Text promptTxt = new Text("Is this the True or False connection?");
        
        
        Button trueBtn = new Button("True");
        Button falseBtn = new Button("False");
        
        trueBtn.setOnAction(e -> {
            isTrueEdge = true;
            stage.close();
        });
        
        falseBtn.setOnAction(e -> {
            isTrueEdge = false;
            stage.close();
        });
        
        HBox btnsHbx = new HBox();
        btnsHbx.getChildren().addAll(trueBtn, falseBtn);
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(promptTxt, btnsHbx);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 150);          
        stage.setTitle("If Statement Edge");
        stage.setScene(scene);
        stage.showAndWait();
        
        //return input
        return isTrueEdge;
    }
}
