
package flowjava;

import java.util.Objects;

/**
 * Variables stored in a flow chart, composed of a data type, name and value
 *
 * @author cwood
 */
public class Var {
    //data type
    private final VarType type;
    //name
    private final String name;
    //value
    private String value;
    
    public Var(VarType type, String name, String value){
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
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Var)){
            return false;
        }
        Var v = (Var) o;
        return type.equals(v.getType()) && name.equals(v.getName()) && value.equals(v.getValue());
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