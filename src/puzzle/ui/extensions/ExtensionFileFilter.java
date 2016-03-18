package puzzle.ui.extensions;

import java.io.File;
import java.util.List;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

import puzzle.PuzzleProperties;

/**
 * This class is a FileFilter that filters
 * files for their extensions.
 * E.g. it is capable of finding all .jpg
 * files within a directory.
 * Use it in JFileChooser.setFileFilter
 * @author Oroko Antony
 */
public class ExtensionFileFilter extends FileFilter {
    
	/**
	 * the standard delimiter which delimits the filename
	 * from it's extension!
	 */
	private final static char standardDelimiter = '.';
	
	/**
     * the list of extensions
     */
    private List<String> extensions;
    
    /**
     * if true than this FileFilter accepts
     * directories (shows them one can go into)
     * if false than this is not allowed
     */
    private boolean acceptDirectorys;
    
    public ExtensionFileFilter() {
        this.extensions = new Vector<String>();
        this.acceptDirectorys = true;
    }
    
    public void addExtension(String extension) {
        String toadd = extension.toLowerCase();
        for (String st: extensions) {
            if (st.equalsIgnoreCase(toadd)) return;
        }
        this.extensions.add(toadd);
    }
    
    public void removeExtension(String extension) {
        String toadd = extension.toLowerCase();
        for (String st: extensions) {
            if (st.equalsIgnoreCase(toadd)) {
                extensions.remove(st);
            };
        }
    }
    
    public boolean acceptsDirectorys() {
        return this.acceptDirectorys;
    }
    
    public void setDirectoryAccept(boolean da) {
        this.acceptDirectorys = da;
    }
    
    public boolean accept(File f) {
        
        if ((this.acceptDirectorys) && f.isDirectory()) return true;
        String filename = f.getName().toLowerCase(); 
        for (String st: extensions) {
            if (filename.length() > st.length()) {
                String last = filename.substring(filename.length()-st.length()-1);
                if (!last.startsWith(standardDelimiter+"")) continue;
                if (st.equalsIgnoreCase(last.substring(1))) return true;
            }
            
        }
        return false;
    }
    
    public String getDescription() {
        StringBuffer StB = new StringBuffer();

        if (!this.extensions.isEmpty()) {
            StB.append(PuzzleProperties
            		.getLocalized("acceptedExtensions"));
            
            for (String st: extensions) {
                StB.append(standardDelimiter+st+" ");
            }
        }
        return new String(StB);
    }
    
}
