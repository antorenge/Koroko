package puzzle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import puzzle.edge.EdgeDisposer;
import puzzle.gameevent.GameEvent;
import puzzle.gameevent.GameEventListener;
import puzzle.gameevent.GameState;
import puzzle.pieces.PuzzlePieceDisposer;
import puzzle.sound.AbstractSoundPlayer;
import puzzle.sound.SoundPlayer;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;
import puzzle.ui.GameMainWindow;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * 
 * @author Oroko Antony
 */
public class GameCommander extends GameState implements GameEventListener,
		Storeable {
	
	private static Logger logger = Logger.getLogger(GameCommander.class);

	private static GameCommander This;

	private PuzzleBuilder builder; // should be final at the end!

	private final AbstractSoundPlayer soundPlayer;

	private final PuzzlePieceDisposer pieceDisposer;

	private EdgeDisposer edgeDisposer;

	private GamePreferences preferences;

	private boolean gameRunning;
        
        private String imageName; 
        
	public boolean isGameRunning() {
		return gameRunning;
	}

	/** Creates a new instance of GameCommander */
	private GameCommander() {
		super();
		this.addListener(this);

		This = this;
		this.builder = new PuzzleBuilder();
		this.soundPlayer = new SoundPlayer();
		this.addListener(this.soundPlayer);
		this.pieceDisposer = new PuzzlePieceDisposer();
		this.edgeDisposer = new EdgeDisposer();
		this.gameRunning = false;
	}

	public void testForGameEnd() throws JigsawPuzzleException {
		if (this.pieceDisposer.ends()) {                        

                    PuzzleTimer.StopTimer();
                    int marks = PuzzleTimer.marks();
                    
                    Pronounce.display(imageName,marks);                                                
                    //play voice
                    sound("/audio/action/"+imageName+".wav");

                    GameEvent ge = new GameEvent(GameEvent.State.END_GAME, null);
                    this.deliverEvent(ge); 
                   
		}
                
	}
	
        public void setImageName(String imgname) {
            this.imageName = imgname;
        }
        
        public final void sound(String path) throws JigsawPuzzleException {
            InputStream in;
            try{
                in= this.getClass().getResourceAsStream(path);
                AudioStream stream=new AudioStream(in);
                AudioPlayer.player.start(stream);
                }
                catch(IOException e){
                    e.printStackTrace();
                }                         
        }
        
	@Override
	public void deliverEvent(GameEvent ge) throws JigsawPuzzleException {
		if (this.gameRunning)
			super.deliverEvent(ge);
	}

	/*
	 * START OF xxxGame
	 */
	public void resetGame() {
		this.builder = new PuzzleBuilder();
		this.pieceDisposer.reset();
		this.edgeDisposer.reset();
	}

	public void newGame(GamePreferences pref) throws JigsawPuzzleException {
		this.preferences = pref;
		this.resetGame();
		
		final int columns = pref.getColumns();
		final int rows = pref.getRows();
		this.builder.makePieces(columns, rows);

		this.gameRunning = true;
		
		GameEvent ge = new GameEvent(GameEvent.State.START_GAME,
				this.preferences);
		this.deliverEvent(ge);
                
                PuzzleTimer.StartTimer();
	}

	public void loadGame(File from) throws LoadGameException {
		this.resetGame();
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(from);
			this.restore(doc);
			
			GameEvent ge = new GameEvent(GameEvent.State.LOAD_GAME,
					this.preferences);
			this.deliverEvent(ge);
			System.gc();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		} catch (JigsawPuzzleException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		}
	}

	public void saveGame(File to) throws SaveGameException {
		Document doc = StorageUtil.createDOMDocument();
		this.store(doc);

		StorageUtil.saveAsXML(doc, to);
		System.gc();
	}

	/*
	 * END OF xxxGame
	 */

	public static GameCommander getInstance() {
		if (This == null)
			This = new GameCommander();
		return This;
	}

	public GamePreferences getPreferences() {
		return this.preferences;
	}

	public EdgeDisposer getEdgeDisposer() {
		return this.edgeDisposer;
	}

	public PuzzlePieceDisposer getPieceDisposer() {
		return this.pieceDisposer;
	}

	public void eventHappened(GameEvent ge) throws JigsawPuzzleException {
		switch (ge.getType()) {
		case SNAP_PIECE:
			this.testForGameEnd();
			break;
		default:
			break;
		}
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node gameNode = StorageUtil.findDirectChildNode(current,
				"PuzzleGameState");
		
		NamedNodeMap nnm = gameNode.getAttributes();
		String storeVersion = nnm.getNamedItem("version").getNodeValue();
		if (!storeVersion.equals(PuzzleProperties.GAME_STORAGE_VERSION)) {
			logger.warn("current version: " + PuzzleProperties.GAME_STORAGE_VERSION + " but found a stored game version: " + storeVersion);
			throw new LoadGameException("wrong version");
		}

		this.preferences = new GamePreferences();
		this.preferences.restore(gameNode);

		this.pieceDisposer.reset();
		this.pieceDisposer.restore(gameNode);
		
		GameMainWindow.getInstance().restore(gameNode);
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = (Document) current;
		Element gameNode = doc.createElement("PuzzleGameState");
		gameNode.setAttribute("version", PuzzleProperties.GAME_STORAGE_VERSION);
		this.preferences.store(gameNode);
		this.pieceDisposer.store(gameNode);
		GameMainWindow.getInstance().store(gameNode);
		doc.appendChild(gameNode);
	}

}
