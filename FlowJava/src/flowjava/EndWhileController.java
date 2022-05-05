package flowjava;

/**
 * Vertex Controller for asserting the end of a while loop
 *
 * @author cwood
 */
public class EndWhileController extends VertexController{
    //the correlating while loop to this end while
    private WhileController whileCtrl;

    /**
     * getter for the while loop controller that correlates to this end while
     * 
     * @return while controller
     */
    public WhileController getWhileCtrl() {
        return whileCtrl;
    }

    /**
     * setter for the while loop controller that correlates to this end while
     * 
     * @param whileCtrl new value for correlating while controller
     */
    public void setWhileCtrl(WhileController whileCtrl) {
        this.whileCtrl = whileCtrl;
    }
    
    @Override
    public String getVertexLabel() {
        return "End While"; 
    }
    
    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 2;
    }
    
    @Override
    public String getJavaDescription() {
        return "in java you do not explicitly\nstate an end while, an end\nwhile is implied by the closing\ncurly brace of a while loop";
    }
}
