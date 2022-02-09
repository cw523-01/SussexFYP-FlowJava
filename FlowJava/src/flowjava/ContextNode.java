/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.List;

/**
 *
 * @author cwood
 */
public class ContextNode {
    private List<ContextNode> children;
    private ContextNode parent;
    
    public ContextNode addChild(ContextNode child){
        child.setParent(this);
        children.add(child);
        return child;
    }
    
    public void setParent (ContextNode parent){
        this.parent = parent;
    }
    
    public List<ContextNode> getChildren() {
        return children;
    }
    
    public ContextNode getParent() {
        return parent;
    }
}
