/**
 * 
 */
package com.tilioteo.hypothesis.messaging;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author kamil
 *
 */
public class MessageXmlUtility {

	public static boolean isValidMessageXml(Document doc) {
		return (doc != null && doc.getRootElement() != null
				&& doc.getRootElement().getName().equals(MessageXmlConstants.MESSAGE));
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getPropertyElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!MessageXmlConstants.MESSAGE.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> properties = documentRoot
					.selectNodes(String.format("%s//%s", MessageXmlConstants.PROPERTIES, MessageXmlConstants.PROPERTY));

			return properties;
		}

		return null;
	}

}
