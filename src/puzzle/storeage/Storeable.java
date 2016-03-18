package puzzle.storeage;


import org.w3c.dom.Node;


/**
 * to be able to store certain objects
 * @author Oroko Antony
 *
 */
public interface Storeable {
	
	/**
	 * store objects into the given element path
	 * @param current
	 * @throws SaveGameException
	 */
	public void store(Node current) throws SaveGameException;
	
	/**
	 * restore objects directly from the given element path
	 * @param current
	 * @throws LoadGameException
	 */
	public void restore(Node current) throws LoadGameException;

}
