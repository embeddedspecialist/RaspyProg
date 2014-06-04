/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import java.util.ArrayList;
import nodes.devices.GenericNode;
import org.netbeans.api.visual.action.ActionFactory;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class MyGraphPinScene extends AFOGraphPinScene {

    AFOSystemManager myManager;

    public MyGraphPinScene(AFOSystemManager manager){
        super();
        myManager = manager;


        getActions ().addAction (ActionFactory.createPanAction ());

    }

    public AFOSystemManager getManager(){
        return myManager;
    }

    public GenericNode getNodeByPinAddress(Integer address) {
        ArrayList<AFOPin> pinList = (ArrayList<AFOPin>) getPins();

        for (AFOPin pin : pinList){
            if (pin.pinAddress == address){
                return (GenericNode) pin.getParentNode();
            }
        }

        return null;
    }

}
