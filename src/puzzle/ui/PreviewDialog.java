package puzzle.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import puzzle.PuzzleProperties;

public class PreviewDialog extends JDialog {

	private BufferedImage img;
	private JLabel label;

	public PreviewDialog(Frame owner) {
		super();
		setTitle(PuzzleProperties.getLocalized("viewMenuPreview"));
		setResizable(false);
		
		this.label = new JLabel();
		//getContentPane().setLayout(new BorderLayout());
		getContentPane().add(this.label);
		
		pack();
	}

	/**
	 * load this image (having right size) into preview
	 * @param img
	 */
	public void loadImage(BufferedImage img) {
		this.img = img;
		// TODO find out where to find the "real" size of the decoration and use
		Dimension size = new Dimension(this.img.getWidth(null) + 1, this.img
				.getHeight(null) + 24);
		
		Icon i = new ImageIcon(img);
		this.label.setIcon(i);
		this.setAlwaysOnTop(true);
		this.setSize(size);
		this.setPreferredSize(size);
	}
	
	/**
	 * unload the image
	 */
	public void unloadImage() {
		this.img = null;
		this.label.setIcon(null);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (this.img != null) {
			super.setVisible(b);
		}
	}
}
