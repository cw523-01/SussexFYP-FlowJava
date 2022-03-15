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
public class ReturnController extends VertexController{

    private String value;
    private transient ExpressionHBox exprHbx;
    private boolean usingExpr;
    
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return ("return (" + value + ");");
    }
    
    @Override
    public String getVertexLabel() {
        return "Return: " + value; 
    }
    
    public boolean isUsingExpr() {
        return usingExpr;
    }

    public void setUsingExpr(boolean usingExpr) {
        this.usingExpr = usingExpr;
    }
}