/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Point;
import java.util.List;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;

/**
 *
 * @author amirrix
 */
public class AFORouter implements Router {
    
    AFOGraphPinScene scene;
    
    public AFORouter(AFOGraphPinScene scene) {
        
        this.scene = scene;
        
    }

    @Override
    public List<Point> routeConnection(ConnectionWidget arg0) {
        
        List<Point> pointList;
        Point startPoint, endPoint;
        Router rt = RouterFactory.createOrthogonalSearchRouter(new AFOCollisionCollector(scene) );
        
        pointList = rt.routeConnection(arg0);
        
        return pointList;
    }

}
