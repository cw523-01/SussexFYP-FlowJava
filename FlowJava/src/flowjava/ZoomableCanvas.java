
package flowjava;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

/**
 * A canvas that can be zoomed via a ScrollEvent
 *
 * @author cwood
 */
public class ZoomableCanvas extends Pane{
    //the maximum and minimum scale value for the pane
    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .05d;
    
    //the current scale value of the pane
    DoubleProperty customScale = new SimpleDoubleProperty(1.0);
    
    public ZoomableCanvas() {
        //setup size and style of the pane
        setPrefSize(1000, 1000);
        setStyle("-fx-background-color: lightgrey;");
        
        //bind the scale of the pane to the customScale variable
        scaleXProperty().bind(customScale);
        scaleYProperty().bind(customScale);
    }
    
    /**
     * Set x/y scale
     * 
     * @param scale new value for x/y scale
     */
    public void setScale(double scale) {
        customScale.set(scale);
    }
    
    /**
     * returns the double value of the customScale DoubleProperty
     * @return double for canvas scale
     */
    public double getScaleVal() {
        return customScale.get();
    }
    
    /**
     * Mouse wheel handler for zooming to pivot point
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            //only zoom if ctrl is held down
            if (event.isShortcutDown()){
                //define variables to use in the zoom calculation
                double delta = 1.2;
                double scale = getScaleVal();
                
                //increase/decrease zoom based on mouse scroll direction
                if (event.getDeltaY() < 0) {
                    scale /= delta;
                } else {
                    scale *= delta;
                }
                
                //ensure scale isn't smaller than MIN_SCALE or greater than MAX_SCALE
                scale = Math.max(MIN_SCALE, scale);
                scale = Math.min(MAX_SCALE, scale);
                
                //update scale
                setScale(scale);
                
                //prevent event from reaching other sources
                event.consume();
            }
        }
    };
    
    /**
     * getter for on scroll event handler
     * 
     * @return on scroll event handler
     */
    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }
    
    
}
