
package flowjava;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 * Used to represent a user created flowchart that translates to Java code
 * Composed of lists of vertices edges and Variables
 * 
 * @author cwood
 */
public class Flowchart {
    //vertices of the flowchart
    private ArrayList<Vertex> vertices;
    //edges of the flowchart
    private ArrayList<Edge> edges;
    //variables of the flowchart
    private ArrayList<Var> variables;
    
    private Vertex startVertex;
    
    public Flowchart(){
        //instantiate lists
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        variables = new ArrayList<>();
    }
    
    /**
     * add a given vertex to the flowchart
     * 
     * @param v vertex to be added
     */
    public void addVertex(Vertex v){        
        vertices.add(v);
    }
    
    /**
     * add an edge to the flowchart between given a set of parent and child vertices
     * 
     * @param parent the parent of the new edge
     * @param child the child of the edge
     * @return the newly created edge or null if the edge is invalid
     */
    public Edge addEdge(Vertex parent, Vertex child){
        //only add edge if both parent and child exist within the flowchart
        if(vertices.contains(parent) && vertices.contains(child)){
            //instantiate the edge with values taken from the parent and child
            Edge e = new Edge(parent.getView().getTranslateX() + (parent.getView().getWidth() / 2), 
                    parent.getView().getTranslateY() + parent.getView().getHeight(), 
                    child.getView().getTranslateX() + (parent.getView().getWidth() / 2), 
                    child.getView().getTranslateY(), 
                    new EdgeController(parent, child));
            
            
            //add connection to the parent and child vertices for this edge
            parent.addConnection(e, true);
            child.addConnection(e, false);
            //add edge to edges
            edges.add(e);
            //return the edge
            return e;
        }
        return null;
    }
    
    /**
     * remove a single given edge from the flowchart
     * @param e edge to be removed
     * @return boolean for if the removal was successful
     */
    public Boolean removeEdge(Edge e){
        //if edge is not in edges return false
        if(!edges.contains(e)){
            return false;
        }
        //remove the edge from edges
        edges.remove(e);
        //if edge parent is an if stmt, update it
        if(e.getController().getParent().getController() instanceof IfStmtController) {
            IfStmtController ifController = (IfStmtController)e.getController().getParent().getController();
            if(ifController.getTrueEdge() ==  e.getView()){
                ifController.setTrueEdge(null);
            }
            if(ifController.getFalseEdge() ==  e.getView()){
                ifController.setFalseEdge(null);
            }
        }
        //remove the connection from the parent and child that correlates to this edge
        e.getController().getParent().removeConnection(e);
        e.getController().getChild().removeConnection(e);
        return true;
    }
    
    /**
     * remove a given vertex from the flowchart, to avoid errors you should
     * call removeEdges() with this vertices connections afterwards
     * 
     * @param v vertex to be removed
     * @return 
     */
    public Boolean removeVertex(Vertex v){
        return vertices.remove(v);
    }
    
    public Boolean removeVertices(ArrayList<Vertex> vertices){
        return vertices.removeAll(vertices);
    }
    
    /**
     * getter for vertices
     * 
     * @return ArrayList of flowchart vertices
     */
    public ArrayList<Vertex> getVertices() {
        return vertices;
    }
    
    /**
     * getter for edges
     * 
     * @return ArrayList of flowchart edges
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    /**
     * adds a variable to the flowchart given a type name and value
     * 
     * @param type the variables type
     * @param name the variables name
     * @param value the variables value
     * @return the new variable or null if the variable already exists
     */
    public Var addVar(VarType type, String name, String value) {
        //if variable already exists return false
        if(getVar(name) != null){
            return null;
        }
        //otherwise add the variable and return true
        else {
            Var v = new Var(type,name,value);
            variables.add(new Var(type,name,value));
            return v;
        }
    }
    
    /**
     * remove a given variable from the flowchart
     * 
     * @param v variable to be removed
     * @return whether the removal was successful
     */
    public Boolean removeVar(Var v){
        return variables.remove(v);
    }
    
    /**
     * returns a variable given a variable name
     * 
     * @param name name of variable to return
     * @return found variable or null if not found
     */
    public Var getVar(String name){
        int i = 0;
        Var v = null;
        while (v == null && i < variables.size()){
            if(variables.get(i).getName().equals(name)){
                v = variables.get(i);
            }
            i++;
        }
        return v;
    } 

    /**
     * remove a list of edges from the flowchart, must be used when deleting multiple edges to avoid ConcurrentModificationException
     * 
     * @param removedEdges edges to remove 
     */
    void removeEdges(ArrayList<Edge> removedEdges) {
        //for each vertex remove all connections that contain a removed edge
        for(Vertex v: vertices){
            ArrayList<Pair<Edge,Boolean>> removedConnections = new ArrayList<>();
            for(Pair<Edge,Boolean> c: v.getConnections()){
                if(removedEdges.contains(c.getKey())){
                    removedConnections.add(c);
                }
            }
            v.getConnections().removeAll(removedConnections);
        }
        for (Edge e : removedEdges) {
            //if edge parent is an if stmt, update it
            if (e.getController().getParent().getController() instanceof IfStmtController) {
                IfStmtController ifController = (IfStmtController) e.getController().getParent().getController();
                if (ifController.getTrueEdge() == e.getView()) {
                    ifController.setTrueEdge(null);
                }
                if (ifController.getFalseEdge() == e.getView()) {
                    ifController.setFalseEdge(null);
                }
            }
        }
        //remove the edges from the edges list
        edges.removeAll(removedEdges);
    }

    public ArrayList<Var> getVariables() {
        return variables;
    }

    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }
    
    
}
