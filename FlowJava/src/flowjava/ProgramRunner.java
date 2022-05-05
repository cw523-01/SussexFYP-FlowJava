package flowjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.util.concurrent.Semaphore;
import javafx.stage.Modality;


/**
 * Used to convert flowcharts to Java programs then compile and/or run them
 *
 * @author cwood
 */
public class ProgramRunner {
    //List of strings to hold output by System.err during user program run
    private ArrayList<String> errorStrings;
    //print stream to divert errors caused by users
    private PrintStream userErrPrintStream;
    //dialog to display user caused errors
    private UserErrReportDialog userErrReportDialog;
    //a stack to keep track of while loops for flowchart structure validation
    private Stack<WhileController> whileStack;
    //a stack to keep track of for loops for flowchart structure validation
    private Stack<ForLoopController> forStack;
    //how many tabs should be inserted before each line of code during code conversion
    private int tabIndex;
    //unknown class for user created programs, run using reflection
    private Class<?> userProgClass;
    //standarf java file manager for the compiler used to compile user created programs
    private StandardJavaFileManager sfm;
    //semaphore for controlling thread execution order
    private Semaphore semaphore = new Semaphore(0);
    
    /**
     * Given a Flowchart fc, converts the program to java then compiles the program,
     * what the method does with the compiled program is determined by the forRun and forSyntaxCheck parameters, 
     * both of which should not be true for any given invocation
     * 
     * @param fc flowchart that represents the desired java program
     * @param forRun whether the program should be run
     * @param forSyntaxCheck whether the program should not run but it's syntax be checked
     * @param functions the functions that should also be converted and compiled
     * 
     * @return returns true if the program is structurally, syntactically and semantically correct at compile time and at runtime if the program is also run
     * @throws UserCreatedExprException thrown when there is an error when attempting to compile/run a user created program 
     */
    public Boolean convertThenCompileProgram(Flowchart fc, Boolean forRun, Boolean forSyntaxCheck, ArrayList<FunctionFlowchart> functions) throws UserCreatedExprException{
        //initialise semaphore
        semaphore = new Semaphore(0);
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
        
        //ensure the programs structure is valid
        if(!validateStructure(fc)){
            showAlert(Alert.AlertType.ERROR, "Program structure invalid!");
            return false;
        }
        //convert the program to a String representation of a java class
        String classCode = convertToJava(fc, "FlowJavaUserClass", forRun, forSyntaxCheck, functions);
        
        //change the System.err to capture any user created errors
        PrintStream originalSysErr = System.err;
        errorStrings = new ArrayList<>();
        System.setErr(userErrPrintStream);
        //compile (and run if forRun) the program 
        try {
            compileConverted("FlowJavaUserClass", "run", classCode, forRun);
        } catch (UserCreatedExprException | RuntimeException e) {
            //if error is encounterd during user run then display the errors to the user using user error report dialog
            if (e instanceof RuntimeException) {
                errorStrings.add(e.getMessage());
                for (StackTraceElement ste : e.getStackTrace()) {
                    errorStrings.add(ste.toString());
                }
                //iterate and all all causes
                Throwable cause = e.getCause();
                while (cause != null) {
                    //number format exceptions are common due to user input so remove errors caused by them when they are part of the stack trace
                    if (cause instanceof NumberFormatException) {
                        errorStrings.clear();
                        NumberFormatException ne = (NumberFormatException) cause;
                        errorStrings.add(ne.getMessage());
                        for (StackTraceElement ste : cause.getStackTrace()) {
                            errorStrings.add(ste.toString());
                        }
                        break;
                    }
                    for (StackTraceElement ste : cause.getStackTrace()) {
                        errorStrings.add(ste.toString());
                    }
                    cause = cause.getCause();
                }
                Platform.runLater(() -> {
                    userErrReportDialog.display(errorStrings);
                });
            } else {
                Platform.runLater(() -> {
                    userErrReportDialog.display(errorStrings);
                });
            }
            //if run ended due to runtime error, prompt the user 
            if (forRun) {
                showAlert(Alert.AlertType.ERROR, "Run Terminated Erroneously");
            }
            return false;
        } finally {
            //revert System.err to its original print stream
            System.setErr(originalSysErr);
        }
        return true;
    }
    
    /**
     * given a Flowchart fc, returns a boolean for whether fc's structure is valid
     * 
     * @param fc Flowchart to validate
     * @return boolean for whether the structure of the Flowchart is valid
     */
    public Boolean validateStructure(Flowchart fc){
        //initialise loop stacks
        whileStack = new Stack<>();
        forStack = new Stack<>();
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
            //if current vertex represents a loop, add the loop to the respective loop stack
            if(currentVertex.getController() instanceof WhileController){
                whileStack.push((WhileController)currentVertex.getController());
            } else if (currentVertex.getController() instanceof ForLoopController) {
                forStack.push((ForLoopController)currentVertex.getController());
            }
            
            //if the current vertex represents the end of a loop, remove the loop from the loop stack 
            if(currentVertex.getController() instanceof EndWhileController){
                //if the loop has not yet been encountered then the program structure is invalid
                if(whileStack.isEmpty()){
                    valid = false;
                    break;
                }
                //if the loop next in the stack is a different loop then the program structure is invalid
                WhileController requiredWhile = whileStack.pop();
                if(requiredWhile != ((EndWhileController)currentVertex.getController()).getWhileCtrl()){
                    valid = false;
                }
            } else if(currentVertex.getController() instanceof EndForController){
                //if the loop has not yet been encountered then the program structure is invalid
                if(forStack.isEmpty()){
                    valid = false;
                    break;
                }
                //if the loop next in the stack is a different loop then the program structure is invalid
                ForLoopController requiredFor = forStack.pop();
                if(requiredFor != ((EndForController)currentVertex.getController()).getForCtrl()){
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
                } else if (currentVertex.getController() instanceof ForLoopController){
                    vertexQueue.remove(((ForLoopController)currentVertex.getController()).getEndFor().getVertex());
                }
            }
        }
        //if a loop has still not been validated then the program structure is invalid
        if(!whileStack.isEmpty() || !forStack.isEmpty()){
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
            //if current vertex represents a loop, add the loop to the respective loop stack
            if(currentVertex.getController() instanceof WhileController){
                whileStack.push((WhileController)currentVertex.getController());
            } else if (currentVertex.getController() instanceof ForLoopController) {
                forStack.push((ForLoopController)currentVertex.getController());
            }
            
            //if the current vertex represents the end of a loop, remove the loop from the loop stack 
            if(currentVertex.getController() instanceof EndWhileController){
                //if the loop has not yet been encountered then the program structure is invalid
                if(whileStack.isEmpty()){
                    valid = false;
                    break;
                }
                //if the loop next in the stack is a different loop then the program structure is invalid
                WhileController requiredWhile = whileStack.pop();
                if(requiredWhile != ((EndWhileController)currentVertex.getController()).getWhileCtrl()){
                    valid = false;
                }
            } else if(currentVertex.getController() instanceof EndForController){
                //if the loop has not yet been encountered then the program structure is invalid
                if(forStack.isEmpty()){
                    valid = false;
                    break;
                }
                //if the loop next in the stack is a different loop then the program structure is invalid
                ForLoopController requiredFor = forStack.pop();
                if(requiredFor != ((EndForController)currentVertex.getController()).getForCtrl()){
                    valid = false;
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
        Platform.runLater(() -> {
            Alert customAlert = new Alert(alertType);
            customAlert.initModality(Modality.NONE);
            customAlert.setContentText(message);
            customAlert.showAndWait();
        });
    }

    /**
     * Converts a given flowchart into a String representing the program in Java
     * 
     * @param fc flowchart to convert to Java
     * @param progName the name of the program
     * @param forRun whether the program is to be run
     * @param forSyntaxCheck whether the syntax of the program is to be checked but the program is not to be run
     * @param functions functions that should also be converted to Java for the program
     * @return String representing the program in Java
     */
    public String convertToJava(Flowchart fc, String progName, Boolean forRun, Boolean forSyntaxCheck, ArrayList<FunctionFlowchart> functions) {
        String javaProg = "";
        String functionName = "";
        //if this flowchart is a function
        if (fc instanceof FunctionFlowchart) {
            functionName = ((FunctionFlowchart) fc).getName();
            FunctionFlowchart fFc = (FunctionFlowchart) fc;
            if (forSyntaxCheck) {
                //add imports and variables needed for a syntax check
                javaProg += "import javafx.stage.Stage;"
                        + "\nimport java.util.Optional;\nimport javafx.scene.control.Alert;\nimport javafx.scene.control.TextInputDialog;\n"
                        + "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n\npublic class "
                        + progName + "{"
                        + "\n\tstatic BufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tstatic String userInputString;";
            }
            //make the function static if it is not for running
            if(forRun){
                javaProg += "\n\tpublic " + functionTypeToString(fFc.getReturnType()) + " "
                    + fFc.getName() + "(";
            } else {
                javaProg += "\n\tpublic static " + functionTypeToString(fFc.getReturnType()) + " "
                    + fFc.getName() + " (";
            }
            //add any parameters
            if (!fFc.getParameters().isEmpty()) {
                for (Var v : fFc.getParameters()) {
                    javaProg += functionTypeToString(v.getType()) + " " + v.getName() + ", ";
                }
                javaProg = javaProg.substring(0, javaProg.length() - 2);
            }
            //finish of the header
            if(forRun){
                javaProg += ") throws IOException, InterruptedException{\n";
            } else {
                javaProg += ") throws IOException{\n";
            }
        } else {
            if (forRun) {
                //add imports and variables needed for a run
                javaProg += "import javafx.stage.Stage;"
                        + "\nimport javafx.application.Platform;"
                        + "\nimport java.util.concurrent.Semaphore;"
                        + "\nimport javafx.stage.Modality;"
                        + "\nimport java.util.Optional;\nimport javafx.scene.control.Alert;\nimport javafx.scene.control.TextInputDialog;\n"
                        + "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n"
                        + "\npublic class " + progName + " extends javafx.application.Application{"
                        + "\nstatic boolean running = true;\n"
                        + "\n\tBufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tString userInputString;"
                        + "\n\tstatic Semaphore semaphore;"
                        + "\n\tstatic Alert customAlert;"
                        + "\n\tstatic TextInputDialog userInDialog;"
                        + "\n\tstatic Boolean isResultPresent = false;"
                        + "\n\t@Override"
                        + "\n\tpublic void start(Stage primaryStage) throws IOException, InterruptedException {run();}"
                        + "\n\tpublic void run() throws IOException, InterruptedException{\n"
                        + "\t\tsemaphore = new Semaphore(0);\n" 
                        + "\t\tPlatform.runLater(() -> {\n"
                        + "\t\t\tcustomAlert = new Alert(Alert.AlertType.INFORMATION);\n"
                        + "\t\t\tuserInDialog = new TextInputDialog();\n"
                        + "\t\t\tsemaphore.release();\n"
                        + "\t\t});\n"
                        + "\t\tsemaphore.acquire();\n";

            } else {
                //add imports and variables needed for a syntax check
                javaProg += "import java.io.InputStreamReader;\nimport java.io.BufferedReader;\nimport java.io.IOException;\n\npublic class "
                        + progName + "{" 
                        + "\n\tstatic BufferedReader userInputBr = new BufferedReader(new InputStreamReader(System.in));"
                        + "\n\tstatic String userInputString;"
                        + "\n\tpublic static void main(String[] args) throws IOException{\n";
            }
        }
        
        //set initial value for tab index
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
            //add lines of java to javaProg based on the class of the controller and whether the program is to be run
            if(currentController instanceof UserInToVarController){
                javaProg += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof VarDecController){
                javaProg += convertVarDec((VarDecController)currentController, tabIndex);
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof OutputController){
                if(forRun){
                    javaProg += repeatString("\t",tabIndex) + "if(running){\n"
                            + "showAlert(Alert.AlertType.INFORMATION, \"Program Outputted: \" + " + ((OutputController)currentController).getExpr() + ");"
                            + repeatString("\t",tabIndex) + "\n}\n";;
                } else {
                    javaProg += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getExpr() + ");\n";
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                if(forRun){
                    javaProg += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    javaProg += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n";
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                javaProg += convertIf(currIfStmt, tabIndex, forRun, functionName);
                controllerStack.push(currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                if(forRun){
                javaProg += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + " && running){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                    javaProg += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                }
                controllerStack.push(currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(forRun){
                    if (currRec.getVariableForValue() == null) {
                        javaProg += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    } else {
                        javaProg += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    }
                } else {
                    if (currRec.getVariableForValue() == null) {
                        javaProg += repeatString("\t", tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                    } else {
                        javaProg += repeatString("\t", tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                    }
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof FunctInvokeController){
                if(forRun){
                    javaProg += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + ((FunctInvokeController)currentController).getJavaDescription() + "\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    javaProg += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                }
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if (currentController instanceof ArrayDecController){
                javaProg += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                controllerStack.push(currentController.getVertex().getChildVertices().get(0).getController());
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                if(forRun){
                javaProg += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + " && running; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                  javaProg += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);  
                }
                controllerStack.push(currFor.getEndFor().getVertex().getChildVertices().get(0).getController());
            }
        }
        
        //add return statement with a return expression where appropriate for functions
        if(fc instanceof FunctionFlowchart){
            FunctionFlowchart fFc = (FunctionFlowchart) fc;
            if (fFc.getRetunVal().isEmpty()){
                javaProg += "\t\treturn;\n";
            } else {
                javaProg += "\t\treturn " + fFc.getRetunVal() + ";\n";
            }
        }
        
        if((!(fc instanceof FunctionFlowchart) && forRun)) {
            //finish program and add other funcions used for running
            javaProg += "\t\tsemaphore = new Semaphore(0);\n" 
                    + "\t\tif(running){"
                    + "\t\t\t\tshowAlert(Alert.AlertType.INFORMATION, \"Run Complete\");"
                    + "\t\t\n}"
                    + "\t}\n\tpublic void getUserInput(String type, String name) throws InterruptedException{"
                    + "\n\t\tsemaphore = new Semaphore(0);\n"
                    + "\n\t\tisResultPresent = false;"
                    + "\t\tPlatform.runLater(() -> {\n"
                    + "\t\tuserInDialog = new TextInputDialog();\n"
                    + "\t\tuserInDialog.setHeaderText(\"Provide a \" + type + \" value for variable \" + name);\n"
                    + "\t\tuserInDialog.setTitle(\"User Input To Variable\");\n"
                    + "\t\tuserInDialog.initModality(Modality.NONE);\n"
                    + "\t\tOptional<String> result = userInDialog.showAndWait();\n"
                    + "\t\tif(result.isPresent()) {\n"
                    + "\t\t\tisResultPresent = true;"
                    + "\t\t}\n"
                    + "\t\tsemaphore.release();\n"
                    + "\t\t});\n"
                    + "\t\tsemaphore.acquire();\n"
                    + "\t\tif(isResultPresent){\n"
                    + "\t\t\tuserInputString = userInDialog.getEditor().getText();\n"
                    + "\t\t} else {\n"
                    + "\t\t\tshowAlert(Alert.AlertType.ERROR, \"No input provided\");\n"
                    + "\t\t\tuserInputString = null;\n"
                    + "\t\t}"
                    + "\n\t}"
                    + "\n\tprivate void showAlert(Alert.AlertType alertType, String message) throws InterruptedException {\n"
                    + "\t\tsemaphore = new Semaphore(0);\n"
                    + "\t\tPlatform.runLater(() -> {"
                    + "\t\t\tcustomAlert = new Alert(alertType);\n"
                    + "\t\t\tcustomAlert.initModality(Modality.NONE);\n"
                    + "\t\t\tcustomAlert.setContentText(message);\n"
                    + "\t\t\tcustomAlert.showAndWait();\n"
                    + "\t\tsemaphore.release();\n"
                    + "\t\t});\n"
                    + "\t\tsemaphore.acquire();\n"
                    + "\t}\n"
                    + "\tpublic static void cancelRun(){\n"
                    + "\t\trunning = false;\n"
                    + "\t\tcustomAlert.close();\n"
                    + "\t\tuserInDialog.close();\n"
                    + "\t\tsemaphore.release();\n"
                    + "\t}";
            
        } else {
            //add closing brackets to program
            if (fc instanceof FunctionFlowchart && forSyntaxCheck) {
                javaProg += "\t}\n";
                for (FunctionFlowchart fFc : functions) {
                    javaProg += convertToJava(fFc, "", false, false, null);
                }
            } else {
            javaProg += "\t}";
            }
        }
        
        //add the other functions to the program code
        for(FunctionFlowchart fFc: fc.getFunctions()){
            javaProg += convertToJava(fFc, "", forRun, false, null);
        }
        //add final closing bracket if required
        if(!(fc instanceof FunctionFlowchart) || (fc instanceof FunctionFlowchart && forSyntaxCheck)){
            javaProg += "\n}";
        }
        return javaProg;
    }
    
    /**
     * Given a UserInToVarController, generate the equivalent Java code in a String and return it
     * 
     * @param ctrl UserInToVarController to use to generate the Java code String
     * @param tabIndex how many tabs should be inserted before each line of code
     * @param forRun whether the code will be used for a program run
     * @return String representing the equivalent Java code
     */
    private String convertUserInToVar(UserInToVarController ctrl, int tabIndex, boolean forRun){
        String java = ""; 
        if(forRun){
            //make sure program hasn't been terminated by user
            java += "if(running){\n"
                //use the getUserInput function to get user input
                + repeatString("\t",tabIndex+2) + "getUserInput(\"" + ctrl.getType().toString().toLowerCase() + "\", \"" + ctrl.getName() + "\");\n"
                + repeatString("\t",tabIndex) + "}\n";
            
        } else {
            //use system.in to get user input
            java += repeatString("\t",tabIndex) + "userInputString = userInputBr.readLine();\n";
        }
        //set the variable to the value of the user input
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
    
    /**
     * Given a VarDecController, generate the equivalent Java code in a String and return it
     * 
     * @param ctrl VarDecController to use to generate the Java code String
     * @param tabIndex how many tabs should be inserted before each line of code
     * @return String representing the equivalent Java code
     */
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
        java += ctrl.getExpr() + ";\n";
        return java;
    }

    /**
     * Given a String s and an integer i, return s repeated i times 
     * @param s String to repeat
     * @param i number of repeats
     * @return String s repeated i times
     */
    private String repeatString(String s, int i){
        return new String(new char[i]).replace("\0", s);
    }
    
    /**
     * Given a IfStmtController, generate the equivalent Java code in a String and return it
     * 
     * @param ctrl UserInToVarController to use to generate the Java code String
     * @param tabIndex how many tabs should be inserted before each line of code
     * @param forRun whether the code will be used for a program run
     * @param functionName name of the current function
     * @return String representing the equivalent Java code
     */
    private String convertIf(IfStmtController ctrl, int tabIndex, boolean forRun, String functionName){
        String java = repeatString("\t",tabIndex) + "if(" + ctrl.getExpr() + "){\n";
        tabIndex ++;
        //convert the two seperate branches of the statement
        java += convertIfBranch(ctrl.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName) + repeatString("\t",tabIndex-1) + "}\n" + repeatString("\t",tabIndex-1) + "else{\n";
        java += convertIfBranch(ctrl.getFalseEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName) + repeatString("\t",tabIndex-1) + "}\n";
        return java;
    }
    
    /**
     * Given an vertex controller of a if statement branch, generate the rest of the equivalent Java code of the if branch body in a String and return it
     * (does not include closing bracket)
     * 
     * @param currentController VertexController to start converting the rest of the if statement branch from
     * @param tabIndex how many tabs should be inserted before each line of code
     * @param forRun whether the code will be used for a program run
     * @param functionName name of the current function
     * @return  String representing the equivalent Java code of the if statement branch body
     */
    private String convertIfBranch(VertexController currentController, int tabIndex, boolean forRun, String functionName){
        String java = "";
        while(!(currentController instanceof EndIfController)){
            //add lines of java to javaProg based on the class of the controller and whether the program is to be run
            if(currentController instanceof UserInToVarController){
                java += convertUserInToVar((UserInToVarController)currentController, tabIndex, forRun);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof VarDecController){
                java += convertVarDec((VarDecController)currentController, tabIndex);
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof OutputController){
                if(forRun){
                    java += repeatString("\t",tabIndex) + "if(running){"
                            + "showAlert(Alert.AlertType.INFORMATION, \"Program Outputted: \" + " + ((OutputController)currentController).getExpr() + ");"
                            + repeatString("\t",tabIndex) + "}";;
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getExpr() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + " && running){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                }
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(forRun){
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    } else {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    }
                } else {
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t", tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                    } else {
                        java += repeatString("\t", tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                    }
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + ((FunctInvokeController)currentController).getJavaDescription() + "\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                if(forRun){
                    if(currFor.getConditionExpr().isEmpty()){
                        java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + "running; " + currFor.getUpdateExpr() + "){\n" 
                            + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                    } else {
                        java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + " && running; " + currFor.getUpdateExpr() + "){\n" 
                            + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                    }
                } else {
                  java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);  
                }
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java;
    }
    
    /**
     * Given an vertex controller of a while loop body, generate the rest of the equivalent Java code of the while loop body in a String and return it
     * (includes closing bracket)
     * 
     * @param currentController VertexController to start converting the rest of the while loop body from
     * @param tabIndex how many tabs should be inserted before each line of code
     * @param forRun whether the code will be used for a program run
     * @param functionName name of the current function
     * @return  String representing the equivalent Java code of the while loop body
     */
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
                    java += repeatString("\t",tabIndex) + "if(running){"
                            + "showAlert(Alert.AlertType.INFORMATION, \"Program Outputted: \" + " + ((OutputController)currentController).getExpr() + ");"
                            + repeatString("\t",tabIndex) + "}";;
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getExpr() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + " && running){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                }
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(forRun){
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    } else {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    }
                } else {
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t", tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                    } else {
                        java += repeatString("\t", tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                    }
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + ((FunctInvokeController)currentController).getJavaDescription() + "\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + " && running; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                  java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);  
                }
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java + repeatString("\t",tabIndex-1) + "}\n";
    }
    
    /**
     * Given an vertex controller of a for loop body, generate the rest of the equivalent Java code of the for loop body in a String and return it
     * (includes closing bracket)
     * 
     * @param currentController VertexController to start converting the rest of the for loop body from
     * @param tabIndex how many tabs should be inserted before each line of code
     * @param forRun whether the code will be used for a program run
     * @param functionName name of the current function
     * @return  String representing the equivalent Java code of the for loop body
     */
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
                    java += repeatString("\t",tabIndex) + "if(running){"
                            + "showAlert(Alert.AlertType.INFORMATION, \"Program Outputted: \" + " + ((OutputController)currentController).getExpr() + ");"
                            + repeatString("\t",tabIndex) + "}";;
                } else {
                    java += repeatString("\t",tabIndex) + "System.out.println(" + ((OutputController)currentController).getExpr() + ");\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof VarAssignController){
                VarAssignController currVarAssign = (VarAssignController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + currVarAssign.getVarName() + " = " + currVarAssign.getExpr() + ";\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof IfStmtController){
                IfStmtController currIfStmt = (IfStmtController)currentController;
                java += convertIf(currIfStmt, tabIndex, forRun, functionName);
                currentController = currIfStmt.getEndIf().getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof WhileController){
                WhileController currWhile = (WhileController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + " && running){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                    java += repeatString("\t",tabIndex) + "while(" + currWhile.getExpr() + "){\n" 
                        + convertWhileBody(currWhile.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                }
                currentController = currWhile.getEndWhile().getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof RecurseController){
                RecurseController currRec = (RecurseController)currentController;
                if(forRun){
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    } else {
                        java += repeatString("\t",tabIndex) +"if(running){\n"
                                + repeatString("\t", tabIndex+1) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n"
                                + repeatString("\t",tabIndex) + "}\n";
                    }
                } else {
                    if (currRec.getVariableForValue() == null) {
                        java += repeatString("\t", tabIndex) + functionName + "(" + currRec.getParameterVals() + ");\n";
                    } else {
                        java += repeatString("\t", tabIndex) + currRec.getVariableForValue() + " = " + functionName + "(" + currRec.getParameterVals() + ");\n";
                    }
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof FunctInvokeController){
                if(forRun){
                    java += repeatString("\t",tabIndex) +"if(running){\n"
                            + repeatString("\t",tabIndex+1) + ((FunctInvokeController)currentController).getJavaDescription() + "\n"
                            + repeatString("\t",tabIndex) + "}\n";
                } else {
                    java += repeatString("\t",tabIndex) + ((FunctInvokeController)currentController).getJavaDescription() + "\n";
                }
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if (currentController instanceof ArrayDecController){
                java += repeatString("\t",tabIndex) + ((ArrayDecController)currentController).getJavaDescription() + "\n";
                currentController = currentController.getVertex().getChildVertices().get(0).getController();
            } else if(currentController instanceof ForLoopController){
                ForLoopController currFor = (ForLoopController)currentController;
                if(forRun){
                    java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + " && running; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);
                } else {
                  java += repeatString("\t",tabIndex) + "for(" + currFor.getInitialExpr() + "; " + currFor.getConditionExpr() + "; " + currFor.getUpdateExpr() + "){\n" 
                        + convertForBody(currFor.getTrueEdge().getEdge().getController().getChild().getController(), tabIndex, forRun, functionName);  
                }
                currentController = currFor.getEndFor().getVertex().getChildVertices().get(0).getController();
            }
        }
        return java + repeatString("\t",tabIndex-1) + "}\n";
    }
    
    /**
     * Compiles (and runs) a program that is stored in a class in a string
     * 
     * @param className the name of the class to compile
     * @param methodName the name of the method to run in the class (can be empty)
     * @param classCode the code of the class to compile
     * @param run whether the specified method should run after the class is compiled
     * @throws UserCreatedExprException thrown if an exception is encountered when compiling or running the class 
     */
    public void compileConverted(String className, String methodName, String classCode, Boolean run) throws UserCreatedExprException{ 
        //create a hash map to use as a class cache for the java compiler
        Map<String, ByteArrayOutputStream> classCache = new HashMap<>();
        //get a compiler using Java X tool provider
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        //if the compiler could not be retrived then the class cannot be run
        if (compiler == null){
            throw new RuntimeException("Could not get a compiler. "
                    + "Please use a JDK when using this application: https://www.guru99.com/install-java.html.\nAnd make sure you run the application from a CLI with Java, e.g. Command Prompt on a Windows system");
        }
        
        //retrive the file manager from the compiler
        sfm  = compiler.getStandardFileManager(null, null, null);
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
        semaphore.release();
        try{
            //load the created unkown class for invokation
            userProgClass = new ClassLoader(){
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
                //invoke the created unkown class
                userProgClass.getMethod(methodName).invoke(userProgClass.newInstance());
            }
            
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException x){
            //if an exception arose from the user created expression then throw a custom exception
            if(x instanceof NullPointerException){
                throw new UserCreatedExprException("Error in user created expression");
            } else {
                throw new RuntimeException("Run failed: " + x, x);
            }
        }
    }
    
    /**
     * given a VarType v convert it to a Java String representing a function type
     * (null = void)
     * 
     * @param v VarType to convert to function type string
     * @return String representing Java function type
     */
    private String functionTypeToString(VarType v){
        if(v == null){
            return "void";
        } else {
            return v.toString().charAt(0) + v.toString().substring(1).toLowerCase();
        }
    }
    
    /**
     * calls a function in a user created program that will cancel an execution of the program
     */
    public void stopRun(){
        try {
            userProgClass.getMethod("cancelRun").invoke(sfm, new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Run failed: " + ex, ex);
        }
    }
}
