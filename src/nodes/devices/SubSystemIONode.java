/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.MyGraphPinScene;
import nodes.gui.DlgSubSystemIO;
import org.netbeans.api.visual.widget.Widget;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class SubSystemIONode extends GenericNode{
    
    private Widget parentIOWidget = null;
    private Widget parentIONameWidget = null;
    private AFOPin parentPin = null;
    MyGraphPinScene parentScene;

    private int ioIndex;

    public SubSystemIONode(String id, String label, AFOGraphPinScene scene, int IOindex) {
        super(id, label+IOindex, scene);
        ioIndex = IOindex;
        if (label.equals(CommonDefinitions.blockTypes.IN.toString())){
            blockType = CommonDefinitions.blockTypes.IN;
        }
        else{
            blockType = CommonDefinitions.blockTypes.OUT;
        }

        createWidget();
        CreatePropertiesDialog();

    }

    public Widget getParentIONameWidget() {
        return parentIONameWidget;
    }

    public void setParentIONameWidget(Widget parentIONameWidget) {
        this.parentIONameWidget = parentIONameWidget;
    }

    public Widget getParentIOWidget() {
        return parentIOWidget;
    }

    public void setParentIOWidget(Widget parentIOWidget) {
        this.parentIOWidget = parentIOWidget;
    }

    public AFOPin getParentPin() {
        return parentPin;
    }

    public void setParentPin(AFOPin parentPin) {
        this.parentPin = parentPin;
    }

    private void createWidget() {
        String pinLabel, pinID;
        AFOPin newPin;
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(label);

        getScene().addNode(this);

        if (blockType == CommonDefinitions.blockTypes.IN){

                pinLabel = "IN";
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
                newPin.setAsBusPin(false);
                newPin.setPinIDString("IN");
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
        }
        else {
                pinLabel = "OUT";
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
                newPin.setAsBusPin(false);
                newPin.setPinIDString("OUT");
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
        }
        ((AFONodeWidget) widget).setSubType(" ");
        //((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    public void setParentScene(MyGraphPinScene scene) {
        parentScene = scene;
    }

    public MyGraphPinScene getParentScene(){
        return parentScene;
    }

    public int getIoIndex() {
        return ioIndex;
    }

    @Override
    public void setPinValue(String pinIdx, String value){
        try {
            getPinByID(pinIdx).setValue(value);
            getScene().validate();

            if (blockType == CommonDefinitions.blockTypes.IN){
                sendValueToChart("IN", value);
            }
        }
        catch (Exception ex){

        }
    }


    @Override
    public void showPropertiesDialog() throws Exception{
       ((DlgSubSystemIO)propertiesDialog).txtLabel.setText(((AFONodeWidget)widget).getModuleComment().getLabel());
       propertiesDialog.setVisible(true);

       if ( ((DlgSubSystemIO)propertiesDialog).getReturnStatus() == DlgSubSystemIO.RET_OK){
           comment = ((DlgSubSystemIO)propertiesDialog).txtLabel.getText();
           if (!comment.equals(CommonDefinitions.DEFAULT_NODE_COMMENT)){
               ((AFONodeWidget)widget).getModuleComment().setLabel(comment);
               parentPin.getPinNameWidget().setLabel(comment);
               ((AFONodeWidget)widget).getModuleComment().setLabel(comment);
           }
       }
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new DlgSubSystemIO(null, true);

    }
}




