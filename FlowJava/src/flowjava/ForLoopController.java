/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 *
 * @author cwood
 */
public class ForLoopController extends VertexController{
    
    private String initialExpr;
    private String conditionExpr;
    private String updateExpr;
    
    private EndForController endFor;
    
    private EdgeView trueEdge;

    public String getInitialExpr() {
        return initialExpr;
    }
    
    public void setInitialExpr(String initialExpr) {
        this.initialExpr = initialExpr;
    }
    
    public String getConditionExpr() {
        return conditionExpr;
    }
    
    public void setConditionExpr(String conditionExpr) {
        this.conditionExpr = conditionExpr;
    }
    
    public String getUpdateExpr() {
        return updateExpr;
    }
    
    public void setUpdateExpr(String updateExpr) {
        this.updateExpr = updateExpr;
    }
    
    @Override
    public String getVertexLabel() {
        return "For (" + initialExpr + "; " + conditionExpr + "; " + updateExpr + ")"; 
    }
    
    public EndForController getEndFor() {
        return endFor;
    }
    
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
    
    public EdgeView getTrueEdge() {
        return trueEdge;
    }
    
    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }
    
    @Override
    public String getJavaDescription() {
        return "for(" + initialExpr + "; " + conditionExpr + "; " + updateExpr + ") {...} \n\nWith everything between the \nfor and end for inside \nthe curly braces.";
    }
    
}
