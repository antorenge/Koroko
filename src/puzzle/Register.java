/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package puzzle;

import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import puzzle.model.LoginModel;


public class Register extends javax.swing.JFrame {
    
    

    public Register() {
        initComponents();
        setLocationRelativeTo(null); 
        getRootPane().setDefaultButton(btnRegister);
        
    }
        
    private void initComponents() {
        
        this.setIconImage(new ImageIcon(getClass().getResource(
				PuzzleProperties.APPLICATION_ICON_FILE)).getImage());
        
        registerPanel = new JPanel();
        
        jLabel1 = new javax.swing.JLabel();
        txtfname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtlname = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(520, 400));
                
        registerPanel.setBackground(new java.awt.Color(47, 28, 18));
        registerPanel.setPreferredSize(new java.awt.Dimension(520, 400));
        
        jLabel1.setFont(new java.awt.Font("DK Crayon Crumble", 0, 36)); // NOI18N
        jLabel1.setText("First Name");
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        
        txtfname.setFont(new java.awt.Font("DK Crayon Crumble", 0, 36)); // NOI18N

        jLabel2.setFont(new java.awt.Font("DK Crayon Crumble", 0, 36)); // NOI18N
        jLabel2.setText("Last Name");
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        
        txtlname.setFont(new java.awt.Font("DK Crayon Crumble", 0, 36)); // NOI18N

        btnRegister.setFont(new java.awt.Font("DK Crayon Crumble", 1, 36)); // NOI18N
        btnRegister.setText("Register");        
        btnRegister.setBackground(new java.awt.Color(47, 28, 18));
        btnRegister.setForeground(new java.awt.Color(255, 255, 255));
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("DK Crayon Crumble", 1, 36)); // NOI18N
        jLabel3.setText("New Pupil Registration");
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        
        btnBack.setFont(new java.awt.Font("DK Crayon Crumble", 1, 36)); // NOI18N
        btnBack.setText("Back");
        btnBack.setBackground(new java.awt.Color(47, 28, 18));
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout registerPanelLayout = new javax.swing.GroupLayout(registerPanel);
        registerPanel.setLayout(registerPanelLayout);
        registerPanelLayout.setHorizontalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addGroup(registerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtfname, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                        .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(btnBack))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtlname, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRegister))))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        registerPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBack, btnRegister});

        registerPanelLayout.setVerticalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel3)
                .addGap(29, 29, 29)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtfname, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtlname, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegister)
                    .addComponent(btnBack))
                .addGap(50, 50, 50))
        );

        registerPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBack, btnRegister});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(registerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(registerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        
        pack();
    }// </editor-fold>                        

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {                                        
        Login.display();
        this.dispose();
    }                                       

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {                                            
        String fn = String.valueOf(txtfname.getText());
        String ln = String.valueOf(txtlname.getText());
        
        if("".equals(fn)||"".equals(ln)){
            Toolkit.getDefaultToolkit().beep(); 
            JOptionPane.showMessageDialog(null, "Sorry! cannot accept a blank record.");
            txtfname.setFocusable(rootPaneCheckingEnabled);
        } else {
            LoginModel lg = new LoginModel();
            try {
                lg.insertUser(fn, ln);
            } catch (SQLException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
                        
        }
        
        txtfname.setText("");
        txtlname.setText("");
        
    }                                           

    /**
     * @param args the command line arguments
     */
    public static void display() {
        /* Set the Nimbus look and feel */
       
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField txtfname;
    private javax.swing.JTextField txtlname;
    private JPanel registerPanel;             
}
