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
public class ArrayDecControllerTest {
    private ArrayDecController instance;
    
    public ArrayDecControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new ArrayDecController();
        instance.setName("a");
        instance.setType(VarType.INTEGER);
        instance.setValues("1,2,3,4,5");
    }

    /**
     * Test of getType method, of class ArrayDecController.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        VarType expResult = VarType.INTEGER;
        VarType result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class ArrayDecController.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "a";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class ArrayDecController.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        VarType type = null;
        instance.setType(type);
    }

    /**
     * Test of setName method, of class ArrayDecController.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        instance.setName(name);
    }

    /**
     * Test of getValues method, of class ArrayDecController.
     */
    @Test
    public void testGetValues() {
        System.out.println("getValues");
        String expResult = "1,2,3,4,5";
        String result = instance.getValues();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValues method, of class ArrayDecController.
     */
    @Test
    public void testSetValues() {
        System.out.println("setValues");
        String values = "";
        instance.setValues(values);
    }

    /**
     * Test of getVertexLabel method, of class ArrayDecController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Integer array a = 1,2,3,4,5";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class ArrayDecController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class ArrayDecController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getJavaDescription method, of class ArrayDecController.
     */
    @Test
    public void testGetJavaDescription() {
        System.out.println("getJavaDescription");
        String expResult = "Integer[] a = {1,2,3,4,5};";
        String result = instance.getJavaDescription();
        assertEquals(expResult, result);
    }
    
}
