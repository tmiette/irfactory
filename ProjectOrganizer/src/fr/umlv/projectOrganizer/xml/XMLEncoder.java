package fr.umlv.projectOrganizer.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.Encodable;

public class XMLEncoder {
	
	public static WriterXML encode(Class<?> c) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException, InstantiationException{
		WriterXML writer = new WriterXML();
		Object clazz = c.newInstance();
		writer = writer.xmlStart("actor");
		
		for(Field field : c.getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getAnnotations()){
				if(a.annotationType() == Encodable.class){
					if(JCheckBox.class.isAssignableFrom(field.getType())){
						JCheckBox ch = (JCheckBox)field.get(clazz);
						writer = writer.xmlAttribute(((Encodable)a).getFieldEncodeName(), ch.isSelected() ? "y" :  "n");
					}
					else if(JTextComponent.class.isAssignableFrom(field.getType())){
						System.out.println("tot");
						JTextComponent jt = (JTextComponent)field.get(clazz);
						writer = writer.xmlAttribute(((Encodable)a).getFieldEncodeName(), jt.getText());
					}
				}
			}
		}
		
		writer.xmlEnd();
		System.out.println(writer);
		return writer;
	}
	
	
}
