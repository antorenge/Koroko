/*
 * PuzzleProperties.java
 *
 * C026-0256/2011
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle;

import java.awt.Color;
import java.util.ResourceBundle;

/**
 * 
 * @author Oroko Antony
 */
public class PuzzleProperties {

	public static final String APPLICATION_ICON_FILE = "/icon/puzzle.gif";
	
	public static final String GAME_STORAGE_VERSION = "1.0";
	
	public static final String GAME_VERSION = "1.0.0";
	
	public static final String GAME_BUILD_VERSION = "ver." + GAME_VERSION ;

	public static final int[] EDGE_LENGTH = { 20, 40, 60 };

	public static final double MAX_SNAP_DISTANCE = 20.0;

	public static final Color PIECE_COLOR = Color.BLACK;
	
	public static final Color PIECE_HIGHLIGHTED_COLOR = Color.GREEN;

	public static final Color PIECE_SHADOW_COLOR = new Color(128, 128, 128, 128);

	public static final Color BACKGROUND_COLOR = Color.white;

	/*
	public static final Stroke PIECE_STROKE = new BasicStroke(2.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0.0f);
	*/
	
	private static ResourceBundle resourceBundle;
	
	/**
	 * prefix for language file
	 */
	private static final String languageFilePrefix = "puzzleLanguage";

	/**
	 * the placeholder if no language has been loaded, or key has no value
	 */
	private static final String placeholder = "*";

	/**
	 * indicates whether the language has been loaded or not
	 */
	private static boolean languageInitiated;
	
	/**
	 * returns the local text String for this key
	 */
	public static String getLocalized(String key) {
    	String localValue = null;
    	if (languageInitiated) {
    		localValue = resourceBundle.getString(key);
    	} 
    	if (localValue == null) {
    		return placeholder;
    	} else if (localValue.equals("")) {
    		return "**********no value present**********";
    	} else {
    		return localValue;
    	}
    }
	
	public static void loadLocal() {
		resourceBundle = ResourceBundle.getBundle(languageFilePrefix);
		languageInitiated = true;
	}
	
	/** Creates a new instance of PuzzleProperties */
	private PuzzleProperties() {
		
	}

}
