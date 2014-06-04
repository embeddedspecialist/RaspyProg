/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import nodes.AFONodeWidget;
import sceneManager.AFOGraphPinScene;

/**
 *
 * @author amirrix
 */
public class DeviceNode extends GenericNode {

    protected String serialNumber = "";

    public DeviceNode(String id, String label, AFOGraphPinScene scene){
        super(id, label, scene);
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber the serialNumber to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        ((AFONodeWidget)widget).setSubType(getSerialNumber());
    }

}
