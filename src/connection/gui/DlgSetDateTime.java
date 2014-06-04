/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgSetDateTime.java
 *
 * Created on 22-giu-2010, 9.47.51
 */

package connection.gui;

import connection.Kernel;
import connection.XMLCommands.Cmd;
import connection.XMLCommands.UtilXML;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author amirrix
 */
public class DlgSetDateTime extends javax.swing.JDialog implements ActionListener {

    Kernel ker;

    /** Creates new form DlgSetDateTime */
    public DlgSetDateTime(java.awt.Frame parent, boolean modal, Kernel ker) {
        super(parent, modal);
        initComponents();
        this.ker = ker;
        btnReloadDate.setEnabled(false);
        btnReloadDate.addActionListener(this);
        btnChangeDate.setEnabled(false);
        btnChangeDate.addActionListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHour = new javax.swing.JTextField();
        btnChangeDate = new javax.swing.JButton();
        btnReloadDate = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(raspyprog.RaspyProgApp.class).getContext().getResourceMap(DlgSetDateTime.class);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtDate.setColumns(15);
        txtDate.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtDate.setText(resourceMap.getString("txtDate.text")); // NOI18N
        txtDate.setName("txtDate"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtHour.setColumns(5);
        txtHour.setText(resourceMap.getString("txtHour.text")); // NOI18N
        txtHour.setName("txtHour"); // NOI18N

        btnChangeDate.setText(resourceMap.getString("btnChangeDate.text")); // NOI18N
        btnChangeDate.setName("btnChangeDate"); // NOI18N

        btnReloadDate.setText(resourceMap.getString("btnReloadDate.text")); // NOI18N
        btnReloadDate.setName("btnReloadDate"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnReloadDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeDate))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChangeDate)
                    .addComponent(btnReloadDate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtVersion.setColumns(10);
        txtVersion.setEditable(false);
        txtVersion.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtVersion.setText(resourceMap.getString("txtVersion.text")); // NOI18N
        txtVersion.setName("txtVersion"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(299, 299, 299)
                        .addComponent(btnClose))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgSetDateTime dialog = new DlgSetDateTime(new javax.swing.JFrame(), true, null);
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
    public javax.swing.JButton btnChangeDate;
    public javax.swing.JButton btnClose;
    public javax.swing.JButton btnReloadDate;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JTextField txtDate;
    public javax.swing.JTextField txtHour;
    public javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables

        private boolean checkDateAndTime(String dateStrings[], String hourStrings[]) {
        if ((dateStrings.length != 3) || (hourStrings.length != 2)) {
            return false;
        }

        if ((new Integer(dateStrings[0]) < 0) || (new Integer(dateStrings[0]) > 31)) {
            return false;
        }

        if ((new Integer(dateStrings[1]) < 0) || (new Integer(dateStrings[1]) > 12)) {
            return false;
        }

        if ((new Integer(dateStrings[2]) < 2008) || (new Integer(dateStrings[1]) > 2038)) {
            return false;
        }

        if ((new Integer(hourStrings[0]) < 0) || (new Integer(hourStrings[0]) > 23)) {
            return false;
        }

        if ((new Integer(hourStrings[1]) < 0) || (new Integer(hourStrings[1]) > 59)) {
            return false;
        }

        return true;
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChangeDate) {
            String dateTimeString, dateStrings[], hourStrings[];
            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "SetDateAndTime");
            dateStrings = txtDate.getText().split("/");
            hourStrings = txtHour.getText().split(":");
            if (!checkDateAndTime(dateStrings, hourStrings)) {
                JOptionPane.showMessageDialog(null, "Campo non valido in data o ora\r\n" +
                        "Il formato data deve essere gg/mm/aaaa\r\nIl formato ora deve essere hh:mm", "Errore", JOptionPane.ERROR_MESSAGE);
            } else {
                //Costruisco la stringa
                dateTimeString = dateStrings[1] + dateStrings[0] + hourStrings[0] + hourStrings[1] + dateStrings[2];
                com.putValue("DATE", dateTimeString);
                ker.sendCommand(com.toString());
            }
        } else if (e.getSource() == btnReloadDate) {
            Cmd com = new Cmd("DEVICE");
            com.putValue("COMMAND", "GetDateAndTime");
            ker.sendCommand(com.toString());
        }
    }

    public boolean parseCmd(Cmd com) {

//		 TODO Auto-generated method stub
        if (UtilXML.cmpCmdName(com, "STATUS")) {
            if (UtilXML.cmpCmdValue(com, "TYPE", "DateAndTime")) {
                //Devo spacchettare la stringa
                String dateTimeString = com.getValue("DATE");
                String date, hour;

                date = dateTimeString.substring(2, 4) + "/" + dateTimeString.substring(0, 2) + "/" + dateTimeString.substring(8);
                hour = dateTimeString.substring(4, 6) + ":" + dateTimeString.substring(6, 8);

                txtDate.setText(date);
                txtHour.setText(hour);

                String version = com.getValue("VERSION");
                if (version != null) {
                    txtVersion.setText(version);
                }

                return true;
            }
        }

        return false;
    }

}
