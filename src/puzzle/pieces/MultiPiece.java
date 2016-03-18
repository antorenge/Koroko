package puzzle.pieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import puzzle.Offset;
import puzzle.edge.Edge;
import puzzle.edge.Edge.Type;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;

/**
 * 
 * @author Oroko Antony
 */
public class MultiPiece extends PuzzlePiece {

	private static final Logger logger = Logger.getLogger(MultiPiece.class);

	private List<SinglePiece> singlePieces;

	public MultiPiece(SinglePiece singlePiece1, SinglePiece singlePiece2) throws JigsawPuzzleException {
		if ((singlePiece1 == null) || (singlePiece2 == null))
			throw new JigsawPuzzleException("one piece null");

		this.singlePieces = new Vector<SinglePiece>();
		this.edges = new ArrayList<Edge>();

		this.addSinglePiece(singlePiece1);
		this.addSinglePiece(singlePiece2);
	}

	/**
	 * use only for storage
	 */
	public MultiPiece() {
		this.singlePieces = new Vector<SinglePiece>();
		this.edges = new ArrayList<Edge>();
		this.puzzleShape = new Area();
	}

	@Override
	public boolean isHit(Point point) {
		for (SinglePiece E : singlePieces) {
			if (E.isHit(point))
				return true;
		}
		return false;
	}

	@Override
	protected void renderFaceInClip(Graphics2D g2d, Rectangle rect) {
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);
		// first paint all singlePieces
		for (SinglePiece sp : singlePieces) {
			sp.renderFaceInClip(g2d, rect);
		}
	}

	@Override
	public List<Edge> getEdges(Edge.Type typ) {
		List<Edge> list = this.getEdges();
		List<Edge> ret = new Vector<Edge>();
		for (Edge k : list) {
			if (k.getType() == typ)
				ret.add(k);
		}
		return ret;
	}

	@Override
	public int getPieceCount() {
		return singlePieces.size();
	}

	final double[] passVariable = new double[6];

	/**
	 * returns the start and end points for this generalPath the firstPoint is
	 * the one and only moveTo segment in this path the lastPoint is the last
	 * lineTo segment in this path
	 * 
	 * @param currentPath
	 * @throws JigsawPuzzleException 
	 */
	protected Point[] getPoints(Shape shape) throws JigsawPuzzleException {
		// logger.debug("getPoints");
		int ret;
		Point firstPoint = null;
		Point lastPoint = null;

		PathIterator pIt = shape.getPathIterator(null);

		while (!pIt.isDone()) {
			ret = pIt.currentSegment(passVariable);
			if (ret == PathIterator.SEG_MOVETO) { // the moveto is only done
													// once!
				if (firstPoint != null) {
					throw new JigsawPuzzleException(
							"this path has more than one moveTo");
				}
				firstPoint = new Point((int) passVariable[0],
						(int) passVariable[1]);
			} else if (ret == PathIterator.SEG_LINETO) { // lineto maybe done
															// more often but
															// the last lineto
															// is the important
															// thing!
				lastPoint = new Point((int) passVariable[0],
						(int) passVariable[1]);
			}
			pIt.next();
		}
		if (firstPoint == null) {
			throw new JigsawPuzzleException("did not find the first point!");
		}
		if (lastPoint == null) {
			throw new JigsawPuzzleException("did not find the last point!");
		}
		return new Point[] { firstPoint, lastPoint };
	}

	/**
	 * now this version can handle holes in puzzle pieces.
	 * TODO there is an issue here: the pieces shape looks different 
	 * as the lines are not moved to each other as in the initial idea.
	 * @throws JigsawPuzzleException 
	 */
	@Override
	protected void buildShape() throws JigsawPuzzleException {
		logger.info("buildShape - advanced version");

		/* the resultingArea means the resultingShape
		 * and a list of shapes for the smaller shapes
		 */
		List<Shape> shapes = new ArrayList<Shape>();
		Area resultingArea = new Area();

		// inits a shrinking list of edges - to be sure to hit all the edges!
		List<Edge> allEdges = new ArrayList<Edge>();
		allEdges.addAll(this.getOpenEdges());

		while (allEdges.size() != 0) { // hopefully use all the edges
			logger.debug("going to find an outline");
			
			// init the first edge
			Edge currentEdge = allEdges.get(0);
			shapes.add(currentEdge.getShape());
			allEdges.remove(currentEdge);
			Point[] pts = getPoints(currentEdge.getShape());
			Point currentPoint = pts[1];
			Point endPoint = pts[0];
			
			boolean isCompleted = false;
			while (!isCompleted) { // for every complete shape outline!
				for (int i = 0; i < allEdges.size(); i++) { // through all puzzle pieces left
					Edge nextEdge = allEdges.get(i);

					Point[] nextEdgePts = getPoints(nextEdge.getShape());

					/*
					 * we should not connect a BOTTOM with a TOP or a RIGHT with
					 * a LEFT edge that is not valid. start test
					 */
					Edge.Type currentType = currentEdge.getType();
					Edge.Type nextType = nextEdge.getType();
					boolean rightLeft = (currentType == Type.RIGHT)
							&& (nextType == Type.LEFT);
					boolean leftRight = (currentType == Type.LEFT)
							&& (nextType == Type.RIGHT);
					boolean topBottom = (currentType == Type.TOP)
							&& (nextType == Type.BOTTOM);
					boolean bottomTop = (currentType == Type.BOTTOM)
							&& (nextType == Type.TOP);

					if (rightLeft || leftRight || topBottom || bottomTop) {
						logger
								.debug("tried to connect LEFT-RIGHT or TOP-BOTTOM that's invalid");
						continue;
					}
					// end of the test

					// test if we have the right edge:
					boolean normal = currentPoint.equals(nextEdgePts[0]);
					// normal the end of the currentEdge is same as the start of
					// the next
					boolean reversed = currentPoint.equals(nextEdgePts[1]);
					// not normal but possible the end of the current is the end
					// of the next -> mirrored
					if (normal ^ reversed) { // XOR bitwise or logical has no
												// difference in this context
						if (normal) {
							currentPoint = nextEdgePts[1]; // the next end point
							shapes.add(nextEdge.getShape());
						} else if (reversed) {
							currentPoint = nextEdgePts[0]; // the next start
															// point
							Shape reversedShape = ShapeUtil
									.createReversed(nextEdge.getShape());
							shapes.add(reversedShape);
						}
						
						logger.debug("found following pair current:"
								+ currentEdge.toString() + ", next:"
								+ nextEdge.toString());

						/*
						 * test if were once through one outline (additional
						 * inner or outer outlines maybe there but are not
						 * connected to this shape!
						 */
						if (endPoint.equals(nextEdgePts[1])
								|| endPoint.equals(nextEdgePts[0])) {
							logger.info("found the endpoint - one shape is done!");
							isCompleted = true;
						}
						
						// here the shit goes to the next iteration
						allEdges.remove(nextEdge);
						currentEdge = nextEdge;
						break;
					}

				}
			} // a complete outline is done! - maybe there are more outlines.
			/*
			 * here an area has always to be created because one will begin
			 * with "some" edge it is not clear if this is the outer outline
			 * or an inner outline so always create areas here and than add
			 * the other areas to it.
			 */
			GeneralPath gp = new GeneralPath();
			for (int i = 0; i < shapes.size(); i++) {
				gp.append(shapes.get(i), true);
				// maybe we have to close the shape here!
			}
			shapes.clear();
			resultingArea.exclusiveOr(new Area(gp));
		}
		this.puzzleShape = resultingArea;
		logger.info("end buildShape - advanced version");
	}

	/**
	 * this version cannot handle holes in the structure - than it hangs.
	 */
	//@Override
	protected void buildShape2() throws JigsawPuzzleException { 
											// the structure
		logger.info("buildShape - simple version");

		List<Shape> shapes = new ArrayList<Shape>();

		// inits a shrinking list of edges - to be sure to hit all the edges!
		List<Edge> allEdges = new ArrayList<Edge>();
		allEdges.addAll(this.getOpenEdges());

		// init the first edge
		Edge currentEdge = allEdges.get(0);
		shapes.add(currentEdge.getShape());
		allEdges.remove(currentEdge);
		Point currentPoint = getPoints(currentEdge.getShape())[1];

		while (allEdges.size() != 0) {

			for (int i = 0; i < allEdges.size(); i++) {
				Edge nextEdge = allEdges.get(i);
				Point[] nextEdgePts = getPoints(nextEdge.getShape());

				// test if we have the right edge:
				boolean normal = currentPoint.equals(nextEdgePts[0]);// normal
																		// next
				boolean mirrored = currentPoint.equals(nextEdgePts[1]);// not
				
				if (normal ^ mirrored) { // XOR bitwise or logical has no
											// difference in this context

					if (normal) {
						currentPoint = nextEdgePts[1]; // the next end point
						shapes.add(nextEdge.getShape());
					} else if (mirrored) {
						currentPoint = nextEdgePts[0]; // the next start point
						Shape mirror = ShapeUtil.createReversed(nextEdge
								.getShape());
						/*
						 * AffineTransform MIRROR =
						 * AffineTransform.getScaleInstance(-1, -1); Shape
						 * mirror =
						 * MIRROR.createTransformedShape(nextEdge.getShape());
						 */
						shapes.add(mirror);
					}

					// here the shit goes to the next iteration
					allEdges.remove(nextEdge);
					currentEdge = nextEdge;
					break;
				}

			}
		}
		// build all the shapes together
		GeneralPath gp = new GeneralPath();
		for (int i = 0; i < shapes.size(); i++) {
			gp.append(shapes.get(i), true);
		}
		this.puzzleShape = gp;
	}

	/**
	 * this version does not work properly as it does not put the edge shapes together appropriately.
	 */
	//@Override
	protected void buildShape_2() throws JigsawPuzzleException {
		//logger.info("buildShape");
		GeneralPath gp = new GeneralPath();

		int c2 = 0;
		for (Edge e : this.getOpenEdges()) {
			c2++;
			logger.info("openEdges: " + e.getType());
			gp.append(e.getShape(), true);

		}
		this.puzzleShape = gp;
	}

	@Override
	public void move(Offset to) {
		for (SinglePiece e : singlePieces) {
			e.move(to);
		}
		AffineTransform af = AffineTransform.getTranslateInstance(to.getX(), to
				.getY());
		this.puzzleShape = af.createTransformedShape(this.puzzleShape);
	}

	private List<SinglePiece> getSinglePieces() {
		return this.singlePieces;
	}

	private void addSinglePiece(SinglePiece piece) {
		this.singlePieces.add(piece);
		this.edges.addAll(piece.getEdges());
	}

	public void addPiece(PuzzlePiece pieceToAdd) throws JigsawPuzzleException {
		if (pieceToAdd instanceof SinglePiece) {
			addSinglePiece((SinglePiece) pieceToAdd);
		} else if (pieceToAdd instanceof MultiPiece) {
			for (SinglePiece actPiece : ((MultiPiece) pieceToAdd)
					.getSinglePieces()) {
				addSinglePiece((SinglePiece) actPiece);
			}
		}
		this.buildShape();
	}

	@Override
	public boolean isWithinRectangle(Rectangle rect) {
		for (SinglePiece e : singlePieces) {
			if (e.isWithinRectangle(rect))
				return true;
		}
		return false;
	}

	@Override
	public Rectangle getBoundingRectangle() throws JigsawPuzzleException {
		Rectangle ret = this.getShape().getBounds();
		// see GAPs for explanation
		return new Rectangle(ret.x - GAP_X, ret.y - GAP_Y, ret.width
				+ GAP_WIDTH, ret.height + GAP_HEIGHT);
	}

	@Override
	public void turnDegrees(Point turnPoint, int degree) {
		for (SinglePiece e : singlePieces) {
			e.turnDegrees(turnPoint, degree);
		}
		AffineTransform af = AffineTransform.getRotateInstance(Math
				.toRadians(degree), turnPoint.x, turnPoint.y);

		this.puzzleShape = af.createTransformedShape(this.puzzleShape);
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node multiPiece = StorageUtil
				.findDirectChildNode(current, "MultiPiece");
		NodeList nodeList = multiPiece.getChildNodes();

		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node act = nodeList.item(i);
			if (act.getNodeName().equals("MP-SP")) {
				SinglePiece sp = new SinglePiece();
				sp.restore(act);
				this.addSinglePiece(sp);
			} else {
				throw new LoadGameException("irregular xml format");
			}
		}
		try {
			this.buildShape();
		} catch (JigsawPuzzleException e) {
			logger.error("could not buildShape: " + e.toString());
			throw new LoadGameException(e);
		}
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Node multiPiece = doc.createElement("MultiPiece");

		Node single;
		for (SinglePiece sp : this.singlePieces) {
			single = doc.createElement("MP-SP");
			sp.store(single);
			multiPiece.appendChild(single);
		}

		current.appendChild(multiPiece);
	}

}