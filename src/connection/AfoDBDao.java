/*
 * AddressDao.java
 *
 * Copyright 2006 Sun Microsystems, Inc. ALL RIGHTS RESERVED Use of 
 * this software is authorized pursuant to the terms of the license 
 * found at http://developers.sun.com/berkeley_license.html .

 */

package connection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Il database e' organizzato su 4 tabelle: una primaria che contiene:
 * - NET, Indirizzo e commento del device
 * Le altre 3 contenegono un singolo valore di temperatura, analogico o digitale,
 * il campo address che serve per ritrovare i dati e una label
 * Esiste una quinta tabella corrispondene agli errori
 * @author Alessandro Mirri
 * 22/09/2009 -- Adapted by Alessandro Mirri
 */
public class AfoDBDao {

    public static final String dbSystemLocation = "./db";
    private String dbName;
    private PreparedStatement stmtSaveNewTempRecord;
    private PreparedStatement stmtSaveNewAnalogRecord;
    private PreparedStatement stmtSaveNewDigitalRecord;
    private PreparedStatement stmtSaveNewDeviceRecord;

    private Connection dbConnection;
    private Properties dbProperties;
    private boolean isConnected = false;

    private boolean isEnabled = true;

    public static enum TimeDiscretization {
      SECOND,
      MINUTE,
      HOUR,
      DAY
    };

    public static final int DEVICES_TABLE = 0;
    public static final int TEMPERATURE_TABLE = 1;
    public static final int DIGITALIO_TABLE = 2;
    public static final int ANALOGIO_TABLE = 3;
    public static final int ERRORS_TABLE = 4;

    private static final String[] strTableNames = {
      "DEVICES",
      "TEMPERATURES",
      "DIGITALIOS",
      "ANALOGIOS",
      "ERRORS"
    };

    public boolean isDBEnabled(){
        return isEnabled;
    }

    public void enableDB(boolean enable){
        isEnabled = enable;
    }

    private static final String strCreateDataTablePrefix = "create table APP.";
    private static final String strCreateDataTableSuffix =
            " (" +
            "    ID          INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "    ADDRESS     INTEGER, " +
            "    LABEL       VARCHAR(256), " +
            "    TIME        TIMESTAMP, " +
            "    VALUE       REAL " +
            ")";

    private static final String strCreateDevicesTable = "create table APP.DEVICES"+
            " (" +
            "    ID          INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "    NET         VARCHAR(8), " +
            "    ADDRESS     INTEGER, " +
            "    COMMENT     VARCHAR(256) " +
            ")";

    private static final String strCreateErrorsTable = "create table APP.ERRORS"+
            " (" +
            "    ID          INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "    NET         INTEGER, " +
            "    DEVICE      INTEGER, " +
            "    DESCRIPTION     VARCHAR(256) " +
            "    TIME        TIMESTAMP" +
            ")";

    private static final String strSaveError = "INSERT INTO APP.ERRORS"+
            "   (NET, DEVICE, DESCRIPTION, TIME) " +
            "VALUES (?, ?, ?, ?)";

    private static final String strSaveDevice ="INSERT INTO APP.DEVICES"+
            "   (NET, ADDRESS, COMMENT) " +
            "VALUES (?, ?, ?)";
    private static final String strSaveTemp ="INSERT INTO APP.TEMPERATURES"+
            "   (ADDRESS, LABEL, TIME, VALUE) " +
            "VALUES (?, ?, ?, ?)";
    private static final String strSaveAnal ="INSERT INTO APP.ANALOGIOS"+
            "   (ADDRESS, LABEL, TIME, VALUE) " +
            "VALUES (?, ?, ?, ?)";
    private static final String strSaveDigital ="INSERT INTO APP.DIGITALIOS"+
            "   (ADDRESS, LABEL, TIME, VALUE) " +
            "VALUES (?, ?, ?, ?)";

    private static final String strGetAllEntriesFromTablePrefix =
            "SELECT ID, ADDRESS, LABEL, TIME, VALUE FROM APP.";
    private static final String strGetAllEntriesFromTableSuffix =
            " ORDER BY ADDRESS ASC, TIME ASC ";

    private static final String strGetDevices =
            "SELECT DISTINCT NET, ADDRESS, COMMENT FROM APP.DEVICES ORDER BY NET ASC, ADDRESS ASC";

    private static final String strDeleteSingleValPrefix =
            "DELETE FROM APP.";
    private static final String strDeleteSingleValSuffix =
            " WHERE ID = ";

    private static final String strCleanDB = "DELETE FROM APP.";

    /** Creates a new instance of AddressDao */
    public AfoDBDao() {
        this("AFODatabase");
    }
    
    public AfoDBDao(String databaseName) {
        this.dbName = databaseName;
        
        setDBSystemDir();
        dbProperties = loadDBProperties();
        String driverName = dbProperties.getProperty("derby.driver","org.apache.derby.jdbc.EmbeddedDriver");
        loadDatabaseDriver(driverName);
        if(!dbExists()) {
            createDatabase();
        }
        
    }
    
    private boolean dbExists() {
        boolean bExists = false;
        String dbLocation = getDatabaseLocation();
        File dbFileDir = new File(dbLocation);
        if (dbFileDir.exists()) {
            bExists = true;
        }
        return bExists;
    }
    
    private void setDBSystemDir() {
        // decide on the db system directory
        //String userHomeDir = System.getProperty("user.home", ".");
        String userHomeDir = ".";
        String systemDir = dbSystemLocation;
        System.setProperty("derby.system.home", systemDir);
        
        // create the db system directory
        File fileSystemDir = new File(systemDir);
        fileSystemDir.mkdir();
    }
    
    private void loadDatabaseDriver(String driverName) {
        // load Derby driver
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private Properties loadDBProperties() {
        InputStream dbPropInputStream = null;
        dbPropInputStream = AfoDBDao.class.getResourceAsStream("Configuration.properties");
        dbProperties = new Properties();
        if (dbPropInputStream != null)
        {
          try {
              dbProperties.load(dbPropInputStream);
          } catch (IOException ex) {
              ex.printStackTrace();
          }
        }

        return dbProperties;
    }
    
    private boolean createTables(Connection dbConnection) {
        boolean bCreatedTables = false;
        Statement statement = null;
        try {
            statement = dbConnection.createStatement();
            //Creo devices
            statement.execute(strCreateDevicesTable);
            //Creo dati
            for (int i = 1; i < strTableNames.length; i++)
            {
              String creaDb = strCreateDataTablePrefix + strTableNames[i] + strCreateDataTableSuffix;
              statement.execute(creaDb);
            }

            bCreatedTables = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return bCreatedTables;
    }
    private boolean createDatabase() {
        boolean bCreated = false;
        
        String dbUrl = getDatabaseUrl();
        dbProperties.put("create", "true");
        
        try {
            dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
            bCreated = createTables(dbConnection);
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        dbProperties.remove("create");
        return bCreated;
    }
    
    public boolean connect() {
        String dbUrl = getDatabaseUrl();
        if (isConnected)
        {
          return true;
        }
        try {
            dbProperties.clear();
            dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
            stmtSaveNewTempRecord = dbConnection.prepareStatement(strSaveTemp, Statement.RETURN_GENERATED_KEYS);
            stmtSaveNewAnalogRecord = dbConnection.prepareStatement(strSaveAnal, Statement.RETURN_GENERATED_KEYS);
            stmtSaveNewDigitalRecord = dbConnection.prepareStatement(strSaveDigital, Statement.RETURN_GENERATED_KEYS);

            stmtSaveNewDeviceRecord = dbConnection.prepareStatement(strSaveDevice, Statement.RETURN_GENERATED_KEYS);
            
            isConnected = dbConnection != null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Attenzione: il programma è già in esecuzione!");
            System.exit(1);
            ex.printStackTrace();
            isConnected = false;
        }
        return isConnected;
    }
    
    private String getHomeDir() {
        return System.getProperty("user.home");
    }
    
    public void disconnect() {
        if(isConnected) {
            String dbUrl = getDatabaseUrl();
            dbProperties.put("shutdown", "true");
            try {
                DriverManager.getConnection(dbUrl, dbProperties);
            } catch (SQLException ex) {
            }
            isConnected = false;
        }
    }
    
    public String getDatabaseLocation() {
        String dbLocation = dbSystemLocation + "/" + dbName;
        return dbLocation;
    }
    
    public String getDatabaseUrl() {
        String dbUrl = "jdbc:derby:" + dbName;
        return dbUrl;
    }
    

//    public int saveDeviceRecord(GenericDevice device) {
//        int id = -1;
//        if (!isEnabled)
//        {
//            return -1;
//        }
//
//        try {
//
//          stmtSaveNewDeviceRecord.clearParameters();
//
//          //NetName, Address, comment, time, value
//          stmtSaveNewDeviceRecord.setString(1,device.getNetName());
//          stmtSaveNewDeviceRecord.setInt(2, device.getAddress());
//          stmtSaveNewDeviceRecord.setString(3, device.getComment());
//
//          stmtSaveNewDeviceRecord.executeUpdate();
//          ResultSet results = stmtSaveNewDeviceRecord.getGeneratedKeys();
//          if (results.next()) {
//              id = results.getInt(1);
//          }
//
//        } catch(SQLException sqle) {
//            sqle.printStackTrace();
//        }
//        return id;
//    }
//
//
//    /**
//     * Checks if a device with the given address already exists in the device list
//     * @param address
//     * @return true if the device exists
//     */
//    public boolean existsDevice(int address)
//    {
//      boolean exists = false;
//      try
//      {
//        //Faccio una query per cercare tutti i dispositivi con questo indirizzo
//        String query = "SELECT * FROM APP.DEVICES WHERE ADDRESS="+Integer.toString(address);
//        Statement st = dbConnection.createStatement();
//        ResultSet results = null;
//
//        results = st.executeQuery(query);
//
//        exists = results.next();
//      }
//      catch(SQLException ex)
//      {
//        ex.printStackTrace();
//      }
//
//      return exists;
//    }
//
//    /**
//     * Salva il record passato inserendo nel campo label la label fornita
//     * @param record il record da salvare
//     * @param label la label con cui viene registrato
//     * @param tableIdx indice della tabella dove salvarlo
//     * @return il nuovo ID
//     */
//    public int saveSingleDataEntryRecord(SingleValDataEntry data, int tableIdx) {
//        int id = -1;
//        try {
//          PreparedStatement st = null;
//          if (tableIdx == TEMPERATURE_TABLE)
//          {
//            st = stmtSaveNewTempRecord;
//          }
//          else if (tableIdx == ANALOGIO_TABLE)
//          {
//            st = stmtSaveNewAnalogRecord;
//          }
//          else if (tableIdx == DIGITALIO_TABLE)
//          {
//            st = stmtSaveNewDigitalRecord;
//          }
//
//
//          if (st == null)
//          {
//            return -1;
//          }
//
//            st.clearParameters();
//
//            //Address, Label, time, value
//            //Address, Label, time, value
//            st.setInt(1,data.getAddress());
//            st.setString(2, data.getComment());
//            st.setTimestamp(3, data.getTimestamp());
//            st.setFloat(4, data.getValue());
//
//            st.executeUpdate();
//            ResultSet results = st.getGeneratedKeys();
//            if (results.next()) {
//                id = results.getInt(1);
//            }
//
//        } catch(SQLException sqle) {
//            sqle.printStackTrace();
//        }
//        return id;
//    }
//
//    public boolean cleanDBData()
//    {
//      boolean bCleaned = false;
//      try {
//
//        for (int i = 1; i < strTableNames.length; i++)
//        {
//          String command = strCleanDB+strTableNames[i];
//          Statement st = dbConnection.createStatement();
//          st.executeUpdate(command);
//        }
//
//        bCleaned = true;
//      }catch (SQLException sqle)
//      {
//        sqle.printStackTrace();
//      }
//
//      return bCleaned;
//    }
//
//    public boolean deleteRecord(int id, String tableName) {
//        boolean bDeleted = false;
//        try {
//          String command = strDeleteSingleValPrefix + tableName + strDeleteSingleValSuffix + Integer.toString(id);
//          Statement st = dbConnection.createStatement();
//          st.executeUpdate(command);
//            bDeleted = true;
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
//        }
//
//        return bDeleted;
//    }
//
//    public List<SingleValDataEntry> getTableDataByAddressLabelAndTime(int address, String label, int tableIdx, AfoDBDao.TimeDiscretization interval) {
//
//       List<SingleValDataEntry> listEntries = new ArrayList<SingleValDataEntry>();
//        Statement queryStatement = null;
//        ResultSet results = null;
//
//        String strTimeInterval = "YEAR(TIME), MONTH(TIME), DAY(TIME), HOUR(TIME), MINUTE(TIME), SECOND(TIME), ";
//
//        switch (interval)
//        {
//          case SECOND:{
//            strTimeInterval = "YEAR(TIME), MONTH(TIME), DAY(TIME),HOUR(TIME), MINUTE(TIME), SECOND(TIME), ";
//          };break;
//          case MINUTE: {
//            strTimeInterval = "YEAR(TIME), MONTH(TIME), DAY(TIME), HOUR(TIME), MINUTE(TIME), ";
//          };break;
//          case HOUR: {
//            strTimeInterval = "YEAR(TIME), MONTH(TIME), DAY(TIME), HOUR(TIME), ";
//          };break;
//          case DAY:{
//            strTimeInterval = "YEAR(TIME), MONTH(TIME), DAY(TIME),";
//          };break;
//
//        }
//
//        try {
//            String query = "SELECT DISTINCT "+strTimeInterval+
//                           " VALUE"+
//                           " FROM APP."+strTableNames[tableIdx] +
//                           " WHERE ADDRESS = " + Integer.toString(address) +
//                           " AND LABEL = '" + label +"' ";
//
//            queryStatement = dbConnection.createStatement();
//            results = queryStatement.executeQuery(query);
//
//            while(results.next()) {
//
//              java.sql.Timestamp timeStamp;
//              String strTime="1970-01-01 00:00:00.0";
//              float value;
//
//
//              //yyyy-mm-dd hh:mm:ss.fffffffff
//              Formatter fmt = new Formatter();
//
//              switch (interval)
//              {
//                case SECOND:{
//                  fmt.format("%4d-%02d-%02d %02d:%02d:%02d.%09d", results.getInt(1),
//                                                     results.getInt(2),
//                                                     results.getInt(3),
//                                                     results.getInt(4),
//                                                     results.getInt(5),
//                                                     results.getInt(6),
//                                                     0);
//                  strTime = fmt.toString();
//
//
//                };break;
//                case MINUTE: {
//                  fmt.format("%4d-%02d-%02d %02d:%02d:%02d.%09d", results.getInt(1),
//                                                     results.getInt(2),
//                                                     results.getInt(3),
//                                                     results.getInt(4),
//                                                     results.getInt(5),
//                                                     0,
//                                                     0);
//                  strTime = fmt.toString();
//                };break;
//                case HOUR: {
//                   fmt.format("%4d-%02d-%02d %02d:%02d:%02d.%09d", results.getInt(1),
//                                                     results.getInt(2),
//                                                     results.getInt(3),
//                                                     results.getInt(4),
//                                                     0,
//                                                     0,
//                                                     0);
//                  strTime = fmt.toString();
//
//                };break;
//                case DAY:{
//                   fmt.format("%4d-%02d-%02d %02d:%02d:%02d.%09d", results.getInt(1),
//                                                     results.getInt(2),
//                                                     results.getInt(3),
//                                                     0,
//                                                     0,
//                                                     0,
//                                                     0);
//                  strTime = fmt.toString();
//                };break;
//
//              }
//
//
//              timeStamp = Timestamp.valueOf(strTime);
//              value = results.getFloat("VALUE");
//              SingleValDataEntry entry = new SingleValDataEntry(0, "",address, label, timeStamp, value);
//              listEntries.add(entry);
//            }
//
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
//
//        }
//
//        return listEntries;
//    }
//
//    /**
//     * Trova tutti i device nella apposita tabella
//     * @return
//     */
//    public List<SingleValDataEntry> getDevicesList() {
//        List<SingleValDataEntry> listEntries = new ArrayList<SingleValDataEntry>();
//        Statement queryStatement = null;
//        ResultSet results = null;
//
//        try {
//          connect();
//          String query = strGetDevices;
//            queryStatement = dbConnection.createStatement();
//            results = queryStatement.executeQuery(query);
//            while(results.next()) {
//                int id = 0;
//                String netName = results.getString(1);
//                int address = results.getInt(2);
//                String comment = results.getString(3);
//                java.sql.Timestamp timestamp = new Timestamp(0);
//                float value = 0.0f;
//
//                SingleValDataEntry entry = new SingleValDataEntry(id, netName,address, comment, timestamp, value);
//                listEntries.add(entry);
//            }
//
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
//
//        }
//
//        return listEntries;
//    }



    public List<String> getUniqueDataLabels(int address, int tableIndex) {
        List<String> listEntries = new ArrayList<String>();
        Statement queryStatement = null;
        ResultSet results = null;

        try {
          connect();
          String query = "SELECT DISTINCT LABEL FROM APP."+strTableNames[tableIndex]+
                         " WHERE ADDRESS=" + Integer.toString(address);
            queryStatement = dbConnection.createStatement();
            results = queryStatement.executeQuery(query);
            while(results.next()) {
                String label = results.getString(1);
                listEntries.add(label);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();

        }

        return listEntries;
    }

    public boolean isConnected() {
        return isConnected;
    }
    
    
}
