/**
 * 
 */
package org.hypothesis.common.xml;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class XmlDocument {

	protected Document doc = null;
	
	public boolean Copy(XmlDocument other) {
		if (other != null && other.doc != null) {
			this.doc = (Document)other.doc.clone();
		}
		else
			this.doc = null;
		
		return (this.doc != null);
	}
	
	public Document getDocument() {
		return doc;
	}
	
	protected void setDocument(Document doc) {
		this.doc = doc;
	}

	public Element getRootElement() {
		if (doc != null) {
			return doc.getRootElement();
		}
		return null;
	}
	
	public boolean readString(final String string) {
		this.doc = Utility.readString(string);
		return (this.doc != null);
	}

	public String writeString() {
		return Utility.writeString(this.doc);
	}
}
