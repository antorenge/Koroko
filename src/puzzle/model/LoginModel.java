/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package puzzle.model;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;


public class LoginModel {
    
    private String fname;
    private String lname;
    private static Statement stmt=null;
    private static ResultSet rs=null;
    private static Connection conn;
        
    public LoginModel() {
              
    }
      
    public String[][] getUser() throws SQLException, ClassNotFoundException {
        String [][] un = new String [100][10];
        
        try {
                
             String sql="SELECT * FROM pupils;";
             
             conn = dbConnection.getConn();
             
             
             stmt = (Statement)conn.createStatement();
             rs = stmt.executeQuery(sql);
             
             int i=0;
             
             while(rs.next()){
                 
                 un[i][1] = rs.getString("pupil_id");
                 un[i][2] = rs.getString("fname");
                 un[i][3] = rs.getString("lname");
                 
                 i++;
             } 
             rs.close();
             
        } catch(SQLException e){
        }
        finally {
            
            if(stmt != null) {
            try{
                stmt.close();
            }catch(SQLException e) {
            }
            }
            if(conn != null )
            {
                try{
                    conn.close();
                } catch (SQLException e){
                }
            }
        }
            
            
            return un;
    }
    
    public void insertUser(String fn,String ln) throws SQLException, ClassNotFoundException{
        this.fname = fn;
        this.lname = ln;
        
        conn = dbConnection.getConn();
        
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM pupils WHERE fname = '"+fn+"' AND lname = '"+ln+"'");
            
            if(!rs.next()) {                
                stmt.executeUpdate("INSERT INTO pupils VALUES (default,'"+fname+"','"+lname+"',default);");
                stmt.close();  
                conn.close(); 
                JOptionPane.showMessageDialog(null,"You have registered successfully. Thank you!");                
            } else {
                Toolkit.getDefaultToolkit().beep(); 
                JOptionPane.showMessageDialog(null,"You are already registered! Please contact your Teacher.","",JOptionPane.WARNING_MESSAGE);                
            }
            
        }
        catch (SQLException | HeadlessException ex)
        {
        }
    
    }

   
    
}
