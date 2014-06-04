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
import java.util.List;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import nodes.AFONodeWidget;
import nodes.AFOSystemManager;
import nodes.CommonDefinitions;
import nodes.MyGraphPinScene;
import nodes.SceneSerializer;
import nodes.gui.ClimaticCurveDialog;
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
public class ClimaticCurveNode extends GenericNode implements ActionListener {

    private Integer climateID = 1;

    public ClimaticCurveNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.ClimaticCurve;
        
        CreateWidget();
        CreatePropertiesDialog();
        calculateClimaticID();
    }

    public void calculateClimaticID(){
        ArrayList<AFOSystemManager> list = (ArrayList<AFOSystemManager>) ((MyGraphPinScene)scene).getManager().getProject().getSubSystemList();

        //Ciclo su tutto e vedo il primo ID libero
        climateID = 0;
        for (AFOSystemManager sm : list){
            for (AFONode node : sm.getNodeList()){
                if (node.getBlockType() == CommonDefinitions.blockTypes.ClimaticCurve){
                    Integer newID = ((ClimaticCurveNode)node).getClimateID();
                    //Se trovo un ID piu' grande lo memorizzo
                    if (newID > climateID){
                        climateID = newID;
                    }
                }
            }
        }

        //A questo punto in climateID ci dovrebbe essere il valore piu' grande ->
        //lo aumento di 1
        climateID = climateID+1;
        ((AFONodeWidget)widget).setSubType(climateID.toString());
    }

    private void CreateWidget() {
         widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Climatic");

        //Aggiungo alla scena:
        getScene().addNode(this);

        String pinLabel = "Out";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("OUT1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        pinLabel = "In";
        pinID = "pin"+ ++scene.pinIDCounter;
        newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        pinList.add(newPin);
        getScene().addPin(this, newPin);

        ((AFONodeWidget)widget).setSubType(getClimateID().toString());

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);
    }

    private void CreatePropertiesDialog() {
        propertiesDialog = new ClimaticCurveDialog(null, true);

        ((ClimaticCurveDialog)propertiesDialog).btnReadParameters.addActionListener(this);
        ((ClimaticCurveDialog)propertiesDialog).btnOk.addActionListener(this);
        ((ClimaticCurveDialog)propertiesDialog).btnSend.setVisible(false);
        ((ClimaticCurveDialog)propertiesDialog).btnCancel.addActionListener(this);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ((ClimaticCurveDialog)propertiesDialog).btnReadParameters){

            Cmd com = generateBasicNodeCommand();
            com.putValue("BLOCKEXEC", "SetClimaticCurve");


            //Riordino la tabella: per sicurezza reimposto l'ordinamento
//            sorter = new TableRowSorter<TableModel>(pan.climaticTable.getModel());
//            sortKeys = new ArrayList<RowSorter.SortKey>();
//            sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
//            sorter.setSortKeys(sortKeys);
//            pan.climaticTable.setRowSorter(sorter);
//            pan.climaticTable.getRowSorter().allRowsChanged();


            int nOfPoints = 0;

            for (int row = 0; row < ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getRowCount(); row++) {
                String pointString = "POINT";
                String pointValue;
                String tExt = ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel().getValueAt(row, 0).toString();
                String tMnd = ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel().getValueAt(row, 1).toString();
                if ((tExt.length() > 0) && (tMnd.length() > 0)) {
                    nOfPoints++;
                    pointString += nOfPoints;
                    pointValue = tExt + ":" + tMnd;
                    com.putValue(pointString, pointValue);
                }
            }

            com.putValue("NOFPOINTS", "" + nOfPoints);

            if ((kernel != null ) && (kernel.isConnected())) {
                sendCommand(com);
            }
            propertiesDialog.setVisible(false);
        }
        else if (e.getSource() == ((ClimaticCurveDialog)propertiesDialog).btnReadParameters){
            Cmd com = generateBasicNodeCommand();
            com.putValue("BLOCKEXEC", "GetClimaticCurve");

            sendCommand(com);
        }
       else if (e.getSource() == ((ClimaticCurveDialog)propertiesDialog).btnCancel){
        propertiesDialog.setVisible(false);
    }

    }

    @Override
    public void showPropertiesDialog() throws Exception{

        ((ClimaticCurveDialog)propertiesDialog).txtAddress.setText(addressList.get(0).toString());
        ((ClimaticCurveDialog)propertiesDialog).txtClimaticID.setText(getClimateID().toString());
        propertiesDialog.setModal(true);
        propertiesDialog.setVisible(true);

        if ( ((ClimaticCurveDialog)propertiesDialog).getReturnStatus() == ClimaticCurveDialog.RET_OK ){
            setClimateID((Integer) Integer.parseInt(((ClimaticCurveDialog)propertiesDialog).txtClimaticID.getText()));
            ((AFONodeWidget)widget).setSubType(getClimateID().toString());
        }
    }


    /**
     * @return the climateID
     */
    public Integer getClimateID() {
        return climateID;
    }

    /**
     * @param climateID the climateID to set
     */
    public void setClimateID(Integer climateID) {
        this.climateID = climateID;
    }

    @Override
    public String getIniString(int deviceIndex) throws Exception {
        String retVal="";

        retVal +="NAME:CLIMATICA,ID:"+climateID+","+getCommonData()+"COMMENT:";

        return retVal;
    }

    /**
     * Crea una stringa che contiene la propria sezione
     * @return stringa
     */
    public String getClimateSection(){
        String retVal = "[CLIMATE"+climateID+"]\r\n";
        Integer nOfPoints = 0;
        DefaultTableModel pointsTable = (DefaultTableModel) ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel();
        for (int i = 0; i < pointsTable.getRowCount(); i++){
            nOfPoints++;
            retVal+="Point"+nOfPoints.toString()+"=";
            retVal+="TEXT:"+pointsTable.getValueAt(i, 0)+", TMND:"+pointsTable.getValueAt(i, 1);
            retVal+="\r\n";
        }

        retVal+="nOfPoints="+nOfPoints.toString();

        return retVal;

    }

    @Override
    public Element serializeNode(Document document, Element nodeElement) throws Exception {

        SceneSerializer.setAttribute(document, nodeElement, "ClimaticID", climateID.toString());
        DefaultTableModel tableModel = (DefaultTableModel) ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel();
        SceneSerializer.setAttribute(document, nodeElement, "NofRows", ""+tableModel.getRowCount());

        for (int i =0 ; i < tableModel.getRowCount(); i++){
            Element climaticElement = document.createElement ("TableRow");
            SceneSerializer.setAttribute(document, climaticElement, "TempExt", tableModel.getValueAt(i, 0).toString());
            SceneSerializer.setAttribute(document, climaticElement, "TempMnd", tableModel.getValueAt(i, 1).toString());
            nodeElement.appendChild(climaticElement);
        }

        return nodeElement;
    }

    @Override
    public void deserializeNode(Node node) throws Exception {

        DefaultTableModel tableModel = (DefaultTableModel) ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel();
        climateID = Integer.parseInt(SceneSerializer.getAttributeValue(node, "ClimaticID"));
        int row = 0;

        for (Node element : SceneSerializer.getChildNode (node)) {
            if (element.getNodeName().equals("TableRow")){
                String tExt,tMnd;
                tExt = SceneSerializer.getAttributeValue(element, "TempExt");
                tMnd = SceneSerializer.getAttributeValue(element, "TempMnd");
                tableModel.addRow(new Object[][]{});
                tableModel.setValueAt(tExt, row, 0);
                tableModel.setValueAt(tMnd, row, 1);
                row++;
            }
        }

        propertiesDialog.validate();
    }

    @Override
    public boolean parseBlockCmd(Cmd com) {

        //Leggo se il messaggio e' per me
        if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
            if (com.containsAttribute("BLOCKEXEC")) {
                //E' un comando
                if (UtilXML.cmpCmdValue(com, "BLOCKEXEC", "ClimaticCurve")) {
                    if (com.containsAttribute("NOFPOINTS"))
                    {
                        DefaultTableModel table = (DefaultTableModel) ((ClimaticCurveDialog)propertiesDialog).tableClimatic.getModel();
                        ((ClimaticCurveDialog)propertiesDialog).clearTable();

                        int nOfPoints = Integer.parseInt(com.getValue("NOFPOINTS"));

                        for (int i = 0; i < nOfPoints; i++) {
                            String pointStr = "POINT"+(i+1);
                            String tokens[] = com.getValue(pointStr).split(":");

                            table.addRow(new Object[][]{});
                            table.setValueAt(Float.parseFloat(tokens[0]), i, 0);
                            table.setValueAt(Float.parseFloat(tokens[1]), i, 1);
                        }

                        //pan.climaticTable.getRowSorter().allRowsChanged();
                    }
                }
            }
            else {
                return parseStandardBlockCmd(com);
            }
        }

        return false;
    }



    public void setConnectionStatus(boolean isConnected) {

        ((ClimaticCurveDialog) propertiesDialog).btnReadParameters.setEnabled(isConnected);

    }
}

