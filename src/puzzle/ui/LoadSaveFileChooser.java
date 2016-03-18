package puzzle.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import puzzle.PuzzleProperties;

public class LoadSaveFileChooser extends JFileChooser {
	
	private Component parent;
	
	private FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        PuzzleProperties.getLocalized("xmlSavagame"), "xml");

	
	public LoadSaveFileChooser(Component p) {
		this.parent = p;
		this.setFileFilter(this.filter);
	}
	
	public File openLoadDialog() {
		int ret = this.showOpenDialog(this.parent);
		if (ret == JFileChooser.APPROVE_OPTION) {
            return this.getSelectedFile();
        } else {
        	return null;
        }
	}
	
	public File openSaveDialog() {
		int ret = this.showSaveDialog(this.parent);
		if (ret == JFileChooser.APPROVE_OPTION) {
            return this.getSelectedFile();
        } else {
        	return null;
        }
	}

}
