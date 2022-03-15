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
public class FunctInvokeController extends VertexController {
    
    private String functionName;
    private String parameterVals;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getParameterVals() {
        return parameterVals;
    }

    public void setParameterVals(String parameterVals) {
        this.parameterVals = parameterVals;
    }

    public String getVariableForValue() {
        return variableForValue;
    }

    public void setVariableForValue(String variableForValue) {
        this.variableForValue = variableForValue;
    }
    
    
    
}
