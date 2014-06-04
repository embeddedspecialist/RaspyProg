/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.gui.GenericBlockDialog;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class LogicNode extends GenericNode {

    public LogicNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        //Estraggo il sottotipo
        
        for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()){
            if (mytype.toString().equals(label)){
                subType = mytype;
            }
        }

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Logic;
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Logic");

        String pinLabel = "OUT";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);

        //Aggiungo alla scena:
        getScene().addNode(this);
        getScene().addPin(this, newPin);

        pinLabel = "IN1";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN1");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        if (subType != CommonDefinitions.subBlockTypes.NOT){
            pinLabel = "IN2";
            pinID = "pin"+ ++scene.pinIDCounter;
            newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
            newPin.setAsBusPin(false);
            newPin.setPinIDString("IN1");
            newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
            getScene().addPin(this, newPin);

            pinList.add(newPin);
        }

        ((AFONodeWidget)widget).setSubType(subType.toString());

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText("NA");

        //Combo
        for (CommonDefinitions.logicTypes myType : CommonDefinitions.logicTypes.values()){
            ((GenericBlockDialog)propertiesDialog).comboSubType.addItem(myType.toString());
        }

        //((GenericBlockDialog)propertiesDialog).comboSubType.setSelectedIndex(subType.ordinal());
        ((GenericBlockDialog)propertiesDialog).lblCombo.setVisible(true);
        ((GenericBlockDialog)propertiesDialog).comboSubType.setVisible(true);

        ((GenericBlockDialog)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).txtField1.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).btnReadParams.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setVisible(false);
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog)propertiesDialog).comboSubType.setSelectedItem(subType.toString());
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog)propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK ){
            //Ricavo l'enum a cui si riferisce
            String newType = ((GenericBlockDialog)propertiesDialog).comboSubType.getSelectedItem().toString();
            for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()){
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

        //NAME:LOGICO, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:LOGICO,TYPE:"+subType.toString()+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }
}
