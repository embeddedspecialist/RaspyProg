package raspyprog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * Splash screen visualizzato per un tempo predefinito all'avvio
 * del programma
 * @author Enrico Strocchi
 */
public class SplashScreen extends JWindow {

    //private static String VERSION = "1.0";
    private static final long serialVersionUID = 3975671720698091112L;
    
    private int duration;
    private Image logo;

    /**
     * Costruttore
     * @param time Tempo di visualizzazione in ms
     */
    public SplashScreen(int time) {
        super();
        duration = time;
        
    }

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash(String version) {

        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);
        

        // Build the splash screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        try {
            //logo = toolkit.getImage(getClass().getResource("resources/splash3.png"));
            logo = toolkit.getImage("./splashRaspyProg.png");
            

            MediaTracker mediaTracker = new MediaTracker(this);
            mediaTracker.addImage(logo, 0);
            try {
                mediaTracker.waitForID(0);
            } catch (InterruptedException ie) {
            }

            //setSize(logo.getWidth(null), logo.getHeight(null));
        } catch (Exception ex) {

        }

        // Set the window's bounds, centering the window
        int width = logo.getWidth(null)+10;
        int height =logo.getHeight(null)+50;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);

        JLabel label = new JLabel(new ImageIcon(logo));
        JLabel copyrt = new JLabel
                ("Version: " + version, JLabel.CENTER);

        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 20));
        //copyrt.setForeground(Color.RED);
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);

        Color oraRed = new Color(156, 20, 20,  255);
        content.setBorder(BorderFactory.createLineBorder(oraRed, 1));

        // Display it
        setVisible(true);

        // Wait a little while, maybe while loading resources
        try { Thread.sleep(duration); } catch (Exception e) {}

        setVisible(false);

    }
    
}
