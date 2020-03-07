/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Image;
import javax.swing.JFrame;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author JAMAL
 */
public class LicitatiiFrame extends javax.swing.JFrame {
    int xMouse, yMouse;
    Connection conn = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 =null;
    ResultSet rs = null;   
    ResultSet rs2 = null;
    ResultSet rs3 = null;
    ResultSet rs5 = null;
    ResultSet rs6 = null;
    
    
    String licitatieID;
    int bestBid;
    int pretIncepere;
    static int licitatie;
    int pretCumparare;
    
    public static int getProdus(){
        return licitatie;
    }
    //aceeasi functie pentru formatarea cu spatii a numarului de card
    public String formatCard(String numar) {
        MaskFormatter model;
        String numarFinal = numar;
        try {
            model = new MaskFormatter("#### #### #### ####");
            model.setValueContainsLiteralCharacters(false);
            numarFinal = model.valueToString(numar);
            return numarFinal;
        } catch (ParseException ex) {
            Logger.getLogger(LicitatiiFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numarFinal;
    }
    //aceeasi functie pentru protejarea numarului de card
    public String protejareCard(String card){
        char[] array = card.toCharArray();
        for(int i = 0; i<14; i++)
            if(array[i] != ' ')
                array[i] = '*';
        
        return new String(array);
    }
    
    /**
     * Creates new form LicitatiiFrame
     */
    public LicitatiiFrame() {
        initComponents();
        
        
        try {
            mesajUser.setText(LoginFrame.getInfo().getString("Nume")+ " " + LoginFrame.getInfo().getString("Prenume"));
        } catch (SQLException ex) {
            Logger.getLogger(MenuFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        UpdateBD.updateLicitatii();  //updatarea statusului si castigatorilor licitatii
        
        //Afisarea in tabel a tuturor licitatiilor
        ArrayList<Product3> list = new ArrayList<Product3>();
        conn = MySqlConnect.ConnectDB();
        String sql = "Select *,(select max(`Suma oferita`) from bids where `Licitatie ID` = l.`Licitatie ID`) as `Best bid` \n" +
"from licitatie l inner join produs p on l.`Produs ID` = p.`Produs ID`;";
        String sql2;
        
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
           
            Product3 p;
            while(rs.next())
                    {   
                        int id = rs.getInt(1);
                        String pretIncepere = rs.getString(3);
                        String pretDirecta = rs.getString(4);
                        Timestamp dataIncepere = rs.getTimestamp(5);
                        Timestamp dataIncheiere = rs.getTimestamp(6);
                        String status = rs.getString(8);
                        
                        byte[] img = rs.getBytes(19);
                        String producator = rs.getString(14);
                        String modelTelefon = rs.getString(15);
                        String stare = rs.getString(16);
 
                        int bestBid = rs.getInt(20);
                        
                        p = new Product3(id,pretIncepere,pretDirecta,dataIncepere,dataIncheiere,status,img,producator,modelTelefon,bestBid);
                        list.add(p);
                        //Object [] content = {id,producator,modelTelefon,stare,an,descriere, newImage};
                       // DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                        
                       // model.addRow(content);
                    } 
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        String[] columnName = {"Licitatie ID","Producator","Model","Pret cumparare","Best bid","Status","Poza","Data incheiere","Data incepere","Pret incepere"};
        Object[][] rows = new Object[list.size()][10];
        for(int i = 0; i < list.size(); i++){
            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getProducator();
            rows[i][2] = list.get(i).getModelTelefon();
            rows[i][3] = list.get(i).getPretDirecta();
            rows[i][4] = list.get(i).getBestBid();
            rows[i][5] = list.get(i).getStatus();

            if(list.get(i).getImagine() != null){
                
             ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getImagine()).getImage()
             .getScaledInstance(100, 100, Image.SCALE_SMOOTH) );   
                
            rows[i][6] = image;
            }
            else{
                rows[i][6] = null;
            }
            
            rows[i][7] = list.get(i).getDataIncheiere().toString();
            rows[i][8] = list.get(i).getDataIncepere().toString();
            rows[i][9] = list.get(i).getPretIncepere();
        }
        
        TheModel model = new TheModel(rows, columnName);
        jTable1.setModel(model);
        jTable1.setRowHeight(120);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        
        //afisarea in comboBox a cardurilor adaugate si a adreselor
        
        try{
            sql = "Select * from plata where `Utilizator ID` = ?";
            sql2 = "Select * from `utilizator.adrese` u inner join adresa a on u.`Adresa ID` = a.`Adresa ID` where `Utilizator ID` = ?;";
            pst = conn.prepareStatement(sql);
            pst.setString(1, LoginFrame.getInfo().getString("Utilizator ID"));
            rs5 = pst.executeQuery();
            
            DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
            while(rs5.next())
            {   
                comboModel.addElement(protejareCard(formatCard(rs5.getObject(3).toString())));
            }
            jComboBox1.setModel(comboModel);
            
            pst2 = conn.prepareStatement(sql2);
            pst2.setString(1, LoginFrame.getInfo().getString("Utilizator ID"));
            rs6 = pst2.executeQuery();
            
            DefaultComboBoxModel comboModel2 = new DefaultComboBoxModel();
            while(rs6.next())
            {   
                String adresa = rs6.getString(5) + " " + rs6.getString(6) + " Nr." + rs6.getString(7);
                comboModel2.addElement(adresa);
            }
            jComboBox2.setModel(comboModel2);
            
        } catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
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

        jCalendar1 = new com.toedter.calendar.JCalendar();
        jPanel2 = new javax.swing.JPanel();
        settings = new javax.swing.JLabel();
        logOut = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        home = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Minimize = new javax.swing.JLabel();
        Exit = new javax.swing.JLabel();
        Icon = new javax.swing.JLabel();
        mesajUser = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        sumaLicitata = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1173, 800));

        settings.setIcon(new javax.swing.ImageIcon("C:\\Users\\JAMAL\\Downloads\\icons8-user-settings-50.png")); // NOI18N
        settings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        settings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settingsMouseClicked(evt);
            }
        });

        logOut.setIcon(new javax.swing.ImageIcon("C:\\Users\\JAMAL\\Downloads\\Change User-50-50px\\icons8-change-user-50-2.png")); // NOI18N
        logOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logOutMouseClicked(evt);
            }
        });

        back.setIcon(new javax.swing.ImageIcon("C:\\Users\\JAMAL\\Downloads\\Back-24-50px\\icons8-back-50.png")); // NOI18N
        back.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backMouseClicked(evt);
            }
        });

        home.setIcon(new javax.swing.ImageIcon("C:\\Users\\JAMAL\\Downloads\\icons8-home-50.png")); // NOI18N
        home.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settings, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(back, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(home, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(20, Short.MAX_VALUE)
                    .addComponent(logOut)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(home)
                .addGap(18, 18, 18)
                .addComponent(back)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(settings)
                .addGap(79, 79, 79))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(623, Short.MAX_VALUE)
                    .addComponent(logOut)
                    .addGap(21, 21, 21)))
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Licitatii");

        Minimize.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        Minimize.setText("-");
        Minimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MinimizeMouseClicked(evt);
            }
        });

        Exit.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        Exit.setText("X");
        Exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ExitMouseClicked(evt);
            }
        });

        Icon.setIcon(new javax.swing.ImageIcon("C:\\Users\\JAMAL\\Downloads\\icons8-user-30.png")); // NOI18N
        Icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IconMouseClicked(evt);
            }
        });

        mesajUser.setText("User");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Icon)
                .addGap(2, 2, 2)
                .addComponent(mesajUser, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Minimize)
                .addGap(18, 18, 18)
                .addComponent(Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Minimize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mesajUser))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(Icon)))
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Licitatie ID", "Producator", "Model", "Pret cumparare", "Best bid", "Status", "Poza", "Data incheiere", "Data incepere", "Pret incepere"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jButton1.setText("Liciteaza");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Suma licitata");

        jLabel3.setText("Cardul ales");

        jLabel4.setText("Adresa aleasa");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton2.setText("Mai multe informatii produs");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Arata toate licitatiile");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Arata licitatii in curs");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Arata licitatii terminate");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Arata licitatie neincepute");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Cumpara la pret direct  ");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jButton3)
                                .addGap(40, 40, 40)
                                .addComponent(jButton4)
                                .addGap(6, 6, 6)
                                .addComponent(jButton5)
                                .addGap(6, 6, 6)
                                .addComponent(jButton6))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(183, 183, 183)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(sumaLicitata, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(40, 40, 40)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(43, 43, 43)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(63, 63, 63)
                                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(9, 9, 9))))
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(jButton6))
                        .addGap(11, 11, 11)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(6, 6, 6)
                                .addComponent(sumaLicitata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(60, 60, 60)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(22, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void settingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsMouseClicked
        UserFrame uf = new UserFrame();
        uf.setVisible(true);
        uf.pack();
        uf.setLocationRelativeTo(null);
        uf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_settingsMouseClicked

    private void logOutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logOutMouseClicked
        LoginFrame lgf = new LoginFrame();
        lgf.setVisible(true);
        lgf.pack();
        lgf.setLocationRelativeTo(null);
        lgf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_logOutMouseClicked

    private void homeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseClicked
        MenuFrame mf = new MenuFrame();
        mf.setVisible(true);
        mf.pack();
        mf.setLocationRelativeTo(null);
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_homeMouseClicked

    private void MinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeMouseClicked

        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_MinimizeMouseClicked

    private void ExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_ExitMouseClicked

    private void IconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconMouseClicked

    }//GEN-LAST:event_IconMouseClicked

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();

        this.setLocation(x-xMouse, y-yMouse);
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanel1MousePressed

    private void backMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseClicked
        MenuFrame mf = new MenuFrame();
        mf.setVisible(true);
        mf.pack();
        mf.setLocationRelativeTo(null);
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_backMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //adaugarea unei oferte pentru o licitatie
        
        UpdateBD.updateLicitatii();
        //o verificare cu datele din tabel pentru suma licitata
        if( !sumaLicitata.getText().isBlank() && bestBid < Integer.parseInt(sumaLicitata.getText()) && pretIncepere < Integer.parseInt(sumaLicitata.getText()) ){
        conn = MySqlConnect.ConnectDB();
        String sql = "INSERT INTO bids (`Plata ID`,`Licitatie ID`,`Utilizator ID`,`Suma oferita`) VALUES (?,?,?,?)";
        String sql2 = "Select l.`Pret incepere`, l.`Data incheiere planificata`, (Select max(`Suma oferita`) from bids where l.`Licitatie ID` = `Licitatie ID`) as `Best bid`\n" +
"from licitatie l where l.`Licitatie ID` = ?; ";
        try{
            pst = conn.prepareStatement(sql2);
            pst.setString(1, licitatieID);
            rs = pst.executeQuery();
            Date data = new Date();
            //verificare daca s-a incheiat si daca suma licitata este buna(direct cu datele din bd)
            if( rs.next() &&
               rs.getInt(1) < Integer.parseInt(sumaLicitata.getText()) &&
               rs.getInt(3) < Integer.parseInt(sumaLicitata.getText()) &&
               rs.getTimestamp(2).after(new Timestamp(data.getTime())))
            {
            System.out.println("S-a ajuns");
            pst = conn.prepareStatement(sql);
            rs5.beforeFirst(); //aici au ramas stocate adresele din ComboBox. Nu le-am rescris.
            rs5.next();
            while( !protejareCard(formatCard(rs5.getString(3))).equals(jComboBox1.getSelectedItem()) )
                rs5.next();
                   
            pst.setString(1,rs5.getString(1));
            pst.setString(2, licitatieID);
            pst.setString(3, LoginFrame.getInfo().getString("Utilizator ID"));
            pst.setString(4, sumaLicitata.getText());
            int i = pst.executeUpdate();
                
            LicitatiiFrame lf = new LicitatiiFrame();
            lf.setVisible(true);
            lf.pack();
            lf.setLocationRelativeTo(null);
            this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Suma nu este corecta sau licitatia nu este 'In curs'");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        }
        else
            JOptionPane.showMessageDialog(null, "Trebuie sa licitati o suma mai mare decat Best bid/Pret incepere");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int index = jTable1.getSelectedRow();
        TableModel model = jTable1.getModel();
        
        licitatieID = model.getValueAt(index, 0).toString();
        bestBid = (int) model.getValueAt(index, 4);
        pretIncepere = Integer.parseInt(model.getValueAt(index, 9).toString());
        licitatie = (int) model.getValueAt(index, 0);
        pretCumparare = (int) Integer.parseInt(model.getValueAt(index, 3).toString());
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        InformatiiProdusFrame ipf = new InformatiiProdusFrame();
        ipf.setVisible(true);
        ipf.pack();
        ipf.setLocationRelativeTo(null);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        LicitatiiFrame lf = new LicitatiiFrame();
        lf.setVisible(true);
        lf.pack();
        lf.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        //afisarea licitatiilor in curs
        
        UpdateBD.updateLicitatii();
        
        ArrayList<Product3> list = new ArrayList<Product3>();
        conn = MySqlConnect.ConnectDB();
        String sql = "Select * from licitatie l inner join produs p on l.`Produs ID` = p.`Produs ID` where `Status` = 'In curs' ;";
        String sql2 = "select max(`Suma oferita`) as `Best bid` from bids where `Licitatie ID` = ?"; 
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
           
            Product3 p;
            while(rs.next())
                    {   
                        int id = rs.getInt(1);
                        String pretIncepere = rs.getString(3);
                        String pretDirecta = rs.getString(4);
                        Timestamp dataIncepere = rs.getTimestamp(5);
                        Timestamp dataIncheiere = rs.getTimestamp(6);
                        String status = rs.getString(8);

                        byte[] img = rs.getBytes(19);
                        String producator = rs.getString(14);
                        String modelTelefon = rs.getString(15);
                        String stare = rs.getString(16);
                        
                        pst2 = conn.prepareStatement(sql2);
                        pst2.setString(1, rs.getString(1));
                        rs2 = pst2.executeQuery();
                        rs2.next();
                        
                        int bestBid = rs2.getInt(1);
                        
                        p = new Product3(id,pretIncepere,pretDirecta,dataIncepere,dataIncheiere,status,img,producator,modelTelefon,bestBid);
                        list.add(p);
                        //Object [] content = {id,producator,modelTelefon,stare,an,descriere, newImage};
                       // DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                        
                       // model.addRow(content);
                    } 
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        String[] columnName = {"Licitatie ID","Producator","Model","Pret cumparare","Best bid","Status","Poza","Data incheiere","Data incepere","Pret incepere"};
        Object[][] rows = new Object[list.size()][10];
        for(int i = 0; i < list.size(); i++){
            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getProducator();
            rows[i][2] = list.get(i).getModelTelefon();
            rows[i][3] = list.get(i).getPretDirecta();
            rows[i][4] = list.get(i).getBestBid();
            rows[i][5] = list.get(i).getStatus();

            if(list.get(i).getImagine() != null){
                
             ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getImagine()).getImage()
             .getScaledInstance(100, 100, Image.SCALE_SMOOTH) );   
                
            rows[i][6] = image;
            }
            else{
                rows[i][6] = null;
            }
            
            rows[i][7] = list.get(i).getDataIncheiere();
            rows[i][8] = list.get(i).getDataIncepere();
            rows[i][9] = list.get(i).getPretIncepere();
        }
        
        TheModel model = new TheModel(rows, columnName);
        jTable1.setModel(model);
        jTable1.setRowHeight(120);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        //afisarea licitatiilor incheiate
        
        UpdateBD.updateLicitatii();
        
        ArrayList<Product3> list = new ArrayList<Product3>();
        conn = MySqlConnect.ConnectDB();
        String sql = "Select * from licitatie l inner join produs p on l.`Produs ID` = p.`Produs ID` where `Status` = 'Incheiata' ;";
        String sql2 = "select max(`Suma oferita`) as `Best bid` from bids where `Licitatie ID` = ?"; 
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
           
            Product3 p;
            while(rs.next())
                    {   
                        int id = rs.getInt(1);
                        String pretIncepere = rs.getString(3);
                        String pretDirecta = rs.getString(4);
                        Timestamp dataIncepere = rs.getTimestamp(5);
                        Timestamp dataIncheiere = rs.getTimestamp(6);
                        String status = rs.getString(8);

                        byte[] img = rs.getBytes(19);
                        String producator = rs.getString(14);
                        String modelTelefon = rs.getString(15);
                        String stare = rs.getString(16);
                        
                        pst2 = conn.prepareStatement(sql2);
                        pst2.setString(1, rs.getString(1));
                        rs2 = pst2.executeQuery();
                        rs2.next();
                        
                        int bestBid = rs2.getInt(1);
                        
                        p = new Product3(id,pretIncepere,pretDirecta,dataIncepere,dataIncheiere,status,img,producator,modelTelefon,bestBid);
                        list.add(p);
                        //Object [] content = {id,producator,modelTelefon,stare,an,descriere, newImage};
                       // DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                        
                       // model.addRow(content);
                    } 
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        String[] columnName = {"Licitatie ID","Producator","Model","Pret cumparare","Best bid","Status","Poza","Data incheiere","Data incepere","Pret incepere"};
        Object[][] rows = new Object[list.size()][10];
        for(int i = 0; i < list.size(); i++){
            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getProducator();
            rows[i][2] = list.get(i).getModelTelefon();
            rows[i][3] = list.get(i).getPretDirecta();
            rows[i][4] = list.get(i).getBestBid();
            rows[i][5] = list.get(i).getStatus();

            if(list.get(i).getImagine() != null){
                
             ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getImagine()).getImage()
             .getScaledInstance(100, 100, Image.SCALE_SMOOTH) );   
                
            rows[i][6] = image;
            }
            else{
                rows[i][6] = null;
            }
            
            rows[i][7] = list.get(i).getDataIncheiere();
            rows[i][8] = list.get(i).getDataIncepere();
            rows[i][9] = list.get(i).getPretIncepere();
        }
        
        TheModel model = new TheModel(rows, columnName);
        jTable1.setModel(model);
        jTable1.setRowHeight(120);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        //afisarea licitatiilor neincepute
        
        UpdateBD.updateLicitatii();
        
        ArrayList<Product3> list = new ArrayList<Product3>();
        conn = MySqlConnect.ConnectDB();
        String sql = "Select * from licitatie l inner join produs p on l.`Produs ID` = p.`Produs ID` where `Status` = 'Neinceputa';";
        String sql2 = "select max(`Suma oferita`) as `Best bid` from bids where `Licitatie ID` = ?"; 
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
           
            Product3 p;
            while(rs.next())
                    {   
                        int id = rs.getInt(1);
                        String pretIncepere = rs.getString(3);
                        String pretDirecta = rs.getString(4);
                        Timestamp dataIncepere = rs.getTimestamp(5);
                        Timestamp dataIncheiere = rs.getTimestamp(6);
                        String status = rs.getString(8);
                 
                        byte[] img = rs.getBytes(19);
                        String producator = rs.getString(14);
                        String modelTelefon = rs.getString(15);
                        String stare = rs.getString(16);
                        
                        pst2 = conn.prepareStatement(sql2);
                        pst2.setString(1, rs.getString(1));
                        rs2 = pst2.executeQuery();
                        rs2.next();
                        
                        
                        int bestBid = rs2.getInt(1);
                        
                        p = new Product3(id,pretIncepere,pretDirecta,dataIncepere,dataIncheiere,status,img,producator,modelTelefon,bestBid);
                        list.add(p);
                        //Object [] content = {id,producator,modelTelefon,stare,an,descriere, newImage};
                       // DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                        
                       // model.addRow(content);
                    } 
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        String[] columnName = {"Licitatie ID","Producator","Model","Pret cumparare","Best bid","Status","Poza","Data incheiere","Data incepere","Pret incepere"};
        Object[][] rows = new Object[list.size()][10];
        for(int i = 0; i < list.size(); i++){
            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getProducator();
            rows[i][2] = list.get(i).getModelTelefon();
            rows[i][3] = list.get(i).getPretDirecta();
            rows[i][4] = list.get(i).getBestBid();
            rows[i][5] = list.get(i).getStatus();

            if(list.get(i).getImagine() != null){
                
             ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getImagine()).getImage()
             .getScaledInstance(100, 100, Image.SCALE_SMOOTH) );   
                
            rows[i][6] = image;
            }
            else{
                rows[i][6] = null;
            }
            
            rows[i][7] = list.get(i).getDataIncheiere();
            rows[i][8] = list.get(i).getDataIncepere();
            rows[i][9] = list.get(i).getPretIncepere();
        }
        
        TheModel model = new TheModel(rows, columnName);
        jTable1.setModel(model);
        jTable1.setRowHeight(120);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        //Cumparare directa, modifica data de incheiere cu data actuala
        //si apeleaza functia update, astfel se va inchide licitatia
        
        UpdateBD.updateLicitatii();
        
        conn = MySqlConnect.ConnectDB();
        String sql = "INSERT INTO bids (`Plata ID`,`Licitatie ID`,`Utilizator ID`,`Suma oferita`) VALUES (?,?,?,?)";
        String sql2 = "Select l.`Data incheiere planificata`" +
"from licitatie l where l.`Licitatie ID` = ?; ";
        try{
            pst = conn.prepareStatement(sql2);
            pst.setString(1, licitatieID);
            rs = pst.executeQuery();
            Date data = new Date();
            //verificare daca s-a incheiat si daca suma licitata este buna(direct cu datele din bd)
            if( rs.next() &&
               rs.getTimestamp(1).after(new Timestamp(data.getTime())))
            {
            pst = conn.prepareStatement(sql);
            rs5.beforeFirst(); //aici au ramas stocate adresele din ComboBox. Nu le-am rescris.
            rs5.next();
            while( !protejareCard(formatCard(rs5.getString(3))).equals(jComboBox1.getSelectedItem()) )
                rs5.next();
                   
            pst.setString(1,rs5.getString(1));
            pst.setString(2, licitatieID);
            pst.setString(3, LoginFrame.getInfo().getString("Utilizator ID"));
            pst.setInt(4, pretCumparare);
            int i = pst.executeUpdate();
            
            sql = "UPDATE licitatie SET `Data incheiere planificata` = ? WHERE `Licitatie ID` = ?;";
            pst = conn.prepareStatement(sql);
            pst.setTimestamp(1, new Timestamp(data.getTime()));
            pst.setString(2,licitatieID);
            i = pst.executeUpdate();
            
            LicitatiiFrame lf = new LicitatiiFrame();
            lf.setVisible(true);
            lf.pack();
            lf.setLocationRelativeTo(null);
            this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Suma nu este corecta sau licitatia nu este 'In curs'");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

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
            java.util.logging.Logger.getLogger(LicitatiiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LicitatiiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LicitatiiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LicitatiiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LicitatiiFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Exit;
    private javax.swing.JLabel Icon;
    private javax.swing.JLabel Minimize;
    private javax.swing.JLabel back;
    private javax.swing.JLabel home;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel logOut;
    private javax.swing.JLabel mesajUser;
    private javax.swing.JLabel settings;
    private javax.swing.JTextField sumaLicitata;
    // End of variables declaration//GEN-END:variables
}
