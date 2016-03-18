/*
 * Main.java
 *
 */

package puzzle;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import puzzle.ui.GameMainWindow;

/**
 *
 * @author antoroko
 */
public class Main extends Thread {
	
    private static Logger logger = Logger.getLogger(Main.class);
    private String arg;
    private String ind;
    private String [][] users = new String[100][10];
    
    /** Creates a new instance of Main
     * @param args
     * @param index
     * @param us */
    public Main(String args, String index,String [][] us) {
        arg=args;
        ind=index;
        users = us;
    }
    
    @Override
    public void run (){    	
        setLookAndFeel();
    	logger.debug("loading properties");
    	PuzzleProperties.loadLocal();        
    	logger.debug("loading Window");           
        GameMainWindow.startUI(arg,ind,users);
        
    }

	private static void setLookAndFeel() {
		try {
			LookAndFeel[] lnfs = UIManager.getAuxiliaryLookAndFeels();
			if (lnfs != null) {
				for (LookAndFeel lnf : lnfs)
					System.out.println(lnf);
			}

			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
