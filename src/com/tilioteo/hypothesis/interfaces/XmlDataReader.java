/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import org.dom4j.Element;

/**
 * @author kamil
 *
 */
public interface XmlDataReader extends Serializable {

	public void readDataFromElement(Element element);

}
