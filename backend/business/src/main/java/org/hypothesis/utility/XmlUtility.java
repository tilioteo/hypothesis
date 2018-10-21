/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.utility;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class XmlUtility implements Serializable {

	private static final String ENCODING_UTF8 = "utf-8";
	public static final String DESCENDANT_FMT = "descendant::%s";

	@SuppressWarnings("unchecked")
	public static void clearAllChilds(Node parent) {
		if (parent != null) {
			for (Iterator<Node> i = parent.selectNodes("").iterator(); i.hasNext();) {
				Node node = i.next();
				node.detach();
			}
		}
	}

	/*
	 * public static boolean writeFile(final Document doc, final File file) { if
	 * (doc != null && file.getName().length() > 0) { try { OutputFormat
	 * outputFormat = OutputFormat.createPrettyPrint();
	 * outputFormat.setEncoding(doc.getXMLEncoding());
	 * 
	 * XMLWriter writer = new XMLWriter(new FileWriter(file), outputFormat);
	 * writer.write(doc); writer.close(); return true; } catch (Throwable e) { }
	 * }
	 * 
	 * return false; }
	 */

	public static Document createDocument() {
		Document doc = DocumentFactory.getInstance().createDocument();
		doc.setXMLEncoding(ENCODING_UTF8);

		return doc;
	}

	public static Attribute findAttributeByName(Node node, String name) {
		if (node != null && name.length() > 0) {
			if (node instanceof Element) {
				Element el = (Element) node;
				return el.attribute(name);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static Element findElementByNameAndValue(boolean descendant, Element element, String name, String prefix,
			String uri, String attributeName, String attributeValue) {
		Element result = null;
		if (element != null) {
			HashMap<String, String> namespaces = new HashMap<>();
			if (isNotEmpty(prefix) && isNotEmpty(uri)) {
				name = String.format("%s:%s", prefix, name);
				namespaces.put(prefix, uri);
			}

			XPath path = element.createXPath(descendant ? String.format(DESCENDANT_FMT, name) : name);
			if (namespaces.size() > 0) {
				path.setNamespaceURIs(namespaces);
			}

			List<Node> nodes = path.selectNodes(element);
			for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
				Node node = i.next();

				if (node instanceof Element) {
					Element el = (Element) node;

					if (attributeName != null) {
						Attribute attr = el.attribute(attributeName);
						if (attr != null && attr.getValue().equals(attributeValue)) {
							result = el;
							break;
						}
					} else {
						result = el;
						break;
					}
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Node findFirstNodeByName(Node parent, String name) {
		if (parent != null && name.length() > 0) {
			for (Iterator<Node> i = parent.selectNodes(String.format(DESCENDANT_FMT, name)).iterator(); i.hasNext();) {
				Node node = i.next();

				if (node.getName().equals(name))
					return node;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Node> findNodesByNameStarting(Node parent, String startName) {
		if (parent != null && startName.length() > 0) {
			return parent.selectNodes("./*[starts-with(name(), '" + startName + "')]");
		}

		return null;
	}

	public static Document readFile(final File file) {
		if (file.exists()) {
			try {
				SAXReader reader = new SAXReader();
				return reader.read(file);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static Document readString(final String xmlString) {
		if (xmlString != null && xmlString.length() > 0) {
			try {
				StringReader stringReader = new StringReader(xmlString);
				SAXReader reader = new SAXReader();
				// reader.setFeature("http://xml.org/sax/features/namespaces",
				// false);
				return reader.read(stringReader);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String writeString(final Document doc) {
		String string = null;
		if (doc != null) {
			try {
				StringWriter stringWriter = new StringWriter();
				XMLWriter writer = new XMLWriter(stringWriter);
				writer.write(doc);
				writer.close();
				string = stringWriter.toString();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return string;
	}
}
