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
public class UserInputToVariable extends VertexController{
    private VarType type;
    private String name;
    private Var var;
    
    public VarType getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    
    public void setType(VarType type) {
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getVertexLabel() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + " " + name + " = user input"; 
    }

    public void setVar(Var v) {
        var = v;
    }

    public Var getVar() {
        return var;
    }
    
    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 1;
    }
}
