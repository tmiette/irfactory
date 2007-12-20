package fr.umlv.projectOrganizer.ui;



import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**This the main UI of the application
 * 
 * @author Moreau Alan
 * @author Pons Julien
 */
public class ApplicationUI {
	
	final static int FRAME_WIDTH = 820;
	final static int FRAME_HEIGHT = 570;
	
	/** Workbench. */
    private JPanel panelWorkbench = new JPanel();

    /** Message and help lowerTabs. */
    private JTabbedPane lowerTabs = new JTabbedPane();
	
	public ApplicationUI() {
		JFrame frame = setUpFrame();
		
		JTabbedPane pane = new JTabbedPane();
		pane.addTab("Acteur", setUpActorPanel(new JPanel(new GridBagLayout())));
		
		pane.addTab("Contact", new JPanel());
		pane.addTab("Absences", new JPanel());
		pane.addTab("Compétences", new JPanel());
		pane.addTab("Collaborateur", new JPanel());
		pane.addTab("Projets", new JPanel());
		pane.addTab("Audit/Sécurité", new JPanel());
		
		frame.getContentPane().add(pane);
		frame.setVisible(true);
	}
	
	private JFrame setUpFrame(){
		JFrame frame = new JFrame();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}
	
	private JPanel setUpActorPanel(JPanel panel){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 2; // two columns
		gbc.gridheight = 5; //five rows
		
		//gbc.weighty = 0;
		//gbc.weightx = 1;
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		
		gbc.anchor = GridBagConstraints.WEST;
		JLabel label = new JLabel("Nom");
		
		panel.add(label, gbc);
		
		JTextField textField = new JTextField(70);
		gbc.gridx = 1;
		//gbc.gridwidth = 300;
		panel.add(textField, gbc);
		
		JCheckBox checkBox = new JCheckBox("Actif");
		gbc.gridy = 1;
		//gbc.gridwidth = 1;
		//gbc.weightx = 100;
		gbc.gridx = 1;
		panel.add(checkBox, gbc);
		
		
		JLabel labelPoste = new JLabel("Poste");
		gbc.gridwidth = 1;
		gbc.weightx = 30;
		gbc.gridx = 0;
		gbc.gridy = 2;
		
		panel.add(labelPoste, gbc);

		JTextField textFieldPoste = new JTextField(70);
		textFieldPoste.getText();
		//		gbc.gridx = 1;
//		gbc.gridwidth = 4;
//		gbc.weightx = 70;
//		gbc.gridy = 2;
//		
//		panel.add(textFieldPoste, gbc);
//		
		
		return panel;
	}
	
}
