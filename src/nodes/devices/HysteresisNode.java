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
import nodes.gui.HystersisDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class HysteresisNode extends GenericNode implements ActionListener {
    private String bandAmplitude = "1.0";
    private String setpoint = "20.0";
    private String minOutput ="0.0";
    private String maxOutput = "1.0";

    public HysteresisNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Hysteresys;
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Hysteresis");

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

        pinLabel = "IN";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "Setpoint";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN2");
        getScene().addPin(this, newPin);
        pinList.add(newPin);


        ((AFONodeWidget)widget).setSubType("SP:"+setpoint+" Hyst:"+bandAmplitude);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new HystersisDialog(null, true);
        ((HystersisDialog)propertiesDialog).btnRead.addActionListener(this);
        ((HystersisDialog)propertiesDialog).btnSend.setVisible(false);
        ((HystersisDialog)propertiesDialog).btnOk.addActionListener(this);

    }

    private void setDialogFields(){
        ((HystersisDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());

        ((HystersisDialog)propertiesDialog).txtSetpoint.setText(setpoint);
        ((HystersisDialog)propertiesDialog).txtAmplitude.setText(bandAmplitude);
        ((HystersisDialog)propertiesDialog).txtMaxOut.setText(maxOutput);
        ((HystersisDialog)propertiesDialog).txtMinOut.setText(minOutput);

    }

    private void getDialogFields() {

        setpoint = ((HystersisDialog)propertiesDialog).txtSetpoint.getText();
        bandAmplitude = ((HystersisDialog)propertiesDialog).txtAmplitude.getText();
        minOutput = ((HystersisDialog)propertiesDialog).txtMinOut.getText();
        maxOutput = ((HystersisDialog)propertiesDialog).txtMaxOut.getText();
        
    }

        @Override
    public void showPropertiesDialog() throws Exception {

        setDialogFields();
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if  ( ((HystersisDialog)propertiesDialog).getReturnStatus() == HystersisDialog.RET_OK ){
            getDialogFields();
            ((AFONodeWidget)widget).setSubType("SP:"+setpoint+" Hyst:"+bandAmplitude);
        }
    }

    @Override
    public Element serializeNode(Document document,Element nodeElement) throws Exception {

        SceneSerializer.setAttribute(document, nodeElement, "BandAmplitude", bandAmplitude);
        SceneSerializer.setAttribute(document, nodeElement, "Setpoint", setpoint);
        SceneSerializer.setAttribute(document, nodeElement, "MinOutput", minOutput);
        SceneSerializer.setAttribute(document, nodeElement, "MaxOutput", maxOutput);

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception{

        setpoint = SceneSerializer.getAttributeValue(node, "Setpoint");
        bandAmplitude = SceneSerializer.getAttributeValue(node, "BandAmplitude");
        minOutput = SceneSerializer.getAttributeValue(node, "MinOutput");
        maxOutput = SceneSerializer.getAttributeValue(node, "MaxOutput");

        ((AFONodeWidget)widget).setSubType("SP:"+setpoint+" Hyst:"+bandAmplitude);
    }

    /**
     * @return the bandAmplitude
     */
    public String getBandAmplitude() {
        return bandAmplitude;
    }

    /**
     * @param bandAmplitude the bandAmplitude to set
     */
    public void setBandAmplitude(String bandAmplitude) {
        this.bandAmplitude = bandAmplitude;
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception{
        String retVal="";
        retVal +="NAME:ISTERESI,"+getCommonData();
        retVal +="SETPOINT:"+setpoint+",DELTA:"+bandAmplitude+",MIN:"+minOutput+" ,MAX:"+maxOutput;
        retVal +=",COMMENT:"+comment;

        return retVal;
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "HystParameters")) {

                    if (com.containsAttribute("SETPOINT")){
                        setpoint = com.getValue("SETPOINT");
                        ((HystersisDialog) propertiesDialog).txtSetpoint.setText(setpoint);
                    }

                    if (com.containsAttribute("DELTA")){
                        bandAmplitude = com.getValue("DELTA");
                        ((HystersisDialog) propertiesDialog).txtAmplitude.setText(bandAmplitude);
                    }

                    if (com.containsAttribute("MIN")){
                        minOutput = com.getValue("MIN");
                        ((HystersisDialog) propertiesDialog).txtMinOut.setText(minOutput);
                    }

                    if (com.containsAttribute("MAX")){
                        maxOutput = com.getValue("MAX");
                        ((HystersisDialog) propertiesDialog).txtMaxOut.setText(maxOutput);
                    }

                    ((AFONodeWidget)widget).setSubType("SP:"+setpoint+" Hyst:"+bandAmplitude);
                    
                    return true;

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
        if (e.getSource().equals(((HystersisDialog) propertiesDialog).btnOk)) {

            if ( (!FieldChecker.checkStringIsNumeric(((HystersisDialog) propertiesDialog).txtSetpoint.getText(),    "Setpoint Errato") ) ||
                 (!FieldChecker.checkStringIsNumeric(((HystersisDialog) propertiesDialog).txtAmplitude.getText(),   "Delta Errato") ) ||
                 (!FieldChecker.checkStringIsNumeric(((HystersisDialog) propertiesDialog).txtMinOut.getText(),      "Min Errato") ) ||
                 (!FieldChecker.checkStringIsNumeric(((HystersisDialog) propertiesDialog).txtMaxOut.getText(),      "Max Errato") )
               ){
                return;
            }

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("SETPOINT", ((HystersisDialog) propertiesDialog).txtSetpoint.getText());
            com.putValue("DELTA", ((HystersisDialog) propertiesDialog).txtAmplitude.getText());
            com.putValue("MIN", ((HystersisDialog) propertiesDialog).txtMinOut.getText());
            com.putValue("MAX", ((HystersisDialog) propertiesDialog).txtMaxOut.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }

        } else if (e.getSource().equals(((HystersisDialog) propertiesDialog).btnRead)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "GetParameters");

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }

        }
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        ((HystersisDialog)propertiesDialog).btnRead.setEnabled(isConnected);
       // ((HystersisDialog)propertiesDialog).btnSend.setEnabled(isConnected);
    }

}
