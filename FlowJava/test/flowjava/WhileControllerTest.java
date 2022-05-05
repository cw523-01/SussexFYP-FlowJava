package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for WhileController
 *
 * @author cwood
 */
public class WhileControllerTest {
    
    private WhileController instance;
    private EndWhileController endWhileInstance;
    
    public WhileControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new WhileController();
        instance.setEndWhile(endWhileInstance);
        instance.setExpr("x == 5");
    }

    /**
     * Test of getExpr method, of class WhileController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "x == 5";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpr method, of class WhileController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of getVertexLabel method, of class WhileController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "While (x == 5)";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndWhile method, of class WhileController.
     */
    @Test
    public void testGetEndWhile() {
        System.out.println("getEndWhile");
        EndWhileController expResult = endWhileInstance;
        EndWhileController result = instance.getEndWhile();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEndWhile method, of class WhileController.
     */
    @Test
    public void testSetEndWhile() {
        System.out.println("setEndWhile");
        EndWhileController endWhile = null;
        instance.setEndWhile(endWhile);
    }

    /**
     * Test of getMaxChildren method, of class WhileController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 2;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class WhileController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
    
}
