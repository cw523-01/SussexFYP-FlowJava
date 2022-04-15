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
public class VarDecControllerTest {
    
    public VarDecControllerTest() {
    }
    
    private VarDecController instance;
    
    @Before
    public void setUp() {
        instance = new VarDecController();
        instance.setType(VarType.INTEGER);
        instance.setName("i");
        instance.setUsingExprHbx(false);
        instance.setExpr("0");
        instance.setVar(new Var(VarType.INTEGER, "i", 0));
    }

    /**
     * Test of getType method, of class VarDecController.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        VarType expResult = VarType.INTEGER;
        VarType result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class VarDecController.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "i";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getExpr method, of class VarDecController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "0";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class VarDecController.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        VarType type = null;
        instance.setType(type);
    }

    /**
     * Test of setName method, of class VarDecController.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        instance.setName(name);
    }

    /**
     * Test of setExpr method, of class VarDecController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of getVertexLabel method, of class VarDecController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Integer i = 0";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class VarDecController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class VarDecController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVar method, of class VarDecController.
     */
    @Test
    public void testSetVar() {
        System.out.println("setVar");
        Var v = null;
        instance.setVar(v);
    }

    /**
     * Test of getVar method, of class VarDecController.
     */
    @Test
    public void testGetVar() {
        System.out.println("getVar");
        Var expResult = new Var(VarType.INTEGER, "i", 0);
        Var result = instance.getVar();
        assertEquals(expResult, result);
    }

    /**
     * Test of getExprHbx method, of class VarDecController.
     */
    @Test
    public void testGetExprHbx() {
        System.out.println("getExprHbx");
        ExpressionHBox expResult = null;
        ExpressionHBox result = instance.getExprHbx();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExprHbx method, of class VarDecController.
     */
    @Test
    public void testSetExprHbx() {
        System.out.println("setExprHbx");
        ExpressionHBox exprHbx = null;
        instance.setExprHbx(exprHbx);
    }

    /**
     * Test of isUsingExprHbx method, of class VarDecController.
     */
    @Test
    public void testIsUsingExprHbx() {
        System.out.println("isUsingExprHbx");
        boolean expResult = false;
        boolean result = instance.isUsingExprHbx();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsingExprHbx method, of class VarDecController.
     */
    @Test
    public void testSetUsingExprHbx() {
        System.out.println("setUsingExprHbx");
        boolean usingExprHbx = false;
        instance.setUsingExprHbx(usingExprHbx);
    }

    /**
     * Test of getJavaDescription method, of class VarDecController.
     */
    @Test
    public void testGetJavaDescription() {
        System.out.println("getJavaDescription");
        String expResult = "Integer i = 0;";
        String result = instance.getJavaDescription();
        assertEquals(expResult, result);
    }
    
}
