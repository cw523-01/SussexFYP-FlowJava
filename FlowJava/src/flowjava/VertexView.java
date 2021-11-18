
package flowjava;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * A view for a vertex of a flowchart
 *
 * @author cwood
 */
public class VertexView extends StackPane{
    //the vertex model of this view
    private final Vertex vertex;
    //a label for this view
    private Text label;
    //the background shape of this view
    private Shape backgroundShape;
    //the default style of this view
    private String defaultStyle;
    //the style of this view when it is selected
    private String selectedStyle;
    
    public VertexView(Vertex vertex, Shape backgroundShape, String defaultStyle, String selectedStyle){
        this.vertex = vertex;
        this.backgroundShape = backgroundShape;
        this.defaultStyle = defaultStyle;
        this.selectedStyle = selectedStyle;
        backgroundShape.setStyle(defaultStyle);
        label = new Text("");
        getChildren().addAll(backgroundShape, label);
    }
    
    public Vertex getVertex() {
        return vertex;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public String getSelectedStyle() {
        return selectedStyle;
    }
    
    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public Shape getBackgroundShape() {
        return backgroundShape;
    }
    
    public void updateLabel(String s){
        label.setText(s);
    }
    
}
