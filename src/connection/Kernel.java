package connection;

import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import connection.gui.DlgSetDateTime;
import connection.gui.ErrorDialog;
import connection.gui.LogDialog;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import nodes.AFOSystemManager;
import nodes.Project;
import nodes.devices.AfoTimer;
import nodes.devices.GenericNode;
import sceneManager.AFONode;

/**
 * Questa classe si occupa del collegamento con il sistema
 *
 * TODO -- Implementare il pannello errori
 *      -- Implementare il pannello del log messaggi in ingresso/uscita
 */
public class Kernel extends Thread implements ActionListener {

    private String host;
    private int port;
    private Socket cs;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean connected;
    //private ErrorPanel ep; <--Da implementare
    public AfoDBDao afoDatabase;

    private Project proj;

    private LogDialog dlgLog;
    private ErrorDialog dlgError;
    private DlgSetDateTime dlgDateTime;

    private JButton btnInviaConfig;
    private JButton btnInviaTimers;
    private JTextArea configFileArea;
    private JTextArea timersArea;

    /**
     * Costruttore
     * @param conf File di configurazione
     * @param time File dei timer
     * @param v Vista
     * @param l Oggetto per la gestione della visualizzazione
     * @param ini file ini principale
     * @throws Exception In caso di errore
     */
    public Kernel(Project p, AfoDBDao db) throws Exception {

        this.afoDatabase = db;
        this.proj = p;

        connected = false;

        dlgLog = new LogDialog(null, false);
        dlgError = new ErrorDialog(null, false);
        dlgDateTime = new DlgSetDateTime(null, false, this);

        

        //TODO istanziare una finestra errori

        // Eseguo run
        this.start();
        initNodesAndTimers();

    }

//    public void redownloadConfigFiles() throws IOException {
//        boolean reconnect = false;
//        if (isConnected()){
//            reconnect = true;
//            disconnect();
//        }
//
//        v.getMainConfig().downAction();
//
//        if (reconnect){
//            try {
//                connect();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }


    /**
     * Il kernel prova a collegarsi al server remoto
     * @throws IOException
     * @throws UnknownHostException
     */
    public synchronized void connect() throws IOException, InterruptedException, Exception {

        // Leggo i dati per la connessione
        //TODO da sistemare per collegarsi alla porta voluta anzichè alla prima
        JTable table = proj.getDialog().tableInterfacce;
        host = (String) ((DefaultTableModel)table.getModel()).getValueAt(0, 0);
        port = Integer.parseInt((String) ((DefaultTableModel)table.getModel()).getValueAt(0, 1));
        if (host == null || port == 0) {
            throw new Exception("Parametri per la connessione non validi");
        }
        
        cs = new Socket(host, port);
        getLog().addMsg("Connessione riuscita", LogDialog.ALLMSG);
        in = new DataInputStream(cs.getInputStream());
        out = new DataOutputStream(cs.getOutputStream());
        getLog().addMsg("Attendere finalizzazione...", LogDialog.ALLMSG);

        //Inizializzo i nodi
        initNodesAndTimers();

        
        //Aspetto un attimo... magari funziona meglio...
        Thread.sleep(1000);
        getLog().addMsg("Finalizzazione OK.", LogDialog.ALLMSG);

        //TODO da controllare in qualche modo
//        if (!afoDatabase.isConnected()) {
//            afoDatabase.connect();
//        }

        connected = true;

        setNodesAndTimersConnectionStatus();
        dlgDateTime.btnChangeDate.setEnabled(true);
        dlgDateTime.btnReloadDate.setEnabled(true);
        proj.getDialog().btnChangeIP.setEnabled(true);
    }

    /*
     * Inizializza i nodi per predisporli all'invio comandi
     */
    private void initNodesAndTimers(){
        for (AFOSystemManager sm : proj.getSubSystemList()){

            for (AFONode node : sm.getNodeList()){
                ((GenericNode)node).setKernel(this);
            }
        }

        for (AFONode node : proj.getMainSystem().getNodeList()){
                ((GenericNode)node).setKernel(this);
        }

        //Anche i timer
        for (AfoTimer timer : proj.getTimerList()){
            timer.setKernel(this);
        }
    }

    private void setNodesAndTimersConnectionStatus(){
        for (AFOSystemManager sm : proj.getSubSystemList()){

            for (AFONode node : sm.getNodeList()){
                ((GenericNode)node).setConnectionStatus(connected);
            }
        }

        for (AFONode node : proj.getMainSystem().getNodeList()){
                ((GenericNode)node).setConnectionStatus(connected);
        }

        for (AfoTimer timer : proj.getTimerList()){
            timer.setConnectionStatus(connected);
        }
    }


    /**
     * Chiude la connessione attiva
     * @throws IOException Errore durante la chiusura della connessione
     */
    public synchronized void disconnect() throws IOException {
        connected = false;

        if (in != null) {
            in.close();
        }

        if (out != null) {
            out.close();
        }

        if (cs != null) {

            cs.close();
//            afoDatabase.disconnect();
        }

        getLog().addMsg("Connessione chiusa", LogDialog.ALLMSG);
        setNodesAndTimersConnectionStatus();
        dlgDateTime.btnChangeDate.setEnabled(false);
        dlgDateTime.btnReloadDate.setEnabled(false);
        proj.getDialog().btnChangeIP.setEnabled(false);
        
    }

    /**
     * Invia un comando sul buffer di uscita
     * @param command Comando da inviare
     */
    public void sendCommand(String command) {

        try {
            out.writeBytes(command + "\0");
            out.flush();
            getLog().addMsg("OUT: " + command, LogDialog.OUTMSG);
            //Da rimuovere
            System.out.println(command);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Errore durante l'invio del comando\n" + e.getMessage(), "Errore (COD 3)", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDateTimeDlg(){
        dlgDateTime.setVisible(true);
    }
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        while (true) {
            try {
                sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isConnected()) {
                try {

                    //Scrivo il comando di lettura

                    // Leggo il buffer se ci sono dei dati disponibili
                    while (in.available() > 0) {
                        //int lettera;
                        StringBuilder sb = new StringBuilder();

                        char car;
                        do {
                            car = (char) in.read();
                        } while (car != '<');

                        do {
                            sb.append(car);
                            car = (char) in.read();
                            //System.out.print(car);
                        } while (car != '>');
                        sb.append('>');


                        // Costruisco il comando che ho ricevuto
                        Cmd com = UtilXML.getCmd(sb.toString());

                        // Controllo se e' un comando da device oppure un errore
                        if (com.getCmd().equals("DEVICE")) {

                            //Distinguo se e' per i timer o per le NET
                            if (UtilXML.cmpCmdValue(com, "TYPE", "TimerSettings")) {
                                for (AfoTimer tmr : proj.getTimerList()){
                                    if (tmr.parseCmd(com)){
                                        break;
                                    }
                                }
                            } else {
                                // E lo invio a tutte le net
                                AFOSystemManager mainManager = proj.getMainSystem();

                                for (int i = 0; i < mainManager.getNodeList().size(); i++){
                                    GenericNode node = ((GenericNode)mainManager.getNodeList().get(i));
                                    if (node.parseCmd(com)){
                                        break;
                                    }
                                }
                            }
                        }
                        else if (com.getCmd().equals("BLOCK")) {
                                // E lo invio a tutte le net
                            boolean messageParsed = false;
                            for (AFOSystemManager sm : proj.getSubSystemList()){

                                for (AFONode node : sm.getNodeList()){
                                    if (((GenericNode)node).parseBlockCmd(com)){
                                        messageParsed = true;
                                        break;
                                    }
                                }

                                if (messageParsed){
                                    break;
                                }
                            }
                        }
                        else if (com.getCmd().equals("ERROR")) {
                            dlgError.addError(com.toString());
                        } else if (com.getCmd().equals("STATUS")) {
                            dlgDateTime.parseCmd(com);
                        } else if (com.getCmd().equals("EXEC")) {
                            //Controllo se qualche iterfaccia driver era aperta
                            if (com.getValue("RESULT").equals("0")) {
                                JOptionPane msgBox = new JOptionPane("Comando:\n" + com.getValue("COMMAND") + "\n NON ESEGUITO",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                            else if (com.getValue("RESULT").equals("1")){
                                System.out.println("Comando eseguito!!");
                            }
                        }

                        getLog().addMsg("IN: " + com.toString(), LogDialog.INMSG);


                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    JOptionPane.showMessageDialog(null, "Connessione chiusa\n" + e.getMessage(), "Errore (COD 4)", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Crea un comando per richiedere le impostazioni attuali dei pid
     * @return Comando
     */
    private Cmd getPidInfo() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetPIDInfo");
        return com;
    }

    private Cmd getTimerSettings() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetTimerSettings");
        return com;
    }

    /**
     * Richiede lo stato degli alarmi dei timer
     * @return Comando
     */
    private Cmd getAlarmTimer() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetTempAlarmsSettings");
        return com;
    }

    private Cmd getFloorSettings() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetFloorSettings");
        return com;
    }

    private Cmd getVLV2Settings() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetVLVSettings");
        return com;
    }

    /**
     * Richiede data e ora
     * @return Comando
     */
    private Cmd getDateAndTime() {
        Cmd com = new Cmd("DEVICE");
        com.putValue("COMMAND", "GetDateAndTime");
        return com;
    }

    private void addErrorToDB(Cmd com) {
//    throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return the connect
     */
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInviaConfig){

            //Avviso!!
            int res = JOptionPane.showConfirmDialog(null, "Attenzione: l'esecuzione del comando causerà il riavvio del sistema di controllo\n" +
                    "e la disconnessione dell'interfaccia.\nContinuare ?", "Info", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }
            
            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "ChangeINIFile");
            com.putValue("INIFILE", configFileArea.getText());
            sendCommand(com.toString());
            try {
                disconnect();
            } catch (IOException ex) {
                Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (e.getSource() == btnInviaTimers) {
            //Avviso!!
            int res = JOptionPane.showConfirmDialog(null, "Attenzione: l'esecuzione del comando causerà il riavvio del sistema di controllo\n" +
                    "e la disconnessione dell'interfaccia.\nContinuare ?", "Info", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }

            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "ChangeINIFile");
            com.putValue("TIMERSFILE", timersArea.getText());
            sendCommand(com.toString());
            try {
                disconnect();
            } catch (IOException ex) {
                Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * @return the log
     */
    public LogDialog getLog() {
        return dlgLog;
    }

    /**
     * @return the dlgError
     */
    public ErrorDialog getDlgError() {
        return dlgError;
    }
}
