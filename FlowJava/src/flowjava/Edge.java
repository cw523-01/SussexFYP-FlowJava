package flowjava;

import java.io.Serializable;

/**
 * Edge model for flowchart edges
 * 
 * @author cwood
 */
public class Edge implements Serializable{
    //controller
    private final EdgeController controller;
    //view (must be transient as it is JavaFX object)
    private transient EdgeView view;
    
    /**
     * Constructor for objects of class Edge
     * 
     * @param x1 the x coordinate for the start of the view
     * @param y1 the y coordinate for the start of the view
     * @param x2 the x coordinate for the end of the view
     * @param y2 the y coordinate for the end of the view
     * @param controller  the controller for the edge model
     */
    public Edge(double x1, double y1, double x2, double y2, EdgeController controller){
        //assign controller and view
        this.controller = controller;
        view = new EdgeView(x1,y1,x2,y2,this);
    }

    /**
     * getter for controller
     * 
     * @return controller
     */
    public EdgeController getController() {
        return controller;
    }

    /**
     * getter for view
     * 
     * @return view
     */
    public EdgeView getView() {
        return view;
    }

    /**
     * setter for view
     * 
     * @param view new view value
     */
    public void setView(EdgeView view) {
        this.view = view;
    }
    
    
    
}
