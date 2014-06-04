/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class KeyEventLoggerAction extends WidgetAction.Adapter {

    AFOSystemManager systemManager;

    public KeyEventLoggerAction(AFOSystemManager sm) {
        systemManager = sm;
    }

    @Override
    public State keyPressed(Widget widget, WidgetKeyEvent event) {
//        System.out.println("KeyPressed at ");
        return State.REJECTED;
    }

    @Override
    public State keyReleased(Widget widget, WidgetKeyEvent event) {
//        System.out.println("KeyReleased at ");
        return State.REJECTED;
    }

    @Override
    public State keyTyped(Widget widget, WidgetKeyEvent event) {

        //Tasto canc
        if (event.getKeyChar() == 127) {
            systemManager.deleteElementFromSystem(widget);
        }

        systemManager.getSubSystemScene().setFocusedObject(null);
        systemManager.getSubSystemScene().validate();
        return State.REJECTED;
    }
}
