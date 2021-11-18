/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

/**
 * Model for Vertices of a flowchart
 *
 * @author cwood
 */
public class Vertex{
    private final VertexController controller;
    private final VertexView view;
    //list of pairs for connected edges and whether this vertex is the parent
    private List<Pair<Edge,Boolean>> connections;
    
    public Vertex(VertexController controller, Shape backgroundShape, String defaultStyle, String selectedStyle){
        this.controller = controller;
        connections = new ArrayList<>();
        view = new VertexView(this, backgroundShape,defaultStyle,selectedStyle);
    }
    
    /**
     * given an edge and whether this vertex is the parent, add a connection
     * 
     * @param connection Edge of the connection
     * @param isParent Boolean for if this vertex is the parent
     */
    public void addConnection(Edge connection, boolean isParent){
        connections.add(new Pair(connection, isParent));
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

    public List<Pair<Edge, Boolean>> getConnections() {
        return connections;
    }

    public VertexController getController() {
        return controller;
    }

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
    
}
