/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Vertex Controller for declaring array variables
 * 
 * @author cwood
 */
public class ArrayDecController extends VertexController{
    //data type of the array
    private VarType type;
    //name of the array
    private String name;
    //the expression for the values of the array
    private String values;
    //the variable to hold the array
    private Var var;
    
    /**
     * getter for data type 
     * 
     * @return type
     */
    public VarType getType() {
        return type;
    }
    
    /**
     * getter for name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * setter for data type
     * 
     * @param type new type value 
     */
    public void setType(VarType type) {
        this.type = type;
    }
    
    /**
     * setter for name
     * 
     * @param name new name value 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * getter for expression for the values
     * 
     * @return values
     */
    public String getValues() {
        return values;
    }

    /**
     * setter for expression for the values
     * 
     * @param values new values expression
     */
    public void setValues(String values) {
        this.values = values;
    }
    
    @Override
    public String getVertexLabel() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + " array " + name + " = " + values; 
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
     * setter for variable to hold array
     * 
     * @param v new variable value
     */
    public void setVar(Var v) {
        var = v;
    }

    /**
     * getter for variable that holds array
     * 
     * @return variable
     */
    public Var getVar() {
        return var;
    }

    @Override
    public String getJavaDescription() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + "[] " + name + " = {" + values + "};";
    }
    
}
