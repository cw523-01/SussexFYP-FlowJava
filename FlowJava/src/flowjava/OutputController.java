package flowjava;

/**
 * Vertex Controller for outputting to a user of a user created program
 *
 * @author cwood
 */
public class OutputController extends VertexController{
    //output expression string
    private String expr;
    //output expression in an ExpressionHBox (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //whether the vertex uses the ExpressionHBox
    private boolean usingExprHbx;
    
    /**
     * getter for output expression in an ExpressionHBox
     * 
     * @return output expression in an ExpressionHBox
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * setter for output expression in an ExpressionHBox
     * 
     * @param exprHbx new expression for output statement in ExpressionHBox
     */
    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    /**
     * getter for output expression string
     * 
     * @return output expression string
     */
    public String getExpr() {
        return expr;
    }

    /**
     * setter for output expression string
     * 
     * @param expr new value for output expression string
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }
    
    @Override
    public String getVertexLabel() {
        return "Output: " + expr; 
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
        return "System.out.println(" + expr + "); \n\nThere are many methods of \noutputting values in java, this \nis the most basic";
    }
}
