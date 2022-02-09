/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

/**
 *
 * @author cwood
 */
abstract public class VertexController {
    public abstract int getMaxChildren();
    public abstract int getMaxParents();
    public abstract String getVertexLabel();
    public abstract String getDefaultStyle();
    public abstract String getSelectedStyle();
}
