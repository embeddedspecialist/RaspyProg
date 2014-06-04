/*
 * Questa classe gestisce i nodi a livello di scena e fornisce i metodi convenienti
 * and open the template in the editor.
 */
package nodes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import nodes.CommonDefinitions.blockTypes;
import nodes.gui.SubSystemDlg;
import org.netbeans.api.visual.widget.Widget;
import sceneManager.AFONode;
import sceneManager.AFOPin;

import nodes.devices.ArithmeticNode;
import nodes.devices.CostantNode;
import nodes.devices.HysteresisNode;
import nodes.devices.IFNode;
import nodes.devices.LogicNode;
import nodes.devices.SaturationNode;
import nodes.devices.SubSystemIONode;
import nodes.devices.PIDNode;
import nodes.devices.AnalogNode;
import nodes.devices.BinaryDecoderNode;
import nodes.devices.BinaryEncoderNode;
import nodes.devices.C3PointCtrlNode;
import nodes.devices.ClimaticCurveNode;
import nodes.devices.ClockNode;
import nodes.devices.CommentNode;
import nodes.devices.TimerNode;
import nodes.devices.CounterNode;
import nodes.devices.DelayNode;
import nodes.devices.MasterNode;
import nodes.devices.DiDoNode;
import nodes.devices.GateNode;
import nodes.devices.GenericNode;
import nodes.devices.MuxNode;
import nodes.devices.ScopeNode;
import nodes.devices.THUNode;
import nodes.devices.TemperatureNode;
import nodes.devices.TriggerNode;
import org.openide.util.Exceptions;

/**
 *
 * @author amirrix
 */
public class AFOSystemManager extends GenericNode {

    //Questa scena contiene i widget e gli elementi del sottosistema
    //mentre la variabile scene di AFONode contiene la scena in cui e' inserito
    //il widget del sottosistema.
    private MyGraphPinScene subSystemScene;
    private List<AFONode> nodeList;
    private List<AFOSystemManager> subSystemsList;
    private boolean isSubSystem = false;
    private String subSystemID;
    //In caso di subSystem questo è il nodo a cui corrisponde sul grafico totale
    private AFONode parentNode = null;
    //In caso di subsystem questo è il widget associato al nodo
    private Widget parentWidget = null;
    //In caso di subsystem questo è il system padre
    private AFOSystemManager parentSystem = null;
    //IN caso di sottosistema
    public String fileName = "";
    private JComponent afoView;
    private SubSystemDlg myForm;
    private Project proj;

    /**
     * Costruttore del sistema principale. Questo sistema non appartiene a nessuna scena ma crea
     * la prima scena del progetto.
     * Per questo nodo particolare la "scene" è il contesto nel quale esiste il suo widget (null se e'
     * il system manager capostipite) mentre subSystemScene è il contesto dove esistono i widget comandati da lui
     */
    public AFOSystemManager(Project proj) {
        nodeList = new ArrayList<AFONode>();
        scene = null;
        subSystemScene = new MyGraphPinScene(this);
        afoView = subSystemScene.createView();
        isSubSystem = false;
        this.proj = proj;

        AFOScenePopup pop = new AFOScenePopup(proj, subSystemScene, this);
        WidgetPopupMenu wpop = new WidgetPopupMenu(subSystemScene, this);

        subSystemScene.setWidgetPopupMenuActiom(wpop);
        subSystemScene.setScenePopupMenuAction(pop);
        KeyEventLoggerAction keyEvent = new KeyEventLoggerAction(this);
        subSystemScene.getSelectProvider().setKeyAction(keyEvent);
        subSystemID = "MainSystem";
    }

    /**
     * Costruttore per i sottosistemi
     * @param id identificativo unico di sistema del widget
     * @param label testo che compare nel widget
     * @param scene scena in cui esiste il widget del sottosistema
     */
    public AFOSystemManager(Project proj, String id, String label, MyGraphPinScene scene) {
        super(id, label, scene);

        this.proj = proj;
        blockType = CommonDefinitions.blockTypes.Subsystem;
        nodeList = new ArrayList<AFONode>();
        subSystemScene = new MyGraphPinScene(this);
        afoView = subSystemScene.createView();
        isSubSystem = true;
        createWidget();
        createSubsystemWindow();
        subSystemID = "Subsystem" + id;

    }

    public int assignConfigNumbers(int startNumber) {
        Iterator<AFONode> nodeIt = nodeList.iterator();
        while (nodeIt.hasNext()) {
            AFONode nodeExamined = nodeIt.next();
            if ((!nodeExamined.getBlockType().equals(CommonDefinitions.blockTypes.IN)
                    && (!nodeExamined.getBlockType().equals(CommonDefinitions.blockTypes.OUT)))) {
                if (nodeExamined instanceof AFOSystemManager) {
                    startNumber = ((AFOSystemManager) nodeExamined).assignConfigNumbers(startNumber);
                } else {
                    if ((nodeExamined.getBlockType().equals(CommonDefinitions.blockTypes.Comment))
                            || (nodeExamined.getBlockType().equals(CommonDefinitions.blockTypes.Scope))) {
                        continue;
                    }
                    startNumber++;
                    ((GenericNode) nodeExamined).setIniFileBlockNumber(startNumber);
                }
            }
        }

        return startNumber;
    }

    public String getSubSystemID() {
        return subSystemID;
    }

    public void setSubSystemID(String subSystemID) {
        this.subSystemID = subSystemID;
    }

    public List<AFONode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<AFONode> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public MyGraphPinScene getScene() {
        return (MyGraphPinScene) scene;
    }

    public MyGraphPinScene getSubSystemScene() {
        return subSystemScene;
    }

    public JComponent getAfoView() {
        return afoView;
    }

    public AFONode getParentNode() {
        return parentNode;
    }

    public void setParentNode(AFONode node) {
        this.parentNode = node;
    }

    public Widget getParentWidget() {
        return parentWidget;
    }

    public void setParentWidget(Widget parentWidget) {
        this.parentWidget = parentWidget;
    }

    public void addNode(AFONode newNode) {

        nodeList.add(newNode);
    }

    public boolean removeNode(AFONode oldNode) {

        if (oldNode instanceof AFOSystemManager) {
            proj.getSubSystemList().remove((AFOSystemManager) oldNode);
        }
        return nodeList.remove(oldNode);
    }

    public void addPin(AFONode node, AFOPin newPin) {
        int nodeIndex = nodeList.indexOf(node);

        try {
            nodeList.get(nodeIndex).addPin(newPin);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("(AFOSM) - Argh!!! Non sono riuscito ad attaccare il pin all'elemento inserito");
        }
    }

    public int getNodeListSize() {
        return nodeList.size();
    }

    public int getPinListSize(AFONode node) {
        try {
            return nodeList.get(nodeList.indexOf(node)).getPinListSize();
        } catch (ArrayIndexOutOfBoundsException e) {
            //TODO da sistemare
            return -1;
        }

    }

    public AFONode getNodeAtIndex(int index) {
        try {
            return nodeList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public AFOPin getPinAtIndex(int index) {
        try {
            return pinList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean isSubSystem() {
        return isSubSystem;
    }

    public AFOSystemManager getParentSystem() {
        return parentSystem;
    }

    public void setParentSystem(AFOSystemManager parentSystem) {
        this.parentSystem = parentSystem;
        this.proj = parentSystem.getProject();
    }

    @Override
    public void showPropertiesDialog() throws Exception {
        getForm().setModal(false);
        getForm().setTitle(comment + " -- " + subSystemID);
        getForm().setVisible(true);
    }

    public void hidePropertiesDialog() {
        myForm.setVisible(false);
    }

    /**
     * Aggiunge al sistema un elemento
     * @param splittedEvent tipo e sottotipo elemento da aggiunere in forma di array di stringhe
     * @param point punto in cui aggiungerlo
     * @param elementID ID all'interno della scena, se null viene generato automaticamente
     * @param address indirizzo di partenza a cui allocare l'elemento, se null viene generato automaticamente
     */
    public void addElementToSystem(String[] splittedEvent, Point point, String elementID, Integer address) {

        GenericNode node = null;
        //Flag che mi serve per fare l'override della dialog per la creazione degli IN e OUT
        boolean inserting = false;

        if (elementID == null) {
            inserting = true;
            elementID = new String("node" + ++subSystemScene.nodeIDcounter);
        }

        //Menu add..

        if (splittedEvent[0].equals(CommonDefinitions.blockTypes.IN.toString())
                || splittedEvent[0].equals(CommonDefinitions.blockTypes.OUT.toString())) {

            int nOfIO;
            boolean isInput;
            int numIOint = 0;
            int numMax = 15;

            if (splittedEvent[0].equals(CommonDefinitions.blockTypes.IN.toString())) {
                isInput = true;
            } else {
                isInput = false;
            }


            //Se elementID non è nullo sto inserendo dei punti, se è null sto ricaricando
            if (inserting){
                String numIO = JOptionPane.showInputDialog(null, "Inserire il numero di blocchi che si vuole creare:") ;
                if (numIO == null){
                    return;
                }

                try {
                    numIOint = Integer.parseInt(numIO);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Inserire un numero!!");

                    //TODO uscire dalla procedura in caso di errore oppure ripetere la domanda
                }
            }
            else {
                numIOint = 1;
            }


            if (numIOint < numMax) {
                
                for (int i = 0; i < numIOint; i++){

                    nOfIO = getFreeIONumber(isInput);
                    if(inserting){
                        elementID = new String("node" + ++subSystemScene.nodeIDcounter);
                    }
                    node = new SubSystemIONode(elementID, splittedEvent[0], subSystemScene, nOfIO);
                    //addNode(node);
                    AFOPin pin = addIOPin(isInput, splittedEvent[0] + nOfIO);
                    ((SubSystemIONode) node).setParentPin(pin);
                    ((SubSystemIONode) node).setParentScene((MyGraphPinScene) getScene());
                    ((SubSystemIONode) node).setParentIOWidget(pin.getWidget());
                    ((SubSystemIONode) node).setParentIONameWidget(pin.getPinNameWidget());
                    //Questa dovrebbe essere uguale a getScene
                    getParentSystem().getSubSystemScene().validate();
                    //subSystemScene.getSceneAnimator().animatePreferredLocation(node.getWidget(),point);
                    //node.generateAddresses(getFreeAddress(node.getBlockType()));
                    try {
                        Point newPt = new Point();
                        newPt.setLocation(point.x, point.y+i*100);
                        subSystemScene.getSceneAnimator().animatePreferredLocation(node.getWidget(), newPt);
                    } catch (Exception e) {
                    }

                    if (address == null) {
                        node.generateAddresses(getFreeAddress(node.getBlockType()));
                    } else {
                        node.generateAddresses(address);
                    }
                    //Registro in tutti i pin il nodo a cui appartengono
                    for (AFOPin pins : node.getPinList()) {
                        pins.setParentNode(node);
                    }
                    ((GenericNode) node).setConnectionStatus(false);
                    //Resetto tutti i valori
                    node.resetPinValues();
                    addNode(node);
                }

                return;
            } else {
                JOptionPane.showMessageDialog(null, "Inserire un numero inferiore a 15.");
            }

        }
        if ((!splittedEvent[0].equals(CommonDefinitions.blockTypes.IN.toString())) || (!splittedEvent[0].equals(CommonDefinitions.blockTypes.IN.toString()))) {
            if (splittedEvent[0].equals("Master")) {
                node = new MasterNode(elementID, "Master", subSystemScene);
            } else if (splittedEvent[0].equals("PID")) {
                //creo un nuovo nodo
                node = new PIDNode(elementID, "PID", subSystemScene);

            } else if (splittedEvent[0].equals("Temp")) {
                node = new TemperatureNode(elementID, "TMP/C", subSystemScene);

            } else if ((splittedEvent[0].equals(CommonDefinitions.blockTypes.IOD4.toString()))
                    || (splittedEvent[0].equals(CommonDefinitions.blockTypes.DO8.toString()))
                    || (splittedEvent[0].equals(CommonDefinitions.blockTypes.DI8.toString()))) {
                node = new DiDoNode(elementID, splittedEvent[0], subSystemScene);

            } else if ((splittedEvent[0].equals(CommonDefinitions.blockTypes.AI.toString()))
                    || (splittedEvent[0].equals(CommonDefinitions.blockTypes.AO.toString()))) {
                node = new AnalogNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.THU.toString())) {
                node = new THUNode(elementID, splittedEvent[0], subSystemScene);
            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.IF.toString())) {

                node = new IFNode(elementID, splittedEvent[1], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Arithmetic.toString())) {

                node = new ArithmeticNode(elementID, splittedEvent[1], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Logic.toString())) {

                node = new LogicNode(elementID, splittedEvent[1], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Saturation.toString())) {

                node = new SaturationNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Hysteresys.toString())) {

                node = new HysteresisNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Costant.toString())) {

                node = new CostantNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.BinaryEncoder.toString())) {

                node = new BinaryEncoderNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.BinaryDecoder.toString())) {

                node = new BinaryDecoderNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Mux.toString())) {

                node = new MuxNode(elementID, splittedEvent[1], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.C3PointCtrl.toString())) {

                node = new C3PointCtrlNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Trigger.toString())) {

                node = new TriggerNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Timer.toString())) {

                node = new ClockNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Delay.toString())) {

                node = new DelayNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Counter.toString())) {

                node = new CounterNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Clock.toString())) {

                node = new TimerNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.ClimaticCurve.toString())) {

                node = new ClimaticCurveNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Comment.toString())) {

                node = new CommentNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Scope.toString())) {

                node = new ScopeNode(elementID, splittedEvent[0], subSystemScene);

            } else if (splittedEvent[0].equals(CommonDefinitions.blockTypes.Gate.toString())) {

                node = new GateNode(elementID, splittedEvent[0], subSystemScene);
            }

            subSystemScene.validate();
            try {
                subSystemScene.getSceneAnimator().animatePreferredLocation(node.getWidget(), point);
            } catch (Exception e) {
            }

            if (address == null) {
                node.generateAddresses(getFreeAddress(node.getBlockType()));
            } else {
                node.generateAddresses(address);
            }
            //Registro in tutti i pin il nodo a cui appartengono
            for (AFOPin pin : node.getPinList()) {
                pin.setParentNode(node);
            }
            ((GenericNode) node).setConnectionStatus(false);
            //Resetto tutti i valori
            node.resetPinValues();
            addNode(node);
            //}
        }
    }

    public void deleteElementFromSystem(Widget element) {

        //Distinguo se è un nodo oppure un connettore
        if (subSystemScene.isEdge(subSystemScene.findObject(element))) {
            subSystemScene.removeEdge((String) subSystemScene.findObject(element));
        } else {
            AFONode oldNode = (AFONode) subSystemScene.findObject(element);

            Widget moduleNameWidget;
            AFOPin oldNodePin;

            removeNode(oldNode);

            subSystemScene.removeNodeWithEdges(oldNode);
            subSystemScene.validate();

            //Controllo se è un sottosistema e se è un ingresso o un'uscita
            //TODO se e' un sottosistema deve essere rimosso anche dal progetto!!!
            if (isSubSystem() && (oldNode instanceof SubSystemIONode)) {

                SubSystemIONode subSystemNode = (SubSystemIONode) oldNode;
                AFONodeWidget subSystemParentWidget = (AFONodeWidget) getWidget();

                if (oldNode.getBlockType() == CommonDefinitions.blockTypes.IN) {
                    //Rimuovo gli oggetti grafici
                    //Questa dovrebbe essere uguale a getScene
                    Object[] edgeList = getParentSystem().getSubSystemScene().findPinEdges(subSystemNode.getParentPin(), false, true).toArray();

                    if (edgeList.length > 0) {
                        //Al pin padre è attaccato al massimo 1 solo pin figlio
                        getParentSystem().getSubSystemScene().removeEdge((String) edgeList[0]);
                    }

                    subSystemNode.getParentIONameWidget().removeFromParent();
                    subSystemNode.getParentIOWidget().removeFromParent();

                } else {
                    //Rimuovo gli oggetti grafici
                    //Questa dovrebbe essere uguale a getScene
                    Object[] edgeList = getParentSystem().getSubSystemScene().findPinEdges(subSystemNode.getParentPin(), true, false).toArray();

                    //Ai pin di uscita possono essere collegati più componenti
                    for (int i = 0; i < edgeList.length; i++) {
                        getParentSystem().getSubSystemScene().removeEdge((String) edgeList[i]);
                    }

                    subSystemNode.getParentIONameWidget().removeFromParent();
                    subSystemNode.getParentIOWidget().removeFromParent();
                }


                //Lo devo rimuovere dalla lista dei pin
                pinList.remove(subSystemNode.getParentPin());

                getParentSystem().getSubSystemScene().validate();
            } else if (oldNode.getBlockType() == CommonDefinitions.blockTypes.ClimaticCurve) {
                proj.regenerateClimaticIDs();
            }

            getSubSystemScene().validate();
        }

    }

    /**
     * Cerca tra tutti i blocchi istanziati quanti sono di tipo IN o OUT
     * @param searchInputs il tipo di blocco da cercare
     * @return numero di blocchi trovati
     */
//    public int getNofIOWidgets(boolean searchInputs){
//        int retVal = 0;
//        blockTypes bt;
//
//        if (searchInputs){
//            bt = blockTypes.IN;
//        }
//        else {
//            bt = blockTypes.OUT;
//        }
//
//        for (int i = 0; i < nodeList.size(); i++){
//            if (nodeList.get(i).getBlockType() == bt){
//                retVal++;
//            }
//
//        }
//
//        return retVal;
//    }
    private void createWidget() {

        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget) widget).setName("SubSystem");
        setComment("SubSystem" + id);
        ((AFONodeWidget) widget).getModuleComment().getActions().addAction(editorAction);

        getScene().addNode(this);

    }

    public void createSubsystemWindow() {
        setForm(new SubSystemDlg(null, true));


        getForm().mainPanel.setLayout(new BorderLayout());
        getForm().setTitle(((AFONodeWidget) widget).getModuleComment().getLabel());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(afoView);

        AFOScenePopup pop = new AFOScenePopup(proj, subSystemScene, this);
        WidgetPopupMenu wpop = new WidgetPopupMenu(subSystemScene, this);

        subSystemScene.setWidgetPopupMenuActiom(wpop);
        subSystemScene.setScenePopupMenuAction(pop);
        KeyEventLoggerAction keyEvent = new KeyEventLoggerAction(this);
        subSystemScene.getSelectProvider().setKeyAction(keyEvent);

        getForm().mainPanel.add(scrollPane, BorderLayout.CENTER);
        getForm().mainPanel.validate();

    }

    public AFOPin addIOPin(boolean isInput, String label) {

        String pinLabel, pinID;
        AFOPin newPin;
        pinLabel = label;

        if (isInput) {
            pinID = "pin" + ++subSystemScene.pinIDCounter;
            newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_INPUT);
            newPin.setAsBusPin(false);
            newPin.setParentNode(this);
            newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
            pinList.add(newPin);
            getScene().addPin(this, newPin);
        } else {
            pinID = "pin" + ++subSystemScene.pinIDCounter;
            newPin = new AFOPin(pinID, pinLabel, AFOPin.E_PinType.PIN_OUTPUT);
            newPin.setAsBusPin(false);
            newPin.setParentNode(this);
            newPin.setWidget(((AFONodeWidget) widget).addPin(newPin));
            pinList.add(newPin);
            getScene().addPin(this, newPin);
        }

        return newPin;
    }

    /**
     * Cerca tra tutti i blocchi istanziati quanti sono di tipo IN o OUT
     * @param searchInputs il tipo di blocco da cercare
     * @return numero di blocchi trovati
     */
    public int getNofIOWidgets(boolean searchInputs) {
        int retVal = 0;
        blockTypes bt;

        if (searchInputs) {
            bt = blockTypes.IN;
        } else {
            bt = blockTypes.OUT;
        }

        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).getBlockType() == bt) {
                retVal++;
            }

        }

        return retVal;
    }

    public int getFreeIONumber(boolean input) {
        List<Integer> ioNodesNumbers = new ArrayList<Integer>();
        blockTypes bt;

        if (input) {
            bt = blockTypes.IN;
        } else {
            bt = blockTypes.OUT;
        }

        if (nodeList.size() == 0) {
            return 1;
        }

        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).getBlockType() == bt) {
                ioNodesNumbers.add(((SubSystemIONode) nodeList.get(i)).getIoIndex());
            }
        }

        Collections.sort(ioNodesNumbers);

        for (int i = 0; i < ioNodesNumbers.size(); i++) {
            if (ioNodesNumbers.get(i) != (i + 1)) {
                //Qui c'è un buco....
                return (i + 1);
            }
        }

        return ioNodesNumbers.size() + 1;
    }

    private Integer getFreeDeviceAddress(blockTypes blockType) {
        //Lista completa degli indirizzi allocati
        List<Integer> usedAddressList = new ArrayList<Integer>();

        //Lista degli indirizzi allocati in un singolo nodo
        List<Integer> nodeAddressList;

        Object sortedAddress[];
        Integer retVal = -1;
        boolean gapFound = false;
        int gapSize = 0;

        if (nodeList.isEmpty()) {
            return new Integer(1);
        }

        //Inizializzo il primo elemento con un indirizzo 0: in questo modo la lista
        //parte sempre da 0 (e i moduli partiranno da 1) e se cancello il primo elemento
        //posso inserirne degli altri
        usedAddressList.add(new Integer(0));

        //Carico tutto in un'unica lista
        for (int i = 0; i < nodeList.size(); i++) {
            nodeAddressList = ((GenericNode) nodeList.get(i)).getAddressList();

            for (int j = 0; j < nodeAddressList.size(); j++) {
                usedAddressList.add(nodeAddressList.get(j));
            }
        }

        if (usedAddressList.isEmpty()) {
            //Sono tutti master: non ho indirizzi validi
            return new Integer(1);
        }
        //Riordino la lista
        sortedAddress = usedAddressList.toArray();
        Arrays.sort(sortedAddress);

        switch (blockType) {
            case IOD4:
            case DI8:
            case DO8: {
                //8 Indirizzi
                gapSize = 8;
                break;
            }
            case Temp:
            case AI:
            case AO: {
                //1 indirizzo
                gapSize = 1;
                break;
            }
            case THU: {
                gapSize = 3;
            }
            ;
            break;
            default: {
                //Forse va tutto qui perchè a parte qualcuno gli altri hanno 1
                //indirizzo solo
                gapSize = Integer.MAX_VALUE;
                break;
            }
        }

        //Controllo se tra un elemento ed il successivo c'è un gap e se c'è quanto è grande
        for (int i = 0; i < sortedAddress.length - 1; i++) {
            if ((((Integer) sortedAddress[i + 1]).intValue() - ((Integer) sortedAddress[i]).intValue()) > gapSize) {
                retVal = (Integer) sortedAddress[i] + 1;
                gapFound = true;
                break;
            }
        }

        //Non ho trovato un gap abbastanza grande, prendo il numero maggiore
        if (!gapFound) {
            retVal = (Integer) sortedAddress[sortedAddress.length - 1] + 1;
        }

        return retVal;
    }

    /**
     * Cicla tra tutti i sottosistemi creati per trovare degli indirizzi liberi
     * @param blockType
     * @return intero che rappresenta un indirizzo o Integer.MAX_VAL
     */
    private Integer getFreeSubsystemBlockAddress(blockTypes blockType) {
        //Lista completa degli indirizzi allocati
        List<Integer> usedAddressList = new ArrayList<Integer>();

        //Lista degli indirizzi allocati in un singolo nodo
        List<Integer> nodeAddressList;
        //Lista dei nodi di un sottosistema
        List<AFONode> subSystemNodes;

        Object sortedAddress[];
        Integer retVal = -1;
        boolean gapFound = false;
        int gapSize = 0;

        //Costruisco il vettore con tutti gli indirizzi gia' assegnati
        //I blocchi partono dall'indirizzo 10000
        //Inizializzo il primo elemento con un indirizzo 9999: in questo modo la lista
        //parte sempre da 9999 (e i moduli partiranno da 10000) e se cancello il primo elemento
        //posso inserirne degli altri
        usedAddressList.add(new Integer(9999));
        for (AFOSystemManager subSystem : proj.getSubSystemList()) {
            if (subSystem.nodeList.isEmpty()) {
                //Non ci sono elementi, continuo
                continue;
            }

            //Carico tutto in un'unica lista
            for (int i = 0; i < subSystem.nodeList.size(); i++) {
                nodeAddressList = ((GenericNode) subSystem.nodeList.get(i)).getAddressList();

                for (int j = 0; j < nodeAddressList.size(); j++) {
                    usedAddressList.add(nodeAddressList.get(j));
                }
            }

        }

        if (usedAddressList.isEmpty()) {
            //Non ho ancora usato degli indirizzi
            return new Integer(10000);
        }

        //Riordino la lista
        sortedAddress = usedAddressList.toArray();
        Arrays.sort(sortedAddress);

        //Direi che in tutti i blocchi dei sottosistemi occupano 1 indirizzo
        //ma lo tengo come variabile perchè non si sa mai
        gapSize = 1;

        //Controllo se tra un elemento ed il successivo c'è un gap e se c'è quanto è grande
        for (int i = 0; i < sortedAddress.length - 1; i++) {
            if ((((Integer) sortedAddress[i + 1]).intValue() - ((Integer) sortedAddress[i]).intValue()) > gapSize) {
                retVal = (Integer) sortedAddress[i] + 1;
                gapFound = true;
                break;
            }
        }

        //Non ho trovato un gap abbastanza grande, prendo il numero maggiore
        if (!gapFound) {
            retVal = (Integer) sortedAddress[sortedAddress.length - 1] + 1;
        }

        return retVal;
    }

    /**
     * Ricerca tra tutti i nodi creati qual'è il primo indirizzo disponibile per
     * assegnarlo ad un nodo di un certo tipo. In altre parole se il blocco
     * ha bisogno di 8 indirizzi cerca nell'insieme degli indirizzi uno slot da 8 posti
     * @param blockType tipo di blocco
     * @return indirizzo disponibile
     */
    public Integer getFreeAddress(blockTypes blockType) {

        //Se sonoun sottosistema cerco un indirizzo libero per un device
        if (isSubSystem) {
            return getFreeSubsystemBlockAddress(blockType);
        } else {
            return getFreeDeviceAddress(blockType);
        }
    }

    /**
     * @return the proj
     */
    public Project getProject() {
        return proj;
    }

    /**
     * @return the myForm
     */
    public SubSystemDlg getForm() {
        return myForm;
    }

    /**
     * @param myForm the myForm to set
     */
    public void setForm(SubSystemDlg myForm) {
        this.myForm = myForm;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public int createIniBlocks(int nOfBlocks, FileWriter stream) throws IOException {
        Iterator<AFONode> nodeIt = nodeList.iterator();
        boolean subSystemNotEmpty = false;

        while (nodeIt.hasNext()) {
            String blockStr = "";
            GenericNode node = (GenericNode) nodeIt.next();

            if ((!node.getBlockType().equals(CommonDefinitions.blockTypes.IN)
                    && (!node.getBlockType().equals(CommonDefinitions.blockTypes.OUT)))) {
                subSystemNotEmpty = true;

                if ((node instanceof ScopeNode) || (node instanceof CommentNode)) {
                    //Non finiscono nel file INI
                    continue;
                }
                if (node instanceof AFOSystemManager) {
                    nOfBlocks = ((AFOSystemManager) node).createIniBlocks(nOfBlocks, stream);
                } else {
                    String iniStr = "";
                    nOfBlocks++;
                    iniStr += "Block";
                    if (nOfBlocks < 10) {
                        iniStr += "0";
                    }
                    iniStr += nOfBlocks + " = ";

                    //node.setIniFileBlockNumber(nOfBlocks);
                    try {
                        iniStr += node.getIniString(nOfBlocks);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    iniStr += "\r\n";

                    stream.write(iniStr);

                }
            }
        }

        if (!subSystemNotEmpty) {
            JOptionPane.showMessageDialog(null,
                    "Il sottosistema " + comment + " NON contiene alcuna logica",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }

        return nOfBlocks;
    }

    /**
     * Trova nel sottosistema il nodo di IO che corrisponde al pin dato
     * @param pin
     * @return
     */
    public SubSystemIONode getPinChildNode(AFOPin pin) {

        for (AFONode node : nodeList) {
            if (node instanceof SubSystemIONode) {
                if (((SubSystemIONode) node).getParentPin().equals(pin)) {
                    return ((SubSystemIONode) node);
                }
            }
        }

        return null;
    }

    void importSubsystem() {
        try {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("AFO Subsystems", CommonDefinitions.SUBSYST_DEFAULT_EXTENSION.substring(1));

            chooser.setDialogTitle("Open subsystem");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(filter);
            chooser.setCurrentDirectory(new File(proj.getProjectFolder()));
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                File openFile = chooser.getSelectedFile();

                //Se è un sottosistema devo creare un nuovo system manager e via così
                String importedSubsystemID = chooser.getName(openFile);
                int sepIdx = importedSubsystemID.lastIndexOf(".sub");

                if (sepIdx > 0) {
                    importedSubsystemID = importedSubsystemID.substring(0, sepIdx);
                }

                proj.addSubSystem(this, new Point(0, 0), null);

                int lastNodeIdx = nodeList.size() - 1;
                AFONode node = nodeList.get(lastNodeIdx);

                //Qui devo controllare se esiste già con quel nome e aggiungere un suffisso (1,2,3...)
//                int idNum = 1;
//                while (existsID(importedSubsystemID)){
//                    importedSubsystemID+="_"+idNum;
//                }

                node.setComment(importedSubsystemID);

                int subSystemSize = proj.getSubSystemList().size();

                //Quello che ho appena creato è l'ultimo della lista
                AFOSystemManager newSystemManager = proj.getSubSystemList().get(subSystemSize - 1);

                //Ricarico ricorsivamente la scena
                SceneSerializer.deserializeScene(proj, newSystemManager.getSubSystemScene(), openFile, true);

                newSystemManager.getForm().setSize(new Dimension(250, 250));
                newSystemManager.getForm().setLocation(0, 0);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore:\n" + ex.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existsID(String importedSubsystemID) {
        List<AFOSystemManager> subSysList = proj.getSubSystemList();
        for (AFOSystemManager subSys : subSysList) {
            if (subSys.getComment().equals(importedSubsystemID)) {
                return true;
            }
        }

        return false;
    }
}
