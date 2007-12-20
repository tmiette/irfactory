import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;

import fr.umlv.projectOrganizer.Encodable;


public class ActorPanel {
	
	private JPanel actor = new JPanel(new GridBagLayout());
	
	@Encodable(getFieldEncodeName="nom")
	private JTextField nom = new JTextField(70);
	
	@Encodable(getFieldEncodeName="test")
	private String test = "sqdsq";
	
	@Encodable(getFieldEncodeName="actif")
	public JCheckBox actif = new JCheckBox("Actif");
	
	@Encodable(getFieldEncodeName="position")
	private JTextField position = new JTextField(70);
	
	@Encodable(getFieldEncodeName="reportsTo")
	private JTextField reportsTo = new JTextField(70);
	
	//@Encodable(getFieldEncodeName="description")
	//private JTextArea description = new JTextArea(5,70);
	
	
	public ActorPanel() {
		actif.setSelected(true);
		position.setText("salut");
		initPanel();
	}

	public JPanel getPanel(){
		return actor;
	}
	
	
	public void initPanel(){
		addLine(0, new JLabel("Nom :"), nom);
		addLine(1, null, actif);
		addLine(2, new JLabel("Position :"), position);
		addLine(3, new JLabel("Reports to :"), reportsTo);
		//addLine(4, new JLabel("Description :"), description);
	}
	
	/**
	 * Adds a new line to current panel
	 * @param lineNumber
	 * @param leftcomponent
	 * @param rightcomponent
	 */
	public void addLine(int lineNumber, JComponent leftcomponent, JComponent rightcomponent){
		if (leftcomponent != null){
			GridBagConstraints gbc = new GridBagConstraints(0, lineNumber, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
			actor.add(leftcomponent, gbc);
		}
		if (rightcomponent != null){
			GridBagConstraints gbc;
			if (rightcomponent instanceof JTextArea)
				gbc = new GridBagConstraints(1, lineNumber, 2, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,0,0), 0, 0);
			else
				gbc = new GridBagConstraints(1, lineNumber, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,0), 0, 0);
			actor.add(rightcomponent, gbc);
		}
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 500); 
		
		// Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// Panel Action
		JPanel action = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton load = new JButton("Load");
		JButton save = new JButton("Save");
		action.add(load);
		action.add(save);
		
		// Actor list
		final DefaultListModel model = new DefaultListModel();
		
		Object data[] = new Object[]{"Actor1","Actor2"};
		JList list = new JList(data); //data has type Object[]
		list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// Actor panel
		ActorPanel actor = new ActorPanel();
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Actor", actor.getPanel());
		
		// Create UI
		mainPanel.add(action, BorderLayout.NORTH);
		mainPanel.add(list, BorderLayout.WEST);
		mainPanel.add(tabPane, BorderLayout.CENTER);
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
	}

	

}


