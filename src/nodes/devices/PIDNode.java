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
import nodes.gui.PidPropertiesDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class PIDNode extends GenericNode implements ActionListener {

//    CommonDefinitions.pidTypes pidType = CommonDefinitions.pidTypes.PID_PF;
    //Parametri PID (Proporz, Int, der), se con isteresi il primo è il valore
    public String[] pidParameters = {"10.0", "1.0", "0.0", "10.0", "1.0", "0.0"};
    //Setpoint , spH, spL
    public String[] pidSetpoint = {"20.0", "35.0", "15.0"};
    public String compExt = "3.0";
    boolean isSummer;

    public PIDNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.PID;
        //Cerco chi sono
//        setPIDType(label);

        CreateWidget();
        CreatePropertiesDialog();

    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("PID");

        getScene().addNode(this);

        String pinLabel = "OUT";
        String pinID = "pin" + ++scene.pinIDCounter;

        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);


        //TBI::Per il PID devo capire quale tipo è per mettere in e out
        pinLabel = "Ctrl IN";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);


        pinLabel = "LM IN";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN2");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "Setpoint";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN3");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "Summer";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN4");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "EN";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN5");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

//        ((AFONodeWidget) widget).setSubType(pidType.toString());

        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);

    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new PidPropertiesDialog(null, true);

        ((PidPropertiesDialog) propertiesDialog).txtAddress.setText("NA");
        ((PidPropertiesDialog) propertiesDialog).setTitle("PID Properties");

//        if (pidType == CommonDefinitions.pidTypes.PID_PF) {
//            ((PidPropertiesDialog) propertiesDialog).txtSetpointH.setEnabled(false);
//            ((PidPropertiesDialog) propertiesDialog).txtSetpointL.setEnabled(false);
//
//            ((PidPropertiesDialog) propertiesDialog).txtKP2.setEnabled(false);
//            ((PidPropertiesDialog) propertiesDialog).txtTD2.setEnabled(false);
//            ((PidPropertiesDialog) propertiesDialog).txtTI2.setEnabled(false);
//            ((PidPropertiesDialog) propertiesDialog).btnSendParameters2.setEnabled(false);
//        }

        ((PidPropertiesDialog) propertiesDialog).btnReadParameters.addActionListener(this);
        ((PidPropertiesDialog) propertiesDialog).btnOK.addActionListener(this);


    }

    private void setDialogFields() {

        ((PidPropertiesDialog) propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((PidPropertiesDialog) propertiesDialog).txtKP1.setText(pidParameters[0]);
        ((PidPropertiesDialog) propertiesDialog).txtTI1.setText(pidParameters[1]);
        ((PidPropertiesDialog) propertiesDialog).txtTD1.setText(pidParameters[2]);

        ((PidPropertiesDialog) propertiesDialog).txtKP2.setText(pidParameters[3]);
        ((PidPropertiesDialog) propertiesDialog).txtTI2.setText(pidParameters[4]);
        ((PidPropertiesDialog) propertiesDialog).txtTD2.setText(pidParameters[5]);

        ((PidPropertiesDialog) propertiesDialog).txtSetpoint.setText(pidSetpoint[0]);
        ((PidPropertiesDialog) propertiesDialog).txtSetpointH.setText(pidSetpoint[1]);
        ((PidPropertiesDialog) propertiesDialog).txtSetpointL.setText(pidSetpoint[2]);

        ((PidPropertiesDialog) propertiesDialog).checkSummer.setSelected(isSummer);

    }

    private void getDialogFields() {

        pidParameters[0] = ((PidPropertiesDialog) propertiesDialog).txtKP1.getText();
        pidParameters[1] = ((PidPropertiesDialog) propertiesDialog).txtTI1.getText();
        pidParameters[2] = ((PidPropertiesDialog) propertiesDialog).txtTD2.getText();

        pidParameters[3] = ((PidPropertiesDialog) propertiesDialog).txtKP2.getText();
        pidParameters[4] = ((PidPropertiesDialog) propertiesDialog).txtTI2.getText();
        pidParameters[5] = ((PidPropertiesDialog) propertiesDialog).txtTD2.getText();

        pidSetpoint[0] = ((PidPropertiesDialog) propertiesDialog).txtSetpoint.getText();
        pidSetpoint[1] = ((PidPropertiesDialog) propertiesDialog).txtSetpointH.getText();
        pidSetpoint[2] = ((PidPropertiesDialog) propertiesDialog).txtSetpointL.getText();

        isSummer = ((PidPropertiesDialog) propertiesDialog).checkSummer.isSelected();
    }

    @Override
    public void showPropertiesDialog() throws Exception {

        setDialogFields();
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if (((PidPropertiesDialog) propertiesDialog).getReturnStatus() == PidPropertiesDialog.RET_OK) {
            getDialogFields();
        }
    }

    @Override
    public Element serializeNode(Document document, Element nodeElement) throws Exception {
        //SceneSerializer.setAttribute(document, nodeElement, SceneSerializer.NODE_SUBTYPE, pidType.toString());

        SceneSerializer.setAttribute(document, nodeElement, PID_SETPOINT, pidSetpoint[0]);
        SceneSerializer.setAttribute(document, nodeElement, PID_SETPOINTH, pidSetpoint[1]);
        SceneSerializer.setAttribute(document, nodeElement, PID_SETPOINTL, pidSetpoint[2]);

        SceneSerializer.setAttribute(document, nodeElement, PID_KP, pidParameters[0]);
        SceneSerializer.setAttribute(document, nodeElement, PID_KI, pidParameters[1]);
        SceneSerializer.setAttribute(document, nodeElement, PID_KD, pidParameters[2]);
        SceneSerializer.setAttribute(document, nodeElement, PID_KP2, pidParameters[3]);
        SceneSerializer.setAttribute(document, nodeElement, PID_KI2, pidParameters[4]);
        SceneSerializer.setAttribute(document, nodeElement, PID_KD2, pidParameters[5]);

        SceneSerializer.setAttribute(document, nodeElement, PID_SUMMER, Boolean.toString(isSummer));

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception {

        pidSetpoint[0] = SceneSerializer.getAttributeValue(node, PID_SETPOINT);
        pidSetpoint[1] = SceneSerializer.getAttributeValue(node, PID_SETPOINTH);
        pidSetpoint[2] = SceneSerializer.getAttributeValue(node, PID_SETPOINTL);

        pidParameters[0] = SceneSerializer.getAttributeValue(node, PID_KP);
        pidParameters[1] = SceneSerializer.getAttributeValue(node, PID_KI);
        pidParameters[2] = SceneSerializer.getAttributeValue(node, PID_KD);

        pidParameters[3] = SceneSerializer.getAttributeValue(node, PID_KP2);
        pidParameters[4] = SceneSerializer.getAttributeValue(node, PID_KI2);
        pidParameters[5] = SceneSerializer.getAttributeValue(node, PID_KD2);

        isSummer = Boolean.parseBoolean(SceneSerializer.getAttributeValue(node, PID_SUMMER));
    }
    private static final String PID_SETPOINT = "setpoint";
    private static final String PID_SETPOINTH = "setpointh";
    private static final String PID_SETPOINTL = "setpointl";
    private static final String PID_KP = "kp";
    private static final String PID_KI = "ki";
    private static final String PID_KD = "kd";
    private static final String PID_KP2 = "kp2";
    private static final String PID_KI2 = "ki2";
    private static final String PID_KD2 = "kd2";
    private static final String PID_SUMMER = "summer";

//    private void setPIDType(String label) {
//        for (CommonDefinitions.pidTypes myType : CommonDefinitions.pidTypes.values()) {
//            if (label.equals(myType.toString())) {
//                pidType = myType;
//            }
//        }
//    }

    /**
     * Consente al dispositivo di aggiungere le proprie stringhe al file ini
     * NAME:PID, TYPE, INPUTX, OUTPUTX, KP, KI, KD, SP, ADDR, SUMMER, COMMENT
     * @throws Exception
     */
    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";
        retVal += "NAME:PID," + getCommonData();
        retVal += "KP:" + pidParameters[0] + ",KI:" + pidParameters[1] + ",KD:" + pidParameters[2];
        retVal += ",KP2:" + pidParameters[3] + ",KI2:" + pidParameters[4] + ",KD2:" + pidParameters[5];
        retVal += ",LIMH:" + pidSetpoint[1] + ",LIML:" + pidSetpoint[2];

        retVal += ",SUMMER:" + boolToInteger(isSummer) + ",SP:" + pidSetpoint[0];
        retVal += ",COMMENT:" + comment;

        return retVal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(((PidPropertiesDialog) propertiesDialog).btnOK)) {

            //Controllo tutti i campi...
        if (FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtKP1.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtTI1.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtTD1.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtKP2.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtTI2.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtTD2.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtSetpoint.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtSetpointH.getText()) &&
            FieldChecker.checkStringIsNumeric(((PidPropertiesDialog) propertiesDialog).txtSetpointL.getText())
            ) {

            
            // scrivo il messaggio

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("SP", ((PidPropertiesDialog) propertiesDialog).txtSetpoint.getText());
            com.putValue("LIMH", ((PidPropertiesDialog) propertiesDialog).txtSetpointH.getText());
            com.putValue("LIML", ((PidPropertiesDialog) propertiesDialog).txtSetpointL.getText());

            if (((PidPropertiesDialog) propertiesDialog).checkSummer.isSelected()) {
                com.putValue("SUMMER", "1");
            } else {
                com.putValue("SUMMER", "0");
            }

           // sendCommand(com);

        // else if (e.getSource().equals(((PidPropertiesDialog) propertiesDialog).btnSendParameters1)) {

           // if ( (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpoint.getText(), "KP Errato") ) ||
             //    (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpointH.getText(), "KI Errato")) ||
               //  (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpointL.getText(), "KD Errato"))
              // ){
               // return;
           // }

            //Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("KP", ((PidPropertiesDialog) propertiesDialog).txtKP1.getText());
            com.putValue("KI", ((PidPropertiesDialog) propertiesDialog).txtTI1.getText());
            com.putValue("KD", ((PidPropertiesDialog) propertiesDialog).txtTD1.getText());

           // sendCommand(com);
       // } else if (e.getSource().equals(((PidPropertiesDialog) propertiesDialog).btnSendParameters2)) {

         //   if ( (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpoint.getText(), "KP Errato") ) ||
           //      (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpointH.getText(), "KI Errato")) ||
             //    (!FieldChecker.checkStringIsNumeric(((PidPropertiesDialog)propertiesDialog).txtSetpointL.getText(), "KD Errato"))
              // ){
               // return;
           // }
            
           // Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("KP2", ((PidPropertiesDialog) propertiesDialog).txtKP2.getText());
            com.putValue("KI2", ((PidPropertiesDialog) propertiesDialog).txtTI2.getText());
            com.putValue("KD2", ((PidPropertiesDialog) propertiesDialog).txtTD2.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }

            getDialogFields();

        }
        propertiesDialog.setVisible(false);
        } else if (e.getSource().equals(((PidPropertiesDialog) propertiesDialog).btnReadParameters)) {

           Cmd com = generateBasicNodeCommand();

           com.putValue("BLOCKEXEC", "GetParameters");
           sendCommand(com);

        }
 }


    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "PidParameters")) {
                    if (com.containsAttribute("SP")) {
                        ((PidPropertiesDialog) propertiesDialog).txtSetpoint.setText(com.getValue("SP"));
                        pidSetpoint[0] = com.getValue("SP");
                    }

                    if (com.containsAttribute("SPH")) {
                        ((PidPropertiesDialog) propertiesDialog).txtSetpointH.setText(com.getValue("SPH"));
                        pidSetpoint[1] = com.getValue("SPH");
                    }

                    if (com.containsAttribute("SPL")) {
                        ((PidPropertiesDialog) propertiesDialog).txtSetpointL.setText(com.getValue("SPL"));
                        pidSetpoint[2] = com.getValue("SPL");
                    }

                    if (com.containsAttribute("SUMMER")) {
                        if (com.getValue("SUMMER").equals("1")) {
                            ((PidPropertiesDialog) propertiesDialog).checkSummer.setSelected(true);
                        } else {
                            ((PidPropertiesDialog) propertiesDialog).checkSummer.setSelected(false);
                        }
                    }

                    if (com.containsAttribute("KP")) {
                        ((PidPropertiesDialog) propertiesDialog).txtKP1.setText(com.getValue("KP"));
                        pidParameters[0] = com.getValue("KP");
                    }

                    if (com.containsAttribute("KI")) {
                        ((PidPropertiesDialog) propertiesDialog).txtTI1.setText(com.getValue("KI"));
                        pidParameters[1] = com.getValue("KI");
                    }

                    if (com.containsAttribute("KD")) {
                        ((PidPropertiesDialog) propertiesDialog).txtTD1.setText(com.getValue("KD"));
                        pidParameters[2] = com.getValue("KD");
                    }

                    if (com.containsAttribute("KP2")) {
                        ((PidPropertiesDialog) propertiesDialog).txtKP2.setText(com.getValue("KP2"));
                        pidParameters[3] = com.getValue("KP2");
                    }

                    if (com.containsAttribute("KI2")) {
                        ((PidPropertiesDialog) propertiesDialog).txtTI2.setText(com.getValue("KI2"));
                        pidParameters[4] = com.getValue("KI2");
                    }

                    if (com.containsAttribute("KD2")) {
                        ((PidPropertiesDialog) propertiesDialog).txtTD2.setText(com.getValue("KD2"));
                        pidParameters[5] = com.getValue("KD2");
                    }

                }
            }
            else {
                return parseStandardBlockCmd(com);
            }
        }

        return false;
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        ((PidPropertiesDialog)propertiesDialog).btnReadParameters.setEnabled(isConnected);
        //((PidPropertiesDialog)propertiesDialog).btnOK.setEnabled(isConnected);
    }
}
    
