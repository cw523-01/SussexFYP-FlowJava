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
public class EndWhileController extends VertexController{
    private WhileController whileCtrl;

    public WhileController getWhileCtrl() {
        return whileCtrl;
    }

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
        return "in java you do not explicitly state an end while,\nan end while is implied by the closing curly brace of an while";
    }
}
