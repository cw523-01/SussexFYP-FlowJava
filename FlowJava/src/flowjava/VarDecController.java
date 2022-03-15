
package flowjava;

/**
 * Controller for variable declaration vertices
 *
 * @author cwood
 */
public class VarDecController extends VertexController {
    
    private VarType type;
    private String name;
    private String value;
    private transient ExpressionHBox exprHbx;
    private boolean usingExpr;
    
    private Var var;

    public VarType getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }

    public void setType(VarType type) {
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getVertexLabel() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + " " + name + " = " + value; 
    }

    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }

    public void setVar(Var v) {
        var = v;
    }

    public Var getVar() {
        return var;
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

    @Override
    public String getJavaDescription() {
        return getVertexLabel()+";";
    }
    
}
