package flowjava;

import java.io.Serializable;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A view for a vertex of a flowchart
 *
 * @author cwood
 */
public class VertexView extends StackPane{
    //the vertex model of this view
    private Vertex vertex;
    //a label for this view
    private Text label;
    //the background shape of this view
    private Shape backgroundShape;
    //the default style of this view
    private String defaultStyle;
    //the style of this view when it is selected
    private String selectedStyle;
    
    /**
     * constructor for objects of class VertexView
     * 
     * @param vertex vertex model of the view
     * @param backgroundShape JavaFX shape for background
     * @param defaultStyle JavaFX style
     * @param selectedStyle JavaFX style for when selected
     */
    public VertexView(Vertex vertex, Shape backgroundShape, String defaultStyle, String selectedStyle){
        this.vertex = vertex;
        this.backgroundShape = backgroundShape;
        this.defaultStyle = defaultStyle;
        this.selectedStyle = selectedStyle;
        backgroundShape.setStyle(defaultStyle);
        label = new Text("");
        label.setStyle(" -fx-font: 11pt \"Consolas\";");
        getChildren().addAll(backgroundShape,label);
    }
    
    /**
     * getter for vertex model
     * 
     * @return vertex model 
     */
    public Vertex getVertex() {
        return vertex;
    }

    /**
     * getter for JavaFX style
     * 
     * @return JavaFX style
     */
    public String getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * getter for JavaFX style when selected
     * 
     * @return JavaFX style when selected
     */
    public String getSelectedStyle() {
        return selectedStyle;
    }
    
    /**
     * setter for JavaFX style
     * 
     * @param defaultStyle new value for JavaFX style
     */
    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    /**
     * setter for JavaFX style when selected
     * 
     * @param selectedStyle new value for JavaFX style when selected 
     */
    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    /**
     * setter for JavaFX shape for background
     * 
     * @param backgroundShape new value for JavaFX shape for background
     */
    public void setBackgroundShape(Shape backgroundShape) {
        this.backgroundShape = backgroundShape;
    }
    
    /**
     * getter for JavaFX shape for background
     * 
     * @return JavaFX shape for background
     */
    public Shape getBackgroundShape() {
        return backgroundShape;
    }
    
    /**
     * update view label content string to a given string
     * 
     * @param s string to update label content to 
     */
    public void updateLabel(String s){
        label.setText(s);
    }

    /**
     * getter for view label
     * 
     * @return view label
     */
    public Text getLabel() {
        return label;
    }

    /**
     * setter for vertex model
     * 
     * @param vertex new value for vertex model
     */
    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }
    
}
