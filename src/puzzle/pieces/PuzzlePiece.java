/*
 * PuzzlePiece.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.pieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Vector;

import puzzle.GameCommander;
import puzzle.GamePreferences;
import puzzle.Offset;
import puzzle.PuzzleProperties;
import puzzle.Turnable;
import puzzle.edge.Edge;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.Storeable;

/**
 * 
 * @author Oroko Antony
 */

public abstract class PuzzlePiece implements Storeable, Turnable {

	/**
	 * Additional Gap for painting issues. If you do not provide 1-2 points at
	 * least, you cannot use the outline because it might be drawn outside the
	 * shape and then you get graphical bugs
	 */
	protected static final int GAP_X = 5;

	/**
	 * gap y
	 * VIEW
	 */
	protected static final int GAP_Y = 5;

	/**
	 * gap widht
	 * VIEW
	 */
	protected static final int GAP_WIDTH = 10;

	/**
	 * gap height
	 * VIEW
	 */
	protected static final int GAP_HEIGHT = 10;

	/**
	 * the shape (the outline shape of that puzzle piece)
	 * VIEW
	 */
	protected transient Shape puzzleShape;
	
	/**
	 * indicates if this puzzle piece is highlighted or not
	 * VIEW
	 */
	protected boolean highlighted = false;
	
	/**
	 * all the edges, that this piece has.
	 * MODEL
	 */
	protected transient List<Edge> edges;
	
	/**
	 * if piece is within this point true, false otherwise
	 * VIEW
	 */
	public abstract boolean isHit(Point punkt);

	/**
	 * set highlight
	 * VIEW
	 */
	public void highlight() {
		this.highlighted = true;
	}
	
	/**
	 * reset the highlight.
	 * VIEW
	 */
	public void unhighlight() {
		this.highlighted = false;
	}
	
	/**
	 * paints this piece in the given rectangle clipping area
	 * VIEW
	 */
	public final void renderInClip(Graphics2D g2d) throws JigsawPuzzleException {
		Rectangle rect = this.getBoundingRectangle();
		GamePreferences gp = GameCommander.getInstance().getPreferences();
		// shadow is the first
		if (gp.isShowShadow()) {
			renderShadowInClip(g2d, rect);
		}
		// than draw the face
		renderFaceInClip(g2d, rect);
		
		// finally draw the outline
		if (gp.isShowOutline() || (this.highlighted && gp.isHighlight())) {
			renderOutlineInClip(g2d, rect);
		}
		
	}

	/**
	 * draw the face of the puzzle piece
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	protected abstract void renderFaceInClip(Graphics2D g2d, Rectangle rect);

	/**
	 * draw the outlined Shape of the piece
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	protected void renderShadowInClip(Graphics2D g2d, Rectangle rect) {
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);

		int shadowLength = GameCommander.getInstance().getPreferences()
				.getShadowLength();
		AffineTransform mover = AffineTransform.getTranslateInstance(
				shadowLength, shadowLength);
		Shape shadowShape = mover.createTransformedShape(this.puzzleShape);
		g2d.setColor(PuzzleProperties.PIECE_SHADOW_COLOR);
		g2d.fill(shadowShape);
	}
	
	/**
	 * draws the outline of this shape within the clips
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	protected void renderOutlineInClip(Graphics2D g2d, Rectangle rect) {
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);
		GamePreferences gp = GameCommander.getInstance().getPreferences();

		g2d.setStroke(gp.getOutlineStroke());
		
		// set highlight or not.
		if (!this.highlighted || !gp.isHighlight())
			g2d.setColor(PuzzleProperties.PIECE_COLOR);
		else
			g2d.setColor(PuzzleProperties.PIECE_HIGHLIGHTED_COLOR);
		
		g2d.draw(this.puzzleShape);
	}

	/**
	 * returns a bounding box (mustn't be the smallest)
	 * VIEW
	 */
	public abstract Rectangle getBoundingRectangle() throws JigsawPuzzleException ;

	/**
	 * Test if this piece is within the given rectangle (either entirely or
	 * partially)
	 * VIEW
	 * @return false if there is no area that belongs to both (the specified
	 *         rect and the one from this piece)
	 */
	public abstract boolean isWithinRectangle(Rectangle rect);
	
	/**
	 * returns the shape
	 * VIEW
	 */
	public Shape getShape() throws JigsawPuzzleException {
		return this.puzzleShape;
	}
	
	/**
	 * a method to rebuild the shape which one can get with getShape() afterwards.
	 */
	protected abstract void buildShape() throws JigsawPuzzleException ;

	/**
	 * test if this piece is near (MAX_SNAP_DISTANCE) to another "brother"
	 * piece, if so it will snap to this one. Meaning that it will additionally
	 * move the remainig length to the other piece and returns true if something
	 * to snap to was found and this was snapped to, false otherwise
	 * @throws JigsawPuzzleException 
	 */

	public PuzzlePiece snap() throws JigsawPuzzleException {
		
		GameCommander gC = GameCommander.getInstance();

		List<Edge> myResolvableEdges = this.getResolvableEdges();
		List<PuzzlePiece> allPieces = gC.getPieceDisposer().getPuzzlePieces();
		for (Edge ownEdge : myResolvableEdges) { // for all own open edges

			// edgeNumber -> get contrary type
			int ownEdgeNumber = ownEdge.getEdgePairNumber();
			Edge.Type contraryEdgeType = Edge.contraryEdgeChar(ownEdge.getType());

			for (PuzzlePiece piece : allPieces) { // for every piece
				if (piece == this) {
					continue; // if same contine
				}
				// get all edges of the desired type (contraryEdgeType)
				List<Edge> list = piece.getResolvableEdges(contraryEdgeType);
				if (list.isEmpty()) {
					continue; // if none continue
				}
				// find the contrary edge by it's number
				Edge contraryEdge = null;
				for (Edge k : list) {
					if (k.getEdgePairNumber() == ownEdgeNumber) {
						contraryEdge = k;
						break;
					}
				}

				if (contraryEdge == null) {
					continue; // if the contrary Edge not in this piece go on
				}

				Point firstPoint = ownEdge.calculatePoint();
				Point secondPoint = contraryEdge.calculatePoint();

				final double dist = firstPoint.distance(secondPoint);

				if (dist < PuzzleProperties.MAX_SNAP_DISTANCE) {

					Offset offToFit = new Offset(secondPoint.x - firstPoint.x,
							secondPoint.y - firstPoint.y);

					this.move(offToFit);
					// retrieve the new piece
					PuzzlePiece pp = GameCommander.getInstance().getPieceDisposer().assamblyPieces(this, ownEdge,
									piece, contraryEdge);
					return pp;
				}
			}
		}
		return null;
	}

	/**
	 * to retrieve all edges
	 * MODEL
	 */
	public List<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * to retrieve all edges from the parameter type
	 * MODEL
	 */
	public abstract List<Edge> getEdges(Edge.Type typ);

	/**
	 * to retrieve a specific edge with the parameter number
	 * MODEL
	 */
	public Edge getEdge(int edgeNumber) {
		List<Edge> liste = this.getEdges();

		for (Edge k : liste) {
			if (k.getEdgePairNumber() == edgeNumber)
				return k;
		}
		return null;
	}

	/**
	 * retrieve edges that are not already closed!
	 * Also edges at the border of the puzzle
	 * are returned, because they will never be closed!
	 * MODEL
	 */
	public List<Edge> getOpenEdges() {
		List<Edge> liste = this.getEdges();
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if (k.isOpen())
				erg.add(k);
		}

		return erg;
	}
	
	/**
	 * retrieve an open edge with the specified number, or null if such an edge
	 * wasn't found
	 * MODEL
	 */
	public Edge getOpenEdge(int edgeNumber) {
		List<Edge> liste = this.getOpenEdges();

		for (Edge k : liste) {
			if (k.isOpen() && k.getEdgePairNumber() == edgeNumber)
				return k;
		}
		return null;

	}
	
	/**
	 * retrieve edges that have a counterpart in this game, and 
	 * are not already connected to that counterpart.
	 * MODEL
	 * @return
	 */
	public List<Edge> getResolvableEdges() {
		List<Edge> liste = this.getEdges();
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if (k.isResolvable())
				erg.add(k);
		}

		return erg;
	}

	/**
	 * retrieves the open resolvable(open and not NULL type) of the specified type
	 * MODEL
	 */
	public List<Edge> getResolvableEdges(Edge.Type edgeType) {
		List<Edge> liste = this.getEdges(edgeType);
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if ((k.isOpen()))
				erg.add(k);
		}
		return erg;
	}

	

	/**
	 * Retrieves 1 for single pieces, and the number of single pieces for multi
	 * pieces
	 * MODEL
	 */
	public abstract int getPieceCount();

	/**
	 * moves the piece the specified offset
	 * MODEL
	 */
	public abstract void move(Offset to);

}
