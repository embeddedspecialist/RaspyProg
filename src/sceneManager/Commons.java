/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sceneManager;

import java.awt.Color;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;

/**
 *
 * @author amirrix
 */
public interface Commons {

    //Bordi
    public static final Border BORDER_4 = BorderFactory.createEmptyBorder (4);
    public static final Border BORDER_FILL_LIGHTYELLOW = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(50, 50, 155), Color.YELLOW);
    public static final Border BORDER_FILL_DARKYELLOW = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(60, 60, 205), Color.YELLOW);
    public static final Border BORDER_LINE = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(0, 0, 1), Color.BLUE);
    public static final Border BORDER_EMPTY = BorderFactory.createEmptyBorder();

    //
    public static final String DEFAULT_NODE_COMMENT="Comment";
}


