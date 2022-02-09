/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class VarDecDialog {
    private VarType type;
    private String name;
 
    public String display() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
         
        TextField text1 = new TextField();
        ComboBox opCb = new ComboBox();
        opCb.setItems(FXCollections.observableArrayList(VarType.values()));
        
        Button button = new Button("Submit");
        button.setOnAction(e -> {
             type = (VarType)opCb.getValue();
             name = text1.getText();
             
             stage.close();
        });
        
        HBox root = new HBox();
        root.getChildren().addAll(opCb,text1,button);
         
        Scene scene = new Scene(root, 250, 150);          
        stage.setTitle("Dialog");
        stage.setScene(scene);
        stage.showAndWait();
         
        return type + "#" + name;
    }
}
