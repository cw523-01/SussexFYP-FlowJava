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
public class VarAssignControllerTest {
    
    private VarAssignController instance;
    
    public VarAssignControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new VarAssignController();
        instance.setExpr("i + 5");
        instance.setVarName("i");
    }

    /**
     * Test of getVertexLabel method, of class VarAssignController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "i = i + 5";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class VarAssignController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class VarAssignController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVarName method, of class VarAssignController.
     */
    @Test
    public void testGetVarName() {
        System.out.println("getVarName");
        String expResult = "i";
        String result = instance.getVarName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVarName method, of class VarAssignController.
     */
    @Test
    public void testSetVarName() {
        System.out.println("setVarName");
        String varName = "";
        instance.setVarName(varName);
    }

    /**
     * Test of getExpr method, of class VarAssignController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "i + 5";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpr method, of class VarAssignController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of isUsingExprHbx method, of class VarAssignController.
     */
    @Test
    public void testIsUsingExprHbx() {
        System.out.println("isUsingExprHbx");
        boolean expResult = false;
        boolean result = instance.isUsingExprHbx();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsingExprHbx method, of class VarAssignController.
     */
    @Test
    public void testSetUsingExprHbx() {
        System.out.println("setUsingExprHbx");
        boolean usingExprHbx = false;
        instance.setUsingExprHbx(usingExprHbx);
    }

    /**
     * Test of getJavaDescription method, of class VarAssignController.
     */
    @Test
    public void testGetJavaDescription() {
        System.out.println("getJavaDescription");
        String expResult = "i = i + 5;";
        String result = instance.getJavaDescription();
        assertEquals(expResult, result);
    }
    
}
