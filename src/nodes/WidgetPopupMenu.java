/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import raspyprog.RaspyProgView;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;
import nodes.devices.ScopeNode;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFONode;

/**
 *
 * @author amirrix
 */
public class WidgetPopupMenu implements PopupMenuProvider, ActionListener{

    private AFOGraphPinScene scene;
    private AFOSystemManager systemManager;
    private JPopupMenu menu;
    private Widget node;

    private Point point;

    public WidgetPopupMenu(AFOGraphPinScene scene, AFOSystemManager systemManager) {
        this.scene = scene;
        this.systemManager = systemManager;
        
        menu = new JPopupMenu("Node Menu");
    }

    @Override
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        
        JMenuItem item;
        
        this.node = widget;
        this.point = point;
        
        menu.removeAll();
        
        item = new JMenuItem(CommonDefinitions.DELETE_OBJECT);
        item.setActionCommand(CommonDefinitions.DELETE_OBJECT);
        item.addActionListener(this);
        menu.add(item);
        
        if (scene.isEdge(scene.findObject(widget)))
        {
            return menu;
        }
        else {
            AFONode selectedNode = (AFONode)scene.findObject(node);
            try {
                if ( selectedNode instanceof AFOSystemManager ){
                    item = new JMenuItem("Export");
                    item.setActionCommand(CommonDefinitions.EXPORT_NODE);
                    item.addActionListener(this);
                    menu.add(item);
                }
                else if (selectedNode instanceof ScopeNode){
                    item = new JMenuItem("Show/Hide Chart");
                    item.setActionCommand(CommonDefinitions.SHOW_CHART);
                    item.addActionListener(this);
                    menu.add(item);
                }
            }
            catch (Exception ex){

            }
            
            item = new JMenuItem("Rotate");
            item.setActionCommand(CommonDefinitions.ROTATE_NODE);
            item.addActionListener(this);
            menu.add(item);
            menu.add(new JSeparator());
            item = new JMenuItem("Properties");
            item.setActionCommand(CommonDefinitions.EDIT_NODE);
            item.addActionListener(this);
            menu.add(item);
        }    
        
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //scene.getSystemManager().getProject().projectChanged = true;

        if(e.getActionCommand().equals(CommonDefinitions.DELETE_OBJECT)){

            systemManager.deleteElementFromSystem(node);

        }
//        else if(e.getActionCommand().equals(CommonDefinitions.ROTATE_NODE)){
//            AFONode oldNode = (AFONode)scene.findObject(node);
//            oldNode.RotateNode();
//            oldNode.getWidget().repaint();
//            scene.validate();
//        }
        else if (e.getActionCommand().equals(CommonDefinitions.EDIT_NODE)){
            CommonDefinitions.blockTypes blockType = null;
            AFONode selectedNode = (AFONode)scene.findObject(node);

            try {
                selectedNode.showPropertiesDialog();
                systemManager.getScene().validate();
            }
            catch (Exception ex){
                System.out.println("Errore in selezione nodo: "+ex.getLocalizedMessage());
            }
        }
        else if (e.getActionCommand().equals(CommonDefinitions.EXPORT_NODE)){
            Set<Object> blockList = (Set<Object>) systemManager.getSubSystemScene().getSelectedObjects();
            Object[] blockArray = blockList.toArray();

            if ((blockList.size() != 1) || (!(blockArray[0] instanceof AFOSystemManager))) {
                return;
            }

            AFOSystemManager manager = (AFOSystemManager) blockArray[0];
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("AFO Subsystems", CommonDefinitions.SUBSYST_DEFAULT_EXTENSION.substring(1));

            chooser.setDialogTitle("Save subsystem");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.addChoosableFileFilter(filter);
            chooser.setCurrentDirectory(new File(systemManager.getProject().getProjectFolder()));
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getCurrentDirectory() + File.separator + manager.getComment() + CommonDefinitions.SUBSYST_DEFAULT_EXTENSION;
                File saveFile = new File(fileName);

                if (saveFile.exists()) {
                    saveFile.delete();
                }
                try {
                    saveFile.createNewFile();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                SceneSerializer.serialize(systemManager.getProject(), manager.getSubSystemScene(), saveFile, true);

            }
        }
        else if (e.getActionCommand().equals(CommonDefinitions.SHOW_CHART)){
            AFONode selectedNode = (AFONode)scene.findObject(node);
            ((ScopeNode)selectedNode).showChartFrame();
        }
    }

}
