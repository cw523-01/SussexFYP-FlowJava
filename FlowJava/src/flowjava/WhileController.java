package flowjava;

/**
 * Vertex Controller for asserting start of while loop
 *
 * @author cwood
 */
public class WhileController extends VertexController{
    //string for condition expression of while loop statement
    private String expr;
    //conditional expression in an ExpressionHBox (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //correlating end while controller
    private EndWhileController endWhile;
    //an edge view connecting from the vertex view that leads to the vertex that is run if the condition expression evaluates to true
    private EdgeView trueEdge;
    
    /**
     * getter for conditional expression in an ExpressionHBox
     * 
     * @return conditional expression in an ExpressionHBox
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * setter for conditional expression in an ExpressionHBox
     * 
     * @param exprHbx new expression for conditional statement in ExpressionHBox
     */
    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    /**
     * getter for conditional expression
     * 
     * @return conditional expression
     */
    public String getExpr() {
        return expr;
    }

    /**
     * setter for conditional expression
     * 
     * @param expr new value for conditional expression
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }
    
    @Override
    public String getVertexLabel() {
        return "While (" + expr + ")"; 
    }

    /**
     * getter for correlating end while controller
     * 
     * @return correlating end while controllers
     */
    public EndWhileController getEndWhile() {
        return endWhile;
    }

    /**
     * setter for correlating end while controller
     * 
     * @param endWhile new value for correlating end while controllers
     */
    public void setEndWhile(EndWhileController endWhile) {
        this.endWhile = endWhile;
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
     * getter for edge view connecting from the vertex view that leads to the vertex that is run if the conditional expression evaluates to true
     * 
     * @return edge view of true edge
     */
    public EdgeView getTrueEdge() {
        return trueEdge;
    }

    /**
     * setter for edge view connecting from the vertex view that leads to the vertex that is run if the conditional expression evaluates to true
     * 
     * @param trueEdge new value for edge view of true edge
     */
    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }

    @Override
    public String getJavaDescription() {
        return "while(" + expr + ") {...} \n\nWith everything between the \nwhile and end while inside \nthe curly braces.";
    }
    
    
    
}
