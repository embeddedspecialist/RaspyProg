/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import connection.Kernel;
import connection.XMLCommands.Cmd;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import nodes.devices.AfoTimer;
import nodes.devices.ClimaticCurveNode;
import nodes.devices.CommentNode;
import nodes.devices.GenericNode;
import nodes.devices.MasterNode;
import nodes.devices.ScopeNode;
import nodes.gui.ProjectDialog;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sceneManager.AFONode;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class Project implements ActionListener{
    
    private AFOSystemManager mainSystem;
    private List<AFOSystemManager> subSystemsList = new ArrayList<AFOSystemManager>();
    private List<AfoTimer> timerList = new ArrayList<AfoTimer>();
    public boolean projectChanged = false;
    /////////////////////////////////////////////////////
    private boolean isActiveProject;
    private String projectFolder;
    private String projectName;
    //////////////////////////////////////////////////////
    private long nextSubSystemID = 0;

    private ProjectDialog myDialog;

    private JTabbedPane myTabbedPane;

    private Kernel kernel;

    public Project () {
        mainSystem = new AFOSystemManager(this);
        myDialog = new ProjectDialog();
        myDialog.btnChangeIP.setEnabled(false);
        myDialog.btnChangeIP.addActionListener(this);
        try {
            kernel = new Kernel(this, null);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public Project (String projectName, String projectFolder) {
        this.projectName = projectName;
        this.projectFolder = projectFolder;
        if (!this.projectFolder.endsWith(File.separator)){
            this.projectFolder+=File.separator;
        }
        mainSystem = new AFOSystemManager(this);
        myDialog = new ProjectDialog();
        myDialog.btnChangeIP.setEnabled(false);
        myDialog.btnChangeIP.addActionListener(this);
    }

    //Overload
    public void addSubSystem(Point point) {
        addSubSystem(point, null);
    }

    //Questa mi serve nella saveAS per fare in modo che NON vengano cancellati i 
    //file vecchi dei sottosistemi
    public void cleanSubsystemFileName(){
        mainSystem.fileName = " ";
        for (AFOSystemManager sys : subSystemsList){
            sys.fileName = " ";
        }
    }
    
    public void addSubSystem(Point point, String id) {

        if (id == null){
            id = new String();
            id = Long.toString(nextSubSystemID++);
        }
        else {
            if (Long.parseLong(id) >= nextSubSystemID){
                nextSubSystemID = Long.parseLong(id)+1;
            }
        }

        AFOSystemManager node = new AFOSystemManager(this, id, "SubSystem", mainSystem.getSubSystemScene());
        mainSystem.getSubSystemScene().validate();
        mainSystem.getSubSystemScene().getSceneAnimator().animatePreferredLocation(node.getWidget(),point);

        node.setParentSystem(mainSystem);
        node.setParentNode(node);
        //Potrei anche togliere dal progetto la lista dei sottosistemi tanto
        //in questo modo finisce sempre in una lista
        mainSystem.addNode(node);
        subSystemsList.add(node);
    }

    public void addSubSystem(AFOSystemManager parent, Point point, String id) {

        if (id == null){
            id = new String();
            id = Long.toString(nextSubSystemID++);
        }
        else {
            if (Long.parseLong(id) >= nextSubSystemID){
                nextSubSystemID = Long.parseLong(id)+1;
            }
        }

        AFOSystemManager node = new AFOSystemManager(this, id, "SubSystem", parent.getSubSystemScene());
        parent.getSubSystemScene().validate();
        parent.getSubSystemScene().getSceneAnimator().animatePreferredLocation(node.getWidget(),point);

        node.setParentSystem(parent);
        node.setParentNode(node);
        //Potrei anche togliere dal progetto la lista dei sottosistemi tanto
        //in questo modo finisce sempre in una lista
        parent.addNode(node);
        subSystemsList.add(node);
    }


    public void openProject()
    {
        
    }
    
    public void closeProject()
    {
        
    }

    /**
     * Questa funzione rigenera gli ID dei controlli climatici
     */
    public void regenerateClimaticIDs(){
        //Prima resetto tutti i controlli climatici
        for (AFOSystemManager sm : subSystemsList){
            for (AFONode node : sm.getNodeList()){
                if (node.getBlockType() == CommonDefinitions.blockTypes.ClimaticCurve){
                    ((ClimaticCurveNode)node).setClimateID(0);
                }
            }
        }

        //Ora li riassegno
        for (AFOSystemManager sm : subSystemsList){
            for (AFONode node : sm.getNodeList()){
                if (node.getBlockType() == CommonDefinitions.blockTypes.ClimaticCurve){
                    ((ClimaticCurveNode)node).calculateClimaticID();
                }
            }
        }

    }
    public void showDialog(){
        getDialog().showDialog(null);
    }
    
    public long getNextSubSystemID() {
        return nextSubSystemID;
    }

    public void setNextSubSystemID(long nextSubSystemID) {
        this.nextSubSystemID = nextSubSystemID;
    }
    
    public String getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public MyGraphPinScene getMainScene() {
        return mainSystem.getSubSystemScene();
    }
    

    public JComponent getAfoView() {
        return mainSystem.getAfoView();
    }

    public AFOSystemManager getMainSystem() {
        return mainSystem;
    }

    public void setSystemManager(AFOSystemManager systemManager) {
        this.mainSystem = systemManager;
    }
    
        public boolean IsActiveProject() {
        return isActiveProject;
    }

    public void setAsActiveProject(boolean isActiveProject) {
        this.isActiveProject = isActiveProject;
    }
       

    public List<AFOSystemManager> getSubSystemList() {
        return subSystemsList;
    }

    private String createPortsConfiguration(int table){

       String retVal="";
       String interfaceString;
       DefaultTableModel tableModel;
       int rowCount;
       
       boolean isServer=true;

       if (table == ProjectDialog.INPUT_TABLE){
            retVal+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_NOF_INPORTS.ordinal()];
            tableModel = (DefaultTableModel) getDialog().tableInput.getModel();
            rowCount = getDialog().tableInput.getRowCount();
            interfaceString = CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_INPORT.ordinal()];
       }
       else if (table == ProjectDialog.INTERFACES_TABLE){
           retVal+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_NOF_INTERFACEPORTS.ordinal()];
           tableModel = (DefaultTableModel) getDialog().tableInterfacce.getModel();
           rowCount = getDialog().tableInterfacce.getRowCount();
           interfaceString = CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_INTERFACEPORT.ordinal()];
       }
       else {
           retVal+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_NOF_OUTPORTS.ordinal()];
           tableModel = (DefaultTableModel) getDialog().tableOutput.getModel();
           rowCount = getDialog().tableOutput.getRowCount();
           interfaceString = CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_OUTPORT.ordinal()];
           isServer = false;
       }

       retVal+="="+rowCount+"\r\n";

       for (int i = 0; i < rowCount; i++) {

           Formatter format = new Formatter();
           retVal+=interfaceString+(i+1)+"=";
           //((Vector)getDataVector().elementAt(1)).elementAt(5);
           if (isServer){
               retVal+=format.format(SERVER_PORT_CONFIG,
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(0),
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(1)
                       );
           }
           else {
               retVal+=format.format(CLIENT_PORT_CONFIG,
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(0),
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(1),
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(2),
                       ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(3)
                       );
           }

           retVal+="\r\n";
       }

       return retVal;
    }

    private final String SERVER_PORT_CONFIG="PortType:ServerSocket,ServerIPAddr:%s,ServerPort:%s";
    private final String CLIENT_PORT_CONFIG="PortType:ClientSocket,ServerIPAddr:%s,ServerPort:%s,MINADDR:%s,MAXADDR:%s";

    
    public boolean createIniFile() {

        try {
            String iniFileString = new String();
            File iniFile = new File(projectFolder+"config.ini");
            File blockIniFile = new File(projectFolder+"Blocks.ini");
            File timersIniFile = new File(projectFolder+"timers.ini");
            File climateIniFile = new File(projectFolder+"climatic.ini");
            FileOutputStream iniFileStream;
            Integer NETNumber = 0;
            Integer nOfDevices = 0;
            Integer nOfAddresses = 0;
            MasterNode master;
            AFOPin busPin;
            boolean blockCoordinatorCreated = false;


            int nOfNets = 0;
            int nOfSubsystems = 0;
            

            /////////////////////////////////////////////
            //Inizio Dispositivi
            /////////////////////////////////////////////
            //Parto cercando i master
            for (int nodeIndex = 0; nodeIndex < mainSystem.getNodeListSize(); nodeIndex++) {
                if (mainSystem.getNodeAtIndex(nodeIndex).getBlockType() == CommonDefinitions.blockTypes.Master) {

                    //Cerco tutti i dispositivi collegati
                    master = (MasterNode) mainSystem.getNodeAtIndex(nodeIndex);

                    busPin = master.getPinAt(0);
                    //prendo l'elenco di tutti i blocchi collegati
                    List<AFONode> nodeList = mainSystem.getSubSystemScene().getNodesConnectedAtPin(busPin);

                    if (nodeList.isEmpty()){
                        JOptionPane.showMessageDialog(null,
                                "Il master avente commento "+master.getComment()+" NON e' collegato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    nOfNets++;
                    //Ho trovato un master... comincio a seguire il filo...
                    //Prendo le informazioni dal master e scrivo nella stringa la sezione

                    //Inizializzo i contatori NET, devices e address
                    NETNumber++;
                    nOfDevices = 1;
                    nOfAddresses=0;

                    //Creo la sezione e i parametri comuni
                    iniFileString += "[NET" + NETNumber.toString() + "]\r\n";

                    
                    try {
                        iniFileString+=master.getIniString(nOfDevices);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        return false;
                    }

                    //Creo tutti i device:
                    for (AFONode deviceNode : nodeList) {
                        try {
                            iniFileString+= ((GenericNode) deviceNode).getIniString(nOfDevices);
                            nOfDevices+=((GenericNode) deviceNode).getConfigSlotsUsed();
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        
                    }

                    //Aggiungo il block coordinator alla fine della prima NET
                    if (!blockCoordinatorCreated){

                        iniFileString+=new Formatter().format("Device%02d=NAME:BlockCoord,ADDR:100000\n",nOfDevices);

                        blockCoordinatorCreated=true;
                    }

                    //Aggiorno il numero di dispositivi
                    iniFileString+="NofDev="+nOfDevices.toString() + "\r\n";

                    //faccio una ricerca di driver NON connessi a master:
                    ArrayList<AFONode> disconnectedNodes = new ArrayList<AFONode>();
                    for (AFOPin pin : mainSystem.getSubSystemScene().getPins()) {
                        if (pin.isBusPin() && (mainSystem.getSubSystemScene().findPinEdges(pin, true, true).isEmpty())){
                            //Il pin non e' collegato a niente
                            disconnectedNodes.add(pin.getParentNode());
                        }
                    }

                    if (!disconnectedNodes.isEmpty()) {
                        String message = "";
                        for (AFONode node : disconnectedNodes){
                            if (node.getBlockType() != CommonDefinitions.blockTypes.Master){
                                message+="Type: "+node.getBlockType()+" Commento:"+node.getComment()+"\n";
                            }
                        }

                        JOptionPane.showMessageDialog(null,
                                "I seguenti blocchi NON sono collegati ad alcun master:\n"+message,
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }                    
                } //IF Master
            } //FOR nodeListSize

            //Creazione della sezione COMMON
            iniFileString += "[COMMON]\r\n";
            //Scrivo le interfacce
            iniFileString+=createPortsConfiguration(ProjectDialog.INTERFACES_TABLE);
            iniFileString+=createPortsConfiguration(ProjectDialog.INPUT_TABLE);
            iniFileString+=createPortsConfiguration(ProjectDialog.OUTPUT_TABLE);

            //Altri dati
            //Miscellanea
            iniFileString+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_DODEBUG.ordinal()]
                    +"="+getDialog().comboDebugLevel.getSelectedIndex()+"\r\n";

            iniFileString+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_WAITONSTARTUP.ordinal()] +
                    "=" +"0\r\n";
            iniFileString+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_UPDATETIME.ordinal()] +
                    "=" + getDialog().txtUpdateTime.getText()+"\r\n";

            //22/06/2010 -- aggiunto setup iniziale
            iniFileString+="EseguiSetup=1\r\n";



            for (int nodeIndex = 0; nodeIndex < mainSystem.getNodeListSize(); nodeIndex++) {
                if (mainSystem.getNodeAtIndex(nodeIndex).getBlockType() == CommonDefinitions.blockTypes.Subsystem){
                    nOfSubsystems++;
                }
            }

            iniFileString+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_NUMBEROFNETS.ordinal()]
                    +"="+nOfNets+"\r\n";
            iniFileString+=CommonDefinitions.config_Strings[CommonDefinitions.configFileVars.CONF_NOF_SUBSYSTEMS.ordinal()]
                    +"="+nOfSubsystems+"\r\n";

            if (!iniFile.exists())
            {
                iniFile.createNewFile();
            }
            else
            {
                iniFile.delete();
                iniFile.createNewFile();
            }

            iniFileStream = new FileOutputStream(iniFile);

            for (int i = 0; i < iniFileString.length();i++) {
                iniFileStream.write(iniFileString.charAt(i));
            }


            iniFileStream.close();

            /////////////////////////////////////////////
            //Creo Blocks.ini
            /////////////////////////////////////////////
            //Creo le sezioni subsystem ciclo in tutti i subsystem
            Iterator<AFOSystemManager> subSystemIt = subSystemsList.iterator();

            while (subSystemIt.hasNext()){
                AFOSystemManager ssManager = subSystemIt.next();
                boolean nodesSwapped = false;

                //Come in afomonitor.cpp ciclo finchè non ci sono blocchi da invertire
                //Riordino i blocchi in ordine di esecuzione:
                //Se un blocco ha degli ingressi che arrivano da un altro blocco
                //li inverto nella lista.
                //C'e' il rischio che se l'utente ha creato un loop algebrico l'algoritmo si blocchi ->
                //metto un limite alle iterazioni
                int iterationLimit = 100000;
                do {
                    //Array che mi serve per tenere la lista ordinata
                    ArrayList<AFONode> swapNodeList = (ArrayList<AFONode>) ssManager.getNodeList();
                    Iterator<AFONode> nodeIt = ssManager.getNodeList().iterator();
                    nodesSwapped = false;

                    //Ciclo su tutti i nodi di questo sistema
                    while (nodeIt.hasNext()){
                        AFONode nodeExamined = nodeIt.next();
                        //Ciclo su tutti gli ingressi di questo nodo
                        Iterator<AFOPin> pinIt = nodeExamined.getPinList().iterator();
                        while (pinIt.hasNext()){
                            AFOPin pinExamined = pinIt.next();
                            AFOPin sourcePin;
                            AFONode sourceNode;
                            //Se è un pin di ingresso guardo a cosa è attaccato
                            if (pinExamined.getPinType() == AFOPin.E_PinType.PIN_INPUT){
                                //Se è attaccato ad un altro blocco li swappo
                                //I pin di ingresso hanno al massimo un solo edge di ingresso
                                if (ssManager.getSubSystemScene().findPinEdges(pinExamined, false, true).size() > 0){

                                    String edge = (String) ssManager.getSubSystemScene().findPinEdges(pinExamined, false, true).toArray()[0];
                                    sourcePin = ssManager.getSubSystemScene().getEdgeSource(edge);
                                    //TBI forse da controllare se sourcePin != null

                                    //Devo risalire al nodo connesso a questo pin
                                    sourceNode = ssManager.getSubSystemScene().getPinNode(sourcePin);

                                    //Controllo se NON è un nodo di ingresso
                                    if (!sourceNode.getBlockType().equals(CommonDefinitions.blockTypes.IN)){
                                        //Swappo i due blocchi nel vettore di swap
                                        //Se gli indici sono diversi
                                        int sourceNodeIdx = swapNodeList.indexOf(sourceNode);
                                        int examNodeIdx = swapNodeList.indexOf(nodeExamined);

                                        if (examNodeIdx < sourceNodeIdx){
                                            swapNodeList.set(examNodeIdx, sourceNode);
                                            swapNodeList.set(sourceNodeIdx,nodeExamined);
                                            nodesSwapped = true;
                                        }
                                    }//Se il blocco è un IN
                                }//IF ci sono edge attaccati

                            }//if pin is input
                        } // WHile pinIt.hasNext
                    }

                    ssManager.setNodeList(swapNodeList);
                    iterationLimit--;
                } while ((nodesSwapped) && (iterationLimit > 0));

                if (iterationLimit == 0){
                    JOptionPane.showMessageDialog(null, "Attenzione il sottosistema: " + ssManager.getComment() + " contiene un loop",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            //A questo punto comincio a scrivere i sottosistemi
            String blockStr = "";
            subSystemIt = subSystemsList.iterator();
            int subSystemNumber = 1;

//            while (subSystemIt.hasNext()){
//                AFOSystemManager ssManager = subSystemIt.next();
//                Iterator<AFONode> nodeIt = ssManager.getNodeList().iterator();

            Iterator<AFONode> nodeManagerIt = mainSystem.getNodeList().iterator();
            if (!blockIniFile.exists())
            {
                blockIniFile.createNewFile();
            }
            else
            {
                blockIniFile.delete();
                blockIniFile.createNewFile();
            }

            //iniFileStream = new FileOutputStream(blockIniFile);
            FileWriter iniWriter = new FileWriter(blockIniFile);

            while (nodeManagerIt.hasNext()){

                AFONode nm = nodeManagerIt.next();
                if (nm instanceof AFOSystemManager){
                    AFOSystemManager nodeManager = ((AFOSystemManager)nm);
                    int nOfBlocks = 0;

                    //Scrivo l'intestazione
                    iniWriter.write("[SUBSYSTEM" + subSystemNumber+"]\r\n");
                    subSystemNumber++;

                    //Devo scrivere prima in tutti i blocchi il numero di config.ini perche' se ci sono
                    //loop algebrici la sequenza corretta non e' assicurata e quindi sbaglia a
                    //scrivere il file
                    nodeManager.assignConfigNumbers(0);
                    nOfBlocks = nodeManager.createIniBlocks(nOfBlocks, iniWriter);

                    blockStr="NofBlocks = "+nOfBlocks+"\r\n";
                    iniWriter.write(blockStr);
                }

            }//While sui sottosistemi

            iniWriter.write("[COMMON]\r\nTotalSubSystems="+nOfSubsystems);

            iniWriter.flush();
            iniWriter.close();

            //////////////////////////////////////////////////////////////////
            //Creo timers.ini
            //////////////////////////////////////////////////////////////////
            String timerString = "[COMMON]\r\nNumberOfTimers="+timerList.size()+"\r\n";

            for (int timerIdx = 0; timerIdx < timerList.size(); timerIdx++){
                timerString+=timerList.get(timerIdx).getIniString();
            }


            if (!timersIniFile.exists())
            {
                timersIniFile.createNewFile();
            }
            else
            {
                timersIniFile.delete();
                timersIniFile.createNewFile();
            }

            FileOutputStream timerStream = new FileOutputStream(timersIniFile);

            for (int i = 0; i < timerString.length();i++) {
                timerStream.write(timerString.charAt(i));
            }

            //////////////////////////////////////////////////////////////////
            //Creo climatic.ini
            //////////////////////////////////////////////////////////////////
            if (!climateIniFile.exists())
            {
                climateIniFile.createNewFile();
            }
            else
            {
                climateIniFile.delete();
                climateIniFile.createNewFile();
            }

            iniWriter = new FileWriter(climateIniFile);
            Integer nOfClimatic = 0;
            for (AFOSystemManager sm : subSystemsList){
                for (AFONode node : sm.getNodeList()){
                    if (node.getBlockType() == CommonDefinitions.blockTypes.ClimaticCurve){
                        iniWriter.write( ((ClimaticCurveNode)node).getClimateSection()+"\r\n");
                        nOfClimatic++;
                    }
                }
            }

            iniWriter.write("[COMMON]\r\nnOfClimatic="+nOfClimatic.toString());
            iniWriter.flush();
            iniWriter.close();

            JOptionPane.showMessageDialog(null, "Config file written !", "Information", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            JOptionPane.showMessageDialog(null, "Config file NOT written !", "Information", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    void serialize(Document document, Element commonProperties) {
        //Serializzo i parametri comuni
        SceneSerializer.setAttribute(document, commonProperties, "ProjectName", projectName);
        SceneSerializer.setAttribute(document, commonProperties, "UpdateTime", getDialog().txtUpdateTime.getText());
        SceneSerializer.setAttribute(document, commonProperties, "DebugLevel", Integer.toString(getDialog().comboDebugLevel.getSelectedIndex()));
        SceneSerializer.setAttribute(document, commonProperties, "IPAddress", getDialog().txtIPAddress.getText());

        //Passo alle interfacce...
        serializeInterfaceTable(ProjectDialog.INPUT_TABLE, commonProperties, document);
        serializeInterfaceTable(ProjectDialog.INTERFACES_TABLE, commonProperties, document);
        serializeInterfaceTable(ProjectDialog.OUTPUT_TABLE, commonProperties, document);

    }

    Element serializeInterfaceTable(int table, Element parentNode, Document document) {
        DefaultTableModel tableModel;
        int rowCount;
        Element subNode;

        if (table == ProjectDialog.INPUT_TABLE) {
            tableModel = (DefaultTableModel)getDialog().tableInput.getModel();
            subNode = document.createElement ("InputPorts");
        }
        else if (table == ProjectDialog.INTERFACES_TABLE){
            tableModel = (DefaultTableModel)getDialog().tableInterfacce.getModel();
            subNode = document.createElement ("InterfacePorts");
        }
        else {
            tableModel = (DefaultTableModel)getDialog().tableOutput.getModel();
            subNode = document.createElement ("OutputPorts");
        }

        rowCount = tableModel.getRowCount();

        SceneSerializer.setAttribute(document, subNode, "nOfPorts", Integer.toString(rowCount));

        for (int i = 0; i < rowCount; i++){
            Element portElement = document.createElement ("IOPort");
            SceneSerializer.setAttribute(document, portElement, "idx", Integer.toString(i));
            SceneSerializer.setAttribute(document, portElement, "ip", ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(0).toString());
            SceneSerializer.setAttribute(document, portElement, "port", ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(1).toString());
            if (table == ProjectDialog.OUTPUT_TABLE){
                SceneSerializer.setAttribute(document, portElement, "min", ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(2).toString());
                SceneSerializer.setAttribute(document, portElement, "max", ((Vector)tableModel.getDataVector().elementAt(i)).elementAt(3).toString());
            }
            subNode.appendChild(portElement);
        }

        parentNode.appendChild(subNode);

        return parentNode;
    }

    public void deserialize(Node node) throws Exception{
        getDialog().txtUpdateTime.setText(SceneSerializer.getAttributeValue(node, "UpdateTime"));
        getDialog().comboDebugLevel.setSelectedIndex(Integer.parseInt(SceneSerializer.getAttributeValue(node, "DebugLevel")));
        getDialog().txtIPAddress.setText(SceneSerializer.getAttributeValue(node, "IPAddress"));

        for (Node element : SceneSerializer.getChildNode (node)) {
            if (element.getNodeName().equals("InputPorts")){
                deserializeTable(element, ProjectDialog.INPUT_TABLE);
            }
            else if (element.getNodeName().equals("InterfacePorts")){
                deserializeTable(element, ProjectDialog.INTERFACES_TABLE);
            }
            else if (element.getNodeName().equals("OutputPorts")){
                deserializeTable(element, ProjectDialog.OUTPUT_TABLE);
            }

        }

    }

    void deserializeTable(Node node, int table){

        int rowCount = 0;
        DefaultTableModel tableModel;
        if (table == ProjectDialog.INPUT_TABLE) {
            tableModel = (DefaultTableModel)getDialog().tableInput.getModel();
        }
        else if (table == ProjectDialog.INTERFACES_TABLE){
            tableModel = (DefaultTableModel)getDialog().tableInterfacce.getModel();
        }
        else {
            tableModel = (DefaultTableModel)getDialog().tableOutput.getModel();
        }

        tableModel.setRowCount(0);
        
        for (Node element : SceneSerializer.getChildNode (node)) {
            if (element.getNodeName().equals("IOPort")){
                String port, ip;

                port = SceneSerializer.getAttributeValue(element, "port");
                ip = SceneSerializer.getAttributeValue(element, "ip");

                tableModel.addRow(new Object[][]{});
                tableModel.setValueAt(ip,rowCount,0);
                tableModel.setValueAt(port,rowCount,1);
                if (table == ProjectDialog.OUTPUT_TABLE){
                    String min, max;
                    min = SceneSerializer.getAttributeValue(element, "min");
                    max = SceneSerializer.getAttributeValue(element, "max");
                    tableModel.setValueAt(min,rowCount,2);
                    tableModel.setValueAt(max,rowCount,3);
                }

                rowCount++;
            }
        }
    }

    public void addTimer() {
        AfoTimer timer = new AfoTimer(getTimerList().size()+1, "Timer "+(getTimerList().size()+1));

        myTabbedPane.add(timer.getName(), timer.getTimerPanel());
        myTabbedPane.validate();

        getTimerList().add(timer);

        
    }

   public void addTimer(AfoTimer timer) {

        myTabbedPane.add(timer.getName(), timer.getTimerPanel());
        myTabbedPane.validate();

        getTimerList().add(timer);

    }

    public void deleteTimer(int timerIndex){
        myTabbedPane.remove(timerIndex+1);
        myTabbedPane.validate();

        timerList.remove(timerIndex);
    }

    /**
     * @return the myTabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return myTabbedPane;
    }

    /**
     * @param myTabbedPane the myTabbedPane to set
     */
    public void setTabbedPane(JTabbedPane myTabbedPane) {
        this.myTabbedPane = myTabbedPane;
    }

    /**
     * @return the timerList
     */
    public List<AfoTimer> getTimerList() {
        return timerList;
    }

    /**
     * @return the myDialog
     */
    public ProjectDialog getDialog() {
        return myDialog;
    }

    /**
     * @return the kernel
     */
    public Kernel getKernel() throws Exception {
        //Aggiungo un controllo perche' sembra che ogni tanto il kernel non si crei
        if (kernel == null){
            kernel = new Kernel(this, null);
        }
        
        return kernel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == myDialog.btnChangeIP){
                       //Avviso!!
            int res = JOptionPane.showConfirmDialog(null, "Attenzione: l'esecuzione del comando causerà il riavvio del sistema di controllo\n" +
                    "e la disconnessione dell'interfaccia.\nContinuare ?", "Info", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }

            //Check della validita' dell'indirizzo
            if (!FieldChecker.isValidIP(myDialog.txtIPAddress.getText())){
                JOptionPane.showMessageDialog(null, "Indirizzo IP non valido", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "SetIPAddress");
            com.putValue("IPADDRESS", myDialog.txtIPAddress.getText());

            kernel.sendCommand(com.toString());
            try {
                kernel.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "Ricordarsi di modificare le " +
                    "impostazioni di connessione per riaccedere al sistema di controllo", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
