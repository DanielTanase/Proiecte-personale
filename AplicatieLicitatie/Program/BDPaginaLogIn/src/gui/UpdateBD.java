/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Date;

/**
 *
 * @author JAMAL
 */
public class UpdateBD {
    
    static Connection conn = null;
    static PreparedStatement pst = null;
    static ResultSet rs = null;
    static ResultSet rs2 = null;
    
    //Functie pentru a updata statusul unei licitatii si in cazul in care s-a
    //terminat, decide si castigatorul.
    //Avem doua cazuri, cand se trece de la Neinceput la In curs, si 
    //cand se face trecerea de la In curs la Incheiata, moment in care se 
    //cauta in tabelul Bids, oferta cea mai mare si se afla castigatorul
    
    public static void updateLicitatii(){
        Date data = new Date();
        System.out.println(data);
        conn = MySqlConnect.ConnectDB();
        String sql = "Select * from licitatie";
        try{
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            
            while(rs.next())
            {
                if(rs.getTimestamp(5).before(new Timestamp(data.getTime())) && rs.getString(8).equalsIgnoreCase("Neinceputa"))
                {
                    sql = "UPDATE licitatie SET `Status`='In curs' WHERE `Licitatie ID` = ?  ";
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, rs.getString(1));
                    int i = pst.executeUpdate();   
                }
                
                if( rs.getTimestamp(6).before(new Timestamp(data.getTime())) && (rs.getString(8).equalsIgnoreCase("In curs") || rs.getString(8).equalsIgnoreCase("Neinceputa")) )
                {   
                    sql = "Select u.`Utilizator ID`,`Nume`,`Prenume` FROM utilizator u INNER JOIN bids b ON u.`Utilizator ID` = b.`Utilizator ID`\n" +
"	 WHERE b.`Licitatie ID` = ? and `Suma oferita` = (Select MAX(`Suma oferita`) From Bids where `Licitatie ID` = ?)";
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, rs.getString(1));
                    pst.setString(2, rs.getString(1));
                    rs2 = pst.executeQuery();
                    if(rs2.next()){        
                        sql = "UPDATE licitatie SET `Status`='Incheiata',`Castigator licitatie`= ? , `Review` = 'Nu' WHERE `Licitatie ID` = ?  ";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, rs2.getString(1));
                        pst.setString(2, rs.getString(1));
                        int i = pst.executeUpdate();    
                    }
                    else if( rs.getTimestamp(6).before(new Timestamp(data.getTime())) ){
                        sql = "UPDATE licitatie SET `Status`='Incheiata' WHERE `Licitatie ID` = ?  ";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, rs.getString(1));
                        int i = pst.executeUpdate();    
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Eroare la updatare");
                }
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e);
        }
                
    }
    
}
