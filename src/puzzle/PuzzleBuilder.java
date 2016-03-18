/*
 * PuzzleBuilder.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import puzzle.edge.Edge;
import puzzle.pieces.PuzzlePiece;
import puzzle.pieces.SinglePiece;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.ui.GameMainWindow;

/**
 * @author Oroko Antony
 */
public class PuzzleBuilder {

	private PuzzlePiece[][] pieceArray;

	/**
	 * this variable increases every time a new piece is created until the
	 * maximum index is reached
	 */
	private int edgeNumberIndex = 0;

	private List<Point> startPointList;

	private boolean isStartPointListInit;

	private Random rand;

	public PuzzleBuilder() {
		this.rand = new Random();
		this.isStartPointListInit = false;
	}

	public void makePieces(final int columns, final int rows) throws JigsawPuzzleException {
		if ((rows < 1) || (columns < 1))
			throw new IllegalArgumentException(
					"positive number for columns and rows expected");

		Edge[][][] edgeArray = new Edge[columns][rows][4];

		for (int columnIndex = 0; columnIndex < columns; columnIndex++) { // columns
			for (int rowIndex = 0; rowIndex < rows; rowIndex++) { // rows

				// left edge:
				if (columnIndex == 0)
					edgeArray[columnIndex][rowIndex][3] = null; // links = 3
				else {
					int ident = edgeArray[columnIndex - 1][rowIndex][2]
							.getEdgePairNumber(); // rechts = 2
					GeneralPath zweiter = GameCommander.getInstance()
							.getEdgeDisposer().findContraryEdgeShape(ident,
									Edge.Type.LEFT);
					edgeArray[columnIndex][rowIndex][3] = new Edge(
							Edge.Type.LEFT, ident, zweiter); // links = 3
				}

				// top edge:
				if (rowIndex == 0)
					edgeArray[columnIndex][rowIndex][0] = null; // oben = 0
				else {
					int ident = edgeArray[columnIndex][rowIndex - 1][1]
							.getEdgePairNumber(); // unten = 1
					GeneralPath zweiter = GameCommander.getInstance()
							.getEdgeDisposer().findContraryEdgeShape(ident,
									Edge.Type.TOP);
					edgeArray[columnIndex][rowIndex][0] = new Edge(
							Edge.Type.TOP, ident, zweiter); // 0 = Oben
				}

				// right edge:
				if (columnIndex == columns - 1)
					edgeArray[columnIndex][rowIndex][2] = null;
				else {
					int ident = edgeNumberIndex;
					GeneralPath sh = GameCommander.getInstance()
							.getEdgeDisposer().generateNewEdgeShape(ident,
									Edge.Type.RIGHT);
					edgeNumberIndex++;
					edgeArray[columnIndex][rowIndex][2] = new Edge(
							Edge.Type.RIGHT, ident, sh); // rechts = 2;
				}

				// bottom edge:
				if (rowIndex == rows - 1)
					edgeArray[columnIndex][rowIndex][1] = null;
				else {
					int ident = edgeNumberIndex;
					GeneralPath sh = GameCommander.getInstance()
							.getEdgeDisposer().generateNewEdgeShape(ident,
									Edge.Type.BOTTOM);
					edgeNumberIndex++;
					edgeArray[columnIndex][rowIndex][1] = new Edge(
							Edge.Type.BOTTOM, ident, sh); // rechts = 2;
				}
			}
		}

		pieceArray = new PuzzlePiece[columns][rows];
		initStartpoints(columns, rows);
		for (int s = 0; s < columns; s++) {
			for (int z = 0; z < rows; z++) {

				pieceArray[s][z] = new SinglePiece(edgeArray[s][z],
						GameCommander.getInstance().getPreferences().getImage()
								.getImage(s, z), getRandomPointFromList());
				GameCommander.getInstance().getPieceDisposer().addPuzzleStueck(
						pieceArray[s][z]);

			}
		}

		if (GameCommander.getInstance().getPreferences().isAllowTurn())
			turnRandom();

	}

	private void initStartpoints(int spalten, int zeilen) {
		this.startPointList = new Vector<Point>();
		int x = 0;
		int y = 0;

		final int sideLength = GameCommander.getInstance().getPreferences()
				.getSideLength();

		for (int s = 0; s < spalten; s++) {
			x += 2 * sideLength;
			for (int z = 0; z < zeilen; z++) {
				y += 2 * sideLength;
				startPointList.add(new Point(x, y));
			}
			y = 0;
		}
		Point last = startPointList.get(startPointList.size() - 1);

		// some boundaries
		int xbound = last.x + 400;
		int ybound = last.y + 200;

		GameMainWindow.getInstance().setBoundaries(xbound, ybound);

		// the above thing no =============================
		this.isStartPointListInit = true;
	}

	/**
	 * if this is a game where turning pieces is allowed, this procedure will
	 * randomly turn the pieces
	 */
	private void turnRandom() throws JigsawPuzzleException {
		for (PuzzlePiece[] PSA : pieceArray) {
			for (PuzzlePiece ps : PSA) {
				// generate random number of turns
				int turns = rand.nextInt(4);

				while (turns > 0) {
					ps.turnDegrees(((SinglePiece)ps).getPoint(), 90);
					turns--;
				}

			}
		}
	}

	private Point getRandomPointFromList() {
		if (!isStartPointListInit)
			throw new RuntimeException("pointlist not been initiated");

		int random = rand.nextInt(this.startPointList.size());

		Point pt = startPointList.get(random);
		startPointList.remove(random);

		return pt;

	}

}
