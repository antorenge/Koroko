package puzzle.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.GroupLayout.Alignment;

import org.apache.log4j.Logger;

import puzzle.PuzzleProperties;
import puzzle.storeage.JigsawPuzzleException;

public class TestGroupLayout extends JFrame {
	
	private static final Logger logger = Logger.getLogger(TestGroupLayout.class);
	
	private JPanel previewPanel;
	private JButton startButton;
	private JButton cancelButton;
	private JButton findFileButton;
	
	private JLabel pieceSizeLabel;
	private JSpinner pieceSizeSpinner;
	
	private JLabel additionalOptionsLabel;
	
	private JLabel turnPieceLabel;
	private JCheckBox turnPieceCheckBox;
	
	private JLabel edgesLabel;
	private JComboBox edgesComboBox;
	
	public TestGroupLayout() {
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Test Layout");
		this.setLocationByPlatform(true);
		this.setResizable(true);
		
		this.initComponents();
		this.initLayout();
		
	}
	
	private void initComponents() {
		this.previewPanel = new JPanel();
		
		this.turnPieceCheckBox = new JCheckBox(PuzzleProperties
				.getLocalized("optionAllowTurn"));
		
		this.startButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogStart"));
		this.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startClicked();
			}
		});
		
		this.cancelButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogCancel"));
		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelClicked();
			}
		});
		
		this.findFileButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogFindFile"));
		this.findFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					findFileClicked();
				} catch (JigsawPuzzleException e) {
					logger.error("couldn't load image: " + e.toString());
					showErrorMessage(PuzzleProperties.getLocalized("loadImageErrorTitle"), PuzzleProperties.getLocalized("loadImageErrorMessage"));
				}
			}
		});
		
		this.pieceSizeLabel = new JLabel(PuzzleProperties
				.getLocalized("newGameDialogPieceSize"));
		this.additionalOptionsLabel = new JLabel(PuzzleProperties
				.getLocalized("newGameDialogOptions"));
		
		this.edgesLabel = new JLabel(PuzzleProperties.getLocalized("newGameDialogEdges"));
	}

	private void initLayout() {
		
		   GroupLayout layout = new GroupLayout(this.getContentPane());
		   this.setLayout(layout);
		 
		   // Turn on automatically adding gaps between components
		   layout.setAutoCreateGaps(true);
		 
		   // Turn on automatically creating gaps between components that touch
		   // the edge of the container and the container.
		   layout.setAutoCreateContainerGaps(true);
		 
		   /*
		    * new attempt for better reading:
		    */
		   
		   layout.setHorizontalGroup(
				   layout.createSequentialGroup()
				      .addComponent(additionalOptionsLabel)
				      .addComponent(edgesLabel)
				      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				           .addComponent(startButton)
				           .addComponent(cancelButton))
				);
				layout.setVerticalGroup(
				   layout.createSequentialGroup()
				      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(additionalOptionsLabel)
				           .addComponent(edgesLabel)
				           .addComponent(startButton))
				      .addComponent(cancelButton)
				);

		   
		   /*
		    * end of new attempt
		    */
		   
		   
		   // Create a sequential group for the horizontal axis.
		 /*
		   GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		   // The sequential group in turn contains two parallel groups.
		   // One parallel group contains the labels, the other the text fields.
		   // Putting the labels in a parallel group along the horizontal axis
		   // positions them at the same x location.
		   //
		   // Variable indentation is used to reinforce the level of grouping.
		   hGroup.addGroup(layout.createParallelGroup().
		            addComponent(this.additionalOptionsLabel).addComponent(this.edgesLabel));
		   
		   hGroup.addGroup(layout.createParallelGroup().
		            addComponent(this.startButton).addComponent(this.cancelButton));
		   layout.setHorizontalGroup(hGroup);
		   
		   // Create a sequential group for the vertical axis.
		   GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		 
		   // The sequential group contains two parallel groups that align
		   // the contents along the baseline. The first parallel group contains
		   // the first label and text field, and the second parallel group contains
		   // the second label and text field. By using a sequential group
		   // the labels and text fields are positioned vertically after one another.
		   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		            addComponent(this.additionalOptionsLabel).addComponent(this.startButton));
		   vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		            addComponent(this.edgesLabel).addComponent(this.cancelButton));
		   layout.setVerticalGroup(vGroup);
		*/
		
		this.pack();
	}
	
	protected void showErrorMessage(String title, String message) {
		// TODO Auto-generated method stub
		
	}

	protected void findFileClicked() throws JigsawPuzzleException {
		throw new JigsawPuzzleException();
	}

	protected void cancelClicked() {
		// TODO Auto-generated method stub
		
	}

	protected void startClicked() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args) {
		PuzzleProperties.loadLocal();
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	TestGroupLayout tgl = new TestGroupLayout();
            	tgl.setVisible(true);
            	
            }
        });
	}

}
