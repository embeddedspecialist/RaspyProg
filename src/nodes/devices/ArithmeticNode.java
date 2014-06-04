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
public class ArithmeticNode extends GenericNode {

    public ArithmeticNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        subType = CommonDefinitions.subBlockTypes.ADD;
        //Estraggo il sottotipo
        for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()){
            if (mytype.toString().equals(label)){
                subType = mytype;
                break;
            }
        }

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Arithmetic;

        CreatePropertiesDialog();
    }

    
    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Arithmetic");

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

        ((AFONodeWidget)widget).setSubType(CommonDefinitions.subBlockStrings[subType.ordinal()]);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog2(null, true);

        //Combo
        for (CommonDefinitions.arithmeticTypes myType : CommonDefinitions.arithmeticTypes.values()){
            ((GenericBlockDialog2)propertiesDialog).comboSubType.addItem(CommonDefinitions.arithmeticStrings[myType.ordinal()]);
        }

        //((GenericBlockDialog)propertiesDialog).comboSubType.setSelectedIndex(subType.ordinal());
        ((GenericBlockDialog2)propertiesDialog).lblCombo.setVisible(true);
        ((GenericBlockDialog2)propertiesDialog).comboSubType.setVisible(true);

        ((GenericBlockDialog2)propertiesDialog).lblField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField1.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam1.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).txtField2.setVisible(false);
        ((GenericBlockDialog2)propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog2)propertiesDialog).btnReadParams.setVisible(false);
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog2)propertiesDialog).txtAddress.setText(addressList.get(0).toString());

        ((GenericBlockDialog2)propertiesDialog).comboSubType.setSelectedItem(((AFONodeWidget)widget).getSubType());

        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog2)propertiesDialog).getReturnStatus() == GenericBlockDialog2.RET_OK ){
            //Ricavo l'enum a cui si riferisce
            String newType = ((GenericBlockDialog2)propertiesDialog).comboSubType.getSelectedItem().toString();
            for (CommonDefinitions.subBlockTypes mytype : CommonDefinitions.subBlockTypes.values()){
                if (CommonDefinitions.subBlockStrings[mytype.ordinal()].equals(newType)){
                    subType = mytype;
                }
            }
            
            ((AFONodeWidget)widget).setSubType(CommonDefinitions.subBlockStrings[subType.ordinal()]);
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:OPERAZIONE, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:OPERAZIONE,TYPE:"+subType.toString()+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }

}
