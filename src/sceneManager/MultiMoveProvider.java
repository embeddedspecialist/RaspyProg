/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sceneManager;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
class MultiMoveProvider implements MoveProvider {

    private HashMap<Widget, Point> originals = new HashMap<Widget, Point>();
    private Point original;
    private AFOGraphPinScene scene;

    public MultiMoveProvider(AFOGraphPinScene scene) {
        this.scene = scene;
    }

    @Override
    public void movementStarted(Widget widget) {
        Object object = scene.findObject(widget);
        if (scene.isNode(object)) {
            for (Object o : scene.getSelectedObjects()) {
                if (scene.isNode(o)) {
                    Widget w = scene.findWidget(o);
                    if (w != null) {
                        originals.put(w, w.getPreferredLocation());
                    }
                }
            }
        } else {
            originals.put(widget, widget.getPreferredLocation());
        }
    }

    @Override
    public void movementFinished(Widget widget) {
        originals.clear();
        original = null;
    }

    @Override
    public Point getOriginalLocation(Widget widget) {
        original = widget.getPreferredLocation();
        return original;
    }

    @Override
    public void setNewLocation(Widget widget, Point location) {
        int dx = location.x - original.x;
        int dy = location.y - original.y;
        for (Map.Entry<Widget, Point> entry : originals.entrySet()) {
            Point point = entry.getValue();
            entry.getKey().setPreferredLocation(new Point(point.x + dx, point.y + dy));
        }
    }
}
