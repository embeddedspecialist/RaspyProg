/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;
import javax.swing.JOptionPane;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.gui.SimplePropertiesDlg;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class AnalogNode extends DeviceNode implements ActionListener {

    private String type;


    public AnalogNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        this.type = label;

        if (type.equals(CommonDefinitions.blockTypes.AO.toString())){
            propertiesDialog = new SimplePropertiesDlg(null, true,"DS2890");
            blockType = CommonDefinitions.blockTypes.AO;
            ((SimplePropertiesDlg)propertiesDialog).btnSend.addActionListener(this);
        }
        else {
            propertiesDialog = new SimplePropertiesDlg(null, true,"DS2438");
            blockType = CommonDefinitions.blockTypes.AI;
            ((SimplePropertiesDlg)propertiesDialog).btnSend.setVisible(false);
            ((SimplePropertiesDlg)propertiesDialog).txtValue.setVisible(false);
        }

        setConnectionStatus(false);
        createWidget();
        configSlotsUsed = 2;
    }

    private void createWidget() {
        String pinLabel, pinID;
        AFOPin newPin;
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(type);

        getScene().addNode(this);

        if (type.equals(CommonDefinitions.blockTypes.AO.toString())){

                pinLabel = "OUT";
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("OUT1");
                pinList.add(newPin);
                getScene().addPin(this, newPin);
        }
        else if (type.equals(CommonDefinitions.blockTypes.AI.toString())){
                pinLabel = "IN";
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("IN1");
                pinList.add(newPin);
                getScene().addPin(this, newPin);
        }

        pinLabel = "BUS";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(true);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        pinList.add(newPin);
        getScene().addPin(this, newPin);
        
        ((AFONodeWidget)widget).setSubType(serialNumber);
        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);

    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((SimplePropertiesDlg)propertiesDialog).txtSerialNumber.setText(serialNumber);
        ((SimplePropertiesDlg)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        propertiesDialog.setVisible(true);

        if ( ((SimplePropertiesDlg)propertiesDialog).getReturnStatus() == SimplePropertiesDlg.RET_OK ){
            serialNumber = ((SimplePropertiesDlg)propertiesDialog).txtSerialNumber.getText();
            ((AFONodeWidget)widget).setSubType(serialNumber);
            addressList.set(0, Integer.parseInt(((SimplePropertiesDlg)propertiesDialog).txtAddress.getText()));
        }
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        
        if (serialNumber.isEmpty()){
            JOptionPane.showMessageDialog(null, "Il dispositivo "+ blockType.toString()+" di indirizzo:" + addressList.get(0)+" , commento: "+
            comment+" NON ha numero di serie.\nIL DISPOSITIVO NON FUNZIONERA'","Errore",JOptionPane.ERROR_MESSAGE);
        }

        String retVal="";

        if (blockType == CommonDefinitions.blockTypes.AO) {
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
            //Creo il driver
            retVal += new Formatter().format(analogOUTDriverFormat,serialNumber,comment) + "\n";

            //Creo il controller
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex+1,comment);
            retVal += new Formatter().format(analogOUTControllerFormat,deviceIndex,addressList.get(0),comment) + "\n";
        }
        else {
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
            //Creo il driver
            retVal += new Formatter().format(analogINDriverFormat,serialNumber,comment) + "\n";

            //Creo il controller
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex+1,comment);
            retVal += new Formatter().format(analogINControllerFormat,deviceIndex,addressList.get(0),comment) + "\n";
        }

        return retVal;
    }

    @Override
    public boolean parseCmd(Cmd com){
        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))){

            String tempVal;
            AFOPin pin = pinList.get(0);
            if (blockType == CommonDefinitions.blockTypes.AO){
                tempVal = com.getValue("OUTVOLT");
                pin.getPinNameWidget().setLabel("("+tempVal+")" + "OUT");
            }
            else {
                tempVal = com.getValue("VAL");
                pin.getPinNameWidget().setLabel("IN"+"("+tempVal+")");
                sendValueToChart("OUT1",tempVal);
                setTargetPinValue(pin, tempVal);
            }

            getScene().validate();
            return true;
        }

        return false;
    }

    private static final String analogOUTDriverFormat="NAME:DS2890,SN:%s,UPDATESTATE:0,COMMENT:%s";
    private static final String analogOUTControllerFormat="NAME:AnalogINOUT,INPUT:%d,ADDR:%d,STARTVAL:0,TIMERID:0,IO:0,COMMENT:%s";

    private static final String analogINDriverFormat="NAME:DS2438,SN:%s,COMP:0.0,CURRENT:0,TEMPERATURE:0,VOLTAGE:1,COMMENT:%s";
    private static final String analogINControllerFormat="NAME:AnalogINOUT,INPUT:%d,ADDR:%d,READCURRENT:0,TIMERID:0,SCALE:1.0,OFFSET:0.0,IO:1,COMMENT:%s";

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource().equals(((SimplePropertiesDlg) propertiesDialog).btnSend)) {

                Cmd com = new Cmd("DEVICE");
                com.putValue("COMMAND", "SetAnalogOutput");
                com.putValue("ADDRESS", addressList.get(0).toString());

                String value = ((SimplePropertiesDlg) propertiesDialog).txtValue.getText();

                if (new Double(value) > 10.0) {
                    value = "10.0";
                    ((SimplePropertiesDlg) propertiesDialog).txtValue.setText(value);
                }

                com.putValue("VAL", value);

                sendCommand(com);
            }
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(null, "Si e' verificato il seguente errore:\n"+numberFormatException.getLocalizedMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
                    );
            numberFormatException.printStackTrace();
        }
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        ((SimplePropertiesDlg)propertiesDialog).btnSend.setEnabled(isConnected);
    }
}
