/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author cwood
 */
public class FlowChart {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    
    public FlowChart(){
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }
    
    public void addVertice(Vertex v){        
        vertices.add(v);
    }
    
    public Edge addEdge(Vertex parent, Vertex child){
        Edge e = new Edge(parent.getTranslateX()+(parent.getWidth()/2), parent.getTranslateY()+parent.getHeight(), 
                child.getTranslateX()+(parent.getWidth()/2), child.getTranslateY(), new EdgeController(parent,child));
        parent.addConnection(e, true);
        child.addConnection(e, false);
        edges.add(e);
        return e;
    }
    
    public Boolean removeEdge(Edge e){
        if(!edges.contains(e)){
            return false;
        }
        edges.remove(e);
        e.getController().getParent().removeConnection(e);
        e.getController().getChild().removeConnection(e);
        return true;
    }
    
    public Boolean removeVertice(Vertex v){
        if(!vertices.contains(v)){
            return false;
        }
        for(Pair c: v.getConnections()){
            
            removeEdge((Edge)c.getKey());
        }
        vertices.remove(v);
        return true;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    
    
}
