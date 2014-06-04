/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SubSystemDlg.java
 *
 * Created on 30-dic-2009, 15.57.41
 */

package nodes.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 *
 * @author amirrix
 */
public class SubSystemDlg extends javax.swing.JDialog {

    /** Creates new form SubSystemDlg */
    public SubSystemDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        center();
    }

    /**
     * Centra il frame nello schermo
     */
    private void center(){
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle frame = getBounds();
            setLocation((screen.width - frame.width)/2, (screen.height - frame.height)/2);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        mainPanel.setName("mainPanel"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SubSystemDlg dialog = new SubSystemDlg(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

}
