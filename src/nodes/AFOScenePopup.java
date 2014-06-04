/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class AFOScenePopup implements PopupMenuProvider, ActionListener {

    
    private Point point;
    private MyGraphPinScene scene;
    private Project prj;
    private JPopupMenu mainMenu;
    private JMenu addMenu;
    private AFOSystemManager systemManager;
    
    public AFOScenePopup(Project prj, MyGraphPinScene scene, AFOSystemManager systemManager) {
        JMenuItem item;
        mainMenu = new JPopupMenu();
        
        addMenu = new JMenu("Add...");
        mainMenu.add(addMenu);
        
        if (!systemManager.isSubSystem()) {
            for (CommonDefinitions.blockTypes bt : CommonDefinitions.blockTypes.values())
            {
                String menuString = "Add "+bt.toString();
                item = new JMenuItem(menuString);
                item.setActionCommand(bt.toString());
                item.addActionListener(this);
                addMenu.add(item);

                if (bt.toString().equals("THU"))
                {
                    menuString = "Add "+CommonDefinitions.blockTypes.Comment.toString();
                    item = new JMenuItem(menuString);
                    item.setActionCommand(CommonDefinitions.blockTypes.Comment.toString());
                    item.addActionListener(this);
                    addMenu.add(item);
                    
                    addMenu.add(new JSeparator());
                    menuString = "Add "+bt.Subsystem.toString();
                    item = new JMenuItem(menuString);
                    item.setActionCommand(bt.Subsystem.toString());
                    item.addActionListener(this);
                    addMenu.add(item);
                    break;
                }

            }

            
            mainMenu.add(new JSeparator());
            item = new JMenuItem("Add Timer");
            item.setActionCommand("Add Timer");
            item.addActionListener(this);
            mainMenu.add(item);
            mainMenu.add(new JSeparator());
            item = new JMenuItem("Project Properties");
            item.setActionCommand("Project Properties");
            item.addActionListener(this);
            mainMenu.add(item);
        }
        else {
            for (  CommonDefinitions.blockTypes bt : CommonDefinitions.blockTypes.values())
            {
                if (bt.ordinal() >= CommonDefinitions.blockTypes.Subsystem.ordinal()) {
                    if (bt == CommonDefinitions.blockTypes.IF){
                        JMenu subMenu = new JMenu("Add IF");
                        addMenu.add(subMenu);
                        for (CommonDefinitions.ifTypes it : CommonDefinitions.ifTypes.values()){
                            item = new JMenuItem(CommonDefinitions.ifStrings[it.ordinal()]);
                            item.setActionCommand(bt.toString()+","+it.toString());
                            item.addActionListener(this);
                            subMenu.add(item);
                        }

                    }
                    else if (bt == CommonDefinitions.blockTypes.Arithmetic){
                        JMenu subMenu = new JMenu("Add Arithmetic");
                        addMenu.add(subMenu);
                        for (CommonDefinitions.arithmeticTypes it : CommonDefinitions.arithmeticTypes.values()){
                            item = new JMenuItem(CommonDefinitions.arithmeticStrings[it.ordinal()]);
                            item.setActionCommand(bt.toString()+","+it.toString());
                            item.addActionListener(this);
                            subMenu.add(item);
                        }
                    }
                    else if (bt == CommonDefinitions.blockTypes.Trigger){
                        JMenu subMenu = new JMenu("Add Trigger");
                        addMenu.add(subMenu);
                        for (CommonDefinitions.triggerTypes it : CommonDefinitions.triggerTypes.values()){
                            item = new JMenuItem(it.toString());
                            item.setActionCommand(bt.toString()+","+it.toString());
                            item.addActionListener(this);
                            subMenu.add(item);
                        }
                    }
                    else if (bt == CommonDefinitions.blockTypes.Logic){
                        JMenu subMenu = new JMenu("Add Logic");
                        addMenu.add(subMenu);
                        for (CommonDefinitions.logicTypes it : CommonDefinitions.logicTypes.values()){
                            item = new JMenuItem(it.toString());
                            item.setActionCommand(bt.toString()+","+it.toString());
                            item.addActionListener(this);
                            subMenu.add(item);
                        }
                    }
//                    else if (bt == CommonDefinitions.blockTypes.PID){
//                        JMenu subMenu = new JMenu("Add PID");
//                        addMenu.add(subMenu);
//                        for (CommonDefinitions.pidTypes it : CommonDefinitions.pidTypes.values()){
//                            item = new JMenuItem(it.toString());
//                            item.setActionCommand(bt.toString()+","+it.toString());
//                            item.addActionListener(this);
//                            subMenu.add(item);
//                        }
//                    }
                    else if (bt == CommonDefinitions.blockTypes.Mux){
                        JMenu subMenu = new JMenu("Add MUX");
                        addMenu.add(subMenu);
                        for (CommonDefinitions.muxTypes it : CommonDefinitions.muxTypes.values()){
                            item = new JMenuItem(it.toString());
                            item.setActionCommand(bt.toString()+","+it.toString());
                            item.addActionListener(this);
                            subMenu.add(item);
                        }
                    }
                    else{
                        String menuString = "Add "+bt.toString();
                        item = new JMenuItem(menuString);
                        item.setActionCommand(bt.toString());
                        item.addActionListener(this);
                        addMenu.add(item);  
                        if (bt == CommonDefinitions.blockTypes.OUT){
                            addMenu.add(new JSeparator());
                        }
                    }
                }
            }
        }

        mainMenu.add(new JSeparator());
        item = new JMenuItem ("Import Subsystem");
        item.setActionCommand("ImportSubsystem");
        item.addActionListener(this);
        mainMenu.add(item);

        this.scene = scene;
        this.systemManager = systemManager;
        this.prj = prj;
    }

    @Override
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
            this.point = point;
            return mainMenu;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String[] splittedEvent = event.getActionCommand().split(",");
        List<String> command = new ArrayList<String>();
        
        for (int i = 0; i < splittedEvent.length; i++){
            command.add(splittedEvent[i]);
        }
        
        if (command.size() == 1){
            command.add("");
        }
        if (event.getActionCommand().equals("Project Properties")){
            prj.showDialog();
        }
        else if (event.getActionCommand().equals(CommonDefinitions.blockTypes.Subsystem.toString())){

            if (!systemManager.isSubSystem()){
                prj.addSubSystem(point,null);
            }
            else {
                prj.addSubSystem(systemManager,point,null);
            }
        }
        else if (event.getActionCommand().equals("Add Timer")){
            prj.addTimer();
        }
        else if (event.getActionCommand().equals("ImportSubsystem")){
            systemManager.importSubsystem();
        }
        else {
            systemManager.addElementToSystem(splittedEvent, point,null,null);
        }   
    }
}
