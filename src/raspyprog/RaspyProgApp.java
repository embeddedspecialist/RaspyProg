/*
 * AFOVisualConfiguratorApp.java
 */

package raspyprog;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class RaspyProgApp extends SingleFrameApplication {

    public static  String VERSION = "1.5";
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        setFontSizeGlobal(12);
        show(new RaspyProgView(this));
    }
    
    public static void setFontSizeGlobal( int size )
    {
        for ( Enumeration e = UIManager.getDefaults().keys(); e.hasMoreElements(); )
        {
            Object key   = e.nextElement();
            Object value = UIManager.get( key );

            if ( value instanceof Font )
            {
                Font f = (Font) value;

                UIManager.put( key, new FontUIResource( f.getName(), Font.PLAIN, size ) );
            }
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of AFOVisualConfiguratorApp
     */
    public static RaspyProgApp getApplication() {
        return Application.getInstance(RaspyProgApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        SplashScreen splash = new  SplashScreen(5000);

        splash.showSplash(VERSION);
        launch(RaspyProgApp.class, args);
    }
}
