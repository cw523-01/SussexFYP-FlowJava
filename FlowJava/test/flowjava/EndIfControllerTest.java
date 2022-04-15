/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cwood
 */
public class EndIfControllerTest {
    
    private EndIfController instance;
    private IfStmtController ifInstance;
    
    public EndIfControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new EndIfController();
        ifInstance = new IfStmtController();
        instance.setIfStmt(ifInstance);
    }

    /**
     * Test of getIfStmt method, of class EndIfController.
     */
    @Test
    public void testGetIfStmt() {
        System.out.println("getIfStmt");
        IfStmtController expResult = ifInstance;
        IfStmtController result = instance.getIfStmt();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIfStmt method, of class EndIfController.
     */
    @Test
    public void testSetIfStmt() {
        System.out.println("setIfStmt");
        IfStmtController ifStmt = null;
        instance.setIfStmt(ifStmt);
    }

    /**
     * Test of getVertexLabel method, of class EndIfController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "End If";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class EndIfController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class EndIfController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 3;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
}
