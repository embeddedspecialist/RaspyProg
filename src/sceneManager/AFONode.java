/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import nodes.AFONodeWidget;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import nodes.CommonDefinitions;
import nodes.CommonDefinitions.blockTypes;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author amirrix
 */
public class AFONode extends AFOGraphElement implements Comparable{

    //Lista dei PIN del nodo
    protected List<AFOPin> pinList;

    //Tipo di blocco, e' la parte da sostituire per adattare l'interfaccia ad altri sistemi
    protected CommonDefinitions.blockTypes blockType;
        
    protected String comment = CommonDefinitions.DEFAULT_NODE_COMMENT;

    protected JDialog propertiesDialog = null;

    protected WidgetAction editorAction;

    protected CommonDefinitions.subBlockTypes subType;
    
    public AFONode (){
        super();
    }
    
    public AFONode(String id, String label, AFOGraphPinScene scene) {
        super(id,label, scene);
        
        pinList = new ArrayList<AFOPin>();

        editorAction = scene.getEditorAction();
        subType = CommonDefinitions.subBlockTypes.NONE;
    }

    public JDialog getPropertiesDialog(){
        return propertiesDialog;
    }

    public List<AFOPin> getPinList() {
        return pinList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        ((AFONodeWidget)(this.widget)).getModuleComment().setLabel(comment);
    }

    public blockTypes getBlockType() {
        return blockType;
    }

    public void setBlockType(blockTypes blockType) {
        this.blockType = blockType;
    }
    
    public int getNofInputPins(){
        int retVal = 0;
        
        for (int i = 0; i < pinList.size();i++){
            if (pinList.get(i).getPinType() != AFOPin.E_PinType.PIN_OUTPUT){
                retVal++;
            }
        }
        
        return retVal;
    }
    
    public int getNofOutputPins(){
        int retVal = 0;
        
        for (int i = 0; i < pinList.size(); i++){
            if (pinList.get(i).getPinType() == AFOPin.E_PinType.PIN_OUTPUT){
                retVal++;
            }
        }
        
        return retVal;
    }
    
    public void addPin(AFOPin newPin) {
        pinList.add(newPin);
    }
    
    public int getPinListSize() {

        return pinList.size();
    }
    
    public AFOPin getPinAt(int pinIndex) {
        
        try {
            return pinList.get(pinIndex);
        }
        catch (IndexOutOfBoundsException e) {
            //TBI
            
            return null;
        }
    }
    
    public void RotateNode()
    {
        if ( ((AFONodeWidget)widget).IsRotated() ){
            this.widget = ((AFONodeWidget)widget).GetDirectInstance();
        }
        else
        {
            this.widget = ((AFONodeWidget)widget).GetRotatedInstance();
        }
    }

    @Override
    public int compareTo(Object arg0) {
        AFONode node = (AFONode) arg0;
        
        return this.id.compareTo(node.getId());
        
    }

    public void showPropertiesDialog() throws Exception{
        //throw new Exception("Impossibile lanciare funzione base");
    }

    public Element serializeNode(Document xml,Element nodeElement) throws Exception {
        throw new Exception("Impossibile lanciare funzione base serializeNode");
    }

    public void deserializeNode(Node node) throws Exception{
        //throw new Exception("Impossibile lanciare funzione base deserializeNode");
    }

    /**
     * @return the subType
     */
    public CommonDefinitions.subBlockTypes getSubType() {
        return subType;
    }

    /**
     * @param subType the subType to set
     */
    public void setSubType(CommonDefinitions.subBlockTypes subType) {
        this.subType = subType;
    }

    public Widget getOverallWidget() {
        return ((AFONodeWidget)widget).GetModuleWidget();
    }
    
}


