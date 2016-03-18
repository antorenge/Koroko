package puzzle.pieces;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import puzzle.storeage.JigsawPuzzleException;

public class ShapeUtil {
	
	private static final Logger logger = Logger.getLogger(ShapeUtil.class);

	
	/**
	 * create a reversed version of this shape
	 * @param s
	 * @return
	 * @throws JigsawPuzzleException
	 */
	public static Shape createReversed(Shape s) throws JigsawPuzzleException {
		
		logger.debug("attempting to reverse a shape");
		
		// defined innerclass
		class PathSegment {
			final double[] pts;
			final int type;

			public PathSegment(double[] pts, int type) {
				this.pts = pts;
				this.type = type;
				logger.info("instantiting a PathSegment: " + pts[0] + ", " + pts[1] + "-type:" + type);
			}
		}
		
		List<PathSegment> segments = new ArrayList<PathSegment>();
		PathIterator p = s.getPathIterator(null);

		PathSegment seg;
		double[] passVar = new double[6];
		while (!p.isDone()) {
			switch (p.currentSegment(passVar)) {
			case PathIterator.SEG_LINETO:
				seg = new PathSegment(copy(passVar, 2),
						PathIterator.SEG_LINETO);
				break;
			case PathIterator.SEG_MOVETO:
				seg = new PathSegment(copy(passVar, 2),
						PathIterator.SEG_MOVETO);
				break;
			case PathIterator.SEG_QUADTO:
				seg = new PathSegment(copy(passVar, 4),
						PathIterator.SEG_QUADTO);
				break;
			case PathIterator.SEG_CUBICTO:
				seg = new PathSegment(copy(passVar, 6),
						PathIterator.SEG_CUBICTO);
				break;
			case PathIterator.SEG_CLOSE:
				seg = new PathSegment(copy(passVar, 0),
						PathIterator.SEG_CLOSE);
				break;
			default:
				throw new JigsawPuzzleException("invalid");
			}
			segments.add(seg);
			p.next();
		}

		GeneralPath gp = new GeneralPath();

		final int size = segments.size();
		PathSegment ps;
		for (int i = size - 1; i >= 0; i--) {
			logger.info("i:"+i);
			ps = segments.get(i);
			switch (ps.type) {
			case PathIterator.SEG_LINETO:
				if (i == size - 1) {
					gp.moveTo(ps.pts[0], ps.pts[1]);
					continue;
				}// else go on to SEG_MOVETO
			case PathIterator.SEG_MOVETO:
				gp.lineTo(ps.pts[0], ps.pts[1]);
				break;
			case PathIterator.SEG_QUADTO:
				// 4 means 2 points
				gp.quadTo(ps.pts[2], ps.pts[3], ps.pts[0], ps.pts[1]);
				// TODO this leads to squared bubbles somehow
				break;
			case PathIterator.SEG_CUBICTO:
				// 6 means 3 points
				// TODO this is unkown behaviour because it's not used!
				gp.curveTo(ps.pts[4], ps.pts[5], ps.pts[2], ps.pts[3], ps.pts[0], ps.pts[1]);
				break;
			default:
				throw new JigsawPuzzleException("invalid segment");
			}
		}
		return gp;
	}

	/**
	 * creates a copy of the double array, this method copies as much values as
	 * amount says.
	 * 
	 * @param org
	 * @param amount
	 * @return
	 */
	protected static double[] copy(double[] org, int amount) {
		double[] ret = new double[amount];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = org[i];
		}
		return ret;
	}

}
