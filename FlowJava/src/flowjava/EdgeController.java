
package flowjava;

/**
 * edge controller for flowchart edges
 *
 * @author cwood
 */
public class EdgeController {
    
    //parent vertex
    final private Vertex parent;
    //child vertex
    final private Vertex child;
    //edge model
    private Edge edge;
    
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

    public Edge getEdge() {
        return edge;
    }
    
    public void setEdge(Edge edge) {
        this.edge = edge;
    }
    
    
    
}
