/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;
import java.sql.*;
import javax.swing.*;
/**
 *
 * @author JAMAL
 */
//conectarea la BD, care ulterior va fi apelata in frame-uri
public class MySqlConnect {
    Connection conn = null;
    public static Connection ConnectDB(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/licitatie","root","pentrutest132");
            //JOptionPane.showMessageDialog(null,"Connected to database");
            return conn;
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
    }

