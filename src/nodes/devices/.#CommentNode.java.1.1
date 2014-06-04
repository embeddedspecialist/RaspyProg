/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextArea;
import nodes.CommonDefinitions;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import sceneManager.AFOGraphPinScene;

/**
 *
 * @author amirrix
 */
public class CommentNode extends GenericNode {

    private WidgetAction moveAction;

    public CommentNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        blockType = CommonDefinitions.blockTypes.Comment;

        moveAction = ActionFactory.createAlignWithMoveAction(scene.mainLayer, scene.interactionLayer, null, false);

        CreateWidget();
    }


    private static class RenameEditor implements TextFieldInplaceEditor {

        @Override
        public boolean isEnabled (Widget widget) {
            return true;
        }

        @Override
        public String getText (Widget widget) {
            return ((LabelWidget) widget).getLabel ();
        }

        @Override
        public void setText (Widget widget, String text) {
            ((LabelWidget) widget).setLabel (text);
        }

    }

    private void CreateWidget() {

        widget = new LabelWidget (scene, label);

        widget.setOpaque (true);
        widget.setBackground (Color.LIGHT_GRAY);
        widget.setBorder (BorderFactory.createEmptyBorder(8));
        widget.setFont (new Font(Font.SANS_SERIF,Font.PLAIN, 14));

        widget.getActions ().addAction (editorAction);

        widget.getActions().addAction(moveAction);
        

        //Aggiungo alla scena:
        getScene().addNode(this);
    }


    @Override
    public void setComment(String comment){
        ((LabelWidget)widget).setLabel(comment);
    }

    @Override
    public String getComment(){
        return ((LabelWidget)widget).getLabel();
    }

    public Widget getOverallWidget() {
        return widget;
    }

}
