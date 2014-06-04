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
public class IFNode extends GenericNode {



    public IFNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        subType = CommonDefinitions.subBlockTypes.IF_EQ;
        //Estraggo il sottotipo
        for (CommonDefinitions.subBlockTypes iftype : CommonDefinitions.subBlockTypes.values()){
            if (iftype.toString().equals(label)){
                subType = iftype;
                break;
            }
        }

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.IF;
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
         widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("IF");

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

        pinLabel = "IN2";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN2");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        ((AFONodeWidget)widget).setSubType(CommonDefinitions.subBlockStrings[getSubType().ordinal()]);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText("NA");

        //Combo
        for (CommonDefinitions.ifTypes myType : CommonDefinitions.ifTypes.values()){
            ((GenericBlockDialog)propertiesDialog).comboSubType.addItem(CommonDefinitions.ifStrings[myType.ordinal()]);
        }

        
        ((GenericBlockDialog)propertiesDialog).lblCombo.setVisible(true);
        ((GenericBlockDialog)propertiesDialog).comboSubType.setVisible(true);

        ((GenericBlockDialog)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).txtField1.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).txtField2.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).btnReadParams.setVisible(false);
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());

        ((GenericBlockDialog)propertiesDialog).comboSubType.setSelectedItem(((AFONodeWidget)widget).getSubType());

        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog)propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK ){
            //Ricavo l'enum a cui si riferisce
            String newType = ((GenericBlockDialog)propertiesDialog).comboSubType.getSelectedItem().toString();
            for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()){
                if (CommonDefinitions.subBlockStrings[mytype.ordinal()].equals(newType)){
                    subType = mytype;
                    break;
                }
            }

            ((AFONodeWidget)widget).setSubType(CommonDefinitions.subBlockStrings[getSubType().ordinal()]);
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:OPERAZIONE, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:IF,TYPE:"+getSubType().toString().substring(3)+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }

}
