/*
 * dbConnection sets connection to the database. It can be used by any other class one invoked. 
 * @author: antony oroko C026-0256/2011
 * 
 */

package puzzle.model;

import java.sql.*;
import javax.swing.JOptionPane;

public class dbConnection {
   public static Connection conn;
   public static Statement stmt=null;
   public static ResultSet rs=null;
   
   public dbConnection(){
       
        //create database and tables
        try
        {            
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","antoroko");            
            Statement statement=connection.createStatement();
            
            //statement for creating database if not exists
            statement.execute("CREATE DATABASE IF NOT EXISTS Koroko");
            
            getConn();
            
            //statements for creating tables 
            String marks="CREATE TABLE IF NOT EXISTS marks (marks_id int(11) NOT NULL AUTO_INCREMENT,marks varchar(45) DEFAULT NULL,timetaken int(3) DEFAULT NULL,timestamp timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,PRIMARY KEY (marks_id));";
            String pupils="CREATE TABLE IF NOT EXISTS pupils (pupil_id int(11) NOT NULL AUTO_INCREMENT,fname varchar(45) DEFAULT NULL,lname varchar(45) DEFAULT NULL,marks_id int(11) DEFAULT NULL,PRIMARY KEY (pupil_id),CONSTRAINT marks_id FOREIGN KEY (marks_id) REFERENCES marks (marks_id));";
           // String constraints = "ALTER TABLE pupils ADD CONSTRAINT marks_id FOREIGN KEY (marks_id) REFERENCES marks (marks_id) ON DELETE NO ACTION ON UPDATE CASCADE;";
            stmt.execute(marks);
            stmt.execute(pupils);
           // stmt.execute(constraints);
            
            
        }
        catch(ClassNotFoundException | SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
    }
     
   public static Connection getConn(){       
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/koroko","root","antoroko");
            stmt=conn.createStatement();
        }
        catch(ClassNotFoundException | SQLException e)
        {
            JOptionPane.showMessageDialog(null, "If you are seeing this, then its your first time Installation. Please wait, while we are working out your storage...");            
        }
        
        return conn;
    }
   
}
