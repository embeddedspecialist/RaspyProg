/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes.devices;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.FieldChecker;
import nodes.gui.GenericBlockDialog;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class CostantNode extends GenericNode implements ActionListener {

    private String value = "0.0";

    public CostantNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Costant;

        CreatePropertiesDialog();


    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("Costant");

        String pinLabel = "OUT";
        String pinID = "pin" + ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);

        //Aggiungo alla scena:
        getScene().addNode(this);
        getScene().addPin(this, newPin);


        ((AFONodeWidget) widget).setSubType(getValue());

        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void showPropertiesDialog() throws Exception {

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(value);
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if (((GenericBlockDialog) propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK) {
            value = ((GenericBlockDialog) propertiesDialog).txtField1.getText();
            ((AFONodeWidget) widget).setSubType(value);
        }
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText("NA");

        ((GenericBlockDialog) propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).lblField1.setText("Value:");
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(value);

        ((GenericBlockDialog) propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).txtField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnSendParam2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnReadParams.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnCancel.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnOk.addActionListener(this);
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";

        retVal += "NAME:COSTANTE,VALUE:" + value + "," + getCommonData() + "COMMENT:" + comment;


        return retVal;
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "Costant")) {
                    value = com.getValue("VAL");
                    ((GenericBlockDialog) propertiesDialog).txtField1.setText(value);
                    ((AFONodeWidget) widget).setSubType(value);
                }
            } else {
                return parseStandardBlockCmd(com);
            }
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnOk)) {

            if ((!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Valore Errato"))) {
                return;
            } 

            if ((kernel != null ) && (kernel.isConnected())) {
                Cmd com = generateBasicNodeCommand();
                com.putValue("BLOCKEXEC", "SetCostant");
                com.putValue("VAL", ((GenericBlockDialog) propertiesDialog).txtField1.getText());
                sendCommand(com);
            }
            
            propertiesDialog.setVisible(false);
            value = ((GenericBlockDialog) propertiesDialog).txtField1.getText();
            ((AFONodeWidget) widget).setSubType(value);

        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnReadParams)) {

            Cmd com = generateBasicNodeCommand();
            com.putValue("BLOCKEXEC", "GetCostant");
            sendCommand(com);

        }
          else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnCancel)) {
            propertiesDialog.setVisible(false);
          }
    }

    @Override
    public void setConnectionStatus(boolean isConnected) {
        //((GenericBlockDialog) propertiesDialog).btnSendParam1.setEnabled(isConnected);
        ((GenericBlockDialog) propertiesDialog).btnReadParams.setEnabled(isConnected);
    }
}

