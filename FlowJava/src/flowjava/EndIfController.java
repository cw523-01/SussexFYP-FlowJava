/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 * Vertex Controller for asserting the end of an if statement
 * 
 * @author cwood
 */
public class EndIfController extends VertexController{
    //the correlating if statement to this end if
    private IfStmtController ifStmt;

    /**
     * getter for the if statement controller that correlates to this end if
     * 
     * @return if statement controller
     */
    public IfStmtController getIfStmt() {
        return ifStmt;
    }

    /**
     * setter for the if statement controller that correlates to this end if
     * 
     * @param ifStmt new value for correlating if statement
     */
    public void setIfStmt(IfStmtController ifStmt) {
        this.ifStmt = ifStmt;
    }
    
    @Override
    public String getVertexLabel() {
        return "End If"; 
    }
    
    @Override
    public Integer getMaxChildren() {
        return 1;
    }

    @Override
    public Integer getMaxParents() {
        return 3;
    }

    @Override
    public String getJavaDescription() {
        return "in java you do not explicitly state an end if,\nan end if is implied by the closing curly brace of an if";
    }
}
