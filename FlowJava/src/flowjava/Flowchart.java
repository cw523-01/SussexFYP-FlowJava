
package flowjava;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 * Used to represent a user created flowchart that translates to Java code
 * Composed of lists of vertices edges and Variables
 * 
 * @author cwood
 */
public class Flowchart implements Serializable {
    //vertices of the flowchart
    private ArrayList<Vertex> vertices;
    //edges of the flowchart
    private ArrayList<Edge> edges;
    //variables of the flowchart
    private ArrayList<Var> variables;
    //the starting vertex of the flowchart
    private Vertex startVertex;
    //the functions that are part of the flowcharts program
    private ArrayList<FunctionFlowchart> functions;
    
    /**
     * Constructor for objects of class Flowchart
     */
    public Flowchart(){
        //instantiate lists
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        variables = new ArrayList<>();
        functions = new ArrayList<>();
        
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
     * 
     * @return true if vertex existed in the flowchart and was removed
     */
    public Boolean removeVertex(Vertex v){
        return vertices.remove(v);
    }
    
    /**
     * given an array list of vertices, remove the vertices from the flowchart
     * 
     * @param vertices array list of vertices to remove
     * @return true if vertices list has changed due to the call
     */
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
    public void removeEdges(ArrayList<Edge> removedEdges) {
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

    /**
     * getter for array list of variables (Var objects) in the flowchart
     * 
     * @return variables in array list 
     */
    public ArrayList<Var> getVariables() {
        return variables;
    }

    /**
     * setter for starting vertex of flowchart
     * 
     * @param startVertex new value for starting vertex
     */
    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    /**
     * getter for starting vertex of flowchart
     * 
     * @return starting vertex 
     */
    public Vertex getStartVertex() {
        return startVertex;
    }
    
    /**
     * adds a given function to the flowchart
     * @param newFunction new function to add to flowchart
     */
    public void addFunction(FunctionFlowchart newFunction){
        functions.add(newFunction);
    }
    
    /**
     * removes a given function from the flowchart
     * 
     * @param function function to remove
     * @return true if function was part of the flowchart
     */
    public Boolean removeFunction(FunctionFlowchart function){
        return functions.remove(function);
    }

    /**
     * getter for array list of functions in the flowchart
     * 
     * @return array list of functions 
     */
    public ArrayList<FunctionFlowchart> getFunctions() {
        return functions;
    }

    /**
     * setter for array list of functions in the flowchart
     * 
     * @param functions new array list of functions
     */
    public void setFunctions(ArrayList<FunctionFlowchart> functions) {
        this.functions = functions;
    }

    /**
     * setter for array list of vertices in the flowchart
     * 
     * @param vertices new array list of vertices 
     */
    public void setVertices(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    /**
     * setter for array list of edges in the flowchart
     * 
     * @param edges new array list of edges
     */
    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    /**
     * setter for array list of variables in the flowchart
     * 
     * @param variables new array list of variables 
     */
    public void setVariables(ArrayList<Var> variables) {
        this.variables = variables;
    }
    
    
}
