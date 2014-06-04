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
import nodes.gui.C3PointDialog;
import nodes.gui.PidPropertiesDialog;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class C3PointCtrlNode extends GenericNode implements ActionListener{

    private String setpoint="20.0",setpointH="35.0",setpointL="15.0",moveTime="180"
            ,nullZone="3.0",mFactor1,qFactor1,mFactor2,qFactor2;
    private String summer="0";

    public C3PointCtrlNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        blockType = CommonDefinitions.blockTypes.C3PointCtrl;

        CreateWidget();
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("3 Point");

        getScene().addNode(this);

        String pinLabel = "OPEN";
        String pinID = "pin"+ ++scene.pinIDCounter;

        AFOPin newPin = new AFOPin(pinID, pinLabel,AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "CLOSE";
        pinID = "pin"+ ++scene.pinIDCounter;

        newPin = new AFOPin(pinID, pinLabel,AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT2");
        getScene().addPin(this, newPin);
        pinList.add(newPin);


        //TBI::Per il PID devo capire quale tipo è per mettere in e out
        pinLabel = "Ctrl IN";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "LM IN";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN2");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "Setpoint";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN3");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        pinLabel = "Summer";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setPinIDString("IN4");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new C3PointDialog(null, true);

        propertiesDialog.setResizable(false);
        
        //((C3PointDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());

        ((C3PointDialog)propertiesDialog).txtMFactor1.setEnabled(false);
        ((C3PointDialog)propertiesDialog).txtQFactor1.setEnabled(false);

        ((C3PointDialog)propertiesDialog).txtMFactor2.setEnabled(false);
        ((C3PointDialog)propertiesDialog).txtQFactor2.setEnabled(false);

        ((C3PointDialog)propertiesDialog).btnReadParameters.addActionListener(this);
        ((C3PointDialog)propertiesDialog).btnOk.addActionListener(this);
        ((C3PointDialog)propertiesDialog).btnSendSetpoints.setVisible(false);
        ((C3PointDialog)propertiesDialog).btnSendSettings.setVisible(false);

//        setDialogFields();
//        getDialogFields();
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        //NAME:C3PointBlock, ADDR, INPUT1, INPUT2, INPUT3, INPUT4, OUTPUT1,OUTPUT2, MOVETIME, HYST, MFACTOR1, QFACTOR1, MFACTOR2,QFACTOR2,LIMH,LIML
        retVal +="NAME:C3POINT,"+getCommonData()+
                "SUMMER:" + summer +","     +
                "MOVETIME:" +moveTime+","   +
                "HYST:"     +nullZone+","   +
                "LIML:"     +setpointL+","  +
                "LIMH:"     +setpointH+","  +
                "SP:"       +setpoint+","   +
                "COMMENT:"  +comment;


        return retVal;
    }

    private void setDialogFields() {

        ((C3PointDialog)propertiesDialog).txtSetpoint.setText(setpoint);
        ((C3PointDialog)propertiesDialog).txtSetpointH.setText(setpointH);
        ((C3PointDialog)propertiesDialog).txtSetpointL.setText(setpointL);

        if (summer.equals("1")){
            ((C3PointDialog)propertiesDialog).checkSummer.setSelected(true);
        }
        else {
            ((C3PointDialog)propertiesDialog).checkSummer.setSelected(false);
        }

        ((C3PointDialog)propertiesDialog).txtMovetime.setText(moveTime);
        ((C3PointDialog)propertiesDialog).txtNullzone.setText(nullZone);
    }

    private void getDialogFields() {
        setpoint = ((C3PointDialog)propertiesDialog).txtSetpoint.getText();
        setpointH = ((C3PointDialog)propertiesDialog).txtSetpointH.getText();
        setpointL = ((C3PointDialog)propertiesDialog).txtSetpointL.getText();
        summer = boolToIntString(((C3PointDialog)propertiesDialog).checkSummer.isSelected());


        moveTime = ((C3PointDialog)propertiesDialog).txtMovetime.getText();
        nullZone = ((C3PointDialog)propertiesDialog).txtNullzone.getText();
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((C3PointDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        setDialogFields();
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);
        
        if ( ((C3PointDialog)propertiesDialog).getReturnStatus() == C3PointDialog.RET_OK ){
            getDialogFields();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(((C3PointDialog)propertiesDialog).btnOk)) {

            if ( (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtSetpoint.getText(), "Setpoint Errato") ) ||
                 (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtSetpointH.getText(), "Setpoint H Errato")) ||
                 (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtSetpointL.getText(), "Setpoint L Errato")) ||
                 (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtMovetime.getText(), "Move Time Errato") ) ||
                 (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtSetpointH.getText(), "Null Zone Errata"))
               ){
                return;
            }

            if (!kernel.isConnected()){
                propertiesDialog.setVisible(false);
                return;
            }


            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("SP", ((C3PointDialog)propertiesDialog).txtSetpoint.getText());
            com.putValue("LIMH", ((C3PointDialog)propertiesDialog).txtSetpointH.getText());
            com.putValue("LIML", ((C3PointDialog)propertiesDialog).txtSetpointL.getText());
            com.putValue("MOVETIME", ((C3PointDialog)propertiesDialog).txtMovetime.getText());
            com.putValue("HYST", ((C3PointDialog)propertiesDialog).txtNullzone.getText());

            if (((C3PointDialog)propertiesDialog).checkSummer.isSelected()) {
                com.putValue("SUMMER", "1");
            } else {
                com.putValue("SUMMER", "0");
            }

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
              propertiesDialog.setVisible(false);
        } else if (e.getSource().equals(((C3PointDialog)propertiesDialog).btnReadParameters)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "GetParameters");

            sendCommand(com);

       // } else if (e.getSource().equals(((C3PointDialog)propertiesDialog).btnSendSettings)) {

         //   if ( (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtMovetime.getText(), "Move Time Errato") ) ||
           //      (!FieldChecker.checkStringIsNumeric(((C3PointDialog)propertiesDialog).txtSetpointH.getText(), "Null Zone Errata"))
             //  ){
               // return;
           // }
            
           // Cmd com = generateBasicNodeCommand();

          //  com.putValue("BLOCKEXEC", "SetParameters");

           // com.putValue("MOVETIME", ((C3PointDialog)propertiesDialog).txtMovetime.getText());
           // com.putValue("HYST", ((C3PointDialog)propertiesDialog).txtNullzone.getText());

           // sendCommand(com);
        } 
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "C3PointParameters")) {
                    if (com.containsAttribute("SP")) {
                        ((C3PointDialog)propertiesDialog).txtSetpoint.setText(com.getValue("SETPOINT"));
                        setpoint = com.getValue("SETPOINT");
                    }

                    if (com.containsAttribute("LIMH")) {
                        ((C3PointDialog)propertiesDialog).txtSetpointH.setText(com.getValue("LIMH"));
                        setpointH = com.getValue("LIMH");
                    }

                    if (com.containsAttribute("LIML")) {
                        ((C3PointDialog)propertiesDialog).txtSetpointL.setText(com.getValue("LIML"));
                        setpointL = com.getValue("LIML");
                    }

                    if (com.containsAttribute("SUMMER")) {
                        if (com.getValue("SUMMER").equals("1")) {
                            ((C3PointDialog)propertiesDialog).checkSummer.setSelected(true);
                        } else {
                            ((C3PointDialog)propertiesDialog).checkSummer.setSelected(false);
                        }
                    }

                    if (com.containsAttribute("MOVETIME")) {
                        ((C3PointDialog)propertiesDialog).txtMovetime.setText(com.getValue("MOVETIME"));
                        moveTime = com.getValue("MOVETIME");
                    }

                    if (com.containsAttribute("HYST")) {
                        ((C3PointDialog)propertiesDialog).txtNullzone.setText(com.getValue("HYST"));
                        nullZone = com.getValue("KI");
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
        ((C3PointDialog)propertiesDialog).btnReadParameters.setEnabled(isConnected);
        //((C3PointDialog)propertiesDialog).btnSendSetpoints.setEnabled(isConnected);
        //((C3PointDialog)propertiesDialog).btnSendSettings.setEnabled(isConnected);
    }

}
