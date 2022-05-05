package flowjava;

/**
 * Vertex Controller for invoking a function
 *
 * @author cwood
 */
public class FunctInvokeController extends VertexController {
    //the name of the function to invoke
    private String functionName;
    //the expression for the parameter values of the invocation
    private String parameterVals;
    //the name of the variable to set the value of the invocation to (can be empty)
    private String variableForValue;
    
    @Override
    public Integer getMaxChildren(){
        return 1;
    }
    
    @Override
    public Integer getMaxParents(){
        return 1;
    }
    
    @Override
    public String getVertexLabel(){
        if(variableForValue == null){
            return "invoke: " + functionName + "(" + parameterVals + ")";
        } else {
            return variableForValue + " = " + functionName + "(" + parameterVals + ")";
        }
    }

    @Override
    public String getJavaDescription() {
        if(variableForValue == null){
            return functionName + "(" + parameterVals + ");";
        } else {
            return variableForValue + " = " + functionName + "(" + parameterVals + ");";
        }
    }

    /**
     * getter for the name of the function to invoke
     * 
     * @return name of the function to invoke
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * setter for the name of the function to invoke
     * 
     * @param functionName new value for the name of the function to invoke
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * getter for the expression for the parameter values of the invocation
     * 
     * @return the expression for the parameter values of the invocation
     */
    public String getParameterVals() {
        return parameterVals;
    }

    /**
     * setter for the the expression for the parameter values of the invocation
     * 
     * @param parameterVals new value for parameters expression
     */
    public void setParameterVals(String parameterVals) {
        this.parameterVals = parameterVals;
    }

    /**
     * getter for the name of the variable to set the value of the invocation to
     * 
     * @return the name of the variable to set the value of the invocation to
     */
    public String getVariableForValue() {
        return variableForValue;
    }

    /**
     * setter for the name of the variable to set the value of the invocation to
     * 
     * @param variableForValue new value for the variable for value
     */
    public void setVariableForValue(String variableForValue) {
        this.variableForValue = variableForValue;
    }
    
}
