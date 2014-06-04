/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.gui.GenericBlockDialog2;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class GateNode extends GenericNode {

    public GateNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.Gate;
        
        CreateWidget();
        CreatePropertiesDialog();

        //propertiesDialog = new SimplePropertiesDlg(null, true,"DS18S20");
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("FF JK");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "Q";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "Q Neg";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT2");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "J";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN1");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        pinLabel = "K";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN2");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
         propertiesDialog = new GenericBlockDialog2(null, true);

        ((GenericBlockDialog2)propertiesDialog).txtAddress.setText("NA");

        //Combo
        for (CommonDefinitions.logicTypes myType : CommonDefinitions.logicTypes.values()){
            ((GenericBlockDialog2)propertiesDialog).comboSubType.addItem(myType.toString());
        }

        //((GenericBlockDialog)propertiesDialog).comboSubType.setSelectedIndex(subType.ordinal());
        ((GenericBlockDialog2)propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField1.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam2.setVisible(false);
    }

    /**
     * Questo blocco non ha configurazioni ma visualizzo l'indirizzo
     * @throws Exception
     */
    @Override
    public void showPropertiesDialog() throws Exception{
       ((GenericBlockDialog2)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
       propertiesDialog.setVisible(true);
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:LOGICO, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:GATE,TYPE:JK,"+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }
 @Override
    public void setConnectionStatus(boolean isConnected){
        ((GenericBlockDialog2)propertiesDialog).btnReadParams.setEnabled(isConnected);
       // ((HystersisDialog)propertiesDialog).btnSend.setEnabled(isConnected);
    }

}
