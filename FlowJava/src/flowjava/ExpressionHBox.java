/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Node;

/**
 *
 * @author cwood
 */
public class ExpressionHBox extends HBox{
    
    Text leftBracket = new Text("(");
    
    VBox lExpressionVBox = new VBox();
    Text lExpressionTxt = new Text("Left Expression");
    TextField lExpressionTxtFld = new TextField();
    Button lExpressionBtn = new Button("*Switch To Subexpression*");
    
    VBox operatorVBox = new VBox();
    ComboBox<String> operatorCmbx = new ComboBox<>(); 
    Text operatorTxt = new Text("Operator");
    
    Button deleteExpressionBtn = new Button("*Del Subexpr*");
    
    VBox rExpressionVBox = new VBox();
    Text rExpressionTxt = new Text("Right Expression");
    TextField rExpressionTxtFld = new TextField();
    Button rExpressionBtn = new Button("*Switch To Subexpression*");
    
    HashMap<String, String> opMap = new HashMap<>();;
    
    Text rightBracket = new Text(")");
    
    public ExpressionHBox(Boolean isSubexpression){
        opMap.put("add/concat","+");
        opMap.put("subtract","-");
        opMap.put("times", "*");
        opMap.put("divide", "/");
        opMap.put("mod", "%");
        opMap.put("equals", "==");
        opMap.put("string variable equals", ".equals(");
        opMap.put("not equals", "!=");
        opMap.put("or", "||");
        opMap.put("and", "&&");
        
        setAlignment(Pos.CENTER);
        
        lExpressionVBox.setPadding(new Insets(0, 5, 0, 5));
        lExpressionTxtFld.setOnKeyReleased(e -> {
            CreateExprDialog.updateExprText();
        });
        lExpressionTxtFld.setPromptText("value or variable name");
        lExpressionBtn.setOnAction(e ->{
            getChildren().remove(lExpressionVBox);
            
            ExpressionHBox newSubexpression = new ExpressionHBox(true);
            
            getChildren().add(1, newSubexpression);
            CreateExprDialog.updateExprText();
        });
        lExpressionVBox.getChildren().addAll(lExpressionTxt, lExpressionTxtFld, lExpressionBtn);
        
        operatorVBox.setPadding(new Insets(0, 5, 0, 5));
        operatorVBox.setAlignment(Pos.TOP_CENTER);
        operatorCmbx.setPromptText("operator");
        ArrayList<String> opKeys = new ArrayList<>();
        opKeys.addAll(opMap.keySet());
        java.util.Collections.sort(opKeys);
        
        operatorCmbx.setItems(FXCollections.observableArrayList(opKeys));
        operatorCmbx.setOnAction(e -> {
            CreateExprDialog.updateExprText();
        });
        operatorVBox.getChildren().addAll(operatorTxt, operatorCmbx);
        
        if(isSubexpression){
            deleteExpressionBtn.setOnAction(e ->{
                ((ExpressionHBox)getParent()).resetExpression(this);
                CreateExprDialog.updateExprText();
            });
            operatorVBox.getChildren().add(deleteExpressionBtn);
        }
        
        rExpressionVBox.setPadding(new Insets(0, 5, 0, 5));
        rExpressionVBox.setAlignment(Pos.TOP_RIGHT);
        rExpressionTxtFld.setOnKeyReleased(e -> {
            CreateExprDialog.updateExprText();
        });
        rExpressionTxtFld.setPromptText("value or variable name");
        rExpressionBtn.setOnAction(e ->{
            getChildren().remove(rExpressionVBox);
            
            ExpressionHBox newSubexpression = new ExpressionHBox(true);
            
            getChildren().add(3, newSubexpression);
            CreateExprDialog.updateExprText();
        });
        rExpressionVBox.getChildren().addAll(rExpressionTxt, rExpressionTxtFld, rExpressionBtn);
        
        getChildren().addAll(leftBracket, lExpressionVBox, operatorVBox, rExpressionVBox, rightBracket);
    }
    
    public String getExprString(){
        if(operatorCmbx.getValue() == null){
            if(getChildren().get(1) instanceof ExpressionHBox && getChildren().get(3) instanceof ExpressionHBox){
                return "(" + ((ExpressionHBox)getChildren().get(1)).getExprString() + "  " + ((ExpressionHBox)getChildren().get(3)).getExprString() + ")";
            }
            if(getChildren().get(1) instanceof ExpressionHBox){
                return "(" + ((ExpressionHBox)getChildren().get(1)).getExprString() + "  " + rExpressionTxtFld.getText() + ")";
            }
            if(getChildren().get(3) instanceof ExpressionHBox){
                return "(" + lExpressionTxtFld.getText() + "  " + ((ExpressionHBox)getChildren().get(3)).getExprString() + ")";
            }
            return "(" + lExpressionTxtFld.getText() + "  " + rExpressionTxtFld.getText() + ")";
        }
        if (getChildren().get(1) instanceof ExpressionHBox && getChildren().get(3) instanceof ExpressionHBox) {
            if(operatorCmbx.getValue().equals("string variable equals")){
                return "(" + ((ExpressionHBox) getChildren().get(1)).getExprString()+ opMap.get(operatorCmbx.getValue())+ ((ExpressionHBox) getChildren().get(3)).getExprString() + "))";
            } else {
                return "(" + ((ExpressionHBox) getChildren().get(1)).getExprString() + " " + opMap.get(operatorCmbx.getValue()) + " " + ((ExpressionHBox) getChildren().get(3)).getExprString() + ")";
            }
        }
        if (getChildren().get(1) instanceof ExpressionHBox) {
            if(operatorCmbx.getValue().equals("string variable equals")){
                return "(" + ((ExpressionHBox) getChildren().get(1)).getExprString() + opMap.get(operatorCmbx.getValue()) + rExpressionTxtFld.getText() + "))";
            } else {
                return "(" + ((ExpressionHBox) getChildren().get(1)).getExprString() + " " + opMap.get(operatorCmbx.getValue()) + " " + rExpressionTxtFld.getText() + ")";
            }
        }
        if (getChildren().get(3) instanceof ExpressionHBox) {
            if(operatorCmbx.getValue().equals("string variable equals")){
                return "(" + lExpressionTxtFld.getText() + opMap.get(operatorCmbx.getValue()) + ((ExpressionHBox) getChildren().get(3)).getExprString() + "))";
            } else {
                return "(" + lExpressionTxtFld.getText() + " " + opMap.get(operatorCmbx.getValue()) + " " + ((ExpressionHBox) getChildren().get(3)).getExprString() + ")";
            }
        }
        if(operatorCmbx.getValue().equals("string variable equals")){
            return "(" + lExpressionTxtFld.getText() + opMap.get(operatorCmbx.getValue()) + rExpressionTxtFld.getText() + "))";
        } else {
            return "(" + lExpressionTxtFld.getText() + " " + opMap.get(operatorCmbx.getValue()) + " " + rExpressionTxtFld.getText() + ")";
        }
    }
    
    public void resetExpression(Node n){
        int i = getChildren().indexOf(n);
        getChildren().remove(n);
        if(i == 1){
            getChildren().add(i, lExpressionVBox);
        }
        else {
            getChildren().add(i, rExpressionVBox);
        }
    }
    
    public Boolean isComplete(){
        boolean isValid;
        if(getChildren().get(1) instanceof ExpressionHBox){
            isValid = ((ExpressionHBox)getChildren().get(1)).isComplete();
        }
        else{
            isValid = !lExpressionTxtFld.getText().isEmpty();
        }
        if(isValid){
            isValid = !(operatorCmbx.getValue() == null);
        }
        if(getChildren().get(3) instanceof ExpressionHBox && isValid){
            isValid = ((ExpressionHBox)getChildren().get(3)).isComplete();
        }
        else if(isValid){
            isValid = !rExpressionTxtFld.getText().isEmpty();
        }
        return isValid;
    }
    
    

    
    
}
