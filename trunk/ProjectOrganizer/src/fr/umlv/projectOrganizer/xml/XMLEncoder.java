package fr.umlv.projectOrganizer.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.XmlEncodable;
import fr.umlv.projectOrganizer.ui.Encodable;

public class XMLEncoder {
	
	public static boolean encode(Encodable d, String id){
		WriterXML writer = new WriterXML();
		Object clazz = d;
		writer = writer.xmlStart("actor");
		for(Field field : d.getClass().getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getAnnotations()){
				if(a.annotationType() == XmlEncodable.class){
					if(JCheckBox.class.isAssignableFrom(field.getType())){
						JCheckBox ch;
						try {
							ch = (JCheckBox)field.get(clazz);
						} catch (IllegalArgumentException e) {
							throw new AssertionError();
						} catch (IllegalAccessException e) {
							throw new AssertionError();
						}
						writer = writer.xmlAttribute(((XmlEncodable)a).getFieldEncodeName(), ch.isSelected() ? "y" :  "n");
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
						writer = writer.xmlAttribute(((XmlEncodable)a).getFieldEncodeName(), jt.getText());
					}
				}
			}
		}
		writer.xmlEnd();
	    
		try {
			updateFile("file/actors.xml", writer, id);
		} catch (IOException e) {
			throw new AssertionError();
		}
		return true;
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

			@Override
			protected void endDocument() {
				// TODO Auto-generated method stub
				
			}
		};

		//WriterXML writer = new WriterXML();
		//xmlReader.xmlIn("po.xml", writer, false);
		//System.out.println(writer);
		return true;
	}
	
	private static void updateFile(final String filename, final WriterXML writerRecord, final String id) throws IOException{
		
		
		ReaderXML xmlReader = new ReaderXML(){
			private boolean finded;
			private Object clazz;
			private WriterXML writer;
			final FileWriter fileWriter = new FileWriter(new File(filename));
			
			
			/** Interprets a that starts an element. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void startsTag(String tag) {
				if(tag.equals(id)){
					finded = true;
				}
				else{
					writer = new WriterXML();
					writer = writer.xmlStart(tag);
				}
			}
			
			 /** Interprets a tag (defined in the class attributes 'tag'), with the
		      * associated text, provided as an argument of the method. Should be
		      * overriden by any derived class thats need to interpret a tag with
		      * associated value on the fly. */
		    @Override
			protected void getTag(String tag, String value) {
		    	if(!finded){
		    		writer = writer.xmlAttribute(tag, value);
		    	}
		    }
			
		    /** Ends the interpretation of a tag (defined in the class attributes 'tag').
		      * Should be overriden by any derived class that needs to interpret a tag on
		      * the fly. */
		    @Override
		    protected void end() {
		    	finded = false;
		    	try {
					fileWriter.write(writer.toString());
				} catch (IOException e) {
					throw new AssertionError();
				}
				writer = null;
		    }
		    
		    @Override
		    protected void endDocument() {
		    	try {
					fileWriter.write(writerRecord.toString());
				} catch (IOException e) {
					throw new AssertionError();
				}
		    }
		};
	}
}
