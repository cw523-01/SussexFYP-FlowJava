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
