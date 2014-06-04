/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package raspyprog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author amirrix
 */
class AppOptions {
    
    public String lastOpenDir;
    private static String OPTIONS_FILE_NAME = "./config.xml";
    private static final String OPTIONS_ELEMENT = "Options"; // NOI18N
    private static final String VERSION_ATTR = "version"; // NOI18N
    private static final String VERSION_VALUE_1 = "1"; // NOI18N
    
    private static final String LAST_OPEN_PROJ="LastOpenProj";
    private static final String LAST_DIR_PATH="path";
    
    public void SaveOptions(){
        File file = null;
        
        try {
            file = new File (OPTIONS_FILE_NAME);
            
            if (file.exists()){
                file.delete();
            }
            
            file.createNewFile();
        }
        catch (Exception e){
            Exceptions.printStackTrace(e);
            return;
        }
            
        Document document = XMLUtil.createDocument (OPTIONS_ELEMENT, null, null, null);

        Node rootElement = document.getFirstChild ();
        setAttribute (document, rootElement, VERSION_ATTR, VERSION_VALUE_1);

        Node nodeElement = document.createElement(LAST_OPEN_PROJ);
        setAttribute(document, nodeElement, LAST_DIR_PATH, lastOpenDir);
        rootElement.appendChild(nodeElement);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream (file);
            XMLUtil.write (document, fos, "UTF-8"); // NOI18N
        } catch (Exception e) {
            Exceptions.printStackTrace (e);
        } finally {
            try {
                if (fos != null) {
                    fos.close ();
                }
            } catch (Exception e) {

            }
        }
    }
    
        // call in AWT to deserialize scene
    public void LoadOptions () {
        File file = null;
        
        lastOpenDir = "./";
        
        try{
            file = new File(OPTIONS_FILE_NAME);
            if (!file.exists()){
                return;
            }
        }
        catch (Exception e){
            Exceptions.printStackTrace(e);    
            return;
        }
        
        Node rootElement = getRootNode (file);
        if (! VERSION_VALUE_1.equals (getAttributeValue (rootElement, VERSION_ATTR)))
            return;
        
        for (Node element : getChildNode (rootElement)) {
            if (LAST_OPEN_PROJ.equals(element.getNodeName())){
                lastOpenDir = getAttributeValue(element, LAST_DIR_PATH);
            }
            
        }
    }

    
    private static void setAttribute (Document xml, Node node, String name, String value) {
        NamedNodeMap map = node.getAttributes ();
        Attr attribute = xml.createAttribute (name);
        attribute.setValue (value);
        map.setNamedItem (attribute);
    }

    private static Node getRootNode (File file) {
        FileInputStream is = null;
        try {
            is = new FileInputStream (file);
            Document doc = XMLUtil.parse (new InputSource (is), false, false, new ErrorHandler() {
                public void error (SAXParseException e) throws SAXException {
                    throw new SAXException (e);
                }

                public void fatalError (SAXParseException e) throws SAXException {
                    throw new SAXException (e);
                }

                public void warning (SAXParseException e) {
                    Exceptions.printStackTrace (e);
                }
            }, null);
            return doc.getFirstChild ();
        } catch (Exception e) {
            Exceptions.printStackTrace (e);
        } finally {
            try {
                if (is != null)
                    is.close ();
            } catch (IOException e) {
                Exceptions.printStackTrace (e);
            }
        }
        return null;
    }

    private static String getAttributeValue (Node node, String attr) {
        try {
            if (node != null) {
                NamedNodeMap map = node.getAttributes ();
                if (map != null) {
                    node = map.getNamedItem (attr);
                    if (node != null)
                        return node.getNodeValue ();
                }
            }
        } catch (DOMException e) {
            Exceptions.printStackTrace (e);
        }
        return null;
    }

    private static Node[] getChildNode (Node node) {
        NodeList childNodes = node.getChildNodes ();
        Node[] nodes = new Node[childNodes != null ? childNodes.getLength () : 0];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = childNodes.item (i);
        return nodes;
    }
    

}
