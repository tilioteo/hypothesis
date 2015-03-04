/**
 * 
 */
package org.hypothesis.core;

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
public class SlideOutputParser {
	
	public static String parseOutput(String xmlString) {
		Document doc = Utility.readString(xmlString);
		if (doc != null) {
			Element element = (Element) doc.getRootElement().selectSingleNode(SlideXmlConstants.VALUE);
			if (element != null) {
				String value = element.getTextTrim(); 
				if (value.length() > 0) {
					return value;
				}
			}
		}
		return null;
	}
	
	public static Map<String, String> parseData(String xmlString) {
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
