package fr.umlv.projectOrganizer.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.Encodable;

public class XMLEncoder {
	
	public static WriterXML encode(Class<?> c, Object o) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException, InstantiationException{
		WriterXML writer = new WriterXML();
		
		for(Field field : c.getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getAnnotations()){
				if(a.annotationType() == Encodable.class){
					if(JCheckBox.class.isAssignableFrom(field.getType())){
						
						Object uu = c.newInstance(); 
						System.out.println(field.get(uu));
						//System.out.println(uu);
					}
					else if(JTextComponent.class.isAssignableFrom(field.getType())){
						Object uu = c.newInstance(); 
						System.out.println(((JTextField)field.get(uu)).getText());
					}
				}
			}
		}
		
		return writer;
	}
	
	
}
