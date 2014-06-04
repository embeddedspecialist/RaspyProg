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
public class TimerNode extends GenericNode implements ActionListener {

    private String m_TimerID = "0";

    public TimerNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);


        blockType = CommonDefinitions.blockTypes.Clock;
        CreateWidget();
        CreatePropertiesDialog();
    }

    @Override
    public void showPropertiesDialog() throws Exception {

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(get_TimerID());
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if (((GenericBlockDialog) propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK) {
            m_TimerID = ((GenericBlockDialog) propertiesDialog).txtField1.getText();
            ((AFONodeWidget) widget).setSubType(get_TimerID());
        }
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog) propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).lblField1.setText("TimerID :");
        ((GenericBlockDialog) propertiesDialog).txtField1.setText(get_TimerID());

        ((GenericBlockDialog) propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).txtField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).btnSendParam1.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnReadParams.addActionListener(this);

        ((GenericBlockDialog) propertiesDialog).btnOk.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnCancel.addActionListener(this);

    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("Clock");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "Digital";
        String pinID = "pin" + ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "Analog";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT2");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        ((AFONodeWidget) widget).setSubType(get_TimerID());

        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";

        retVal += "NAME:TIMER,ID:" + get_TimerID() + "," + getCommonData() + "COMMENT:";

        return retVal;
    }

    @Override
    public Element serializeNode(Document document, Element nodeElement) throws Exception {
        SceneSerializer.setAttribute(document, nodeElement, "TimerID", get_TimerID());

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception {

        m_TimerID = SceneSerializer.getAttributeValue(node, "TimerID");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnOk)) {

            if ((!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Valore Errato"))) {
                return;
            }

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetTimerID");

            com.putValue("VAL", ((GenericBlockDialog) propertiesDialog).txtField1.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
            propertiesDialog.setVisible(false);

        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnReadParams)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "GetTimerID");

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }

        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnCancel)) {
            propertiesDialog.setVisible(false);
        }
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "Timer")) {
                    String value = com.getValue("VAL");
                    ((GenericBlockDialog) propertiesDialog).txtField1.setText(value);
                    ((AFONodeWidget) widget).setSubType(value);
                }
            } else {
                return parseStandardBlockCmd(com);
            }
        }

        return false;
    }

    /**
     * @return the m_TimerID
     */
    public String get_TimerID() {
        return m_TimerID;
    }

     @Override
    public void setConnectionStatus(boolean isConnected){
        //((GenericBlockDialog)propertiesDialog).btnSendParam1.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnReadParams.setEnabled(isConnected);
    }


}
