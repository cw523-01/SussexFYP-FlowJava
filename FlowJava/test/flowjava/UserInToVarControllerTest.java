package flowjava;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test class for UserInToVarController
 *
 * @author cwood
 */
public class UserInToVarControllerTest {
    
    private UserInToVarController instance;
    
    public UserInToVarControllerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new UserInToVarController();
        instance.setName("x");
        instance.setType(VarType.INTEGER);
        instance.setVar(new Var(VarType.INTEGER, "x", null));
    }

    /**
     * Test of getType method, of class UserInToVarController.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        VarType expResult = VarType.INTEGER;
        VarType result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class UserInToVarController.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "x";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class UserInToVarController.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        VarType type = null;
        instance.setType(type);
    }

    /**
     * Test of setName method, of class UserInToVarController.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        instance.setName(name);
    }

    /**
     * Test of getVertexLabel method, of class UserInToVarController.
     */
    @Test
    public void testGetVertexLabel() {
        System.out.println("getVertexLabel");
        String expResult = "Integer x = user input";
        String result = instance.getVertexLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVar method, of class UserInToVarController.
     */
    @Test
    public void testSetVar() {
        System.out.println("setVar");
        Var v = null;
        instance.setVar(v);
    }

    /**
     * Test of getVar method, of class UserInToVarController.
     */
    @Test
    public void testGetVar() {
        System.out.println("getVar");
        Var expResult = new Var(VarType.INTEGER, "x", null);
        Var result = instance.getVar();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxChildren method, of class UserInToVarController.
     */
    @Test
    public void testGetMaxChildren() {
        System.out.println("getMaxChildren");
        Integer expResult = 1;
        Integer result = instance.getMaxChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxParents method, of class UserInToVarController.
     */
    @Test
    public void testGetMaxParents() {
        System.out.println("getMaxParents");
        Integer expResult = 1;
        Integer result = instance.getMaxParents();
        assertEquals(expResult, result);
    }

}
