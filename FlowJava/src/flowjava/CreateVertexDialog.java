package flowjava;

import java.net.URL;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Dialog used to help the users create Vertices for a program
 *
 * @author cwood
 */
public class CreateVertexDialog {
    //text fields for collecting values from users
    private TextField valueTxtFld, nameTxtFld, parametersTxtFld, initExprTxtFld, condExprTxtFld, updateExprTxtFld;
    //expression HBox for building expressions
    private ExpressionHBox exprHbx;
    //flag booleans for input form responses
    private boolean valid, usingExpr, declaredByValues;
    //descriptive texts for if and while input forms
    private Text ifText, whileText;
    //descriptive text for prompting user to enter values
    private Text valPromptTxt;
    //data type for variable vertices
    private VarType type;
    //Strings returning user input
    private String name, val, expr, parseVarName, intExpr, condExpr, updateExpr;
    //combo box for choosing a VarType
    private ComboBox<VarType> typeCmbx;
    //HBox for organising form components
    private HBox valInputsHBox;
    //combo box for choosing a type of value
    private ComboBox<String> valCmbx = new ComboBox<>();
    //combo box for choosing a variable
    private ComboBox<String> varCmbx = new ComboBox<>();
    //text to prompt user about a saved expression
    private Text exprSavedTxt;
    //HBox for elements relating to expression entry
    private HBox createExprHbox;
    //Button for using the CreateExprDialog 
    private Button createExprBtn;
    //Button for confirming user input
    private Button confirmBtn;
    //root node
    private VBox root;
    //HBox for elements relating to user input confirmation
    private HBox confirmHBox;
    //List of all variable names
    private ArrayList<String> varNames;
    //object array for returning user input
    private Object[] results;
    //the function chosen for a function invocation vertex
    private FunctionFlowchart chosenFunction;
    
    /**
     * gets user input for creating a new vertex of a specified type and returns the entered values,
     * if the user is editing a vertex then vertex values are required and the updated values are returned.
     * 
     * the different supported vertex types and the values required for and edit are:
     * 
     * "If Statement" - boolean expression stored in String, ExpressionHBox (can be null).
     * "While Loop" - boolean expression stored in String, ExpressionHBox (can be null).
     * "For Loop" - initialisation expression stored in String, conditional expression stored in String, update expression stored in String.
     * "Output" - expression stored in String, ExpressionHBox (can be null).
     * "User Input to Variable" - VarType for variable type, String for variable name.
     * "Variable Assignment" - String for variable name, String for assignment expression, ExpressionHBox (can be null).
     * "Variable Declaration" - VarType for variable type, String for variable name, String for assignment expression, ExpressionHBox (can be null).
     * "Invoke Function" - String for function name, String for parameter values, String for variable name to assign return value (can be null).
     * "Recurse" - String for parameter values, String for variable name to assign return value (can be null).
     * "Array Declaration" - VarType for array type, String for array name, String for assignment values.
     * 
     * @param newVertexType The type of vertex to get values for, supported types are "If Statement", "While Loop", "For Loop", "Output", "User Input to Variable", "Variable Assignment", "Variable Declaration", "Invoke Function", "Recurse", "Array Declaration" 
     * @param variables The variables in the flowchart the vertex will be added to (only needed for some vertex types)
     * @param isEdit Whether the dialog is being used to edit a vertex
     * @param vertexVals Values of the vertex being edited if the dialog is being used to edit a vertex 
     * @return Array of objects used for constructing a vertex based on user input
     */
    public Object[] display(String newVertexType, ArrayList<Var> variables, Boolean isEdit, Object[] vertexVals) {
        //initialise fields with default values
        valid = false;
        expr = "";
        exprHbx = null;
        ifText = new Text("If: ");
        whileText = new Text("While: ");
        val = "";
        exprHbx = null;
        usingExpr = false;
        type = null;
        name = "";
        confirmBtn = new Button("Confirm");
        varNames = new ArrayList<>();
        createExprHbox = new HBox();
        valInputsHBox = new HBox();
        valCmbx = new ComboBox<>();
        exprSavedTxt = new Text("  :saved:  ");
        exprSavedTxt.setVisible(false);
        
        //set the defaukt scene size
        int sceneWidth = 400;
        int sceneHeight = 200;
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        URL imgURL = getClass().getResource("/images/LogoImg.png");
        stage.getIcons().add(new Image(imgURL.toString()));
        
        
        //instantiate create expression button
        createExprBtn = new Button("Create Expression");
        createExprBtn.setOnAction(e -> {
            //use CreateExprDialog to help user create an expression
            CreateExprDialog cED = new CreateExprDialog();
            Object[] exprResult = cED.display(exprHbx);
            if (((Boolean) exprResult[1])) {
                exprHbx = (ExpressionHBox) exprResult[0];
                exprSavedTxt.setVisible(true);
                createExprBtn.setText("Edit Expression");
            }
        });
        createExprHbox.setAlignment(Pos.CENTER);
        createExprHbox.getChildren().addAll(createExprBtn, exprSavedTxt);
        
        if(newVertexType.equals("If Statement") || newVertexType.equals("While Loop")){
            
            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Boolean Expression");
            valCmbx.setItems(FXCollections.observableArrayList("Enter Expression", "Expression Assistant"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Type Expression");

            valInputsHBox = new HBox();
            valInputsHBox.getChildren().add(valueTxtFld);
            
            valCmbx.setOnAction(e -> {
                expr = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression Assistant":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            valCmbx.setValue("Enter Expression");
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "please specify expression value!");
                    return;
                }
                //set expr based on input method
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the expression!");
                            return;
                        }
                        expr = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Expression Assistant":
                        if (exprHbx == null) {
                            showAlert(Alert.AlertType.ERROR, "please create expression!");
                            return;
                        }
                        String expr = exprHbx.getExprString();
                        this.expr = expr;
                        valid = true;
                        break;
                }
                stage.close();
            });
            
            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);
            
            //instantiate root node
            root = new VBox();
            if(newVertexType.equals("If Statement")){
                root.getChildren().addAll(ifText, valCmbx, valInputsHBox, confirmHBox);
            } else {
                root.getChildren().addAll(whileText, valCmbx, valInputsHBox, confirmHBox);
                
            }
            root.setPadding(new Insets(10, 50, 50, 50));
            
            if(isEdit){
                valueTxtFld.setText((String)vertexVals[0]);
                exprHbx = (ExpressionHBox)vertexVals[1];
            }
            
        } else if(newVertexType.equals("For Loop")) {
            
            //instantiate text fields
            initExprTxtFld = new TextField();
            initExprTxtFld.setPromptText("Intial Expression");
            
            condExprTxtFld = new TextField();
            condExprTxtFld.setPromptText("Condition Expression");
            
            updateExprTxtFld = new TextField();
            updateExprTxtFld.setPromptText("Update Expression");
            
            confirmBtn.setOnAction(e -> {
                intExpr = initExprTxtFld.getText();
                condExpr = condExprTxtFld.getText();
                updateExpr = updateExprTxtFld.getText();
                valid = true;
                stage.close();
            });
            
            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);
            
            root = new VBox();
            root.getChildren().addAll(new VBox(new Text("Initial Expression (e.g. Integer i = 0):"), initExprTxtFld), 
                    new VBox(new Text("Conditional Expression (e.g. i < 10):"), condExprTxtFld), 
                    new VBox(new Text("Update Expression (e.g. i++):"), updateExprTxtFld), confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            root.setSpacing(5);
            
            if(isEdit){
                initExprTxtFld.setText((String)vertexVals[0]);
                condExprTxtFld.setText((String)vertexVals[1]);
                updateExprTxtFld.setText((String)vertexVals[2]);
            }
            
            sceneHeight = 230;
        
        } else if(newVertexType.equals("Output")){
        
            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Value");
            valCmbx.setItems(FXCollections.observableArrayList("Enter Expression", "Expression Assistant"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Value or Variable name");
            
            valInputsHBox = new HBox();
            valInputsHBox.getChildren().add(valueTxtFld);
            
            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression Assistant":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            valCmbx.setValue("Enter Expression");
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                    return;
                }
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the value!");
                            return;
                        }
                        val = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Expression Assistant":
                        if (exprHbx == null) {
                            showAlert(Alert.AlertType.ERROR, "please create expression!");
                            return;
                        }
                        String expr = exprHbx.getExprString();
                        val = expr;
                        valid = true;
                        usingExpr = true;
                        break;
                }
                stage.close();
            });

            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);

            //instantiate root node
            root = new VBox();
            root.getChildren().addAll(valCmbx, valInputsHBox, confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            
            if(isEdit){
                valueTxtFld.setText((String)vertexVals[0]);
                exprHbx = (ExpressionHBox)vertexVals[1];
            }
            
        } else if(newVertexType.equals("User Input to Variable")){
            
            for (Var v : variables) {
                varNames.add(v.getName());
            }
            //instantiate combo box for choosing a data type
            typeCmbx = new ComboBox<>();
            typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
            typeCmbx.setPromptText("Variable Type");

            //instantiate text field for entering variable name
            nameTxtFld = new TextField();
            nameTxtFld.setPromptText("Variable Name");
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (typeCmbx.getValue() == null || nameTxtFld.getText().equals("")) {
                    showAlert(Alert.AlertType.ERROR, "empty values!");
                } else if (varNames.contains(nameTxtFld.getText())) {
                    showAlert(Alert.AlertType.ERROR, "variable name already used!");
                } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString") || 
                        nameTxtFld.getText().equals("running") || nameTxtFld.getText().equals("semaphore") ||
                        nameTxtFld.getText().equals("customAlert") || nameTxtFld.getText().equals("isResultPresent")){
                    showAlert(Alert.AlertType.ERROR, "user created variables cannot be named \"userInputBr\",\"running\","
                            + "\"semaphore\", \"customAlert\", \"isResultPresent\" or \"userInputString\" in flow java");
                } else if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                    showAlert(Alert.AlertType.ERROR, "variable name is invalid!");
                } else {
                    type = (VarType) typeCmbx.getValue();
                    name = nameTxtFld.getText();
                    stage.close();
                }
            });

            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);

            //instantiate root node
            root = new VBox();
            root.getChildren().addAll(typeCmbx, nameTxtFld, confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            
            if(isEdit){
                typeCmbx.setValue((VarType)vertexVals[0]);
                nameTxtFld.setText((String)vertexVals[1]);
            }
            
        } else if (newVertexType.equals("Variable Assignment")) {
            
            nameTxtFld = new TextField();
            nameTxtFld.setPromptText("Variable name");

            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Value");
            valCmbx.setItems(FXCollections.observableArrayList("Enter Expression", "Expression Assistant"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Value");

            valInputsHBox = new HBox();
            valInputsHBox.getChildren().add(valueTxtFld);

            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression Assistant":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            valCmbx.setValue("Enter Expression");

            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (nameTxtFld.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "please specify variable name!");
                    return;
                }
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                    return;
                }
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the value!");
                            return;
                        }
                        name = nameTxtFld.getText();
                        val = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Expression Assistant":
                        if (exprHbx == null) {
                            showAlert(Alert.AlertType.ERROR, "please create expression!");
                            return;
                        }
                        name = nameTxtFld.getText();
                        String expr = exprHbx.getExprString();
                        val = expr;
                        valid = true;
                        usingExpr = true;
                        break;
                }
                stage.close();
            });

            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);

            Text nameTxt = new Text("Variable name:");
            
            //instantiate root node
            root = new VBox();
            root.getChildren().addAll(nameTxt, nameTxtFld, valCmbx, valInputsHBox, confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            
            if(isEdit){
                nameTxtFld.setText((String)vertexVals[0]);
                valueTxtFld.setText((String)vertexVals[1]);
                exprHbx = (ExpressionHBox)vertexVals[2];
            }

        } else if (newVertexType.equals("Variable Declaration")) {
            
            varNames = new ArrayList<>();
            for (Var v : variables) {
                varNames.add(v.getName());
            }

            //instantiate combo box for choosing a data type
            typeCmbx = new ComboBox<>();
            typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
            typeCmbx.setPromptText("Type");

            //instantiate text field for entering variable name
            nameTxtFld = new TextField();
            nameTxtFld.setPromptText("Name");

            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Value");
            valCmbx.setItems(FXCollections.observableArrayList("Enter Expression", "Expression Assistant"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valInputsHBox.getChildren().add(valueTxtFld);
            valueTxtFld.setPromptText("Value");

            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Enter Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression Assistant":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            valCmbx.setValue("Enter Expression");
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (typeCmbx.getValue() == null || nameTxtFld.getText().equals("")) {
                    showAlert(Alert.AlertType.ERROR, "empty values!");
                } else if (varNames.contains(nameTxtFld.getText())) {
                    showAlert(Alert.AlertType.ERROR, "variable name already used!");
                } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString") || 
                        nameTxtFld.getText().equals("running") || nameTxtFld.getText().equals("semaphore") ||
                        nameTxtFld.getText().equals("customAlert") || nameTxtFld.getText().equals("isResultPresent")){
                    showAlert(Alert.AlertType.ERROR, "user created variables cannot be named \"userInputBr\",\"running\","
                            + "\"semaphore\", \"customAlert\", \"isResultPresent\" or \"userInputString\" in flow java");
                } else if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                    showAlert(Alert.AlertType.ERROR, "variable name is invalid!");
                } else if(valCmbx.getValue() == null){
                    showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                } else {
                    switch (valCmbx.getValue()) {
                        case "Enter Expression":
                            if (valueTxtFld.getText().isEmpty()) {
                                showAlert(Alert.AlertType.ERROR, "please specify the value!");
                                return;
                            }
                            if (exprHbx == null) {
                                exprHbx = new ExpressionHBox(false);
                            }
                            val = valueTxtFld.getText();
                            break;

                        case "Expression Assistant":
                            if (exprHbx == null) {
                                showAlert(Alert.AlertType.ERROR, "please create expression!");
                                return;
                            }
                            usingExpr = true;
                            String expr = exprHbx.getExprString();
                            val = expr;
                            break;
                    }
                    type = (VarType) typeCmbx.getValue();
                    name = nameTxtFld.getText();
                    stage.close();
                }
            });

            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);

            //instantiate root node
            root = new VBox();
            root.getChildren().addAll(typeCmbx, nameTxtFld, valCmbx, valInputsHBox, confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            
            if(isEdit){
                typeCmbx.setValue((VarType)vertexVals[0]);
                nameTxtFld.setText((String)vertexVals[1]);
                valueTxtFld.setText((String)vertexVals[2]);
                exprHbx = (ExpressionHBox)vertexVals[3];
            }
            
        } else if (newVertexType.equals("Invoke Function")) {
            @SuppressWarnings("unchecked")
            ArrayList<FunctionFlowchart> functions = (ArrayList<FunctionFlowchart>)vertexVals[0];
            
            ArrayList<String> functionNames = new ArrayList<>();
            for(FunctionFlowchart fF: functions){
                functionNames.add(fF.getName());
            }
            
            chosenFunction = null;
            
            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Function");
            valCmbx.setItems(FXCollections.observableArrayList(functionNames));
            
            TextArea parameterInfoTxtA = new TextArea("");
            parameterInfoTxtA.setEditable(false);
            
            valCmbx.setOnAction(e -> {
                chosenFunction = null;
                int i = 0;
                while(chosenFunction == null && i < functions.size()){
                    if(functions.get(i).getName().equals(valCmbx.getValue())){
                        chosenFunction = functions.get(i);
                    }
                    i++;
                }
                
                root.getChildren().remove(1, root.getChildren().size());
                
                if(chosenFunction.getParameters().isEmpty()){
                    parametersTxtFld.setText("");
                    root.getChildren().addAll(new VBox(new Text("Assign value to variable: "),new HBox(varCmbx, new Text("*optional"))), confirmHBox);
                } else {
                    String parameterInfo = "Add the values for the following parameters:\n\n";
                    for(Var v: chosenFunction.getParameters()){
                        parameterInfo += v.getType().toString() + " " + v.getName() + "\n";
                    }
                    parameterInfo += "\nAnd seperate each value with a comma ','";
                    parameterInfoTxtA.setText(parameterInfo);
                    root.getChildren().addAll(new VBox(parameterInfoTxtA, parametersTxtFld), new VBox(new Text("Assign value to variable: "),new HBox(varCmbx, new Text("*optional"))), confirmHBox);
                }
                
            });
            
            //instantiate text field for typing a given value
            parametersTxtFld = new TextField();
            parametersTxtFld.setPromptText("Parameter values");
            
            varNames = new ArrayList<>();
            for (Var v : variables) {
                varNames.add(v.getName());
            }
            
            varCmbx = new ComboBox<>();
            varCmbx.setPromptText("Variable");
            varCmbx.setItems(FXCollections.observableArrayList(varNames));
            
            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "choose a function!");
                } else if (!chosenFunction.getParameters().isEmpty() && parametersTxtFld.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "provide parameter values!");
                } else {
                    name = valCmbx.getValue();
                    val = parametersTxtFld.getText();
                    parseVarName = varCmbx.getValue();
                    valid = true;
                    stage.close();
                }
            });
            
            root = new VBox();
            root.getChildren().addAll(valCmbx, new VBox(new Text("Assign value to variable: "),new HBox(varCmbx, new Text("*optional"))), confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            root.setSpacing(5);
            
            sceneWidth = 600;
            sceneHeight = 400;
            
            if(isEdit){
                valCmbx.setValue((String)vertexVals[1]);
                parametersTxtFld.setText((String)vertexVals[2]);
                varCmbx.setValue((String)vertexVals[3]);
                Event.fireEvent(valCmbx, new ActionEvent());
            }
            
        } else if (newVertexType.equals("Recurse")) {
            
            TextArea parameterInfoTxtA = new TextArea("");
            parameterInfoTxtA.setEditable(false);
            
            String parameterInfo = "Add the values for this functions parameters\n";
            parameterInfo += "\nAnd seperate each value with a comma ','\n\n*If there are none then\nleave this field empty";
            parameterInfoTxtA.setText(parameterInfo);

            //instantiate text field for typing a given value
            parametersTxtFld = new TextField();
            parametersTxtFld.setPromptText("Parameter values");

            varNames = new ArrayList<>();
            for (Var v : variables) {
                varNames.add(v.getName());
            }
            
            varCmbx = new ComboBox<>();
            varCmbx.setPromptText("Variable");
            varCmbx.setItems(FXCollections.observableArrayList(varNames));
            
            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                valid = true;
                val = parametersTxtFld.getText();
                parseVarName = varCmbx.getValue();
                stage.close();
            });
            
            root = new VBox();
            root.getChildren().addAll(new VBox(parameterInfoTxtA, parametersTxtFld), new VBox(new Text("Assign value to variable: "), new HBox(varCmbx, new Text("*optional"))), confirmHBox);
            root.setPadding(new Insets(10, 50, 50, 50));
            root.setSpacing(5);
            
            sceneWidth = 600;
            sceneHeight = 400;
            
            if(isEdit){
                parametersTxtFld.setText((String)vertexVals[0]);
                varCmbx.setValue((String)vertexVals[1]);
            }
            
        } else if (newVertexType.equals("Array Declaration")) {
            varNames = new ArrayList<>();
            for (Var v : variables) {
                varNames.add(v.getName());
            }

            //instantiate combo box for choosing a data type
            typeCmbx = new ComboBox<>();
            typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
            typeCmbx.setPromptText("Type");
            
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Declare by values");
            valCmbx.setItems(FXCollections.observableArrayList("Declare by values", "Declare by length"));
            valCmbx.setValue("Declare by values");
            
            //instantiate text field for entering variable name
            nameTxtFld = new TextField();
            nameTxtFld.setPromptText("Name");
            
            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Values");
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (typeCmbx.getValue() == null || nameTxtFld.getText().equals("")) {
                    showAlert(Alert.AlertType.ERROR, "empty values!");
                } else if (varNames.contains(nameTxtFld.getText())) {
                    showAlert(Alert.AlertType.ERROR, "variable name already used!");
                } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString") || 
                        nameTxtFld.getText().equals("running") || nameTxtFld.getText().equals("semaphore") ||
                        nameTxtFld.getText().equals("customAlert") || nameTxtFld.getText().equals("isResultPresent")){
                    showAlert(Alert.AlertType.ERROR, "user created variables cannot be named \"userInputBr\",\"running\","
                            + "\"semaphore\", \"customAlert\", \"isResultPresent\" or \"userInputString\" in flow java");
                } else if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                    showAlert(Alert.AlertType.ERROR, "variable name is invalid!");
                } else if(valueTxtFld.getText() == null || valueTxtFld.getText().isEmpty()){
                    showAlert(Alert.AlertType.ERROR, "please specify array values!");
                } else {
                    val = valueTxtFld.getText();
                    type = (VarType) typeCmbx.getValue();
                    name = nameTxtFld.getText();
                    declaredByValues = valCmbx.getValue().equals("Declare by values");
                    stage.close();
                }
            });

            //instantiate HBox for positioning confirm button
            confirmHBox = new HBox();
            confirmHBox.setAlignment(Pos.CENTER);
            confirmHBox.setPadding(new Insets(10, 50, 50, 50));
            confirmHBox.getChildren().add(confirmBtn);

            valPromptTxt = new Text("enter values for the array of the correct\ntype, spereated by commas ','");
            
            valCmbx.setOnAction(e -> {
                if(valCmbx.getValue().equals("Declare by values")){
                    valPromptTxt.setText("enter values for the array of the correct\ntype, spereated by commas ','");
                    valueTxtFld.setPromptText("Values");
                } else if (valCmbx.getValue().equals("Declare by length")){
                    valPromptTxt.setText("enter the length of the array\n(values will initialise as null)");
                    valueTxtFld.setPromptText("Length");
                }
            });
            
            //instantiate root node
            root = new VBox();
            root.setPadding(new Insets(10, 50, 50, 50));
            root.setSpacing(5);
            root.getChildren().addAll(typeCmbx, nameTxtFld, valCmbx, 
                    new VBox(valPromptTxt, valueTxtFld), confirmHBox);
            
            sceneHeight = 240;
            
            if(isEdit){
                typeCmbx.setValue((VarType)vertexVals[0]);
                nameTxtFld.setText((String)vertexVals[1]);
                valueTxtFld.setText((String)vertexVals[2]);
                if(!(Boolean)vertexVals[3]){
                    valCmbx.setValue("Declare by length");
                    valPromptTxt.setText("enter the length of the array\n(values will initialise as null)");
                    valueTxtFld.setPromptText("Length");
                }
            }
        }
        
        //instantiate scene
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        stage.setTitle(newVertexType);
        stage.setScene(scene);
        //ensure stage bounds are reasonable
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(bounds.getHeight());
        stage.setMaxWidth(bounds.getWidth());
        
        stage.showAndWait();
        
        //return certain values depending on on newVertexType
        switch (newVertexType){
            case "If Statement":
            case "While Loop":
                results = new Object[]{expr, exprHbx, valid};
                break;
            case "Output":
                results = new Object[]{val, exprHbx, valid, usingExpr};
                break;
            case "User Input to Variable":
                results = new Object[]{type,name};
                break;
            case "Variable Assignment":
                results = new Object[]{name, val, exprHbx, valid, usingExpr};
                break;
            case "Variable Declaration":
                results = new Object[]{type, name, val, exprHbx, usingExpr};
                break;
            case "Invoke Function":
                results = new Object[]{name, val, parseVarName, valid};
                break;
            case "Recurse":
                results = new Object[]{valid, val, parseVarName};
                break;
            case "Array Declaration":
                results = new Object[]{type, name, val, declaredByValues};
                break;
            case "For Loop":
                results = new Object[]{intExpr, condExpr, updateExpr, valid};
                break;
        }
        
        //return input
        return results;
    }
    
    /**
     * given an alert type and message, display an alert using these parameters for the type and content text 
     * @param alertType the alert type for the alert to display
     * @param message the content text for the alert to display
     */
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.showAndWait();
    }
}
