/*
 * GamePanel.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import puzzle.GameCommander;
import puzzle.PuzzleProperties;
import puzzle.gameevent.GameEvent;
import puzzle.gameevent.GameEventListener;
import puzzle.pieces.PuzzlePiece;
import puzzle.storeage.JigsawPuzzleException;

/**
 * 
 * @author Oroko Antony
 */
public class GamePanel extends JPanel implements GameEventListener {
	
	private Logger logger = Logger.getLogger(GamePanel.class);
	
	// offscreenImage from the double buffer
	private Image offscreenImage;
	
	private final PuzzleInputListener inputListener;

	public PuzzleInputListener getInputListener() {
		return inputListener;
	}

	private final GameCommander gC = GameCommander.getInstance();
	
	/** Creates a new instance of GamePanel */
	public GamePanel() {
		this.inputListener = new PuzzleInputListener();
		this.addMouseListener(this.inputListener);
		this.addMouseMotionListener(this.inputListener);		
		GameCommander.getInstance().addListener(this);
	}

	/**
	 * paints the offscreen to screen buffer
	 */
	/*
	public void paint(Graphics g) {
		super.paint(g);

		if (this.gC.isGameRunning()) {
			if (GameCommander.getInstance().getPreferences().isAntiAliasing())
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if ((offscreenImage != null))
				g.drawImage(offscreenImage, 0, 0, null);
		}
	}
	*/
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.gC.isGameRunning()) {
			if (GameCommander.getInstance().getPreferences().isAntiAliasing())
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if ((offscreenImage != null))
				g.drawImage(offscreenImage, 0, 0, null);
		}
		
	}

	/**
	 * deletes a specific piece from the drawable area.
	 * it does that by simply deleting the area and
	 * drawing all pieces which are in the area of 
	 * that piece again.
	 * @param actPs the piece to delete
	 */
	private void deleteFromArea(PuzzlePiece actPs) throws JigsawPuzzleException {
		Rectangle clippingArea = actPs.getBoundingRectangle();

		Graphics2D offscreenPainter = (Graphics2D) offscreenImage.getGraphics()
				.create();
		// clear the area first
		offscreenPainter.setClip(clippingArea);
		offscreenPainter.setColor(PuzzleProperties.BACKGROUND_COLOR);
		offscreenPainter.fill(clippingArea);
		
		if (GameCommander.getInstance().getPreferences().isAntiAliasing())
			offscreenPainter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw 'em now
		for (PuzzlePiece ps : GameCommander.getInstance().getPieceDisposer().getPuzzlePieces()) {
			if (ps.isWithinRectangle(clippingArea) && ps != actPs) {
				ps.renderInClip(offscreenPainter);
			}
		}

		offscreenPainter.dispose();
		this.repaint(clippingArea);
	}

	/**
	 * draws exactly only that piece into its drawable area
	 * @param piece the piece to draw
	 */
	private void drawToArea(PuzzlePiece piece) throws JigsawPuzzleException {
		Rectangle clippingArea = piece.getBoundingRectangle();
		Graphics2D offscreenPainter = (Graphics2D) offscreenImage.getGraphics()
				.create();
		offscreenPainter.setClip(clippingArea);
		piece.renderInClip(offscreenPainter);
		offscreenPainter.dispose();
		this.repaint(clippingArea);
	}

	/**
	 * called to create new image and render it within it's given coordinates.
	 * @throws JigsawPuzzleException 
	 */
	public void reRender() throws JigsawPuzzleException {
		Graphics2D dbg;

		boolean deleteImageContent = false;		

		// do this only once, or if the image has changed size!
		if ((offscreenImage == null) || (!isSameSize(offscreenImage, this.getPreferredSize()))) {
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration();			
			offscreenImage = gc.createCompatibleImage(this.getSize().width, this.getSize().height);
			deleteImageContent = true;
		} 
		// getting graphics context.
		dbg = (Graphics2D) offscreenImage.getGraphics();
		
		if (deleteImageContent) { // do that if only renewing!
			dbg.clearRect(0, 0, this.offscreenImage.getWidth(null), this.offscreenImage.getHeight(null));
		}

		// paint background
		dbg.setColor(PuzzleProperties.BACKGROUND_COLOR);
		dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

		// fill the pic with the pieces
		List<PuzzlePiece> Puzzlestuecke = GameCommander.getInstance().getPieceDisposer()
				.getPuzzlePieces();
		for (PuzzlePiece ps : Puzzlestuecke) {
			ps.renderInClip(dbg);
		}
		dbg.dispose();
		this.repaint();
	}
	
	/**
	 * finds out if the image has the same size as the Dimension
	 * @param i
	 * @param d
	 * @return
	 */
	private boolean isSameSize(Image i, Dimension d) {		
		int imageWidth = i.getWidth(null);
		int imageHeight = i.getHeight(null);
		
		int dimWidth = d.width;
		int dimHeight = d.height;
		
		if ((imageWidth == dimWidth) && (imageHeight == dimHeight)) {
			return true;
		} else {
			return false;
		}
	}

	public Dimension getPreferredSize() {
		return this.getSize();
	}

	public void eventHappened(GameEvent ge) throws JigsawPuzzleException {
		switch (ge.getType()) {
		case PREPARE_TO_TURN_PIECE:
		case PREPARE_TO_MOVE_PIECE:
		case PREPARE_TO_HIGHLIGHT_PIECE:
			this.deleteFromArea((PuzzlePiece)ge.getInfo());
			break;
		case TURN_PIECE:
		case MOVE_PIECE:
		case HIGHLIGHT_PIECE:
			this.drawToArea((PuzzlePiece)ge.getInfo());
			break;
		default:
			break;
		}
		
	}
}
