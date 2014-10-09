/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideUtility {

	@SuppressWarnings("unchecked")
	public static StringMap getActionAttributesMap(Element element) {
		StringMap map = new StringMap();

		List<Attribute> attributes = element.attributes();
		for (Attribute attribute : attributes) {
			map.put(attribute.getName(), attribute.getValue());
		}

		return map;
	}

	public static List<Element> getHandlerElements(Element component) {
		return SlideXmlUtility.getComponentHandlers(component);
	}

	public static List<Element> getItemElements(Element component) {
		return SlideXmlUtility.getComponentItems(component);
	}

	public static List<Element> getValidatorElements(Element field) {
		return SlideXmlUtility.getFieldValidators(field);
	}

	public static StringMap getPropertyValueMap(Element component) {
		StringMap map = new StringMap();
		List<Element> elements = SlideXmlUtility
				.getComponentProperties(component);

		for (Element element : elements) {
			String name = element.getName();
			Attribute value = element.attribute(SlideXmlConstants.VALUE);
			if (value != null)
				map.put(name, value.getValue());
		}

		return map;
	}
	
}
