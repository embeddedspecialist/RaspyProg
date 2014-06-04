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
public class CounterNode extends GenericNode implements ActionListener {
    String amountToCount = "0";

    public CounterNode (String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        CreatePropertiesDialog();
        blockType = CommonDefinitions.blockTypes.Counter;
        
    }

    private void CreateWidget() {
         widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Counter");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "CNT";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "OUT";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT2");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "Count";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN1");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        pinLabel = "Reset";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("IN2");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        pinLabel = "Load";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText("NA");

        //Combo
        ((GenericBlockDialog)propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).lblField1.setText("Amount: ");
        ((GenericBlockDialog)propertiesDialog).txtField1.setText(amountToCount);

        ((GenericBlockDialog)propertiesDialog).btnReadParams.addActionListener(this);
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).btnOk.addActionListener(this);
        ((GenericBlockDialog)propertiesDialog).btnCancel.addActionListener(this);

        ((GenericBlockDialog)propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setText("Reset");
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setVisible(true);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.addActionListener(this);
            
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog)propertiesDialog).txtField1.setText(amountToCount);
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog)propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK ){
            amountToCount = ((GenericBlockDialog)propertiesDialog).txtField1.getText();
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:OPERAZIONE, TYPE, INPUT1, INPUT2, OUTPUT1, ADDR, COMMENT
        retVal +="NAME:CONTATORE,AMOUNT:"+amountToCount+","+getCommonData()+"COMMENT:"+comment;

        return retVal;
    }

    @Override
    public Element serializeNode(Document document,Element nodeElement) throws Exception {
        SceneSerializer.setAttribute(document, nodeElement, "delay", amountToCount);
        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception{
        amountToCount = SceneSerializer.getAttributeValue(node, "delay");
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "Amount")) {
                    amountToCount = com.getValue("VAL");
                    ((GenericBlockDialog)propertiesDialog).txtField1.setText(amountToCount);
                }
            }
            else {
                return parseStandardBlockCmd(com);
            }
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnOk)) {

            if ( (!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Valore Errato") )
               ){
                return;
            }

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetAmount");

            com.putValue("VAL", ((GenericBlockDialog) propertiesDialog).txtField1.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
            propertiesDialog.setVisible(false);

        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnReadParams)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "GetAmount");

            sendCommand(com);

        } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnSendParam2)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "Reset");

            sendCommand(com);
        }
          else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnCancel)) {
              propertiesDialog.setVisible(false);
          }
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        //((GenericBlockDialog)propertiesDialog).btnSendParam1.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnReadParams.setEnabled(isConnected);
    }

}
