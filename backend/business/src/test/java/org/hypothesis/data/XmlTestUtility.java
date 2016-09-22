/**
 * 
 */
package org.hypothesis.data;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.dom4j.Document;
import org.hypothesis.utility.XmlUtility;

/**
 * @author morongk
 *
 */
public class XmlTestUtility {
	
	public static String getSampleReaderXmlString() {
		try {
			return new String(Files.readAllBytes(Paths.get(XmlTestUtility.class.getResource("/xml/sampleReader.xml").toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getSampleReaderString() {
		try {
			return new String(Files.readAllBytes(Paths.get(XmlTestUtility.class.getResource("/xml/sampleReader.txt").toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Document getSampleReaderXml() {
		try {
			Document doc = XmlUtility.readString(getSampleReaderXmlString());
			
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
