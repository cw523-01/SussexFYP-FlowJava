/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;

/**
 *
 * @author cwood
 */
public class FunctionFlowchart extends Flowchart{
    
    private ArrayList<Var> parameters;
    private VarType returnType;
    private String name;
    private String retunVal;
    
    public FunctionFlowchart(VarType returnType, String name, ArrayList<Var> parameters){
        super();
        this.parameters = parameters;
        this.returnType = returnType;
        this.name = name;
    }

    public boolean addParameter(Var v){
        boolean valid = true;
        int i = 0;
        while(valid && i < parameters.size()){
            if(parameters.get(i).getName().equals(v.getName())){
                valid = false;
            }
            i++;
        }
        if(valid){
            parameters.add(v);
        }
        return valid;
    }
    
    public boolean removeParameter(Var v){
        return parameters.remove(v);
    }
    
    public void setParameters(ArrayList<Var> parameters) {
        this.parameters = parameters;
    }

    public void setReturnType(VarType returnType) {
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<Var> getParameters() {
        return parameters;
    }

    public VarType getReturnType() {
        return returnType;
    }

    public String getRetunVal() {
        return retunVal;
    }

    public void setRetunVal(String retunVal) {
        this.retunVal = retunVal;
    }
    
}
