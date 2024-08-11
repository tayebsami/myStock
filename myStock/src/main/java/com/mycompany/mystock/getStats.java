/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mystock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author air
 */
public class getStats {
    Connection connection;
    ResultSet rs;
    public static int i = 0;

    
      public Connection Connect()
    {
        try {  
            
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
    public int Jan() throws SQLException {
        try
        {
            Connect();
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/01/01' and '2022/01/31'";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
               // i = rs.getString(String.valueOf());
            }
        
         System.out.println("i = "+ i);
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    
    public int Fev() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/02/01' and '2022/02/29'";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Mars() throws SQLException {
        try
        {
            Connect();
            
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/03/01' and '2022/03/31'";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
         
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Avril() throws SQLException {
        try {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/04/01' and '2022/04/30'";
            rs = st.executeQuery(check);
            while (rs.next()) {
              i = rs.getInt(1);
            }
        System.out.println("i = "+ i);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return i;
    }
    

    public int mai() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/05/01' and '2022/05/31'";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Juin() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/06/01' and '2022/06/30';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Juillet() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/07/01' and '2022/07/31';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Aout() throws SQLException {
        try
        {
            Connect();
            
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/08/01' and '2022/08/31'";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Septembre() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/09/01' and '2022/09/30';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Octobre() throws SQLException {
        try
        {
            Connect();
            
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/10/01' and '2022/10/31';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Nov() throws SQLException {
        try
        {
            Connect();
          
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/11/01' and '2022/11/30';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
    public int Dec() throws SQLException {
        try
        {
            Connect();
           
            Statement st = connection.createStatement();
            String check = "Select sum(prix_total) from ventes where date between '2022/12/01' and '2022/12/31';";
            rs = st.executeQuery(check);
            while(rs.next())
            {
                i = rs.getInt(1);
            }
        
        
    }catch(SQLException ex)
    {
        ex.printStackTrace();
    }
        return i;
      } 
}
