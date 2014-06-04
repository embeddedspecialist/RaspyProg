/*
 * AFOVisualConfiguratorView.java
 */
package raspyprog;

import connection.XMLCommands.Cmd;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.util.Set;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import nodes.*;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;
import sceneManager.AFOGraphElement;
import sceneManager.AFONode;
import javax.swing.filechooser.*;


/**
 * The application's main frame.
 */
public class RaspyProgView extends FrameView {

    private AppOptions options = new AppOptions();
    private Project project;
    private JTabbedPane projectsTabbedPane;
    SingleFrameApplication myApp;

    public RaspyProgView(SingleFrameApplication app) {
        super(app);

        myApp = app;
        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        projectsTabbedPane = new JTabbedPane();
        mainPanel.add(projectsTabbedPane, BorderLayout.CENTER);

        options.LoadOptions();
        menuNewPrj.setText("New Project");

        menuOpenPrj.setText("Open Project");
        //menuOpenPrj.setIcon(new javax.swing.ImageIcon(getClass().getResource("/afovisualconfigurator/resources/icons_small/folder_blue.png"))); // NOI18N

        menuSavePrj.setText("Save Project");
        //menuSavePrj.setIcon(new javax.swing.ImageIcon(getClass().getResource("/afovisualconfigurator/resources/icons_small/save_all.png"))); // NOI18N
        menuSavePrj.setEnabled(false);

        menuSaveAs.setText("Save as..");
        //menuSavePrj.setIcon(new javax.swing.ImageIcon(getClass().getResource("/afovisualconfigurator/resources/icons_small/save_all.png"))); // NOI18N
        menuSaveAs.setEnabled(false);

        menuCloseProject.setText("Close Project");
        //menuCloseProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/afovisualconfigurator/resources/icons_small/fileclose.png"))); // NOI18N
        menuCloseProject.setEnabled(false);

        btnToolClose.setEnabled(false);
        btnToolSave.setEnabled(false);

        //TODO da rimuovere
        btnZoomToFit.setEnabled(false);

        enableEditTools(false);

        menuEdit.setText("Edit");

        menuProjProperties.setText("Project Properties");
        menuProjProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/raspyprog/resources/icons_small/configure.png"))); // NOI18N

        btnAddTimer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/raspyprog/resources/icons_big/history.png"))); // NOI18N
        btnDeleteTimer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/raspyprog/resources/icons_big/deleteTimer_big.png"))); // NOI18N

        btnDisconnect.setEnabled(false);
        statusMessageLabel.setText("Disconnected");

        btnUploadConfiguration.setEnabled(false);
        btnSendConfigFile.setEnabled(false);
        btnSetDateTime.setEnabled(false);

    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = RaspyProgApp.getApplication().getMainFrame();
            aboutBox = new RaspyProgAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        RaspyProgApp.getApplication().show(aboutBox);
    }

    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    static public boolean deleteFilesByExtension(File path, String extension) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(extension)) {
                    files[i].delete();
                }
            }
        }

        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        menuNewPrj = new javax.swing.JMenuItem();
        menuOpenPrj = new javax.swing.JMenuItem();
        menuSavePrj = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        menuCloseProject = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuImport = new javax.swing.JMenuItem();
        menuExport = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem menuExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuProjProperties = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuAddTimer = new javax.swing.JMenuItem();
        menuDeleteTimer = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        menuSatellite = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();
        toolBarFile = new javax.swing.JToolBar();
        btnToolNew = new javax.swing.JButton();
        btnToolOpen = new javax.swing.JButton();
        btnToolSave = new javax.swing.JButton();
        btnToolClose = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        toolBarEdit = new javax.swing.JToolBar();
        btnProjProperties = new javax.swing.JButton();
        btnEditBlock = new javax.swing.JButton();
        btnDeleteBlock = new javax.swing.JButton();
        btnExportToINI = new javax.swing.JButton();
        toolBarTimer = new javax.swing.JToolBar();
        btnAddTimer = new javax.swing.JButton();
        btnDeleteTimer = new javax.swing.JButton();
        btnSetDateTime = new javax.swing.JButton();
        toolBarConnect = new javax.swing.JToolBar();
        btnConnect = new javax.swing.JButton();
        btnDisconnect = new javax.swing.JButton();
        btnShowLog = new javax.swing.JButton();
        btnShowErrors = new javax.swing.JButton();
        btnUploadConfiguration = new javax.swing.JButton();
        btnSendConfigFile = new javax.swing.JButton();
        toolView = new javax.swing.JToolBar();
        btnShowSatellite = new javax.swing.JButton();
        btnZoomToFit = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        menuBar.setName("menuBar"); // NOI18N
        menuBar.setVerifyInputWhenFocusTarget(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(raspyprog.RaspyProgApp.class).getContext().getActionMap(RaspyProgView.class, this);
        fileMenu.setAction(actionMap.get("newProjectAction")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(raspyprog.RaspyProgApp.class).getContext().getResourceMap(RaspyProgView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        menuNewPrj.setAction(actionMap.get("newProjectAction")); // NOI18N
        menuNewPrj.setIcon(resourceMap.getIcon("menuNewPrj.icon")); // NOI18N
        menuNewPrj.setText(resourceMap.getString("menuNewPrj.text")); // NOI18N
        menuNewPrj.setName("menuNewPrj"); // NOI18N
        fileMenu.add(menuNewPrj);

        menuOpenPrj.setAction(actionMap.get("openProject")); // NOI18N
        menuOpenPrj.setIcon(resourceMap.getIcon("menuOpenPrj.icon")); // NOI18N
        menuOpenPrj.setText(resourceMap.getString("menuOpenPrj.text")); // NOI18N
        menuOpenPrj.setName("menuOpenPrj"); // NOI18N
        fileMenu.add(menuOpenPrj);

        menuSavePrj.setAction(actionMap.get("saveProject")); // NOI18N
        menuSavePrj.setIcon(resourceMap.getIcon("menuSavePrj.icon")); // NOI18N
        menuSavePrj.setText(resourceMap.getString("menuSavePrj.text")); // NOI18N
        menuSavePrj.setName("menuSavePrj"); // NOI18N
        fileMenu.add(menuSavePrj);

        menuSaveAs.setAction(actionMap.get("saveAsAction")); // NOI18N
        menuSaveAs.setName("menuSaveAs"); // NOI18N
        fileMenu.add(menuSaveAs);

        menuCloseProject.setAction(actionMap.get("closeProject")); // NOI18N
        menuCloseProject.setIcon(resourceMap.getIcon("menuCloseProject.icon")); // NOI18N
        menuCloseProject.setText(resourceMap.getString("menuCloseProject.text")); // NOI18N
        menuCloseProject.setName("menuCloseProject"); // NOI18N
        fileMenu.add(menuCloseProject);

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

        menuImport.setText(resourceMap.getString("menuImport.text")); // NOI18N
        menuImport.setName("menuImport"); // NOI18N
        fileMenu.add(menuImport);

        menuExport.setText(resourceMap.getString("menuExport.text")); // NOI18N
        menuExport.setName("menuExport"); // NOI18N
        fileMenu.add(menuExport);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        menuExit.setAction(actionMap.get("quit")); // NOI18N
        menuExit.setIcon(resourceMap.getIcon("menuExit.icon")); // NOI18N
        menuExit.setName("menuExit"); // NOI18N
        fileMenu.add(menuExit);

        menuBar.add(fileMenu);

        menuEdit.setText(resourceMap.getString("menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N

        menuProjProperties.setAction(actionMap.get("editProjectAction")); // NOI18N
        menuProjProperties.setIcon(resourceMap.getIcon("menuProjProperties.icon")); // NOI18N
        menuProjProperties.setText(resourceMap.getString("menuProjProperties.text")); // NOI18N
        menuProjProperties.setName("menuProjProperties"); // NOI18N
        menuEdit.add(menuProjProperties);

        jSeparator2.setName("jSeparator2"); // NOI18N
        menuEdit.add(jSeparator2);

        menuAddTimer.setAction(actionMap.get("addTimer")); // NOI18N
        menuAddTimer.setIcon(resourceMap.getIcon("menuAddTimer.icon")); // NOI18N
        menuAddTimer.setText(resourceMap.getString("menuAddTimer.text")); // NOI18N
        menuAddTimer.setName("menuAddTimer"); // NOI18N
        menuEdit.add(menuAddTimer);

        menuDeleteTimer.setAction(actionMap.get("deleteTimer")); // NOI18N
        menuDeleteTimer.setIcon(resourceMap.getIcon("menuDeleteTimer.icon")); // NOI18N
        menuDeleteTimer.setText(resourceMap.getString("menuDeleteTimer.text")); // NOI18N
        menuDeleteTimer.setName("menuDeleteTimer"); // NOI18N
        menuEdit.add(menuDeleteTimer);

        menuBar.add(menuEdit);

        View.setText(resourceMap.getString("View.text")); // NOI18N
        View.setName("View"); // NOI18N

        menuSatellite.setAction(actionMap.get("openSateliteView")); // NOI18N
        menuSatellite.setIcon(resourceMap.getIcon("menuSatellite.icon")); // NOI18N
        menuSatellite.setText(resourceMap.getString("menuSatellite.text")); // NOI18N
        menuSatellite.setName("menuSatellite"); // NOI18N
        View.add(menuSatellite);

        menuBar.add(View);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 690, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        toolBar.setRollover(true);
        toolBar.setBorderPainted(false);
        toolBar.setName("toolBar"); // NOI18N

        toolBarFile.setRollover(true);
        toolBarFile.setName("toolBarFile"); // NOI18N

        btnToolNew.setAction(actionMap.get("newProjectAction")); // NOI18N
        btnToolNew.setIcon(resourceMap.getIcon("btnToolNew.icon")); // NOI18N
        btnToolNew.setText(resourceMap.getString("btnToolNew.text")); // NOI18N
        btnToolNew.setToolTipText(resourceMap.getString("btnToolNew.toolTipText")); // NOI18N
        btnToolNew.setFocusable(false);
        btnToolNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnToolNew.setName("btnToolNew"); // NOI18N
        btnToolNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnToolNew);

        btnToolOpen.setAction(actionMap.get("openProject")); // NOI18N
        btnToolOpen.setIcon(resourceMap.getIcon("btnToolOpen.icon")); // NOI18N
        btnToolOpen.setText(resourceMap.getString("btnToolOpen.text")); // NOI18N
        btnToolOpen.setToolTipText(resourceMap.getString("btnToolOpen.toolTipText")); // NOI18N
        btnToolOpen.setFocusable(false);
        btnToolOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnToolOpen.setName("btnToolOpen"); // NOI18N
        btnToolOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnToolOpen);

        btnToolSave.setAction(actionMap.get("saveProject")); // NOI18N
        btnToolSave.setIcon(resourceMap.getIcon("btnToolSave.icon")); // NOI18N
        btnToolSave.setText(resourceMap.getString("btnToolSave.text")); // NOI18N
        btnToolSave.setToolTipText(resourceMap.getString("btnToolSave.toolTipText")); // NOI18N
        btnToolSave.setFocusable(false);
        btnToolSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnToolSave.setName("btnToolSave"); // NOI18N
        btnToolSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnToolSave);

        btnToolClose.setAction(actionMap.get("closeProject")); // NOI18N
        btnToolClose.setIcon(resourceMap.getIcon("btnToolClose.icon")); // NOI18N
        btnToolClose.setText(resourceMap.getString("btnToolClose.text")); // NOI18N
        btnToolClose.setToolTipText(resourceMap.getString("btnToolClose.toolTipText")); // NOI18N
        btnToolClose.setFocusable(false);
        btnToolClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnToolClose.setName("btnToolClose"); // NOI18N
        btnToolClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnToolClose);

        btnImport.setAction(actionMap.get("importSubSystemAction")); // NOI18N
        btnImport.setIcon(resourceMap.getIcon("btnImport.icon")); // NOI18N
        btnImport.setText(resourceMap.getString("btnImport.text")); // NOI18N
        btnImport.setToolTipText(resourceMap.getString("btnImport.toolTipText")); // NOI18N
        btnImport.setFocusable(false);
        btnImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImport.setName("btnImport"); // NOI18N
        btnImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnImport);

        btnExport.setAction(actionMap.get("exportSubSystemAction")); // NOI18N
        btnExport.setIcon(resourceMap.getIcon("btnExport.icon")); // NOI18N
        btnExport.setText(resourceMap.getString("btnExport.text")); // NOI18N
        btnExport.setToolTipText(resourceMap.getString("btnExport.toolTipText")); // NOI18N
        btnExport.setFocusable(false);
        btnExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExport.setName("btnExport"); // NOI18N
        btnExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarFile.add(btnExport);

        toolBar.add(toolBarFile);

        toolBarEdit.setRollover(true);
        toolBarEdit.setName("toolBarEdit"); // NOI18N

        btnProjProperties.setAction(actionMap.get("editProjectAction")); // NOI18N
        btnProjProperties.setIcon(resourceMap.getIcon("btnProjProperties.icon")); // NOI18N
        btnProjProperties.setText(resourceMap.getString("btnProjProperties.text")); // NOI18N
        btnProjProperties.setToolTipText(resourceMap.getString("btnProjProperties.toolTipText")); // NOI18N
        btnProjProperties.setFocusable(false);
        btnProjProperties.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProjProperties.setName("btnProjProperties"); // NOI18N
        btnProjProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarEdit.add(btnProjProperties);

        btnEditBlock.setAction(actionMap.get("editSelectedBlock")); // NOI18N
        btnEditBlock.setIcon(resourceMap.getIcon("btnEditBlock.icon")); // NOI18N
        btnEditBlock.setText(resourceMap.getString("btnEditBlock.text")); // NOI18N
        btnEditBlock.setToolTipText(resourceMap.getString("btnEditBlock.toolTipText")); // NOI18N
        btnEditBlock.setFocusable(false);
        btnEditBlock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditBlock.setName("btnEditBlock"); // NOI18N
        btnEditBlock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarEdit.add(btnEditBlock);

        btnDeleteBlock.setAction(actionMap.get("deleteBlock")); // NOI18N
        btnDeleteBlock.setIcon(resourceMap.getIcon("btnDeleteBlock.icon")); // NOI18N
        btnDeleteBlock.setText(resourceMap.getString("btnDeleteBlock.text")); // NOI18N
        btnDeleteBlock.setToolTipText(resourceMap.getString("btnDeleteBlock.toolTipText")); // NOI18N
        btnDeleteBlock.setFocusable(false);
        btnDeleteBlock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteBlock.setName("btnDeleteBlock"); // NOI18N
        btnDeleteBlock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarEdit.add(btnDeleteBlock);

        btnExportToINI.setAction(actionMap.get("exportToIni")); // NOI18N
        btnExportToINI.setIcon(resourceMap.getIcon("btnExportToINI.icon")); // NOI18N
        btnExportToINI.setText(resourceMap.getString("btnExportToINI.text")); // NOI18N
        btnExportToINI.setToolTipText(resourceMap.getString("btnExportToINI.toolTipText")); // NOI18N
        btnExportToINI.setFocusable(false);
        btnExportToINI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportToINI.setName("btnExportToINI"); // NOI18N
        btnExportToINI.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportToINI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportToINIActionPerformed(evt);
            }
        });
        toolBarEdit.add(btnExportToINI);

        toolBar.add(toolBarEdit);

        toolBarTimer.setRollover(true);
        toolBarTimer.setName("toolBarTimer"); // NOI18N

        btnAddTimer.setAction(actionMap.get("addTimer")); // NOI18N
        btnAddTimer.setIcon(resourceMap.getIcon("btnAddTimer.icon")); // NOI18N
        btnAddTimer.setText(resourceMap.getString("btnAddTimer.text")); // NOI18N
        btnAddTimer.setFocusable(false);
        btnAddTimer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTimer.setName("btnAddTimer"); // NOI18N
        btnAddTimer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarTimer.add(btnAddTimer);

        btnDeleteTimer.setAction(actionMap.get("deleteTimer")); // NOI18N
        btnDeleteTimer.setIcon(resourceMap.getIcon("btnDeleteTimer.icon")); // NOI18N
        btnDeleteTimer.setText(resourceMap.getString("btnDeleteTimer.text")); // NOI18N
        btnDeleteTimer.setFocusable(false);
        btnDeleteTimer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteTimer.setName("btnDeleteTimer"); // NOI18N
        btnDeleteTimer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarTimer.add(btnDeleteTimer);

        btnSetDateTime.setAction(actionMap.get("setDateTimeAction")); // NOI18N
        btnSetDateTime.setIcon(resourceMap.getIcon("btnSetDateTime.icon")); // NOI18N
        btnSetDateTime.setText(resourceMap.getString("btnSetDateTime.text")); // NOI18N
        btnSetDateTime.setToolTipText(resourceMap.getString("btnSetDateTime.toolTipText")); // NOI18N
        btnSetDateTime.setFocusable(false);
        btnSetDateTime.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetDateTime.setName("btnSetDateTime"); // NOI18N
        btnSetDateTime.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarTimer.add(btnSetDateTime);

        toolBar.add(toolBarTimer);

        toolBarConnect.setRollover(true);
        toolBarConnect.setName("toolBarConnect"); // NOI18N

        btnConnect.setAction(actionMap.get("connectAction")); // NOI18N
        btnConnect.setIcon(resourceMap.getIcon("btnConnect.icon")); // NOI18N
        btnConnect.setText(resourceMap.getString("btnConnect.text")); // NOI18N
        btnConnect.setToolTipText(resourceMap.getString("btnConnect.toolTipText")); // NOI18N
        btnConnect.setFocusable(false);
        btnConnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConnect.setName("btnConnect"); // NOI18N
        btnConnect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnConnect);

        btnDisconnect.setAction(actionMap.get("disconnectAction")); // NOI18N
        btnDisconnect.setIcon(resourceMap.getIcon("btnDisconnect.icon")); // NOI18N
        btnDisconnect.setText(resourceMap.getString("btnDisconnect.text")); // NOI18N
        btnDisconnect.setToolTipText(resourceMap.getString("btnDisconnect.toolTipText")); // NOI18N
        btnDisconnect.setFocusable(false);
        btnDisconnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDisconnect.setName("btnDisconnect"); // NOI18N
        btnDisconnect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnDisconnect);

        btnShowLog.setAction(actionMap.get("showConnectionLogAction")); // NOI18N
        btnShowLog.setIcon(resourceMap.getIcon("btnShowLog.icon")); // NOI18N
        btnShowLog.setText(resourceMap.getString("btnShowLog.text")); // NOI18N
        btnShowLog.setToolTipText(resourceMap.getString("btnShowLog.toolTipText")); // NOI18N
        btnShowLog.setFocusable(false);
        btnShowLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowLog.setName("btnShowLog"); // NOI18N
        btnShowLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnShowLog);

        btnShowErrors.setAction(actionMap.get("showErrorLogAction")); // NOI18N
        btnShowErrors.setIcon(resourceMap.getIcon("btnShowErrors.icon")); // NOI18N
        btnShowErrors.setText(resourceMap.getString("btnShowErrors.text")); // NOI18N
        btnShowErrors.setToolTipText(resourceMap.getString("btnShowErrors.toolTipText")); // NOI18N
        btnShowErrors.setFocusable(false);
        btnShowErrors.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowErrors.setName("btnShowErrors"); // NOI18N
        btnShowErrors.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnShowErrors);

        btnUploadConfiguration.setAction(actionMap.get("sendConfigToDevice")); // NOI18N
        btnUploadConfiguration.setIcon(resourceMap.getIcon("btnUploadConfiguration.icon")); // NOI18N
        btnUploadConfiguration.setText(resourceMap.getString("btnUploadConfiguration.text")); // NOI18N
        btnUploadConfiguration.setToolTipText(resourceMap.getString("btnUploadConfiguration.toolTipText")); // NOI18N
        btnUploadConfiguration.setFocusable(false);
        btnUploadConfiguration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUploadConfiguration.setName("btnUploadConfiguration"); // NOI18N
        btnUploadConfiguration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnUploadConfiguration);

        btnSendConfigFile.setAction(actionMap.get("actionSendConfigFile")); // NOI18N
        btnSendConfigFile.setIcon(resourceMap.getIcon("btnSendConfigFile.icon")); // NOI18N
        btnSendConfigFile.setText(resourceMap.getString("btnSendConfigFile.text")); // NOI18N
        btnSendConfigFile.setToolTipText(resourceMap.getString("btnSendConfigFile.toolTipText")); // NOI18N
        btnSendConfigFile.setFocusable(false);
        btnSendConfigFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSendConfigFile.setName("btnSendConfigFile"); // NOI18N
        btnSendConfigFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarConnect.add(btnSendConfigFile);

        toolBar.add(toolBarConnect);

        toolView.setRollover(true);
        toolView.setName("toolView"); // NOI18N

        btnShowSatellite.setAction(actionMap.get("openSateliteView")); // NOI18N
        btnShowSatellite.setIcon(resourceMap.getIcon("btnShowSatellite.icon")); // NOI18N
        btnShowSatellite.setText(resourceMap.getString("btnShowSatellite.text")); // NOI18N
        btnShowSatellite.setFocusable(false);
        btnShowSatellite.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowSatellite.setName("btnShowSatellite"); // NOI18N
        btnShowSatellite.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolView.add(btnShowSatellite);

        btnZoomToFit.setIcon(resourceMap.getIcon("btnZoomToFit.icon")); // NOI18N
        btnZoomToFit.setText(resourceMap.getString("btnZoomToFit.text")); // NOI18N
        btnZoomToFit.setFocusable(false);
        btnZoomToFit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomToFit.setName("btnZoomToFit"); // NOI18N
        btnZoomToFit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolView.add(btnZoomToFit);

        toolBar.add(toolView);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(toolBar);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportToINIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportToINIActionPerformed
        project.createIniFile();
    }//GEN-LAST:event_btnExportToINIActionPerformed

    @Action
    public void newProjectAction() {
        NewProjectDialog dlg = new NewProjectDialog(null, true, options.lastOpenDir);
        dlg.setVisible(true);
        if (dlg.getReturnStatus() == NewProjectDialog.RET_OK) {
            //Controllo se esiste già
            String path = dlg.txtProjectPath.getText();

            File projectDir = new File(path);

            if (projectDir.exists()) {
                //Troppo pericoloso: mi sono gia' seccato una cartella per sbaglio!!
                if (projectDir.listFiles().length > 0) {
                }
                //int n = JOptionPane.showConfirmDialog(this.getComponent(), "Attenzione: la cartella esiste, continuando verrà cancellata!!","Conferma",JOptionPane.YES_NO_OPTION);
//                if (n == JOptionPane.YES_OPTION)
//                {
//                    deleteDirectory(projectDir);
//                    projectDir.mkdir();
//                }
            } else {
                projectDir.mkdir();
            }


            project = new Project(dlg.txtProjectName.getText() + ".prj", path);

            options.lastOpenDir = project.getProjectFolder();
            options.SaveOptions();

            try {
                projectsTabbedPane.removeAll();
            } catch (Exception ex) {
            }

            //mainPanel.setLayout(new BorderLayout());
//            projectsTabbedPane = new JTabbedPane();
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(project.getAfoView());
            AFOScenePopup pop = new AFOScenePopup(project, (MyGraphPinScene) project.getMainScene(), project.getMainSystem());
            WidgetPopupMenu wpop = new WidgetPopupMenu(project.getMainScene(), project.getMainSystem());

            project.getMainScene().setWidgetPopupMenuActiom(wpop);
            project.getMainScene().setScenePopupMenuAction(pop);
            project.setTabbedPane(projectsTabbedPane);

            projectsTabbedPane.add(project.getProjectName(), scrollPane);

//            mainPanel.add(projectsTabbedPane,BorderLayout.CENTER);
            mainPanel.validate();

            menuSavePrj.setEnabled(true);
            menuCloseProject.setEnabled(true);
            menuSaveAs.setEnabled(true);

            btnToolClose.setEnabled(true);
            btnToolSave.setEnabled(true);

            enableEditTools(true);

            addTimer();
        }
    }

    @Action
    public void saveProject() {

        File file = new File(project.getProjectFolder() + project.getProjectName());

        //Cancello tutti i file .prj e .sub
        deleteFilesByExtension(new File(project.getProjectFolder()), ".prj");
        deleteFilesByExtension(new File(project.getProjectFolder()), ".sub");

        //Resetto il contatore
        SceneSerializer.subSystemNumber = 1;
        SceneSerializer.serialize(project, project.getMainScene(), file, false);
    }

    @Action
    public void openProject() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("AFO projects", CommonDefinitions.PROJ_DEFAULT_EXTENSION.substring(1));
        JScrollPane scrollPane = new JScrollPane();

        if (mainPanel.getComponentCount() > 0) {
            if (projectsTabbedPane.getComponentCount() > 0) {
                closeProject();
                //mainPanel.removeAll();
            }
        }

        //Devo anticipare questo perche' la deserializzazione aggiunge
//        projectsTabbedPane = new JTabbedPane();
        projectsTabbedPane.removeAll();

        chooser.setDialogTitle("Load Scene ...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(filter);
        chooser.setCurrentDirectory(new File(options.lastOpenDir));
        if (chooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
            project = new Project();
            project.openProject();
            project.setProjectFolder(chooser.getCurrentDirectory().getAbsolutePath() + File.separator);
            project.setProjectName(chooser.getSelectedFile().getName());
            options.lastOpenDir = project.getProjectFolder();
            options.SaveOptions();


            project.setTabbedPane(projectsTabbedPane);

            try {
                SceneSerializer.deserialize(project, project.getMainScene(), chooser.getSelectedFile(), false);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
                return;
            }


            scrollPane.setViewportView(project.getAfoView());
            AFOScenePopup pop = new AFOScenePopup(project, (MyGraphPinScene) project.getMainScene(), project.getMainSystem());
            WidgetPopupMenu wpop = new WidgetPopupMenu(project.getMainScene(), project.getMainSystem());

            project.getMainScene().setWidgetPopupMenuActiom(wpop);
            project.getMainScene().setScenePopupMenuAction(pop);



            projectsTabbedPane.add(scrollPane, 0);
            projectsTabbedPane.setTitleAt(0, project.getProjectName());
            projectsTabbedPane.setSelectedIndex(0);

//            if (mainPanel.getComponentCount() == 0){
//                mainPanel.add(projectsTabbedPane,BorderLayout.CENTER);
//            }
            mainPanel.validate();

            menuSavePrj.setEnabled(true);
            menuCloseProject.setEnabled(true);
            menuSaveAs.setEnabled(true);
            btnToolClose.setEnabled(true);
            btnToolSave.setEnabled(true);


            enableEditTools(true);
        }
    }

    @Action
    public void closeProject() {

        if (JOptionPane.showConfirmDialog(null, "Salvare il progetto ?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            saveProject();
        }

        try {
            if (project.getKernel().isConnected()) {
                disconnectAction();
            }
        } catch (Exception ex) {
            System.out.println("Errore su disconnessione Kernel:");
            ex.printStackTrace();
        }

        menuSavePrj.setEnabled(false);
        menuCloseProject.setEnabled(false);

        projectsTabbedPane.removeAll();
        projectsTabbedPane.validate();

        for (AFOSystemManager sm : project.getSubSystemList()) {
            sm.hidePropertiesDialog();
        }

//        mainPanel.validate();

//        mainPanel.remove(projectsTabbedPane);
//        mainPanel.validate();
//        myApp.getMainFrame().validate();

        btnToolClose.setEnabled(false);
        btnToolSave.setEnabled(false);
        enableEditTools(false);

        //Forzo il garbage collector a distruggere immediatamente il progetto
        project = null;

    }

    private void enableEditTools(boolean enable) {
        btnDeleteBlock.setEnabled(enable);
        btnProjProperties.setEnabled(enable);
        btnEditBlock.setEnabled(enable);
        btnExportToINI.setEnabled(enable);
        menuProjProperties.setEnabled(enable);
        btnAddTimer.setEnabled(enable);
        btnDeleteTimer.setEnabled(enable);
        btnConnect.setEnabled(enable);
        btnShowLog.setEnabled(enable);
        btnShowErrors.setEnabled(enable);
        btnExport.setEnabled(enable);
        btnImport.setEnabled(enable);
        btnShowSatellite.setEnabled(enable);
    }

    @SuppressWarnings("unchecked")
    @Action
    public void editSelectedBlock() {
        try {
            //Vedo se c'è un blocco selezionato
            Set<AFOGraphElement> blockList = (Set<AFOGraphElement>) project.getMainScene().getSelectedObjects();

            //Edito solo se c'e' un solo blocco selezionato
            if (blockList.size() == 1) {

                //Controllo se è un  edge
                if (!(blockList.iterator().next() instanceof AFONode)) {
                    JOptionPane.showMessageDialog(null, "Selezionare un blocco");
                } else {
                    AFONode node = (AFONode) blockList.iterator().next();
                    node.showPropertiesDialog();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore:\n" + ex.toString());
        }

    }

    @SuppressWarnings("unchecked")
    @Action
    public void deleteBlock() {
        try {
            //Non posso usare strutture piu' sofisticate tipo iteratori perche' quando cancello
            //un elemento dalla scena il vettore degli elementi selezionati (sottostante all'iteratore)
            //cambia e quindi mi genera un'eccezione allora la faccio brutale con gli array (Evviva il C)
            //Vedo se c'è un blocco selezionato
            Set<Object> blockList = (Set<Object>) project.getMainScene().getSelectedObjects();
            Object[] blockArray = blockList.toArray();

            //Elimino prima gli edge e poi i blocchi
            for (int i = 0; i < blockArray.length; i++) {
                if (blockArray[i] instanceof String) {
                    Widget widg = project.getMainScene().findWidget(blockArray[i]);
                    project.getMainSystem().deleteElementFromSystem(widg);
                }
            }

            for (int i = 0; i < blockArray.length; i++) {
                if (blockArray[i] instanceof AFOGraphElement) {
                    Widget widg = project.getMainScene().findWidget(blockArray[i]);
                    project.getMainSystem().deleteElementFromSystem(widg);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore:\n" + ex.toString());
        }
    }

    @Action
    public void exportToIni() {
    }

    @Action
    public void editProjectAction() {
        project.showDialog();
    }

    @Action
    public void addTimer() {
        project.addTimer();
    }

    @Action
    public void deleteTimer() {
        int selectedTimer = projectsTabbedPane.getSelectedIndex();
        if (selectedTimer > 0) {
            //I timer partono da 0 ma nel tabbed pane sono in realta' a partire dal
            //numero 1
            project.deleteTimer(selectedTimer - 1);
        }
    }

    @Action
    public void connectAction() {
        try {
            project.getKernel().connect();
            if (project.getKernel().isConnected()) {
                btnSetDateTime.setEnabled(true);
                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
                btnUploadConfiguration.setEnabled(true);
                btnSendConfigFile.setEnabled(true);
                statusMessageLabel.setText("Connected");
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            JOptionPane.showMessageDialog(null, "Errore:\n" + ex.toString());
        }
    }

    @Action
    public void disconnectAction() {
        try {
            project.getKernel().disconnect();
            if (!project.getKernel().isConnected()) {
                btnConnect.setEnabled(true);
                btnDisconnect.setEnabled(false);
                btnSetDateTime.setEnabled(false);
                btnUploadConfiguration.setEnabled(false);
                btnSendConfigFile.setEnabled(false);
                statusMessageLabel.setText("Disconnected");
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            JOptionPane.showMessageDialog(null, "Errore:\n" + ex.toString());
        }
    }

    @Action
    public void showConnectionLogAction() {
        try {
            project.getKernel().getLog().setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Action
    public void showErrorLogAction() {
        try {
            project.getKernel().getDlgError().setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Action
    public void importSubSystemAction() {

        try {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("AFO Subsystems", CommonDefinitions.SUBSYST_DEFAULT_EXTENSION.substring(1));

            chooser.setDialogTitle("Open subsystem");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(filter);
            chooser.setCurrentDirectory(new File(options.lastOpenDir));
            if (chooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {

                File openFile = chooser.getSelectedFile();

                //Se è un sottosistema devo creare un nuovo system manager e via così
                String subSystemID = chooser.getName(openFile);

                project.addSubSystem(new Point(0, 0), null);

                int lastNodeIdx = project.getMainSystem().getNodeListSize() - 1;
                AFONode node = project.getMainSystem().getNodeList().get(lastNodeIdx);
                node.setComment(subSystemID);

                int subSystemSize = project.getSubSystemList().size();

                //Quello che ho appena creato è l'ultimo della lista
                AFOSystemManager newSystemManager = project.getSubSystemList().get(subSystemSize - 1);

                //Ricarico ricorsivamente la scena
                SceneSerializer.deserializeScene(project, newSystemManager.getSubSystemScene(), openFile, true);

                newSystemManager.getForm().setSize(new Dimension(250, 250));
                newSystemManager.getForm().setLocation(0, 0);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore:\n" + ex.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public void exportSubSystemAction() {

        try {
            Set<Object> blockList = (Set<Object>) project.getMainScene().getSelectedObjects();
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
            chooser.setCurrentDirectory(new File(options.lastOpenDir));
            if (chooser.showSaveDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getCurrentDirectory() + File.separator + manager.getComment() + CommonDefinitions.SUBSYST_DEFAULT_EXTENSION;
                File saveFile = new File(fileName);

                if (saveFile.exists()) {
                    saveFile.delete();
                }

                saveFile.createNewFile();

                SceneSerializer.serialize(project, manager.getSubSystemScene(), saveFile, true);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore:\n" + ex.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public void openSateliteView() {
        SatelliteDialog dlg = new SatelliteDialog(null, false);
        dlg.add(project.getMainScene().createSatelliteView());
        dlg.setSize(250, 250);
        dlg.setTitle("Satellite View -- Main Scene");
        dlg.setVisible(true);
    }

    @Action
    public void sendConfigToDevice() {

        int res = JOptionPane.showConfirmDialog(null, "Attenzione: l'esecuzione del comando causerà il riavvio del sistema di controllo\n"
                + "e la disconnessione dell'interfaccia.\nContinuare ?", "Info", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.NO_OPTION) {
            return;
        }
        //Qui direi che salvo i file di configurazione e li ricarico inviandoli
        try {
            if (project.createIniFile()) {

                Cmd com = new Cmd("DEVICE");
                com.putValue("COMMAND", "ChangeINIFile");

                String filePath = project.getProjectFolder();
                File f = new File(filePath + "config.ini");
                com.putValue("FILENAME1", "./config.ini");
                //faccio  una "porcatina"....
                JTextArea area = new JTextArea();
                area.read(new FileReader(f), null);
                com.putValue("INIFILE1", area.getText());

                f = new File(filePath + "timers.ini");
                com.putValue("FILENAME2", "./timers.ini");
                //faccio  una "porcatina"....
                area = new JTextArea();
                area.read(new FileReader(f), null);
                com.putValue("INIFILE2", area.getText());

                f = new File(filePath + "Blocks.ini");
                com.putValue("FILENAME3", "./Blocks.ini");
                //faccio  una "porcatina"....
                area = new JTextArea();
                area.read(new FileReader(f), null);
                com.putValue("INIFILE3", area.getText());

//                f = new File (filePath+"climatic.ini");
//                com.putValue("FILENAME4", "./climatic.ini");
//                //faccio  una "porcatina"....
//                area = new JTextArea();
//                area.read(new FileReader(f), null);
//                com.putValue("INIFILE4", area.getText());

                //Mo lo sparo tutto
                project.getKernel().sendCommand(com.toString());

                //Questo mi serve per evitare di chiudere il socket troppo in fretta
                //e perdere il messaggio
                int waitTime = (int) (com.toString().length());

                if (waitTime < 1000) {
                    waitTime = 1000;
                }

                Thread.sleep(waitTime);

                //Mi disconnetto perche' tanto il programma si e' riavviato
                disconnectAction(); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'invio dei file", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public void setDateTimeAction() {
        try {
            project.getKernel().showDateTimeDlg();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }



    @Action
    public void saveAsAction() {

        boolean exists;
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("AFO projects", CommonDefinitions.PROJ_DEFAULT_EXTENSION.substring(1));

        chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        chooser.setApproveButtonText("Salva");

        chooser.setDialogTitle("Save As...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(filter);
        chooser.setCurrentDirectory(new File(options.lastOpenDir));

        //chooser.setFileFilter(new Filtro());

        chooser.setVisible(true);

        if (chooser.showSaveDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {

            File f = chooser.getSelectedFile();
            String nameOfFile = "";
            String projectName = "";
            nameOfFile = f.getPath();
            exists = (new File(nameOfFile)).exists();
            if (exists) {
                if (JOptionPane.showConfirmDialog(null,
                        "Sovrascrivere il file esistente ?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
                    return;
                }
            }

            projectName = chooser.getSelectedFile().getName();
            //Check se c'è l'estensione
            if (!nameOfFile.endsWith(CommonDefinitions.PROJ_DEFAULT_EXTENSION)) {
                nameOfFile = nameOfFile+CommonDefinitions.PROJ_DEFAULT_EXTENSION;
                projectName = projectName + CommonDefinitions.PROJ_DEFAULT_EXTENSION;
            }

            File file = new File(nameOfFile);

            project.setProjectFolder(chooser.getCurrentDirectory().getAbsolutePath() + File.separator);
            project.setProjectName(projectName);
            project.cleanSubsystemFileName();

            //TODO da inserire dei check sul risultato
            SceneSerializer.subSystemNumber = 1;
            SceneSerializer.serialize(project, project.getMainScene(), file, false);

            projectsTabbedPane.setTitleAt(0, projectName);

        }
    }

    /**
     * Invia un file al cervelletto SENZA crearlo
     */
    @Action
    public void actionSendConfigFile() {
                int res = JOptionPane.showConfirmDialog(null, "Attenzione: l'esecuzione del comando causerà il riavvio del sistema di controllo\n"
                + "e la disconnessione dell'interfaccia.\nContinuare ?", "Info", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.NO_OPTION) {
            return;
        }
        //Qui direi che salvo i file di configurazione e li ricarico inviandoli
        try {

            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "ChangeINIFile");

            String filePath = project.getProjectFolder();
            File f = new File(filePath + "config.ini");
            if (!f.exists()){
                JOptionPane.showMessageDialog(null, "Il file config.ini NON esiste", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            com.putValue("FILENAME1", "./config.ini");
            //faccio  una "porcatina"....
            JTextArea area = new JTextArea();
            area.read(new FileReader(f), null);
            com.putValue("INIFILE1", area.getText());

            f = new File(filePath + "timers.ini");
            if (!f.exists()){
                JOptionPane.showMessageDialog(null, "Il file timers.ini NON esiste", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            com.putValue("FILENAME2", "./timers.ini");
            //faccio  una "porcatina"....
            area = new JTextArea();
            area.read(new FileReader(f), null);
            com.putValue("INIFILE2", area.getText());

            f = new File(filePath + "Blocks.ini");
            if (!f.exists()){
                JOptionPane.showMessageDialog(null, "Il file Blocks.ini NON esiste", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            com.putValue("FILENAME3", "./Blocks.ini");
            //faccio  una "porcatina"....
            area = new JTextArea();
            area.read(new FileReader(f), null);
            com.putValue("INIFILE3", area.getText());

//                f = new File (filePath+"climatic.ini");
//                com.putValue("FILENAME4", "./climatic.ini");
//                //faccio  una "porcatina"....
//                area = new JTextArea();
//                area.read(new FileReader(f), null);
//                com.putValue("INIFILE4", area.getText());

            //Mo lo sparo tutto
            project.getKernel().sendCommand(com.toString());

            //Questo mi serve per evitare di chiudere il socket troppo in fretta
            //e perdere il messaggio
            int waitTime = (int) (com.toString().length());

            if (waitTime < 1000) {
                waitTime = 1000;
            }

            Thread.sleep(waitTime);

            //Mi disconnetto perche' tanto il programma si e' riavviato
            disconnectAction();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'invio dei file", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu View;
    private javax.swing.JButton btnAddTimer;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDeleteBlock;
    private javax.swing.JButton btnDeleteTimer;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnEditBlock;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnExportToINI;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnProjProperties;
    private javax.swing.JButton btnSendConfigFile;
    private javax.swing.JButton btnSetDateTime;
    private javax.swing.JButton btnShowErrors;
    private javax.swing.JButton btnShowLog;
    private javax.swing.JButton btnShowSatellite;
    private javax.swing.JButton btnToolClose;
    private javax.swing.JButton btnToolNew;
    private javax.swing.JButton btnToolOpen;
    private javax.swing.JButton btnToolSave;
    private javax.swing.JButton btnUploadConfiguration;
    private javax.swing.JButton btnZoomToFit;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem menuAddTimer;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuCloseProject;
    private javax.swing.JMenuItem menuDeleteTimer;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenuItem menuExport;
    private javax.swing.JMenuItem menuImport;
    private javax.swing.JMenuItem menuNewPrj;
    private javax.swing.JMenuItem menuOpenPrj;
    private javax.swing.JMenuItem menuProjProperties;
    private javax.swing.JMenuItem menuSatellite;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JMenuItem menuSavePrj;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToolBar toolBarConnect;
    private javax.swing.JToolBar toolBarEdit;
    private javax.swing.JToolBar toolBarFile;
    private javax.swing.JToolBar toolBarTimer;
    private javax.swing.JToolBar toolView;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
