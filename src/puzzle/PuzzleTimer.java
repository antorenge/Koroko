/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package puzzle;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import puzzle.model.MarksModel;
import puzzle.ui.GameMainWindow;


public class PuzzleTimer {
    
    private static Timer timer;
    private static int i = 0;
    private static int marks;
    
    public PuzzleTimer() {
    
    }
    
    public static void StartTimer() {   
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask() {            
            public void run() {
                i++;                
                if(i==120) {
                    timer.cancel();
                    GameMainWindow.newgame();
                    JOptionPane.showMessageDialog(null, "Oops! Timed out. Play Again.");
                }                    
            }
        }, 0, 1000);
        
    }
    
    public static void StopTimer() {
        timer.cancel();
        
        try {
            MarksModel.insertMarks(marks(),i);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(PuzzleTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    public static void imStopTimer() {
        timer.cancel();
        resetCounter();
    }
    
    public static int marks () {  
        double a = (i/120.00);
        double mks = (100.00-(a*100.00));
        marks = (int)mks;
        return marks;
    }
    
    public static void resetCounter() {
        i = 0;
    }
    
    
}
