
package flowjava;

/**
 * Vertex controller for start and stop vertices
 *
 * @author cwood
 */
public class Terminal extends VertexController{
    //whether the terminal is the start terminal
    private final Boolean start;
    //whether the terminal is in a function
    private final Boolean inFunction;
    
    /**
     * constructor for objects of class Terminal
     * 
     * @param start whether the terminal is the start terminal (false for stop terminal)
     * @param inFunction whether the terminal is in a function
     */
    public Terminal(Boolean start, Boolean inFunction){
        this.start = start;
        this.inFunction = inFunction;
    }
    
    @Override
    public Integer getMaxChildren() {
        if(start){
            return 1;
        }
        return 0;
    }

    @Override
    public Integer getMaxParents() {
        if(start){
            return 0;
        }
        return 1;
    }

    @Override
    public String getVertexLabel() {
        if(start){
            return "Start";
        }
        return "Stop";
    }

    /**
     * getter for whether the terminal is the start terminal
     * 
     * @return whether the terminal is the start terminal (false for stop terminal)
     */
    public Boolean isStart() {
        return start;
    }

    /**
     * getter for whether the terminal is in a function
     * 
     * @return whether the terminal is in a function
     */
    public Boolean isInFunction() {
        return inFunction;
    }
    
    @Override
    public String getJavaDescription() {
        //generate description based on whether the terminal is start or stop and whether it is in a function
        if(start){
            if(inFunction){
                return "public static ~function data type~ ~function name~ (~function parameter declarations~){...}\n\n"
                        +"With the rest of the function\ninside the body of the curly\nbraces.\n\n"
                        +"This function would be placed\ninside the body of the main\nprogram.";
            } else { 
                return "Java programs are declared \nas methods or constructors \nof a class, e.g. this program \ncould be written as:\n\n"
                        + "public class myClass{\n  public static void main(String[] args){...}}\n\nWith the rest of the program\n"
                        + "being in the main() method's \ncurly braces. Here we have\ncreated a class called\nmyClass with a "
                        + "main method \nto place our program in.";
            }
        } else {
            if (inFunction){
                return "The end of a Java function is \nimplied by the closing curly \nbrace.\n\n"
                        +"The return statement of the\nfunction would go at end of\nthe fuction before the closing\ncurly brace.\n\n"
                        +"The Java of the return \nstatement would be:\n\nreturn ~return value expression~;";
            } else {
                return "The end of a Java function or \nclass is implied by the closing \ncurly brace";
            }
        }
    }
    
}
