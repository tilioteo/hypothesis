/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface Element extends Serializable, Iterable<Element> {

	String getName();
	
	String getNamespace();
	
	String getShortName();

	void setName(String name);

	String getText();

	void setText(String text);

	Element parent();

	Map<String, String> attributes();

	void setAttribute(String name, String value);

	String getAttribute(String name);

	void removeAttribute(String name);

	boolean hasAttribute(String name);

	List<Element> children();

	Element createChild(String name, String text);

	Element createChild(String name);

	Element createChild(Element element);

	Element createChildBefore(Element sibling, String name, String text);

	Element createChildBefore(Element sibling, String name);

	Element createChildAfter(Element sibling, String name, String text);

	Element createChildAfter(Element sibling, String name);

	void removeChild(Element element);

	Element firstChild();

	Element lastChild();

	Element previousSibling();

	Element nextSibling();

	Element selectElement(String name);

	List<Element> selectElements(String name);
	
	String toString(boolean detailed, int ident);

}
