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
 * A specialised HBox that has components that can be used to build a valid java expression
 * 
 * @author cwood
 */
public class ExpressionHBox extends HBox{
    //text labele for the far left opening bracket
    Text leftBracket = new Text("(");
    //VBox to hold the left hand subexpression of the expression
    VBox lExpressionVBox = new VBox();
    //text label for left hand expression
    Text lExpressionTxt = new Text("Left Expression");
    //text field for entering left hand subexpression
    TextField lExpressionTxtFld = new TextField();
    //button to switch left hand subexpression to an expression HBox
    Button lExpressionBtn = new Button("*Switch To Subexpression*");
    //Vbox to hold elements for the expressions operator
    VBox operatorVBox = new VBox();
    //combobox for choosing the expressions operator
    ComboBox<String> operatorCmbx = new ComboBox<>();
    //text label for operator feilds
    Text operatorTxt = new Text("Operator");
    //button to delete this expression if it is a subexpression
    Button deleteExpressionBtn = new Button("*Del Subexpr*");
    //VBox to hold the right hand subexpression of the expression
    VBox rExpressionVBox = new VBox();
    //text label for right hand expression
    Text rExpressionTxt = new Text("Right Expression");
    //text field for entering left hand subexpression
    TextField rExpressionTxtFld = new TextField();
    //button to switch right hand subexpression to an expression HBox
    Button rExpressionBtn = new Button("*Switch To Subexpression*");
    //operator map for paring operators symbols with their names 
    HashMap<String, String> opMap = new HashMap<>();;
    //text labele for the far right closing bracket
    Text rightBracket = new Text(")");
    
    /**
     * constructor for objects of class ExpressionHBox
     * 
     * @param isSubexpression whether the expression is a subexpression
     */
    public ExpressionHBox(Boolean isSubexpression){
        //add operators symbols and their names to opMap 
        opMap.put("add/concat","+");
        opMap.put("subtract","-");
        opMap.put("times", "*");
        opMap.put("divide", "/");
        opMap.put("mod", "%");
        opMap.put("equals", "==");
        opMap.put("greater than", ">");
        opMap.put("less than", "<");
        opMap.put("greater than or equal", ">=");
        opMap.put("less than or equal", "<=");
        opMap.put("string variable equals", ".equals(");
        opMap.put("not equals", "!=");
        opMap.put("or", "||");
        opMap.put("and", "&&");
        
        setAlignment(Pos.CENTER);
        
        //setup left subexpression elements
        lExpressionVBox.setPadding(new Insets(0, 5, 0, 5));
        lExpressionTxtFld.setOnKeyReleased(e -> {
            CreateExprDialog.updateExprText();
        });
        lExpressionTxtFld.setPromptText("value or variable name");
        lExpressionBtn.setOnAction(e ->{
            //switch lExpressionVBox with a new ExpressionHbox
            getChildren().remove(lExpressionVBox);
            ExpressionHBox newSubexpression = new ExpressionHBox(true);
            getChildren().add(1, newSubexpression);
            CreateExprDialog.updateExprText();
        });
        lExpressionVBox.getChildren().addAll(lExpressionTxt, lExpressionTxtFld, lExpressionBtn);
        
        //set up operator elements
        operatorVBox.setPadding(new Insets(0, 5, 0, 5));
        operatorVBox.setAlignment(Pos.TOP_CENTER);
        operatorCmbx.setPromptText("operator");
        //get list of operator names from opMap
        ArrayList<String> opKeys = new ArrayList<>();
        opKeys.addAll(opMap.keySet());
        //sort operator names then add them to operatorCmbx
        java.util.Collections.sort(opKeys);
        operatorCmbx.setItems(FXCollections.observableArrayList(opKeys));
        operatorCmbx.setOnAction(e -> {
            CreateExprDialog.updateExprText();
        });
        operatorVBox.getChildren().addAll(operatorTxt, operatorCmbx);
        
        //if expression is a subexpression
        if(isSubexpression){
            //add button to delete this subexpression from the expression
            deleteExpressionBtn.setOnAction(e ->{
                ((ExpressionHBox)getParent()).resetExpression(this);
                CreateExprDialog.updateExprText();
            });
            operatorVBox.getChildren().add(deleteExpressionBtn);
        }
        
        //setup right subexpression elements
        rExpressionVBox.setPadding(new Insets(0, 5, 0, 5));
        rExpressionVBox.setAlignment(Pos.TOP_RIGHT);
        rExpressionTxtFld.setOnKeyReleased(e -> {
            CreateExprDialog.updateExprText();
        });
        rExpressionTxtFld.setPromptText("value or variable name");
        rExpressionBtn.setOnAction(e ->{
            //switch rExpressionVBox with a new ExpressionHbox
            getChildren().remove(rExpressionVBox);
            ExpressionHBox newSubexpression = new ExpressionHBox(true);
            getChildren().add(3, newSubexpression);
            CreateExprDialog.updateExprText();
        });
        rExpressionVBox.getChildren().addAll(rExpressionTxt, rExpressionTxtFld, rExpressionBtn);
        
        getChildren().addAll(leftBracket, lExpressionVBox, operatorVBox, rExpressionVBox, rightBracket);
    }
    
    /**
     * returns a string representing the current expression
     * 
     * @return expression as a String
     */
    public String getExprString(){
        //if operator is empty
        if(operatorCmbx.getValue() == null){
            //of left and right subexpressions are in a ExpressionHBox
            if(getChildren().get(1) instanceof ExpressionHBox && getChildren().get(3) instanceof ExpressionHBox){
                //return left subexpression from ExpressionHBox  next to right subexpression from ExpressionHBox in brackets
                return "(" + ((ExpressionHBox)getChildren().get(1)).getExprString() + "  " + ((ExpressionHBox)getChildren().get(3)).getExprString() + ")";
            }
            //if only left subexpressions is in a ExpressionHBox
            if(getChildren().get(1) instanceof ExpressionHBox){
                //return left subexpression from ExpressionHBox  next to right subexpression from rExpressionTxtFld in brackets
                return "(" + ((ExpressionHBox)getChildren().get(1)).getExprString() + "  " + rExpressionTxtFld.getText() + ")";
            }
            //if only right subexpressions is in a ExpressionHBox
            if(getChildren().get(3) instanceof ExpressionHBox){
                //return left subexpression from lExpressionTxtFld next to right subexpression from ExpressionHBox in brackets
                return "(" + lExpressionTxtFld.getText() + "  " + ((ExpressionHBox)getChildren().get(3)).getExprString() + ")";
            }
            //neither subexpression is in a ExpressionHBox so return left subexpression from lExpressionTxtFld next to right subexpression from rExpressionTxtFld in brackets
            return "(" + lExpressionTxtFld.getText() + "  " + rExpressionTxtFld.getText() + ")";
        }
        //use same method of generating expression string but with operator between subexpressions
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
    
    /**
     * removes an ExpressionHBox and resets it to either the lExpressionVBox or rExpressionVBox
     * 
     * @param n node to remove and reset
     */
    public void resetExpression(Node n){
        //reset node based on its index
        int i = getChildren().indexOf(n);
        getChildren().remove(n);
        if(i == 1){
            getChildren().add(i, lExpressionVBox);
        }
        else {
            getChildren().add(i, rExpressionVBox);
        }
    }
    
    /**
     * returns whether the input form and the input forms of all subexpressions have been completely filled out
     * 
     * @return boolean for whether the expression is complete
     */
    public Boolean isComplete(){
        boolean complete;
        //check clompleteness of left subexpression
        if(getChildren().get(1) instanceof ExpressionHBox){
            complete = ((ExpressionHBox)getChildren().get(1)).isComplete();
        }
        else{
            complete = !lExpressionTxtFld.getText().isEmpty();
        }
        //check completeness of operator
        if(complete){
            complete = !(operatorCmbx.getValue() == null);
        }
        //check completeness of right subexpression
        if(getChildren().get(3) instanceof ExpressionHBox && complete){
            complete = ((ExpressionHBox)getChildren().get(3)).isComplete();
        }
        else if(complete){
            complete = !rExpressionTxtFld.getText().isEmpty();
        }
        return complete;
    }
    
}
