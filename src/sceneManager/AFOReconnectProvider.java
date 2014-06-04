/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Point;
import nodes.CommonDefinitions;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
class AFOReconnectProvider implements ReconnectProvider {

    private String edge;
    private AFOPin originalPin;
    private AFOPin replacementPin;
    
    private AFOGraphPinScene scene;
    private LayerWidget connectionLayer;
        
    public AFOReconnectProvider(AFOGraphPinScene scene, LayerWidget connectionLayer) {
        this.scene = scene;
        this.connectionLayer = connectionLayer;
    }

    public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
        Object object = scene.findObject (connectionWidget);
        edge = scene.isEdge (object) ? (String) object : null;
        originalPin = edge != null ? scene.getEdgeSource (edge) : null;
        return originalPin != null;
    }

    public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
            Object object = scene.findObject (connectionWidget);
            edge = scene.isEdge (object) ? (String) object : null;
            originalPin = edge != null ? scene.getEdgeTarget (edge) : null;
            return originalPin != null;
    }

    public void reconnectingStarted(ConnectionWidget arg0, boolean arg1) {
        
    }

    public void reconnectingFinished(ConnectionWidget arg0, boolean arg1) {
        
    }

    public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        Object object = scene.findObject (replacementWidget);
        replacementPin = scene.isPin(object) ? (AFOPin) object : null;
        if (replacementPin != null)
            return ConnectorState.ACCEPT;
        return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
    }

    public boolean hasCustomReplacementWidgetResolver(Scene arg0) {
        return false;
    }

    public Widget resolveReplacementWidget(Scene arg0, Point arg1) {
        return null;
    }

    public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        
        boolean busPinOK;
        
        if (replacementWidget == null)
        {
            busPinOK = false;
        }
        else
        { 
            busPinOK = (originalPin.isBusPin() == replacementPin.isBusPin());
        }
        
        if (!busPinOK){
            return;
        }
        

        int originalOrdinal = originalPin.getParentNode().getBlockType().ordinal();
        int replacementOrdinal = replacementPin.getParentNode().getBlockType().ordinal();
        boolean isOriginalDriver = originalOrdinal <= CommonDefinitions.blockTypes.THU.ordinal();
        boolean isReplacementDriver = replacementOrdinal <= CommonDefinitions.blockTypes.THU.ordinal();

        if (reconnectingSource) {
            if ( replacementPin.getPinType() != AFOPin.E_PinType.PIN_OUTPUT )
            {
                return;
            }

            //Se sono driver...
            if ( (isOriginalDriver ^ isReplacementDriver)){
                
                return;
            }
            else {
                scene.setEdgeSource (edge, replacementPin);
            }
        }
        else {
            //Se non e' un ingresso o il pin e' gia' occupato
            if ((replacementPin.getPinType() != AFOPin.E_PinType.PIN_INPUT) ||
                (scene.findPinEdges(replacementPin, false, true).size() > 0))
            {
                return;
            }

            //Se sono driver...
            if (isOriginalDriver ^ isReplacementDriver){
                return;
            }
            else {
                scene.setEdgeTarget (edge, replacementPin);
            }

        }
    }

}
