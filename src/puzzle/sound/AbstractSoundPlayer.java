package puzzle.sound;

import puzzle.gameevent.GameEventListener;

public interface AbstractSoundPlayer extends GameEventListener {
	
	public void playSnap();

	public void playTurn();

	public void playWon();
                

}
