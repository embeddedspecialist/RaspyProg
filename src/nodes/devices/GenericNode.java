/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import connection.Kernel;
import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import nodes.AFOSystemManager;
import nodes.CommonDefinitions;
import nodes.MyGraphPinScene;
import org.netbeans.api.visual.action.WidgetAction;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFONode;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class GenericNode extends AFONode {

    //Lista degli indirizzi del componente: se driver sono I/O, se blocco logico contiene
    //solo il suo indirizzo
    protected ArrayList<Integer> addressList;

    //Indica quanti "slot" (ovvero numeri) del file di configurazione sono impegnati da questo dispositivo
    //Default 1
    protected int configSlotsUsed = 1;

    protected static final String deviceConfigFormat ="Device%02d=";

    private int iniFileBlockNumber;

    protected static final int MAX_NUM_INPUT_PIN = 32;
    protected static final int MAX_NUM_OUTPUT_PIN = 32;

    protected Kernel kernel;

    public GenericNode(){

    }

    public GenericNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);
        addressList = new ArrayList<Integer>();
    }

    /**
     * Consente al dispositivo di aggiungere le proprie stringhe al file ini
     * @throws Exception
     */
    public String getIniString(int deviceIndex) throws Exception{
        throw new Exception("Impossibile istanziare classe base");
    }

    public boolean parseCmd(Cmd com){
        return false;
    }
    
    public void resetPinValues(){
        for (AFOPin checkedPin : pinList ){
            if ((!checkedPin.isBusPin()) &&
                (!(checkedPin.getParentNode() instanceof SubSystemIONode))
            ){
                setPinValue(checkedPin.getPinIDString(), "-100.0");
            }
        }
    }

    protected boolean parseStandardBlockCmd(Cmd com){
        try {
            //Leggo se il messaggio e' per me
           if (UtilXML.cmpCmdValue(com, "ADDRESS", addressList.get(0))) {
               //Parso tutti gli ingressi
               for (int i = 0; i < MAX_NUM_INPUT_PIN; i++){
                   String input = "IN"+(i+1);
                   if (!com.containsAttribute(input)){
                       break;
                   }

                   String tempVal = com.getValue(input);
                   setPinValue(input, tempVal);
               }

               for (int i = 0; i < MAX_NUM_OUTPUT_PIN; i++){
                   String output = "OUT"+(i+1);
                   if (!com.containsAttribute(output)){
                       break;
                   }

                   String tempVal = com.getValue(output);
                   setPinValue(output, tempVal);
                   setTargetPinValue(output, tempVal);
                   sendValueToChart(output,tempVal);
               }

                getScene().validate();
               return true;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Si e' verificato un errore nell'interpretazione dei messaqgi",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public boolean parseBlockCmd(Cmd com){
        if (blockType.equals(CommonDefinitions.blockTypes.Subsystem) ||
            blockType.equals(CommonDefinitions.blockTypes.IN) ||
            blockType.equals(CommonDefinitions.blockTypes.OUT) ||
            blockType.equals(CommonDefinitions.blockTypes.Comment) ||
            blockType.equals(CommonDefinitions.blockTypes.Scope)){
            return false;
        }

        return parseStandardBlockCmd(com);
    }

    protected AFOPin getPinByID(String pinIdx){
        for (AFOPin pin : pinList){
            if (pin.getPinIDString().equals(pinIdx)){

                return pin;
            }
        }

        return null;
    }

    public void setPinValue(String pinIdx, String value){
        try {
            getPinByID(pinIdx).setValue(value);
            getScene().validate();
        }
        catch (Exception ex){
            
        }
    }

    /**
     * @return the addressList
     */
    public List<Integer> getAddressList() {
        return addressList;
    }

    public void generateAddresses(Integer startAddress) {
        int addressSpace = 0;

        switch (blockType){
            case IOD4:
            case DI8:
            case DO8:{
                //8 Indirizzi
                addressSpace = 8;
                break;
            }
            case Temp:
            case AI:
            case AO:{
                //1 indirizzo
                addressSpace = 1;
                break;
            }
//            case THU:{
//                addressSpace = 1;
//                break;
//            }
            case Comment:{
                addressSpace = 0;
                break;
            }
            default:{
                //Forse va tutto qui perchè a parte qualcuno gli altri hanno 1
                //indirizzo solo
                addressSpace = 1;
                break;
            }
        }

        try{
            for (int i = 0; i < addressSpace; i++)
            {
                addressList.add(new Integer(startAddress.intValue()+i));
                pinList.get(i).pinAddress = startAddress.intValue()+i;
            }

            //Se ho altri PIN li battezzo uguali all'ultimo
            if (pinList.size() > addressSpace+1) {
                for (int i = addressSpace; i < pinList.size(); i++){
                    if (!pinList.get(i).isBusPin()){
                        pinList.get(i).pinAddress = pinList.get(addressSpace - 1).pinAddress;
                    }
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            addressList.add(new Integer(0));
        }
    }

    /**
     * @return the configSlotsUsed
     */
    public int getConfigSlotsUsed() {
        return configSlotsUsed;
    }

    protected String boolToIntString(boolean val){
        if (val){
            return "1";
        }
        else
        {
            return "0";
        }
    }

    protected boolean intStringToBool(String val){
        if (val.equalsIgnoreCase("1")){
            return true;
        }
        else {
            return false;
        }
    }
    
    protected Integer boolToInteger(boolean val){
        Integer retVal;

        if (val){
            retVal = new Integer(1);
        }
        else {
            retVal = new Integer(0);
        }

        return retVal;
    }

    protected String getCommonData() {
        int inputNumber = 1;
        int outputNumber = 1;
        String retVal="";
        

        retVal+="ADDR:"+Integer.toString(addressList.get(0))+",";
        Iterator<AFOPin> pinIt = getPinList().iterator();

        while (pinIt.hasNext()) {

            AFOPin pinExamined = pinIt.next();
            //Controllo se c'è qualcosa attaccato
            if (getScene().findPinEdges(pinExamined, true, true).size() == 0) {

                //Se è un ingresso non collegato magari è facoltativo e lo devo marcare..
                if (pinExamined.getPinType() == AFOPin.E_PinType.PIN_INPUT) {
                    retVal += " INPUT" + inputNumber + ":N,";
                    inputNumber++;
                }
                else if (pinExamined.getPinType() == AFOPin.E_PinType.PIN_OUTPUT){
                    retVal += " OUTPUT" + outputNumber + ":Store,";
                    outputNumber++;
                }

                continue;
            }

            if (pinExamined.getPinType() == AFOPin.E_PinType.PIN_INPUT) {
                retVal+=getCommonInputData((MyGraphPinScene) scene,pinExamined,inputNumber);
                inputNumber++;
            } else {
                boolean outputAlreadyWritten = false;
                //E' un'uscita, controllo se tra tutte le cose collegate ci sono degli indirizzi
                //di uscita, altrimenti lo marco come Store
                Object[] edges = getScene().findPinEdges(pinExamined, true, false).toArray();

                if (edges.length == 0){
                    retVal += "OUTPUT"+outputNumber+":Store,";
                    outputNumber++;
                    outputAlreadyWritten = true;
                }
                else {
                    retVal += "OUTPUT" + outputNumber + ":";
                    for (int i = 0; i < edges.length; i++) {
                        AFOPin targetPin = getScene().getEdgeTarget(edges[i].toString());
                        if (targetPin != null) {
                            AFONode targetNode = getScene().getPinNode(targetPin);

                            if (targetNode.getBlockType().equals(CommonDefinitions.blockTypes.OUT)) {
                                List<Integer> outputAddresses = getIOAddress(false, (SubSystemIONode) targetNode);
                                if (!outputAddresses.isEmpty()){
                                    outputAlreadyWritten=true;
                                    for (int j = 0; j < outputAddresses.size(); j++) {
                                         retVal+="A" + outputAddresses.get(j).intValue();

                                         if (j < outputAddresses.size()-1){
                                             retVal+="-";
                                         }
                                    }

                                    if (i < edges.length - 1){
                                         retVal+="-";
                                     }
                                }
                            }
                        }
                    }//For
                    
                    if (!outputAlreadyWritten){
                        retVal+="Store,";
                    }
                    else {
                        retVal+=",";
                    }
                }

            }//IF uscita
        }

        return retVal;
    }

    public String getCommonInputData(MyGraphPinScene pinScene, AFOPin inputPin, int inputNumber){

        AFOPin sourcePin;
        GenericNode sourceNode;
        String retVal="";

        //E' un ingresso, devo vedere da dove viene
        Object[] edgesArray = pinScene.findPinEdges(inputPin, false, true).toArray();
        if (edgesArray.length == 0){
            //Strano... c'e' un qualche marone
            return "INPUT"+inputNumber+":ERROR IN CHAIN FOLLOWING,";
        }

        String edge = (String) edgesArray[0];


        sourcePin = pinScene.getEdgeSource(edge);
        sourceNode = (GenericNode)pinScene.getPinNode(sourcePin);
        if (sourceNode.getBlockType().equals(CommonDefinitions.blockTypes.IN)) {
            Object[] edges = ((SubSystemIONode)sourceNode).
                    getParentScene().
                    findPinEdges(((SubSystemIONode)sourceNode).getParentPin(), false, true).
                    toArray();

            if (edges.length == 0){
                //Alla fine non c'e' niente collegato
                retVal = " INPUT" + inputNumber + ":N,";
            }
            else {
                for (int i =0; i < edges.length; i++){
                    //E' un ingresso: prendo il nodo padre e riparto
                    AFOPin sourceParentPin = ((SubSystemIONode)sourceNode).getParentPin();
                    retVal = getCommonInputData(((SubSystemIONode)sourceNode).getParentScene(),sourceParentPin, inputNumber);
                }
            }
        } else if (sourceNode instanceof AFOSystemManager) {

            //Qui dovrei "entrare" nel sottosistema e ricominciare a seguire il filo....
            //Devo trovare il widget il cui padre
            SubSystemIONode ioNode = ((AFOSystemManager)sourceNode).getPinChildNode(sourcePin);

            try {
                //Sfrutto il fatto che gli IONode hanno 1 solo pin
                retVal=getCommonInputData((MyGraphPinScene) ioNode.getScene(),ioNode.getPinAt(0), inputNumber);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        else if (sourceNode.getBlockType().ordinal() >= CommonDefinitions.blockTypes.IF.ordinal()){
            //E' un altro blocco, TBI il discorso del canale di uscita (aggiungo 1 perche' afomonitor li
            //considera a base 1
            int pinIndex = sourceNode.getOutputPinChannel(sourcePin); //sourceNode.getPinList().indexOf(sourcePin)+1;
            retVal += " INPUT" + inputNumber + ":B" + sourceNode.iniFileBlockNumber +"-"+ pinIndex+",";
        }
        else if (sourceNode.getBlockType().ordinal() <= CommonDefinitions.blockTypes.THU.ordinal()){
            int pinIndex = sourceNode.getOutputPinChannel(sourcePin); //sourceNode.getPinList().indexOf(sourcePin)+1;
            //Per i DI/DO non vale la regola del PINperche' ogni PIN e' in realta' un dispositivo
            if ( (sourceNode.getBlockType() == CommonDefinitions.blockTypes.DI8)||
                 (sourceNode.getBlockType() == CommonDefinitions.blockTypes.DO8)||
                 (sourceNode.getBlockType() == CommonDefinitions.blockTypes.IOD4)
                 ) {
                retVal+=" INPUT"+inputNumber+":A"+sourcePin.pinAddress + "-1,";
            }
            else {
                retVal+=" INPUT"+inputNumber+":A"+sourcePin.pinAddress + "-" + pinIndex + ",";
            }
        }

        return retVal;
    }

    /**
     * Controlla nella lista dei PIN che numero ha il pin di uscita dato per scrivere il collegamento
     * nel file INI
     */
    private int getOutputPinChannel(AFOPin pin){
        int channelNo = 1;
        for (AFOPin checkPin : pinList){
            if (checkPin.equals(pin)){
                return channelNo;
            }
            else if (checkPin.getPinType() == AFOPin.E_PinType.PIN_OUTPUT){
                channelNo++;
            }
        }

        return 0;
    }

    private List<Integer> getIOAddress(boolean isInput, SubSystemIONode ioNode){
        List<Integer> retVal = new ArrayList<Integer>();

        try {
        Object[] edges = ioNode.getParentScene().findPinEdges(ioNode.getParentPin(), true, true).toArray();

        if (edges.length == 0){
            //Se non c'è niente collegato...
            //TBI da controllare e magari lanciare un'eccezione
            return retVal;
        }

        //Risalgo al nodo padre e vedo dove è collegato
        if (isInput){
            //Ciclo su tutti i pin collegati
            for (int i = 0; i < edges.length; i++){
                int pinNumber;
                AFOPin sourcePin = ioNode.getParentScene().getEdgeSource((String)edges[i]);

                AFONode sourceNode = (GenericNode)ioNode.getParentScene().getPinNode(sourcePin);
                if (sourceNode.getBlockType().equals(CommonDefinitions.blockTypes.IN)) {
                    //E' un ingresso: devo risalire all'indirizzo
                    if (getIOAddress(true, (SubSystemIONode) sourceNode).size() > 0) {
                        //Al PIN è collegato qualcosa
                        retVal.add(getIOAddress(true, (SubSystemIONode) sourceNode).get(0).intValue());
                    }
                    else {
                        //??
                    }
                } else {
                    retVal.add(new Integer(sourcePin.pinAddress));
                }
            }
        }
        else{
            //Ciclo su tutti i pin collegati
            for (int i = 0; i < edges.length; i++){
                int pinNumber;
                AFOPin targetPin = ioNode.getParentScene().getEdgeTarget((String)edges[i]);
                AFONode targetNode = (GenericNode)ioNode.getParentScene().getPinNode(targetPin);

                if (targetNode instanceof SubSystemIONode){
                    List<Integer> subAddresses = getIOAddress(false, (SubSystemIONode) targetNode);
                    for (Integer subAddr : subAddresses){
                        retVal.add(subAddr);
                    }
                    
                }
                else {
                    //Lo metto nella lista solo se e' un blocco controllore, non se e' un altro blocco
                    if (targetNode.getBlockType().ordinal() <= CommonDefinitions.blockTypes.THU.ordinal()){
                        retVal.add(new Integer(targetPin.pinAddress));
                    }
                }
            }

        }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return retVal;
    }

    /**
     * @return the iniFileBlockNumber
     */
    public int getIniFileBlockNumber() {
        return iniFileBlockNumber;
    }

    /**
     * @param iniFileBlockNumber the iniFileBlockNumber to set
     */
    public void setIniFileBlockNumber(int iniFileBlockNumber) {
        this.iniFileBlockNumber = iniFileBlockNumber;
    }

    public void addKeyEventManager(WidgetAction.Adapter action){
        widget.getActions().addAction(action);
    }

    protected void setTargetPinValue(AFOPin sourcePin, String value){

        try {
            //Potrei cercare se qui e' attaccato qualcosa tipo IONode
            Object[] edgeArray = getScene().findPinEdges(sourcePin, true, false).toArray();

            for (int i = 0; i < edgeArray.length; i++){
                AFOPin targetPin = getScene().getEdgeTarget(edgeArray[i].toString());
                if (targetPin != null){
                    if ( (targetPin.getParentNode() instanceof AFOSystemManager)){
                        targetPin.setValue(value);
                        //Ora controllo cosa c'è attaccato
                        SubSystemIONode ioNode = ((AFOSystemManager)targetPin.getParentNode()).getPinChildNode(targetPin);
                        ioNode.setPinValue("IN", value);

                        ioNode.setTargetPinValue(ioNode.getPinByID("IN"), value);

                    }
                    else if (targetPin.getParentNode() instanceof SubSystemIONode){
                        targetPin.setValue(value);
                        AFOPin parentPin = ((SubSystemIONode)targetPin.getParentNode()).getParentPin();
                        AFOSystemManager sm = (AFOSystemManager) parentPin.getParentNode();

                        parentPin.setValue(value);
                        sm.sendValueToChart(parentPin, value);

                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    protected void setTargetPinValue(String pinIdx, String value){
        for (AFOPin pin : pinList){
            if (pin.getPinIDString().equals(pinIdx)){

                setTargetPinValue(pin,value);
                break;
            }
        }

        getScene().validate();
    }

    /**
     * @param kernel the kernel to set
     */
    public void setKernel(Kernel kernel) {
        this.kernel = kernel;
    }

    protected Cmd generateBasicNodeCommand(){
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND","BlockCommand");
        com.putValue("ADDRESS", "100000");
        com.putValue("SUBADDR", addressList.get(0).toString());

        return com;
    }

    /*
     * Questa funzione viene usata per abilitare/disabilitare i pulsanti legati alla
     * connessione
     */
    public void setConnectionStatus(boolean isConnected){

    }

    protected void sendCommand(Cmd com){

        try {
            kernel.sendCommand(com.toString());
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'invio del comando:\n"+ex.toString());
            ex.printStackTrace();
        }
    }

    /**
     * Cerca tra tutti i nodi collegati al pin dato se c'e' uno scope e nel caso ci sia aggiorna il dato
     * @param val il valore da aggiungere
     */
    protected void sendValueToChart(String pinIdx, String val){
        try {
            AFOPin pin = getPinByID(pinIdx);

            List<AFONode> nodeList = scene.getNodesConnectedAtPin(pin);

            for (AFONode node : nodeList){
                if (node instanceof ScopeNode){
                    ((ScopeNode)node).addValue(val);
                }
            }
        }
        catch (Exception ex){
            
        }
    }

    protected void sendValueToChart(AFOPin pin, String val){
        try {

            List<AFONode> nodeList = scene.getNodesConnectedAtPin(pin);

            for (AFONode node : nodeList){
                if (node instanceof ScopeNode){
                    ((ScopeNode)node).addValue(val);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
