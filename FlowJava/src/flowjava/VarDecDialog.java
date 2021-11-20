
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
                showAlert(AlertType.ERROR, "empty values!");
            } else if (varNames.contains(nameTxtFld.getText())){
                showAlert(AlertType.ERROR, "variable name already used!");
            } else if(!nameTxtFld.getText().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")){
                showAlert(AlertType.ERROR, "variable name is invalid!");
            } else {
                if(valCmbx.getValue() == null){
                    showAlert(AlertType.ERROR, "please specify variable value!");
                    return;
                }
                switch(valCmbx.getValue()){
                    case "Null":
                        val = "null";
                        break;
                    case "Given Value":
                        val = valueTxtFld.getText();
                        break;
                        
                    case "Expression":
                        if(exprHbx == null){
                            showAlert(AlertType.ERROR, "please create expression!");
                            return;
                        }
                        String expr = exprHbx.getExprString();
                        val = expr;
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
}
