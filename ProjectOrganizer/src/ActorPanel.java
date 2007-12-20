import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import fr.umlv.projectOrganizer.Encodable;


public class ActorPanel {
	
	private JPanel actor = new JPanel(new GridBagLayout());
	
	@Encodable(getFieldEncodeName="nom")
	private JTextField nom = new JTextField(70);
	
	@Encodable(getFieldEncodeName="actif")
	private JCheckBox actif = new JCheckBox("Actif");
	
	@Encodable(getFieldEncodeName="position")
	private JTextField position = new JTextField(70);
	
	@Encodable(getFieldEncodeName="reportsTo")
	private JTextField reportsTo = new JTextField(70);
	
	@Encodable(getFieldEncodeName="description")
	private JTextArea description = new JTextArea(5,70);
	
	
	public ActorPanel() {
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
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 500);
		ActorPanel actor = new ActorPanel();
		frame.getContentPane().add(actor.getPanel());
		frame.setVisible(true);
	}
}


