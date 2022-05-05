package flowjava;

import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Dialog used to ask the user what an edge from an if statement vertex correlates to, the if branch or the else branch
 *
 * @author cwood
 */
public class IfEdgeDialog {
    //whether the edge is for when the if statement evaluates to true
    private Boolean isTrueEdge;
    
    /**
     * prompts the user whether the edge is for the if or else branch then return true for the if branch and false for the else branch 
     * 
     * @return whether the edge is for when the if statement evaluates to true
     */
    public Boolean display() {
        isTrueEdge = null;
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //initilise UI elements
        Text promptTxt = new Text("Is this the True or False connection?");
        Button trueBtn = new Button("True");
        Button falseBtn = new Button("False");
        
        //if they select true then the edge is for when the if statement evaluates to true
        trueBtn.setOnAction(e -> {
            isTrueEdge = true;
            stage.close();
        });
        
        //if they select false then the edge is for when the if statement evaluates to false
        falseBtn.setOnAction(e -> {
            isTrueEdge = false;
            stage.close();
        });
        
        //set up UI components
        HBox btnsHbx = new HBox();
        btnsHbx.getChildren().addAll(trueBtn, falseBtn);
        VBox root = new VBox();
        root.getChildren().addAll(promptTxt, btnsHbx);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate scene
        Scene scene = new Scene(root, 400, 150);          
        stage.setTitle("If Statement Edge");
        stage.setScene(scene);
        URL imgURL = getClass().getResource("/images/LogoImg.png");
        stage.getIcons().add(new Image(imgURL.toString()));
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(bounds.getHeight());
        stage.setMaxWidth(bounds.getWidth());
        
        stage.showAndWait();
        
        //return input
        return isTrueEdge;
    }
}
