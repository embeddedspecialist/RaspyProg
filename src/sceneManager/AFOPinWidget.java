/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author amirrix
 */
public class AFOPinWidget extends LabelWidget{
            
    private boolean isInput;

    public AFOPinWidget(Scene scene) {
        super(scene);
        
        this.setLayout (LayoutFactory.createHorizontalFlowLayout ());
        isInput = false;

    }
    
    public boolean getInputState() {
        return isInput;
    }

    public void setInputState(boolean isInput) {
        this.isInput = isInput;
    }

}
