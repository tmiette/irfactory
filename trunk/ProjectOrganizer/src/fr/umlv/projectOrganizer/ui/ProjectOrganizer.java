package fr.umlv.projectOrganizer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.projectOrganizer.XmlEncodable;
import fr.umlv.projectOrganizer.xml.XMLEncoder;


public class ProjectOrganizer {

	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 500); 

		// currentIdentifier
		final StringBuilder identifier = new StringBuilder();

		// Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Actor panel
		final PanelActor actor = new PanelActor();
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Actor", actor.getPanel());

		// Init actor list
		final HashMap<String, String> data = XMLEncoder.getXMLValues(actor, "nom");
		final JList list = new JList(); 
		list.setPrototypeCellValue(new String("prototypelenght"));
		refreshList(list, data);

		// Panel Action
		JPanel action = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton newActor = new JButton("New");
		JButton saveActor = new JButton("Save");
		action.add(newActor);
		action.add(saveActor);

		newActor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				XMLEncoder.encode(actor, UUID.randomUUID().toString());
				HashMap<String, String> newlist = XMLEncoder.getXMLValues(actor, "nom");
				refreshList(list, newlist);
			}
		});
		saveActor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				XMLEncoder.encode(actor, identifier.toString());
				HashMap<String, String> newlist = XMLEncoder.getXMLValues(actor, "nom");
				refreshList(list, newlist);
			}
		});

		// List listener
		list.setSelectedIndex(0);
		list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		list.addListSelectionListener(new ListSelectionListener(){
			private int oldSelectedIndex = -1;
			public void valueChanged(ListSelectionEvent e) {
				if (oldSelectedIndex != list.getSelectedIndex()){
					String value = (String)list.getSelectedValue();
					if (value == null) return;
					identifier.replace(0, identifier.length(), "");
					String id = data.get(value);
					identifier.append(id);
					XMLEncoder.decode(actor, id);
					HashMap<String, String> newEntry = XMLEncoder.getXMLValues(actor, "nom");
					for(Entry<String, String> entry:newEntry.entrySet()){
						if (data.get(entry.getKey()) == null){
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
		mainPanel.add(action, BorderLayout.NORTH);
		mainPanel.add(list, BorderLayout.WEST);
		mainPanel.add(tabPane, BorderLayout.CENTER);
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
	}


	public static void refreshList(JList list, HashMap<String, String> newlist){
		ArrayList<String> value = new ArrayList<String>();
		Set<Entry<String,String>> set = newlist.entrySet();
		for (Entry<String, String> entry : set) {
			value.add(entry.getKey());
		}
		list.setListData(value.toArray());
	}



}


