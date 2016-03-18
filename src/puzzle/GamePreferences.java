/*
 * GamePreferences.java
 */

package puzzle;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import puzzle.edge.AbstractEdgeProducer;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;
import puzzle.ui.PuzzleImage;

/**
 * Saves the relevant information for the actual game
 * 
 * @author Oroko Antony
 */
public class GamePreferences implements Storeable {
	
	private static final Logger logger = Logger.getLogger(GamePreferences.class);
	
	// START static OPTIONS
	
	private AbstractEdgeProducer edgeProducer;
	
	private int sideLength;

	private PuzzleImage image;

	private boolean allowTurn;

	private int columns;

	private int rows;
	
	private int initialPieces;

	private int shadowLength;
	
	private Stroke puzzleOutline;
	
	private boolean antiAliasing;

	// END static OPTIONS

	// DYNAMIC options
	private boolean showOutline;
	private boolean showShadow;
	private boolean highlight;
	private boolean sound;
	// DYNAMIC options
	
	public AbstractEdgeProducer getEdgeProducer() {
		return edgeProducer;
	}

	/*
	 * for internal restore usage only
	 */
	public GamePreferences() {
		
	}
	
	public GamePreferences(int sideLength, PuzzleImage img, boolean allowTurn,
			boolean showOutline, boolean showShadow, boolean highlight, boolean sound, AbstractEdgeProducer edgeProducer) {
		this.sideLength = sideLength;
		this.shadowLength = sideLength / 12;
		this.image = img;
		this.allowTurn = allowTurn;
		this.showOutline = showOutline;
		this.showShadow = showShadow;
		this.highlight = highlight;
		this.sound = sound;
		this.edgeProducer = edgeProducer;
		logger.debug("constructing new GamePreferences, sideLength:"+this.sideLength + ", shadow:"+this.showShadow + ", outline:"+this.showOutline+", producer:"+this.edgeProducer.getClass().getName());
	}

	public int getSideLength() {
		return this.sideLength;
	}

	public PuzzleImage getImage() {
		return this.image;
	}

	public int getColumns() {
		return this.columns;
	}

	public int getRows() {
		return this.rows;
	}
	
	public int getInitialPieces() {
		return this.initialPieces;
	}

	public boolean isAllowTurn() {
		return this.allowTurn;
	}
	
	public Stroke getOutlineStroke() {
		return this.puzzleOutline;
	}

	public void setShowOutline(boolean outline) {
		this.showOutline = outline;
	}
	
	public boolean isShowOutline() {
		return this.showOutline;
	}
	
	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public boolean isHighlight() {
		return highlight;
	}
	
	public boolean isAntiAliasing() {
		return antiAliasing;
	}

	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
	}

	public void setSound(boolean sound) {
		this.sound = sound;
	}
	
	public boolean isSound() {
		return this.sound;
	}

	public int getShadowLength() {
		return this.shadowLength;
	}

	public void setShowShadow(boolean shadow) {
		this.showShadow = shadow;
	}
	
	public boolean isShowShadow() {
		return showShadow;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	/**
	 * this calculates the initial pieces field
	 */
	public void calcInitialPieces() {
		this.initialPieces = this.rows * this.columns;
	}
	
	/**
	 * this calculates deduced attributes e.g.
	 * the columns and rows from the 
	 * knowledge of the image size and sideLength
	 * size as well as the stroke of pieces.
	 */
	public void calcDeducedAttributes() {
		if (this.image == null) {
			throw new NullPointerException("no image");
		}
		
		this.columns = this.image.getWidth() / this.sideLength;
		this.rows = this.image.getHeight() / this.sideLength;
		if ((this.columns < 1) || (this.rows < 1)) {
			throw new IllegalArgumentException("posive number expected");
		} // tests the things
		
		int thickness = this.sideLength / 20;
		this.puzzleOutline = new BasicStroke(thickness,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1f, null, 0.0f);

	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node preferencesNode = StorageUtil.findDirectChildNode(current, "Preferences");
		NamedNodeMap nnm = preferencesNode.getAttributes();
		Node item;
		item = nnm.getNamedItem("sideLength");
		this.sideLength = Integer.parseInt(item.getNodeValue());
		
		item = nnm.getNamedItem("allowTurn");
		this.allowTurn = Boolean.parseBoolean(item.getNodeValue());

		item = nnm.getNamedItem("columns");
		this.columns = Integer.parseInt(item.getNodeValue());
		
		item = nnm.getNamedItem("rows");
		this.rows = Integer.parseInt(item.getNodeValue());
		
		item = nnm.getNamedItem("initialPieces");
		this.initialPieces = Integer.parseInt(item.getNodeValue());
		
		item = nnm.getNamedItem("showOutline");
		this.showOutline = Boolean.parseBoolean(item.getNodeValue());
		
		item = nnm.getNamedItem("shadowLength");
		this.shadowLength = Integer.parseInt(item.getNodeValue());
		
		item = nnm.getNamedItem("showShadow");
		this.showShadow = Boolean.parseBoolean(item.getNodeValue());
		
		item = nnm.getNamedItem("sound");
		this.sound = Boolean.parseBoolean(item.getNodeValue());
		this.image = new PuzzleImage();
		this.image.restore(preferencesNode);
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Element preferences = doc.createElement("Preferences");
		preferences.setAttribute("sideLength", ""+this.sideLength);
		preferences.setAttribute("allowTurn", ""+this.allowTurn);
		preferences.setAttribute("columns", ""+this.columns);
		preferences.setAttribute("rows", ""+this.rows);
		preferences.setAttribute("initialPieces", ""+this.initialPieces);
		preferences.setAttribute("showOutline", ""+this.showOutline);
		preferences.setAttribute("shadowLength", ""+this.shadowLength);
		preferences.setAttribute("showShadow", ""+this.showShadow);
		preferences.setAttribute("sound", ""+this.sound);
		// PuzzleImage image store to preferences
		this.image.store(preferences);
		current.appendChild(preferences);
	}


}
