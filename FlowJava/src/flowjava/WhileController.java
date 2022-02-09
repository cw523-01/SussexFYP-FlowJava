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
public class WhileController extends VertexController{
    private String expr;
    private ExpressionHBox exprHbx;
    private EndWhileController endWhile;
    
    private EdgeView trueEdge;
    
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }
    
    @Override
    public String getVertexLabel() {
        return "While (" + expr + ")"; 
    }

    public EndWhileController getEndWhile() {
        return endWhile;
    }

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

    public EdgeView getTrueEdge() {
        return trueEdge;
    }

    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }

    @Override
    public String getJavaDescription() {
        return "while(" + expr + ") {...} \n\nWith everything between the \nwhile and end while inside \nthe curly braces.";
    }
    
    
    
}
