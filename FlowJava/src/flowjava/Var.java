
package flowjava;

import java.io.Serializable;
import java.util.Objects;

/**
 * Variables stored in a flowchart, composed of a data type, name and value
 *
 * @author cwood
 */
public class Var implements Serializable{
    //data type
    private final VarType type;
    //name
    private final String name;
    //value
    private Object value;
    
    /**
     * constructor for objects of type Var
     * 
     * @param type data type of variable
     * @param name string for variable name
     * @param value value of variable
     */
    public Var(VarType type, String name, Object value){
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * getter for variable name
     * 
     * @return variable name
     */
    public String getName() {
        return name;
    }
    
    /**
     * getter for variable data type
     * 
     * @return variable data type
     */
    public VarType getType() {
        return type;
    }
    
    /**
     * getter for value of variable
     * 
     * @return value of variable
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * setter for value of variable
     * 
     * @param value new value for value of variable
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Var)){
            return false;
        }
        Var v = (Var) o;
        if(value != null){
            return type.equals(v.getType()) && name.equals(v.getName()) && value.equals(v.getValue());
        } else {
            if(v.getValue() == null){
                return type.equals(v.getType()) && name.equals(v.getName());
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
