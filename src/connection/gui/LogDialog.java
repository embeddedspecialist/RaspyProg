/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogDialog.java
 *
 * Created on 23-gen-2010, 17.03.47
 */

package connection.gui;

import java.util.Date;
import javax.swing.JScrollBar;

/**
 *
 * @author amirrix
 */
public class LogDialog extends javax.swing.JDialog {

    public static int ALLMSG = 0;
    public static int OUTMSG = 1;
    public static int INMSG = 2;

    /** Creates new form LogDialog */
    public LogDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Logs");
//        setResizable(false);
        checkShowIN.setSelected(true);
        checkShowOUT.setSelected(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollLog = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnClear = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        checkShowIN = new javax.swing.JCheckBox();
        checkShowOUT = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        scrollLog.setName("scrollLog"); // NOI18N

        txtLog.setColumns(20);
        txtLog.setEditable(false);
        txtLog.setRows(5);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(raspyprog.RaspyProgApp.class).getContext().getResourceMap(LogDialog.class);
        txtLog.setText(resourceMap.getString("txtLog.text")); // NOI18N
        txtLog.setName("txtLog"); // NOI18N
        scrollLog.setViewportView(txtLog);

        btnClear.setIcon(resourceMap.getIcon("btnClear.icon")); // NOI18N
        btnClear.setText(resourceMap.getString("btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        checkShowIN.setText(resourceMap.getString("checkShowIN.text")); // NOI18N
        checkShowIN.setName("checkShowIN"); // NOI18N

        checkShowOUT.setText(resourceMap.getString("checkShowOUT.text")); // NOI18N
        checkShowOUT.setName("checkShowOUT"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollLog, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnClear)
                            .addGap(18, 18, 18)
                            .addComponent(checkShowIN)
                            .addGap(18, 18, 18)
                            .addComponent(checkShowOUT))
                        .addComponent(btnClose, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(checkShowIN)
                    .addComponent(checkShowOUT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollLog, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addGap(23, 23, 23)
                .addComponent(btnClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtLog.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    public void addMsg(String message, int direction){

        if ( ((direction == OUTMSG) && (checkShowOUT.isSelected())) ||
             ((direction == INMSG) && (checkShowIN.isSelected())) ) {
                        // Determine whether the scrollbar is currently at the very bottom position.
            JScrollBar vbar = scrollLog.getVerticalScrollBar();
            boolean autoScroll = false;
            
            if ( ((vbar.getValue() + vbar.getVisibleAmount()) >= vbar.getMaximum()-50)){
                autoScroll = true;
            }

            Date data = new Date();

            txtLog.append(data.toString()+" "+message+"\n");

            // now scroll if we were already at the bottom.
            if( autoScroll ) txtLog.setCaretPosition( txtLog.getDocument().getLength() );
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LogDialog dialog = new LogDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox checkShowIN;
    private javax.swing.JCheckBox checkShowOUT;
    private javax.swing.JScrollPane scrollLog;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables

}
