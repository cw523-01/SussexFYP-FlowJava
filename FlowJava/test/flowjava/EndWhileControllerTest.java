package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for EndWhileController
 * 
 * @author cwood
 */
public class EndWhileControllerTest {
    
    private EndWhileController instance;
    private WhileController whileInstance;
    
    public EndWhileControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new EndWhileController();
        whileInstance = new WhileController();
        instance.setWhileCtrl(whileInstance);
    }

    /**
     * Test of getWhileCtrl method, of class EndWhileController.
     */
    @Test
    public void testGetWhileCtrl() {
        System.out.println("getWhileCtrl");
        WhileController expResult = whileInstance;
        WhileController result = instance.getWhileCtrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of setWhileCtrl method, of class EndWhileController.
     */
    @Test
    public void testSetWhileCtrl() {
        System.out.println("setWhileCtrl");
        WhileController whileCtrl = null;
        instance.setWhileCtrl(whileCtrl);
    }

    /**
     * Test of getVertexLabel method, of class EndWhileController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "End While";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class EndWhileController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class EndWhileController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 2;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }
    
}
