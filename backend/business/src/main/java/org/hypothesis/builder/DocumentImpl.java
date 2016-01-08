/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
			return "root=" + root.toString(true, 0);
		} else {
			return "root=(null)";
		}
	}

}
