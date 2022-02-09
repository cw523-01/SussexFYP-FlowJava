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
public class IfStmtController extends VertexController{
    private String expr;
    private ExpressionHBox exprHbx;
    private EndIfController endIf;
    
    private EdgeView trueEdge;
    private EdgeView falseEdge;
    
    
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
        return "If (" + expr + ")"; 
    }

    public EndIfController getEndIf() {
        return endIf;
    }

    public void setEndIf(EndIfController endIf) {
        this.endIf = endIf;
    }

    public EdgeView getTrueEdge() {
        return trueEdge;
    }

    public void setTrueEdge(EdgeView trueEdge) {
        this.trueEdge = trueEdge;
    }

    public EdgeView getFalseEdge() {
        return falseEdge;
    }

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
