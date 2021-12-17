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
public class VarAssignController extends VertexController {
    private String varName;
    private String value;
    private ExpressionHBox exprHbx;
    private boolean usingExpr;
    
    @Override
    public String getVertexLabel() {
        return varName + " = " + value; 
    }

    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    public void setExprHbx(ExpressionHBox exprHbx) {
        this.exprHbx = exprHbx;
    }

    public boolean isUsingExpr() {
        return usingExpr;
    }

    public void setUsingExpr(boolean usingExpr) {
        this.usingExpr = usingExpr;
    }
    
}
