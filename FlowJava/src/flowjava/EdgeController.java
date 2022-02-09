/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 *
 * @author cwood
 */
public class EdgeController {
    
    final private Vertex parent;
    final private Vertex child;
    
    public EdgeController(Vertex parent, Vertex child){
        //assign the parent and child vertices
        this.parent = parent;
        this.child = child;
    }

    public Vertex getParent() {
        return parent;
    }
    
    public Vertex getChild() {
        return child;
    }
    
    
    
}
