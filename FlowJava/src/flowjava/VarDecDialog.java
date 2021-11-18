package flowjava;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Used to display dialog box for an input form for variable declaration vertices
 *
 * @author cwood
 */
public class VarDecDialog {
    //data type
    private VarType type;
    //variable name and value
    private String name, val;
    
    private ComboBox<VarType> typeCmbx;
    
    private TextField valueTxtFld;
    
    private ExpressionHBox exprHbx;
    
    public Object[] display(ArrayList<Var> variables) {
        //set the fields to empty values
        type = null;
        name = "";
        val = "";
        exprHbx = null;
        
        ArrayList<String> varNames = new ArrayList<>();
        for(Var v: variables){
            varNames.add(v.getName());
        }
        
        //instantiate the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //instantiate combo box for choosing a data type
        typeCmbx = new ComboBox<>();
        typeCmbx.setItems(FXCollections.observableArrayList(VarType.values()));
        typeCmbx.setPromptText("Type");
        
        //instantiate text field for entering variable name
        TextField nameTxtFld = new TextField();
        nameTxtFld.setPromptText("Name");
        
        //instantiate combo box for choosing how to enter value
        ComboBox<String> valCmbx = new ComboBox<>();
        valCmbx.setPromptText("Value");
        valCmbx.setItems(FXCollections.observableArrayList("Given Value", "Expression", "Null"));
        
        //instantiate text field for typing a given value
        valueTxtFld = new TextField();
        valueTxtFld.setPromptText("Value");
        
        Text exprSavedTxt = new Text("  :saved:  ");
        exprSavedTxt.setVisible(false);
        
        //instantiate create expression button
        Button createExprBtn = new Button("Create Expression");
        createExprBtn.setOnAction(e -> {
            Object[] exprResult = CreateExprDialog.display(exprHbx);
            if(((Boolean)exprResult[1])){
                exprHbx = (ExpressionHBox) exprResult[0];
                exprSavedTxt.setVisible(true);
                createExprBtn.setText("Edit Expression");
            }
        });
        
        HBox createExprHbox = new HBox();
        createExprHbox.setAlignment(Pos.CENTER);
        createExprHbox.getChildren().addAll(createExprBtn,exprSavedTxt);
        
        HBox valInputsHBox = new HBox();
        
        valCmbx.setOnAction(e -> {
            val = "";
            valInputsHBox.getChildren().clear();
            switch (valCmbx.getValue()){
                    case "Given Value":
                        valInputsHBox.getChildren().add(valueTxtFld);
                        break;
                    case "Expression":
                        valInputsHBox.getChildren().add(createExprHbox);
                        break;
            }
        });
        
        //instantiate button for confirming input
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e -> {
            //validate input
            if(typeCmbx.getValue() == null || nameTxtFld.getText().equals("")){
                Alert nullAlert = new Alert(AlertType.ERROR);
                nullAlert.setContentText("empty values!");
                nullAlert.show();
            } else if (varNames.contains(nameTxtFld.getText())){
                Alert nullAlert = new Alert(AlertType.ERROR);
                nullAlert.setContentText("variable name already used!");
                nullAlert.show();
            } else if(!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")){
                Alert nullAlert = new Alert(AlertType.ERROR);
                nullAlert.setContentText("variable name is invalid!");
                nullAlert.show();
            } else {
                if(valCmbx.getValue() == null){
                    Alert nullAlert = new Alert(AlertType.ERROR);
                    nullAlert.setContentText("please specify variable value!");
                    nullAlert.show();
                    return;
                }
                switch(valCmbx.getValue()){
                    case "Null":
                        val = "null";
                        break;
                    case "Given Value":
                        Object givenVal = FlowJava.parseVal(typeCmbx.getValue(), valueTxtFld.getText());
                        if(givenVal == null){
                            Alert nullAlert = new Alert(AlertType.ERROR);
                            nullAlert.setContentText("Given value cannot parse to specified type!");
                            nullAlert.show();
                            return;
                        }
                        if(valueTxtFld.getText().isEmpty()){
                            Alert nullAlert = new Alert(AlertType.ERROR);
                            nullAlert.setContentText("Given value cannot be empty!");
                            nullAlert.show();
                            return;
                        } else if (typeCmbx.getValue().equals(VarType.STRING) && 
                                (!((String)givenVal).startsWith("\"")  || !((String)givenVal).endsWith("\""))){
                            Alert nullAlert = new Alert(AlertType.ERROR);
                            nullAlert.setContentText("String values are kept in quotations!\n E.g. \"example string\" or \'example string\'");
                            nullAlert.show();
                            return;
                        } else if (typeCmbx.getValue().equals(VarType.STRING) && !((String)givenVal).matches("^\"(\\.|[^\"])*\"$")){
                            Alert nullAlert = new Alert(AlertType.ERROR);
                            nullAlert.setContentText("Invalid String Body!");
                            nullAlert.show();
                            return;
                        }
                        val = givenVal.toString();
                        break;
                        
                    case "Expression":
                        ScriptEngineManager mgr = new ScriptEngineManager();
                        ScriptEngine engine = mgr.getEngineByName("JavaScript");
                        ArrayList<Var> usedVars = new ArrayList<>();
                        ArrayList<String> exprStrs = exprHbx.textFldVals();
                        for (Var v : variables) {
                                engine.put(v.getName(), FlowJava.sampleVal(v.getType()));
                                if(exprStrs.contains(v.getName())){
                                    usedVars.add(v);
                                }
                        }
                        String expr = exprHbx.getExprString();
                        Object o;
                        try {
                            o = engine.eval(expr);
                        } catch (ScriptException scrExc) {
                            System.out.println(scrExc);
                            return;
                        }
                        switch (typeCmbx.getValue()) {
                            case DOUBLE:
                                try {
                                    if (o instanceof Integer) {
                                        o = (double) ((int) o);
                                    } else if (o instanceof Double) {
                                        o = (double) o;
                                    } else {
                                        o = (double) o;
                                    }
                                    if(Double.valueOf(o.toString()).isNaN()){
                                        showAlert(AlertType.ERROR, "Expression does not produce a valid number!");
                                        return;
                                    }
                                } catch (ClassCastException exc) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }
                                val = expr;
                                break;
                                
                            case FLOAT:
                                boolean valid = true;
                                int i = 0;
                                while(valid && i < usedVars.size()){
                                    if(usedVars.get(i).getType().equals(VarType.DOUBLE)){
                                        valid = false;
                                    }
                                    i++;
                                }
                                if(!valid){
                                    showAlert(AlertType.ERROR, "Cannot use numbers of type Double in Float assignment");
                                    return;
                                }
                                try {
                                    if (o instanceof Integer) {
                                        o = (float) ((int) o);
                                    } else if (o instanceof Double) {
                                        o = (float) ((double) o);
                                    } else {
                                        o = (float) o;
                                    }
                                    if((Double.valueOf(o.toString())).isNaN()){
                                        showAlert(AlertType.ERROR, "Expression does not produce a valid number!");
                                        return;
                                    }
                                } catch (ClassCastException exc) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }
                                val = expr;
                                break;

                            case LONG:
                                valid = true;
                                i = 0;
                                while (valid && i < usedVars.size()) {
                                    if (usedVars.get(i).getType().equals(VarType.DOUBLE) || usedVars.get(i).getType().equals(VarType.FLOAT)) {
                                        valid = false;
                                    }
                                    i++;
                                }
                                if (!valid) {
                                    showAlert(AlertType.ERROR, "Cannot use numbers of type Double or Float in Long assignment");
                                    return;
                                }
                                try {
                                    if (o instanceof Integer) {
                                        o = (long) ((int) o);
                                    } else if (o instanceof Double) {
                                        o = (long) ((double) o);
                                    } else {
                                        o = (long) o;
                                    }
                                    if(Double.valueOf(o.toString()).isNaN()){
                                        showAlert(AlertType.ERROR, "Expression does not produce a valid number!");
                                        return;
                                    }
                                } catch (ClassCastException exc) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }
                                val = expr;
                                break;

                            case INTEGER:
                                valid = true;
                                i = 0;
                                while (valid && i < usedVars.size()) {
                                    if (usedVars.get(i).getType().equals(VarType.DOUBLE) || usedVars.get(i).getType().equals(VarType.FLOAT)
                                            || usedVars.get(i).getType().equals(VarType.LONG)) {
                                        valid = false;
                                    }
                                    i++;
                                }
                                if (!valid) {
                                    showAlert(AlertType.ERROR, "Cannot use numbers of type Double, Float or Long in Integer assignment");
                                    return;
                                }
                                try {
                                    if (o instanceof Double) {
                                        o = (int) ((double) o);
                                    } else {
                                        o = (int) o;
                                    }
                                    if(Double.valueOf(o.toString()).isNaN()){
                                        showAlert(AlertType.ERROR, "Expression does not produce a valid number!");
                                        return;
                                    }
                                } catch (ClassCastException exc) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }
                                val = expr;
                                break;

                            case SHORT:
                                valid = validateUsedTypes(new VarType[]{VarType.DOUBLE, VarType.FLOAT, VarType.LONG, VarType.INTEGER}, usedVars);
                                if (!valid) {
                                    showAlert(AlertType.ERROR, "Cannot use numbers of type Double, Float, Long or Integer in Short assignment");
                                    return;
                                }
                                try {
                                    if (o instanceof Integer) {
                                        o = (short) ((int) o);
                                    } else if (o instanceof Double) {
                                        o = (short) ((double) o);
                                    } else {
                                        o = (short) o;
                                    }
                                    if(Double.valueOf(o.toString()).isNaN()){
                                        showAlert(AlertType.ERROR, "Expression does not produce a valid number!");
                                        return;
                                    }
                                } catch (ClassCastException exc) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }
                                val = expr;
                                break;

                            default:
                                if (FlowJava.parseVal(typeCmbx.getValue(), o.toString()) == null) {
                                    showAlert(AlertType.ERROR, "Expression returns type that cannot be cast to specified variable type!\nExpression type: "
                                            + o.getClass().getSimpleName());
                                    return;
                                }

                                val = expr;
                                break;
                        }
                        break;
                }
                type = (VarType)typeCmbx.getValue();
                name = nameTxtFld.getText();
                stage.close();   
            }
        });

        //instantiate HBox for positioning confirm button
        HBox confirmHBox = new HBox();
        confirmHBox.setAlignment(Pos.CENTER);
        confirmHBox.setPadding(new Insets(10, 50, 50, 50));
        confirmHBox.getChildren().add(confirmBtn);
        
        //instantiate root node
        VBox root = new VBox();
        root.getChildren().addAll(typeCmbx, nameTxtFld, valCmbx, valInputsHBox, confirmHBox);
        root.setPadding(new Insets(10, 50, 50, 50));
        
        //instantiate and show scene
        Scene scene = new Scene(root, 400, 200);          
        stage.setTitle("Variable Declaration");
        stage.setScene(scene);
        stage.showAndWait();
        
        //return input
        return new Object[]{type,name,val,exprHbx};
    }
    
    private void showAlert(AlertType alertType, String message){
        Alert customAlert = new Alert(alertType);
        customAlert.setContentText(message);
        customAlert.show();
    }

    private boolean validateUsedTypes(VarType[] invalidTypes, ArrayList<Var> usedVars) {
        boolean valid = true;
        int i = 0;
        while(valid && i < usedVars.size()){
            for(VarType t: invalidTypes){
                valid = !usedVars.get(i).getType().equals(t);
            }
            i++;
        }
        return valid;
    }

        
}

