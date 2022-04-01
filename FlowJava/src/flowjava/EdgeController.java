
package flowjava;

import java.io.Serializable;

/**
 * Edge controller for flowchart edges
 *
 * @author cwood
 */
public class EdgeController implements Serializable{
    
    //parent vertex
    final private Vertex parent;
    //child vertex
    final private Vertex child;
    //edge model
    private Edge edge;
    
    /**
     * constructor for objects of class EdgeController
     * 
     * @param parent the vertex that is the parent of this edge
     * @param child the vertex that is the child of this edge
     */
    public EdgeController(Vertex parent, Vertex child){
        //assign the parent and child vertices
        this.parent = parent;
        this.child = child;
    }

    /**
     * getter for parent vertex
     * 
     * @return parent vertex 
     */
    public Vertex getParent() {
        return parent;
    }
    
    /**
     * getter for child vertex
     * 
     * @return child vertex 
     */
    public Vertex getChild() {
        return child;
    }

    /**
     * getter for edge model
     * 
     * @return edge model 
     */
    public Edge getEdge() {
        return edge;
    }
    
    /**
     * setter for edge model
     * 
     * @param edge new edge model value 
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }
    
    
    
}
