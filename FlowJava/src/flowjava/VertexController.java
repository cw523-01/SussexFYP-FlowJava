package flowjava;

import java.io.Serializable;

/**
 * Abstract class for vertex controllers, subclasses are used for converting vertices into Java code
 *
 * @author cwood
 */
public abstract class VertexController implements Serializable{
    //the vertex model of this controller
    protected Vertex vertex;
    /**
     * @return a description of this vertex with relation to its Java equivalency
     */
    abstract public String getJavaDescription();
    
    /**
     * getter for max number of children for this vertex
     * 
     * @return the max number of children for this vertex
     */
    public Integer getMaxChildren(){return null;};
    
    /**
     * getter for max number of parents for this vertex
     * 
     * @return the max number of parents for this vertex
     */
    public Integer getMaxParents(){return null;};
    
    /**
     * getter for summary of this vertex
     * 
     * @return a summary of this vertex
     */
    public String getVertexLabel(){return null;};

    /**
     * getter for vertex model
     * 
     * @return vertex model
     */
    public Vertex getVertex() {
        return vertex;
    }

    /**
     * setter for vertex model
     * 
     * @param vertex new value for vertex model
     */
    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }
    
    /**
     * given the view of a child edge, return the x coordinate for the start of the edge view
     * 
     * @param ev child edge view
     * @return x coordinate for the start of the edge view
     */
    public double getChildEdgeX(EdgeView ev) {
        return (getVertex().getView().getTranslateX()+(getVertex().getView().getWidth()/2));
    }

    /**
     * given the view of a child edge, return the y coordinate for the start of the edge view
     * 
     * @param ev child edge view
     * @return y coordinate for the start of the edge view
     */
    public double getChildEdgeY(EdgeView ev) {
        return (getVertex().getView().getTranslateY()+getVertex().getView().getHeight());
    }

    /**
     * given the view of a parent edge, return the x coordinate for the end of the edge view
     * 
     * @param ev child edge view
     * @return x coordinate for the end of the edge view
     */
    public double getParentEdgeX(EdgeView ev) {
        return (getVertex().getView().getTranslateX()+(getVertex().getView().getWidth()/2));
    }

    /**
     * given the view of a parent edge, return the y coordinate for the end of the edge view
     * 
     * @param ev child edge view
     * @return y coordinate for the end of the edge view
     */
    public double getParentEdgeY(EdgeView ev) {
        return (getVertex().getView().getTranslateY());
    }
    
}
