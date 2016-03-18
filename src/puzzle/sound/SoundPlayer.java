package puzzle.sound;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

import puzzle.GameCommander;
import puzzle.gameevent.GameEvent;

/**
 * a class that is capable of playing sounds in an
 * asynchronous way (uses a new thread to play)
 * @author Oroko Antony
 *
 */
public class SoundPlayer implements AbstractSoundPlayer {

	private static final Logger logger = Logger.getLogger(SoundPlayer.class); 
	
	/**
	 * paths to all sound resources
	 */
	private String[] waves = { "/audio/winnertrack.wav", "/audio/pop.wav", 
                                    "/audio/clickstop.wav", "/audio/fngrsnap.wav"};

	/**
	 * a separate thread for a player
	 */
	private ThreadPlayer[] player;

	/**
	 * random
	 */
	private Random rng;

	public SoundPlayer() {
		this.player = new ThreadPlayer[3];
		this.rng = new Random();

		// init all audio files
		for (int i = 0; i < this.player.length; i++) {
			this.player[i] = new ThreadPlayer(getClass().getResource(
					this.waves[i]));
		}
	}

        @Override
	public void playSnap() {
		play(1);
	}

        @Override
	public void playTurn() {
		play(2);
	}

        @Override
	public void playWon() {
//		int what = rng.nextInt(3);
		play(0);
	}
                
	private void play(int index) {
		boolean active = GameCommander.getInstance().getPreferences().isSound();
		if (!active) return; // ignore the event if no sound activated!
		
		this.player[index].start();
		this.player[index] = new ThreadPlayer(getClass().getResource(
				this.waves[index]));
	}

	/**
	 * the thread player	 
	 */
	static class ThreadPlayer extends Thread {

		private AudioInputStream stream = null;

		private SourceDataLine sl = null;

		private byte buffer[] = null;
		private int numBytesRead = 0;
		
		private boolean error = false;

		ThreadPlayer(URL url) {

			try {
				this.stream = AudioSystem.getAudioInputStream(url);
				
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						this.stream.getFormat());
				this.sl = (SourceDataLine) AudioSystem.getLine(info);
				
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				this.error = true;
				logger.error("exception in SoundPlayer" + e);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				this.error = true;
				logger.error("exception in SoundPlayer" + e);
			}catch (IOException e) {
				e.printStackTrace();
				this.error = true;
				logger.error("exception in SoundPlayer" + e);
			}
		}

		public void run() {
			if (this.error) return;
			try {
				this.sl.open();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				logger.error("exception in SoundPlayer" + e);
				return;
			}
			this.sl.start();

			this.buffer = new byte[256];

			while (true) {
				try {
					this.numBytesRead = this.stream.read(this.buffer, 0, 256);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("exception in SoundPlayer" + e);
				}
				if (this.numBytesRead == -1)
					break;

				this.sl.write(this.buffer, 0, this.buffer.length);
			}
			this.sl.drain();
			this.sl.stop();
			this.sl.close();
			return;
		}
	}

	public void eventHappened(GameEvent ge) {
		switch (ge.getType()) {
		case START_GAME:
                    
                    break;
		case LOAD_GAME:
			// donots
			break;
		case END_GAME:
			playWon();
			break;
		case MOVE_PIECE:
			break;
		case PREPARE_TO_SNAP_PIECE:
			playSnap();
			break;
		case TURN_PIECE:
			playTurn();
			break;
		}
	}

}
