package fr.umlv.projectOrganizer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.projectOrganizer.XmlEncodable;
import fr.umlv.projectOrganizer.xml.XMLEncoder;


public class ActorPanel implements Encodable {
	
	private JPanel actor = new JPanel(new GridBagLayout());
	
	@XmlEncodable(getFieldEncodeName="nom")
	private JTextField nom = new JTextField(70);

	@XmlEncodable(getFieldEncodeName="actif")
	public JCheckBox actif = new JCheckBox("Actif");
	
	@XmlEncodable(getFieldEncodeName="position")
	private JTextField position = new JTextField(70);
	
	@XmlEncodable(getFieldEncodeName="reportsTo")
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
		
		// Actor panel
		final ActorPanel actor = new ActorPanel();
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Actor", actor.getPanel());
		
		
		// Panel Action
		JPanel action = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton load = new JButton("Load");
		JButton save = new JButton("Save");
		action.add(load);
		action.add(save);
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				XMLEncoder.encode(actor, "id");
			}
		});
		
		// Actor list
		final DefaultListModel model = new DefaultListModel();
		// Init list
		ArrayList<String> data = XMLEncoder.getValuesAsListFromXML(actor, "nom");

		// Test
		final JList list = new JList(data.toArray()); //data has type Object[]
		list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		list.addListSelectionListener(new ListSelectionListener(){
			
			private int oldSelectedIndex = -1;
			
			public void valueChanged(ListSelectionEvent e) {
				if (oldSelectedIndex != list.getSelectedIndex()){
					String value = (String)list.getSelectedValue();
					System.out.println("Retrieve :"+value.split(":")[1]);
					XMLEncoder.decode(actor, value.split(":")[1]);
				}
				oldSelectedIndex = list.getSelectedIndex();
			}
		});
	
		// Create UI
		mainPanel.add(action, BorderLayout.NORTH);
		mainPanel.add(list, BorderLayout.WEST);
		mainPanel.add(tabPane, BorderLayout.CENTER);
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
	}

	

}


