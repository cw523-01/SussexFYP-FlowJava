package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for Terminal
 *
 * @author cwood
 */
public class TerminalTest {
    
    Terminal startInstance;
    Terminal stopInstance;
    
    public TerminalTest() {
    }
    
    @Before
    public void setUp() {
        startInstance = new Terminal(true, false);
        stopInstance = new Terminal(false, false);
        
    }

    /**
     * Test of getMaxChildren method, of class Terminal.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = startInstance.getMaxChildren();
        assertEquals(expResult, result);
        expResult = 0;
        result = stopInstance.getMaxChildren();
        assertEquals(expResult, result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class Terminal.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 0;
        Integer result = startInstance.getMaxParents();
        assertEquals(expResult, result);
        expResult = 1;
        result = stopInstance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVertexLabel method, of class Terminal.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Start";
        String result = startInstance.getVertexLabel();
        assertEquals(expResult, result);
        expResult = "Stop";
        result = stopInstance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of isStart method, of class Terminal.
     */
    @Test
    public void testIsStart() {
        System.out.println("isStart");
        Boolean expResult = true;
        Boolean result = startInstance.isStart() && !stopInstance.isStart();
        assertEquals(expResult, result);
    }

    /**
     * Test of isInFunction method, of class Terminal.
     */
    @Test
    public void testIsInFunction() {
        System.out.println("isInFunction");
        Boolean expResult = false;
        Boolean result = startInstance.isInFunction();
        assertEquals(expResult, result);
    }
    
    
}
