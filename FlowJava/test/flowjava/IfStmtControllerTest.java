package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for IfStmtController
 *
 * @author cwood
 */
public class IfStmtControllerTest {
    
    private IfStmtController instance;
    private EndIfController EndIfInstance;
    
    public IfStmtControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new IfStmtController();
        EndIfInstance = new EndIfController();
        instance.setEndIf(EndIfInstance);
        instance.setExpr("i == 5");
    }

    /**
     * Test of getExpr method, of class IfStmtController.
     */
    @Test
    public void testGetExpr() {
        System.out.println("getExpr");
        String expResult = "i == 5";
        String result = instance.getExpr();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpr method, of class IfStmtController.
     */
    @Test
    public void testSetExpr() {
        System.out.println("setExpr");
        String expr = "";
        instance.setExpr(expr);
    }

    /**
     * Test of getVertexLabel method, of class IfStmtController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "If (i == 5)";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndIf method, of class IfStmtController.
     */
    @Test
    public void testGetEndIf() {
        System.out.println("getEndIf");
        EndIfController expResult = EndIfInstance;
        EndIfController result = instance.getEndIf();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEndIf method, of class IfStmtController.
     */
    @Test
    public void testSetEndIf() {
        System.out.println("setEndIf");
        EndIfController endIf = null;
        instance.setEndIf(endIf);
    }

    /**
     * Test of getMaxChildren method, of class IfStmtController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 3;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class IfStmtController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
    
}
