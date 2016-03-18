package puzzle.storeage;

public class LoadGameException extends JigsawPuzzleException {
	
	public LoadGameException() {
	}
	
	public LoadGameException(Throwable t) {
		super(t);
	}
	
	public LoadGameException(String message) {
		super(message);
	}
	
	public LoadGameException(String message, Throwable t) {
		super(message, t);
	}

}
