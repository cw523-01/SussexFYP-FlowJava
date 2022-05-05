package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for RecurseController
 *
 * @author cwood
 */
public class RecurseControllerTest {
    
    private RecurseController instance;
    
    public RecurseControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new RecurseController();
        instance.setFunctionName("f");
        instance.setParameterVals("x,y,z");
        instance.setVariableForValue("i");
    }

    /**
     * Test of getVertexLabel method, of class RecurseController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "i = recurse(x,y,z)";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }
    
}
