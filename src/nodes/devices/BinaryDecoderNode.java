/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.gui.GenericBlockDialog;
import nodes.gui.GenericBlockDialog2;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class BinaryDecoderNode extends GenericNode {

    public BinaryDecoderNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        blockType = CommonDefinitions.blockTypes.BinaryDecoder;

        CreateWidget();
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Decoder");

        getScene().addNode(this);

        String pinLabel = "IN";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        for (int i = 1; i <= 4; i++){
            pinLabel = "OUT"+i;
            pinID = "pin"+ ++scene.pinIDCounter;
            newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
            newPin.setAsBusPin(false);
            newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
            newPin.setPinIDString("OUT"+i);
            pinList.add(newPin);
            getScene().addPin(this, newPin);
        }


        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog2(null, true);

        ((GenericBlockDialog2)propertiesDialog).comboSubType.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).lblCombo.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam1.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).btnReadParams.setVisible(false);
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

        //NAME:BINARYENCDEC, TYPE, INPUTx, OUTPUTx, ADDR, COMMENT
        retVal +="NAME:BINARYENCDEC,TYPE:DEC"+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }
}
