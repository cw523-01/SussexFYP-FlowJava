package flowjava;

/**
 * Vertex Controller for declaring a variable
 *
 * @author cwood
 */
public class VarDecController extends VertexController {
    //data type of the variable
    private VarType type;
    //name of the variable
    private String name;
    //expression to assign value of to variable
    private String expr;
    //assignment expression in an ExpressionHBox (must be transient as it is JavaFX object)
    private transient ExpressionHBox exprHbx;
    //whether the vertex uses the ExpressionHBox
    private boolean usingExprHbx;
    //variable object to store values
    private Var var;

    /**
     * getter for data type of the variable
     * 
     * @return data type of the variable
     */
    public VarType getType() {
        return type;
    }
    
    /**
     * getter for name of the variable
     * 
     * @return name of the variable
     */
    public String getName() {
        return name;
    }
    
    /**
     * getter for expression to assign value of to variable
     * 
     * @return expression to assign value of to variable
     */
    public String getExpr() {
        return expr;
    }

    /**
     * setter for data type of the variable
     * 
     * @param type new value for data type of the variable
     */
    public void setType(VarType type) {
        this.type = type;
    }
    
    /**
     * setter for name of the variable
     * 
     * @param name new value for name of the variable
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * setter for expression to assign value of to variable
     * 
     * @param expr new value for expression to assign value of to variable
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }

    @Override
    public String getVertexLabel() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + " " + name + " = " + expr; 
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
     * setter for variable to store values
     * 
     * @param v new value for variable to store values
     */
    public void setVar(Var v) {
        var = v;
    }

    /**
     * getter for variable to store values
     * 
     * @return variable to store values
     */
    public Var getVar() {
        return var;
    }

    /**
     * getter for expression for assign value in an ExpressionHBox
     * 
     * @return expression for assign value in an ExpressionHBox
     */
    public ExpressionHBox getExprHbx() {
        return exprHbx;
    }

    /**
     * setter for expression for assign value in an ExpressionHBox
     * 
     * @param exprHbx new value for expression for assign value in an ExpressionHBox
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
        return getVertexLabel()+";";
    }
    
}
