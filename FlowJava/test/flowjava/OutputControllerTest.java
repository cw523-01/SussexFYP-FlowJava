package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for OutputController
 * 
 * @author cwood
 */
public class OutputControllerTest {
    
    private OutputController instance;
    
    public OutputControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new OutputController();
        instance.setExpr("\"hello world!\"");
    }

    /**
     * Test of getExpr method, of class OutputController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "\"hello world!\"";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpr method, of class OutputController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of getVertexLabel method, of class OutputController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Output: \"hello world!\"";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class OutputController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class OutputController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of isUsingExprHbx method, of class OutputController.
     */
    @Test
    public void testIsUsingExprHbx() {
        System.out.println("isUsingExprHbx");
        boolean expResult = false;
        boolean result = instance.isUsingExprHbx();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsingExprHbx method, of class OutputController.
     */
    @Test
    public void testSetUsingExprHbx() {
        System.out.println("setUsingExprHbx");
        boolean usingExprHbx = false;
        instance.setUsingExprHbx(usingExprHbx);
    }
    
}
