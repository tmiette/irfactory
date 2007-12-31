package fr.umlv.projectOrganizer.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This annotation defines a field which can be encoded in xml format
 * 
 * @author Moreau Alan
 * @author Pons Julien
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlEncodable {
	String getFieldEncodeName();
}
