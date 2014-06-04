/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class AfoSelectProvider implements SelectProvider {

    private LayerWidget mainLayer;
    private LayerWidget interactionLayer;
    private AFOGraphPinScene scene;

    //Azione che processa un tasto quando un widget e' selezionato
    private WidgetAction keyAction;
    
    private int nodeID = 0;
    private int pinID = 0;

    private boolean connectionReset = false;
    
    public AfoSelectProvider(AFOGraphPinScene scene, LayerWidget mainLayer, LayerWidget interactionLayer) {
        this.mainLayer = mainLayer;
        this.interactionLayer = interactionLayer;
        this.scene = scene;
    }

    public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
        return false;
    }

    public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
        return true;
    }

    public void select(Widget relatedWidget, Point localLocation, boolean arg2)  {
        scene.setFocusedWidget(relatedWidget);

        if (keyAction != null){
            //Mah... mi sembra una cazzata!!
            Collection<AFONode> nodeList = scene.getNodes();
            for (AFONode node : nodeList){
                node.getWidget().getActions().removeAction(keyAction);
            }

            if (relatedWidget != null){
                relatedWidget.getActions().addAction(keyAction);
            }
        }

        //Quando clicco su qualcosa "riattacco" tutti i nodi...
        if (!connectionReset){
            //Qui dovrei riabilitare il routing nella scena...
            Collection<String> edgeList = scene.getEdges();
            Object[] sortedEdges = edgeList.toArray();
            Arrays.sort(sortedEdges);
            for (int i = 0; i < sortedEdges.length; i++) {
                ConnectionWidget conn = (ConnectionWidget) scene.findWidget(sortedEdges[i]);
                conn.setRoutingPolicy (ConnectionWidget.RoutingPolicy.DISABLE_ROUTING_UNTIL_END_POINT_IS_MOVED);
            }
            connectionReset = true;
        }
    }



    /**
     * @param keyAction the keyAction to set
     */
    public void setKeyAction(WidgetAction keyAction) {
        this.keyAction = keyAction;
    }

}
