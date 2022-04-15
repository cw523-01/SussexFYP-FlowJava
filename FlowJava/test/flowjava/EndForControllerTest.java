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
public class EndForControllerTest {
    
    private EndForController instance;
    
    public EndForControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new EndForController();
    }

    /**
     * Test of getForCtrl method, of class EndForController.
     */
    @Test
    public void testGetForCtrl() {
        System.out.println("getForCtrl");
        ForLoopController expResult = null;
        ForLoopController result = instance.getForCtrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of setForCtrl method, of class EndForController.
     */
    @Test
    public void testSetForCtrl() {
        System.out.println("setForCtrl");
        ForLoopController forCtrl = null;
        instance.setForCtrl(forCtrl);
    }

    /**
     * Test of getVertexLabel method, of class EndForController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "End For";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class EndForController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class EndForController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 2;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

}
