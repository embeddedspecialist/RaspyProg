package nodes.gui;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PanelDevice extends JPanel {

    private static final long serialVersionUID = -262697958511920891L;
    private static Dimension boxdim = new Dimension(250, 450);
    private String title;
    private JComboBox comboTimerID;
    public JButton btnChangeTimer;

    /**
     * Costruttore
     * @param title Titolo attorno al bordo
     */
    public PanelDevice(String title, int nOfTimers) {
        super();
        this.title = title;
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
        setPreferredSize(boxdim);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlarm(false);
        AddTimerCommands(nOfTimers);
    }

    public PanelDevice(String title, Dimension newboxdim, int nOfTimers) {
        super();
        setFont(new Font("sansserif", Font.BOLD, 32));
        this.title = title;
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
        setPreferredSize(newboxdim);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlarm(false);
        AddTimerCommands(nOfTimers);
    }

    public void EnableTimerCommands(boolean enable)
    {
        comboTimerID.setEnabled(enable);
        btnChangeTimer.setEnabled(enable);
    }

    private void AddTimerCommands(int nOfTimers) {

        JPanel commandPanel = new JPanel();

        JLabel labl = new JLabel("Timer:");

        comboTimerID = new JComboBox();

        comboTimerID.addItem("No Timer");
        for (int i = 0; i < nOfTimers; i++) {
            comboTimerID.addItem("Timer "+(i+1));
        }

        btnChangeTimer = new JButton();
        btnChangeTimer.setText("Imposta");

        //commandPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        //commandPanel.setPreferredSize(new Dimension(0,0));
        
        if (nOfTimers != 0) {
            commandPanel.add(labl);
            commandPanel.add(comboTimerID);
            commandPanel.add(btnChangeTimer);

            add(commandPanel);
        }

    }

    public void setComboTimerIndex(int index){
        try{
            comboTimerID.setSelectedIndex(index);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    /**
     * Attiva o disattiva la segnalazione di un allarme sul pannello
     * @param b true allarme attivato
     */
    public void setAlarm(boolean b) {

        TitledBorder tb;
        if (b) {
            tb = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.RED, Color.MAGENTA), title);
        } else {
           tb = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title);
        }
        tb.setTitleFont(new Font("sansserif", Font.BOLD, 12));
        setBorder(tb);
    }

    public void AddTimerCommandsActionListener(ActionListener newAL){
        btnChangeTimer.addActionListener(newAL);
    }

    public int getTimerID() {
        return comboTimerID.getSelectedIndex();
    }
}
