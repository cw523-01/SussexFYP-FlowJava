/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 *
 * @author cwood
 */
public class IfVertexView extends VertexView{
    
    public IfVertexView(Vertex vertex, Shape backgroundShape, String defaultStyle, String selectedStyle) {
        super(vertex, backgroundShape, defaultStyle, selectedStyle);
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
