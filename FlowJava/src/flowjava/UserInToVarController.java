/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Vertex Controller for getting a users input and storing it in a variable
 *
 * @author cwood
 */
public class UserInToVarController extends VertexController{
    //type of variable to assign value to
    private VarType type;
    //name of variable to assign value to
    private String name;
    //variable object for storing values
    private Var var;
    
    /**
     * getter for type of variable to assign value to
     * 
     * @return variable to assign value to
     */
    public VarType getType() {
        return type;
    }
    
    /**
     * getter for name of variable to assign value to
     * 
     * @return name of variable to assign value to
     */
    public String getName() {
        return name;
    }
    
    /**
     * setter for type of variable to assign value to
     * 
     * @param type new value for type of variable to assign value to
     */
    public void setType(VarType type) {
        this.type = type;
    }
    
    /**
     * setter for name of variable to assign value to
     * 
     * @param name new value for name of variable to assign value to
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getVertexLabel() {
        return type.toString().substring(0, 1)+ type.toString().substring(1).toLowerCase() + " " + name + " = user input"; 
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
        String javaDesc = "BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
            "String input = br.readLine();";
        switch (type){
            case STRING:
                javaDesc += "\nString " + name + " = input;";
                break;
            case BOOLEAN:
                javaDesc += "Boolean " + name + " = Boolean.valueOf(input);";
                break;
            case CHARACTER:
                javaDesc += "Character " + name + " = input.charAt(0);";
                break;
            case INTEGER:
                javaDesc += "Integer " + name + " = Integer.valueOf(input);";
                break;
            case DOUBLE:
                javaDesc += "Double " + name + " = Double.valueOf(input);";
                break;
            case FLOAT:
                javaDesc += "Float " + name + " = Float.valueOf(input);";
                break;
            case LONG:
                javaDesc += "Long " + name + " = Long.valueOf(input);";
                break;
            case SHORT:
                javaDesc += "Short " + name + " = Short.valueOf(input);";
                break;
        }
        javaDesc += "\n\nThere are many methods of \ninputting values in java, this \nis the most basic.";
        return javaDesc;
    }
}
