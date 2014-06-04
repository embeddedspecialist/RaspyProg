/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes.devices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import nodes.FieldChecker;
import nodes.gui.GenericBlockDialog;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFONode;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class ClockNode extends GenericNode implements ActionListener {

    public ClockNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);



        blockType = CommonDefinitions.blockTypes.Timer;
        CreateWidget();
        CreatePropertiesDialog();


    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("Timer");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "Time";
        String pinID = "pin" + ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "Reset";
        pinID = "pin" + ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new GenericBlockDialog(null, true);

        ((GenericBlockDialog) propertiesDialog).txtAddress.setText("NA");

        //Combo
        ((GenericBlockDialog) propertiesDialog).lblCombo.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).comboSubType.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).lblField2.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).txtField2.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).btnSendParam1.setVisible(false);
        ((GenericBlockDialog) propertiesDialog).btnSendParam2.setVisible(false);

        ((GenericBlockDialog) propertiesDialog).btnOk.addActionListener(this);
        ((GenericBlockDialog) propertiesDialog).btnCancel.addActionListener(this);


    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal = "";

        retVal += "NAME:CLOCK," + getCommonData() + "COMMENT:" + comment;


        return retVal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(((GenericBlockDialog) propertiesDialog).btnOk)) {
            if ((!FieldChecker.checkStringIsNumeric(((GenericBlockDialog) propertiesDialog).txtField1.getText(), "Valore Errato"))) {
                return;
            }
            propertiesDialog.setVisible(false);
        }
       else if (e.getSource().equals(((GenericBlockDialog)propertiesDialog).btnCancel)) {
            propertiesDialog.setVisible(false);

        }
    }

    @Override
    public void setConnectionStatus(boolean isConnected) {

        ((GenericBlockDialog) propertiesDialog).btnReadParams.setEnabled(isConnected);

    }
}


