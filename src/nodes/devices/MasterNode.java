/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.SceneSerializer;
import nodes.gui.MasterPropertiesDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFONode;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class MasterNode extends GenericNode  {
    
    public String serPort = "/dev/ttyS3";
    public String netDelay = "0";
    public boolean isWl = false;
    public String wlNet = "1";
    public String wlSubNet = "1";
    public boolean isOverIP = false;
    public String ipAddress = "192.168.0.90";



    public MasterNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        blockType = CommonDefinitions.blockTypes.Master;
        configSlotsUsed = 0;

        CreateWidget();
        CreatePropertiesDialog();
    }

    private void CreateWidget(){
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName(label);

        String pinLabel = "BUS";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(true);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));

        //Aggiungo alla scena:
        getScene().addNode(this);
        getScene().addPin(this, newPin);


        pinList.add(newPin);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);

    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new MasterPropertiesDialog(null, true);
        setPropertiesFields();
    }

    /**
     * Imposta nella dialog delle propietà i singoli campi
     */
    private void setPropertiesFields(){
        MasterPropertiesDialog dlg = (MasterPropertiesDialog)propertiesDialog;
        int index = 0;
        //((MasterPropertiesDialog)propertiesDialog).txtSerPort.setText(serPort);
        for (index = 0; index < dlg.comboSerPort.getItemCount(); index++){
            if (serPort.equals(dlg.comboSerPort.getItemAt(index).toString())){
                break;
            }
        }

        if (index == dlg.comboSerPort.getItemCount()){
            index = 0;
        }

        dlg.comboSerPort.setSelectedIndex(index);
        
        ((MasterPropertiesDialog)propertiesDialog).txtWLNet.setText(wlNet);
        ((MasterPropertiesDialog)propertiesDialog).txtWLSubnet.setText(wlSubNet);
        ((MasterPropertiesDialog)propertiesDialog).txtIpAddr.setText(ipAddress);

        ((MasterPropertiesDialog)propertiesDialog).checkIsWL.setSelected(isWl);
        ((MasterPropertiesDialog)propertiesDialog).isWLSelected();

        ((MasterPropertiesDialog)propertiesDialog).checkOverIP.setSelected(isOverIP);
        ((MasterPropertiesDialog)propertiesDialog).isOverIpSelected();

    }

    /**
     * Legge dalla dialog delle propietà i singoli campi e li aggiorna internamente
     */
    private void getPropertiesFields(){
        //serPort = ((MasterPropertiesDialog)propertiesDialog).txtSerPort.getText();
        serPort = ((MasterPropertiesDialog)propertiesDialog).comboSerPort.getSelectedItem().toString();
        wlNet = ((MasterPropertiesDialog)propertiesDialog).txtWLNet.getText();
        wlSubNet = ((MasterPropertiesDialog)propertiesDialog).txtWLSubnet.getText();
        ipAddress = ((MasterPropertiesDialog)propertiesDialog).txtIpAddr.getText();

        isOverIP = ((MasterPropertiesDialog)propertiesDialog).checkOverIP.isSelected();
        isWl = ((MasterPropertiesDialog)propertiesDialog).checkIsWL.isSelected();
    }

    @Override
    public void showPropertiesDialog() throws Exception{

        setPropertiesFields();
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);
        if ( ((MasterPropertiesDialog)propertiesDialog).getReturnStatus() == MasterPropertiesDialog.RET_OK ){
            getPropertiesFields();
        }
    }

    @Override
    public Element serializeNode(Document xml,Element nodeElement) throws Exception {
        SceneSerializer.setAttribute(xml, nodeElement, MASTER_PORT, serPort);

        SceneSerializer.setAttribute(xml, nodeElement, MASTER_ISWIRELESS, Boolean.toString(isWl));
        SceneSerializer.setAttribute(xml, nodeElement, MASTER_WL_NET, wlNet);
        SceneSerializer.setAttribute(xml, nodeElement, MASTER_WL_SUBNET, wlSubNet);

        SceneSerializer.setAttribute(xml, nodeElement, MASTER_ISOVERIP, Boolean.toString(isOverIP));
        SceneSerializer.setAttribute(xml, nodeElement, MASTER_IPADDR, ipAddress);
        return nodeElement;
    }

    @Override
    public void deserializeNode(Node element){
        serPort = SceneSerializer.getAttributeValue(element, MASTER_PORT);

        isWl = Boolean.parseBoolean(SceneSerializer.getAttributeValue(element, MASTER_ISWIRELESS));
        wlNet = SceneSerializer.getAttributeValue(element, MASTER_WL_NET);
        wlSubNet = SceneSerializer.getAttributeValue(element, MASTER_WL_SUBNET);

        isOverIP = Boolean.parseBoolean(SceneSerializer.getAttributeValue(element, MASTER_ISOVERIP));
        ipAddress = SceneSerializer.getAttributeValue(element, MASTER_IPADDR);

    }

    /*
     * PortaComunicazione=/dev/ttyS3
        WIRELESS=ISWL:0,NETADDR:1,SUBNETADDR:11
        OverIP=ISOIP:0,ADDR:192.168.0.200,PORT:1470
     */
    @Override
    public String getIniString(int devIndex) throws Exception{
        String configString="";

        configString+="PortaComunicazione="+serPort+"\n";
        configString+=  "RitardoNet=0\n"+
                        "SoglieDiAllarme=AllarmeMax:22,AllarmeMin:10\n"+
                        "AllarmiTemperaturaSw=1\n"+
                        "TimerID=0\n"+
                        "TempoScatto=0.1\n"+
                        "IButtonReader=0\n"+
                        "SaveDigitalState=0\n";  //< Aggiunto 22/06/2010

        return configString;

    }

    private static final String MASTER_PORT = "Port";
    private static final String MASTER_ISWIRELESS = "IsWL";
    private static final String MASTER_WL_NET = "WLNet";
    private static final String MASTER_WL_SUBNET = "WLSubNet";
    private static final String MASTER_ISOVERIP = "IsOverIP";
    private static final String MASTER_IPADDR = "IPAddress";

}
