/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import org.dom4j.Element;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface XmlDataWriter extends Serializable {

	public void writeDataToElement(Element element);

}
