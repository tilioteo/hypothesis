/**
 * 
 */
package org.hypothesis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.application.common.xml.SlideXmlConstants;
import org.hypothesis.common.xml.Utility;

/**
 * @author kamil
 *
 */
public class SlideDataParser {
	
	public static List<String> parseOutputValues(String xmlString) {
		List<String> list = new ArrayList<String>();
		Document doc = Utility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement().selectNodes(String.format(Utility.DESCENDANT_FMT, SlideXmlConstants.OUTPUT_VALUE));

			if (elements.size() > 0) {
				for (int i = 0; i < 10; ++i) {
					list.add(null);
				}
			}
			
			for (Element element : elements) {
				String index = element.attributeValue(SlideXmlConstants.INDEX);

				try {
					int i = Integer.parseInt(index);
					String value = element.getTextTrim(); 
					if (!value.isEmpty() && i >=1 && i <= list.size()) {
						list.set(i-1, value);
					}
				} catch (NumberFormatException e) {}
			}
		}
		return list;
	}
	
	public static Map<String, String> parseFields(String xmlString) {
		HashMap<String, String> map = new HashMap<String, String>();
		Document doc = Utility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement().selectNodes(String.format(Utility.DESCENDANT_FMT, SlideXmlConstants.FIELD));
			for (Element element : elements) {
				String id = element.attributeValue(SlideXmlConstants.ID);
				
				String caption = "";
				Element captionElement = (Element) element.selectSingleNode(SlideXmlConstants.CAPTION);
				if (captionElement != null) {
					caption = captionElement.getTextTrim();
				}
				
				if (!caption.isEmpty()) {
					id += " [" + caption + "]";
				}

				String valueId = "";
				Element valueElement = (Element) element.selectSingleNode(SlideXmlConstants.VALUE);
				if (valueElement != null) {
					valueId = valueElement.attributeValue(SlideXmlConstants.ID);
					String value = valueElement.getText();
					if (valueId != null && !valueId.isEmpty()) {
						value = valueId + " [" + value + "]";
					}
					map.put(id, value);
				}
			}
		}
		return map;
	}
}
