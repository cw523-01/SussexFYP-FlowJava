/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;


import javafx.scene.shape.Polyline;
import javafx.scene.Group;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 *
 * @author cwood
 */
public class Edge{
    private final EdgeController controller;
    private final EdgeView view;
    
    public Edge(double x1, double y1, double x2, double y2, EdgeController controller){
        //assign controller
        this.controller = controller;
        this.view = new EdgeView(x1,y1,x2,y2,this);
    }
    

    public EdgeController getController() {
        return controller;
    }

    EdgeView getView() {
        return view;
    }
    
}
