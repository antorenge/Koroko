/*
 * Offset.java
 *
 * Created on 27. August 2006, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;

/**
 * 
 * @author Oroko Antony
 */

public class Offset implements Storeable  {

	private int off_x;
	private int off_y;
	private boolean changed;

	public Offset(int x, int y) {
		this.off_x = x;
		this.off_y = y;
		this.changed = false;
	}
	
	/**
	 * use only for restoring
	 */
	public Offset() {
		
	}

	public int getX() {
		return this.off_x;
	}

	public int getY() {
		return this.off_y;
	}

	public void addToX(int xplus) {
		if (xplus != 0) {
			this.off_x += xplus;
			this.changed = true;
		}
	}

	public void addToY(int yplus) {
		if (yplus != 0) {
			this.off_y += yplus;
			this.changed = true;
		}
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public String toString() {
		return ("Offset: x->" + this.off_x + " y->" + this.off_y + " virgine: " + !this.changed);
	}
	
	public Point toPoint() {
		return new Point(off_x, off_y);
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node offset = StorageUtil.findDirectChildNode(current, "Offset");
		
		NamedNodeMap nnm = offset.getAttributes();
		Node n;
		
		n = nnm.getNamedItem("X");
		this.off_x = Integer.parseInt(n.getNodeValue());
		
		n = nnm.getNamedItem("Y");
		this.off_y = Integer.parseInt(n.getNodeValue());
		
		n = nnm.getNamedItem("Changed");
		this.changed = Boolean.parseBoolean(n.getNodeValue());
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Element offset = doc.createElement("Offset");
		
		offset.setAttribute("X", ""+this.off_x);
		offset.setAttribute("Y", ""+this.off_y);
		offset.setAttribute("Changed", ""+this.changed);
		current.appendChild(offset);
	}

}
