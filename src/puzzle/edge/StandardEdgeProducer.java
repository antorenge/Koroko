package puzzle.edge;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import puzzle.storeage.JigsawPuzzleException;

public class StandardEdgeProducer extends AbstractEdgeProducer {

	private float alphaLeft, alphaRight;
	
	/**
	 * the gap of the basic line where the neck will be entered later
	 */
	private int neckGap;

	/**
	 * this is the minimum space that should be left before any point of the bubble should begin.
	 */
	private int minimumSpaceFromVertex;

	/**
	 * constructor for serialisation loading
	 */
	public StandardEdgeProducer() {
	}

	public void _init() {
		this.neckGap = (int) (this.sideLength / 2.5d);
		this.actualPoints = new Point[12];
		this.minimumSpaceFromVertex = this.sideLength / 4;
	}

	public void produce() throws JigsawPuzzleException {
		// produce the first two points
		// left end
		this.actualPoints[0] = new Point(-sideLength / 2, 0);
		// right end
		this.actualPoints[11] = new Point(sideLength / 2, 0);
		
		this.generateRandomAlphas();
		this.produceBaseline();
		this.produceNeck();
		this.produceBubble();
		this.recalculate(); // turn if necessary
		this.generateShapes();
	}

	/**
	 * will produce the points for indices 1 and 10, needs the points indexed by
	 * 0 and 11 taken from old attempt
	 * @throws JigsawPuzzleException 
	 */
	private void produceBaseline() throws JigsawPuzzleException {

		// all points on baseline are at y = 0
		int yForPoints = 0;
		// the rest that results if you substract all necessary stuff from the whole side
		int restBaseLine = sideLength - neckGap - 2
				* minimumSpaceFromVertex;
		// random from the rest calculated to add to left, difference has to be added right
		int randomXToAddLeft = rand.nextInt(restBaseLine);
		int randomXToAddRight = restBaseLine - randomXToAddLeft;
		{
			// test if logic is correct
			int shouldBeSideLength = 2 * minimumSpaceFromVertex + randomXToAddLeft + randomXToAddRight
					+ neckGap;
			if (shouldBeSideLength != this.sideLength)
				throw new JigsawPuzzleException("inner logic is incorrect");
		}
		// the neck gap will always be the same!
		int xLeftPoint = actualPoints[0].x + minimumSpaceFromVertex + randomXToAddLeft;
		int xRightPoint = actualPoints[11].x - minimumSpaceFromVertex - randomXToAddRight;
		this.actualPoints[1] = new Point(xLeftPoint, yForPoints);
		this.actualPoints[10] = new Point(xRightPoint, yForPoints);
	}

	/**
	 * produces points of indices 2 and 9 using 0,1,10,11 static implementation
	 * for y values
	 */
	private void produceNeck() {
		// always add this to the points 1 and 10 to get the newer points
		int staticToAdd = (sideLength / 20) * 3;
		int xLeftPoint = this.actualPoints[1].x + staticToAdd;
		int xRightPoint = this.actualPoints[10].x - staticToAdd;
		int yLeftPoint = Math.round(this.alphaLeft);
		int yRightPoint = Math.round(this.alphaRight);
		this.actualPoints[2] = new Point(xLeftPoint, yLeftPoint);
		this.actualPoints[9] = new Point(xRightPoint, yRightPoint);
	}

	/**
	 * produces points 3 to 8 using 0-2 and 9-11
	 * @throws JigsawPuzzleException 
	 * 
	 */
	private void produceBubble() throws JigsawPuzzleException {
//		 die mittleren
		int y2_links = Math.round(2 * this.alphaLeft);
		int y2_rechts = Math.round((2 * this.alphaRight));

		int x2_links = this.actualPoints[1].x - this.actualPoints[2].x;
		if (x2_links > 0)
			throw new JigsawPuzzleException("Must not be bigger than zero!");

		int x2_rechts = this.actualPoints[10].x - this.actualPoints[9].x;
		if (x2_rechts < 0)
			throw new JigsawPuzzleException("Must not be smaller than 0!");

		this.actualPoints[3] = new Point(x2_links, y2_links);
		this.actualPoints[8] = new Point(x2_rechts, y2_rechts);

		// und die oberen in einer Geraden liegend, mit den passenden unteren
		// beiden
		int y3_links = Math.round(3 * this.alphaLeft);
		int y3_rechts = Math.round(3 * this.alphaRight);

		// gerade der Punkte [2] und [3] berechnen.
		float m_links = (float) ((float) this.actualPoints[3].y - (float) this.actualPoints[2].y)
				/ (float) (this.actualPoints[3].x - (float) this.actualPoints[2].x);
		float b_links = this.actualPoints[3].y - m_links * this.actualPoints[3].x;

		float m_rechts = ((float) this.actualPoints[10].y - (float) this.actualPoints[9].y)
				/ ((float) this.actualPoints[10].x - (float) this.actualPoints[9].x);
		float b_rechts = this.actualPoints[10].y - m_rechts * this.actualPoints[10].x;

		// und nun nur noch die x werte errechnen...
		int x3_links = Math.round((y3_links - b_links) / m_links);
		int x3_rechts = Math.round((y3_rechts - b_rechts) / m_rechts);

		this.actualPoints[4] = new Point(x3_links, y3_links);
		this.actualPoints[7] = new Point(-x3_rechts, y3_rechts);

		// nun nur noch die unkritischen spitzenpunkte der zwei seiten...
		int y4_links = Math.round(4 * this.alphaLeft);
		int y4_rechts = Math.round(4 * this.alphaRight);

		int x4_links = this.actualPoints[2].x + sideLength / 20;
		int x4_rechts = this.actualPoints[9].x + sideLength / 20;

		this.actualPoints[5] = new Point(x4_links, y4_links);
		this.actualPoints[6] = new Point(x4_rechts, y4_rechts);
	}
	
	private void generateRandomAlphas() {
		this.alphaRight = -1 * this.sideLength / 10
				* (rand.nextFloat() / 2 + 0.8f);
		float max = this.sideLength / 10 * (0.5f + 0.8f);
		float temp = 4 * alphaRight;
		boolean add_or_substract = rand.nextBoolean();

		float seed = (max + this.alphaRight);
		if (seed < 1)
			seed *= 10;
		// if (seed < 1) seed *= 10;
		if (seed < 1)
			this.alphaLeft = this.alphaRight;
		else {
			int rando = rand.nextInt((int) seed);

			float difference = rando / 10;
			if (add_or_substract)
				this.alphaLeft = (temp + difference) / 4;
			else
				this.alphaLeft = (temp - difference) / 4;
		}
	}
	
	

	/**
	 * concatenates the calculated points to two shapes and saves them
	 * 
	 */
	private void generateShapes() {
		this.twoShapes = new Shape[2];

		GeneralPath one = new GeneralPath();
		one.moveTo(actualPoints[0].x, actualPoints[0].y);
		one.lineTo(actualPoints[1].x, actualPoints[1].y);
		one.quadTo(actualPoints[2].x, actualPoints[2].y, actualPoints[3].x,
				actualPoints[3].y);
		one.quadTo(actualPoints[4].x, actualPoints[4].y, actualPoints[5].x,
				actualPoints[5].y);
		one.lineTo(actualPoints[6].x, actualPoints[6].y);
		one.quadTo(actualPoints[7].x, actualPoints[7].y, actualPoints[8].x,
				actualPoints[8].y);
		one.quadTo(actualPoints[9].x, actualPoints[9].y, actualPoints[10].x,
				actualPoints[10].y);
		one.lineTo(actualPoints[11].x, actualPoints[11].y);
		this.twoShapes[0] = one;

		GeneralPath two = new GeneralPath();
		two.moveTo(actualPoints[11].x, actualPoints[11].y);
		two.lineTo(actualPoints[10].x, actualPoints[10].y);
		two.quadTo(actualPoints[9].x, actualPoints[9].y, actualPoints[8].x,
				actualPoints[8].y);
		two.quadTo(actualPoints[7].x, actualPoints[7].y, actualPoints[6].x,
				actualPoints[6].y);
		two.lineTo(actualPoints[5].x, actualPoints[5].y);
		two.quadTo(actualPoints[4].x, actualPoints[4].y, actualPoints[3].x,
				actualPoints[3].y);
		two.quadTo(actualPoints[2].x, actualPoints[2].y, actualPoints[1].x,
				actualPoints[1].y);
		two.lineTo(actualPoints[0].x, actualPoints[0].y);
		two.transform(AffineTransform.getRotateInstance(Math.toRadians(180), 0,
				0));
		this.twoShapes[1] = two;

	}

}
