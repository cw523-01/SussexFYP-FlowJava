package flowjava;

/**
 * An exception that is thrown when an exception is thrown when a user created
 * program is compiled or run
 *
 * @author cwood
 */
public class UserCreatedExprException extends Exception{
    /**
     * constructor for objects of class UserCreatedExprException
     * 
     * @param s exception message
     */
    public UserCreatedExprException(String s){
        super(s);
    }
}
