package flowjava;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * A specialised VertexView for if statement vertices
 *
 * @author cwood
 */
public class IfVertexView extends VertexView{
    
    /**
     * constructor for objects of class IfVertexView
     * 
     * @param vertex the vertex model for this view
     * @param backgroundShape the JavaFX background shape for this view
     * @param defaultStyle the default JavaFX style for this view
     * @param selectedStyle the JavaFX style for this view when it is selected
     */
    public IfVertexView(Vertex vertex, Shape backgroundShape, String defaultStyle, String selectedStyle) {
        //construct vertex view
        super(vertex, backgroundShape, defaultStyle, selectedStyle);
        //add specialised components/attributes
        Label tLbl = new Label("t");
        Label fLbl = new Label("f");
        Pane labelGrp = new Pane();
        labelGrp.getChildren().addAll(tLbl,fLbl);
        tLbl.setTranslateY(87);
        tLbl.setTranslateX(127);
        fLbl.setTranslateY(25);
        fLbl.setTranslateX(187);
        getChildren().addAll(labelGrp);
        
    }
}
