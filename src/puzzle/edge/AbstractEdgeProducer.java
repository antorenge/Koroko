package puzzle.edge;

import java.awt.Point;
import java.awt.Shape;
import java.util.Random;

import puzzle.storeage.JigsawPuzzleException;

/**
 * interface for all producers of edges. The way they go is first call
 * constructor to construct new object, than init the object with some parameters
 * that are predefined and wouldn't be changed in the process, later call
 * produce as often as you need to produce several edges that suit to your
 * predefinitions made trough the init method. At last call the getShapes method
 * this method will give you the produces edges as a contrary pair
 * 
 * @author Oroko Antony
 * 
 */

public abstract class AbstractEdgeProducer {
	
	/**
	 * a random number generator - the key thing for production
	 */
	protected static final Random rand = new Random();
	
	/**
	 * the points for the actual shape
	 */
	protected Point[] actualPoints;

	/**
	 * if this is a male one = true, otherwise if female on = false
	 */
	protected boolean isBubble;

	/**
	 * the two generated shapes are saved here
	 */
	protected Shape[] twoShapes;
	
	/**
	 * length of a side
	 */
	protected int sideLength;

	/**
	 * this should init this produces with the predifined side length
	 */
	public void init(int sideLength) {
		this.sideLength = sideLength;
		this.isBubble = rand.nextBoolean();
		_init();
	}
	
	/**
	 * delegation of init method if additional initiation stuff has to be done
	 */
	protected abstract void _init();

	/**
	 * this should produce a random edge, might be called as often as needed to
	 * produce new edges from given type (type given by init)
	 * 
	 */
	public abstract void produce() throws JigsawPuzzleException;

	/**
	 * retrieves the two shapes generated by this producer through the produce
	 * procedure, no special order the first might be a male or female type
	 * 
	 * @return a two fielded array containing the shapes
	 */
	public Shape[] getBothShapes() {
		return this.twoShapes;
	}
	
	/**
	 * this method recalculates the points of the actualPoints list
	 * to get a female piece.
	 */
	protected void recalculate() {
		if (!this.isBubble) {
			// turnsTheActualPoint Ys -> to get the female
			for (Point p : this.actualPoints) {
				p.y *= -1;
			}
		}
	}

}
