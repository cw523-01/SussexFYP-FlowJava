/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.lang.NumberFormatException;


/**
 *
 * @author cwood
 */
public class ProgramRunner {
    //hash map to hold variables and their contexts during run
    private HashMap<Var, Integer> variableContexts;
    //int for context
    private int currentContext;
    private ArrayList<String> errorStrings;
    private Stack<String> outputStrings;
    //print stream to divert errors caused by users
    private PrintStream userErrPrintStream;
    //print stream to divert output intended for users
    private PrintStream flowJavaOutPrintStream;
    //dialog to display user caused errors
    private UserErrReportDialog userErrReportDialog;
    
    private Stack<WhileController> whileStack;
    
    private int tabIndex;
    
    /**
     * given a Flowchart fc, runs the program represented by fc
     * 
     * @param fc the flow chart representing a program to run
     */
    public void runProgram(Flowchart fc){
        //initialise error dialog
        userErrReportDialog = new UserErrReportDialog();
        //initialise variable map
        variableContexts = new HashMap<>();
        currentContext = 0;
        
        //initialise specialised input and output streams
        userErrPrintStream = new PrintStream(new OutputStream() {
                private StringBuilder line = new StringBuilder();
                @Override
                public void write(int b) throws IOException {
                    if (b == '\n') {
                        String s = line.toString();
                        line.setLength(0);
                        errorStrings.add(s);
                    } else if (b != '\r') {
                        line.append((char) b);
                    }
                }
        });
        
        flowJavaOutPrintStream = new PrintStream(new OutputStream() {
                private StringBuilder line = new StringBuilder();
                @Override
                public void write(int b) throws IOException {
                    if (b == '\n') {
                        String s = line.toString();
                        line.setLength(0);
                        outputStrings.push(s);
                    } else if (b != '\r') {
                        line.append((char) b);
                    }
                }
        });
                
        //validate the structure of the given program
        if(!validateStructure(fc)){
            showAlert(Alert.AlertType.ERROR, "Invalid Program Structure");
            return;
        }
        
        //stack to hold the vertices that need to be run
        Stack<VertexController> controllerStack = new Stack<>();
        //the controller of the flowchart node currently being run, initialise as start node
        VertexController currentController = fc.getStartVertex().getController();
        //push start node onto stack
        controllerStack.push(currentController);
        
        //while there are still nodes that need to run
        while(!controllerStack.isEmpty()) {
            //get next node that needs to run
            currentController = controllerStack.pop();
            //execute based on the class of the controller
            if (currentController instanceof Terminal){
                if(((Terminal)currentController).getIsStart()){
                    //just push child nodes controller
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                    System.out.println("------------------");
                    System.out.println("start.");
                } else {
                    //do nothing
                    System.out.println("finish.");
                    System.out.println("------------------");}
            } else if(currentController instanceof UserInToVarController){
                System.out.println("run UserInToVar.");
                //attempt to execute node
                if(runUserInToVar((UserInToVarController)currentController)){
                    //push child nodes controller
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof VarDecController){
                System.out.println("run VarDec.");
                //attempt to execute node
                if(runVarDec((VarDecController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof OutputController){
                System.out.println("run Ouput.");
                //attempt to execute node
                if(runOutput((OutputController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof VarAssignController){
                System.out.println("run VarAssign.");
                //attempt to execute node
                if(runVarAssign((VarAssignController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof IfStmtController){
                System.out.println("run IfStmt.");
                //attempt to execute node
                currentContext++;
                VertexController nextContr = runIfStmt((IfStmtController)currentController);
                if(nextContr != null){
                    controllerStack.push(nextContr);
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof EndIfController){
                //remove(currentContext) would only remove the first variable within the current context
                variableContexts.values().removeAll(Collections.singleton(currentContext));
                currentContext--;
                //push child nodes controller
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            
            } else if(currentController instanceof WhileController){
                System.out.println("run While.");
                //attempt to execute node
                currentContext++;
                VertexController nextContr = runWhile((WhileController)currentController);
                
                if(nextContr != null){
                    controllerStack.push(nextContr);
                    if(nextContr == ((WhileController)currentController).getEndWhile().getVertex().getChildVertices().get(0).getController()){
                        //remove(currentContext) would only remove the first variable within the current context
                        variableContexts.values().removeAll(Collections.singleton(currentContext));
                        currentContext--;
                    }
                } else {
                    //exit erroneously
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof EndWhileController){
                //push child nodes controller
                controllerStack.push(((EndWhileController)currentController).getWhileCtrl());
            }
            
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Program Run Complete");
    }
    
    public Boolean convertThenCompileProgram(Flowchart fc, Boolean forRun, Boolean forSyntaxCheck, ArrayList<FunctionFlowchart> functions) throws UserCreatedExprException{ 
        //initialise error dialog
        userErrReportDialog = new UserErrReportDialog();
        
        //initialise specialised input and output streams
        userErrPrintStream = new PrintStream(new OutputStream() {
                private StringBuilder line = new StringBuilder();
                @Override
                public void write(int b) throws IOException {
                    if (b == '\n') {
                        String s = line.toString();
                        line.setLength(0);
                        errorStrings.add(s);
                    } else if (b != '\r') {
                        line.append((char) b);
                    }
                }
        });
        
        if(!validateStructure(fc)){
            showAlert(Alert.AlertType.ERROR, "Program structure invalid!");
            return false;
        }
        String classCode = convertToJava(fc, "FlowJavaUserClass", forRun, forSyntaxCheck, functions);
        //change the System.err to capture any user created errors
        PrintStream originalSysErr = System.err;
        errorStrings = new ArrayList<>();
        System.setErr(userErrPrintStream);
        //evaluate the expression
            try{
                compileConverted("FlowJavaUserClass", "run", classCode, forRun);
            } catch (UserCreatedExprException | RuntimeException e){
                if(e instanceof RuntimeException){
                    errorStrings.add(e.getMessage());
                    for(StackTraceElement ste :e.getStackTrace()){
                            errorStrings.add(ste.toString());
                    }
                    Throwable cause = e.getCause();
                    while(cause != null){
                        if(cause instanceof NumberFormatException){
                            errorStrings.clear();
                            NumberFormatException ne = (NumberFormatException)cause;
                            errorStrings.add(ne.getMessage());
                            for(StackTraceElement ste :cause.getStackTrace()){
                                errorStrings.add(ste.toString());
                            }
                            break;
                        }
                        for(StackTraceElement ste :cause.getStackTrace()){
                            errorStrings.add(ste.toString());
                        }
                        cause = cause.getCause();
                    }
                    userErrReportDialog.display(errorStrings);
                } else {
                    userErrReportDialog.display(errorStrings);
                }
                if(forRun){
                    showAlert(Alert.AlertType.ERROR, "Run Terminated Erroneously");
                }
                return false;
            } finally {
                //revert System.err to its original print stream
                System.setErr(originalSysErr);
            }
        if(forRun){
            showAlert(Alert.AlertType.INFORMATION, "Run Complete");
        }
        return true;
    }
    
    /**
     * adds a variable to the program given a type name and value
     * 
     * @param type the variables type
     * @param name the variables name
     * @param value the variables value
     * @return the new variable or null if the variable already exists
     */
    public Var addVar(VarType type, String name, Object value) {
        //if variable already exists return null
        if(getVar(name) != null){
            return null;
        }
        //otherwise add the variable and return it
        else {
            Var v = new Var(type,name,value);
            variableContexts.put(v,currentContext);
            return v;
        }
    }
    
    /**
     * returns a variable given a variable name
     * 
     * @param name name of variable to return
     * @return found variable or null if not found
     */
    public Var getVar(String name){
        int i = 0;
        Var v = null;
        Object[] vars = variableContexts.keySet().toArray();
        while (v == null && i < vars.length){
            if(((Var)vars[i]).getName().equals(name)){
                v = (Var)vars[i];
            }
            i++;
        }
        return v;
    }
    
    /**
     * given a Flowchart fc, returns a boolean for whether fc's structure is valid
     * 
     * @param fc Flowchart to validate
     * @return boolean for whether the structure of the Flowchart is valid
     */
    public Boolean validateStructure(Flowchart fc){
        
        whileStack = new Stack<>();
        //queue for the vertices of the flowchart
        Queue<Vertex> vertexQueue = new ArrayDeque<>();
        //add start vertex to queue
        vertexQueue.add(fc.getStartVertex());
        Boolean valid = true;
        Vertex currentVertex;
        //while there are still vertices to validate
        while(!vertexQueue.isEmpty() && valid) {
            currentVertex = vertexQueue.remove();
            if(currentVertex == null){
                valid = false;
                    break;
            }
            //if current vertex represents an if statement
            if(currentVertex.getController() instanceof IfStmtController){
                //get the vertex controller and store the current vertex in a temporary field
                IfStmtController currIfContr = (IfStmtController)currentVertex.getController();
                Vertex tempCurr = currentVertex;
                //validate that both true and false edges are set
                if(currIfContr.getTrueEdge() == null || currIfContr.getFalseEdge() == null){
                    valid = false;
                    break;
                }
                //validate each branch of the if statement
                currentVertex = validateIfBranch(currentVertex, currIfContr.getTrueEdge().getEdge().getController().getChild());
                if(currentVertex != validateIfBranch(tempCurr, currIfContr.getFalseEdge().getEdge().getController().getChild())){
                    valid = false;
                    if(currentVertex == null){
                        break;
                    }
                }
            }
            
            if(currentVertex.getController() instanceof WhileController){
                whileStack.push((WhileController)currentVertex.getController());
            }
            
            if(currentVertex.getController() instanceof EndWhileController){
                if(whileStack.isEmpty()){
                    valid = false;
                    break;
                }
                WhileController requiredWhile = whileStack.pop();
                if(requiredWhile != ((EndWhileController)currentVertex.getController()).getWhileCtrl()){
                    valid = false;
                }
            }
            
            //validate that the current vertex has the correct number of children and parents
            if(currentVertex.getParentVertices() == null || currentVertex.getController() == null 
                    || currentVertex.getChildVertices() == null || currentVertex.getController() == null){
                valid = false;
            } else if (currentVertex.getParentVertices().size() != currentVertex.getController().getMaxParents() ||
                    currentVertex.getChildVertices().size() != currentVertex.getController().getMaxChildren()) {
                valid = false;
            } else {
                vertexQueue.addAll(currentVertex.getChildVertices());
                if(currentVertex.getController() instanceof WhileController){
                    vertexQueue.remove(((WhileController)currentVertex.getController()).getEndWhile().getVertex());
                }
            }
        }
        if(!whileStack.isEmpty()){
            valid = false;
        }
        return valid;
    }
    
    /**
     * Given a vertex that represents an if statement ifVertex and a vertex that is a child of ifVertex, return the EndIf vertex
     * that corresponds to the if statement or null if this branch of the statement is invalid
     * 
     * @param ifVertex the if statement vertex of which the branch belongs to
     * @param startVertex a child of the if statement vertex that starts the branch being validated
     * @return the EndIf vertex of the if statement or null if the branch is invalid
     */
    private Vertex validateIfBranch(Vertex ifVertex, Vertex startVertex){
        //queue for vertices of the branch
        Queue<Vertex> vertexQueue = new ArrayDeque<>();
        //add the first vertex of the branch to the queue
        vertexQueue.add(startVertex);
        Boolean valid = true;
        Boolean endIfFound = false;
        Vertex currentVertex = null;
        //while there are still vertices that need validating
        while(!vertexQueue.isEmpty() && valid && !endIfFound) {
            //get the next vertex that needs validating
            currentVertex = vertexQueue.remove();
            //if the vertex represents an if statement
            if(currentVertex.getController() instanceof IfStmtController){
                IfStmtController currIfContr = (IfStmtController)currentVertex.getController();
                Vertex tempCurr = currentVertex;
                //validate that both true and false edges are set
                if(currIfContr.getTrueEdge() == null || currIfContr.getFalseEdge() == null){
                    valid = false;
                    break;
                }
                //validate each branch of the if statement
                currentVertex = validateIfBranch(currentVertex, currIfContr.getTrueEdge().getEdge().getController().getChild());
                if(currentVertex != validateIfBranch(tempCurr, currIfContr.getFalseEdge().getEdge().getController().getChild())){
                    valid = false;
                    if(currentVertex == null){
                        break;
                    }
                }
            }
            
            //validate that the current vertex has the correct number of children and parents
            if(currentVertex == null || currentVertex.getParentVertices() == null || currentVertex.getController() == null 
                    || currentVertex.getChildVertices() == null || currentVertex.getController() == null){
                valid = false;
                break;
            } else if (currentVertex.getParentVertices().size() != currentVertex.getController().getMaxParents() ||
                    currentVertex.getChildVertices().size() != currentVertex.getController().getMaxChildren()) {
                valid = false;
            } else {
                vertexQueue.addAll(currentVertex.getChildVertices());
            }
            if(currentVertex.getController() instanceof EndIfController){
                if(((EndIfController)currentVertex.getController()).getIfStmt().equals(ifVertex.getController())){
                    endIfFound = true;
                }
            }
        }
        
        //if the current vertex represents an EndIF then return it, otherwise the branch is invalid
        if(currentVertex.getController() == null){
            return null;
        }else if(currentVertex.getController() instanceof EndIfController && endIfFound && valid){
            return currentVertex;
        } else {
            return null;
        }
    }
    
    /**
     * given an AlertType alertType and a String message, display an 
     * Alert using these parameters
     * 
     * @param alertType the alert type of the desired alert
     * @param message the String message of the desired alert
     */
    private void showAlert(Alert.AlertType alertType, String message){
        //Platform.runLater(() -> {
            Alert customAlert = new Alert(alertType);
            customAlert.setContentText(message);
            customAlert.showAndWait();
        //});
    }
    
    /**
     * given a VarType type and a String value, attempt to return an Object with the corresponding given type and value
     * 
     * @param type the type of the object
     * @param value the value of the object
     * @return the created object or null if the value will not parse
     */
    public Object parseStringValue(VarType type, String value){
        switch(type){
            case BOOLEAN:
                //attempt to parse as a boolean
                if(value.equals("true")){
                    return true;
                } else if (value.equals("false")){
                    return false;
                } else{ 
                    System.out.println("Error: invalid value for a boolean: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for boolean variable: " + value);
                    return null;
                }
            
            case INTEGER:
                //attempt to parse as an integer
                try{
                    int i = Integer.valueOf(value);
                    return i;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for an integer: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid value for integer variable: " + value);
                    return null;
                }
            
            case DOUBLE:
                //attempt to parse as a double
                try{
                    double d = Double.valueOf(value);
                    return d;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a double: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid value for double variable: " + value);
                    return null;
                }
            
            case FLOAT:
                //attempt to parse as a float
                try{
                    if(value.matches("(^([+-]?\\d*\\.?\\d*)f$)|(^([+-]?\\d*)$)")){
                        float f = Float.valueOf(value);
                        return f;
                    }
                    System.out.println("Error: invalid value for a float: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for float variable: " + value 
                            + "\n(floats must end in an f if they have a decimal e.g. 2 or 2.1f)");
                    return null;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a float: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for float variable: " + value);
                    return null;
                }
            
            case LONG:
                //attempt to parse as a long
                try{
                    if(value.endsWith("l")){
                        value = value.substring(0, value.length() - 1);
                    }
                    long l = Long.valueOf(value);
                    return l;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a long: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for long variable: " + value);
                    return null;
                }
            
            case SHORT:
                //attempt to parse as a short
                try{
                    short s = Short.valueOf(value);
                    return s;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a short: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for short variable: " + value);
                    return null;
                }
            
            case STRING:
                //attempt to parse as a string
                if(value.matches("\"([^\"]*)\"")){
                    return value.substring(1, value.length() - 1);
                } else { 
                    System.out.println("Error: invalid value for a String: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for String variable: " + value 
                            + "\n(Strings must be in double quotations e.g. \"string\")");
                    return null;
                }
            
            case CHARACTER:
                //attempt to parse as a character
                if(value.matches("'.'")){
                    return value.charAt(1);
                } else{
                    System.out.println("Error: invalid value for a character: "
                            + "\n\t" + value);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for character variable: " + value 
                            + "\n(characters must be in single quotations e.g. 'c')");
                    return null;
                }
        }
        return null;
    }
    
    /**
     * given a UserInToVarController userInToVar, execute the actions described by the controller
     * 
     * @param userInToVar the UserInToVarController that details the actions to execute
     * @return boolean to represent whether the execution was successful
     */
    private boolean runUserInToVar(UserInToVarController userInToVar){
        //get the user input for the variables value using a text input dialog
        TextInputDialog userInDialog = new TextInputDialog();
        userInDialog.setHeaderText("Provide a " + userInToVar.getType().toString().toLowerCase() + " value for variable " + userInToVar.getName());
        userInDialog.setTitle("User Input To Variable");
        Optional<String> result = userInDialog.showAndWait();
        //if the user gave a value
        if (result.isPresent()) {
            //attempt to create a variable using the type from userInToVar and the value provided by the user 
            Object inputObject = parseUserInput(userInToVar.getType(), userInDialog.getEditor().getText());
            if(inputObject == null){
                return false;
            } else {
                //attempt to add the newly created variable
                if(addVar(userInToVar.getType(),userInToVar.getName(),inputObject) != null){
                    System.out.println("new var added:"
                        + "\n\ttype: " + userInToVar.getType()
                        + "\n\tname: " + userInToVar.getName()
                        + "\n\tvalue: " + inputObject);
                    return true;
                } else {
                    System.out.println("Error: variable already exists: "
                            + "\n\t" + userInToVar.getName());
                    showAlert(Alert.AlertType.ERROR, "Variable " + userInToVar.getName() + " already exists");
                    return false;
                }
            }
        //otherwise exit erroneously
        } else {
            System.out.println("Error: user failed to input.");
            showAlert(Alert.AlertType.ERROR, "No input provided");
            return false;
        }
    }
    
    /**
     * given a VarType type and a String input provided by a user at runtime, 
     * attempt to return an Object with the corresponding given type and input value
     * 
     * @param type the type of the object
     * @param value the value of the object
     * @return the created object or null if the value will not parse
     */
    private Object parseUserInput(VarType type, String input){
        switch(type){
            case BOOLEAN:
                //attempt to parse as a boolean
                return Boolean.valueOf(input);
            
            case INTEGER:
                //parse using parseStringValue()
                return parseStringValue(type, input);
            
            case DOUBLE:
                //parse using parseStringValue()
                return parseStringValue(type, input);
            
            case FLOAT:
                //attempt to parse as a float
                try{
                    float f = Float.valueOf(input);
                    return f;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a float: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for float variable");
                    return null;
                }
            
            case LONG:
                //attempt to parse as a long
                try{
                    long l = Long.valueOf(input);
                    return l;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a long: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for long variable");
                    return null;
                }
            
            case SHORT:
                //parse using parseStringValue()
                return parseStringValue(type, input);
            
            case STRING:
                //simply return input
                return input;
                
            case CHARACTER:
                //attempt to parse as a character
                if(input.matches(".")){
                    return input.charAt(0);
                } else {
                    System.out.println("Error: invalid value for a character: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for char variable");
                    return null;
                }
        }
        return null;
    }

    /**
     * given a VarDecController varDecContr, execute the actions described by the controller
     * 
     * @param varDecContr the VarDecController that details the actions to execute
     * @return boolean to represent whether the execution was successful
     */
    private boolean runVarDec(VarDecController varDecContr) {
        //if the variable is declared using an expression
        if(varDecContr.isUsingExpr()){
            //create an expression evaluator to evaluate the expression
            ExpressionEvaluator varDecEval = new ExpressionEvaluator(getVariables(), varDecContr.getValue(), false);
            Object evalResult;
            //change the System.err to capture any user created errors
            PrintStream originalSysErr = System.err;
            errorStrings = new ArrayList<>();
            System.setErr(userErrPrintStream);
            //evaluate the expression
            try{
                evalResult = varDecEval.eval();
            } catch (UserCreatedExprException e){
                userErrReportDialog.display(errorStrings);
                return false;
            } finally {
                //revert System.err to its original print stream
                System.setErr(originalSysErr);
            }
            
            //attempt to parse the resulting value to the required type
            Object givenObject = parseExprObjectValue(varDecContr.getType(), evalResult, varDecContr.getValue());
            if(givenObject == null){
                return false;
            } else {
                //attempt to add the variables to the variable list
                if(addVar(varDecContr.getType(),varDecContr.getName(),givenObject) != null){
                    System.out.println("new var added:"
                        + "\n\ttype: " + varDecContr.getType()
                        + "\n\tname: " + varDecContr.getName()
                        + "\n\tvalue: " + givenObject);
                    return true;
                } else {
                    System.out.println("Error: variable already exists: "
                            + "\n\t" + varDecContr.getName());
                    showAlert(Alert.AlertType.ERROR, "Variable " + varDecContr.getName() + " already exists");
                    return false;
                }
            }
        } else {
            //attempt to parse the given value to the required type
            Object givenObject = parseStringValue(varDecContr.getType(), varDecContr.getValue());
            if(givenObject == null){
                return false;
            } else {
                //attempt to add the variables to the variable list
                variableContexts.put(new Var(varDecContr.getType(), varDecContr.getName(), givenObject), currentContext);
                System.out.println("new var added:"
                        + "\n\ttype: " + varDecContr.getType()
                        + "\n\tname: " + varDecContr.getName()
                        + "\n\tvalue: " + givenObject);
                return true;
            }
        }
    }
    
    /**
     * given a VarType type, Object value and String expr attempt to return an Object with the 
     * corresponding given type and input value, using casting
     * 
     * @param type the type of the object
     * @param value the value of the object
     * @param expr the expression that returns the value of the value variable
     * @return the created object or null if the value will not parse
     */
    public Object parseExprObjectValue(VarType type, Object value, String expr){
        switch(type){
            case BOOLEAN:
                //attempt to cast to a boolean
                try{
                    boolean b = (boolean)value;
                    return b;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a boolean: "
                            + "\n\t" + expr);
                    showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to boolean: " + expr);
                    return null; 
                }
            
            case INTEGER:
                //attempt to cast to an integer
                try{
                    int i = (int)value;
                    return i;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for an integer: "
                            + "\n\t" + expr);
                    showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to integer: " + expr);
                    return null; 
                }
            
            case DOUBLE:
                //attempt to cast to a double
                try{
                    double d = (double)value;
                    return d;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a double: "
                            + "\n\t" + expr);
                    showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to double: " + expr);
                    return null; 
                }
            
            case FLOAT:
                //attempt to cast to a float
                try{
                    float f = (float)value;
                    return f;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a float: "
                            + "\n\t" + expr);
                    showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to float: " + expr);
                    return null;
                }
            
            case LONG:
                //attempt to cast to a long
                try{
                    long l = (long)value;
                    return l;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a long: "
                            + "\n\t" + expr);
                    showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to float: " + expr);
                    return null; 
                }
            
            case SHORT:
                //attempt to cast to a short
                try{
                    short s = (short)value;
                    return s;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a short: "
                            + "\n\t" + expr);
                   showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to short: " + expr);
                   return null; 
                }
            
            case STRING:
                //attempt to cast to a String
                try{
                    String s = (String)value;
                    return s;
                } catch (ClassCastException e){
                    System.out.println("Error: invalid expression for a String: "
                            + "\n\t" + expr);
                   showAlert(Alert.AlertType.ERROR, "Expression does not evaluate to String: " + expr);
                   return null; 
                }
            
            case CHARACTER:
                //always fail for character types
                System.out.println("Error: attempted use of expression for a character.");
                showAlert(Alert.AlertType.ERROR, "Expressions cannot be used for characters.");
                return null;
        }
        return null;
    }
    
    /**
     * given a OutputController outputContr, execute the actions described by the controller
     * 
     * @param outputContr the OutputController that details the actions to execute
     * @return boolean to represent whether the execution was successful
     */
    private boolean runOutput(OutputController outputContr) {
        //create an expression evaluator to evaluate the expression
        ExpressionEvaluator outputEval = new ExpressionEvaluator(getVariables(), outputContr.getValue(), true);
        //change the System.err to capture any user created errors and change the output stream to capture
        //outputs resulting from this controller
        PrintStream originalSysErr = System.err;
        PrintStream originalSysOut = System.out;
        errorStrings = new ArrayList<>();
        outputStrings = new Stack<>();
        System.setErr(userErrPrintStream);
        System.setOut(flowJavaOutPrintStream);
        //evaluate the expression
        try {
            outputEval.eval();
            showAlert(Alert.AlertType.INFORMATION, "Program Outputted: \n" + outputStrings.pop());
        } catch (UserCreatedExprException e) {
            userErrReportDialog.display(errorStrings);
            return false;
        } finally {
            //revert System.err and System.out to their original print streams
            System.setErr(originalSysErr);
            System.setOut(originalSysOut);
        }
        return true;
    }
    
    /**
     * given a String name and String value, attempt to update a variable with the 
     * given name using the provided value
     * 
     * @param name the name of the variable to update
     * @param value the new value to assign to the variable
     * @return boolean representing whether the update was successful
     */
    private boolean updateVariable(String name, String value){
        //try and fetch the variable using the name
        Var v = getVar(name);
        if(v == null){
            System.out.println("Error: attempted to update non-existant var.");
            showAlert(Alert.AlertType.ERROR, "Variable " + name + " does not exist.\n(if defined check context)");
            return false;
        } else {
            //create an expression evaluator to evaluate the update expression value
            ExpressionEvaluator updateVarEval = new ExpressionEvaluator(getVariables(), value, false);
            Object evalResult;
            //change the System.err to capture any user created errors
            PrintStream originalSysErr = System.err;
            errorStrings = new ArrayList<>();
            System.setErr(userErrPrintStream);
            //evaluate the expression
            try{
                evalResult = updateVarEval.eval();
            } catch (UserCreatedExprException e){
                userErrReportDialog.display(errorStrings);
                return false;
            } finally {
                System.setErr(originalSysErr);
            }
            //attempt to parse the value to the variables original type
            Object exprResObject = parseExprObjectValue(v.getType(), evalResult, value);
            if(exprResObject != null){
                //update the variable  
                v.setValue(evalResult);
                return true;
            }
        }
        return false;
    }

    /**
     * given a VarAssignController varAssignController, execute the actions described by the controller
     * 
     * @param varAssignController the VarAssignController that details the actions to execute
     * @return boolean to represent whether the execution was successful
     */
    private boolean runVarAssign(VarAssignController varAssignController) {
        //attempt to update the specified variable with the specified value
        if(updateVariable(varAssignController.getVarName(), varAssignController.getValue())){
            System.out.println("var updated:"
                        + "\n\ttype: " + getVar(varAssignController.getVarName()).getType()
                        + "\n\tname: " + varAssignController.getVarName()
                        + "\n\tvalue: " + getVar(varAssignController.getVarName()).getValue());
            return true;
        } else {
            System.out.println("failed to assign variable");
            return false;
        }   
    }
    
    /**
     * given a IfStmtController ifStmtController, execute the actions described by the controller
     * 
     * @param ifStmtController the IfStmtController that details the actions to execute
     * @return boolean to represent whether the execution was successful
     */
    private VertexController runIfStmt(IfStmtController ifStmtController){
        //create an expression evaluator to evaluate the expression
        ExpressionEvaluator ifEval = new ExpressionEvaluator(getVariables(), ifStmtController.getExpr(), false);
        Object evalResult;
        //change the System.err to capture any user created errors
        PrintStream originalSysErr = System.err;
        errorStrings = new ArrayList<>();
        System.setErr(userErrPrintStream);
        //evaluate the expression
        try {
            evalResult = ifEval.eval();
        } catch (UserCreatedExprException e) {
            userErrReportDialog.display(errorStrings);
            return null;
        } finally {
            System.setErr(originalSysErr);
        }
        //try and parse the resulting value as a boolean
        Object exprResObject = parseExprObjectValue(VarType.BOOLEAN, evalResult, ifStmtController.getExpr());
        if (exprResObject != null) {
            //return a child controller based on the result of the expression evaluation
            if((boolean)exprResObject){
                System.out.println("Expression evaluated to: True.");
                return ifStmtController.getTrueEdge().getEdge().getController().getChild().getController();
            } else {
                System.out.println("Expression evaluated to: False.");
                return ifStmtController.getFalseEdge().getEdge().getController().getChild().getController();
            }
        }
        return null;
    }
    
    private VertexController runWhile(WhileController WhileCtrl){
        //create an expression evaluator to evaluate the expression
        ExpressionEvaluator ifEval = new ExpressionEvaluator(getVariables(), WhileCtrl.getExpr(), false);
        Object evalResult;
        //change the System.err to capture any user created errors
        PrintStream originalSysErr = System.err;
        errorStrings = new ArrayList<>();
        System.setErr(userErrPrintStream);
        //evaluate the expression
        try {
            evalResult = ifEval.eval();
        } catch (UserCreatedExprException e) {
            userErrReportDialog.display(errorStrings);
            return null;
        } finally {
            System.setErr(originalSysErr);
        }
        //try and parse the resulting value as a boolean
        Object exprResObject = parseExprObjectValue(VarType.BOOLEAN, evalResult, WhileCtrl.getExpr());
        if (exprResObject != null) {
            //return a child controller based on the result of the expression evaluation
            if((boolean)exprResObject){
                System.out.println("Expression evaluated to: True.");
                return WhileCtrl.getTrueEdge().getEdge().getController().getChild().getController();
            } else {
                System.out.println("Expression evaluated to: False.");
                return WhileCtrl.getEndWhile().getVertex().getChildVertices().get(0).getController();
            }
        }
        return null;
    }
    
    public ArrayList<Var> getVariablesInContext(int context){
        ArrayList<Var> variables = new ArrayList<>();
        for(Var v: variableContexts.keySet()){
            if(variableContexts.get(v) == context){
                variables.add(v);
            }
        }
        return variables;
    }
    
    public ArrayList<Var> getVariables(){
        ArrayList<Var> vars = new ArrayList<>();
            for(Var v: variableContexts.keySet()){
                vars.add(v);
            }
        return vars;
    }

    public String convertToJava(Flowchart fc, String progName, Boolean forRun, Boolean forSyntaxCheck, ArrayList<FunctionFlowchart> functions) {
        String javaProg = "";
        String functionName = "";
        if (fc instanceof FunctionFlowchart) {
            functionName = ((FunctionFlowchart) fc).getName();
            FunctionFlowchart fFc = (FunctionFlowchart) fc;
            if (forSyntaxCheck) {
                javaProg += "import javafx.stage.Stage;"
                        + "\nimport java.util.Optional;\nimport javafx.scene.control.Alert;\nimport javafx.scene.control.TextInputDialog;\n"
                        + "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n\npublic class "
                        + progName + "{"
                        + "\n\tBufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tString userInputString;";

            }
            if(forRun){
            javaProg += "\n\tpublic " + functionTypeToString(fFc.getReturnType()) + " "
                    + fFc.getName() + "(";
            } else {
                javaProg += "\n\tpublic static " + functionTypeToString(fFc.getReturnType()) + " "
                    + fFc.getName() + " (";
            }
            if (!fFc.getParameters().isEmpty()) {
                for (Var v : fFc.getParameters()) {
                    javaProg += functionTypeToString(v.getType()) + " " + v.getName() + ", ";
                }
                javaProg = javaProg.substring(0, javaProg.length() - 2);
            }
            javaProg += ") throws IOException{\n";
        } else {
            if (forRun) {
                javaProg += "import javafx.stage.Stage;"
                        + "\nimport java.util.Optional;\nimport javafx.scene.control.Alert;\nimport javafx.scene.control.TextInputDialog;\n"
                        + "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n\npublic class "
                        + progName + " extends javafx.application.Application{"
                        + "\n\tBufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tString userInputString;\n"
                        + "\n\t@Override"
                        + "\n\tpublic void start(Stage primaryStage) throws IOException {run();}"
                        + "\n\tpublic void run() throws IOException{\n";

            } else {
                javaProg += "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n\npublic class "
                        + progName + "{" 
                        + "\n\tBufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tString userInputString;"
                        + "\n\tpublic static void main(String[] args) throws IOException{\n";
            }
        }
        tabIndex = 2;
        
        //stack to hold the vertices that need to be converted
        Stack<VertexController> controllerStack = new Stack<>();
        //the controller of the flowchart node currently being converted, initialise as the child of the start node
        VertexController currentController = fc.getStartVertex().getChildVertices().get(0).getController();
        //push start node onto stack
        controllerStack.push(currentController);
        
        //while there are still nodes that need converting
        while(!controllerStack.isEmpty()) {
            //get next node that needs to be converted
            currentController = controllerStack.pop();
            //add lines of java to javaProg based on the class of the controller
            if(currentController instanceof UserInToVarController){
                javaProg += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof VarDecController){
                javaProg += convertVarDec((VarDecController)currentController, tabIndex);
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof OutputController){
                if(forRun){
                    javaProg += repeatString("\t",tabIndex) + "showAlert(Alert.AlertType.INFORMATION, " + "\"Program Outputted: \" + " + ((OutputController)currentController).getValue() + ");\n";
                } else {
                    javaProg += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getValue() + ");\n";
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                javaProg += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getValue() + ";\n";
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                javaProg += convertIf(currIfStmt, tabIndex, forRun, functionName);
                controllerStack.push(currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                javaProg += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                controllerStack.push(currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(currRec.getVariableForValue() == null){
                    javaProg += repeatString("\t",tabIndex) +((FunctionFlowchart)fc).getName() + "(" + currRec.getParameterVals() + ");\n";
                } else {
                    javaProg += repeatString("\t",tabIndex) + currRec.getVariableForValue() + " = " + ((FunctionFlowchart)fc).getName() + "(" + currRec.getParameterVals() + ");\n";
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof FunctInvokeController){
                javaProg += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof ArrayDecController){
                javaProg += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                javaProg += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                controllerStack.push(currFor.getEndFor().getVertex().getChildVertices().get(0).getController());
            }
            
        }
        
        if(fc instanceof FunctionFlowchart){
            FunctionFlowchart fFc = (FunctionFlowchart) fc;
            if (fFc.getRetunVal().isEmpty()){
                javaProg += "\t\treturn;\n";
            } else {
                javaProg += "\t\treturn " + fFc.getRetunVal() + ";\n";
            }
        }
        
        if((!(fc instanceof FunctionFlowchart) && forRun) || ((fc instanceof FunctionFlowchart) && forSyntaxCheck)) {
            
            javaProg += "\t}\n\tpublic String getUserInput(String type, String name){"
                    + "\n\t\tTextInputDialog userInDialog = new TextInputDialog();\n"
                    + "\t\tuserInDialog.setHeaderText(\"Provide a \" + type + \" value for variable \" + name);\n"
                    + "\t\tuserInDialog.setTitle(\"User Input To Variable\");\n"
                    + "\t\tOptional<String> result = userInDialog.showAndWait();\n"
                    + "\t\tif (result.isPresent()) {\n"
                    + "\t\t\treturn userInDialog.getEditor().getText();\n"
                    + "\t\t} else {\n"
                    + "\t\t\tshowAlert(Alert.AlertType.ERROR, \"No input provided\");\n"
                    + "\t\t\treturn null;\n"
                    + "\t\t}\n\t}"
                    + "\n\tprivate void showAlert(Alert.AlertType alertType, String message){\n"
                    + "\t\tAlert customAlert = new Alert(alertType);\n"
                    + "\t\tcustomAlert.setContentText(message);\n"
                    + "\t\tcustomAlert.showAndWait();\n"
                    + "\t}";
            
            if(fc instanceof FunctionFlowchart){
                for (FunctionFlowchart fFc : functions) {
                    javaProg += convertToJava(fFc, "", forRun, false, null);
                }
            }
        } else {
            javaProg += "\t}";
        }
        
        for(FunctionFlowchart fFc: fc.getFunctions()){
            javaProg += convertToJava(fFc, "", forRun, false, null);
        }
        if(!(fc instanceof FunctionFlowchart) || (fc instanceof FunctionFlowchart && forSyntaxCheck)){
            javaProg += "\n}";
        }
        return javaProg;
    }
    
    private String convertUserInToVar(UserInToVarController ctrl, int tabIndex, boolean forRun){
        String java = ""; 
        if(forRun){
            java += repeatString("\t",tabIndex) + "userInputString = getUserInput(\"" + ctrl.getType().toString().toLowerCase() + "\", \"" + ctrl.getName() + "\");\n";
        } else {
            java += repeatString("\t",tabIndex) + "userInputString = userInputBr.readLine();\n";
        }
        switch (ctrl.getType()){
            case STRING:
                java += repeatString("\t",tabIndex) + "String " + ctrl.getName() + " = userInputString;\n";
                break;
            case BOOLEAN: 
                java += repeatString("\t",tabIndex) + "Boolean " + ctrl.getName() + " = Boolean.parseBoolean(userInputString);\n";
                break;
            case CHARACTER:
                java += repeatString("\t",tabIndex) + "Character " + ctrl.getName() + " = userInputString.charAt(0);\n";
                break;
            case INTEGER:
                java += repeatString("\t",tabIndex) + "Integer " + ctrl.getName() + " = Integer.parseInt(userInputString);\n";
                break;
            case DOUBLE:
                java += repeatString("\t",tabIndex) + "Double " + ctrl.getName() + " = Double.parseDouble(userInputString);\n";
                break;
            case FLOAT:
                java += repeatString("\t",tabIndex) + "Float " + ctrl.getName() + " = Float.parseFloat(userInputString);\n";
                break;
            case LONG:
                java += repeatString("\t",tabIndex) + "Long " + ctrl.getName() + " = Long.parseLong(userInputString);\n";
                break;
            case SHORT:
                java += repeatString("\t",tabIndex) + "Short " + ctrl.getName() + " = Short.parseShort(userInputString);\n";
                break;
        }
        return java;
    }
    
    private String convertVarDec(VarDecController ctrl, int tabIndex){
        String java = "";
        switch (ctrl.getType()){
            case STRING:
                java += repeatString("\t",tabIndex) + "String " + ctrl.getName() + " = ";
                break;
            case BOOLEAN: 
                java += repeatString("\t",tabIndex) + "Boolean " + ctrl.getName() + " = ";
                break;
            case CHARACTER:
                java += repeatString("\t",tabIndex) + "Character " + ctrl.getName() + " = ";
                break;
            case INTEGER:
                java += repeatString("\t",tabIndex) + "Integer " + ctrl.getName() + " = ";
                break;
            case DOUBLE:
                java += repeatString("\t",tabIndex) + "Double " + ctrl.getName() + " = ";
                break;
            case FLOAT:
                java += repeatString("\t",tabIndex) + "Float " + ctrl.getName() + " = ";
                break;
            case LONG:
                java += repeatString("\t",tabIndex) + "Long " + ctrl.getName() + " = ";
                break;
            case SHORT:
                java += repeatString("\t",tabIndex) + "Short " + ctrl.getName() + " = ";
                break;
        }
        java += ctrl.getValue() + ";\n";
        return java;
    }

    private String repeatString(String s, int i){
        return new String(new char[i]).replace("\0", s);
    }
    
    private String convertIf(IfStmtController ctrl, int tabIndex, boolean forRun, String functionName){
        String java = repeatString("\t",tabIndex) + "if(" + ctrl.getExpr() + "){\n";
        tabIndex ++;
        java += convertIfBranch(ctrl.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName) + repeatString("\t",tabIndex-1) + "}\n" + repeatString("\t",tabIndex-1) + "else{\n";
        java += convertIfBranch(ctrl.getFalseEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName) + repeatString("\t",tabIndex-1) + "}\n";
        return java;
    }
    
    private String convertIfBranch(VertexController currentController, int tabIndex, boolean forRun, String functionName){
        String java = "";
        
        while(!(currentController instanceof EndIfController)){
            //add lines of java to javaProg based on the class of the controller
            if(currentController instanceof UserInToVarController){
                java += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof VarDecController){
                java += convertVarDec((VarDecController)currentController, tabIndex);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof OutputController){
                if(forRun){
                    java += repeatString("\t",tabIndex) + "showAlert(Alert.AlertType.INFORMATION, " + "\"Program Outputted: \"+" + ((OutputController)currentController).getValue() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getValue() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getValue() + ";\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(currRec.getVariableForValue() == null){
                    java += repeatString("\t",tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                java += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java;
    }
    
    private String convertWhileBody(VertexController currentController, int tabIndex, boolean forRun, String functionName){
        String java = "";
        tabIndex ++;
        while(!(currentController instanceof EndWhileController)){
            //add lines of java to javaProg based on the class of the controller
            if(currentController instanceof UserInToVarController){
                java += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof VarDecController){
                java += convertVarDec((VarDecController)currentController, tabIndex);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof OutputController){
                if(forRun){
                    java += repeatString("\t",tabIndex) + "showAlert(Alert.AlertType.INFORMATION, " + "\"Program Outputted: \"+" + ((OutputController)currentController).getValue() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getValue() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getValue() + ";\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(currRec.getVariableForValue() == null){
                    java += repeatString("\t",tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                java +=  repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java + repeatString("\t",tabIndex-1) + "}\n";
    }
    
    private String convertForBody(VertexController currentController, int tabIndex, boolean forRun, String functionName){
        String java = "";
        tabIndex ++;
        while(!(currentController instanceof EndForController)){
            //add lines of java to javaProg based on the class of the controller
            if(currentController instanceof UserInToVarController){
                java += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof VarDecController){
                java += convertVarDec((VarDecController)currentController, tabIndex);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof OutputController){
                if(forRun){
                    java += repeatString("\t",tabIndex) + "showAlert(Alert.AlertType.INFORMATION, " + "\"Program Outputted: \"+" + ((OutputController)currentController).getValue() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getValue() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getValue() + ";\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(currRec.getVariableForValue() == null){
                    java += repeatString("\t",tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                } else {
                    java += repeatString("\t",tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                java +=  repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java + repeatString("\t",tabIndex-1) + "}\n";
    }
    
     public void compileConverted(String className, String methodName, String classCode, Boolean run) throws UserCreatedExprException{ 
        //create a hash map to use as a class cache for the java compiler
        Map<String, ByteArrayOutputStream> classCache = new HashMap<>();
        //get a compiler using Java X tool provider
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        //if the compiler could not be retrived then the class cannot be run
        if (compiler == null){
            throw new RuntimeException("Could not get a compiler. "
                    + "Please use a JDK when using this application: https://www.oracle.com/uk/java/technologies/javase/javase8-archive-downloads.html");
        }
        
        //retrive the file manager from the compiler
        StandardJavaFileManager sfm  = compiler.getStandardFileManager(null, null, null);
        //create a forwarding java file manager using the compiler file manager
        ForwardingJavaFileManager<StandardJavaFileManager> fjfm = new ForwardingJavaFileManager<StandardJavaFileManager>(sfm){
            @Override
            public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
                    throws IOException{
                if (StandardLocation.CLASS_OUTPUT == location && JavaFileObject.Kind.CLASS == kind)
                    return new SimpleJavaFileObject(URI.create("mem:///" + className + ".class"), JavaFileObject.Kind.CLASS){
                        @Override
                        public OutputStream openOutputStream()
                                throws IOException{
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            classCache.put(className, baos);
                            return baos;
                        }
                    };
                else
                    throw new IllegalArgumentException("Unexpected output file requested: " + location + ", " + className + ", " + kind);
            }
        };
        
        //create a file list and add a simple java file object that returns the created class when accessed
        List<JavaFileObject> files = new LinkedList<JavaFileObject>(){{
            add(
                new SimpleJavaFileObject(URI.create("string:///" + className + ".java"), JavaFileObject.Kind.SOURCE){
                    @Override
                    public CharSequence getCharContent(boolean ignoreEncodingErrors){
                        return classCode;
                    }
                }
            );
        }};
        
        //compile the class using the compiler
        compiler.getTask(null, fjfm, null, null, null, files).call();
        try{
            //load the created class for invokation
            Class<?> clarse = new ClassLoader(){
                @Override
                public Class<?> findClass(String name){
                    if (!name.startsWith(className)){
                        throw new IllegalArgumentException("This class loader is for " + className + " - could not handle \"" + name + '"');
                    }
                    byte[] bytes = classCache.get(name).toByteArray();
                    return defineClass(name, bytes, 0, bytes.length);
                }
            }.loadClass(className);
            
            if(run){
                //invoke the created class
                clarse.getMethod(methodName).invoke(clarse.newInstance());
            }
            
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException x){
            //if an exception arose from the user created expression then throw a custom exception
            if(x instanceof NullPointerException){
                throw new UserCreatedExprException("Error in user created expression");
            } else {
                throw new RuntimeException("Run failed: " + x, x);
            }
        }
    }
    
    private String functionTypeToString(VarType v){
        if(v == null){
            return "void";
        } else {
            return v.toString().charAt(0) + v.toString().substring(1).toLowerCase();
        }
    }
    
}
