/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.net.URL;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Dialog used to show errors to users if they are encountered when a user
 * created program is compiled or run
 *
 * @author cwood
 */
public class UserErrReportDialog {
    
    /**
     * given an array list of strings describing errors, show them in the dialog
     * to the user
     * 
     * @param errorStrs array list of error strings
     */
    public void display(ArrayList<String> errorStrs) {
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        
        //create informative text labels
        Text titleTxt = new Text("User Created Expression Error Report");
        Text headerTxt = new Text("The following errors were found in the expression:");
        Text footerTxt = new Text("note that the errors closer to the top are more"
                + "\nlikely to be the cause of the error");
        
        //create a text area and add error strings to its text content
        TextArea errorsTxtArea = new TextArea();
        for(int i = 0; i < errorStrs.size(); i++){
            //modify some error strings to simplify them
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
        //monospace font so concurrent error string lines match up
        errorsTxtArea.setFont(Font.font("monospace"));
        errorsTxtArea.setEditable(false);
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(titleTxt, headerTxt, errorsTxtArea, footerTxt);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate scene
        Scene scene = new Scene(root, 600, 400);          
        stage.setTitle("User Program Error Report");
        stage.setScene(scene);
        URL imgURL = getClass().getResource("/images/LogoImg.png");
        stage.getIcons().add(new Image(imgURL.toString()));
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(bounds.getHeight());
        stage.setMaxWidth(bounds.getWidth());
        stage.showAndWait();
        
    }
}
