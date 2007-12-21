package fr.umlv.projectOrganizer.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import fr.umlv.projectOrganizer.XmlEncodable;


public class PanelActor implements Encodable {

	private JPanel actor = new JPanel(new GridBagLayout());

	@XmlEncodable(getFieldEncodeName="nom")
	private JTextField nom = new JTextField(70);

	@XmlEncodable(getFieldEncodeName="actif")
	public JCheckBox actif = new JCheckBox("Actif");

	@XmlEncodable(getFieldEncodeName="position")
	private JTextField position = new JTextField(70);

	@XmlEncodable(getFieldEncodeName="reportsTo")
	private JTextField reportsTo = new JTextField(70);

	@XmlEncodable(getFieldEncodeName="description")
	private JTextArea description = new JTextArea(5,70);


	public PanelActor() {
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
		addLine(4, new JLabel("Description :"), description);
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
}
