/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author cwood
 */
public class ProgramRunner {
    private ArrayList<Var> variables;
    private ArrayList<String> errorStrings;
    private Stack<String> outputStrings;
    private PrintStream userErrPrintStream;
    private PrintStream flowJavaOutPrintStream;
    private UserErrReportDialog userErrReportDialog;
    
    public void runProgram(Flowchart fc){
        userErrReportDialog = new UserErrReportDialog();
        variables = new  ArrayList<>();
        
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
        
        if(!validateStructure(fc)){
            showAlert(Alert.AlertType.ERROR, "Invalid Program Structure");
            return;
        }
        
        Stack<VertexController> controllerStack = new Stack<>();
        VertexController currentController = fc.getStartVertex().getController();
        controllerStack.push(currentController);
        
        while(!controllerStack.isEmpty()) {
            currentController = controllerStack.pop();
            if (currentController instanceof Terminal){
                if(((Terminal)currentController).getIsStart()){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                    System.out.println("------------------");
                    System.out.println("start.");
                } else {
                    System.out.println("finish.");
                    System.out.println("------------------");}
            } else if(currentController instanceof UserInToVarController){
                System.out.println("run UserInToVar.");
                if(runUserInToVar((UserInToVarController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof VarDecController){
                System.out.println("run VarDec.");
                if(runVarDec((VarDecController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof OutputController){
                System.out.println("run Ouput.");
                if(runOutput((OutputController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            } else if(currentController instanceof VarAssignController){
                System.out.println("run VarAssign.");
                if(runVarAssign((VarAssignController)currentController)){
                    controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
                } else {
                    System.out.println("terminanted erroneously.");
                    System.out.println("------------------");
                    showAlert(Alert.AlertType.ERROR, "Run Failed");
                    return;
                }
            }
        }
        showAlert(Alert.AlertType.INFORMATION, "Program Run Complete");
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
        //if variable already exists return false
        if(getVar(name) != null){
            return null;
        }
        //otherwise add the variable and return true
        else {
            Var v = new Var(type,name,value);
            variables.add(new Var(type,name,value));
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
        while (v == null && i < variables.size()){
            if(variables.get(i).getName().equals(name)){
                v = variables.get(i);
            }
            i++;
        }
        return v;
    }
    
    public Boolean validateStructure(Flowchart fc){
        Queue<Vertex> vertexQueue = new ArrayDeque<>();
        vertexQueue.add(fc.getStartVertex());
        Boolean valid = true;
        while(!vertexQueue.isEmpty() && valid) {
            Vertex currentVertex = vertexQueue.remove();
            if (currentVertex.getParentVertices().size() != currentVertex.getController().getMaxParents() ||
                    currentVertex.getChildVertices().size() != currentVertex.getController().getMaxChildren()) {
                valid = false;
            } else {
                vertexQueue.addAll(currentVertex.getChildVertices());
            }
        }
        return valid;
    }
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
    }
    
    public Object parseStringValue(VarType type, String value){
        switch(type){
            case BOOLEAN:
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
    
    private boolean runUserInToVar(UserInToVarController userInToVar){
        TextInputDialog userInDialog = new TextInputDialog();
        userInDialog.setHeaderText("Provide a "+ userInToVar.getType().toString().toLowerCase() +" value for variable " + userInToVar.getName());
        userInDialog.setTitle("User Input To Variable");
        Optional<String> result = userInDialog.showAndWait();
        if (result.isPresent()) {
            Object inputObject = parseUserInput(userInToVar.getType(), userInDialog.getEditor().getText());
            if(inputObject == null){
                return false;
            } else {
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
        } else {
            System.out.println("Error: user failed to input.");
            showAlert(Alert.AlertType.ERROR, "No input provided");
            return false;
        }
    }
    
    private Object parseUserInput(VarType type, String input){
        switch(type){
            case BOOLEAN:
                    return Boolean.valueOf(input);
            
            case INTEGER:
                try{
                    int i = Integer.valueOf(input);
                    return i;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for an integer: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for integer variable");
                    return null;
                }
            
            case DOUBLE:
                try{
                    double d = Double.valueOf(input);
                    return d;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a double: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for double variable");
                    return null;
                }
            
            case FLOAT:
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
                try{
                    short s = Short.valueOf(input);
                    return s;
                } catch (NumberFormatException e){
                    System.out.println("Error: invalid value for a short: "
                            + "\n\t" + input);
                    showAlert(Alert.AlertType.ERROR, "Invalid input for short variable");
                    return null;
                }
            
            case STRING:
                return input;
                
            case CHARACTER:
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

    private boolean runVarDec(VarDecController varDecContr) {
        if(varDecContr.isUsingExpr()){
            ExpressionEvaluator varDecEval = new ExpressionEvaluator(variables, varDecContr.getValue(), false);
            Object evalResult;
            PrintStream originalSysErr = System.err;
            errorStrings = new ArrayList<>();
            System.setErr(userErrPrintStream);
            try{
                evalResult = varDecEval.eval();
            } catch (UserCreatedExprException e){
                userErrReportDialog.display(errorStrings);
                return false;
            } finally {
                System.setErr(originalSysErr);
            }
            Object givenObject = parseExprObjectValue(varDecContr.getType(), evalResult, varDecContr.getValue());
            if(givenObject == null){
                return false;
            } else {
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
            Object givenObject = parseStringValue(varDecContr.getType(), varDecContr.getValue());
            if(givenObject == null){
                return false;
            } else {
                variables.add(new Var(varDecContr.getType(), varDecContr.getName(), givenObject));
                System.out.println("new var added:"
                        + "\n\ttype: " + varDecContr.getType()
                        + "\n\tname: " + varDecContr.getName()
                        + "\n\tvalue: " + givenObject);
                return true;
            }
        }
    }
    
    public Object parseExprObjectValue(VarType type, Object value, String expr){
        switch(type){
            case BOOLEAN:
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
                System.out.println("Error: attempted use of expression for a character.");
                showAlert(Alert.AlertType.ERROR, "Expressions cannot be used for characters.");
                return null;
        }
        return null;
    }
    
    private boolean runOutput(OutputController outputContr) {
        ExpressionEvaluator varDecEval = new ExpressionEvaluator(variables, outputContr.getValue(), true);
        PrintStream originalSysErr = System.err;
        PrintStream originalSysOut = System.out;
        errorStrings = new ArrayList<>();
        outputStrings = new Stack<>();
        System.setErr(userErrPrintStream);
        System.setOut(flowJavaOutPrintStream);
        try {
            varDecEval.eval();
            showAlert(Alert.AlertType.INFORMATION, "Program Outputted: \n" + outputStrings.pop());
        } catch (UserCreatedExprException e) {
            userErrReportDialog.display(errorStrings);
            return false;
        } finally {
            System.setErr(originalSysErr);
            System.setOut(originalSysOut);
        }
        return true;
    }
    
    private boolean updateVariable(String name, String value){
        Var v = getVar(name);
        if(v == null){
            System.out.println("Error: attempted to update non-existant var.");
            showAlert(Alert.AlertType.ERROR, "Variable " + name + " does not exist.");
            return false;
        } else {
            ExpressionEvaluator varDecEval = new ExpressionEvaluator(variables, value, false);
            Object evalResult;
            PrintStream originalSysErr = System.err;
            errorStrings = new ArrayList<>();
            System.setErr(userErrPrintStream);
            try{
                evalResult = varDecEval.eval();
            } catch (UserCreatedExprException e){
                userErrReportDialog.display(errorStrings);
                return false;
            } finally {
                System.setErr(originalSysErr);
            }
            Object exprResObject = parseExprObjectValue(v.getType(), evalResult, value);
            if(exprResObject != null){
                v.setValue(evalResult);
                return true;
            }
        }
        return false;
    }

    private boolean runVarAssign(VarAssignController varAssignController) {
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
}
