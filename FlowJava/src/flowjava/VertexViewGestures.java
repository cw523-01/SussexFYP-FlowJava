/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

/**
 *
 * @author cwood
 */
public class VertexViewGestures {
    //values used for calculating drags
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;
    //the canvas in which the vertices are held
    private ZoomableCanvas canvas;
    //a list of all vertices in the application
    private ArrayList<Vertex> verticeList;
    
    public VertexViewGestures(ZoomableCanvas canvas, ArrayList<Vertex> verticeList){
        this.canvas = canvas;
        this.verticeList = verticeList;
    }
    
    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            //only handle events where left mouse is clicked
            if(!event.isPrimaryButtonDown())
                return;
            
            //store the source of the event in a variable
            VertexView vView = (VertexView) event.getSource();
            
            //provide visual confirmation to the user that the vertex has been clicked
            vView.toFront();
            vView.setOpacity(0.5);
            
            //set the mouse anchors to where the mouse clicked, incase the vertex is about to be dragged
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();

            //set the translate anchors to where the vertex is, incase the vertex is about to be dragged
            translateAnchorX = vView.getTranslateX();
            translateAnchorY = vView.getTranslateY();
        }
    };
    
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            //only handle events where left mouse is dragging
            if( !event.isPrimaryButtonDown()){
                return;
            }
            
            //store the current scale of the canvas
            double scale = canvas.getScaleVal();
            
            //get the source vertex
            VertexView vView = (VertexView) event.getSource();
            
            //translate the vertex accordingly
            vView.setTranslateX(Math.max(0,(translateAnchorX + ((event.getSceneX() - mouseAnchorX) / scale))));
            vView.setTranslateY(Math.max(0,(translateAnchorY + ((event.getSceneY() - mouseAnchorY) / scale))));
            
            //increase the size of the canvas if the vertex is reaching its bounds
            canvas.setPrefSize(Math.max(canvas.getPrefWidth(),(getFurthestVertexCoordinates().getKey() + 1000)), Math.max(canvas.getPrefHeight(),(getFurthestVertexCoordinates().getValue() + 1000)));
            
            //update all the edges connected to this vertex
            for(Pair<Edge,Boolean> c: vView.getVertex().getConnections()){
                EdgeView cView = c.getKey().getView();
                if(c.getValue()){
                    cView.setX1(vView.getTranslateX()+(vView.getWidth()/2));
                    cView.setY1(vView.getTranslateY()+vView.getHeight());
                }
                else{
                    cView.setX2(vView.getTranslateX()+(vView.getWidth()/2));
                    cView.setY2(vView.getTranslateY());
                }
            }
            
            //prevent event from reaching other sources
            event.consume();
        }
    };
    
    public Pair<Double,Double> getFurthestVertexCoordinates(){
        //initialise furthest coordinates as 0,0
        Double furthestX = 0.0;
        Double furthestY = 0.0;
        
        //for each vertex update furthest coordinates
        for(Vertex v: verticeList){
            furthestX = Math.max(furthestX, v.getView().getTranslateX());
            furthestY = Math.max(furthestY, v.getView().getTranslateY());
        }
        
        //return furthest found coordinates as a pair
        return(new Pair<>(furthestX, furthestY));
    }
    
    private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            //provide visual confirmation to the user that the vertex has been released
            VertexView vView = (VertexView) event.getSource();
            vView.setOpacity(1);
        }
    };
    
    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }
    
    public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
        return onMouseReleasedEventHandler;
    }
    
    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

}
