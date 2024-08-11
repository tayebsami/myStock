/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mystock;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;


/**
 *
 * @author air
 */
public class Ventes extends javax.swing.JFrame {

    /**
     * Creates new form Ventes
     */
    public Ventes() {
        initComponents();
         Connect();
           Vendeur();
    }
    Connection connection;
    PreparedStatement pst;
     PreparedStatement pst1;
    PreparedStatement pst2;
    DefaultTableModel df;
    ResultSet rs;
    
    public Connection Connect()
    {
        try{     
         Class.forName("com.mysql.jdbc.Driver");
          connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myStock", "root", "conan-kun"); 
         System.out.println("connected");
         return connection ;

            }catch(Exception e)
            { 
                e.printStackTrace();
                System.out.println("not connected");
                return null;
            }  
    }
    public void barcode()
    {
        try {
            String pcode = barcodeVtxt.getText();
  
            pst = connection.prepareStatement("SELECT * FROM product WHERE barcode = ?");
            pst.setString(1, pcode);
            rs = pst.executeQuery();
            
           if(rs.next() == false)
           {
               JOptionPane.showMessageDialog(this, "BarCode Not Found");
               barcodeVtxt.setText("");
           }
            else
               
           {
               String pname = rs.getString("pname");
               String prix = rs.getString("prix_vente");
               
                 nomVtxt.setText(pname.trim());
                 prixVtxt.setText(prix.trim());
                 quantiteVtxt.requestFocus(); 
           }
    
        } catch (SQLException ex) {
            Logger.getLogger(Ventes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Vendeur()
    {
        
        try {
            pst = connection.prepareStatement("SELECT Distinct name from vendor");
            rs = pst.executeQuery();
            
            vendeurSelect.removeAllItems();

            while(rs.next())
            {
                vendeurSelect.addItem(rs.getString("name"));
            }
  
        } catch (SQLException ex) {
            Logger.getLogger(Achats.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public void ventes() {

        try {
            String pcode = barcodeVtxt.getText();

            pst = connection.prepareStatement("select * from product where barcode = ?");
            pst.setString(1, pcode);
            rs = pst.executeQuery();

            while (rs.next()) {
                int currentqty;
                currentqty = rs.getInt("quantite");
                int price = Integer.parseInt(prixVtxt.getText());
                int qty = Integer.parseInt(quantiteVtxt.getText());
                int tot = price * qty;

                if (qty >= currentqty) {
                    JOptionPane.showMessageDialog(this, "Qty Not Enough!!!!!!!!!!");
                } else {
                    df = (DefaultTableModel) venteTable.getModel();
                    df.addRow(new Object[]{
                        barcodeVtxt.getText(),
                        nomVtxt.getText(),
                        prixVtxt.getText(),
                        quantiteVtxt.getText(),
                        tot

                    });

                }

            }
            int sum = 0;
            
            for(int i = 0; i<venteTable.getRowCount(); i++)
            {
                
                sum = sum + Integer.parseInt(venteTable.getValueAt(i, 4).toString());
                
                
            }
            
            prixTotalVtxt.setText(String.valueOf(sum));
            
            barcodeVtxt.setText("");
            nomVtxt.setText("");
            prixVtxt.setText("");
            quantiteVtxt.setText("");
        } catch (SQLException ex) {
            Logger.getLogger(Ventes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Valider()
    {
        try {
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String date = dt.format(now);
            String vendor = vendeurSelect.getSelectedItem().toString();
            String prix_total = prixTotalVtxt.getText();
            String paiement = paiementVTxt.getText();
            String solde = SoldeVTxt.getText();
            int lastid = 0;
            
            
            String query1 = "insert into ventes(date,vendeur,prix_total,paiement,solde)values(?,?,?,?,?)" ;
            pst = connection.prepareStatement(query1,Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, date);
            pst.setString(2, vendor);
            pst.setString(3, prix_total);
            pst.setString(4, paiement);
            pst.setString(5, solde);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();
            
            if(rs.next())
            {
                
                lastid = rs.getInt(1);
            }
            
            
            String query2 = "insert into vente_Item(idventeItem,pid,prix,quantite,total)values(?,?,?,?,?) ";
            pst1 = connection.prepareStatement(query2);
            
          
              String productid;
              String prix;
              String quantite;
             int total = 0;
             
             
             
             for(int i=0; i<venteTable.getRowCount(); i++)
             {
                 productid = (String)venteTable.getValueAt(i, 0);
                  prix = (String)venteTable.getValueAt(i, 2);
                  quantite = (String)venteTable.getValueAt(i, 3);
                  total = (int)venteTable.getValueAt(i, 4);
                  
                  
                  pst1.setInt(1, lastid);
                  pst1.setString(2, productid);
                   pst1.setString(3, prix);
                   pst1.setString(4, quantite);
                   pst1.setInt(5, total);
                   pst1.executeUpdate();        
             }
             
             
             String query3 = "update product set quantite = quantite- ?   where barcode = ?";
             pst2 = connection.prepareStatement(query3);
             
               
             for(int i=0; i<venteTable.getRowCount(); i++)
             {
                 productid = (String)venteTable.getValueAt(i, 0);
                 quantite = (String)venteTable.getValueAt(i, 3);
                 
                 pst2.setString(1, quantite);
                 pst2.setString(2, productid);
                 pst2.executeUpdate();

             }
             
             
             JOptionPane.showMessageDialog(this, "Vente est complete!");
            barcodeVtxt.setText("");
            nomVtxt.setText("");
            prixVtxt.setText("");
            quantiteVtxt.setText("");
             
       
        } catch (SQLException ex) {
            Logger.getLogger(Ventes.class.getName()).log(Level.SEVERE, null, ex);
        }
     
     
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        barcodeVtxt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ajouterVBtn = new javax.swing.JButton();
        nomVtxt = new javax.swing.JTextField();
        prixVtxt = new javax.swing.JTextField();
        quantiteVtxt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        venteTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        prixTotalVtxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        paiementVTxt = new javax.swing.JTextField();
        SoldeVTxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        validerVBtn = new javax.swing.JButton();
        fermerBtn = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        vendeurSelect = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 255));

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 3, 24)); // NOI18N
        jLabel1.setText("Liste Des Ventes");

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel2.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel2.setText("Barcode Produit");

        barcodeVtxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                barcodeVtxtKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel3.setText("Nom Produit");

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel4.setText("Prix");

        jLabel5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel5.setText("Quantite");

        ajouterVBtn.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        ajouterVBtn.setText("Ajouter");
        ajouterVBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterVBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barcodeVtxt))
                .addGap(110, 110, 110)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(nomVtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(prixVtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(quantiteVtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addComponent(ajouterVBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ajouterVBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(barcodeVtxt, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                            .addComponent(nomVtxt)
                            .addComponent(prixVtxt)
                            .addComponent(quantiteVtxt))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        venteTable.setBackground(new java.awt.Color(204, 255, 204));
        venteTable.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        venteTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barcode", "Nom ", "Prix", "Quantite", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        venteTable.setRowHeight(30);
        jScrollPane1.setViewportView(venteTable);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        prixTotalVtxt.setEditable(false);

        jLabel8.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel8.setText("solde");

        jLabel7.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel7.setText("paiement");

        jLabel6.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel6.setText("prix total");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prixTotalVtxt)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(0, 69, Short.MAX_VALUE))
                    .addComponent(paiementVTxt)
                    .addComponent(SoldeVTxt))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prixTotalVtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paiementVTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SoldeVTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        validerVBtn.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        validerVBtn.setIcon(new javax.swing.ImageIcon("/Users/air/Downloads/checked.png")); // NOI18N
        validerVBtn.setText("Valider");
        validerVBtn.setAutoscrolls(true);
        validerVBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validerVBtnActionPerformed(evt);
            }
        });

        fermerBtn.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        fermerBtn.setText("Fermer");
        fermerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fermerBtnActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 3, 16)); // NOI18N
        jLabel9.setText("Vendeur :");

        vendeurSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vendeurSelect.setSelectedIndex(-1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addGap(57, 57, 57)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(34, 34, 34)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(367, 367, 367)
                                .addComponent(validerVBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fermerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(62, 62, 62))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(411, 411, 411)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vendeurSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vendeurSelect)
                        .addComponent(jLabel9)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fermerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(validerVBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void barcodeVtxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodeVtxtKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        barcode();
    }//GEN-LAST:event_barcodeVtxtKeyPressed

    private void ajouterVBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterVBtnActionPerformed
        ventes();
    }//GEN-LAST:event_ajouterVBtnActionPerformed

    private void validerVBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validerVBtnActionPerformed
        int paiment = Integer.parseInt(paiementVTxt.getText());
        int prixT = Integer.parseInt(prixTotalVtxt.getText());
        int solde = prixT - paiment;
        SoldeVTxt.setText(String.valueOf(solde));
        Valider();

       

    }//GEN-LAST:event_validerVBtnActionPerformed

    private void fermerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fermerBtnActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_fermerBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField SoldeVTxt;
    private javax.swing.JButton ajouterVBtn;
    private javax.swing.JTextField barcodeVtxt;
    private javax.swing.JButton fermerBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nomVtxt;
    private javax.swing.JTextField paiementVTxt;
    private javax.swing.JTextField prixTotalVtxt;
    private javax.swing.JTextField prixVtxt;
    private javax.swing.JTextField quantiteVtxt;
    private javax.swing.JButton validerVBtn;
    private javax.swing.JComboBox<String> vendeurSelect;
    private javax.swing.JTable venteTable;
    // End of variables declaration//GEN-END:variables
}
