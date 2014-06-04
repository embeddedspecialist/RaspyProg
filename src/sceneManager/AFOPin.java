/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import org.netbeans.api.visual.widget.LabelWidget;

/**
 *
 * @author amirrix
 */
public class AFOPin  extends AFOGraphElement {

    /**
     * @return the parentNode
     */
    public AFONode getParentNode() {
        return parentNode;
    }


    public void setValue(String value){
         //Prondo la string del pin e ne ricavo la sottostringa fino al (
        String pinLabel = getPinNameWidget().getLabel();

        if (getPinType() == AFOPin.E_PinType.PIN_INPUT){
            int subStrIdx = pinLabel.indexOf("(");
            if (subStrIdx > 0){
                pinLabel = pinLabel.substring(0, subStrIdx);
            }
            getPinNameWidget().setLabel(pinLabel+"("+value+")");
        }
        else {
            int subStrIdx = pinLabel.indexOf(")");
            if (subStrIdx > 0){
                pinLabel = pinLabel.substring(subStrIdx+1);
            }

            getPinNameWidget().setLabel("("+value+")" + pinLabel);
        }
    }

    /**
     * @param parentNode the parentNode to set
     */
    public void setParentNode(AFONode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * @return the pinIDString
     */
    public String getPinIDString() {
        return pinIDString;
    }

    /**
     * @param pinIDString the pinIDString to set
     */
    public void setPinIDString(String pinIDString) {
        this.pinIDString = pinIDString;
    }

    public enum E_PinType
    {
        PIN_OUTPUT,
        PIN_COMMAND,
        PIN_INPUT
    }
    private E_PinType pinType;
    private boolean isBusInput;
    //Questo è il label widget, lo uso nel caso in cui debba rimuovere il pin
    //Fondamentalmente per i widget dei sottosistemi
    private LabelWidget pinNameWidget;
    private AFONode parentNode;
    //Questo identifica il pin di ingresso o uscita  come IN1,IN2 etc o OUT1,OUT2,
    private String pinIDString = "ERROR";
    public int pinAddress = 0;
    
    public AFOPin () {
        super();
    }
    
    public AFOPin (String ID, String label, E_PinType isInput) {
        super(ID,label,null);
        this.pinType = isInput;
        this.isBusInput = false;
    }
    
    public E_PinType getPinType() {
        return pinType;
    }

    public void setPinType(E_PinType pinType) {
        this.pinType = pinType;
    }
    
    public boolean isBusPin() {
        return isBusInput;
    }

    public void setAsBusPin(boolean isBusInput) {
        this.isBusInput = isBusInput;
    }

    public LabelWidget getPinNameWidget() {
        return pinNameWidget;
    }

    public void setPinNameWidget(LabelWidget pinLabelWidget) {
        this.pinNameWidget = pinLabelWidget;
    }
    
    

}
