package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for FunctInvokeController
 * 
 * @author cwood
 */
public class FunctInvokeControllerTest {
    
    private FunctInvokeController instance;
    
    public FunctInvokeControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new FunctInvokeController();
        instance.setFunctionName("f");
        instance.setParameterVals("x,y,z");
        instance.setVariableForValue("i");
    }

    /**
     * Test of getMaxChildren method, of class FunctInvokeController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class FunctInvokeController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVertexLabel method, of class FunctInvokeController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "i = f(x,y,z)";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFunctionName method, of class FunctInvokeController.
     */
    @Test
    public void testGetFunctionName() {
        System.out.println("getFunctionName");
        String expResult = "f";
        String result = instance.getFunctionName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFunctionName method, of class FunctInvokeController.
     */
    @Test
    public void testSetFunctionName() {
        System.out.println("setFunctionName");
        String functionName = "";
        instance.setFunctionName(functionName);
    }

    /**
     * Test of getParameterVals method, of class FunctInvokeController.
     */
    @Test
    public void testGetParameterVals() {
        System.out.println("getParameterVals");
        String expResult = "x,y,z";
        String result = instance.getParameterVals();
        assertEquals(expResult, result);
    }

    /**
     * Test of setParameterVals method, of class FunctInvokeController.
     */
    @Test
    public void testSetParameterVals() {
        System.out.println("setParameterVals");
        String parameterVals = "";
        instance.setParameterVals(parameterVals);
    }

    /**
     * Test of getVariableForValue method, of class FunctInvokeController.
     */
    @Test
    public void testGetVariableForValue() {
        System.out.println("getVariableForValue");
        String expResult = "i";
        String result = instance.getVariableForValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariableForValue method, of class FunctInvokeController.
     */
    @Test
    public void testSetVariableForValue() {
        System.out.println("setVariableForValue");
        String variableForValue = "";
        instance.setVariableForValue(variableForValue);
    }
    
}
