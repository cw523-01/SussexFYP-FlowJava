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
public class RecurseController extends FunctInvokeController{
    
    @Override
    public String getVertexLabel(){
        if(getVariableForValue() == null){
            return "recurse(" + getParameterVals() + ")";
        } else {
            return getVariableForValue() + " = recurse(" + getParameterVals() + ")";
        }
    }
    
    @Override
    public String getJavaDescription() {
        String s = "To make a recursive call you\ninvoke the function like you\nwould with any other funciton,\nwithin itself:\n\n";
        if(getVariableForValue() == null){
            return s + "-this functions name- (" + getParameterVals() + ");";
        } else {
            return s + getVariableForValue() + " = -this functions name- (" + getParameterVals() + ");";
        }
    }
}
