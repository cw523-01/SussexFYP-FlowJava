/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author cwood
 */
public class CreateVertexDialog {
    private TextField valueTxtFld, nameTxtFld;
    private ExpressionHBox exprHbx;
    private boolean valid, usingExpr;
    private Text ifText, whileText;
    //data type
    private VarType type;
    //variable name and value
    private String name, val, expr;
    private ComboBox<VarType> typeCmbx;
    HBox valInputsHBox;
    ComboBox<String> valCmbx = new ComboBox<>();
    Text exprSavedTxt;
    HBox createExprHbox;
    Button createExprBtn;
    Button confirmBtn;
    VBox root;
    HBox confirmHBox;
    ArrayList<String> varNames;
    Object[] results;
    
    public Object[] display(String newVertexType, ArrayList<Var> variables, Boolean isEdit, Object[] vertexVals) {
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
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        varNames = new ArrayList<>();
        
        createExprHbox = new HBox();
        valInputsHBox = new HBox();
        valCmbx = new ComboBox<>();
        
        exprSavedTxt = new Text("  :saved:  ");
        exprSavedTxt.setVisible(false);
        
        //instantiate create expression button
        createExprBtn = new Button("Create Expression");
        createExprBtn.setOnAction(e -> {
            Object[] exprResult = CreateExprDialog.display(exprHbx);
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
            valCmbx.setItems(FXCollections.observableArrayList("Manual Expression", "Build Expression"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Type Expression");

            valInputsHBox = new HBox();

            valCmbx.setOnAction(e -> {
                expr = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Manual Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Build Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "please specify expression value!");
                    return;
                }
                switch (valCmbx.getValue()) {
                    case "Manual Expression":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the expression!");
                            return;
                        }
                        expr = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Build Expression":
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
            
        } else if(newVertexType.equals("Output")){
        
            //instantiate combo box for choosing how to enter value
            valCmbx = new ComboBox<>();
            valCmbx.setPromptText("Value");
            valCmbx.setItems(FXCollections.observableArrayList("Value/Manual Expression", "Build Expression"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Value or Variable name");
            
            valInputsHBox = new HBox();

            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Value/Manual Expression":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Build Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (valCmbx.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                    return;
                }
                switch (valCmbx.getValue()) {
                    case "Value/Manual Expression":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the value!");
                            return;
                        }
                        val = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Build Expression":
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
                } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString")){
                    showAlert(Alert.AlertType.ERROR, "user created variables cannot be named \"userInputBr\" or \"userInputString\" in flow java");
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
            valCmbx.setItems(FXCollections.observableArrayList("Given Value", "Expression"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();
            valueTxtFld.setPromptText("Value");

            valInputsHBox = new HBox();

            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Given Value":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });

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
                    case "Given Value":
                        if (valueTxtFld.getText().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "please specify the value!");
                            return;
                        }
                        name = nameTxtFld.getText();
                        val = valueTxtFld.getText();
                        valid = true;
                        break;

                    case "Expression":
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
            valCmbx.setItems(FXCollections.observableArrayList("Given Value", "Manual Expression", "Build Expression"));

            //instantiate text field for typing a given value
            valueTxtFld = new TextField();

            valCmbx.setOnAction(e -> {
                val = "";
                valInputsHBox.getChildren().clear();
                switch (valCmbx.getValue()) {
                    case "Manual Expression":
                        valueTxtFld.setPromptText("Expression");
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Given Value":
                        valueTxtFld.setPromptText("Value");
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Build Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
                }
            });
            
            //instantiate button for confirming input
            confirmBtn.setOnAction(e -> {
                //validate input
                if (typeCmbx.getValue() == null || nameTxtFld.getText().equals("")) {
                    showAlert(Alert.AlertType.ERROR, "empty values!");
                } else if (varNames.contains(nameTxtFld.getText())) {
                    showAlert(Alert.AlertType.ERROR, "variable name already used!");
                } else if (nameTxtFld.getText().equals("userInputBr") || nameTxtFld.getText().equals("userInputString")){
                    showAlert(Alert.AlertType.ERROR, "user created variables cannot be named \"userInputBr\" or \"userInputString\" in flow java");
                } else if (!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                    showAlert(Alert.AlertType.ERROR, "variable name is invalid!");
                } else {
                    if (valCmbx.getValue() == null) {
                        showAlert(Alert.AlertType.ERROR, "please specify variable value!");
                        return;
                    }
                    switch (valCmbx.getValue()) {
                        case "Given Value":
                            if (valueTxtFld.getText().isEmpty()) {
                                showAlert(Alert.AlertType.ERROR, "please specify the value!");
                                return;
                            }
                            if (exprHbx == null) {
                                exprHbx = new ExpressionHBox(false);
                            }
                            val = valueTxtFld.getText();
                            break;

                        case "Build Expression":
                            if (exprHbx == null) {
                                showAlert(Alert.AlertType.ERROR, "please create expression!");
                                return;
                            }
                            usingExpr = true;
                            String expr = exprHbx.getExprString();
                            val = expr;
                            break;

                        case "Manual Expression":
                            if (valueTxtFld.getText().isEmpty()) {
                                showAlert(Alert.AlertType.ERROR, "please specify the expression!");
                                return;
                            }
                            usingExpr = true;
                            if (exprHbx == null) {
                                exprHbx = new ExpressionHBox(false);
                            }
                            val = valueTxtFld.getText();
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
        }
        
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 200);          
        stage.setTitle(newVertexType);
        stage.setScene(scene);
        stage.showAndWait();
        
        switch (newVertexType){
            case "If Statement":
            case "While Loop":
                results = new Object[]{expr,exprHbx,valid};
                break;
            case "Output":
                results = new Object[]{val,exprHbx,valid,usingExpr};
                break;
            case "User Input to Variable":
                results = new Object[]{type,name};
                break;
            case "Variable Assignment":
                results = new Object[]{name,val,exprHbx,valid,usingExpr};
                break;
            case "Variable Declaration":
                results = new Object[]{type, name, val, exprHbx, usingExpr};
                break;
        }
        
        //return input
        return results;
    }
    
    
    private void showAlert(Alert.AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
    }
}
