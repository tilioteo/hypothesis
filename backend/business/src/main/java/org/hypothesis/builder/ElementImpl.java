/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ElementImpl implements Element {

	private String name;
	private String namespace;
	private String shortName;

	private String text = null;

	private ElementImpl parent = null;
	private LinkedList<Element> children = new LinkedList<>();

	private HashMap<String, String> attributes = new HashMap<>();

	protected ElementImpl(String name, String text) {
		setName(name);
		this.text = text;
	}

	protected ElementImpl(String name) {
		this(name, null);
	}

	protected ElementImpl(Element element) {
		setName(element.getName());
		this.text = element.getText();

		for (Entry<String, String> entry : element.attributes().entrySet()) {
			this.attributes.put(entry.getKey(), entry.getValue());
		}
		Iterator<Element> iterator = element.iterator();
		while (iterator.hasNext()) {
			createChild((ElementImpl) iterator.next());
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public void setName(String name) {
		this.name = name;

		if (name != null) {
			int lastIndex = name.lastIndexOf(Document.NAMESPACE_SEPARATOR);

			this.namespace = lastIndex > 0 ? name.substring(0, lastIndex) : "";
			this.shortName = lastIndex > 0 ? name.substring(lastIndex + 1) : name;
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public ElementImpl parent() {
		return parent;
	}

	@Override
	public Map<String, String> attributes() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	@Override
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	@Override
	public List<Element> children() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public ElementImpl createChild(String name, String text) {
		ElementImpl child = new ElementImpl(name, text);
		child.parent = this;
		children.add(child);

		return child;
	}

	@Override
	public Element createChild(String name) {
		return createChild(name, null);
	}

	@Override
	public Element createChild(Element element) {
		ElementImpl child = new ElementImpl(element);
		child.parent = this;
		children.add(child);

		return child;
	}

	@Override
	public Element createChildBefore(Element sibling, String name, String text) {
		int index = children.indexOf(sibling);
		if (index >= 0) {
			ElementImpl child = new ElementImpl(name, text);
			child.parent = this;
			children.add(index, child);

			return child;
		}
		return null;
	}

	@Override
	public Element createChildBefore(Element sibling, String name) {
		return createChildBefore(sibling, name, null);
	}

	@Override
	public Element createChildAfter(Element sibling, String name, String text) {
		int index = children.indexOf(sibling);
		if (index >= 0) {
			ElementImpl child = new ElementImpl(name, text);
			child.parent = this;
			if (++index < children.size()) {
				children.add(index, child);
			} else {
				children.add(child);
			}

			return child;
		}
		return null;
	}

	@Override
	public Element createChildAfter(Element sibling, String name) {
		return createChildAfter(sibling, name, null);
	}

	@Override
	public void removeChild(Element element) {
		if (children.contains(element)) {
			children.remove(element);
			if (element instanceof ElementImpl) {
				((ElementImpl) element).parent = null;
			}
		}
	}

	@Override
	public Element firstChild() {
		try {
			return children.getFirst();
		} catch (Exception e) {
		}

		return null;
	}

	@Override
	public Element lastChild() {
		try {
			return children.getLast();
		} catch (Exception e) {
		}

		return null;
	}

	@Override
	public Element previousSibling() {
		if (parent != null) {
			Element previous = null;
			Iterator<Element> iterator = children.iterator();
			while (iterator.hasNext()) {
				Element node = iterator.next();
				if (node == this) {
					break;
				} else {
					previous = node;
				}
			}
			return previous;
		}

		return null;
	}

	@Override
	public Element nextSibling() {
		if (parent != null) {
			Iterator<Element> iterator = children.iterator();
			while (iterator.hasNext()) {
				Element node = iterator.next();
				if (node == this && iterator.hasNext()) {
					return iterator.next();
				}
			}
		}

		return null;
	}

	@Override
	public Iterator<Element> iterator() {
		return children.iterator();
	}

	@Override
	public Element selectElement(String name) {
		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			if (element.getName().equals(name)) {
				return element;
			}
		}

		return null;
	}

	@Override
	public List<Element> selectElements(String name) {
		List<Element> elements = new LinkedList<>();

		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			if (element.getName().equals(name)) {
				elements.add(element);
			}
		}

		return Collections.unmodifiableList(elements);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean detailed) {
		return toString(detailed, 0);
	}

	@Override
	public String toString(boolean detailed, int ident) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < ident; ++i) {
			builder.append("\t");
		}

		builder.append("<" + name + ":" + text + "(");

		if (!attributes.isEmpty()) {
			String str = "";
			for (Entry<String, String> entry : attributes.entrySet()) {
				str += entry.toString() + ",";
			}
			builder.append(str.substring(0, str.length() - 1));
		}
		builder.append(")[");

		if (!children.isEmpty()) {
			String str = "";

			if (!detailed) {
				for (Element element : children) {
					str += element.getName() + ",";
				}
				builder.append(str.substring(0, str.length() - 1));
			} else {
				builder.append("\n");

				for (Element element : children) {
					builder.append(element.toString(detailed, ident + 1));
					builder.append("\n");
				}
			}

			for (int i = 0; i < ident; ++i) {
				builder.append("\t");
			}

		}
		builder.append("]>");

		return builder.toString();
	}

}
