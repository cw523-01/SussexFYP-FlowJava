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
public class VarDeclaration extends VertexController {
    
    private VarType type;
    private String name;
    private String value;

    public VarType getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }

    public void setType(VarType type) {
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getDefaultStyle() {
        return "-fx-padding: 10;-fx-border-style: solid inside;"
                + "-fx-border-width: 2;-fx-border-color: black;-fx-background-color: white;";
    }

    @Override
    public String getSelectedStyle() {
        return "-fx-padding: 10;-fx-border-style: solid inside;"
                + "-fx-border-width: 2;-fx-border-color: red;-fx-background-color: white;";
    }

    @Override
    public int getMaxChildren() {
        return 1;
    }

    @Override
    public int getMaxParents() {
        return 1;
    }

    @Override
    public String getVertexLabel() {
        return type.toString() + " " + name + " = " + value; 
    }
    
    
    
    
    
    
}
