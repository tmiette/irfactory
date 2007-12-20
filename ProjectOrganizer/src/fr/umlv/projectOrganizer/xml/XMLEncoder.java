package fr.umlv.projectOrganizer.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.XmlEncodable;
import fr.umlv.projectOrganizer.ui.Encodable;

public class XMLEncoder {
	
	private final static String xmlFile = "./files/po.xml";
	
	/**
	 * Encode an encodable object
	 * @param encodableUI an encodable swing interface
	 * @return the xml encoded string
	 */
	public static WriterXML encode(Encodable encodableUI){
		WriterXML writer = new WriterXML();
		Object clazz = encodableUI;
		writer = writer.xmlStart(encodableUI.getClass()+"");
		for(Field field : encodableUI.getClass().getDeclaredFields()){
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
		System.out.println(writer);
		return writer;
	}
	
	/**
	 * Decode an object from xml
	 * @param encodableUI an encodable class
	 */
	public static void decode(final Encodable encodableUI){
		ReaderXML xmlReader = new ReaderXML(){
			private boolean ok;
			
			/** Interprets a that starts an element. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void startsTag(String tag) {
				if(tag.equals(encodableUI.getClass())){
					ok = true;
				}
			}
			
			 /** Interprets a tag (defined in the class attributes 'tag'), with the
		      * associated text, provided as an argument of the method. Should be
		      * overriden by any derived class thats need to interpret a tag with
		      * associated value on the fly. */
		    @Override
			protected void getTag(String tag, String value) {
		        if(ok){
		        		System.out.println(tag);
		        		for(Field field : encodableUI.getClass().getDeclaredFields()){
		        			field.setAccessible(true);
		        			try {
		        				XmlEncodable annotation = field.getAnnotation(XmlEncodable.class);
		        				if (annotation.getFieldEncodeName().equals(tag)){
		        					if(JCheckBox.class.isAssignableFrom(field.getType())){
		        						JCheckBox ch;
		        						try {
		        							ch = (JCheckBox)field.get(encodableUI);
		        							if (value.equals("y")){
		        								System.out.println("true");
		        								ch.setSelected(true);
		        							}
		        							else {
		        								System.out.println("false");
		        								ch.setSelected(false);
		        							}
		        						} catch (IllegalArgumentException e) {
		        							throw new AssertionError();
		        						} catch (IllegalAccessException e) {
		        							throw new AssertionError();
		        						}
		        					}
		        					else if(JTextComponent.class.isAssignableFrom(field.getType())){
		        						JTextComponent jt;
		        						try {
		        							jt = (JTextComponent)field.get(encodableUI);
		        							jt.setText(value);
		        						} catch (IllegalArgumentException e) {
		        							throw new AssertionError();
		        						} catch (IllegalAccessException e) {
		        							throw new AssertionError();
		        						}
		        					}
		        				}
		        			}
		        			catch (NullPointerException e){
		        				System.err.println("No such annotation");
		        			}
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
				//Nothing
			}
		};

		WriterXML writer = new WriterXML();
		try {
			xmlReader.xmlIn(new BufferedReader(new FileReader(new File(xmlFile))), writer, false);
		} catch (FileNotFoundException e) {
			System.err.println("No such file ");
		}
	}
	
	
	
	public static void getValuesAsListFromXML(){
		
	}

	private static void updateFile(final String filename, final String record, final String id) throws IOException{
		
		
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
					fileWriter.write(record);
				} catch (IOException e) {
					throw new AssertionError();
				}
		    }
		};
	}
}
