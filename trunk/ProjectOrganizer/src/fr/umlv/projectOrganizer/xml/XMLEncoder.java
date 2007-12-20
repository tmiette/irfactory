package fr.umlv.projectOrganizer.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.Encodable;
import fr.umlv.projectOrganizer.ui.PanelData;

public class XMLEncoder {
	
	public static WriterXML encode(PanelData d, String id){
		WriterXML writer = new WriterXML();
		Object clazz = d;
		
		writer = writer.xmlStart("actor");
		for(Field field : d.getClass().getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getAnnotations()){
				if(a.annotationType() == Encodable.class){
					if(JCheckBox.class.isAssignableFrom(field.getType())){
						JCheckBox ch;
						try {
							ch = (JCheckBox)field.get(clazz);
						} catch (IllegalArgumentException e) {
							throw new AssertionError();
						} catch (IllegalAccessException e) {
							throw new AssertionError();
						}
						writer = writer.xmlAttribute(((Encodable)a).getFieldEncodeName(), ch.isSelected() ? "y" :  "n");
					}
					else if(JTextComponent.class.isAssignableFrom(field.getType())){
						JTextComponent jt;
						try {
							jt = (JTextComponent)field.get(clazz);
						} catch (IllegalArgumentException e) {
							throw new AssertionError();
						} catch (IllegalAccessException e) {
							throw new AssertionError();
						}
						writer = writer.xmlAttribute(((Encodable)a).getFieldEncodeName(), jt.getText());
					}
				}
			}
		}
		writer.xmlEnd();
		System.out.println(writer);
		return writer;
	}
	
	public static boolean decode(final Class<?> c, final String id){
		ReaderXML xmlReader = new ReaderXML(){
			private boolean ok;
			private Object clazz;
			/** Interprets a that starts an element. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void startsTag(String tag) {
				if(tag.equals(c.getName()+":"+id)){
					ok = true;
					try {
						this.clazz = c.newInstance();
					} catch (InstantiationException e) {
						throw new AssertionError();
					} catch (IllegalAccessException e) {
						throw new AssertionError();
					}
				}
			}
			
			 /** Interprets a tag (defined in the class attributes 'tag'), with the
		      * associated text, provided as an argument of the method. Should be
		      * overriden by any derived class thats need to interpret a tag with
		      * associated value on the fly. */
		    @Override
			protected void getTag(String tag, String value) {
		        if(ok){
		        	try {
						Field field = c.getField(tag);
						if(JCheckBox.class.isAssignableFrom(field.getType())){
							try {
								JCheckBox ch = new JCheckBox();
								if(value.equals("y")) ch.setSelected(true); 
								else{ ch.setSelected(false);} 
								
								field.set(clazz, ch);
							} catch (IllegalArgumentException e) {
								throw new AssertionError();
							} catch (IllegalAccessException e) {
								throw new AssertionError();
							}
							
						}
						else if(JTextComponent.class.isAssignableFrom(field.getType())){
							try {
								JTextComponent jt = (JTextComponent)field.get(clazz);
								jt.setText(value);
								field.set(clazz, jt);
							} catch (IllegalArgumentException e) {
								throw new AssertionError();
							} catch (IllegalAccessException e) {
								throw new AssertionError();
							}
						}
					} catch (SecurityException e) {
						throw new AssertionError();
					} catch (NoSuchFieldException e) {
						throw new AssertionError();
					}
		        }
		    }
			
		    /** Ends the interpretation of a tag (defined in the class attributes 'tag').
		      * Should be overriden by any derived class that needs to interpret a tag on
		      * the fly. */
		    @Override
		    protected void end() {
		    	ok = false;
		    }
		};

		
		return true;
	}
	
	
}
