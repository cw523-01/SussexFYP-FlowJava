/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Vertex Controller for a return statement
 *
 * @author cwood
 */
public class ReturnController extends VertexController{
    //return expression string
    private String expr;
    //output expression in an ExpressionHBox (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //whether the vertex uses the ExpressionHBox
    private boolean usingExprHbx;
    
    /**
     * getter for output expression in an ExpressionHBox
     * 
     * @return return expression in an ExpressionHBox
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * setter for output expression in an ExpressionHBox
     * 
     * @param exprHbx new expression for return statement in ExpressionHBox
     */
    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    /**
     * getter for return expression string
     * 
     * @return return expression string
     */
    public String getExpr() {
        return expr;
    }

    /**
     * setter for return expression string
     * 
     * @param expr new value for return expression string
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }
    
    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }
    
    @Override
    public String getJavaDescription() {
        return ("return (" + expr + ");");
    }
    
    @Override
    public String getVertexLabel() {
        return "Return: " + expr; 
    }
    
    /**
     * getter for whether the vertex uses the ExpressionHBox
     * 
     * @return whether the vertex uses the ExpressionHBox
     */
    public boolean isUsingExprHbx() {
        return usingExprHbx;
    }

    /**
     * setter for whether the vertex uses the ExpressionHBox
     * 
     * @param usingExprHbx new value for whether the vertex uses the ExpressionHBox
     */
    public void setUsingExprHbx(boolean usingExprHbx) {
        this.usingExprHbx = usingExprHbx;
    }
}
