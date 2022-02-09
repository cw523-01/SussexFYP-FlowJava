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
public class Var {
    
    private final String name;
    private final VarType type;
    private String value;
    
    public Var(String name, VarType type, String value){
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public VarType getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
}
