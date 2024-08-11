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
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author air
 */
public class Achats extends javax.swing.JFrame {

    /**
     * Creates new form ventes
     */
    public Achats() {
        initComponents();
        Connect();
        Vendeur();
        Fournisseur();
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
    public void Fournisseur()
    {
        
        try {
            pst = connection.prepareStatement("SELECT Distinct nom_prenom from fournisseurs");
            rs = pst.executeQuery();
            
            fourniCombo.removeAllItems();

            while(rs.next())
            {
                fourniCombo.addItem(rs.getString("nom_prenom"));
            }
  
        } catch (SQLException ex) {
            Logger.getLogger(Achats.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    public void barcode()
    {
        try {
            String pcode = barcodePtxt.getText();
  
            pst = connection.prepareStatement("SELECT * FROM product WHERE barcode = ?");
            pst.setString(1, pcode);
            rs = pst.executeQuery();
            
           if(rs.next() == false)
           {
               JOptionPane.showMessageDialog(this, "BarCode Not Found");
               barcodePtxt.setText("");
           }
            else
               
           {
               String pname = rs.getString("pname");
               String prix = rs.getString("prix_vente");
               
                 nomPtxt.setText(pname.trim());
                 prixPtxt.setText(prix.trim());
                 quantitePtxt.requestFocus(); 
           }
    
        } catch (SQLException ex) {
            Logger.getLogger(Achats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void purchase()
           {
        int price = Integer.parseInt(prixPtxt.getText());
        int qty = Integer.parseInt(quantitePtxt.getText());

        int tot = price * qty;

        df = (DefaultTableModel) achatTable.getModel();
        df.addRow(new Object[]{
            barcodePtxt.getText(),
            fourniCombo.getSelectedItem(),
            nomPtxt.getText(),
            prixPtxt.getText(),
            quantitePtxt.getText(),
            tot
        });
        
        int sum = 0;
       
       for(int i = 0; i<achatTable.getRowCount(); i++)
               {
                   sum = sum + Integer.parseInt(achatTable.getValueAt(i, 5).toString());
               }
        prixTotaltxt.setText(String.valueOf(sum));
        barcodePtxt.setText("");
        nomPtxt.setText("");
        prixPtxt.setText("");
        quantitePtxt.setText("");
            }
    
    public void valider()
    {
        try {
            
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String date = dt.format(now);
            String vendor = vendeurSelect.getSelectedItem().toString();
            String prixT = prixTotaltxt.getText();
            String fourni = (String) fourniCombo.getSelectedItem();
            String paiement = paiementTxt.getText();
            String solde = SoldeTxt.getText();
            int lastid = 0;
            
            
            String query1 = "insert into achats(date,fournisseur,vendeur,prix_total,paiement,solde)values(?,?,?,?,?,?)" ;
            pst = connection.prepareStatement(query1,Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, date);
            pst.setString(2, fourni);
            pst.setString(3, vendor);
            pst.setString(4, prixT);
            pst.setString(5, paiement);
            pst.setString(6, solde);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();
            System.out.println("its okey ");
            if(rs.next())
            {  
                lastid = rs.getInt(1);
            }
             System.out.println("its okey 2 ");
            
            String query2 = "insert into achatItem(purid,pid,prix_vente,quantite,total)values(?,?,?,?,?)";
            pst1 = connection.prepareStatement(query2);
            
          
              String productid;
              String price;
              String qty;
             int total = 0;
             
             
             
             for(int i=0; i<achatTable.getRowCount(); i++)
             {
                 productid = (String)achatTable.getValueAt(i, 0);
                  price = (String)achatTable.getValueAt(i, 3);
                  qty = (String)achatTable.getValueAt(i, 4);
                  total = (int)achatTable.getValueAt(i, 5);
                  
                  
                  pst1.setInt(1, lastid);
                  pst1.setString(2, productid);
                   pst1.setString(3, price);
                   pst1.setString(4, qty);
                   pst1.setInt(5, total);
                   pst1.executeUpdate();        
             }
              System.out.println("its okey 3");
             
             String query3 = "update product set quantite = quantite+ ?   where barcode = ?";
             pst2 = connection.prepareStatement(query3);
             
               
             for(int i=0; i<achatTable.getRowCount(); i++)
             {
                 productid = (String)achatTable.getValueAt(i, 0);
                 qty = (String)achatTable.getValueAt(i, 3);
                 pst2.setString(1, qty);
                 pst2.setString(2, productid);
                 pst2.executeUpdate();
             }
             JOptionPane.showMessageDialog(this, "Achat est complete!");
 
        } catch (SQLException ex) {
            Logger.getLogger(Achats.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        barcodePtxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ajouter1Btn = new javax.swing.JButton();
        nomPtxt = new javax.swing.JTextField();
        prixPtxt = new javax.swing.JTextField();
        quantitePtxt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        fourniCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        achatTable = new javax.swing.JTable();
        validerBtn = new javax.swing.JButton();
        fermerBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        prixTotaltxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        paiementTxt = new javax.swing.JTextField();
        SoldeTxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        vendeurSelect = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 255));

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel1.setText("Barcode Produit");

        barcodePtxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                barcodePtxtKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel2.setText("Nom Produit");

        jLabel3.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel3.setText("Prix");

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel4.setText("Quantite");

        ajouter1Btn.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        ajouter1Btn.setText("Ajouter");
        ajouter1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter1BtnActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        jLabel10.setText("Fournisseur");

        fourniCombo.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        fourniCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "nom_prenom", " " }));
        fourniCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fourniComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barcodePtxt))
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(nomPtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(prixPtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(quantitePtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(fourniCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addComponent(ajouter1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ajouter1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(prixPtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nomPtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel10))
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(quantitePtxt))
                                    .addComponent(barcodePtxt)
                                    .addComponent(fourniCombo, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addGap(23, 23, 23))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {nomPtxt, prixPtxt, quantitePtxt});

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 3, 24)); // NOI18N
        jLabel5.setText("Liste des Achats");

        achatTable.setBackground(new java.awt.Color(204, 255, 204));
        achatTable.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        achatTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barcode", "Fournisseur", "Nom ", "Prix", "Quantite", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        achatTable.setRowHeight(30);
        jScrollPane1.setViewportView(achatTable);

        validerBtn.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        validerBtn.setIcon(new javax.swing.ImageIcon("/Users/air/NetBeansProjects/myStock/src/main/java/com/mycompany/mystock/images/checked.png")); // NOI18N
        validerBtn.setText("Valider");
        validerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validerBtnActionPerformed(evt);
            }
        });

        fermerBtn.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        fermerBtn.setText("Fermer");
        fermerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fermerBtnActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        prixTotaltxt.setEditable(false);

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
                    .addComponent(prixTotaltxt)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(0, 69, Short.MAX_VALUE))
                    .addComponent(paiementTxt)
                    .addComponent(SoldeTxt))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prixTotaltxt, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paiementTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SoldeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 3, 16)); // NOI18N
        jLabel9.setText("Vendeur :");

        vendeurSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vendeurSelect.setSelectedIndex(-1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(376, 376, 376)
                            .addComponent(jLabel5)
                            .addGap(268, 268, 268)
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(vendeurSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(329, 329, 329)
                                .addComponent(validerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 812, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(fermerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(vendeurSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(9, 9, 9)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fermerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(validerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void validerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validerBtnActionPerformed
         int paiment = Integer.parseInt(paiementTxt.getText());
        int prixT = Integer.parseInt(prixTotaltxt.getText());
        int solde = prixT - paiment;
         SoldeTxt.setText(String.valueOf(solde));
         
        valider();       
         
    }//GEN-LAST:event_validerBtnActionPerformed

    private void fermerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fermerBtnActionPerformed
         this.setVisible(false);
    }//GEN-LAST:event_fermerBtnActionPerformed

    private void barcodePtxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodePtxtKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
            barcode();
    }//GEN-LAST:event_barcodePtxtKeyPressed

    private void ajouter1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter1BtnActionPerformed
        purchase();
    }//GEN-LAST:event_ajouter1BtnActionPerformed

    private void fourniComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fourniComboActionPerformed
      
    }//GEN-LAST:event_fourniComboActionPerformed

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
            java.util.logging.Logger.getLogger(Achats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Achats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Achats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Achats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Achats().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField SoldeTxt;
    private javax.swing.JTable achatTable;
    private javax.swing.JButton ajouter1Btn;
    private javax.swing.JTextField barcodePtxt;
    private javax.swing.JButton fermerBtn;
    private javax.swing.JComboBox<String> fourniCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JTextField nomPtxt;
    private javax.swing.JTextField paiementTxt;
    private javax.swing.JTextField prixPtxt;
    private javax.swing.JTextField prixTotaltxt;
    private javax.swing.JTextField quantitePtxt;
    private javax.swing.JButton validerBtn;
    private javax.swing.JComboBox<String> vendeurSelect;
    // End of variables declaration//GEN-END:variables
}
