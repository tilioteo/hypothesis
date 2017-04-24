/**
 * 
 */
package org.hypothesis.data;

import org.dom4j.Document;
import org.hypothesis.utility.XmlUtility;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author morongk
 *
 */
public final class XmlTestUtility {

	private XmlTestUtility() {
	}
	
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

	public static String getSampleWriterString() {
		try {
			return new String(Files.readAllBytes(Paths.get(XmlTestUtility.class.getResource("/xml/sampleWriter.txt").toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
