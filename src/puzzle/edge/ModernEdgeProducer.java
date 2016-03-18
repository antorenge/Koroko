package puzzle.edge;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import puzzle.storeage.JigsawPuzzleException;

/**
 * a attempt to create jigsaw puzzle piece edges freely used the idea of:
 * http://www.funphotoart.com/web-graphics/learn-to-make-jigsaw-puzzle-piece-made-of-painted-cardboard-like-material
 * 
 * 
 */

public class ModernEdgeProducer extends AbstractEdgeProducer {


	@Override
	public void _init() {
		randomDegrees();
		this.actualPoints = new Point[2 + walks]; // the 2 starting points and
	}

	@Override
	public void produce() throws JigsawPuzzleException {
		produceBaseline();
		produceCircle();
		recalculate();

		// build first shape
		GeneralPath one = new GeneralPath();
		one.moveTo(this.actualPoints[0].x, this.actualPoints[0].y);
		for (int i = 1; i < this.actualPoints.length; i++) {
			one.lineTo(this.actualPoints[i].x, this.actualPoints[i].y);
		}

		// build second shape
		int size = this.actualPoints.length;
		GeneralPath two = new GeneralPath();
		two.moveTo(this.actualPoints[size - 1].x, this.actualPoints[size - 1].y);
		for (int i = size - 1; i >= 0; i--) {
			two.lineTo(this.actualPoints[i].x, this.actualPoints[i].y);
		}
		two.transform(AffineTransform.getRotateInstance(Math.toRadians(180), 0, 0));

		this.twoShapes = new Shape[2];
		this.twoShapes[0] = one;
		this.twoShapes[1] = two;
	}
	
	// where to start
	private int startDegree = 130;
	// where to end
	private int endDegree = 410;
	// deviation value from startDegree, endDegree and STEP
	private int walks = -1;
	
	/**
	 * generate some random values
	 */
	private void randomDegrees() {
		final int maxDev = 2;
		this.startDegree = randomlyGenerateDeviationAbs(this.startDegree, maxDev);
		this.endDegree = randomlyGenerateDeviationAbs(this.endDegree, maxDev);
		this.walks = (endDegree - startDegree) / STEP;
	}

	/**
	 * will produce the points for indices 0,1, last and the one before the last
	 */
	private void produceBaseline() {
		// start plus end point
		int halfSideLength = sideLength / 2;

		// the hard first point
		this.actualPoints[0] = new Point(-halfSideLength, 0);

		// the last point follow from here
		int lastPoint = this.actualPoints.length - 1;

		// the hard last point - cannot change 'em
		this.actualPoints[lastPoint] = new Point(+halfSideLength, 0);
	}

	/**
	 * for going through the circle this is the stepwidth
	 */
	private static final int STEP = 10;

	/**
	 * produces the circle, so the bubble of the piece
	 * 
	 * @return
	 */
	private void produceCircle() {

		int xMultiply = generateRandomNumberAround();
		int yMultiply = generateRandomNumberAround();

		int count = 0; // the count for actualPoints (do not interfere with the
		// first and last two that represent the baseline)

		int translateY = randomlyGenerateDeviation(this.sideLength / 10, 50);

		/*
		 * here we have to use another apporach, because we should
		 * not overwrite the last point! old appraoch:
		 * for (int i = startDegree; i < endDegree; i += STEP) {
		 * 
		 */
		for (int i = startDegree; count < walks; i += STEP) { 
			count++;
			double radians = Math.toRadians(i);

			int x = (int) (Math.cos(radians) * (xMultiply));
			int y = (int) (Math.sin(radians) * (yMultiply));

			y -= (translateY); // translate a little
			this.actualPoints[count] = new Point(x, y);
		}
	}

	/**
	 * produces a random number roughly around the side length divided by 5
	 * 
	 * @return
	 */
	private int generateRandomNumberAround() {

		int around = this.sideLength / 5;

		int minus = (int) Math.round(around / 3d);
		int offRand = rand.nextInt((minus) + 1);
		return around - offRand;
	}

	/**
	 * returns a randomly generated number. The number is within the given
	 * maxProcentualDeviation from averageValue, e.g. for
	 * randomlyGenerateDeviation(100,20) will generate a number between 80 and
	 * 120. Obviously this won't work if averageValue is 0!
	 * 
	 * @param averageValue
	 * @param maxProcentualDeviation
	 * @return
	 */
	public static int randomlyGenerateDeviation(int averageValue,
			int maxProcentualDeviation) {
		if (averageValue == 0) { // we can never affect 0 by a muliplication.
			return 0;
		}
		boolean plusMinus = rand.nextBoolean();
		double deviationVal = rand.nextInt(maxProcentualDeviation + 1) / 100d;

		double deviation;
		if (plusMinus) {
			deviation = 1.0 + deviationVal;
		} else {
			deviation = 1.0 - deviationVal;
		}
		return (int) Math.round(deviation * averageValue);
	}
	
	public static int randomlyGenerateDeviationAbs(int averageValue, int maxDeviationAbs) {
		
		int dev = rand.nextInt((maxDeviationAbs*2)+1);
		dev -= maxDeviationAbs;
		return averageValue+dev;
		
	}

}
