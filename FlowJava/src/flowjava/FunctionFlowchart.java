package flowjava;

import java.util.ArrayList;

/**
 * A specialised flowchart that represents a function of another flowchart
 *
 * @author cwood
 */
public class FunctionFlowchart extends Flowchart{
    //list of parameters of the function 
    private ArrayList<Var> parameters;
    //data type that is returned by the function (null = void)
    private VarType returnType;
    //name of the function
    private String name;
    //expression for the return value of the function
    private String retunVal;
    
    /**
     * constructor for objects of type FunctionFlowchart
     * 
     * @param returnType the data type returned by the function (null = void)
     * @param name the name of the function
     * @param parameters list of parameters of the function
     */
    public FunctionFlowchart(VarType returnType, String name, ArrayList<Var> parameters){
        //construct the flow chart
        super();
        //initialise function fields
        this.parameters = parameters;
        this.returnType = returnType;
        this.name = name;
    }

    /**
     * given a Var v, add it as a parameter to the function
     * 
     * @param v variable to add as parameter
     * @return whether the parameter was successfully added
     */
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
    
    /**
     * given a Var v, remove it as a parameter of the function
     * 
     * @param v variable as parameter to remove
     * @return true if the parameters contained the variable 
     */
    public boolean removeParameter(Var v){
        return parameters.remove(v);
    }
    
    /**
     * setter for the array list of parameters of the function 
     * 
     * @param parameters new value for the array list of parameters of the function 
     */
    public void setParameters(ArrayList<Var> parameters) {
        this.parameters = parameters;
    }

    /**
     * setter for the data type that is returned by the function (null = void)
     * 
     * @param returnType new value for data type
     */
    public void setReturnType(VarType returnType) {
        this.returnType = returnType;
    }

    /**
     * getter for the name of the function
     * 
     * @return name of the function
     */
    public String getName() {
        return name;
    }

    /**
     * setter for the name of the function
     * 
     * @param name new value for the name of the function
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * getter for the array list of parameters of the function 
     * 
     * @return array list of parameters of the function 
     */
    public ArrayList<Var> getParameters() {
        return parameters;
    }

    /**
     * getter for the data type that is returned by the function (null = void)
     * 
     * @return data type
     */
    public VarType getReturnType() {
        return returnType;
    }

    /**
     * setter for the expression for the return value of the function
     * 
     * @return the expression for the return value of the function
     */
    public String getRetunVal() {
        return retunVal;
    }

    /**
     * getter for the expression for the return value of the function
     * 
     * @param retunVal new value for the expression for the return value
     */
    public void setRetunVal(String retunVal) {
        this.retunVal = retunVal;
    }
    
}
