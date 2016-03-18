
package puzzle.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import net.proteanit.sql.DbUtils;
import puzzle.ui.GameMainWindow;

public class MarksModel {
    
    private static int timetaken;
    private static int marks;
    private static Statement stmt=null;
    private static ResultSet rs=null;
    private static Connection conn;
    
    public static void insertMarks(int mks, int time) throws SQLException, ClassNotFoundException{
        
        conn = dbConnection.getConn();
        
        marks = mks;        
        timetaken = time;
        
        try
        {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO marks VALUES (default,'"+marks+"','"+timetaken+"',default);");
            stmt.close();  
            conn.close();
        }
        catch (SQLException ex)
        {
        }
        
        //finally
        updatePupilsMarks();
        
    }
    
    public static void updatePupilsMarks() throws SQLException, ClassNotFoundException {
        
        conn = dbConnection.getConn();
        
        try
        {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE pupils SET marks_id='"+getMarksID()+"' WHERE pupil_id='"+GameMainWindow.getUserID()+"';");
            stmt.close();  
            conn.close();
        }
        catch (SQLException ex)
        {
        }
    
    }
    
    public static String getMarksID() {
        String marks_id = null;
        
        try {
            String sql="SELECT marks_id FROM marks ORDER BY timestamp ASC;";
            
            conn = dbConnection.getConn();
            
            stmt = (Statement)conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                marks_id = rs.getString("marks_id");
            }
            
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(MarksModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return marks_id;
    }
    
    public static void ShowPupilRanking(JTable tableBestPupils) {
                
        try {
            String sql="SELECT CONCAT(pupils.fname,' ',pupils.lname) AS Name, marks.marks AS Marks FROM pupils,marks WHERE pupils.marks_id = marks.marks_id ORDER BY marks DESC LIMIT 10;";
            
            conn = dbConnection.getConn();
            
            stmt = (Statement)conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            tableBestPupils.setModel(DbUtils.resultSetToTableModel(rs));
            
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(MarksModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

}
