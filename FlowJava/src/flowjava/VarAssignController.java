package flowjava;

/**
 * Vertex Controller for assigning a value to a variable
 *
 * @author cwood
 */
public class VarAssignController extends VertexController {
    //name of variable to assign expr to
    private String varName;
    //value expression to assign to variable
    private String expr;
    //assignment expression in an ExpressionHBox (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //whether the vertex uses the ExpressionHBox
    private boolean usingExprHbx;
    
    @Override
    public String getVertexLabel() {
        return varName + " = " + expr; 
    }

    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }

    /**
     * getter for name of variable to assign value to
     * 
     * @return name of variable to assign value to
     */
    public String getVarName() {
        return varName;
    }

    /**
     * setter for name of variable to assign value to
     * 
     * @param varName new expression for name of variable to assign value to
     */
    public void setVarName(String varName) {
        this.varName = varName;
    }

    /**
     * getter for value expression to assign to variable
     * 
     * @return value expression to assign to variable
     */
    public String getExpr() {
        return expr;
    }

    /**
     * setter for value expression to assign to variable
     * 
     * @param expr new value for value expression to assign to variable
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }

    /**
     * getter for value expression in an ExpressionHBox
     * 
     * @return value expression in an ExpressionHBox
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * setter for value expression in an ExpressionHBox
     * 
     * @param exprHbx new value expression in ExpressionHBox
     */
    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
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
    
    @Override
    public String getJavaDescription() {
        return getVertexLabel() + ";";
    }
}
