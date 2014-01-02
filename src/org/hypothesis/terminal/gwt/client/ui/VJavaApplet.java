/**
 * 
 */
package org.hypothesis.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * @author morong
 * 
 */
public class VJavaApplet extends HTML implements Paintable {

	private static String CLASSNAME = "v-javaapplet";

	private String height;
	private String width;

	private ApplicationConnection client;

	public VJavaApplet() {
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}
		this.client = client;

		Element el = null;
		boolean created = false;

		NodeList<Node> nodes = getElement().getChildNodes();
		if (nodes != null && nodes.getLength() == 1) {
			Node n = nodes.getItem(0);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (e.getTagName().equals("OBJECT")) {
					el = e;
				}
			}
		}
		if (el == null) {
			setHTML("");
			el = DOM.createElement("object");
			el.setAttribute("type", "application/x-java-applet");
			created = true;

			// Set attributes
			String w = uidl.getStringAttribute("width");
			if (w != null) {
				el.setAttribute("width", w);
			} else {
				el.removeAttribute("width");
			}
			String h = uidl.getStringAttribute("height");
			if (h != null) {
				el.setAttribute("height", h);
			} else {
				el.removeAttribute("height");
			}

			if (uidl.hasAttribute("code")) {
				Element param = DOM.createElement("param");
				param.setAttribute("name", "code");
				param.setAttribute("value", uidl.getStringAttribute("code"));
				el.appendChild(param);
			}

			if (uidl.hasAttribute("archive")) {
				Element param = DOM.createElement("param");
				param.setAttribute("name", "archive");
				param.setAttribute("value", uidl.getStringAttribute("archive"));
				el.appendChild(param);
			}

			if (created) {
				// insert in dom late
				getElement().appendChild(el);
			}

			String java_arguments = "";
			Map<String, String> arguments = getArguments(uidl);
			for (Map.Entry<String, String> entry : arguments.entrySet()) {
				if (java_arguments.length() > 0) {
					java_arguments += " -D"+entry.getKey()+"="+entry.getValue();
				} else {
					java_arguments = "-D"+entry.getKey()+"="+entry.getValue();
				}
			}

			if (java_arguments.length() > 0) {
				Element param = DOM.createElement("param");
				param.setAttribute("name", "java_arguments");
				param.setAttribute("value", java_arguments);
				el.appendChild(param);
			}
		}
	}

	private static Map<String, String> getArguments(UIDL uidl) {
		Map<String, String> arguments = new HashMap<String, String>();

		Iterator<Object> childIterator = uidl.getChildIterator();
		while (childIterator.hasNext()) {

			Object child = childIterator.next();
			if (child instanceof UIDL) {

				UIDL childUIDL = (UIDL) child;
				if (childUIDL.getTag().equals("java_argument")) {
					String name = childUIDL.getStringAttribute("name");
					String value = childUIDL.getStringAttribute("value");
					arguments.put(name, value);
				}
			}

		}

		return arguments;
	}

}
