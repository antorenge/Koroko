package puzzle.edge;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;

import puzzle.storeage.JigsawPuzzleException;

/**
 * This producer produces "no" edges meaning that all edges are simple lines between
 * two points. This will result in quadratic pieces in the game.
 *
 */
public class FlatEdgeProducer extends AbstractEdgeProducer {

	@Override
	protected void _init() {
		this.actualPoints = new Point[2];
	}

	@Override
	public void produce() throws JigsawPuzzleException {
		int halfSideLength = sideLength / 2;
		this.actualPoints[0] = new Point(-halfSideLength, 0);
		this.actualPoints[1] = new Point(+halfSideLength, 0);
		
		this.twoShapes = new Shape[2];
		
		Shape s = new Line2D.Float(this.actualPoints[0], this.actualPoints[1]);
		this.twoShapes[0] = this.twoShapes[1] = s;
	}

}
