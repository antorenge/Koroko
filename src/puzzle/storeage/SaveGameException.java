package puzzle.storeage;

public class SaveGameException extends JigsawPuzzleException {
	
	public SaveGameException() {
	}
	
	public SaveGameException(Throwable t) {
		super(t);
	}
	
	public SaveGameException(String message) {
		super(message);
	}
	
	public SaveGameException(String message, Throwable t) {
		super(message, t);
	}

}
