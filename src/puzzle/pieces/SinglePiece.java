/*
 * SinglePiece.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.pieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import puzzle.GameCommander;
import puzzle.Offset;
import puzzle.edge.Edge;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;

/**
 * 
 * @author Oroko Antony
 */
public class SinglePiece extends PuzzlePiece {

	private static final Logger logger = Logger.getLogger(SinglePiece.class);

	private BufferedImage image;

	private Point center;

	// can be serialzed using the image and recalculateTexture
	private TexturePaint pieceContent;

	private Edge topEdge;

	private Edge bottomEdge;

	private Edge rightEdge;

	private Edge leftEdge;

	/**
	 * @param edges
	 *            indices: 0 = top, 1 = bottom, 2 = right, 3 = left always
	 *            provide a 4 field array
	 * @param image
	 *            the image part for this single piece
	 * @param center
	 *            center of the piece
	 * @throws JigsawPuzzleException
	 */
	public SinglePiece(Edge[] edges, BufferedImage image, Point center)
			throws JigsawPuzzleException {

		// some errors to avoid
		if (edges == null)
			throw new NullPointerException("edge null");
		if (edges.length != 4)
			throw new IllegalArgumentException(
					"only 4 field arrays are allowd...");
		if (center == null)
			throw new NullPointerException("center null");
		if (image == null)
			throw new NullPointerException("null image");
		if ((edges[0] == null) && (edges[1] == null))
			throw new IllegalArgumentException(
					"no top and no bottom edge - the puzzle is one dimensional ?");
		if ((edges[2] == null) && (edges[3] == null))
			throw new IllegalArgumentException(
					"no right and no left edges - the puzzle is one dimensional?");

		this.topEdge = edges[0];
		this.bottomEdge = edges[1];
		this.rightEdge = edges[2];
		this.leftEdge = edges[3];

		this.image = image;
		this.center = center;
		this.puzzleShape = new GeneralPath();

		initEdges();
		buildShape();
		initEdgeList();
		recalculateTexture();
	}

	/**
	 * constructor should only be used for restoring
	 */
	public SinglePiece() {

	}

	private void initEdgeList() {
		this.edges = new ArrayList<Edge>();
		this.edges.add(topEdge);
		this.edges.add(bottomEdge);
		this.edges.add(rightEdge);
		this.edges.add(leftEdge);
	}

	/**
	 * inits the edges by setting their offsets, setting the owner
	 * 
	 * @throws JigsawPuzzleException
	 * 
	 */
	private void initEdges() throws JigsawPuzzleException {
		final int sideLength = GameCommander.getInstance().getPreferences()
				.getSideLength();

		Offset relativeToCenter;
		/*
		 * initializes the four edges to fit together in a generalpath of this
		 * piece
		 */

		// start topEdge
		if (this.topEdge == null) {
			GeneralPath path = new GeneralPath();
			path.moveTo(-(sideLength / 2), 0);
			path.lineTo(sideLength / 2, 0);
			this.topEdge = new Edge(Edge.Type.NULL, -1, path);
		}
		topEdge.setOwnerPiece(this);
		relativeToCenter = new Offset(0, -(sideLength / 2));
		topEdge.setOffset(relativeToCenter); // means that I have the absolute value in the shape of the edge!
		topEdge.move(new Offset(this.center.x + relativeToCenter.getX(),
				this.center.y + relativeToCenter.getY()));
		// end topEdge

		// start rightEdge
		if (this.rightEdge == null) {
			GeneralPath path = new GeneralPath();
			path.moveTo(0, -sideLength / 2);
			path.lineTo(0, sideLength / 2);
			this.rightEdge = new Edge(Edge.Type.NULL, -1, path);
		}
		rightEdge.setOwnerPiece(this);
		relativeToCenter = new Offset(sideLength / 2, 0);
		rightEdge.setOffset(relativeToCenter);
		rightEdge.move(new Offset(this.center.x + relativeToCenter.getX(),
				this.center.y + relativeToCenter.getY()));
		// end rightEdge

		// start bottomEdge
		if (this.bottomEdge == null) {
			GeneralPath path = new GeneralPath();
			path.moveTo(-(sideLength / 2), 0);
			path.lineTo(sideLength / 2, 0);
			this.bottomEdge = new Edge(Edge.Type.NULL, -1, path);
		}
		bottomEdge.setOwnerPiece(this);
		relativeToCenter = new Offset(0, sideLength / 2);
		bottomEdge.setOffset(relativeToCenter);
		bottomEdge.move(new Offset(this.center.x + relativeToCenter.getX(),
				this.center.y + relativeToCenter.getY()));
		// end bottomEdge

		// start leftEdge
		if (this.leftEdge == null) {
			GeneralPath path = new GeneralPath();
			path.moveTo(0, sideLength / 2);
			path.lineTo(0, -(sideLength / 2));
			this.leftEdge = new Edge(Edge.Type.NULL, -1, path);
		}
		leftEdge.setOwnerPiece(this);
		Offset v = new Offset(-(sideLength / 2), 0);
		leftEdge.setOffset(v);
		leftEdge.move(new Offset(this.center.x + v.getX(), this.center.y
				+ v.getY()));
		// end leftEdge
	}

	@Override
	protected void buildShape() throws JigsawPuzzleException {
		//logger.debug("build shape");
		GeneralPath gp = new GeneralPath();
		gp.append(topEdge.getShape(), true);
		gp.append(rightEdge.getShape(), true);
		gp.append(bottomEdge.getShape(), true);
		gp.append(leftEdge.getShape(), true);
		this.puzzleShape = gp;
	}

	@Override
	protected void renderFaceInClip(Graphics2D g2d, Rectangle rect) {
		
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);
		g2d.setPaint(this.pieceContent);
		g2d.fill(this.puzzleShape);
		
		// use this code to enable debugging of edges!
		/*
		g2d.setColor(Color.orange);
		g2d.drawRect(this.center.x-1, this.center.y-1, 2, 2);
		
		g2d.setColor(Color.cyan);
		Point current = this.topEdge.calculatePoint();
		g2d.drawRect(current.x-1, current.y-1, 2, 2);
		g2d.draw(this.topEdge.getShape());
		
		g2d.setColor(Color.blue);
		current = this.rightEdge.calculatePoint();
		g2d.drawRect(current.x-1, current.y-1, 2, 2);
		g2d.draw(this.rightEdge.getShape());
		
		g2d.setColor(Color.red);
		current = this.bottomEdge.calculatePoint();
		g2d.drawRect(current.x-1, current.y-1, 2, 2);
		g2d.draw(this.bottomEdge.getShape());
		
		g2d.setColor(Color.green);
		current = this.leftEdge.calculatePoint();
		g2d.drawRect(current.x-1, current.y-1, 2, 2);
		g2d.draw(this.leftEdge.getShape());
		*/
	}

	/**
	 * recalculate the texture under this piece, has to be called if the piece
	 * should be moved.
	 */
	private void recalculateTexture() {
		Rectangle r = new Rectangle(this.center.x - this.image.getWidth() / 2,
				this.center.y - this.image.getHeight() / 2, this.image
						.getWidth(), this.image.getHeight());

		this.pieceContent = new TexturePaint(this.image, r);
	}

	@Override
	public boolean isHit(Point punkt) {
		if (this.puzzleShape.contains(punkt))
			return true;
		return false;
	}

	@Override
	public void move(Offset to) {
		//logger.debug("move singlePiece");
		center.x = center.x + to.getX();
		center.y = center.y + to.getY();
		AffineTransform af = AffineTransform.getTranslateInstance(to.getX(), to
				.getY());
		
		for (Edge e : this.edges) {
			e.move(to); // does a coordinates transformation for every edge!
		}

		this.puzzleShape = af.createTransformedShape(this.puzzleShape); // again you transform the whole shape once
		this.recalculateTexture();
	}

	@Override
	public List<Edge> getEdges(Edge.Type typ) {
		List<Edge> liste = this.getEdges();
		List<Edge> erg = new Vector<Edge>();
		for (Edge k : liste) {
			if (k.getType() == typ) {
				erg.add(k);
				return erg;
			}
		}
		return erg;
	}

	@Override
	public int getPieceCount() {
		return 1;
	}

	/**
	 * return a copy of the center point.
	 */
	public Point getPoint() {
		int x = center.x;
		int y = center.y;
		return new Point(x, y);
	}

	@Override
	public void turnDegrees(Point turnPoint, int degree) {
		// TODO problem this only supports turning 90 degrees!
		logger.debug("rotating singlePiece");

		AffineTransform turner = AffineTransform.getRotateInstance(Math
				.toRadians(degree), turnPoint.x, turnPoint.y);
		this.puzzleShape = turner.createTransformedShape(this.puzzleShape); // has to be recalulated using buildShape()

		// TODO in these methods we also have problems with the type of an edge
		this.topEdge.turnDegrees(turnPoint, degree);
		this.leftEdge.turnDegrees(turnPoint, degree);
		this.bottomEdge.turnDegrees(turnPoint, degree);
		this.rightEdge.turnDegrees(turnPoint, degree);
		
		// TODO that actually is the problem because you don't know how to set these values!
		Edge temp = this.topEdge;
		topEdge = leftEdge;
		leftEdge = bottomEdge;
		bottomEdge = rightEdge;
		rightEdge = temp;

		// recalculate new center point
		if (turnPoint != this.center) {
			Point2D newCenter2D = turner.transform(this.center, null);
			Point newCenter = new Point((int) newCenter2D.getX(),
					(int) newCenter2D.getY());
			this.center = newCenter;
		}

		// turn the picture also
		rotateImage(degree);
		
		// recalculate because the image points may have been changed
		this.recalculateTexture();
	}

	@Override
	public boolean isWithinRectangle(Rectangle rect) {
		Rectangle myrect = this.getBoundingRectangle();
		if (rect.contains(myrect))
			return true;
		if (rect.intersects(myrect))
			return true;

		return false;
	}

	@Override
	public Rectangle getBoundingRectangle() {
		Rectangle newmy = this.puzzleShape.getBounds();

		return new Rectangle(newmy.x - GAP_X, newmy.y - GAP_Y, newmy.width
				+ GAP_WIDTH, newmy.height + GAP_HEIGHT);
	}

	private void rotateImage(int degrees) {
		int oldWidth = this.image.getWidth();
		int oldHeight = this.image.getHeight();
		// a new image with widthNew = heightOld, heightNew = widthNew, and same
		// color type
		BufferedImage newImage = new BufferedImage(oldHeight, oldWidth,
				this.image.getType());

		// rotation through center point of new image
		AffineTransform at = AffineTransform.getRotateInstance(Math
				.toRadians(degrees), (newImage.getWidth() / 2), (newImage
				.getHeight() / 2));

		AffineTransform translationTransform;

		Point2D p2d_in = new Point2D.Double(0, 0);
		Point2D p2d_out = at.transform(p2d_in, null);

		double ytrans = p2d_out.getY(), xtrans = ytrans;
		translationTransform = AffineTransform.getTranslateInstance(-xtrans,
				-ytrans);
		at.preConcatenate(translationTransform);

		Graphics2D g = (Graphics2D) newImage.getGraphics();
		g.setTransform(at);
		g.drawImage(this.image, 0, 0, null);
		g.dispose();
		this.image = newImage;
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node singlePieceNode = StorageUtil.findDirectChildNode(current,
				"SinglePiece");

		// recreate the image
		byte[] data = StorageUtil.restoreBinaryData(singlePieceNode,
				"PieceImage");
		ByteArrayInputStream imageByteStream = new ByteArrayInputStream(data);
		try {
			// done creating the image
			this.image = ImageIO.read(imageByteStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		}

		// load the center
		NamedNodeMap nnm = singlePieceNode.getAttributes();
		Node item;
		item = nnm.getNamedItem("centerX");
		int centerX = Integer.parseInt(item.getNodeValue());
		item = nnm.getNamedItem("centerY");
		int centerY = Integer.parseInt(item.getNodeValue());
		this.center = new Point(centerX, centerY);
		// got the center

		// get the edges
		Node topE = StorageUtil.findDirectChildNode(singlePieceNode, "TopEdge");
		if (topE.hasChildNodes()) {
			this.topEdge = new Edge();
			this.topEdge.restore(topE);
			try {
				this.topEdge.setOwnerPiece(this);
			} catch (JigsawPuzzleException e) {
				throw new LoadGameException(e);
			}
		}

		Node bottomE = StorageUtil.findDirectChildNode(singlePieceNode,
				"BottomEdge");
		if (bottomE.hasChildNodes()) {
			this.bottomEdge = new Edge();
			this.bottomEdge.restore(bottomE);
			try {
				this.bottomEdge.setOwnerPiece(this);
			} catch (JigsawPuzzleException e) {
				throw new LoadGameException(e);
			}
		}

		Node rightE = StorageUtil.findDirectChildNode(singlePieceNode,
				"RightEdge");
		if (rightE.hasChildNodes()) {
			this.rightEdge = new Edge();
			this.rightEdge.restore(rightE);
			try {
				this.rightEdge.setOwnerPiece(this);
			} catch (JigsawPuzzleException e) {
				throw new LoadGameException(e);
			}
		}

		Node leftE = StorageUtil.findDirectChildNode(singlePieceNode,
				"LeftEdge");
		if (leftE.hasChildNodes()) {
			this.leftEdge = new Edge();
			this.leftEdge.restore(leftE);
			try {
				this.leftEdge.setOwnerPiece(this);
			} catch (JigsawPuzzleException e) {
				throw new LoadGameException(e);
			}
		}

		// finally fill the edgelist and the texture Paint
		try {
			buildShape();
		} catch (JigsawPuzzleException e) {
			throw new LoadGameException(e);
		}
		initEdgeList();
		recalculateTexture();
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Element singlePiece = doc.createElement("SinglePiece");

		ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
		// write the image in the format to the byte stream array
		try {
			ImageIO.write(this.image, "png", imageByteStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SaveGameException(e);
		}
		StorageUtil.storeBinaryData(singlePiece, "PieceImage", imageByteStream
				.toByteArray());
		// center
		singlePiece.setAttribute("centerX", "" + this.center.x);
		singlePiece.setAttribute("centerY", "" + this.center.y);

		// topEdge
		Element topE = doc.createElement("TopEdge");
		if (topEdge != null) {
			topEdge.store(topE);
		}
		singlePiece.appendChild(topE);

		// bottomEdge
		Element bottomE = doc.createElement("BottomEdge");
		if (bottomEdge != null) {
			bottomEdge.store(bottomE);
		}
		singlePiece.appendChild(bottomE);

		// rightEdge
		Element rightE = doc.createElement("RightEdge");
		if (rightEdge != null) {
			rightEdge.store(rightE);
		}
		singlePiece.appendChild(rightE);

		// leftEdge
		Element leftE = doc.createElement("LeftEdge");
		if (leftEdge != null) {
			leftEdge.store(leftE);
		}
		singlePiece.appendChild(leftE);

		current.appendChild(singlePiece);
	}

}
