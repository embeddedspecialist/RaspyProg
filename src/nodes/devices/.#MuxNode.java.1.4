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
public class MuxNode extends GenericNode {

    public MuxNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        blockType = CommonDefinitions.blockTypes.Mux;

        //Estraggo il sottotipo
        for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()) {
            if (mytype.toString().equals(label)) {
                subType = mytype;
            }
        }

        CreateWidget();
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("MUX");

        getScene().addNode(this);

        String pinLabel = "OUT";
        String pinID = "pin" + ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "SEL";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        int stopVal = 16;
        if (subType == CommonDefinitions.subBlockTypes.MUX_2) {
            stopVal = 2;
        } else if (subType == CommonDefinitions.subBlockTypes.MUX_4) {
            stopVal = 4;
        } else if (subType == CommonDefinitions.subBlockTypes.MUX_8) {
            stopVal = 8;
        }

        for (int i = 1; i <= stopVal; i++) {
            pinLabel = "IN" + i;
            pinID = "pin" + ++scene.pinIDCounter;
            newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
            newPin.setAsBusPin(false);
            newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
            newPin.setPinIDString("IN" + (i + 1));
            pinList.add(newPin);
            getScene().addPin(this, newPin);
        }


        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";

        //NAME:MUX, INPUT1:,INPUTx:, OUTPUT1:
        retVal += "NAME:MUX," + getCommonData() + "COMMENT:" + comment;


        return retVal;
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog2(null, true);

        ((GenericBlockDialog2) propertiesDialog).comboSubType.setVisible(false);
        ((GenericBlockDialog2) propertiesDialog).lblCombo.setVisible(false);

        ((GenericBlockDialog2) propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog2) propertiesDialog).txtField1.setVisible(false);

        ((GenericBlockDialog2) propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog2) propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog2) propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog2) propertiesDialog).btnSendParam2.setVisible(false);

    }

    /**
     * Questo blocco non ha configurazioni ma visualizzo l'indirizzo
     * @throws Exception
     */
    @Override
    public void showPropertiesDialog() throws Exception {
        ((GenericBlockDialog2) propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        propertiesDialog.setVisible(true);
    }

    @Override
    public void setConnectionStatus(boolean isConnected) {
        ((GenericBlockDialog2) propertiesDialog).btnReadParams.setEnabled(isConnected);
        // ((HystersisDialog)propertiesDialog).btnSend.setEnabled(isConnected);
    }
}
