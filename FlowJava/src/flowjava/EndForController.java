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
public class EndForController extends VertexController{
    private ForLoopController forCtrl;

    public ForLoopController getForCtrl() {
        return forCtrl;
    }

    public void setForCtrl(ForLoopController forCtrl) {
        this.forCtrl = forCtrl;
    }
    
    @Override
    public String getVertexLabel() {
        return "End For"; 
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
        return "in java you do not explicitly\nstate an end for, an end\nfor is implied by the closing\ncurly brace of a for loop";
    }
    
}
