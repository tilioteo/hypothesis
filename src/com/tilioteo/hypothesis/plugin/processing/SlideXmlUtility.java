/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import org.dom4j.Element;

/**
 * @author kamil
 *
 */
public class SlideXmlUtility {

	//private static Logger log = Logger.getLogger(SlideXmlUtility.class);

	public static String getId(Element element) {
		return element.attributeValue(SlideXmlConstants.ID);
	}

	public static String getValue(Element element) {
		return element.attributeValue(SlideXmlConstants.VALUE);
	}

	public static Element getCodeElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.CODE);
		}

		return null;
	}

}
