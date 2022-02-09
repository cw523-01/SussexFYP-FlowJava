/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class UserErrReportDialog {
    
    public void display(ArrayList<String> errorStrs) {
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Text titleTxt = new Text("User Created Expression Error Report");
        Text headerTxt = new Text("The following errors were found in the expression:");
        Text footerTxt = new Text("note that the errors closer to the top are more"
                + "\nlikely to be the cause of the error");
        
        TextArea errorsTxtArea = new TextArea();
        for(int i = 0; i < errorStrs.size(); i++){
            if(errorStrs.get(i).startsWith("/javaEvaluator.java:")){
                errorStrs.set(i, errorStrs.get(i).substring(23, errorStrs.get(i).length()));
            } else if (errorStrs.get(i).startsWith("Object o = ")){
                errorStrs.set(i, errorStrs.get(i).substring(11, errorStrs.get(i).length()));
                errorStrs.set(i+1, errorStrs.get(i+1).substring(10, errorStrs.get(i+1).length()));
            } else if (errorStrs.get(i).endsWith("location: class javaEvaluator")){
                errorStrs.set(i, errorStrs.get(i).substring(0, errorStrs.get(i).length()-29));
            }
            errorsTxtArea.setText(errorsTxtArea.getText() + errorStrs.get(i) + "\n");
        }
        errorsTxtArea.setFont(Font.font("monospace"));
        errorsTxtArea.setEditable(false);
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(titleTxt, headerTxt, errorsTxtArea, footerTxt);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate and show scene
        Scene scene = new Scene(root, 600, 400);          
        stage.setTitle("User Created Expression Error Report");
        stage.setScene(scene);
        stage.showAndWait();
        
    }
}
