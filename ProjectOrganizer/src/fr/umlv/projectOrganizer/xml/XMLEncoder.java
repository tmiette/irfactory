package fr.umlv.projectOrganizer.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import fr.umlv.projectOrganizer.Pair;
import fr.umlv.projectOrganizer.XmlEncodable;
import fr.umlv.projectOrganizer.ui.Encodable;

public class XMLEncoder {

	private final static String xmlFile = "./files/po.xml";

	/**
	 * Encode an encodable object
	 * @param encodableUI an encodable swing interface
	 * @return the xml encoded string
	 */
	public static WriterXML encode(Encodable encodableUI, String id){
		WriterXML writer = new WriterXML();
		Object clazz = encodableUI;
		writer = writer.xmlStart(encodableUI.getClass().getName()+":"+id);
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

		try {
			updateFile(xmlFile, writer.toString().split("\\?>")[1],  encodableUI.getClass().getName()+":"+id);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError();
		}
		return writer;
	}

	/**
	 * Decode an object from xml
	 * @param encodableUI an encodable class
	 */
	public static void decode(final Encodable encodableUI, final String id){
		ReaderXML xmlReader = new ReaderXML(){
			private boolean ok;

			/** Interprets a that starts an element. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void startsTag(String tag) {
				if(tag.equals(encodableUI.getClass().getName()+":"+id)){
					ok = true;
				}
			}

			/** Interprets a tag (defined in the class attributes 'tag'), with the
			 * associated text, provided as an argument of the method. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void getTag(String tag, String value) {
				if(ok){System.out.println(id);
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
											ch.setSelected(true);
										}
										else {
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
										//System.out.println("Setting text: "+value);
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
							//System.err.println("No such annotation");
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

			@Override
			protected void startDocument() {
				// TODO Auto-generated method stub
				
			}
		};

		WriterXML writer = new WriterXML();
		try {
			xmlReader.xmlIn(new BufferedReader(new FileReader(new File(xmlFile))), writer, false);
		} catch (FileNotFoundException e) {
			System.err.println("No such file ");
		}
	}



	public static HashMap<String, String> getValuesAsListFromXML(final Encodable encodableUI, final String needle){

		final HashMap<String, String> values = new HashMap<String, String>();

		ReaderXML xmlReader = new ReaderXML(){
			private boolean ok = false;
			private String identifier;

			@Override
			protected void startsTag(String tag) {
				String clazzIdentifier[] = tag.split(":");
				if(clazzIdentifier[0].equals(encodableUI.getClass().getName())){
					ok = true;
					identifier = clazzIdentifier[1];
				}
			}

			/** Interprets a tag (defined in the class attributes 'tag'), with the
			 * associated text, provided as an argument of the method. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void getTag(String tag, String value) {
				if (tag.equals(needle)){
					values.put(value,identifier);
					//System.out.println("Added : "+values);
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
				// Nothing
			}

			@Override
			protected void startDocument() {
				// TODO Auto-generated method stub
				
			}
		};
		
		WriterXML writer = new WriterXML();
		try {
			xmlReader.xmlIn(new BufferedReader(new FileReader(new File(xmlFile))), writer, false);
		} catch (FileNotFoundException e) {
			System.err.println("No such file ");
		}
		return values;
	}

	private static void updateFile(final String filename, final String record, final String id) throws IOException{
		final FileOutputStream filestream = new FileOutputStream(new File("./files/test.xml"));
	
		ReaderXML xmlReader = new ReaderXML(){
			private boolean finded;
			private WriterXML writer;	

			/** Interprets a that starts an element. Should be
			 * overriden by any derived class thats need to interpret a tag with
			 * associated value on the fly. */
			@Override
			protected void startsTag(String tag) {
				if(tag.equals(id)){
					finded = true;
				}
				else if (!tag.equals("projectorganizer")){
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
					//System.out.println("N'a pas changé : "+tag);
					writer = writer.xmlAttribute(tag, value);
				}
			}

			/** Ends the interpretation of a tag (defined in the class attributes 'tag').
			 * Should be overriden by any derived class that needs to interpret a tag on
			 * the fly. */
			@Override
			protected void end() {
				if(!finded && writer != null){
					try {
						writer.xmlEnd();
						filestream.write(writer.toString().split("\\?>")[1].getBytes());
						writer = null;
					} catch (IOException e) {
						throw new AssertionError();
					}
				}
				finded = false;
			}

			@Override
			protected void endDocument() {
				try {
					filestream.write(record.getBytes());
					filestream.write("</projectorganizer>".getBytes());
					new File("./files/test.xml").renameTo(new File(xmlFile));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected void startDocument() {
				// TODO Auto-generated method stub
				try {
					filestream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><projectorganizer>".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		};

		try{
			xmlReader.xmlIn(new BufferedReader(new FileReader(new File(xmlFile))), new WriterXML(), false);
		} catch (FileNotFoundException e) {
			System.err.println("No such file ");
		}
	}
}
