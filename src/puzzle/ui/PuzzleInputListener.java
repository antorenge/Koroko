package puzzle.ui;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import puzzle.GameCommander;
import puzzle.Offset;
import puzzle.gameevent.GameEvent;
import puzzle.pieces.PuzzlePiece;
import puzzle.storeage.JigsawPuzzleException;

/**
 * 
 * @author Oroko Antony
 */

public class PuzzleInputListener extends KeyAdapter implements
		MouseMotionListener, MouseListener {

	private static Logger logger = Logger.getLogger(PuzzleInputListener.class);

	private GameCommander gC = GameCommander.getInstance();

	private PuzzlePiece selectedPiece;
	private Point lastPoint;
	private Point actPoint;

	// indicator for dragging:
	private boolean dragging;

	public PuzzleInputListener() {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// intentionally left empty

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// intentionally left empty
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// intentionally left empty
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F) {
			if (this.dragging) {
				dragged(currentPoint);
			} else {
				prepareDragging(currentPoint);
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (this.dragging) {
			dragging(e.getPoint());
		}
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (this.dragging)
				dragging(e.getPoint());
			else
				prepareDragging(e.getPoint());
		} else if (SwingUtilities.isRightMouseButton(e)) {
			if (gC.getPreferences().isAllowTurn() && !this.dragging)
				turnPiece(e.getPoint());
		} else if (SwingUtilities.isMiddleMouseButton(e)) {
			if (this.dragging)
				dragged(e.getPoint());
			else
				prepareDragging(e.getPoint());
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dragged(e.getPoint());

		}
	}

	private Point currentPoint;

	public void mouseMoved(MouseEvent e) {
		currentPoint = e.getPoint();

		if (this.dragging) {
			this.dragging(e.getPoint());

		} else {
			this.mouseHighlight(this.gC.getPieceDisposer().findbyPoint(
					e.getPoint()));
		}
	}

	/**
	 * turn a piece (usually turn it for 90 degrees)
	 * 
	 * @param p
	 */
	private void turnPiece(Point p) {
		try {
			this.actPoint = p;
			this.selectedPiece = this.gC.getPieceDisposer().findbyPoint(
					actPoint);
			if (selectedPiece == null)
				return;

			GameEvent prep = new GameEvent(
					GameEvent.State.PREPARE_TO_TURN_PIECE, selectedPiece);
			this.gC.deliverEvent(prep);

			selectedPiece.turnDegrees(actPoint, 90);

			GameEvent turn = new GameEvent(GameEvent.State.TURN_PIECE,
					selectedPiece);
			this.gC.deliverEvent(turn);
		} catch (JigsawPuzzleException e) {
			logger.error("while turning piece" + e.toString());
		}
	}

	private PuzzlePiece lastHighlighted = null;

	/**
	 * indicates highlighting of a puzzle piece.
	 * 
	 * @param pp
	 */
	private void mouseHighlight(PuzzlePiece pp) {

		// do nothing if the same again.
		if (pp == lastHighlighted)
			return;

		try {
			// reset the last highlighted one:
			if (lastHighlighted != null) {

				GameEvent ge = new GameEvent(
						GameEvent.State.PREPARE_TO_HIGHLIGHT_PIECE,
						lastHighlighted);
				this.gC.deliverEvent(ge);

				lastHighlighted.unhighlight();

				GameEvent ge2 = new GameEvent(GameEvent.State.HIGHLIGHT_PIECE,
						lastHighlighted);
				this.gC.deliverEvent(ge2);

				lastHighlighted = null;
			}
			// set the newly highlighted
			if (pp != null) {
				GameEvent ge = new GameEvent(
						GameEvent.State.PREPARE_TO_HIGHLIGHT_PIECE, pp);
				this.gC.deliverEvent(ge);

				pp.highlight();

				GameEvent ge2 = new GameEvent(GameEvent.State.HIGHLIGHT_PIECE,
						pp);
				this.gC.deliverEvent(ge2);

				lastHighlighted = pp;
			}
		} catch (JigsawPuzzleException e) {
			logger.error("while highlighting piece" + e.toString());
		}
	}

	/**
	 * drags the current piece to this position
	 */
	private void dragging(Point p) {
		// logger.debug("drag end: moving piece.");

		try {
			lastPoint = actPoint;
			actPoint = p;

			Offset tomove = new Offset(actPoint.x - lastPoint.x, actPoint.y
					- lastPoint.y);

			GameEvent prepareMove = new GameEvent(
					GameEvent.State.PREPARE_TO_MOVE_PIECE, selectedPiece);
			this.gC.deliverEvent(prepareMove);

			selectedPiece.move(tomove);

			GameEvent moved = new GameEvent(GameEvent.State.MOVE_PIECE,
					selectedPiece);
			this.gC.deliverEvent(moved);
		} catch (JigsawPuzzleException e) {
			logger.error("while dragging " + e.toString());
		}
	}

	/**
	 * prepares the dragging by storing the piece and the point.
	 * IN fact if no piece selected than don't prepare
	 * @param p
	 */
	private void prepareDragging(Point p) {
		// logger.debug("drag start: store point and piece.");
		PuzzlePiece pp = this.gC.getPieceDisposer().findbyPoint(p);
		
		if (pp != null) {
			this.actPoint = p;
			this.lastPoint = null;
			this.selectedPiece = pp;
			this.dragging = true;
		}
	}

	/**
	 * ends the dragging
	 * 
	 * @param p
	 * @throws JigsawPuzzleException
	 */
	private void dragged(Point p) {

		if (selectedPiece == null)
			return;

		try {
			lastPoint = actPoint;
			actPoint = p;

			GameEvent geMoved = new GameEvent(
					GameEvent.State.PREPARE_TO_MOVE_PIECE, selectedPiece);
			this.gC.deliverEvent(geMoved);

			GameEvent snap = null;
			GameEvent move = null;

			/*
			 * problem starts here, because you snap that piece possibly to
			 * another bigger piece and repaint this piece! - this results in
			 * repainting the single piece and not the new MultiPiece!
			 */
			PuzzlePiece pp = null;

			pp = selectedPiece.snap();

			if (pp != null) {
				int pieceNumber = this.gC.getPieceDisposer().getPieceCount();
				GameEvent ge = new GameEvent(
						GameEvent.State.PREPARE_TO_SNAP_PIECE, pieceNumber);
				this.gC.deliverEvent(ge);
				snap = new GameEvent(GameEvent.State.SNAP_PIECE, pieceNumber);
				move = new GameEvent(GameEvent.State.MOVE_PIECE, pp); // if
				// snapped
				// than
				// inform
				// about the
				// new piece
			} else {
				move = new GameEvent(GameEvent.State.MOVE_PIECE, selectedPiece); // if
				// not
				// snapped
				// than
				// inform
				// about
				// the
				// selected
				// piece
			}

			this.gC.deliverEvent(move);

			if (snap != null) {
				this.gC.deliverEvent(snap);
			}

			this.selectedPiece = null;
			this.actPoint = null;
			this.lastPoint = null;
			this.dragging = false;
		} catch (JigsawPuzzleException e) {
			logger.error("while drag ended" + e.toString());
		}
	}

}
