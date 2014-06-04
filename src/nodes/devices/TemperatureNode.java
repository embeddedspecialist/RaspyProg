/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
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
public class TemperatureNode extends DeviceNode {

    public TemperatureNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Temp;
        propertiesDialog = new SimplePropertiesDlg(null, true,"DS18S20");
        ((SimplePropertiesDlg)propertiesDialog).txtValue.setVisible(false);
        ((SimplePropertiesDlg)propertiesDialog).btnSend.setVisible(false);
        ((SimplePropertiesDlg)propertiesDialog).lblValue.setVisible(false);
        configSlotsUsed=2;
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(label);

        String pinLabel = "T";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);

        //Aggiungo alla scena:
        getScene().addNode(this);
        getScene().addPin(this, newPin);

        pinLabel = "BUS";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(true);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        getScene().addPin(this, newPin);

        pinList.add(newPin);

        ((AFONodeWidget)widget).setSubType(serialNumber);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((SimplePropertiesDlg)propertiesDialog).txtSerialNumber.setText(serialNumber);
        ((SimplePropertiesDlg)propertiesDialog).txtAddress.setText(Integer.toString(addressList.get(0)));
        propertiesDialog.setVisible(true);

        if ( ((SimplePropertiesDlg)propertiesDialog).getReturnStatus() == SimplePropertiesDlg.RET_OK ){
            serialNumber = ((SimplePropertiesDlg)propertiesDialog).txtSerialNumber.getText();
            ((AFONodeWidget)widget).setSubType(serialNumber);
            addressList.set(0,Integer.parseInt(((SimplePropertiesDlg)propertiesDialog).txtAddress.getText()));
        }
    }

        @Override
    public String getIniString(int deviceIndex) throws Exception {

            if (serialNumber.isEmpty()){
                JOptionPane.showMessageDialog(null, "Il dispositivo TEMP/C di indirizzo:" + addressList.get(0)+" , commento: "+
                        comment+" NON ha numero di serie.\n IL DISPOSITIVO NON FUNZIONERA'","Errore",JOptionPane.ERROR_MESSAGE);
            }
        String retVal="";

        retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
        retVal += new Formatter().format(driverFormat,serialNumber,0,comment)+"\n";

        //
        retVal += new Formatter().format(deviceConfigFormat,deviceIndex+1,comment);
        retVal += new Formatter().format(controllerFormat,
                                         deviceIndex,                   //Input
                                         addressList.get(0),            //Address
                                         "0",                           //ALARMMIN
                                         "100",                         //ALARMMAX
                                         comment) + "\n";               //COMMENT


        return retVal;
    }

    @Override
    public boolean parseCmd(Cmd com){

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            String tempVal = com.getValue("TEMP");

            AFOPin pin = pinList.get(0);
            pin.getPinNameWidget().setLabel("T ("+tempVal+")");

            setTargetPinValue(pin, tempVal);

            getScene().validate();

            sendValueToChart("OUT1",tempVal);
            return true;

        }

        return false;
    }

    private final static String driverFormat = "NAME:DS18S20,SN:%s,COMP:0.0,COMMENT:%s";
    //NAME:DigitalINOUT,INPUT,IO,STARTV,CHANNEL,ADDR,TIMERID,INVERTOUT,STEP,COMMENT
    private final static String controllerFormat = "NAME:TempController,INPUT:%s,ADDR:%s,NOUT:0,SWALARMS:1,ALARMMIN:%s,ALARMMAX:%s,COMMENT:%s";
}
