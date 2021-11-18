/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Abstract class for vertex controllers, subclasses are used for converting vertices into Java code
 *
 * @author cwood
 */
public abstract class VertexController {
    //the vertex model of this controller
    protected Vertex vertex;
    //the max ammount of children for this vertex
    public Integer getMaxChildren(){return null;};
    //the max ammount of parents for this vertex
    public Integer getMaxParents(){return null;};
    //a string that summarises this vertex
    public String getVertexLabel(){return null;};

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }
    
}