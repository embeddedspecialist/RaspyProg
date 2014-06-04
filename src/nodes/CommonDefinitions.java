/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;

/**
 *
 * @author amirrix
 */
public interface CommonDefinitions {
    
    //Azioni e define per i moduli del sistema

    //ATTENZIONE: mantenere VLV come ultimo dei driver per la corretta creazione dei menu
    public static enum blockTypes {Master,Temp,IOD4, DI8,DO8,AI,AO,THU,Subsystem,IN,OUT,IF,
                                 Arithmetic,Clock,Logic,PID, Saturation,Hysteresys,Counter,
                                 Costant, Gate,Mux,BinaryEncoder,BinaryDecoder,C3PointCtrl,Trigger,
                                 Delay, Timer,ClimaticCurve,Comment, Scope};
    public static enum ifTypes {IF_EQ, IF_LT, IF_GT, IF_GET, IF_LET,IF_NE};
    public static final String ifStrings[] = {
        "==",
        "<",
        ">",
        ">=",
        "<=",
        "!="
    };
    
    public static enum arithmeticTypes {ADD, SUB, MUL, DIV, REM, MAX, MIN};
    public static final String arithmeticStrings[] = {
        "+",
        "-",
        "*",
        "/",
        "REM",
        "MAX",
        "MIN"
    };
    
    public static enum logicTypes {AND, OR, NOT, XOR, NAND, NOR, XNOR};
    
//    public static enum pidTypes {PID_PF, PID_LM};

    public static enum saturationTypes {BOTH, LOW,HIGH};
    
    public static enum subBlockTypes {NONE, IF_EQ, IF_LT, IF_GT, IF_GET, IF_LET,IF_NE,
                                      ADD, SUB, MUL, DIV, REM, MAX, MIN,
                                      AND, OR, NOT, XOR, NAND, NOR, XNOR,
                                      PID_PF, PID_LM,
                                      MUX_2, MUX_4, MUX_8, MUX_16 };
    public static final String subBlockStrings[] = {
        "None",
        "==",
        "<",
        ">",
        ">=",
        "<=",
        "!=",
        "+",
        "-",
        "*",
        "/",
        "REM",
        "MAX",
        "MIN",
        "AND",
        "OR",
        "NOT",
        "XOR",
        "NAND",
        "NOR",
        "XNOR",
        "PF",
        "LM",
        "MUX_2",
        "MUX_4",
        "MUX_6",
        "RISE",
        "FALL",
    };

    public static enum triggerTypes {RISE,FALL,LEV_HI, LEV_LOW};

    public static enum muxTypes {MUX_2, MUX_4, MUX_8, MUX_16};

//    public static enum blockTypes {Master, TEMP, IOD4, DI8, DO8, AI, AO, THU, HUM, LUX, VLVL, IF, ARITH, CLOCK, LOGIC, PID, SATURATION, HYST, COUNTER,
//                                   COSTANT, GATE, TIMER};
    public static final String DELETE_OBJECT = "Delete Object";
    public static final String ROTATE_NODE = "Rotate Node";
    public static final String EDIT_NODE = "Edit Node";
    public static final String EXPORT_NODE = "Export Node";
    public static final String SHOW_CHART = "Show Chart";

    //Bordi
    public static final Border BORDER_4 = BorderFactory.createEmptyBorder (4);
    public static final Border BORDER_FILL_LIGHTYELLOW = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(50, 50, 155), Color.YELLOW);
    public static final Border BORDER_FILL_DARKYELLOW = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(60, 60, 205), Color.YELLOW);
    public static final Border BORDER_LINE = BorderFactory.createRoundedBorder(0, 0,Color.getHSBColor(0, 0, 1), Color.BLUE);
    public static final Border BORDER_EMPTY = BorderFactory.createEmptyBorder();

    
    public static final String DEFAULT_NODE_COMMENT = "Comment";
    public static final String PROJ_DEFAULT_EXTENSION = ".prj";
    public static final String SUBSYST_DEFAULT_EXTENSION = ".sub";
    
    public static enum configFileVars {
        CONF_UPDATETIME,
        CONF_NOF_INTERFACEPORTS,
        CONF_INTERFACEPORT,
        CONF_NOF_OUTPORTS,
        CONF_OUTPORT,
        CONF_NOF_INPORTS,
        CONF_INPORT,
        CONF_NUMBEROFNETS,
        CONF_DEFAULTTEMPALARMS,
        CONF_ENABLELOG,
        CONF_LOGINTERVAL,
        CONF_LOGFILENAME,
        CONF_LOGMAXFILEDIM,
        CONF_DODEBUG,
        CONF_WAITONSTARTUP,
//        CONF_ENABLE_ERRORLOG,
//        CONF_ERRORFILENAME,
//        CONF_ERRORMAXFILEDIM,
//        CONF_USEWATCHDOG,
        CONF_NETCOMPORT,
        CONF_NETWL,
        CONF_NETDELAY,
        CONF_NETDEFAULTTEMPALARMS,
        CONF_NETSWTEMPALARMS,
        CONF_NETTIMERID,
        CONF_NETDIGOUTDELAY,
        CONF_NETNUMBEROFDEVICES,
//        CONF_NETTEMPUPDATEINTERVAL,
//        CONF_NETDIGUPDATEINTERVAL,
//        CONF_NETANALOGUPDATEINTERVAL,
//        CONF_NETLUXUPDATEINTERVAL,
//        CONF_NETHUMUPDATEINTERVAL,
//        CONF_NETUPIDUPDATEINTERVAL,
//        CONF_NOF_ADDRESS_ENTRIES,
        CONF_NOF_SUBSYSTEMS,
        CONF_NOF_BLOCKS,
        CONF_BLK_UPTIME,
        CONF_TOTALNUMBEROFPARAM 
    };
    
    public static final String config_Strings[] =
    {
        "UpdateTime",
        "NPorteInterfaccia",
        "PortaInterfaccia",
        "NPorteComOut",
        "PortaComunicazioneOut",
        "NPorteComIn",
        "PortaComunicazioneIn",
        "TotalNets",
        "SoglieDiAllarme",
        "AbilitaLog",
        "IntervalloLog",
        "LogFile",
        "MAXDimensioneFile",
        "DODEBUG",
        "AttendiInAvvio",
//        "AbilitaLogErrori",
//        "ErrorLogFile",
//        "MaxDimErrori",
//        "UsaWatchDog",
        "PortaComunicazione",
        "Wireless",
        "RitardoNet",
        "SoglieDiAllarme",
        "AllarmiTemperaturaSw",
        "TimerID",
        "TempoScatto",
        "NofDev",
//        "TAggTemp",
//        "TAggDig",
//        "TAggAna",
//        "TAggLux",
//        "TAggUmid",
//        "TAggUPID",
//        "nOfAddresses",
        "TotalSubSystems",
        "NofBlocks",
        "UpdateTime",
    };
    
    ////////////////
    //TIMERS
    //////////////
    public static final String CALENDAR_COLUMN_SELECTED = "calendarColumnSelected";
    public static final String CALENDAR_CELL_SELECTED = "calendarCellSelected";
    public static final int MAX_NUM_TZ = 12;
    
    public static enum e_TimerValues {
        TIMERVAL_DIGITAL,
        TIMERVAL_ANALOG,
        TIMERVAL_SETPOINT,
        TIMERVAL_NUMTOT,
    } ;

    public static final String timerValsString[] =
    {
        "Digital",
        "Analog",
        "SetPoint"
    };

    public class TimerStruct
    {   
        //Global levels for the timer
        int levelsMatrix[][] = new int[3][e_TimerValues.TIMERVAL_NUMTOT.ordinal()+1]; 

        //Global matrix with all the days in the week
        List<String> timerDay = new ArrayList<String>();
        
        boolean isValid = true;

        public TimerStruct() {
            
            for (int i = 0; i < 7; i++){
                String defaultTimerDay = "0000:Lev1,2359:Lev1";
                timerDay.add(defaultTimerDay);
            }
            
        }
    };

    public static enum daysInWeek
    {
        SUN,
        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT,
        TOTALDAYS
    };

    public static final String daysStrings[] = 
    {
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    };
}


