package flowjava;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

/**
 * Model for Vertices of a flowchart
 *
 * @author cwood
 */
public class Vertex implements Serializable{
    //cotroller for vertex
    private final VertexController controller;
    //view for vertex (must be transient as it is JavaFX object)
    private transient VertexView view;
    //list of pairs for connected edges and whether this vertex is the parent
    private List<Pair<Edge,Boolean>> connections;
    
    /**
     * constructor for objects of class vertex
     * 
     * @param controller controller for vertex model
     * @param backgroundShape background JavaFX shape for vertex view
     * @param defaultStyle default JavaFX style for vertex view
     * @param selectedStyle JavaFX style for vertex view for when it is selected
     */
    public Vertex(VertexController controller, Shape backgroundShape, String defaultStyle, String selectedStyle){
        this.controller = controller;
        connections = new ArrayList<>();
        view = new VertexView(this, backgroundShape, defaultStyle, selectedStyle);
    }
    
    /**
     * constructor for objects of class vertex
     * 
     * @param controller controller for vertex model
     * @param backgroundShape background JavaFX shape for vertex view
     * @param defaultStyle default JavaFX style for vertex view
     * @param selectedStyle JavaFX style for vertex view for when it is selected
     * @param isIfVertex whether the vertex controller is to be of class IfStmtController
     */
    public Vertex(VertexController controller, Shape backgroundShape, String defaultStyle, String selectedStyle, boolean isIfVertex){
        if(isIfVertex){
            this.controller = controller;
            connections = new ArrayList<>();
            //use IfVertexView instead of VertexView
            view = new IfVertexView(this, backgroundShape,defaultStyle,selectedStyle);
        } else {
            this.controller = controller;
            connections = new ArrayList<>();
            view = new VertexView(this, backgroundShape,defaultStyle,selectedStyle);
        }
    }
    
    /**
     * given an edge and whether this vertex is the parent, add a connection
     * 
     * @param connection Edge of the connection
     * @param isParent Boolean for if this vertex is the parent
     */
    public void addConnection(Edge connection, boolean isParent){
        connections.add(new Pair<>(connection, isParent));
    }
    
    /**
     * remove a connection given an edge
     * @param connection edge of connection to remove
     * @return whether the removal was successful
     */
    public Boolean removeConnection(Edge connection){
        Pair p = null;
        boolean found = false;
        int i = 0;
        while(!found && i < connections.size()){
            if(connections.get(i).getKey().equals(connection)){
                p = connections.get(i);
                found = true;
            }
            i++;
        }
        if(p == null){
            return false;
        }
        return connections.remove(p);
    }

    /**
     * getter for list of connections (edge plus whether vertex is the parent)
     * 
     * @return list of connections
     */
    public List<Pair<Edge, Boolean>> getConnections() {
        return connections;
    }

    /**
     * getter for vertex controller
     * 
     * @return vertex controller
     */
    public VertexController getController() {
        return controller;
    }

    /**
     * getter for vertex view
     * 
     * @return vertex view
     */
    public VertexView getView() {
        return view;
    }

    /**
     * returns every vertex that is directly connected as a child of this vertex
     * 
     * @return ArrayList of child vertices
     */
    public ArrayList<Vertex> getChildVertices(){
        ArrayList<Vertex> childVertices = new ArrayList<>();
        for(Pair<Edge, Boolean> c: connections){
            if(c.getValue()){
                childVertices.add(c.getKey().getController().getChild());
            }
        }
        return childVertices;
    }
    
    /**
     * returns every vertex that is directly connected as a parent of this vertex
     * 
     * @return ArrayList of parent vertices
     */
    public ArrayList<Vertex> getParentVertices(){
        ArrayList<Vertex> parentVertices = new ArrayList<>();
        for(Pair<Edge, Boolean> c: connections){
            if(!c.getValue()){
                parentVertices.add(c.getKey().getController().getParent());
            }
        }
        return parentVertices;
    }

    /**
     * setter for vertex view
     * 
     * @param view new value for vertex view 
     */
    public void setView(VertexView view) {
        this.view = view;
    }
    
}
