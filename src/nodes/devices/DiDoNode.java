/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Formatter;
import javax.swing.JOptionPane;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.SceneSerializer;
import nodes.gui.DidoDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class DiDoNode extends DeviceNode implements ActionListener {

    private String typeName;
    private ArrayList<Boolean> stepList;
    private ArrayList<Boolean> normallyOpenList;

    public DiDoNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        this.typeName = label;
        if (typeName.equals(CommonDefinitions.blockTypes.DO8.toString())){
            blockType = CommonDefinitions.blockTypes.DO8;
        }
        else if (typeName.equals(CommonDefinitions.blockTypes.DI8.toString())){
            blockType = CommonDefinitions.blockTypes.DI8;
        }
        else if (typeName.equals(CommonDefinitions.blockTypes.IOD4.toString())){
            blockType = CommonDefinitions.blockTypes.IOD4;
        }

        stepList = new ArrayList<Boolean>();
        normallyOpenList = new ArrayList<Boolean>();
        for (int i = 0; i <8; i++){
            stepList.add(false);
            normallyOpenList.add(false);
            //addressList.add(-1);
        }
        
        CreateWidget();
        CreatePropertiesDialog();

        configSlotsUsed=9;
    }

    private void CreateWidget() {
        String pinLabel, pinID;
        AFOPin newPin;
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(typeName);

        getScene().addNode(this);
        
        if (typeName.equals(CommonDefinitions.blockTypes.DO8.toString())){
            for (int i = 0; i < 8; i++)
            {
                pinLabel = "OUT"+(i+1);
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("OUT"+(i+1));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
            }
        }
        else if (typeName.equals(CommonDefinitions.blockTypes.IOD4.toString())){
            for (int i = 0; i < 4; i++)
            {
                pinLabel = "OUT"+(i+1);
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("OUT"+(i+1));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
            }

            for (int i = 0; i < 4; i++)
            {
                pinLabel = "IN"+(i+1);
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("IN"+(i+1));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
            }
        }
        else if (typeName.equals(CommonDefinitions.blockTypes.DI8.toString())){
            for (int i = 0; i < 8; i++)
            {
                pinLabel = "IN"+(i+1);
                pinID = "pin" + ++scene.pinIDCounter;
                newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
                newPin.setAsBusPin(false);
                newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
                newPin.setPinIDString("OUT"+(i+1));
                pinList.add(newPin);
                getScene().addPin(this, newPin);
            }
        }

        pinLabel = "BUS";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(true);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        ((AFONodeWidget)widget).setSubType(getSerialNumber());
        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void setDialogFields(){
        ((DidoDialog)propertiesDialog).txtSerNum.setText(getSerialNumber());

        for (int i = 0; i < 8; i++){
            ((DidoDialog)propertiesDialog).getChannelPanel(i).txtChannel.setText(Integer.toString(addressList.get(i)));
            ((DidoDialog)propertiesDialog).getChannelPanel(i).checkNO.setSelected(normallyOpenList.get(i));
            ((DidoDialog)propertiesDialog).getChannelPanel(i).checkStep.setSelected(stepList.get(i));
        }
    }

    private void getDialogFields(){
        try {
            serialNumber = ((DidoDialog)propertiesDialog).txtSerNum.getText();
            for (int i = 0; i < 8; i++){
                addressList.set(i,Integer.parseInt( ((DidoDialog)propertiesDialog).getChannelPanel(i).txtChannel.getText()));
                normallyOpenList.set(i,((DidoDialog)propertiesDialog).getChannelPanel(i).checkNO.isSelected());
                stepList.set(i,((DidoDialog)propertiesDialog).getChannelPanel(i).checkStep.isSelected());
            }
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore nei dati della finestra");
        }
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        
        setDialogFields();
        propertiesDialog.setVisible(true);

        if ( ((DidoDialog)propertiesDialog).getReturnStatus() == DidoDialog.RET_OK ){
            getDialogFields();
            ((AFONodeWidget)widget).setSubType(getSerialNumber());
        }
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new DidoDialog(null, true);
        if (blockType == CommonDefinitions.blockTypes.DO8){
            for (int i = 1; i <= 8; i++){
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).lblChannel.setText("OUT"+i+":");
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).btnChange.addActionListener(this);
            }
        }
        else if (blockType == CommonDefinitions.blockTypes.DI8){
            for (int i = 1; i <= 8; i++){
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).lblChannel.setText("IN"+i+":");
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).btnChange.setVisible(false);
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).checkStep.setEnabled(false);
            }
        }
        else {
            for (int i = 1; i <= 4; i++){
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).lblChannel.setText("OUT"+i+":");
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).btnChange.addActionListener(this);
            }

            for (int i = 5; i <= 8; i++){
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).lblChannel.setText("IN"+(i-4)+":");
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).btnChange.setVisible(false);
                ((DidoDialog)propertiesDialog).getChannelPanel(i-1).checkStep.setEnabled(false);
            }
        }
    }

    @Override
    public Element serializeNode(Document document,Element nodeElement) throws Exception {

        for (int i =0 ; i < 8; i++){
            Element digitalElement = document.createElement (DIGITAL_NODE);
            SceneSerializer.setAttribute(document, digitalElement, "idx", Integer.toString(i));
            SceneSerializer.setAttribute(document, digitalElement, "NO", normallyOpenList.get(i).toString());
            SceneSerializer.setAttribute(document, digitalElement, "STEP", stepList.get(i).toString());
            nodeElement.appendChild(digitalElement);
        }

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception{
        for (Node element : SceneSerializer.getChildNode (node)) {
            if (element.getNodeName().equals(DIGITAL_NODE)){
                int idx;
                boolean no,step;
                
                idx = Integer.parseInt(SceneSerializer.getAttributeValue(element, "idx"));
                no = Boolean.parseBoolean(SceneSerializer.getAttributeValue(element, "NO"));
                step = Boolean.parseBoolean(SceneSerializer.getAttributeValue(element, "STEP"));
                normallyOpenList.set(idx,no);
                stepList.set(idx,step);
            }
        }

    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        if (serialNumber.isEmpty()){
                JOptionPane.showMessageDialog(null, "Il dispositivo "+ blockType.toString()+" di indirizzo:" + addressList.get(0)+" , commento: "+
                        comment+" NON ha numero di serie.\n IL DISPOSITIVO NON FUNZIONERA'","Errore",JOptionPane.ERROR_MESSAGE);
            }
        String retVal="";
        int startIndex = deviceIndex;

        if (blockType == CommonDefinitions.blockTypes.DI8) {
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
            retVal += new Formatter().format(driverFormat,serialNumber,0,comment)+"\n";

            //Creo i controller
            for (int i = 1; i <= 8; i++){
                retVal += new Formatter().format(deviceConfigFormat,startIndex+i,comment);
                retVal += new Formatter().format(controllerFormat,
                                                 deviceIndex,                   //Input
                                                 new Integer(i).toString(),     //Channel
                                                 addressList.get(i-1),          //Address
                                                 "1",                           //IO
                                                 "0",                           //TIMERID
                                                 boolToInteger(normallyOpenList.get(i-1)).toString(),  //INVERTOUT
                                                 "0",                           //STEP
                                                 comment) + "\n";               //COMMENT
            }
        }
        else if (blockType == CommonDefinitions.blockTypes.DO8) {
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
            retVal += new Formatter().format(driverFormat,serialNumber,1,comment)+"\n";

            //Creo i controller
            for (int i = 1; i <= 8; i++){
                retVal += new Formatter().format(deviceConfigFormat,startIndex+i,comment);
                retVal += new Formatter().format(controllerFormat,
                                                 deviceIndex,                   //Input
                                                 new Integer(i).toString(),     //Channel
                                                 addressList.get(i-1),          //Address
                                                 "0",                           //IO
                                                 "0",                           //TIMERID
                                                 boolToInteger(normallyOpenList.get(i-1)).toString(),  //INVERTOUT
                                                 boolToInteger(stepList.get(i-1)).toString(),          //STEP
                                                 comment) + "\n";               //COMMENT
            }
        }
        else {
            retVal += new Formatter().format(deviceConfigFormat,deviceIndex,comment);
            retVal += new Formatter().format(driverFormat,serialNumber,2,comment)+"\n";

            //Creo i controller
            for (int i = 1; i <= 4; i++){
                retVal += new Formatter().format(deviceConfigFormat,startIndex+i,comment);
                retVal += new Formatter().format(controllerFormat,
                                                 deviceIndex,                   //Input
                                                 new Integer(i).toString(),     //Channel
                                                 addressList.get(i-1),          //Address
                                                 "0",                           //IO
                                                 "0",                           //TIMERID
                                                 boolToInteger(normallyOpenList.get(i-1)).toString(),  //INVERTOUT
                                                 boolToInteger(stepList.get(i-1)).toString(),  //STEP
                                                 comment) + "\n";               //COMMENT
            }

            //Creo i controller
            for (int i = 5; i <= 8; i++){
                retVal += new Formatter().format(deviceConfigFormat,startIndex+i,comment);
                retVal += new Formatter().format(controllerFormat,
                                                 deviceIndex,                   //Input
                                                 new Integer(i).toString(),     //Channel
                                                 addressList.get(i-1),          //Address
                                                 "1",                           //IO
                                                 "0",                           //TIMERID
                                                 boolToInteger(normallyOpenList.get(i-1)).toString(),  //INVERTOUT
                                                 "0",                           //STEP
                                                 comment) + "\n";               //COMMENT
            }
        }

        return retVal;
    }

    @Override
    public boolean parseCmd(Cmd com){
        //Leggo se il messaggio e' per me
        int address = Integer.parseInt(com.getValue("ADDRESS"));
        for (int i =0; i < 8; i++){
            if (address == addressList.get(i)){

                String tempVal = com.getValue("STATE");
                AFOPin pin = pinList.get(i);

                if (pin.getPinType() == AFOPin.E_PinType.PIN_INPUT){
                    pin.getPinNameWidget().setLabel("("+tempVal+")" + "OUT"+(i+1));
                }
                else {
                    pin.getPinNameWidget().setLabel("IN"+(i+1)+"("+tempVal+")");
                }

                try {
                    ((DidoDialog) propertiesDialog).getChannelPanel(i).txtStatus.setText(new Integer(tempVal).toString());
                    setTargetPinValue(pin, tempVal);
                } catch (NumberFormatException numberFormatException) {
                    System.out.println("Errore nel DIDO:"+numberFormatException.getMessage()+"\ntempVal="+tempVal);
                    numberFormatException.printStackTrace();
                    //TODO da rimuovere ma e' per capire cosa provoca questo errore
                    System.exit(1);
                }
                getScene().validate();
                return true;
            }
        }

        return false;
    }

    private final static String driverFormat = "NAME:DS2408,SN:%s,ACTIVITY:0,TYPE:%d,COMMENT:%s";
    //NAME:DigitalINOUT,INPUT,IO,STARTV,CHANNEL,ADDR,TIMERID,INVERTOUT,STEP,COMMENT
    private final static String controllerFormat = "NAME:DigitalINOUT,INPUT:%s,CHANNEL:%s,ADDR:%s,IO:%s,TIMERID:%s,INVERTOUT:%s,STEP:%s,COMMENT:%s";

    private final static String DIGITAL_NODE = "digitalio";
    private final static String DIGITAL_INDEX = "index";

    @Override
    public void actionPerformed(ActionEvent e) {
        int panelIdx = -1;
        //Devo beccare da quale bottone mi è arrivato il comando
        for (int i =0; i < 8; i++){
            if (((DidoDialog)propertiesDialog).getChannelPanel(i).btnChange.equals(e.getSource())){
                panelIdx = i;
                break;
            }
        }

        if (panelIdx >= 0){
            Cmd com = new Cmd("DEVICE");

            com.putValue("COMMAND","ChangeDOState");
            com.putValue("ADDRESS", ((DidoDialog)propertiesDialog).getChannelPanel(panelIdx).txtChannel.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
        }
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        for (int i =0; i < 8; i++){
            ((DidoDialog)propertiesDialog).getChannelPanel(i).btnChange.setEnabled(isConnected);
            ((DidoDialog)propertiesDialog).getChannelPanel(i).checkNO.setEnabled(!isConnected);
        }

        if (blockType == CommonDefinitions.blockTypes.DO8){
            for (int i = 0; i < 8; i++)
            {
                ((DidoDialog)propertiesDialog).getChannelPanel(i).checkStep.setEnabled(!isConnected);
            }
        }
        else if (blockType == CommonDefinitions.blockTypes.IOD4){
            for (int i = 0; i < 4; i++)
            {
                ((DidoDialog)propertiesDialog).getChannelPanel(i).checkStep.setEnabled(!isConnected);
            }
        }
    }
}
