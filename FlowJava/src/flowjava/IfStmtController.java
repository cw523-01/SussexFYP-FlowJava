package flowjava;

/**
 * Vertex Controller for asserting start of an if statement
 *
 * @author cwood
 */
public class IfStmtController extends VertexController{
    //conditional expression
    private String expr;
    //ExpressionHBox for conditional expression (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //correlatinf end if controller
    private EndIfController endIf;
    //the edge leading to the true branch
    private transient EdgeView trueEdge;
    //the edge leading to the false branch
    private transient EdgeView falseEdge;
    
    /**
     * getter for ExpressionHBox used for conditional expression
     * 
     * @return ExpressionHBox used for conditional expression
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * getter for ExpressionHBox used for conditional expression
     * 
     * @param exprHbx new value for ExpressionHBox used for conditional expression
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
        return "If (" + expr + ")"; 
    }

    /**
     * getter for correlating end if controller
     * 
     * @return correlating end if controller
     */
    public EndIfController getEndIf() {
        return endIf;
    }

    /**
     * setter for correlating end if controller
     * 
     * @param endIf new value for correlating end if controller
     */
    public void setEndIf(EndIfController endIf) {
        this.endIf = endIf;
    }

    /**
     * getter for edge view leading to true branch
     * 
     * @return edge view leading to true branch
     */
    public EdgeView getTrueEdge() {
        return trueEdge;
    }

    /**
     * setter for edge view leading to true branch
     * 
     * @param trueEdge new value for edge view leading to true branch
     */
    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }

    /**
     * getter for edge view leading to false branch
     * 
     * @return edge view leading to false branch
     */
    public EdgeView getFalseEdge() {
        return falseEdge;
    }

    /**
     * setter for edge view leading to false branch
     * 
     * @param falseEdge new value for edge view leading to false branch
     */
    public void setFalseEdge(EdgeView falseEdge) {
        this.falseEdge = falseEdge;
    }
    
    @Override
    public Integer getMaxChildren() {
        return 3;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }
    
    
    @Override
    public double getChildEdgeY(EdgeView eView) {
        //get corrdinate based on the branch the edge should lead to
        if(eView.equals(trueEdge)){
            return (getVertex().getView().getTranslateY()+getVertex().getView().getHeight());
        } else if (eView.equals(falseEdge)) {
            return (getVertex().getView().getTranslateY()+getVertex().getView().getHeight()/2);
        }
        return (getVertex().getView().getTranslateY()+getVertex().getView().getHeight());
    }
    
    @Override
    public double getChildEdgeX(EdgeView eView){
        if(eView.equals(trueEdge)){
            return (getVertex().getView().getTranslateX()+(getVertex().getView().getWidth()/2));
        } else if (eView.equals(falseEdge)) {
            return (getVertex().getView().getTranslateX()+(getVertex().getView().getWidth()));
        }
        return (getVertex().getView().getTranslateX()+(getVertex().getView().getWidth()/2));
    }
    
    @Override
    public String getJavaDescription() {
        return "if(" + expr + ") {...} else {...} \n\nWith everything between the\ntrue branch and end if "
                + "inside\nthe first set of curly braces \nand everything between the \nfalse branch and end if "
                + "inside \nthe second set of curly braces.";
    }
    
}
