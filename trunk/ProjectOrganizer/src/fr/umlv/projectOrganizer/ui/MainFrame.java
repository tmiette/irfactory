package fr.umlv.projectOrganizer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.projectOrganizer.xml.XMLEncoder;

/**
 * This is the main Frame of the application
 * It contains the different components such as JPanel or JList
 *  
 * @author Moreau Alan
 * @author Pons Julien
 */
public class MainFrame {

	private final JFrame mainFrame;

	private final JPanel mainPanel;

	public MainFrame() {

		this.mainFrame = new JFrame();
		this.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.mainFrame.setSize(800, 500);

		// currentIdentifier
		final StringBuilder identifier = new StringBuilder();

		// Main panel
		this.mainPanel = new JPanel(new BorderLayout());

		// Actor panel
		final PanelActor actor = new PanelActor();
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Actor", actor.getPanel());

		// Init actor list
		final HashMap<String, String> data = XMLEncoder.getXMLValues(actor, "nom");
		final DefaultListModel listModel = new DefaultListModel();
		final JList list = new JList(listModel);
		list.setPrototypeCellValue(new String("prototypelenght"));
		list.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		refreshList(list, data);

		// Panel Action
		JPanel action = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton newActor = new JButton("New");
		JButton saveActor = new JButton("Save");
		action.add(newActor);
		action.add(saveActor);

		newActor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				XMLEncoder.encode(actor, UUID.randomUUID().toString());
				HashMap<String, String> newlist = XMLEncoder.getXMLValues(actor, "nom");
				refreshList(list, newlist);
			}
		});
		saveActor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				XMLEncoder.encode(actor, identifier.toString());
				HashMap<String, String> newlist = XMLEncoder.getXMLValues(actor, "nom");
				refreshList(list, newlist);
			}
		});

		// List listener
		list.setSelectedIndex(0);
		list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		list.addListSelectionListener(new ListSelectionListener() {

			private int oldSelectedIndex = -1;

			public void valueChanged(ListSelectionEvent e) {

				if (oldSelectedIndex != list.getSelectedIndex()) {
					String value = (String) list.getSelectedValue();
					if (value == null)
						return;
					identifier.replace(0, identifier.length(), "");
					String id = data.get(value);
					identifier.append(id);
					XMLEncoder.decode(actor, id);
					HashMap<String, String> newEntry = XMLEncoder.getXMLValues(actor,
							"nom");
					for (Entry<String, String> entry : newEntry.entrySet()) {
						if (data.get(entry.getKey()) == null) {
							data.put(entry.getKey(), entry.getValue());
							data.remove(value);
							break;
						}
					}

				}
				oldSelectedIndex = list.getSelectedIndex();
			}
		});

		// Create UI
		this.mainPanel.add(action, BorderLayout.NORTH);
		this.mainPanel.add(list, BorderLayout.WEST);
		this.mainPanel.add(tabPane, BorderLayout.CENTER);
		this.mainFrame.getContentPane().add(this.mainPanel);
		this.mainFrame.setVisible(true);
	}

	/**
	 * Refreshes the list containing actors
	 * @param list oldlist
	 * @param newlist
	 */
	public static void refreshList(JList list, HashMap<String, String> newlist) {

		DefaultListModel listModel = (DefaultListModel) list.getModel();
		for (Entry<String, String> entry : newlist.entrySet()) {
			listModel.addElement(entry.getKey());
		}
	}
	
	/**Retrieves the Main Frame.
	 * 
	 * @return JFrame the main Frame
	 */
	public JFrame getMainFrame() {
		return this.mainFrame;
	}
}
