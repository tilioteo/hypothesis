/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.common.Strings;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideXmlUtility implements Serializable {

	//private static Logger log = Logger.getLogger(SlideXmlUtility.class);

	public static String getId(Element element) {
		return element.attributeValue(SlideXmlConstants.ID);
	}

	public static String getName(Element element) {
		return element.attributeValue(SlideXmlConstants.NAME);
	}
	
	public static ArrayList<String> getArgumentTypes(Element element) {
		ArrayList<String> types = new ArrayList<String>();
		
		List<Element> arguments = getArgumentElements(element);
		
		if (arguments != null) {
			for (Element argument : arguments) {
				String type = argument.attributeValue(SlideXmlConstants.TYPE);
				if (!Strings.isNullOrEmpty(type)) {
					types.add(type);
				} else {
					types.add(SlideXmlConstants.STRING);
				}
			}
		}
		return types;
	}

	@SuppressWarnings("unchecked")
	private static List<Element> getArgumentElements(Element element) {
		if (element != null) {
			return element.selectNodes(SlideXmlConstants.ARGUMENT);
		}

		return null;
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
