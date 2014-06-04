/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import nodes.CommonDefinitions;
import nodes.devices.GenericNode;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author amirrix
 */
public class AFOGraphPinScene extends GraphPinScene<AFONode, String, AFOPin> implements ObjectSceneListener{

//    private AFOSystemManager systemManager;
    private LayerWidget background;
    public LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private LayerWidget pinLayer;
    public LayerWidget interactionLayer;

    private WidgetAction selectAction;
    private WidgetAction connectAction; 
    private WidgetAction reconnectAction;
    //Gestisce il movimento di piu' widget
    private WidgetAction moveAction;

    private AfoSelectProvider selectProvider;


    //Questo menu e' uno dei punti di contatto tra la libreria generica di gestione della scena visiva
    //e l'applicazione vera e propria. Attraverso un opportuno accessor posso settare l'azione dall'esterno
    private WidgetAction popupMenuAction;
    
    private WidgetAction editorAction;
    
    public long nodeIDcounter = 0;
    public long edgeIDcounter = 0;
    public long pinIDCounter = 0;

    public int drawEdgeColor = 0;

    private PopupMenuProvider widgetPopupMenu;

    public AFOGraphPinScene () {
//        collisionCollector = new AFOCollisionCollector(this);
        background = new LayerWidget (this);
        mainLayer = new LayerWidget (this);
        connectionLayer = new LayerWidget (this);
        pinLayer = new LayerWidget(this);
        interactionLayer = new LayerWidget(this);

        addChild(background);
        addChild (mainLayer);
        addChild (connectionLayer);
        addChild(pinLayer);
        addChild(interactionLayer);

        selectProvider = new AfoSelectProvider(this, mainLayer,interactionLayer);
        selectAction = ActionFactory.createSelectAction(selectProvider);
        connectAction = ActionFactory.createExtendedConnectAction(connectionLayer, new AfoConnectProvider(this, connectionLayer));
        reconnectAction = ActionFactory.createReconnectAction(new AFOReconnectProvider(this, connectionLayer));
        //Azione che consente di spostare piu' di un widget alla volta.
        moveAction = ActionFactory.createMoveAction (null, new MultiMoveProvider (this));

        editorAction = ActionFactory.createInplaceEditorAction (new LabelTextFieldEditor ());
        
        this.getActions ().addAction (ActionFactory.createRectangularSelectAction (this, background));
        this.getActions().addAction(ActionFactory.createZoomAction());

        //Aggiungo l'apertura delle proprieta' del widget su doppio click
        this.getActions().addAction (ActionFactory.createEditAction (new EditProvider() {
            @Override
            public void edit (Widget widget) {
                Set<Object> blockList = (Set<Object>) getSelectedObjects();
                Object[] blockArray = blockList.toArray();

                if (blockArray.length == 1){
                    if (blockArray[0] instanceof AFOGraphElement){
                        try {
                            ((GenericNode) blockArray[0]).showPropertiesDialog();
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }));
        
        this.setFont(getDefaultFont().deriveFont(8.0f));

        this.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);

        initGrids();
        
    }

    public void setWidgetPopupMenuActiom(PopupMenuProvider widgetPopup){
        this.widgetPopupMenu = widgetPopup;
    }

    public void setScenePopupMenuAction (PopupMenuProvider newPopup){
        popupMenuAction = ActionFactory.createPopupMenuAction(newPopup);
        this.getActions().addAction( popupMenuAction);
    }

    WidgetAction getEditorAction() {
        return ActionFactory.createInplaceEditorAction (new AFOGraphPinScene.LabelTextFieldEditor());
    }

    public AfoSelectProvider getSelectProvider(){
        return selectProvider;
    }

    public class LabelTextFieldEditor implements TextFieldInplaceEditor {

        @Override
        public boolean isEnabled (Widget widget) {
            return true;
        }

        @Override
        public String getText (Widget widget) {
            return ((LabelWidget) widget).getLabel ();
        }

        @Override
        public void setText (Widget widget, String text) {
            //systemManager.getProject().projectChanged = true;
            AFONode node = (AFONode) findObject(widget);
            //Ho cambiato il commento: se è un sottosistema devo aggiornare i file
            if (node.getBlockType() == CommonDefinitions.blockTypes.Subsystem){
                //Devo cercare nella lista dei sottosistemi se ne esiste uno con questo nome
                //Perchè i sottosistemi sono salvati in base al loro nome
                Collection<AFONode> nodeCollection = getNodes();
                Iterator<AFONode> nodeIt = nodeCollection.iterator();
                
                while (nodeIt.hasNext()){
                    if (nodeIt.next().getComment().equals(text)){
                        JOptionPane.showMessageDialog(null,"Duplicated Subsystem name", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            ((LabelWidget) widget).setLabel (text);
            node.setComment(text);
        }

    }

    @Override
    protected Widget attachNodeWidget(AFONode node) {
        Widget widget = node.getWidget();

        mainLayer.addChild (widget);
        
        widget.getActions().addAction(selectAction);
        widget.getActions().addAction(moveAction);
        widget.getActions().addAction(ActionFactory.createAlignWithMoveAction(mainLayer, interactionLayer, null));

        

        if (widgetPopupMenu != null){
            widget.getActions ().addAction (ActionFactory.createPopupMenuAction(widgetPopupMenu));
        }
        else{
            JOptionPane.showMessageDialog(null, "WidegtPopupMenu nullo!!!!");
        }

        return widget;
    }

    @Override
    protected Widget attachEdgeWidget(String edge) {
        //systemManager.getProject().projectChanged = true;
        ConnectionWidget connectionWidget = new ConnectionWidget(this);

        if (drawEdgeColor == 1) {
            connectionWidget.setLineColor(Color.RED);
        }
        else if (drawEdgeColor == 2) {
            connectionWidget.setLineColor(Color.GREEN);
        }
        else {
            connectionWidget.setLineColor(Color.BLUE);
        }
        connectionWidget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionWidget.setEndPointShape (PointShape.SQUARE_FILLED_BIG);
        connectionWidget.setRouter (new AFORouter(this));
        connectionWidget.setRoutingPolicy(ConnectionWidget.RoutingPolicy.DISABLE_ROUTING_UNTIL_END_POINT_IS_MOVED);
        connectionWidget.setPaintControlPoints (true);
        connectionWidget.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
        connectionWidget.setControlPointsCursor (Cursor.getPredefinedCursor (Cursor.MOVE_CURSOR));
        connectionWidget.setControlPointCutDistance(5);
        
        
        //Azioni
        connectionWidget.getActions ().addAction (createObjectHoverAction ());
        connectionWidget.getActions ().addAction (createSelectAction ());
        connectionWidget.getActions ().addAction (reconnectAction);
        connectionWidget.getActions ().addAction (ActionFactory.createPopupMenuAction(widgetPopupMenu));
        connectionWidget.getActions ().addAction (ActionFactory.createAddRemoveControlPointAction (1.0, 5.0, ConnectionWidget.RoutingPolicy.DISABLE_ROUTING_UNTIL_END_POINT_IS_MOVED));
//        connectionWidget.getActions ().addAction (ActionFactory.createMoveControlPointAction (ActionFactory.createOrthogonalMoveControlPointProvider(), ConnectionWidget.RoutingPolicy.DISABLE_ROUTING_UNTIL_END_POINT_IS_MOVED));
        connectionWidget.getActions ().addAction (ActionFactory.createMoveControlPointAction (ActionFactory.createOrthogonalMoveControlPointProvider(), ConnectionWidget.RoutingPolicy.UPDATE_END_POINTS_ONLY));
        
        connectionLayer.addChild (connectionWidget);
        return connectionWidget;

    }

    @Override
    protected Widget attachPinWidget(AFONode node, AFOPin pin) {

        Widget pinWidget = pin.getWidget();
        pinWidget.getActions().addAction(connectAction);
        
        return pinWidget;
        
    }
    
    /**
     * Trova tutti i nodi connessi in uscita al pin dato
     * @param pin
     * @return lista dei nodi connessi la pin come target
     */
    public List<AFONode> getNodesConnectedAtPin(AFOPin pin) {
        List<AFONode> nodeList = new ArrayList<AFONode>();
        Collection<String> pinEdges = this.findPinEdges(pin, true, false);
        Iterator<String> edgeIt = pinEdges.iterator();
        
        while (edgeIt.hasNext()) {
            nodeList.add(getPinNode(getEdgeTarget(edgeIt.next())));
        }
        
        return nodeList;
    }

    public void initGrids(){
        try {
            Image sourceImage = ImageUtilities.loadImage("sceneManager/resources/paper_grid17.png"); // NOI18N
            int width = sourceImage.getWidth(null);
            int height = sourceImage.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(sourceImage, 0, 0, null);
            graphics.dispose();
            TexturePaint PAINT_BACKGROUND = new TexturePaint(image, new Rectangle(0, 0, width, height));
            setBackground(PAINT_BACKGROUND);
            repaint();
            revalidate(false);
            validate();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void attachEdgeSourceAnchor(String edge, AFOPin oldSourcePin, AFOPin sourcePin) {
        
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createDirectionalAnchor(findWidget (sourcePin),AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
    }

    @Override
    protected void attachEdgeTargetAnchor(String edge, AFOPin oldTargetPin, AFOPin targetPin) {
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createDirectionalAnchor(findWidget (targetPin),AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
    }

    public void objectAdded(ObjectSceneEvent arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void objectRemoved(ObjectSceneEvent arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void objectStateChanged(ObjectSceneEvent arg0, Object arg1, ObjectState arg2, ObjectState arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void selectionChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2) {
        System.out.println("Stato cambiato");
    }

    public void highlightingChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void hoverChanged(ObjectSceneEvent arg0, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void focusChanged(ObjectSceneEvent arg0, Object arg1, Object arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}