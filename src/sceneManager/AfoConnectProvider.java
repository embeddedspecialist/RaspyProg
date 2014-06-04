/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Point;
import nodes.CommonDefinitions;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
class AfoConnectProvider implements ConnectProvider {
    private AFOGraphPinScene scene;
    private LayerWidget connectionLayer;
    private int edgeTest = 0;

    public AfoConnectProvider(AFOGraphPinScene aThis, LayerWidget connectionLayer) {
        this.scene = aThis;
        this.connectionLayer = connectionLayer;
    }

    @Override
    public boolean isSourceWidget(Widget sourceWidget) {
        if (sourceWidget instanceof AFOPinWidget)
        {
            if (((AFOPinWidget)sourceWidget).getInputState())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else {
            return false;
        }
        
    }

    @Override
    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        
        boolean sourceIsBus, targetIsBus;
        
        if (targetWidget instanceof AFOPinWidget) {
            
            if (targetWidget != sourceWidget)
            {
                sourceIsBus = ((AFOPin)(scene.findObject(sourceWidget))).isBusPin();
                targetIsBus = ((AFOPin)scene.findObject(targetWidget)).isBusPin();

                if (sourceIsBus && targetIsBus && ((AFOPinWidget)targetWidget).getInputState()) {
                    return ConnectorState.ACCEPT;
                }
                else if ((!sourceIsBus) && (!targetIsBus) && ((AFOPinWidget)targetWidget).getInputState()) {
                    //Ora controllo cosa sto attaccando: devo vietare di collegare due driver, un IN con un OUT direttamente
                    if ( (((AFOPin)(scene.findObject(sourceWidget))).getParentNode().blockType.ordinal() <=
                            CommonDefinitions.blockTypes.THU.ordinal()) &&
                            (((AFOPin)(scene.findObject(targetWidget))).getParentNode().blockType.ordinal() <=
                            CommonDefinitions.blockTypes.THU.ordinal())
                        ) {
                        return ConnectorState.REJECT;
                    }
                    else if ( (((AFOPin)(scene.findObject(sourceWidget))).getParentNode().blockType ==
                            CommonDefinitions.blockTypes.IN) &&
                            (((AFOPin)(scene.findObject(targetWidget))).getParentNode().blockType ==
                            CommonDefinitions.blockTypes.OUT)
                        ) {
                        return ConnectorState.REJECT;
                    }
                    else {
                        return ConnectorState.ACCEPT;
                    }
                }
                else
                {
                    return ConnectorState.REJECT;
                }


//                if ( (sourceIsBus && targetIsBus && ((AFOPinWidget)targetWidget).getInputState()) ||
//                     ((!sourceIsBus) && (!targetIsBus) && ((AFOPinWidget)targetWidget).getInputState())
//                   )
//                {
//                    //Ora controllo se sto attaccando due device (cosa vietatissima!!!)
//                    if ( (((AFOPin)(scene.findObject(sourceWidget))).getParentNode().blockType.ordinal() <=
//                            CommonDefinitions.blockTypes.VLV.ordinal()) &&
//                            (((AFOPin)(scene.findObject(targetWidget))).getParentNode().blockType.ordinal() <=
//                            CommonDefinitions.blockTypes.VLV.ordinal())
//                        ) {
//                        return ConnectorState.REJECT;
//                    }
//                    else {
//                        return ConnectorState.ACCEPT;
//                    }
//                }
//                else {
//                    return ConnectorState.REJECT;
//                }
            }
            else
            {
                return ConnectorState.REJECT;
            }
        }
        else {
            return ConnectorState.REJECT_AND_STOP;
        }
    }

    @Override
    public boolean hasCustomTargetWidgetResolver(Scene arg0) {
        return false;
    }

    @Override
    public Widget resolveTargetWidget(Scene arg0, Point arg1) {
        return null;
    }

    @Override
    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        
        //Get source PIN
        AFOPin srcPin = ((AFOPin)scene.findObject(sourceWidget));
        //Get Target PIN
        AFOPin trgPin = ((AFOPin)scene.findObject(targetWidget));
        String edgeID = "edge";
        
        //Controllo subito se il pin di destinazione ha già un collegamento
        if (scene.findPinEdges(trgPin, false, true).size() > 0) {
            return;
        }
        
        edgeID+= ++scene.edgeIDcounter;
        try {
            if (srcPin.isBusPin())  {
                scene.drawEdgeColor = 1;
            }
            else
            {
                scene.drawEdgeColor = 0;
            }
        }
        catch (Exception ex) {

        }
        scene.addEdge(edgeID);
        scene.setEdgeSource (edgeID, srcPin);
        
        scene.setEdgeTarget (edgeID, trgPin);
        
        scene.validate();
        
    }

}
