/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Vertex Controller for asserting start of for loop
 *
 * @author cwood
 */
public class ForLoopController extends VertexController{
    //string for intial expression of for loop statement (can be empty)
    private String initialExpr;
    //string for condition expression of for loop statement (can be empty)
    private String conditionExpr;
    //string for update expression of for loop statement (can be empty)
    private String updateExpr;
    //correlating end for assertion vertex
    private EndForController endFor;
    //an edge view connecting from the vertex view that leads to the vertex that is run if the condition expression evaluates to true
    private EdgeView trueEdge;
    
    /**
     * getter for initial expression string
     * 
     * @return initial expression string
     */
    public String getInitialExpr() {
        return initialExpr;
    }
    
    /**
     * setter for initial expression string
     * 
     * @param initialExpr new value for initial expression string
     */
    public void setInitialExpr(String initialExpr) {
        this.initialExpr = initialExpr;
    }
    
    /**
     * getter for condition expression string
     * 
     * @return condition expression string
     */
    public String getConditionExpr() {
        return conditionExpr;
    }
    
    /**
     * setter for condition expression string
     * 
     * @param conditionExpr new value for condition expression string
     */
    public void setConditionExpr(String conditionExpr) {
        this.conditionExpr = conditionExpr;
    }
    
    /**
     * getter for update expression string
     * 
     * @return update expression string
     */
    public String getUpdateExpr() {
        return updateExpr;
    }
    
    /**
     * setter for update expression string
     * 
     * @param updateExpr new value for update expression string
     */
    public void setUpdateExpr(String updateExpr) {
        this.updateExpr = updateExpr;
    }
    
    @Override
    public String getVertexLabel() {
        return "For (" + initialExpr + "; " + conditionExpr + "; " + updateExpr + ")"; 
    }
    
    /**
     * getter for correlating end for vertex controller
     * 
     * @return correlating end for vertex controller
     */
    public EndForController getEndFor() {
        return endFor;
    }
    
    /**
     * setter for correlating end for vertex controller
     * 
     * @param endFor new value for correlating end for vertex controller
     */
    public void setEndFor(EndForController endFor) {
        this.endFor = endFor;
    }
    
    @Override
    public Integer getMaxChildren() {
        return 2;
    }
    
    @Override
    public Integer getMaxParents() {
        return 1;
    }
    
    /**
     * getter for edge view connecting from the vertex view that leads to the vertex that is run if the condition expression evaluates to true
     * 
     * @return edge view of true edge
     */
    public EdgeView getTrueEdge() {
        return trueEdge;
    }
    
    /**
     * setter for edge view connecting from the vertex view that leads to the vertex that is run if the condition expression evaluates to true
     * 
     * @param trueEdge new value for edge view of true edge
     */
    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }
    
    @Override
    public String getJavaDescription() {
        return "for(" + initialExpr + "; " + conditionExpr + "; " + updateExpr + ") {...} \n\nWith everything between the \nfor and end for inside \nthe curly braces.";
    }
    
}
