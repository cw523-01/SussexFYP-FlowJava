
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
        return "Ends";
    }

    public Boolean getIsStart() {
        return isStart;
    }
    
}
