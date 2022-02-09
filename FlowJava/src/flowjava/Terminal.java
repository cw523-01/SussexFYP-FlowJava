
package flowjava;

/**
 * Vertex controller for start and stop vertices
 *
 * @author cwood
 */
public class Terminal extends VertexController{
    private final Boolean isStart;
    
    public Terminal(Boolean isStart){
        this.isStart = isStart;
    }
    
    @Override
    public Integer getMaxChildren() {
        if(isStart){
            return 1;
        }
        return 0;
    }

    @Override
    public Integer getMaxParents() {
        if(isStart){
            return 0;
        }
        return 1;
    }

    @Override
    public String getVertexLabel() {
        if(isStart){
            return "Start";
        }
        return "Stop";
    }

    public Boolean getIsStart() {
        return isStart;
    }
    
    @Override
    public String getJavaDescription() {
        if(isStart){
            return "Java programs are declared \nas methods or constructors \nof a class, e.g. this program \ncould be written as:\n\n"
                    + "public class myClass{\n  public static void main(String[] args){...}}\n\nWith the rest of the program\n"
                    + "being in the main() method's \ncurly braces. Here we have\ncreated a class called\nmyClass with a "
                    + "main method \nto place our program in.";
        } else {
            return "The end of a Java method or \nclass is implied by the closing \ncurly brace";
        }
    }
    
}
