
package flowjava;

import java.io.Serializable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

/**
 * Edge view for flowchart edges
 *
 * @author cwood
 */
public class EdgeView extends Group {
    //the x coordinates for each end of the edge
    private SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    //the y coordinates for each end of the edge
    private SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y2 = new SimpleDoubleProperty();
    //the body (main line) of the arrow 
    private Polyline mainLine = new Polyline();
    //the line for the lineHead of the arrow
    private Polyline lineHead = new Polyline();
    //boolean for if the lineHead of the arrow should be visible
    private SimpleBooleanProperty headVisible = new SimpleBooleanProperty(true);
    //a scaler for reducing the size of the ends of the arrow
    private final double ARROW_SCALER = 0;
    //the angle of the arrow lineHead tips from the main line
    private final double ARROWHEAD_ANGLE = Math.toRadians(20);
    //the lendth of the arrow lineHead tips
    private final double ARROWHEAD_LENGTH = 10;
    //the edge that this edge view displays
    private Edge edge;
    //whether the edge is removable by a user
    private boolean removable;
    
    /**
     * constructor for objects of class EdgeView
     * 
     * @param x1 initial x coordinate for start of the main line
     * @param y1 initial y coordinate for start of the main line
     * @param x2 initial x coordinate for end of the main line
     * @param y2 initial y coordinate for end of the main line
     * @param edge edge model that this view is visualising
     */
    public EdgeView(double x1, double y1, double x2, double y2, Edge edge){
        //assign the initial coordinates of the arrow
        this.x1.set(x1);
        this.y1.set(y1);
        this.x2.set(x2);
        this.y2.set(y2);
        
        removable = true;
        
        //assign edge
        this.edge = edge;
        
        //add the body and lineHead of the arrow to the group
        getChildren().addAll(mainLine, lineHead);
        
        mainLine.setStrokeWidth(2);
        lineHead.setStrokeWidth(2);
        
        //add a lister to update the groups style classes 
        getStyleClass().addListener((ListChangeListener<? super String>) c -> {
            c.next();
            for(Polyline p : new Polyline[]{mainLine, lineHead}){
                p.getStyleClass().addAll(c.getAddedSubList());
                p.getStyleClass().removeAll(c.getRemoved());
            }
        });
        
        //add listners to the coordinate objects for the update method
        this.x1.addListener((l,o,n) -> update());
        this.x2.addListener((l,o,n) -> update());
        this.y1.addListener((l,o,n) -> update());
        this.y2.addListener((l,o,n) -> update());
        
        //bind the arrow heads visibility with the lineHead visible boolean
        lineHead.visibleProperty().bind(headVisible);
        
        update();
    }
    
    /**
     * 
     */
    private void update() {
        //get new line coodinates using scale()
        double[] head = scale(x1.get(), y1.get(), x2.get(), y2.get());
        double[] tail = scale(x2.get(), y2.get(), x1.get(), y1.get());
        
        //assign new coordinates to variables
        double newX1 = head[0];
        double newY1 = head[1];
        double newX2 = tail[0];
        double newY2 = tail[1];

        //set the coordinates of the line
        mainLine.getPoints().setAll(newX1,newY1,newX2,newY2);

        //update the arrow lineHead components
        double theta = Math.atan2(newY2-newY1, newX2-newX1);
        //calculate and set the first coordinates of the arrow head
        double x = newX2 - Math.cos(theta + ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        double y = newY2 - Math.sin(theta + ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        this.lineHead.getPoints().setAll(x,y,newX2,newY2);
        //calculate and set the last coordinates of the arrow head
        x = newX2 - Math.cos(theta - ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        y = newY2 - Math.sin(theta - ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        this.lineHead.getPoints().addAll(x,y);
    }
    
    /**
     * takes the 4 coordinates of a line and returns the first two coordinates,
     * scaled by ARROW_SCALER
     * 
     * @param x1 the x coordinate of the point of the line to be scaled
     * @param y1 the y coordinate of the point of the line to be scaled
     * @param x2 the other x coordinate of the line
     * @param y2 the other y coordinate of the line
     * @return a set of scaled coordinates
     */
    private double[] scale(double x1, double y1, double x2, double y2){
        double theta = Math.atan2(y2-y1, x2-x1);
        //return the scaled coordinates
        return new double[]{
                x1 + Math.cos(theta) * ARROW_SCALER,
                y1 + Math.sin(theta) * ARROW_SCALER
        };
    }
    
    /**
     * setter for x coordinate of the start of the main line
     * 
     * @param x1 x coordinate
     */
    public void setX1(double x1) {
        this.x1.set(x1);
    }
    
    /**
     * setter for y coordinate of the start of the main line
     * 
     * @param y1 y coordinate
     */
    public void setY1(double y1) {
        this.y1.set(y1);
    }
    
    /**
     * setter for x coordinate of the end of the main line
     * 
     * @param x2 x coordinate
     */
    public void setX2(double x2) {
        this.x2.set(x2);
    }
    
    /**
     * setter for y coordinate of the end of the main line
     * 
     * @param y2 y coordinate
     */
    public void setY2(double y2) {
        this.y2.set(y2);
    }

    /**
     * add effects to the edges lines to show it is selected
     */
    public void select() {
        DropShadow selectedBorderEffect = new DropShadow(
                BlurType.THREE_PASS_BOX, Color.RED, 2, 1, 0, 0
        );
        mainLine.setEffect(selectedBorderEffect);
        lineHead.setEffect(selectedBorderEffect);
    }

    /**
     * remove selected line effects
     */
    public void deselect() {
        mainLine.setEffect(null);
        lineHead.setEffect(null);
    }

    /**
     * getter for the edge model of this view
     * 
     * @return edge model
     */
    public Edge getEdge() {
        return edge;
    }
    
    /**
     *  add effects to the edges lines to show users that they can't delete it
     */
    public void makeSubtle(){
        mainLine.getStrokeDashArray().addAll(5d, 5d, 5d, 5d);
        headVisible.set(false);
        mainLine.setOpacity(0.25);
    }

    /**
     * getter for if the edge is removable
     * 
     * @return boolean for if edge is removable
     */
    public boolean isRemovable() {
        return removable;
    }

    /**
     * setter for if the edge is removable
     * 
     * @param removable new value for removable
     */
    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    /**
     * setter for edge model 
     * 
     * @param edge new value for edge model
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }
    
    
}
