/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package nodes;

import java.util.Collection;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;
import javax.swing.JOptionPane;
import nodes.devices.CostantNode;
import nodes.devices.DeviceNode;
import nodes.devices.DiDoNode;
import nodes.devices.GenericNode;
import nodes.devices.MasterNode;
import nodes.devices.SaturationNode;
import nodes.devices.AfoTimer;
import nodes.devices.SubSystemIONode;
import nodes.devices.THUNode;
import org.netbeans.api.visual.widget.ConnectionWidget;
import sceneManager.AFONode;
import sceneManager.AFOPin;

/**
 * @author David Kaspar
 * Modified by Alessandro Mirri - February 2008
 */
public class SceneSerializer {

    private static final String PROJECT_ELEMENT = "Project";
    private static final String SCENE_ELEMENT = "Scene"; // NOI18N
    private static final String VERSION_ATTR = "version"; // NOI18N
    private static final String SCENE_NODE_COUNTER_ATTR = "nodeIDcounter"; // NOI18N
    private static final String SCENE_EDGE_COUNTER_ATTR = "edgeIDcounter"; // NOI18N

    private static final String SCENE_SUBSYSTEM_COUNTER_ATTR = "subSystemIDCounter";

    private static final String SCENE_COMMON_PROPERTIES = "CommonProperties";
    private static final String NODE_ELEMENT = "Node"; // NOI18N
    private static final String NODE_TYPE = "type";
    public static final String NODE_SUBTYPE = "subType";
    private static final String NODE_ID_ATTR = "id"; // NOI18N
    private static final String NODE_X_ATTR = "x"; // NOI18N
    private static final String NODE_Y_ATTR = "y"; // NOI18N
    private static final String NODE_SERIAL_NUMBER = "serialNumber";
    private static final String NODE_MAIN_VAL = "mainVal";
    private static final String NODE_SEC_VAL = "secVal";
    private static final String NODE_ADDRESS = "address";
    private static final String NODE_COMMENT = "comment";

    private static final String SUBSYSTEM_NAME = "subSystemName";
    private static final String SUBSYSTEM_ID = "subSystemID";

    private static final String EDGE_ELEMENT = "Edge"; // NOI18N
    private static final String EDGE_ID_ATTR = "id"; // NOI18N
    private static final String EDGE_SOURCE_NODE = "sourceNode";
    private static final String EDGE_SOURCE_PIN = "sourcePin"; // NOI18N
    private static final String EDGE_TARGET_NODE = "targetNode";
    private static final String EDGE_TARGET_PIN = "targetPin"; // NOI18N
    private static final String EDGE_CONTROLPOINT = "controlPoint"; // NOI18N
    private static final String VERSION_VALUE_1 = "1"; // NOI18N

    public static int subSystemNumber = 1;
    // call in AWT to serialize scene
    public static void serialize(Project project, MyGraphPinScene scene, File file, boolean isExportAction) {

        
        Document document = XMLUtil.createDocument(PROJECT_ELEMENT, null, null, null);
        Element sceneElement;

        try {

            //Se e' un sottositema NON serializzo i timer e il progetto
            if (!scene.getManager().isSubSystem()){
                //Serializzo i timer
                Element timerElement = document.createElement("Timers");
                setAttribute(document, timerElement, "nOfTimers", new Integer(project.getTimerList().size()).toString());
                for (int timer = 0; timer < project.getTimerList().size(); timer++) {
                    project.getTimerList().get(timer).serialize(document, timerElement);
                }

                document.getFirstChild().appendChild(timerElement);
                sceneElement = document.createElement(SCENE_ELEMENT);
                document.getFirstChild().appendChild(sceneElement);
                setAttribute(document, sceneElement, VERSION_ATTR, VERSION_VALUE_1);
                setAttribute(document, sceneElement, SCENE_NODE_COUNTER_ATTR, Long.toString(scene.nodeIDcounter));
                setAttribute(document, sceneElement, SCENE_EDGE_COUNTER_ATTR, Long.toString(scene.edgeIDcounter));
                setAttribute(document, sceneElement, SCENE_SUBSYSTEM_COUNTER_ATTR, Long.toString(project.getNextSubSystemID()));

                //Registro le propietà generali del sistema
                Element commonProperties = document.createElement(SCENE_COMMON_PROPERTIES);

                project.serialize(document, commonProperties);

                sceneElement.appendChild(commonProperties);
            }
            else {
                sceneElement = document.createElement(SCENE_ELEMENT);
                document.getFirstChild().appendChild(sceneElement);
                setAttribute(document, sceneElement, VERSION_ATTR, VERSION_VALUE_1);
                setAttribute(document, sceneElement, SCENE_NODE_COUNTER_ATTR, Long.toString(scene.nodeIDcounter));
                setAttribute(document, sceneElement, SCENE_EDGE_COUNTER_ATTR, Long.toString(scene.edgeIDcounter));
                setAttribute(document, sceneElement, SCENE_SUBSYSTEM_COUNTER_ATTR, Long.toString(project.getNextSubSystemID()));
            }

            //Registro tutti i nodi, prima, però, li ordino in ordine di immissione
            Collection<AFONode> nodeList = scene.getNodes();
            Object sortedAddress[] = nodeList.toArray();
            Arrays.sort(sortedAddress);
            for (int i = 0; i < sortedAddress.length; i++) {

                AFONode node = (AFONode) sortedAddress[i];
                Element nodeElement = document.createElement(NODE_ELEMENT);
                setAttribute(document, nodeElement, NODE_ID_ATTR, node.getId());
                setAttribute(document, nodeElement, NODE_TYPE, node.getBlockType().toString());
                setAttribute(document, nodeElement, NODE_COMMENT, node.getComment());

                if (node instanceof MasterNode) {
                    nodeElement = node.serializeNode(document, nodeElement);
                } else if (node instanceof DeviceNode) {
                    setAttribute(document, nodeElement, NODE_SERIAL_NUMBER, ((DeviceNode) node).getSerialNumber());
                    setAttribute(document, nodeElement, NODE_ADDRESS, ((GenericNode) node).getAddressList().get(0).toString());

                    if (node instanceof DiDoNode) {
                        ((DiDoNode) node).serializeNode(document, nodeElement);
                    }
                    else if (node instanceof THUNode){
                        ((THUNode)node).serializeNode(document, nodeElement);
                    }
                } else if (node instanceof SubSystemIONode) {
                    AFOPin parentPin = ((SubSystemIONode) node).getParentPin();

                    AFOSystemManager manager = scene.getManager();

                    Integer parentPinIdx = manager.getPinList().indexOf(parentPin);
                    setAttribute(document, nodeElement, "parentPINIdx", parentPinIdx.toString());
                } else if (node.getBlockType().equals(CommonDefinitions.blockTypes.Subsystem)) {

//                    String subSystemFileName;
//                    String subSystemName = "SubSystem" + subSystemNumber;
//                    subSystemFileName = project.getProjectFolder() + subSystemName;
//                    setAttribute(document, nodeElement, SUBSYSTEM_NAME, subSystemName);
//                    subSystemNumber++;

                    String subSystemFileName;
                    String subSystemName = "SubSystem" + subSystemNumber;
                    //20/12/2010
                    //subSystemFileName = project.getProjectFolder() + subSystemName;
                    int nPOS = file.getAbsolutePath().lastIndexOf(File.separatorChar)+1;
                    subSystemFileName = file.getAbsolutePath().substring(0, nPOS) + subSystemName;
                    setAttribute(document, nodeElement, SUBSYSTEM_NAME, subSystemName);
                    subSystemNumber++;

                    //Salvo ricorsivamente il sottosistema
//                    if (!node.getComment().equals(CommonDefinitions.DEFAULT_NODE_COMMENT)) {
//                        subSystemFileName = project.getProjectFolder() + node.getComment();
//                        setAttribute(document, nodeElement, SUBSYSTEM_NAME, node.getComment());
//                    } else {
//                        subSystemFileName = project.getProjectFolder() + "SubSystem" + subSystemNumber;
//                        setAttribute(document, nodeElement, SUBSYSTEM_NAME, "SubSystem" + subSystemNumber);
//                        subSystemNumber++;
//                    }


                    subSystemFileName += CommonDefinitions.SUBSYST_DEFAULT_EXTENSION;
                    File subSystemFile = new File(subSystemFileName);

                    //Controllo se il sottosistema ha cambiato nome e nel caso cancello il file
                    //Devo cercare quale sottosistema è e poi creare la Dialog
                    AFOSystemManager subSystemManager = null;
                    for (int subSystemIndex = 0; subSystemIndex < project.getSubSystemList().size(); subSystemIndex++) {
                        if (project.getSubSystemList().get(subSystemIndex).getParentNode() == node) {
                            subSystemManager = project.getSubSystemList().get(subSystemIndex);
                            break;
                        }
                    }

                    if (subSystemManager == null) {
                        JOptionPane.showMessageDialog(null, "Si è verificato un errore nel salvataggio del sottosistema:\n"
                                + "Impossibile trovare il sottosistema di appartenenza");
                        return;
                    }

                    if (!isExportAction){
                        File oldFile = new File(subSystemManager.fileName);

                        try {

                            if (oldFile.exists()) {
                                oldFile.delete();
                            }

                            if (!subSystemFile.exists()) {
                                subSystemFile.createNewFile();
                            } else {
                                subSystemFile.delete();
                                subSystemFile.createNewFile();
                            }

                        } catch (IOException iOException) {
                            JOptionPane.showMessageDialog(null, "Errore in cancellazione file");
                            return;
                        }
                    }

                    setAttribute(document, nodeElement, SUBSYSTEM_ID, subSystemManager.getSubSystemID());
                    setAttribute(document, nodeElement, "frameWidth",
                            new Integer(subSystemManager.getForm().getPreferredSize().width).toString());
                    setAttribute(document, nodeElement, "frameHeight",
                            new Integer(subSystemManager.getForm().getPreferredSize().height).toString());
                    setAttribute(document, nodeElement, "frameX",
                            new Integer(subSystemManager.getForm().getLocation().x).toString());
                    setAttribute(document, nodeElement, "frameY",
                            new Integer(subSystemManager.getForm().getLocation().y).toString());
                    //Mi salvo anche il nome del file per poterlo cambiare successivamente
                    subSystemManager.fileName = subSystemFileName;
                    SceneSerializer.serialize(project, (MyGraphPinScene) subSystemManager.getSubSystemScene(), subSystemFile, isExportAction);

                } else if (node.getBlockType().ordinal() >= CommonDefinitions.blockTypes.IF.ordinal()) {
                    
                    //E' un block
                    //Alcuni block non hanno indirizzo
                    if ( (((GenericNode) node).getAddressList() != null ) &&
                         (((GenericNode) node).getAddressList().size() > 0 )
                    ){
                        setAttribute(document, nodeElement, NODE_ADDRESS, ((GenericNode) node).getAddressList().get(0).toString());
                    }
                    
                    switch (node.getBlockType()) {
                        case IF:
                        case Arithmetic:
                        case Logic:
                        case Gate:
                        case Mux:{
                            setAttribute(document, nodeElement, NODE_SUBTYPE, node.getSubType().toString());
                            break;
                        }
                        case Costant: {
                            setAttribute(document, nodeElement, NODE_SUBTYPE, node.getBlockType().toString());
                            setAttribute(document, nodeElement, NODE_MAIN_VAL, ((CostantNode) node).getValue());
                        }
                        ;
                        break;
                        case Hysteresys: {
                            try {
                                nodeElement = node.serializeNode(document, nodeElement);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //continue;
                            }
                            break;
                        }
                        case Saturation: {
                            setAttribute(document, nodeElement, NODE_SUBTYPE, node.getBlockType().toString());
                            setAttribute(document, nodeElement, NODE_MAIN_VAL, ((SaturationNode) node).getUpperLimit());
                            setAttribute(document, nodeElement, NODE_SEC_VAL, ((SaturationNode) node).getLowerLimit());
                            break;
                        }
                        default: {
                            try {
                                nodeElement = node.serializeNode(document, nodeElement);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //continue;
                            }
                        }
                    }

                }

                Widget widget = scene.findWidget(node);
                Point location = widget.getPreferredLocation();
                setAttribute(document, nodeElement, NODE_X_ATTR, Integer.toString(location.x));
                setAttribute(document, nodeElement, NODE_Y_ATTR, Integer.toString(location.y));
                sceneElement.appendChild(nodeElement);
            }

            //Registro tutti gli edge riordinandoli
            Collection<String> edgeList = scene.getEdges();
            Object[] sortedEdges = edgeList.toArray();
            Arrays.sort(sortedEdges);
            for (int i = 0; i < sortedEdges.length; i++) {
                String edge = (String) sortedEdges[i];
                Element edgeElement = document.createElement(EDGE_ELEMENT);
                setAttribute(document, edgeElement, EDGE_ID_ATTR, edge);
                AFOPin sourcePin = scene.getEdgeSource(edge);
                AFONode sourceNode = scene.getPinNode(sourcePin);
                if (sourceNode != null) {
                    setAttribute(document, edgeElement, EDGE_SOURCE_NODE, sourceNode.getId());
                    //Per identificare il pin salvo il numero dello stesso nella lista dei pin
                    //del nodo padre
                    setAttribute(document, edgeElement, EDGE_SOURCE_PIN, "" + sourceNode.getPinList().indexOf(sourcePin));

                    //Punti di controllo
                    ConnectionWidget conn = (ConnectionWidget) scene.findWidget(edge);
                    ListIterator<Point> iterator = conn.getControlPoints().listIterator();
                    while (iterator.hasNext()) {
                        Point ctrlPt = iterator.next();
                        Element controlEl = document.createElement(EDGE_CONTROLPOINT);
                        controlEl.setAttribute("x", new Integer(ctrlPt.x).toString());
                        controlEl.setAttribute("y", new Integer(ctrlPt.y).toString());
                        edgeElement.appendChild(controlEl);
                    }

                }


                AFOPin targetPin = scene.getEdgeTarget(edge);
                AFONode targetNode = scene.getPinNode(targetPin);
                if (targetNode != null) {
                    setAttribute(document, edgeElement, EDGE_TARGET_NODE, targetNode.getId());
                    setAttribute(document, edgeElement, EDGE_TARGET_PIN, "" + targetNode.getPinList().indexOf(targetPin));
                }

                sceneElement.appendChild(edgeElement);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Si è verificato un errore: " + ex.toString());
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            XMLUtil.write(document, fos, "UTF-8"); // NOI18N
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // call in AWT to deserialize scene

    public static void deserialize(Project project, MyGraphPinScene scene, File file, boolean doImport) throws Exception {
        //Devo separarle perche' la ricorsivita' della prima finisce per fare caricare piu' volte la seconda
        deserializeScene(project, scene, file, doImport);
        deserializeTimers(project, scene, file);
    }

    /**
     * Questa classe contiene i dati tremporanei relativi ai nodi IO
     */
    private static class IONodeTempData implements Comparable {

        public String id;
        public String[] names = new String[2];
        public Point position;
        public int pinIdx;

        @Override
        public int compareTo(Object o) {
            IONodeTempData temp = (IONodeTempData)o;

            if (this.pinIdx == temp.pinIdx){
                return 0;
            }
            else if (this.pinIdx > temp.pinIdx){
                return 1;
            }
            else {
                return -1;
            }
        }

    }

    public static void deserializeScene(Project project, MyGraphPinScene scene, File file, boolean doImport) throws Exception {

        ArrayList<IONodeTempData> ioList = new ArrayList<IONodeTempData>();

        try {
            AFOSystemManager systemManager = scene.getManager();

            Node rootNode = getRootNode(file);

            for (Node xmlNode : getChildNode(rootNode)) {

                //            Node xmlNode = rootNode.getChildNodes().item(nodeIdx);
                if (xmlNode.getNodeName().equals(SCENE_ELEMENT)) {

                    if (!VERSION_VALUE_1.equals(getAttributeValue(xmlNode, VERSION_ATTR))) {
                        return;
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////
                    scene.nodeIDcounter = Long.parseLong(getAttributeValue(xmlNode, SCENE_NODE_COUNTER_ATTR));
                    scene.edgeIDcounter = Long.parseLong(getAttributeValue(xmlNode, SCENE_EDGE_COUNTER_ATTR));

                    long nextID = Long.parseLong(getAttributeValue(xmlNode, SCENE_SUBSYSTEM_COUNTER_ATTR))+1;
                    if (nextID > project.getNextSubSystemID()){
                        project.setNextSubSystemID(nextID);
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////


                    for (Node element : getChildNode(xmlNode)) {
                        if (SCENE_COMMON_PROPERTIES.equals(element.getNodeName())) {
                            project.deserialize(element);

                        } else if (NODE_ELEMENT.equals(element.getNodeName())) {
                            String nodeId = getAttributeValue(element, NODE_ID_ATTR);
                            String nodeType = getAttributeValue(element, NODE_TYPE);
                            String nodeSubType = getAttributeValue(element, NODE_SUBTYPE);

                            if (nodeSubType == null) {
                                nodeSubType = CommonDefinitions.subBlockStrings[CommonDefinitions.subBlockTypes.NONE.ordinal()];
                            }
                            int x = Integer.parseInt(getAttributeValue(element, NODE_X_ATTR));
                            int y = Integer.parseInt(getAttributeValue(element, NODE_Y_ATTR));

                            if (nodeType.equals(CommonDefinitions.blockTypes.IN.toString())
                                    || nodeType.equals(CommonDefinitions.blockTypes.OUT.toString())) {

                                IONodeTempData data = new IONodeTempData();
                                data.id = getAttributeValue(element, NODE_ID_ATTR);

                                data.names[0] = getAttributeValue(element, NODE_TYPE);
                                data.names[1] = getAttributeValue(element, NODE_COMMENT);

                                data.position = new Point(x, y);
                                data.pinIdx = Integer.parseInt(getAttributeValue(element, "parentPINIdx"));

                                ioList.add(data);


                            } else if (nodeType.equals(CommonDefinitions.blockTypes.Subsystem.toString())) {

                                //Se è un sottosistema devo creare un nuovo system manager e via così
                                String subSystemID = getAttributeValue(element, SUBSYSTEM_ID);

                                if (systemManager.isSubSystem()){
                                    project.addSubSystem(systemManager,new Point(x, y), nodeId);
                                }
                                else {
                                    project.addSubSystem(new Point(x, y), nodeId);
                                }

                                int lastNodeIdx = systemManager.getNodeListSize() - 1;
                                AFONode node = systemManager.getNodeList().get(lastNodeIdx);
                                node.setComment(getAttributeValue(element, NODE_COMMENT));
                                scene.validate();

                                int subSystemSize = project.getSubSystemList().size();

                                //Quello che ho appena creato è l'ultimo della lista
                                AFOSystemManager newSystemManager = project.getSubSystemList().get(subSystemSize - 1);

                                //Ricarico ricorsivamente la scena
                                String subSystemFileName = project.getProjectFolder() + getAttributeValue(element, SUBSYSTEM_NAME) + CommonDefinitions.SUBSYST_DEFAULT_EXTENSION;
                                newSystemManager.fileName = subSystemFileName;
                                File subSystemFile = new File(subSystemFileName);
                                deserializeScene(project, newSystemManager.getSubSystemScene(), subSystemFile, doImport);
                                int frameX, frameY, width, height;
                                frameX = Integer.parseInt(getAttributeValue(element, "frameX"));
                                frameY = Integer.parseInt(getAttributeValue(element, "frameY"));

                                width = Integer.parseInt(getAttributeValue(element, "frameWidth"));
                                height = Integer.parseInt(getAttributeValue(element, "frameHeight"));
                                newSystemManager.getForm().setSize(new Dimension(width, height));
                                newSystemManager.getForm().setLocation(frameX, frameY);

                            } else {
                                String[] elementTypes = new String[2];
                                elementTypes[0] = nodeType;
                                elementTypes[1] = nodeSubType;
                                Integer address;

                                try {
                                    address = Integer.parseInt(getAttributeValue(element, NODE_ADDRESS));
                                } catch (Exception ex) {
                                    address = Integer.MAX_VALUE;
                                }

                                if (doImport){
                                    //Nell'importazione devo ricreare gli indirizzi ex-novo
                                    systemManager.addElementToSystem(elementTypes, new Point(x, y), nodeId, null);
                                }
                                else {
                                    systemManager.addElementToSystem(elementTypes, new Point(x, y), nodeId, address);
                                }
                                

                                int lastNodeIdx = systemManager.getNodeListSize() - 1;
                                AFONode node = systemManager.getNodeList().get(lastNodeIdx);
                                node.setComment(getAttributeValue(element, NODE_COMMENT));

                                scene.validate();

                                if (node.getBlockType().ordinal() >= CommonDefinitions.blockTypes.IF.ordinal()) {
                                    switch (node.getBlockType()) {
                                        case Costant: {
                                            ((CostantNode) node).setValue(getAttributeValue(element, NODE_MAIN_VAL));
                                            ((AFONodeWidget) node.getWidget()).setSubType(getAttributeValue(element, NODE_MAIN_VAL));

                                        }
                                        ;
                                        break;
                                        case Hysteresys: {
                                            try {
                                                node.deserializeNode(element);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                //continue;
                                            }
                                            break;
                                        }
                                        case Saturation: {
                                            ((SaturationNode) node).setUpperLimit(getAttributeValue(element, NODE_MAIN_VAL));
                                            ((SaturationNode) node).setLowerLimit(getAttributeValue(element, NODE_SEC_VAL));
                                            ((AFONodeWidget)node.getWidget()).setSubType(getAttributeValue(element, NODE_SEC_VAL)+
                                                    "-"+getAttributeValue(element, NODE_MAIN_VAL));
                                            break;
                                        }
                                        default: {
                                            try {
                                                node.deserializeNode(element);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                //continue;
                                            }
                                            break;
                                        }
                                    }
                                } else if (node instanceof DeviceNode) {
                                    ((DeviceNode) node).setSerialNumber(getAttributeValue(element, NODE_SERIAL_NUMBER));
                                    if ((node instanceof DiDoNode) || (node instanceof THUNode)) {
                                        node.deserializeNode(element);
                                    }


                                } else if (node instanceof MasterNode) {
                                    node.deserializeNode(element);
                                }
                            }

                        }
                    }
                }
            }

            //Ora cosrtuisco i pin del widget del sottosistema
            if (ioList.size() > 0) {
                //Qui dovrei prima ordinarli
                Collections.sort(ioList);
                for (int i = 0; i < ioList.size(); i++) {
                    systemManager.addElementToSystem(ioList.get(i).names, ioList.get(i).position, ioList.get(i).id, null);
                    int lastNodeIdx = systemManager.getNodeListSize() - 1;
                    AFONode node = systemManager.getNodeList().get(lastNodeIdx);
                    node.setComment(ioList.get(i).names[1]);
                }
            }

            //Ora devo ricostruire gli edge
            for (Node xmlNode : getChildNode(rootNode)) {
                if (xmlNode.getNodeName().equals(SCENE_ELEMENT)) {

                    for (Node element : getChildNode(xmlNode)) {
                        if (EDGE_ELEMENT.equals(element.getNodeName())) {
                            String edge = getAttributeValue(element, EDGE_ID_ATTR);
                            AFONode sourceNode = null, targetNode = null;
                            String sourceNodeID = getAttributeValue(element, EDGE_SOURCE_NODE);
                            String targetNodeID = getAttributeValue(element, EDGE_TARGET_NODE);

                            for (int i = 0; i < systemManager.getNodeListSize(); i++) {
                                String tempNodeID = systemManager.getNodeList().get(i).getId();
                                if (tempNodeID.equals(sourceNodeID)) {
                                    sourceNode = systemManager.getNodeList().get(i);
                                    break;
                                }
                            }

                            for (int i = 0; i < systemManager.getNodeListSize(); i++) {
                                String tempNodeID = systemManager.getNodeList().get(i).getId();

                                if (tempNodeID.equals(targetNodeID)) {
                                    targetNode = systemManager.getNodeList().get(i);
                                    break;
                                }
                            }

                            if ((targetNode == null) || (sourceNode == null)) {
                                //Errore!!
                                return;
                            }

                            int sourcePinIdx = new Integer(getAttributeValue(element, EDGE_SOURCE_PIN)).intValue();
                            AFOPin sourcePin = sourceNode.getPinList().get(sourcePinIdx);

                            int targetPinIdx = new Integer(getAttributeValue(element, EDGE_TARGET_PIN)).intValue();
                            AFOPin targetPin = targetNode.getPinList().get(targetPinIdx);

                            if (sourcePin.isBusPin()) {
                                scene.drawEdgeColor = 1;
                            } else {
                                scene.drawEdgeColor = 0;
                            }
                            scene.addEdge(edge);
                            scene.setEdgeSource(edge, sourcePin);
                            scene.setEdgeTarget(edge, targetPin);

                            //Ora che l'ho aggiunto alla scena gli imposto i punti di controllo
                            ArrayList<Point> ctrlPointsList = new ArrayList<Point>();
                            for (Node controlNode : getChildNode(element)) {
                                if (controlNode.getNodeName().equalsIgnoreCase(EDGE_CONTROLPOINT)) {
                                    int x = Integer.parseInt(getAttributeValue(controlNode, "x"));
                                    int y = Integer.parseInt(getAttributeValue(controlNode, "y"));

                                    ctrlPointsList.add(new Point(x, y));
                                }

                            }

                            ConnectionWidget conn = (ConnectionWidget) scene.findWidget(edge);
                            conn.setControlPoints(ctrlPointsList, true);
                            conn.setRoutingPolicy(ConnectionWidget.RoutingPolicy.UPDATE_END_POINTS_ONLY);
                            scene.validate();
                        }
                    }
                }
            }
        }
        catch  (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static void deserializeTimers(Project project, MyGraphPinScene scene, File file) throws Exception {

        Node rootNode = getRootNode(file);

        for (Node xmlNode : getChildNode(rootNode)) {
            if (xmlNode.getNodeName().equals("Timers")) {
                //Deserializzo i timer
                for (Node element : getChildNode(xmlNode)) {
                    if (element.getNodeName().equalsIgnoreCase("timer")) {
                        String timerID = getAttributeValue(element, "id");
                        AfoTimer timer = new AfoTimer(Integer.parseInt(timerID), "Timer " + timerID);
                        //Creo il pannello in cui carichero' i dati
                        timer.getTimerPanel();
                        timer.deserialize(element);
                        project.addTimer(timer);
                    }
                }
            }
        }
    }

    public static void setAttribute(Document xml, Node node, String name, String value) {
        NamedNodeMap map = node.getAttributes();
        Attr attribute = xml.createAttribute(name);
        attribute.setValue(value);
        map.setNamedItem(attribute);
    }

    private static Node getRootNode(File file) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            Document doc = XMLUtil.parse(new InputSource(is), false, false, new ErrorHandler() {

                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw new SAXException(e);
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw new SAXException(e);
                }

                @Override
                public void warning(SAXParseException e) {
                    Exceptions.printStackTrace(e);
                }
            }, null);
            return doc.getFirstChild();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return null;
    }

    public static String getAttributeValue(Node node, String attr) {
        try {
            if (node != null) {
                NamedNodeMap map = node.getAttributes();
                if (map != null) {
                    node = map.getNamedItem(attr);
                    if (node != null) {
                        return node.getNodeValue();
                    }
                }
            }
        } catch (DOMException e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    public static Node[] getChildNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        Node[] nodes = new Node[childNodes != null ? childNodes.getLength() : 0];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = childNodes.item(i);
        }
        return nodes;
    }
}

