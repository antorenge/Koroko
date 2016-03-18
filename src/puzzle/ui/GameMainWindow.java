package puzzle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import puzzle.GameCommander;
import puzzle.GamePreferences;
import puzzle.Login;
import puzzle.PuzzleProperties;
import puzzle.PuzzleTimer;
import puzzle.Ranking;
import puzzle.gameevent.GameEvent;
import puzzle.gameevent.GameEventListener;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class GameMainWindow extends JFrame implements GameEventListener, Storeable {

	private static final Logger logger = Logger.getLogger(GameMainWindow.class);
	private static String args;        
	private static String index;        
	private static GameMainWindow This;
        private static String [][] users = new String[100][10];
        
	public static GameMainWindow getInstance() {
            return This;
        }

	public static void startUI(String arg, String ind, String [][] us) {
            args = arg;
            index=ind;
            users = us;
            This = new GameMainWindow();                    
            This.setVisible(true);
                        
        }

	private PreviewDialog previewDialog;
	private LoadSaveFileChooser fileChooser;

	private JMenuBar menuBar;
	
	private JMenu gameMenu;
	private JMenuItem gameNew;
	private JMenuItem gameReset;
	private JMenuItem gameLoad;
	private JMenuItem gameSave;
	private JMenuItem gameExit;
	
	private JMenu viewMenu;
	private JMenuItem preview;
	
	private JMenu optionMenu;
	private JCheckBoxMenuItem outlineCheck;
	private JCheckBoxMenuItem shadowCheck;
	private JCheckBoxMenuItem highlightCheck;
	private JCheckBoxMenuItem antiAliasingCheck;
	private JCheckBoxMenuItem soundCheck;
	
	private GamePanel gamePanel;
	private JScrollPane gamePanelScroll;
	private static LeftPanel leftPanel;
        private static JPanel bottomPanel;
        private static MainPanel mainMenuPanel;
        
        private JSeparator jSeparator1;
        private JSeparator jSeparator2;
        private JSeparator jSeparator3;
        private JSeparator jSeparator4;
        private JSeparator jSeparator5;
              
	private JLabel statusInformation;
        private JLabel lblTitle;
        private JLabel lblSettings;
        private JLabel lblUser;
        private JLabel lblcat;
        private JLabel lblduck;
        private JLabel lbltweety;
        private JLabel lbllogo;
	private int startingPieceCount;
	private int actualPieceCount;         
        private JButton btnAnimals;        
        private JButton btnFamily;
        private JButton btnUtensils;  
        private JToggleButton btnHighlight;
        private JButton btnNew;
        private JToggleButton btnOutline;
        private JButton btnPreview;
        private JButton btnReset;
        private JToggleButton btnShadow;
        private JToggleButton btnSound;
        private JButton btnBack;
        private JButton btnMExit;        
        private JButton btnRanking;        
        private Image bimg;
        private Image mimg;
        AudioStream stream;
        private int completed;
        private int outof;
        
	public GameMainWindow() {
		this.setIconImage(new ImageIcon(getClass().getResource(
				PuzzleProperties.APPLICATION_ICON_FILE)).getImage());
		
		String header = PuzzleProperties.getLocalized("gameHeader");
		this.setTitle(header);
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(400, 300));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.initComponents();
		this.fileChooser = new LoadSaveFileChooser(this);
		this.previewDialog = new PreviewDialog(this);
		this.createBufferStrategy(2);
                this.leftPanel.setVisible(false);
                
                //play welcome voice
                sound("/audio/welcome.wav");
		
                // add me as a listener to the Game state
		GameCommander.getInstance().addListener(this);
	}
        
        public final void sound(String path)
        {
            InputStream in;
            try{
                in= this.getClass().getResourceAsStream(path);
                stream=new AudioStream(in);
                AudioPlayer.player.start(stream);
                }
                catch(IOException e){
                    e.printStackTrace();
                } 
        }
        
        private class LeftPanel extends JPanel {
        
            public LeftPanel() {
                try {
                    ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/left-pane.jpg"));
                    bimg = icon.getImage();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
            }       

            @Override
            protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                                    // paint the background image and scale it to fill the entire space
                    g.drawImage(bimg, 0, 0, getWidth(), getHeight(), this);
            }
        
        }
        
        private class MainPanel extends JPanel {
        
            public MainPanel() {
                try {
                    ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/blackboard.jpg"));
                    mimg = icon.getImage();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
            }       

            @Override
            protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                                    // paint the background image and scale it to fill the entire space
                    g.drawImage(mimg, 0, 0, getWidth(), getHeight(), this);
            }
        
        }
        
	public GamePanel getGamePanel() {
		return gamePanel;
	}
        
	private void initComponents() {   
            
                //components
                this.jSeparator1 = new JSeparator();
                this.jSeparator2 = new JSeparator();
                this.jSeparator3 = new JSeparator();
                this.jSeparator4 = new JSeparator();
                this.jSeparator5 = new JSeparator();
                this.btnBack = new JButton();
                this.btnNew = new JButton();
                this.btnReset = new JButton();
                this.btnOutline = new JToggleButton();
                this.btnShadow = new JToggleButton();
                this.btnHighlight = new JToggleButton();
                this.btnSound = new JToggleButton();
                this.btnPreview = new JButton();
                
                this.lblUser = new JLabel();
                this.lblSettings = new JLabel();
                this.lbltweety = new JLabel();
                this.lblcat = new JLabel();
                this.lblduck = new JLabel();
                this.lbllogo = new JLabel();                
                this.lblTitle = new JLabel();   
                this.btnAnimals = new JButton();
                this.btnFamily = new JButton();
                this.btnUtensils = new JButton();
                this.statusInformation = new JLabel();
                this.btnMExit = new JButton();
                this.btnRanking = new JButton();
                                
                //panels
		mainMenuPanel = new MainPanel();
                this.gamePanel = new GamePanel();		
                this.addKeyListener(this.gamePanel.getInputListener());
		this.gamePanel.setBackground(new java.awt.Color(59, 101, 61));
                mainMenuPanel.setBackground(new java.awt.Color(59, 101, 61));                
                this.gamePanel.setLayout(new BorderLayout());
                gamePanel.add(this.mainMenuPanel);                            
                
                //left panel
                leftPanel = new LeftPanel();
                this.initUI();
                
                //south panel
                bottomPanel = new JPanel();
                bottomPanel.setBackground(new java.awt.Color(189, 253, 37));
                bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                bottomPanel.add(this.btnMExit);                
                bottomPanel.add(this.btnRanking);
                
                btnMExit.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnMExitActionPerformed(evt);
                    }
                });
                
                btnRanking.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnRankingActionPerformed(evt);
                    }
                });
                
                GroupLayout mainMenuPanelLayout = new GroupLayout(this.mainMenuPanel);
                this.mainMenuPanel.setLayout(mainMenuPanelLayout);

                mainMenuPanelLayout.setHorizontalGroup(
                    mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenuPanelLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbllogo)
                            .addComponent(lblduck))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenuPanelLayout.createSequentialGroup()
                                .addComponent(lblTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbltweety)
                                .addGap(45, 45, 45))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenuPanelLayout.createSequentialGroup()
                                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAnimals, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnFamily)                                    
                                    .addComponent(btnUtensils))
                                .addGap(130, 130, 130)
                                .addComponent(lblcat)))
                        .addGap(40, 40, 40))
                    
                );
                mainMenuPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAnimals, btnFamily, btnUtensils});
                
                mainMenuPanelLayout.setVerticalGroup(
                    mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMenuPanelLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTitle)
                            .addComponent(lbltweety)
                            .addComponent(lbllogo))                        
                        .addGap(5, 5, 5)
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenuPanelLayout.createSequentialGroup()
                                .addComponent(btnAnimals, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(btnFamily)
                                .addGap(45, 45, 45)
                                .addComponent(btnUtensils)    
                                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)                                    
                                    .addComponent(lblcat)))
                            .addComponent(lblduck, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(90, 90, 90))
                );
                mainMenuPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAnimals, btnFamily, btnUtensils});
                
                javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
                leftPanel.setLayout(leftPanelLayout);
                leftPanelLayout.setHorizontalGroup(
                    leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)    
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(statusInformation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnSound, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnHighlight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnOutline, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnShadow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPreview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnReset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addComponent(btnNew, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                leftPanelLayout.setVerticalGroup(
                    leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(statusInformation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(lblSettings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnOutline, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnShadow, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHighlight, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSound, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUser)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                        
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                leftPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jSeparator2, jSeparator3});

                
		this.gamePanelScroll = new JScrollPane(gamePanel);
		this.gamePanelScroll.getViewport().add(gamePanel);

		this.menuBar = new JMenuBar();
		this.gameMenu = new JMenu();
		this.gameMenu.setText(PuzzleProperties.getLocalized("gameMenu"));
                
                lblTitle.setFont(new java.awt.Font("DK Crayon Crumble", 0, 48)); 
                lblTitle.setForeground(new java.awt.Color(245, 245, 245));
                lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblTitle.setText("Hello "+users[Integer.parseInt(index)][3]+", click on an item to learn");
                
                btnAnimals.setFont(new java.awt.Font("DK Crayon Crumble", 0, 60)); 
                btnAnimals.setBackground(new java.awt.Color(66, 139, 202));
                btnAnimals.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/animals-icon.PNG"))); 
                btnAnimals.setForeground(new java.awt.Color(255, 255, 255));
                btnAnimals.setText("Animals");
                btnAnimals.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnAnimalsActionPerformed(evt);
                    }
                });
                
                btnFamily.setFont(new java.awt.Font("DK Crayon Crumble", 0, 60)); 
                btnFamily.setBackground(new java.awt.Color(255, 185, 0));
                btnFamily.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/family-icon.PNG")));
                btnFamily.setForeground(new java.awt.Color(255, 255, 255));
                btnFamily.setText("Family");
                btnFamily.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnFamilyActionPerformed(evt);
                    }
                });
                
                btnUtensils.setFont(new java.awt.Font("DK Crayon Crumble", 0, 60)); 
                btnUtensils.setBackground(new java.awt.Color(255, 133, 0));
                btnUtensils.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/utensils-icon.PNG")));
                btnUtensils.setForeground(new java.awt.Color(255, 255, 255));
                btnUtensils.setText("Utensils");
                btnUtensils.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnUtensilsActionPerformed(evt);
                    }
                });
                
                lbltweety.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tweety.gif"))); 
                lblcat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cat.PNG")));
                lblduck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/duck.PNG"))); 
                lbllogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lion.PNG"))); 
        
		this.gameNew = new JMenuItem(PuzzleProperties.getLocalized("gameMenuNewGame"));
		this.gameNew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        clickedNewGame();
                    }
                });
		
		this.gameReset = new JMenuItem(PuzzleProperties.getLocalized("gameMenuResetGame"));
		this.gameReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        try {
                                                clickedResetGame();
                                        } catch (JigsawPuzzleException e) {
                                                logger.error("reset game error" + e.toString());
                                        }
                    }
                });
		
		this.gameLoad = new JMenuItem(PuzzleProperties.getLocalized("gameMenuLoadGame"));
		this.gameLoad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        clickedLoadGame();
                    }
                });
		
		this.gameSave = new JMenuItem(PuzzleProperties.getLocalized("gameMenuSaveGame"));
		this.gameSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        clickedSaveGame();
                    }
                });
		
		this.gameExit = new JMenuItem(PuzzleProperties.getLocalized("gameMenuExitGame"));
		this.gameExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        clickedExitGame();
                    }
                });
		
		this.viewMenu = new JMenu();
		this.viewMenu.setText(PuzzleProperties.getLocalized("viewMenu"));
		this.preview = new JMenuItem();
		this.preview.setText(PuzzleProperties.getLocalized("viewMenuPreview"));
		this.preview.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        clickedPreview();
                    }
                });
		
		this.optionMenu = new JMenu();
		this.optionMenu.setText(PuzzleProperties.getLocalized("options"));
		
		this.outlineCheck = new JCheckBoxMenuItem();
		this.outlineCheck.setText(PuzzleProperties.getLocalized("optionShowOutline"));
		this.outlineCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        boolean outline = outlineCheck.getState();
                        GameCommander.getInstance().getPreferences().setShowOutline(outline);
                        try {
                                                gamePanel.reRender();
                                        } catch (JigsawPuzzleException e) {
                                                logger.error("error in rerendering" + e.toString());
                                        }
                    }
                });
		
		this.shadowCheck = new JCheckBoxMenuItem();
		this.shadowCheck.setText(PuzzleProperties.getLocalized("optionShowShadow"));                
		this.shadowCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        boolean shadow = shadowCheck.getState();
                        GameCommander.getInstance().getPreferences().setShowShadow(shadow);
                        try {
                                                gamePanel.reRender();
                                        } catch (JigsawPuzzleException e) {
                                                logger.error("error in rerendering" + e.toString());
                                        }
                    }
                });
		
		this.highlightCheck = new JCheckBoxMenuItem();
		this.highlightCheck.setText(PuzzleProperties.getLocalized("optionHighlight"));
		this.highlightCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        boolean high = highlightCheck.getState();
                        GameCommander.getInstance().getPreferences().setHighlight(high);
                        try {
                                                gamePanel.reRender();
                                        } catch (JigsawPuzzleException e) {
                                                logger.error("error in rerendering" + e.toString());
                                        }
                    }
                });
		
		this.antiAliasingCheck = new JCheckBoxMenuItem();
		this.antiAliasingCheck.setText(PuzzleProperties.getLocalized("viewMenuAntiAliasing"));
		this.antiAliasingCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        boolean ali = antiAliasingCheck.getState();
                        GameCommander.getInstance().getPreferences().setAntiAliasing(ali);
                        try {
                                                gamePanel.repaint();
                                                gamePanel.reRender();
                                        } catch (JigsawPuzzleException e) {
                                                logger.error("error in rerendering" + e.toString());
                                        }
                    }
                });
		
		this.soundCheck = new JCheckBoxMenuItem();
		this.soundCheck.setText(PuzzleProperties.getLocalized("optionSoundOn"));
		this.soundCheck.setState(true);
		this.soundCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        boolean snd = soundCheck.getState();
                        GameCommander.getInstance().getPreferences().setSound(snd);
                    }
                });
                
		//left-pane components
                
                btnNew.setFont(new java.awt.Font("DK Crayon Crumble", 0, 30)); 
                btnNew.setBackground(new java.awt.Color(66, 139, 202));
                btnNew.setForeground(new java.awt.Color(255, 255, 255));
                btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/blocks.png"))); // NOI18N
                btnNew.setText(" New Game");
                btnNew.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnNewActionPerformed(evt);
                    }
                });

                btnReset.setFont(new java.awt.Font("DK Crayon Crumble", 0, 30)); 
                btnReset.setBackground(new java.awt.Color(66, 139, 202));
                btnReset.setForeground(new java.awt.Color(255, 255, 255));
                btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/refresh4.png"))); // NOI18N
                btnReset.setText(" Reset Game");
                btnReset.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            btnResetActionPerformed(evt);
                        } catch (JigsawPuzzleException ex) {
                            java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                btnOutline.setFont(new java.awt.Font("DK Crayon Crumble", 0, 24));  
                btnOutline.setBackground(new java.awt.Color(188, 253, 36));                
                btnOutline.setForeground(new java.awt.Color(107, 107, 107));
                btnOutline.setText(" Outline");
                btnOutline.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnOutlineActionPerformed(evt);
                    }
                });

                btnShadow.setFont(new java.awt.Font("DK Crayon Crumble", 0, 24)); 
                btnShadow.setBackground(new java.awt.Color(188, 253, 36));
                btnShadow.setForeground(new java.awt.Color(107, 107, 107));
                btnShadow.setText(" Shadow");
                btnShadow.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnShadowActionPerformed(evt);
                    }
                });

                btnHighlight.setFont(new java.awt.Font("DK Crayon Crumble", 0, 24)); 
                btnHighlight.setBackground(new java.awt.Color(188, 253, 36));
                btnHighlight.setForeground(new java.awt.Color(107, 107, 107));
                btnHighlight.setText(" Highlight");
                btnHighlight.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnHighlightActionPerformed(evt);
                    }
                });

                btnSound.setFont(new java.awt.Font("DK Crayon Crumble", 0, 24)); 
                btnSound.setBackground(new java.awt.Color(188, 253, 36));
                btnSound.setForeground(new java.awt.Color(107, 107, 107));
                btnSound.setText(" Sound");
                btnSound.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnSoundActionPerformed(evt);
                    }
                });

                
                
                btnPreview.setFont(new java.awt.Font("DK Crayon Crumble", 0, 24)); 
                btnPreview.setBackground(new java.awt.Color(188, 253, 36));
                btnPreview.setForeground(new java.awt.Color(107, 107, 107));
                btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/preview.png"))); // NOI18N
                btnPreview.setText(" Clue");
                btnPreview.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnPreviewActionPerformed(evt);
                    }
                });

                btnBack.setFont(new java.awt.Font("DK Crayon Crumble", 0, 30)); 
                btnBack.setBackground(new java.awt.Color(255, 194, 30));
                btnBack.setForeground(new java.awt.Color(102, 51, 0));
                btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/exit.png"))); // NOI18N
                btnBack.setText(" Back");
                btnBack.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnBackActionPerformed(evt);
                    }
                });

                lblSettings.setFont(new java.awt.Font("DK Crayon Crumble", 0, 25)); // NOI18N
                lblSettings.setForeground(new java.awt.Color(255, 255, 255));
                lblSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/settings.png"))); // NOI18N
                lblSettings.setText(" Settings:");
                
                lblUser.setFont(new java.awt.Font("DK Crayon Crumble", 1, 30)); // NOI18N
                lblUser.setForeground(new java.awt.Color(255, 255, 255));
                lblUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pupil-white.png"))); // NOI18N
                lblUser.setText(" "+users[Integer.parseInt(index)][3]);
                
                //bottom-pane components
                btnMExit.setFont(new java.awt.Font("DK Crayon Crumble", 1, 32)); 
                btnMExit.setBackground(new java.awt.Color(190, 30, 45));
                btnMExit.setForeground(new java.awt.Color(0, 0, 0));
                btnMExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
                btnMExit.setText(" Leave Class");
                
                btnRanking.setFont(new java.awt.Font("DK Crayon Crumble", 1, 32)); 
                btnRanking.setBackground(new java.awt.Color(66, 139, 202));
                btnRanking.setForeground(new java.awt.Color(0, 0, 0));
                btnRanking.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pupil.png"))); // NOI18N
                btnRanking.setText(" Best Pupils");
                
		// disable the options
		enableOptions(false);
		
		// game menu
		this.gameMenu.add(this.gameNew);
		this.gameMenu.add(this.gameReset);
		this.gameMenu.add(new JSeparator());
		this.gameMenu.add(this.gameLoad);
		this.gameMenu.add(this.gameSave);
		this.gameMenu.add(new JSeparator());
		this.gameMenu.add(this.gameExit);
		
		// view menu
		this.viewMenu.add(this.preview);
		this.viewMenu.add(this.antiAliasingCheck);
		
		// option menu
		this.optionMenu.add(this.outlineCheck);
		this.optionMenu.add(this.shadowCheck);
		this.optionMenu.add(this.highlightCheck);
		this.optionMenu.add(this.soundCheck);
		
		// menu bar
		this.menuBar.add(this.gameMenu);
		this.menuBar.add(this.optionMenu);
		this.menuBar.add(this.viewMenu);
                
		// add stuff to the main jframe
		this.setLayout(new BorderLayout());                
		this.getContentPane().add(gamePanelScroll, BorderLayout.CENTER);
		this.getContentPane().add(leftPanel, BorderLayout.WEST);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
                this.setJMenuBar(this.menuBar);
		
		this.pack();
	}
	
        /**
	 * START CLICKED
	 */
	protected void clickedExitGame() {
		System.exit(0);
	}

	public void clickedNewGame() {
            mainMenuPanel.setVisible(true);
            try {
                this.clickedResetGame();
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
	}
        
	private void clickedPreview() {
		this.previewDialog.setVisible(true);
	}
	
	private void clickedResetGame() throws JigsawPuzzleException {
		GameCommander.getInstance().resetGame();
		this.reset();
	}
	
	private void clickedSaveGame() {
		File toSave = this.fileChooser.openSaveDialog();
		if (toSave == null) {
			return; // ignore and donot save anything
		}
		try {
			GameCommander.getInstance().saveGame(toSave);
		} catch (SaveGameException e) {
			e.printStackTrace();
			showErrorMessage(PuzzleProperties.getLocalized("storeSaveErrorTitle"), PuzzleProperties.getLocalized("storeSaveErrorMessage"));
		}
	}

	private void clickedLoadGame() {
		File toLoad = this.fileChooser.openLoadDialog();
		if (toLoad == null) {
			return; // ignore if nothing selected
		}
		try {
			GameCommander.getInstance().loadGame(toLoad);
		} catch (LoadGameException e) {
			e.printStackTrace();
			showErrorMessage(PuzzleProperties.getLocalized("storeLoadErrorTitle"), PuzzleProperties.getLocalized("storeLoadErrorMessage"));
		}
	}
        
        private void btnAnimalsActionPerformed(java.awt.event.ActionEvent evt) {   
            AudioPlayer.player.stop(stream);
            //play animals voice
            sound("/audio/action/animals.wav");
            try {
                reset();
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            GameStart opt = new GameStart();
            opt.animalsClicked();
            
            btnOutline.setSelected(true);
            btnShadow.setSelected(true);
            btnHighlight.setSelected(true);            
            btnSound.setSelected(true);
            btnOutline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnShadow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnHighlight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            
            leftPanel.setVisible(true);
            bottomPanel.setVisible(false);
            mainMenuPanel.setVisible(false);
            
        }                                          

        private void btnFamilyActionPerformed(java.awt.event.ActionEvent evt) {  
            AudioPlayer.player.stop(stream);
            //play family voice
            sound("/audio/action/family.wav");
            try {
                reset();
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            GameStart opt = new GameStart();
            opt.familyClicked();
            
            btnOutline.setSelected(true);
            btnShadow.setSelected(true);
            btnHighlight.setSelected(true);            
            btnSound.setSelected(true);
            btnOutline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnShadow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnHighlight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            
            leftPanel.setVisible(true);
            bottomPanel.setVisible(false);
            mainMenuPanel.setVisible(false);
        }                                         

        private void btnUtensilsActionPerformed(java.awt.event.ActionEvent evt) {      
            AudioPlayer.player.stop(stream);
            //play utensils voice
            sound("/audio/action/utensils.wav");
            try {
                reset();
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            GameStart opt = new GameStart();
            opt.utensilsClicked();
            
            btnOutline.setSelected(true);
            btnShadow.setSelected(true);
            btnHighlight.setSelected(true);            
            btnSound.setSelected(true);
            btnOutline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnShadow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnHighlight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            btnSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            
            leftPanel.setVisible(true);
            bottomPanel.setVisible(false);
            mainMenuPanel.setVisible(false);
        }
        
        public void btnNewActionPerformed(java.awt.event.ActionEvent evt) {                                       
            
            leftPanel.setVisible(false);
            mainMenuPanel.setVisible(true);
            bottomPanel.setVisible(true);
            
            try {
                this.clickedResetGame();
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                                      

        private void btnResetActionPerformed(java.awt.event.ActionEvent evt) throws JigsawPuzzleException {                                         
            PuzzleTimer.imStopTimer();
            GameCommander.getInstance().resetGame();
            this.reset();
        }                                        

        private void btnOutlineActionPerformed(java.awt.event.ActionEvent evt) {                                           
            boolean outline = btnOutline.isSelected();
            if(outline == true) {
                btnOutline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            } else {
                btnOutline.setIcon(null);
            }
            GameCommander.getInstance().getPreferences().setShowOutline(outline);
            try {
                                    gamePanel.reRender();
                            } catch (JigsawPuzzleException e) {
                                    logger.error("error in rerendering" + e.toString());
                            }            
        }                                          

        private void btnShadowActionPerformed(java.awt.event.ActionEvent evt) {                                          
            boolean shadow = btnShadow.isSelected();
            if(shadow == true) {
                btnShadow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            } else {
                btnShadow.setIcon(null);
            }
            GameCommander.getInstance().getPreferences().setShowShadow(shadow);
            try {
                    gamePanel.reRender();
                } catch (JigsawPuzzleException e) {
                                    logger.error("error in rerendering" + e.toString());
                }
        }                                         

        private void btnHighlightActionPerformed(java.awt.event.ActionEvent evt) {                                             
            boolean high = btnHighlight.isSelected();
            if(high == true) {
                btnHighlight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            } else {
                btnHighlight.setIcon(null);
            }
            
            GameCommander.getInstance().getPreferences().setHighlight(high);
            try {
                                    gamePanel.reRender();
                            } catch (JigsawPuzzleException e) {
                                    logger.error("error in rerendering" + e.toString());
                            }
        }                                            

        private void btnSoundActionPerformed(java.awt.event.ActionEvent evt) {                                         
            boolean snd = btnSound.isSelected();
            if(snd == true) {
                btnSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/middlenav/check.png")));
            } else {
                btnSound.setIcon(null);
            }
            GameCommander.getInstance().getPreferences().setSound(snd);
        }                                        

        private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {                                           
            this.previewDialog.setVisible(true);
        }                                          

        private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {                                        
            leftPanel.setVisible(false);
            mainMenuPanel.setVisible(true);
            bottomPanel.setVisible(true);
            PuzzleTimer.imStopTimer();
            try {
                this.btnResetActionPerformed(evt);
            } catch (JigsawPuzzleException ex) {
                java.util.logging.Logger.getLogger(GameMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
        private void btnMExitActionPerformed(java.awt.event.ActionEvent evt) {                                                                 
            AudioPlayer.player.stop(stream);
            Login.display();
            this.dispose();
        } 
        
        private void btnRankingActionPerformed(java.awt.event.ActionEvent evt) {                                                                 
            AudioPlayer.player.stop(stream);
            Ranking.display();            
        } 
	
	/**
	 * END CLICKED
	 * @throws JigsawPuzzleException 
	 */
	
	private void reset() throws JigsawPuzzleException {
		this.initUI();
		this.enableOptions(false);
		this.gamePanel.reRender();
		this.previewDialog.unloadImage();
	}
	
	private void enableOptions(boolean enable) {
		this.outlineCheck.setEnabled(enable);
		this.shadowCheck.setEnabled(enable);
		this.highlightCheck.setEnabled(enable);
		this.antiAliasingCheck.setEnabled(enable);
		this.soundCheck.setEnabled(enable);
		
		this.gameSave.setEnabled(enable);		
	}
	
	private void initUI() {
		this.statusInformation.setText(PuzzleProperties.getLocalized("puzzlePieces"));
                this.statusInformation.setFont(new java.awt.Font("DK Crayon Crumble", 1, 24));                 
                this.statusInformation.setForeground(new java.awt.Color(255, 255, 255));
	}
	
	private void showErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(this, title, message, JOptionPane.ERROR_MESSAGE);
	}
    
	/**
	 * this should set the boundaries, if and only if the 
	 * @param xbound
	 * @param ybound
	 */
    public void setBoundaries(int xbound, int ybound) {
		GamePreferences gp = GameCommander.getInstance().getPreferences();

		// set the size so that you can almost 'hide' a piece and it's possible to grab it although
		Dimension wishedSize = new Dimension(xbound + gp.getSideLength(), ybound + gp.getSideLength());
		// the old size from the panel
		Dimension oldSize = this.gamePanel.getSize();
		
		// x,y set the larger variants
		int xSize, ySize;
		if (wishedSize.width > oldSize.width)
			xSize = wishedSize.width;
		else xSize = oldSize.width;
		if (wishedSize.height > oldSize.height)
			ySize = wishedSize.height;
		else ySize = oldSize.height;
		
		// the larger version for x and y for wished and old sizes
		Dimension newSize = new Dimension(xSize, ySize);

		// later size was set!
		this.gamePanel.setPreferredSize(newSize);
		this.gamePanel.setSize(newSize);
	}
    
    public void setPieceCount(int pieceCount) {
    	this.actualPieceCount = pieceCount;
        completed = this.startingPieceCount - this.actualPieceCount;
        outof = this.startingPieceCount - 1;
    	String pieceText = PuzzleProperties.getLocalized("puzzlePieces");
    	this.statusInformation.setText(pieceText + " " + this.completed + "/" + this.outof);
    }
    
    public void startGame(GamePreferences gp) throws JigsawPuzzleException {
		this.previewDialog.loadImage(gp.getImage().getImage());

		this.enableOptions(true);
		
		this.outlineCheck.setState(gp.isShowOutline());
		this.shadowCheck.setState(gp.isShowShadow());
		this.highlightCheck.setState(gp.isHighlight());
		this.antiAliasingCheck.setState(gp.isAntiAliasing());
		this.soundCheck.setState(gp.isSound());
		
		this.gamePanel.reRender();
		this.repaint();
	}

	public void eventHappened(GameEvent ge) throws JigsawPuzzleException {
		GamePreferences gp;
		switch(ge.getType()) {
		case START_GAME:
			gp  = (GamePreferences)ge.getInfo();
			this.startingPieceCount = gp.getInitialPieces();
			this.setPieceCount(this.startingPieceCount);
			this.startGame(gp);
			break;
		case LOAD_GAME:
			gp = (GamePreferences)ge.getInfo();
			this.startGame(gp);
			break;
		case SNAP_PIECE:
			int pieces = (Integer)ge.getInfo();
			this.setPieceCount(pieces);
			break;
		}
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node mainWindowNode = StorageUtil.findDirectChildNode(current, "GameMainWindow");
		NamedNodeMap nnm = mainWindowNode.getAttributes();
		Node sizeW = nnm.getNamedItem("SizeWidth");
		int width = Integer.parseInt(sizeW.getNodeValue());
		
		Node sizeH = nnm.getNamedItem("SizeHeight");
		int height = Integer.parseInt(sizeH.getNodeValue());
		this.setBoundaries(width, height);
		
		Node initialPieceCount = nnm.getNamedItem("InitialPieceCount");
		this.startingPieceCount = Integer.parseInt(initialPieceCount.getNodeValue());
		
		Node pieceCount = nnm.getNamedItem("PieceCount");
		this.setPieceCount(Integer.parseInt(pieceCount.getNodeValue()));
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Element mainWindowNode = doc.createElement("GameMainWindow");
		Dimension prefSize = this.gamePanel.getPreferredSize();
		
		mainWindowNode.setAttribute("SizeWidth", ""+prefSize.width);
		mainWindowNode.setAttribute("SizeHeight", ""+prefSize.height);
		mainWindowNode.setAttribute("InitialPieceCount", ""+this.startingPieceCount);
		mainWindowNode.setAttribute("PieceCount", ""+this.actualPieceCount);
		current.appendChild(mainWindowNode);
	}
        
        public static void newgame() {            
            leftPanel.setVisible(false);
            mainMenuPanel.setVisible(true);
            bottomPanel.setVisible(true);                                          
        }
        
        public static int getUserID() {  
            int ind = Integer.parseInt(index)+1;
            return ind;
        }
        
}
