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
public class UserInToVarController extends VertexController{
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
