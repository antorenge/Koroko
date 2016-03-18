package puzzle.edge;

import puzzle.pieces.SinglePiece;

public class EdgeModel {
	
	/**
	 * the different types of edges
	 * 
	 */
	public static enum Type {
		TOP, BOTTOM, RIGHT, LEFT, NULL
	}

	/**
	 * the type of this edge
	 * MODEL
	 */
	private Type type;

	/**
	 * always two edges share the same number
	 * MODEL
	 */
	private int edgePairNumber;
	
	/**
	 * the piece for that edge
	 * MODEL
	 */
	private SinglePiece ownerPiece;

	/**
	 * indicates whether the ownerPiece was set
	 * MODEL
	 */
	private boolean isOwnerPieceSet;

	/**
	 * indicate where this edge has found his partner or not
	 * MODEL
	 */
	private boolean open;

}
