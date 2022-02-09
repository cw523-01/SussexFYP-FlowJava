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
public class EndIfController extends VertexController{
    private IfStmtController ifStmt;

    public IfStmtController getIfStmt() {
        return ifStmt;
    }

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
