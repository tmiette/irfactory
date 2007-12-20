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


public class ActorPanel {
	
	
	JTextField blou;
	JComponent component;
	
	
	
	private JPanel actor = new JPanel(new GridBagLayout());
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 500);
		ActorPanel actor = new ActorPanel();
		actor.addLine(0, new JLabel("Nom :"), new JTextField(70));
		actor.addLine(1, null, new JCheckBox("Actif"));
		actor.addLine(2, new JLabel("Position :"), new JTextField(70));
		actor.addLine(3, new JLabel("Reports to :"), new JTextField(70));
		actor.addLine(4, new JLabel("Description :"), new JTextArea(5, 70));
		frame.getContentPane().add(actor.getPanel());
		frame.setVisible(true);
	}
	
	public JPanel getPanel(){
		return actor;
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


