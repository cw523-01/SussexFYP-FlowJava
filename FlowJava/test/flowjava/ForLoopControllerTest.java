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
public class ForLoopControllerTest {
    
    private ForLoopController instance;
    private EndForController endForInstance;
    
    
    public ForLoopControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new ForLoopController();
        endForInstance = new EndForController();
        instance.setEndFor(endForInstance);
        instance.setInitialExpr("int i = 0");
        instance.setConditionExpr("i < 10");
        instance.setUpdateExpr("i++");
    }

    /**
     * Test of getInitialExpr method, of class ForLoopController.
     */
    @Test
    public void testGetInitialExpr() {
        System.out.println("getInitialExpr");
        String expResult = "int i = 0";
        String result = instance.getInitialExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setInitialExpr method, of class ForLoopController.
     */
    @Test
    public void testSetInitialExpr() {
        System.out.println("setInitialExpr");
        String initialExpr = "";
        instance.setInitialExpr(initialExpr);
    }

    /**
     * Test of getConditionExpr method, of class ForLoopController.
     */
    @Test
    public void testGetConditionExpr() {
        System.out.println("getConditionExpr");
        String expResult = "i < 10";
        String result = instance.getConditionExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setConditionExpr method, of class ForLoopController.
     */
    @Test
    public void testSetConditionExpr() {
        System.out.println("setConditionExpr");
        String conditionExpr = "";
        instance.setConditionExpr(conditionExpr);
    }

    /**
     * Test of getUpdateExpr method, of class ForLoopController.
     */
    @Test
    public void testGetUpdateExpr() {
        System.out.println("getUpdateExpr");
        String expResult = "i++";
        String result = instance.getUpdateExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUpdateExpr method, of class ForLoopController.
     */
    @Test
    public void testSetUpdateExpr() {
        System.out.println("setUpdateExpr");
        String updateExpr = "";
        instance.setUpdateExpr(updateExpr);
    }

    /**
     * Test of getVertexLabel method, of class ForLoopController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "For (int i = 0; i < 10; i++)";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndFor method, of class ForLoopController.
     */
    @Test
    public void testGetEndFor() {
        System.out.println("getEndFor");
        EndForController expResult = endForInstance;
        EndForController result = instance.getEndFor();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEndFor method, of class ForLoopController.
     */
    @Test
    public void testSetEndFor() {
        System.out.println("setEndFor");
        EndForController endFor = null;
        instance.setEndFor(endFor);
    }

    /**
     * Test of getMaxChildren method, of class ForLoopController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 2;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class ForLoopController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
    
}
