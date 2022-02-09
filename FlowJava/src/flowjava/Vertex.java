/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

/**
 *
 * @author cwood
 */
public class Vertex{
    private final VertexController controller;
    private final VertexView view;
    private List<Pair<Edge,Boolean>> connections;
    
    public Vertex(VertexController controller){
        this.controller = controller;
        connections = new ArrayList<>();
        view = new VertexView(this);
    }
    
    public void addConnection(Edge connection, boolean isParent){
        connections.add(new Pair(connection, isParent));
    }
    
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
    
}
