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
import nodes.gui.GenericBlockDialog;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class SaturationNode extends GenericNode implements ActionListener{

    private String upperLimit = "0.0";
    private String lowerLimit = "0.0";

    public SaturationNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Saturation;
        CreatePropertiesDialog();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Limit");

        String pinLabel = "OUT";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setPinIDString("OUT1");
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
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


        ((AFONodeWidget)widget).setSubType(getLowerLimit()+"-"+getUpperLimit());

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

        @Override
    public void showPropertiesDialog() throws Exception{

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((GenericBlockDialog)propertiesDialog).txtField2.setText(getUpperLimit());
        ((GenericBlockDialog)propertiesDialog).txtField1.setText(getLowerLimit());
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((GenericBlockDialog)propertiesDialog).getReturnStatus() == GenericBlockDialog.RET_OK ){
            setUpperLimit(((GenericBlockDialog) propertiesDialog).txtField2.getText());
            setLowerLimit(((GenericBlockDialog) propertiesDialog).txtField1.getText());
            ((AFONodeWidget)widget).setSubType(getLowerLimit()+"-"+getUpperLimit());
        }
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog)propertiesDialog).txtAddress.setText("NA");

        ((GenericBlockDialog)propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog)propertiesDialog).lblField1.setText("Lower:");
        ((GenericBlockDialog)propertiesDialog).txtField1.setText(getLowerLimit());

        ((GenericBlockDialog)propertiesDialog).lblField2.setText("Upper:");
        ((GenericBlockDialog)propertiesDialog).txtField2.setText(getUpperLimit());

        ((GenericBlockDialog)propertiesDialog).btnReadParams.addActionListener(this);
        ((GenericBlockDialog)propertiesDialog).btnOk.addActionListener(this);
        ((GenericBlockDialog)propertiesDialog).btnCancel.addActionListener(this);
        
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setVisible(false);

    }

    /**
     * @return the upperLimit
     */
    public String getUpperLimit() {
        return upperLimit;
    }

    /**
     * @return the lowerLimit
     */
    public String getLowerLimit() {
        return lowerLimit;
    }

    /**
     * @param upperLimit the upperLimit to set
     */
    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * @param lowerLimit the lowerLimit to set
     */
    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        retVal +="NAME:SATURAZIONE,TYPE:SAT"+","+"MIN:"+lowerLimit+",MAX:"+upperLimit+","+getCommonData()+"COMMENT:"+comment;


        return retVal;
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "SatParameters")) {

                    if (com.containsAttribute("MIN")){
                        lowerLimit = com.getValue("MIN");
                    }

                    if (com.containsAttribute("MAX")){
                        upperLimit = com.getValue("MAX");
                    }

                    ((AFONodeWidget)widget).setSubType(getLowerLimit()+"-"+getUpperLimit());
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

            if ( (!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Min Errato") )
                    || (!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField2.getText(), "Max Errato") ))
               {
                return;
            }

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "SetParameters");

            com.putValue("MIN", ((GenericBlockDialog) propertiesDialog).txtField1.getText());
            com.putValue("MAX", ((GenericBlockDialog) propertiesDialog).txtField2.getText());

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
            propertiesDialog.setVisible(false);

       // } else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnSendParam2)) {
         //   if ( (!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField2.getText(), "Max Errato") )
           //    ){
             //   return;
           // }

          //  Cmd com = generateBasicNodeCommand();

            //com.putValue("BLOCKEXEC", "SetParameters");

            //com.putValue("MAX", ((GenericBlockDialog) propertiesDialog).txtField2.getText());

          //  sendCommand(com);

        }

   else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnReadParams)) {

            Cmd com = generateBasicNodeCommand();

            com.putValue("BLOCKEXEC", "GetParameters");

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
        }
        else if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnCancel)) {
            propertiesDialog.setVisible(false);
        }
    }

    @Override
    public void setConnectionStatus(boolean isConnected){
        ((GenericBlockDialog)propertiesDialog).btnReadParams.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnSendParam1.setEnabled(isConnected);
        ((GenericBlockDialog)propertiesDialog).btnSendParam2.setEnabled(isConnected);
    }

}
