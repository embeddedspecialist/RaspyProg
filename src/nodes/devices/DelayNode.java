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
import nodes.SceneSerializer;
import nodes.gui.GenericBlockDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class DelayNode extends GenericNode implements ActionListener {

    private String delayAmount = "0";

    public DelayNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.Delay;

        CreateWidget();
        CreatePropertiesDialog();


    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("Delay");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "OUT";
        String pinID = "pin" + ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "Start";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN1");
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        pinLabel = "Reset";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN2");
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        pinLabel = "Load";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN3");
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        ((AFONodeWidget) widget).setSubType(delayAmount);

        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText("NA");

        //Combo
        ((GenericBlockDialog) propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).lblField1.setText("Delay: ");
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(delayAmount);

        ((GenericBlockDialog) propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).btnReadParams.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnOk.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnCancel.addActionListener(this);

    }

    @Override
    public void showPropertiesDialog() throws Exception {

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(delayAmount);
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        // if (((GenericBlockDialog) propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK) {
        //   delayAmount = ((GenericBlockDialog) propertiesDialog).txtField1.getText();
        // }

        if (((GenericBlockDialog) propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK) {
            if ((!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Valore Errato"))) {
                return;
            }

            delayAmount = ((GenericBlockDialog) propertiesDialog).txtField1.getText();
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";

        //NAME:OPERAZIONE, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal += "NAME:DELAY,TIME:" + delayAmount + "," + getCommonData() + "COMMENT:" + comment;

        return retVal;
    }

    @Override
    public Element serializeNode(Document document, Element nodeElement) throws Exception {
        SceneSerializer.setAttribute(document, nodeElement, "delay", delayAmount);
        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception {
        delayAmount = SceneSerializer.getAttributeValue(node, "delay");
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "Delay")) {
                    delayAmount = com.getValue("VAL");
                    ((GenericBlockDialog) propertiesDialog).txtField1.setText(delayAmount);
                    ((AFONodeWidget) widget).setSubType(delayAmount);
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

            if ((!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Delay errato"))) {
                {
                    return;
                }
            }

            Cmd com = generateBasicNodeCommand();
            com.putValue("BLOCKEXEC", "SetDelay");
            com.putValue("DELAY", ((GenericBlockDialog) propertiesDialog).txtField1.getText());
            com.putValue("ADDRESS", addressList.get(0).toString());
            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
            propertiesDialog.setVisible(false);


        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnReadParams)) {

            Cmd com = generateBasicNodeCommand();
            com.putValue("BLOCKEXEC", "GetDelay");
            com.putValue("ADDRESS", addressList.get(0).toString());
            sendCommand(com);



        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnCancel)) {
            propertiesDialog.setVisible(false);
        }
    }

    public void setConnectionStatus(boolean isConnected) {

        ((GenericBlockDialog) propertiesDialog).btnReadParams.setEnabled(isConnected);

    }
}
