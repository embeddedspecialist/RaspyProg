/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import nodes.AFONodeWidget;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.visual.router.CollisionsCollector;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class AFOCollisionCollector implements CollisionsCollector{
    
    private AFOGraphPinScene scene;
    
    public AFOCollisionCollector(AFOGraphPinScene scene){
        this.scene = scene;
    }

    @Override
    public void collectCollisions(List<Rectangle> verticalCollisions, List<Rectangle> horizontalCollisions) {
        
        Iterator it = scene.getNodes().iterator();
        AFONode afoNode;
        Widget nodeWidget;
        Widget moduleWidget;
        Rectangle rec;
        Dimension dim;
        
        while (it.hasNext()) {
            Point point = new Point();
            
            afoNode = ((AFONode)it.next());
            nodeWidget = afoNode.getWidget();
            moduleWidget = afoNode.getOverallWidget();
            
            point.x = nodeWidget.getLocation().x + moduleWidget.getLocation().x;
            point.y = nodeWidget.getLocation().y + moduleWidget.getLocation().y;
                    
            dim = new Dimension(moduleWidget.getBounds().width, moduleWidget.getBounds().height);
            
            rec = new Rectangle(point,dim);
            horizontalCollisions.add(rec);
            verticalCollisions.add(rec);
        }
    }

}
