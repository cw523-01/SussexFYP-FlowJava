package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for Var
 *
 * @author cwood
 */
public class VarTest {
    private Var instance;
    
    public VarTest() {
    }
    
    @Before
    public void setUp() {
        instance = new Var(VarType.INTEGER,"i",0);
    }

    /**
     * Test of getName method, of class Var.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "i";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class Var.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        VarType expResult = VarType.INTEGER;
        VarType result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Var.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        Object expResult = 0;
        Object result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class Var.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        Object value = null;
        instance.setValue(value);
    }

    /**
     * Test of equals method, of class Var.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = new Var(VarType.INTEGER,"i",0);
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }
    
}
