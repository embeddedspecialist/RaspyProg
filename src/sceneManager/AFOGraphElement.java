/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class AFOGraphElement {
    
    protected String label;
    protected String id;
    protected Widget widget;
    protected AFOGraphPinScene scene;
    
    public AFOGraphElement() {
        
    }
    
    public AFOGraphElement(String id, String label, AFOGraphPinScene scene) {
        this.id = id;
        this.label = label;
        this.scene = scene;
    }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    /**
     * @return the scene
     */
    public AFOGraphPinScene getScene() {
        return scene;
    }

    /**
     * @param scene the scene to set
     */
    public void setScene(AFOGraphPinScene scene) {
        this.scene = scene;
    }
    


}
