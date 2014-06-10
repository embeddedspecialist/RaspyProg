/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//TODO da mettere a posto la dialog e i campi dell'INI per la configurazione
//TODO da serializzare
package nodes.devices;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.util.Formatter;
import javax.swing.JOptionPane;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.SceneSerializer;
import nodes.gui.THUDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class THUNode extends DeviceNode {

    boolean hasSetpointDevice = true;
    boolean isSPDeviceAbsolute = false;
    String setpointRange = "3.0";
    String setpoint = "20.0";

    public THUNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.THU;
        CreateWidget();
        propertiesDialog = new THUDialog(null, true);
        configSlotsUsed=2;

    }

    private void CreateWidget() {
         widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(label);
        getScene().addNode(this);

        //Temperatura
        String pinLabel = "T";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        pinList.add(newPin);
        newPin.setPinIDString("OUT1");
        getScene().addPin(this, newPin);

        //Umidita'
        pinLabel = "H";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        pinList.add(newPin);
        newPin.setPinIDString("OUT2");
        getScene().addPin(this, newPin);

        //Setpoint
        pinLabel = "SP";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        pinList.add(newPin);
        newPin.setPinIDString("OUT3");
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

        ((THUDialog)propertiesDialog).txtSerNum.setText(serialNumber);
        ((THUDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());

        ((THUDialog)propertiesDialog).checkRitaratore.setSelected(hasSetpointDevice);
        ((THUDialog)propertiesDialog).checkRitAssoluto.setSelected(isSPDeviceAbsolute);
        ((THUDialog)propertiesDialog).txtSetpointRange.setText(setpointRange);
        ((THUDialog)propertiesDialog).enableFields(hasSetpointDevice);
        ((THUDialog)propertiesDialog).txtSetpoint.setText(setpoint);

        propertiesDialog.setVisible(true);

        if ( ((THUDialog)propertiesDialog).getReturnStatus() == THUDialog.RET_OK ){

            serialNumber = ((THUDialog)propertiesDialog).txtSerNum.getText();
            ((AFONodeWidget)widget).setSubType(serialNumber);

            hasSetpointDevice = ((THUDialog)propertiesDialog).checkRitaratore.isSelected();
            isSPDeviceAbsolute = ((THUDialog)propertiesDialog).checkRitAssoluto.isSelected();
            setpointRange = ((THUDialog)propertiesDialog).txtSetpointRange.getText();
            setpoint = ((THUDialog)propertiesDialog).txtSetpoint.getText();
        }
    }

        @Override
    public Element serializeNode(Document document,Element nodeElement) throws Exception {

        SceneSerializer.setAttribute(document, nodeElement, "HasSPDevice", boolToIntString(hasSetpointDevice));
        SceneSerializer.setAttribute(document, nodeElement, "Setpoint", setpoint);
        SceneSerializer.setAttribute(document, nodeElement, "SetpointRange", setpointRange);
        SceneSerializer.setAttribute(document, nodeElement, "IsAbsolute", boolToIntString(isSPDeviceAbsolute));

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception{

        setpoint = SceneSerializer.getAttributeValue(node, "Setpoint");
        setpointRange = SceneSerializer.getAttributeValue(node, "SetpointRange");
        String test = SceneSerializer.getAttributeValue(node, "HasSPDevice");
        hasSetpointDevice = intStringToBool(test);
        test = SceneSerializer.getAttributeValue(node, "IsAbsolute");
        isSPDeviceAbsolute = intStringToBool(test);
        
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {

            if (serialNumber.isEmpty()){
                JOptionPane.showMessageDialog(null, "THU device with address :" + addressList.get(0)+" , comment: "+
                            comment+" DOES NOT HAVE any serial number.\n THE DEVICE WILL NOT WORK","Errore",JOptionPane.ERROR_MESSAGE);
            }
        String retVal="";

        retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
        retVal += new Formatter().format(driverFormat,serialNumber,0,comment)+"\n";

        //
        retVal += new Formatter().format(deviceConfigFormat,deviceIndex+1,comment);

        retVal += new Formatter().format(controllerFormat,
                                         deviceIndex,                           //Input
                                         addressList.get(0),                    //Address
                                         boolToIntString(hasSetpointDevice),    //ISRIT
                                         setpoint,                              //SP
                                         boolToIntString(!isSPDeviceAbsolute),  //ISRELATIVE
                                         setpointRange,                                   //RIT
                                         comment) + "\n";                       //COMMENT


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
            sendValueToChart("OUT1",tempVal);

            tempVal = com.getValue("HUM");
            pin = pinList.get(1);
            pin.getPinNameWidget().setLabel("H ("+tempVal+")");
            setTargetPinValue(pin, tempVal);
            sendValueToChart("OUT2",tempVal);

            tempVal = com.getValue("SP");
            pin = pinList.get(2);
            pin.getPinNameWidget().setLabel("SP ("+tempVal+")");
            setTargetPinValue(pin, tempVal);
            sendValueToChart("OUT3",tempVal);

            getScene().validate();

            return true;

        }

        return false;
    }

   private final static String driverFormat = "NAME:DS2438,SN:%s,COMP:0.0,COMMENT:%s";
    //NAME:DigitalINOUT,INPUT,IO,STARTV,CHANNEL,ADDR,TIMERID,INVERTOUT,STEP,COMMENT
    private final static String controllerFormat = "NAME:Thu,INPUT:%s,ADDR:%s,ISRIT:%s,SP:%s,ISRELATIVE:%s,RIT:%s,COMMENT:%s";
}
