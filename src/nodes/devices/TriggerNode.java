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
public class TriggerNode extends GenericNode{

    CommonDefinitions.triggerTypes subType = CommonDefinitions.triggerTypes.RISE;

    public TriggerNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        blockType = CommonDefinitions.blockTypes.Trigger;

        //Estraggo il sottotipo
        for (CommonDefinitions.triggerTypes mytype : CommonDefinitions.triggerTypes.values()){
            if (mytype.toString().equals(label)){
                subType = mytype;
            }
        }

        CreateWidget();
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Trigger");

        getScene().addNode(this);

        String pinLabel = "IN";
        String pinID = "pin"+ ++scene.pinIDCounter;

        AFOPin newPin = new AFOPin(pinID, pinLabel,AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "OUT";
        pinID = "pin"+ ++scene.pinIDCounter;

        newPin = new AFOPin(pinID, pinLabel,AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        ((AFONodeWidget)widget).setSubType(subType.toString());
        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog2(null, true);

        //Combo
        for (CommonDefinitions.triggerTypes myType : CommonDefinitions.triggerTypes.values()){
            ((GenericBlockDialog2)propertiesDialog).comboSubType.addItem(myType.toString());
        }
        
        ((GenericBlockDialog2)propertiesDialog).comboSubType.setSelectedIndex(subType.ordinal());
        ((GenericBlockDialog2)propertiesDialog).lblCombo.setVisible(true);
        ((GenericBlockDialog2)propertiesDialog).comboSubType.setVisible(true);

        ((GenericBlockDialog2)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField1.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).btnReadParams.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam2.setVisible(false);
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog2)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog2)propertiesDialog).comboSubType.setSelectedIndex(subType.ordinal());
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog2)propertiesDialog).getReturnStatus() == GenericBlockDialog2.RET_OK ){
            //Ricavo l'enum a cui si riferisce
            String newType = ((GenericBlockDialog2)propertiesDialog).comboSubType.getSelectedItem().toString();
            for (CommonDefinitions.triggerTypes mytype : CommonDefinitions.triggerTypes.values()){
                if (mytype.toString().equals(newType)){
                    subType = mytype;
                }
            }

            ((AFONodeWidget)widget).setSubType(subType.toString());
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:TRIGGER, TYPE, INPUT1, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:TRIGGER,TYPE:"+subType.toString()+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }

}
