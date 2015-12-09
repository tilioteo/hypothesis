/**
 * 
 */
package org.hypothesis.builder;

import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DocumentImpl implements Document {

	private Element root = null;

	@Override
	public Element root() {
		return root;
	}

	@Override
	public Element createRoot(String name) {
		root = new ElementImpl(name);
		return root;
	}

	@Override
	public Element createRoot(Element element) {
		root = new ElementImpl(element);
		return root;
	}

	@Override
	public String toString() {
		if (root != null) {
			return "root=" + root.toString();
		} else {
			return "root=(null)";
		}
	}

}
