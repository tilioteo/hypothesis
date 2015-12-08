/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import com.tilioteo.hypothesis.interfaces.Document;
import com.tilioteo.hypothesis.interfaces.Element;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DocumentImpl implements Document {

	private Element root = null;

	public Element root() {
		return root;
	}

	public Element createRoot(String name) {
		root = new ElementImpl(name);
		return root;
	}

	public Element createRoot(Element element) {
		root = new ElementImpl(element);
		return root;
	}

}
