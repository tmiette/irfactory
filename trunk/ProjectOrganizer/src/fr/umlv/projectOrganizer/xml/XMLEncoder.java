package fr.umlv.projectOrganizer.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.Encodable;

public class XMLEncoder {
	
	public static WriterXML encode(Class<?> c){
		
		WriterXML writer = new WriterXML();
		
		for(Field field : c.getFields()){
			for(Annotation a : field.getAnnotations()){
				if(a.annotationType() == Encodable.class){
					if(JCheckBox.class.isAssignableFrom(field.getClass())){
						JCheckBox ch = field.;
						
						//writer.xmlAttribute(((Encodable)a).getFieldEncodeName(), ().isSelected() ? "true" : "false");
					}
					else if(JTextComponent.class.isAssignableFrom(field.getClass())){
						
					}
				}
			}
		}
		
		return writer;
	}
	
	
}
