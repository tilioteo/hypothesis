/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import org.dom4j.Document;

/**
 * @author kamil
 *
 */
public interface HasDocument {
	
	Document getDocument();
	
	void setDocument(Document document);
	
	boolean isValidDocument(Document doc);

}
