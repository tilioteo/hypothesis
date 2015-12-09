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

	public String getName();

	public void setName(String name);

	public String getText();

	public void setText(String text);

	public Element parent();

	public Map<String, String> attributes();

	public void setAttribute(String name, String value);

	public String getAttribute(String name);

	public void removeAttribute(String name);

	public boolean hasAttribute(String name);

	public List<Element> children();

	public Element createChild(String name, String text);

	public Element createChild(String name);

	public Element createChild(Element element);

	public Element createChildBefore(Element sibling, String name, String text);

	public Element createChildBefore(Element sibling, String name);

	public Element createChildAfter(Element sibling, String name, String text);

	public Element createChildAfter(Element sibling, String name);

	public void removeChild(Element element);

	public Element firstChild();

	public Element lastChild();

	public Element previousSibling();

	public Element nextSibling();

	public Element selectElement(String name);

	public List<Element> selectElements(String name);

}
