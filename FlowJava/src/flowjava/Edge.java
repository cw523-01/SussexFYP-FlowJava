
package flowjava;

import java.io.Serializable;

/**
 * edge model for flowchart edges
 * 
 * @author cwood
 */
public class Edge implements Serializable{
    //controller
    private final EdgeController controller;
    //view
    private transient EdgeView view;
    
    public Edge(double x1, double y1, double x2, double y2, EdgeController controller){
        //assign controller and view
        this.controller = controller;
        view = new EdgeView(x1,y1,x2,y2,this);
    }

    /**
     * getter for controller
     * @return controller
     */
    public EdgeController getController() {
        return controller;
    }

    /**
     * getter for view
     * @return view
     */
    public EdgeView getView() {
        return view;
    }

    public void setView(EdgeView view) {
        this.view = view;
    }
    
    
    
}
