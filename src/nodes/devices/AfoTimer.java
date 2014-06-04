package nodes.devices;

import connection.Kernel;
import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.text.DateFormatSymbols;
import java.util.Locale;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import nodes.SceneSerializer;
import nodes.gui.PanelDevice;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AfoTimer implements ActionListener, PropertyChangeListener {

    private int num;
    private String name;
    private JSpinner digitalLevel[];
    private JSpinner analogLevel[];

    private javax.swing.JTable tzTable[];
    private JButton insertButtonArray[];
    private JButton deleteButtonArray[];

    private JButton[] copyButtonArray;
    private JButton[] copyAllButtonArray;
    
    private JButton btnEnableTimer;
    private JButton btnGetTimerData;
    private JButton btnSendTimer;
    private JButton btnSendLevels;

    private final String[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] DAYS_ANSWER = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private final String TIMER_NODE = "Timer";
    private final String LEVEL_NODE ="Level";

    JPanel timerPanel;
    private Kernel k;
    private PanelDevice buttonPanel;


    public AfoTimer(int num, String name) {
        this.name = name;
        this.num = num;
        createTimerPanel();
        //Aggiungo i sorter
        sortAllTables();
    }

    public void sortAllTables(){
        for (int i = 0; i < 7; i++){
            TableRowSorter<TableModel> sorter
                = new TableRowSorter<TableModel>(tzTable[i].getModel());
            List <RowSorter.SortKey> sortKeys
                = new ArrayList<RowSorter.SortKey>();
            sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
//            sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
            sorter.setSortKeys(sortKeys);
            tzTable[i].setRowSorter(sorter);
        }
    }

    /**
     * Ritorna il nome del timer
     * @return Nome del timer
     */
    public String getName() {
        return name;
    }

    private void createTimerPanel(){
        timerPanel = new JPanel();
        GridLayout gl = new GridLayout(0, 3);
        gl.setHgap(5);
        gl.setVgap(5);
        timerPanel.setLayout(gl);
        timerPanel.add(createButtonPanel());
        timerPanel.add(createLevelPanel());
        createDayPanel(timerPanel);
        
    }

    /**
     * Ritorna il pannello da aggiungere al tabs della vista
     * @return Pannello
     */
    public JPanel getTimerPanel() {
        return timerPanel;
    }

    /**
     * Abilita/disabilita la modifica del timer
     * @param b attivato/disattivato
     */
    public void setEnabled(boolean b) {
        for (int i = 0; i < 7; i++) {
            insertButtonArray[i].setEnabled(b);
            deleteButtonArray[i].setEnabled(b);
        }

        //btnEnableTimer.setEnabled(b);
        btnGetTimerData.setEnabled(b);
        btnSendTimer.setEnabled(b);
        btnSendLevels.setEnabled(b);
    }

    private JPanel createButtonPanel() {
        buttonPanel = new PanelDevice("Comandi",0);

        JPanel pd1 = new JPanel();
        btnGetTimerData = new JButton("Get Timer");
        btnGetTimerData.addActionListener(this);
        pd1.add(btnGetTimerData);

//        JPanel pd2 = new JPanel();
//        btnEnableTimer = new JButton("Enable");
//        btnEnableTimer.addActionListener(this);
//        pd2.add(btnEnableTimer);

        JPanel pd3 = new JPanel();
        btnSendTimer = new JButton("Send Timer");
        btnSendTimer.addActionListener(this);
        pd3.add(btnSendTimer);

        boolean connected = false;
        if (k != null){
            connected = k.isConnected();
        }

        setConnectionStatus(connected);
        
        buttonPanel.add(pd1);
//        buttonPanel.add(pd2);
        buttonPanel.add(pd3);

        return buttonPanel;
    }

    private JPanel createLevelPanel() {
        PanelDevice mainLevelPanel = new PanelDevice("Timer Levels", new Dimension(200, 250),0);

        digitalLevel = new JSpinner[3];
        analogLevel = new JSpinner[3];
//        PIDLevel = new JSpinner[3];


        for (int level = 0; level < 3; level++) {
            String panelName = "Level ";
            panelName += level + 1;

            PanelDevice levelPanel = new PanelDevice(panelName, new Dimension(200, 250),0);
            JPanel pd = new JPanel();
            pd.setLayout(new FlowLayout(FlowLayout.CENTER));
            pd.add(new JLabel("Digital :"));
            digitalLevel[level] = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
            pd.add(digitalLevel[level]);
            pd.add(new JLabel("Analogic : "));
            analogLevel[level] = new JSpinner(new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.5));
            pd.add(analogLevel[level]);

            levelPanel.add(pd);
            mainLevelPanel.add(levelPanel);
        }

        digitalLevel[1].setValue(1);
        
//        JPanel pd4 = new JPanel();
//        pd4.setLayout(new FlowLayout(FlowLayout.CENTER));
//        sendLevels = new JButton("Cambia Livelli");
//        sendLevels.addActionListener(this);
//        pd4.add(sendLevels);
//        mainLevelPanel.add(pd4);
        
        return mainLevelPanel;
    }

//    private void createDayPanel(JPanel p) {
//        //Prendo i giorni della settimana
//        Locale myLocale = Locale.getDefault();
//        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(myLocale);
//        String[] dayNames = dateFormatSymbols.getWeekdays();
//
//        tzTable = new javax.swing.JTable[7];
//        insertButtonArray = new JButton[7];
//        deleteButtonArray = new JButton[7];
//
//        for (int i = 0; i < 7; i++) {
//            //Definisco il modello della tabella
//            DefaultTableModel tzTableModel = new javax.swing.table.DefaultTableModel(
//                    new Object[][]{},
//                    new String[]{
//                        "Hour", "Lev"
//                    });
//
//            PanelDevice newDay = new PanelDevice(dayNames[i + 1],0);
//            tzTable[i] = new javax.swing.JTable();
//            tzTable[i].setModel(tzTableModel);
//
//            TableColumn levelColumn = tzTable[i].getColumnModel().getColumn(1);
//            TableColumn hourColumn = tzTable[i].getColumnModel().getColumn(0);
//
//            JComboBox comboBox = new JComboBox();
//            comboBox.addItem("1");
//            comboBox.addItem("2");
//            comboBox.addItem("3");
//            comboBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);
//
//            levelColumn.setCellEditor(new DefaultCellEditor(comboBox));
//
//            JTextField textField = new JTextField();
//            textField.setHorizontalAlignment(JTextField.CENTER);
//            hourColumn.setCellEditor(new DefaultCellEditor(textField));
//
//            JPanel pd = new JPanel();
//            pd.setLayout(new FlowLayout(FlowLayout.CENTER));
//            JScrollPane scrollPane = new JScrollPane();
//
//            scrollPane.setPreferredSize(new Dimension(100, 200));
//            scrollPane.setViewportView(tzTable[i]);
//
//            JPanel buttonPanel = new JPanel();
//            buttonPanel.setPreferredSize(new Dimension(100, 200));
////            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//            insertButtonArray[i] = new JButton("Insert");
//            insertButtonArray[i].addActionListener(this);
//            buttonPanel.add(insertButtonArray[i]);
//
//            deleteButtonArray[i] = new JButton("Delete");
//            deleteButtonArray[i].addActionListener(this);
//            buttonPanel.add(deleteButtonArray[i]);
//
//            pd.add(buttonPanel);
//            pd.add(scrollPane);
//
//            ((DefaultTableModel) tzTable[i].getModel()).addRow(new Object[]{""});
//            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("0000", 0, 0);
//            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("1", 0, 1);
//
//            ((DefaultTableModel) tzTable[i].getModel()).addRow(new Object[]{""});
//            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("2345", 1, 0);
//            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("1", 1, 1);
//
//
//            newDay.add(pd);
//
//            p.add(newDay);
//        }
//    }

    private void setupTableColumns(JTable table){
        TableColumn levelColumn = table.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("1");
        comboBox.addItem("2");
        comboBox.addItem("3");
        comboBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        levelColumn.setCellEditor(new DefaultCellEditor(comboBox));

        TableColumn hourColumn = table.getColumnModel().getColumn(0);
        JComboBox hourCombo = new JComboBox();

        for (int hour = 0; hour < 24; hour++){
            for (int minute = 0; minute<60; minute+=30){
                hourCombo.addItem(String.format("%1$02d", hour)+":"+String.format("%1$02d", minute));
            }
        }

        hourColumn.setCellEditor(new DefaultCellEditor(hourCombo));
    }

    private void createDayPanel(JPanel p) {
        //Prendo i giorni della settimana
        Locale myLocale = Locale.getDefault();
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(myLocale);
        String[] dayNames = dateFormatSymbols.getWeekdays();

        tzTable = new javax.swing.JTable[7];
        insertButtonArray = new JButton[7];
        deleteButtonArray = new JButton[7];
        copyButtonArray = new JButton[7];
        copyAllButtonArray = new JButton[7];

        for (int i = 0; i < 7; i++) {
            //Definisco il modello della tabella
            DefaultTableModel tzTableModel = new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Hour", "Lev"
                    });

            PanelDevice newDay = new PanelDevice(dayNames[i + 1],0);
            tzTable[i] = new javax.swing.JTable();
            tzTable[i].addPropertyChangeListener(this);
            tzTable[i].setModel(tzTableModel);

            setupTableColumns(tzTable[i]);

//            JTextField textField = new JTextField();
//            textField.setHorizontalAlignment(JTextField.CENTER);
//            hourColumn.setCellEditor(new DefaultCellEditor(textField));

            JPanel pd = new JPanel();
            pd.setLayout(new FlowLayout(FlowLayout.CENTER));
            JScrollPane scrollPane = new JScrollPane();

            scrollPane.setPreferredSize(new Dimension(130, 200));
            scrollPane.setViewportView(tzTable[i]);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setPreferredSize(new Dimension(100, 200));
//            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            insertButtonArray[i] = new JButton("Insert");
            insertButtonArray[i].addActionListener(this);
            buttonPanel.add(insertButtonArray[i]);

            deleteButtonArray[i] = new JButton("Delete");
            deleteButtonArray[i].addActionListener(this);
            buttonPanel.add(deleteButtonArray[i]);

            copyButtonArray[i] = new JButton("Copy Next");
            copyButtonArray[i].setToolTipText("Copies the current day to the next one");
            copyButtonArray[i].addActionListener(this);
            buttonPanel.add(copyButtonArray[i]);
            Dimension dim = copyButtonArray[i].getPreferredSize();

            copyAllButtonArray[i] = new JButton("Copy All");
            copyButtonArray[i].setToolTipText("Copies the current day to every day");
            copyAllButtonArray[i].addActionListener(this);
            buttonPanel.add(copyAllButtonArray[i]);

            insertButtonArray[i].setPreferredSize(dim);
            deleteButtonArray[i].setPreferredSize(dim);
            copyAllButtonArray[i].setPreferredSize(dim);

            pd.add(buttonPanel);
            pd.add(scrollPane);

            ((DefaultTableModel) tzTable[i].getModel()).addRow(new Object[]{""});
            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("00:00", 0, 0);
            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("1", 0, 1);

            ((DefaultTableModel) tzTable[i].getModel()).addRow(new Object[]{""});
            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("23:45", 1, 0);
            ((DefaultTableModel) tzTable[i].getModel()).setValueAt("1", 1, 1);


            newDay.add(pd);

            p.add(newDay);
        }
    }

    public void setConnectionStatus(boolean isConnected){
        btnGetTimerData.setEnabled(isConnected);
//        btnEnableTimer.setEnabled(isConnected);
        btnSendTimer.setEnabled(isConnected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("get timer")){
            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "GetTimerSettings");
            com.putValue("TIMERID", num + "");
            k.sendCommand(com.toString());
        }
        else if (e.getSource().equals(btnSendTimer)){

            //Devo mandare un messaggio per giorno
            for (int day = 0; day < 7; day++) {
                Cmd com = new Cmd("DEVICE");
                com.putValue("COMMAND", "ChangeTimerSettings");
                com.putValue("TIMERID", num + "");
                String dayTimerString = "";
                DefaultTableModel tableModel = ((DefaultTableModel) tzTable[day].getModel());

                int nOfRows = tableModel.getRowCount();
                for (int row = 0; row < nOfRows; row++) {

                    //Tolgo i due punti perchè non mi servono:
                    String hourStr = tableModel.getValueAt(row, 0).toString();
                    int columnPos = hourStr.indexOf(':');
                    if (columnPos < 0){
                        JOptionPane.showMessageDialog(null, "Errore nei parametri del timer!!\r\nEsco!");
                        return;
                    }

                    //dayTimerString += ((DefaultTableModel) tzTable[day].getModel()).getValueAt(row, 0).toString();
                    dayTimerString += hourStr.substring(0,columnPos)+hourStr.substring(columnPos+1);

                    dayTimerString += ":Liv";
                    dayTimerString += ((DefaultTableModel) tzTable[day].getModel()).getValueAt(row, 1).toString();

                    if (row != nOfRows - 1) {
                        //Se NON sono all'ultima riga aggiungo la virgola
                        dayTimerString += ",";
                    }
                }

                com.putValue("DAY", DAYS[day]);
                com.putValue("SETTING", dayTimerString);

                k.sendCommand(com.toString());
            }

        } else if (e.getActionCommand().equalsIgnoreCase("Insert")) {
            int dayIndex = -1;
            //devo cercare di capire da quale giorno arriva...
            for (int i = 0; i < 7; i++) {
                if (insertButtonArray[i] == e.getSource()) {
                    dayIndex = i;
                    break;
                }
            }

            if (dayIndex < 0) {
                return;
            }

            if (tzTable[dayIndex].getSelectedRow() >= 0) {
                ((DefaultTableModel) tzTable[dayIndex].getModel()).insertRow(tzTable[dayIndex].getSelectedRow(), new Object[]{""});
            } else if (tzTable[dayIndex].getRowCount() == 0) {
                ((DefaultTableModel) tzTable[dayIndex].getModel()).addRow(new Object[]{""});
            }
        } else if (e.getActionCommand().equalsIgnoreCase("Delete")) {
            int dayIndex = -1;
            //devo cercare di capire da quale giorno arriva...
            for (int i = 0; i < 7; i++) {
                if (deleteButtonArray[i] == e.getSource()) {
                    dayIndex = i;
                    break;
                }
            }

            if (dayIndex < 0) {
                return;
            }

            if (tzTable[dayIndex].getSelectedRow() >= 0) {
                ((DefaultTableModel) tzTable[dayIndex].getModel()).removeRow(tzTable[dayIndex].getSelectedRow());
            }
        }
       else if (e.getActionCommand().equalsIgnoreCase("Copy Next")) {

            int dayIndex = -1;
            int nextDayIdx = -1;

            //devo cercare di capire da quale giorno arriva...
            for (int i = 0; i < 7; i++) {
                if (copyButtonArray[i] == e.getSource()) {
                    dayIndex = i;
                    break;
                }
            }

            if (dayIndex < 0) {
                return;
            }

            nextDayIdx = (dayIndex+1)%7;

            tzTable[nextDayIdx].setRowSorter(null);
            cloneTableModel(tzTable[dayIndex], tzTable[nextDayIdx]);
            timerPanel.invalidate();
            sortAllTables();


        } else if (e.getActionCommand().equalsIgnoreCase("Copy All")) {
            int dayIndex = -1;
            //devo cercare di capire da quale giorno arriva...
            for (int i = 0; i < 7; i++) {
                if (copyAllButtonArray[i] == e.getSource()) {
                    dayIndex = i;
                    break;
                }
            }

            if (dayIndex < 0) {
                return;
            }


            for (int i = 0 ; i < 7; i++){
                tzTable[i].setRowSorter(null);
                if (i == dayIndex){
                    continue;
                }

                cloneTableModel(tzTable[dayIndex], tzTable[i]);

            }

            sortAllTables();
            timerPanel.validate();
        }
    }

    private void cleanTable(DefaultTableModel tableModel) {

        while (tableModel.getRowCount() > 0){
            tableModel.removeRow(0);
        }
    }

    private DefaultTableModel cloneTableModel(JTable tableToClone, JTable tableDest){

        DefaultTableModel newModel = new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Hour", "Lev"
                    });

        cleanTable((DefaultTableModel)tableDest.getModel());
        DefaultTableModel modelToClone = (DefaultTableModel)tableToClone.getModel();

        tableDest.setModel(newModel);

//        Vector dataRow = modelToClone.getDataVector();
//        Vector columnsId = new Vector();
//        columnsId.add("Hour");
//        columnsId.add("Lev");
//
//        Vector newDataVector = (Vector) dataRow.clone();
//        newModel.setDataVector(newDataVector, columnsId);

        for (int i = 0; i < modelToClone.getRowCount(); i++){
            Object value;
            newModel.addRow(new Object[]{""});
            value = modelToClone.getValueAt(i, 0);
            newModel.setValueAt(value, i, 0);
            value = modelToClone.getValueAt(i, 1);
            newModel.setValueAt(value, i, 1);
        }

        newModel.fireTableDataChanged();
        setupTableColumns(tableDest);
        return newModel;
    }

    /**
     * Ritorna un hash che contiene gli attributi del comando xml
     * @param xml comando
     * @return Hash con gli attributi
     */
    private HashMap<String, String> createMap(String inputString) {
        HashMap<String, String> hm = new HashMap<String, String>();

        int pos = 0;

        while (pos < inputString.length()) {
            while (inputString.charAt(pos) == ' ') {
                pos++;
            }

            StringBuilder key = new StringBuilder();
            while (inputString.charAt(pos) != ':') {
                key.append(inputString.charAt(pos));
                pos++;
            }
            pos++;
            StringBuilder value = new StringBuilder();
            while ((pos < inputString.length()) && (inputString.charAt(pos) != ',')) {
                value.append(inputString.charAt(pos));
                pos++;
            }
            pos++;
            hm.put(key.toString().trim(), value.toString().trim());
        }

        return hm;
    }

    public void deserialize(Node node) throws Exception{
        String attribute;
        for (Node element : SceneSerializer.getChildNode (node)) {
            if (element.getNodeName().equals("Level1")){
                attribute = SceneSerializer.getAttributeValue(element, "Digital");
                digitalLevel[0].setValue(Integer.parseInt(attribute));
                analogLevel[0].setValue(Double.parseDouble(SceneSerializer.getAttributeValue(element, "Analog")));
            }
            else if (element.getNodeName().equals("Level2")){
                attribute =SceneSerializer.getAttributeValue(element, "Digital");
                digitalLevel[1].setValue(Integer.parseInt(attribute));
                attribute = SceneSerializer.getAttributeValue(element, "Analog");
                analogLevel[1].setValue(Double.parseDouble(attribute));
            }
            else if (element.getNodeName().equals("Level3")){
                attribute = SceneSerializer.getAttributeValue(element, "Digital");
                digitalLevel[2].setValue(Integer.parseInt(attribute));
                attribute = SceneSerializer.getAttributeValue(element, "Analog");
                analogLevel[2].setValue(Double.parseDouble(attribute));
            }
            else {
                int dayIndex = -1;
                for (int i = 0; i<7; i++){
                    if (element.getNodeName().equals(DAYS[i])){
                        dayIndex = i;
                        break;
                    }
                }

                if (dayIndex < 0){
//                    JOptionPane.showMessageDialog(null,"Si e' verificato un errore nel timer "+num, "Errore",JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                CleanTable(((DefaultTableModel) tzTable[dayIndex].getModel()));
                //Deserializzo i giorni
                for (Node dayInterval : SceneSerializer.getChildNode(element)){
                    if (dayInterval.getNodeName().equalsIgnoreCase("interval")){
                        //Aggiungo la riga
                        ((DefaultTableModel) tzTable[dayIndex].getModel()).addRow(new Object[]{""});
                        int rowCount = ((DefaultTableModel) tzTable[dayIndex].getModel()).getRowCount();
                        ((DefaultTableModel) tzTable[dayIndex].getModel()).setValueAt(SceneSerializer.getAttributeValue(dayInterval, "time"), rowCount-1, 0);

                        ((DefaultTableModel) tzTable[dayIndex].getModel()).setValueAt(SceneSerializer.getAttributeValue(dayInterval, "level"), rowCount-1, 1);
                    }
                }
            }
        }

        sortAllTables();
    }

    /**
     * Serializza un timer nel seguente modo:
     * <Timer id="0">
     *      <Sun interval0="11:22" interval1="" />
     *      <Level1 digital="0" analog="100.0" />
     * </Timer>
     * @param document il documento a cui aggiungere questo elemento
     * @param nodeElement il nodo elemento a cui sarà aggiunto questo timer
     * @return l'elemento creato
     * @throws Exception
     */
    public Element serialize(Document document,Element nodeElement) throws Exception {
        Element timerNode = document.createElement(TIMER_NODE);

        SceneSerializer.setAttribute(document, timerNode, "id", new Integer(num).toString());

        //Ora aggiungo i livelli
        for (int i = 1; i <= 3; i++) {
            Element levelNode = document.createElement(LEVEL_NODE+i);
            SceneSerializer.setAttribute(document, levelNode, "Digital", digitalLevel[i-1].getModel().getValue().toString());
            SceneSerializer.setAttribute(document, levelNode, "Analog", analogLevel[i-1].getModel().getValue().toString());
            timerNode.appendChild(levelNode);
        }

        //Ora aggiungo i giorni:
        for (int day = 0; day <7; day++){
            Element dayNode = document.createElement(DAYS[day]);
            //Ora creo degli altri figli: uno per intervallo in questo giorno
            for (int interval = 0; interval < tzTable[day].getRowCount(); interval++){
                DefaultTableModel tableMode = (DefaultTableModel)tzTable[day].getModel();
                Element intervalNode = document.createElement("Interval");
                SceneSerializer.setAttribute(document, intervalNode, "time",
                        ((Vector)tableMode.getDataVector().elementAt(interval)).elementAt(0).toString());

                SceneSerializer.setAttribute(document, intervalNode, "level",
                        ((Vector)tableMode.getDataVector().elementAt(interval)).elementAt(1).toString());
                dayNode.appendChild(intervalNode);
            }

            timerNode.appendChild(dayNode);
        }

        nodeElement.appendChild(timerNode);
        return nodeElement;
    }

    /*
     * [Timer1]
        Comment=TimerLuci
        Level1=Digital:1,Alarm:0,PID:18.0
        Level2=Digital:0,Alarm:0,PID:19.0
        Level3=Digital:0,Alarm:0,PID:20.0
        Sun=0000:Liv1,0600:Liv2,2030:Liv2,2345:Liv2
        Mon=0000:Liv2,0600:Liv1,2030:Liv2,2345:Liv2
        Tue=0000:Liv2,0600:Liv1,2030:Liv2,2345:Liv2
        Wed=0000:Liv2,0600:Liv1,2030:Liv2,2345:Liv2
        Thu=0000:Liv2,0600:Liv1,2030:Liv2,2345:Liv2
        Fri=0000:Liv2,0600:Liv1,2030:Liv2,2345:Liv2
        Sat=0000:Liv2,0600:Liv1,2130:Liv2,2345:Liv2
     */
    public String getIniString(){
        String retVal="[Timer"+num+"]\r\n";
        retVal+="Comment=DaImplementare\r\n";
        
        for (int i = 0; i < 3; i++){
            retVal+="Level"+(i+1)+"=";
            retVal+="Digital:"+(Integer)(digitalLevel[i].getValue())+", Analog:"+(Double)(analogLevel[i].getValue())+"\r\n";
        }

        for (int day = 0; day < 7; day++){
            DefaultTableModel tableModel = (DefaultTableModel) tzTable[day].getModel();
            retVal+=DAYS[day]+"=";

            if (tableModel.getRowCount() == 0){
                retVal+="0000:Liv1,2359:Liv1";
            }
            else {
//                for (int row = 0; row < tableModel.getRowCount(); row++){
//                    retVal+=tableModel.getValueAt(row, 0).toString()+":Liv"+tableModel.getValueAt(row, 1).toString()+",";
//                }
                for (int row = 0; row < tableModel.getRowCount(); row++){
                    //Tolgo i due punti perchè non mi servono:
                    String hourStr = tableModel.getValueAt(row, 0).toString();
                    int columnPos = hourStr.indexOf(':');
                    if (columnPos < 0){
                        JOptionPane.showMessageDialog(null, "Errore nei parametri del timer!!\r\nEsco!");
                        return "";
                    }

                    //retVal+=tableModel.getValueAt(row, 0).toString()+"-"+tableModel.getValueAt(row, 1).toString()+" ";
                    retVal+=hourStr.substring(0,columnPos)+hourStr.substring(columnPos+1)+":Liv"+tableModel.getValueAt(row, 1).toString()+",";
                }

                //Tolgo l'ultima virgola...
                retVal = retVal.substring(0, retVal.length()-1);
            }

            retVal+="\r\n";
        }


        retVal+="\r\n";
        return retVal;
    }

    private void CleanTable(DefaultTableModel tableModel) {

        while (tableModel.getRowCount() > 0){
            tableModel.removeRow(0);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
                //Ricavo quale tabella è
        int index = 0;
        for (index = 0; index < 7; index++){
            if (evt.getSource().equals(tzTable[index])){
                break;
            }
        }

        if (index == 7){
            return;
        }

        if (evt.getPropertyName().equalsIgnoreCase("tableCellEditor")){
            //Controllo ogni riga con le precedenti
            if (tzTable[index].getModel().getRowCount()<=1){
                //Ho una riga sola... esco
                return;
            }


            try {
                tzTable[index].getRowSorter().allRowsChanged();
                tzTable[index].invalidate();
            } catch (Exception ex){
                ex.toString();
            }
        }
    }

    /**
     * @param k the k to set
     */
    public void setKernel(Kernel k) {
        this.k = k;
    }

    public boolean parseCmd(Cmd com){
        try {
            if (UtilXML.cmpCmdValue(com, "TIMERID", num)) {
                //Inizio il loop sulle giornate
                for (int day = 0; day < 7; day++) {
                    String dayString;

                    //Prendo la stringa del giorno
                    dayString = com.getValue(DAYS_ANSWER[day]);
                    String timeZoneArray[] = dayString.split(",");

                    //Pulisco la tabella
                    int nOfRows = ((DefaultTableModel) tzTable[day].getModel()).getRowCount();
                    if (nOfRows > 0) {
                        for (int row = 0; row < nOfRows; row++) {
                            ((DefaultTableModel) tzTable[day].getModel()).removeRow(0);
                        }
                    }

                    //In teoria ho la mappa con tutte le zone della giornata -> riempio la tabella
                    for (int tz = 0; tz < timeZoneArray.length; tz++) {
                        String tzToken[] = timeZoneArray[tz].split(":");
                        //in 0 c'è la fascia oraria e in 1 il livello
                        String levelSubString = tzToken[1].substring(3);
                        ((DefaultTableModel) tzTable[day].getModel()).addRow(new Object[][]{});
                        //Suddivido la stringa
                        String hourStr, minuteStr;
                        hourStr = tzToken[0].substring(0, 2);
                        minuteStr = tzToken[0].substring(2);
                        ((DefaultTableModel) tzTable[day].getModel()).setValueAt(hourStr+":"+minuteStr, tz, 0);
                        ((DefaultTableModel) tzTable[day].getModel()).setValueAt(levelSubString, tz, 1);
                    }
                }

                //Converto i livelli
                for (int level = 0; level < 3; level++) {
                    HashMap<String, String> hm;
                    String levelKey = "LEVEL";
                    String levelString;
                    String valString;

                    levelKey += level + 1;

                    levelString = com.getValue(levelKey);
                    hm = createMap(levelString);

                    if (hm.containsKey("Digital")) {
                        valString = hm.get("Digital");

                        if (valString.equalsIgnoreCase("0")) {
                            digitalLevel[level].setValue(0);
                        } else {
                            digitalLevel[level].setValue(1);
                        }
                    }

                    if (hm.containsKey("PID")) {
                        valString = hm.get("PID");

                        analogLevel[level].setValue(new Float(valString));
                    }
                }

                //Controllo se e' abilitato
//                if (com.containsAttribute("ENABLED")) {
//                    if (com.getValue("ENABLED").equalsIgnoreCase("1")) {
//                        btnEnableTimer.setText("Disabilita");
//                    } else {
//                        btnEnableTimer.setText("Abilita");
//                    }
//                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return true;
    }
}
