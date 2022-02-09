/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import javafx.scene.layout.StackPane;

/**
 *
 * @author cwood
 */
public class VertexView extends StackPane{
    private final Vertex vertex;
    public VertexView(Vertex vertex){
        this.vertex = vertex;
        
        setPrefSize(125, 75);
        setStyle(vertex.getController().getDefaultStyle());
    }

    public Vertex getVertex() {
        return vertex;
    }
    
}
