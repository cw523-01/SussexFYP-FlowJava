package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for Return Controller
 * 
 * @author cwood
 */
public class ReturnControllerTest {
    
    private ReturnController instance;
    
    public ReturnControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new ReturnController();
        instance.setExpr("x");
    }

    /**
     * Test of getExpr method, of class ReturnController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "x";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpr method, of class ReturnController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of getMaxChildren method, of class ReturnController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class ReturnController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of getJavaDescription method, of class ReturnController.
     */
    @Test
    public void testGetJavaDescription() {
        System.out.println("getJavaDescription");
        String expResult = "return (x);";
        String result = instance.getJavaDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVertexLabel method, of class ReturnController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Return: x";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of isUsingExprHbx method, of class ReturnController.
     */
    @Test
    public void testIsUsingExprHbx() {
        System.out.println("isUsingExprHbx");
        boolean expResult = false;
        boolean result = instance.isUsingExprHbx();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsingExprHbx method, of class ReturnController.
     */
    @Test
    public void testSetUsingExprHbx() {
        System.out.println("setUsingExprHbx");
        boolean usingExprHbx = false;
        instance.setUsingExprHbx(usingExprHbx);
    }
    
}
