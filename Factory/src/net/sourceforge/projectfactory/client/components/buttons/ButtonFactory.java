package net.sourceforge.projectfactory.client.components.buttons;

/**This is a method factory which creates different buttons
 * 
 * @author Moreau Alan - Miette Tom - Mouret Sebastien - Pons Julien
 */
public class ButtonFactory {
	
	public static Button createAddButton(){
		return new ButtonAdd();
	}
	
	public static Button createAddLevelButton(){
		return new ButtonAddLevel();
	}
	
	public static Button createRemoveLevelButton(){
		return new ButtonAddLevel();
	}
	
	public static Button createRemoveButton(){
		return new ButtonAddLevel();
	}
	
	public static Button createDownButton(){
		return new ButtonDown();
	}
	
	public static Button createUpButton(){
		return new ButtonUp();
	}
	
	public static Button createCommandButton(String title){
		return new ButtonCommand(title);
	}
}
