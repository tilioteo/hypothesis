/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.hypothesis.interfaces.DocumentConstants;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
@Deprecated
public class SlideDataParser {
	
	public static List<String> parseOutputValues(String xmlString) {
		List<String> list = new ArrayList<String>();
		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement().selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.OUTPUT_VALUE));

			if (elements.size() > 0) {
				for (int i = 0; i < 10; ++i) {
					list.add(null);
				}
			}
			
			for (Element element : elements) {
				String index = element.attributeValue(DocumentConstants.INDEX);

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
	
	public static FieldWrapper parseFields(String xmlString) {
		FieldWrapper wrapper = new FieldWrapper();

		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement().selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.FIELD));
			for (Element element : elements) {
				String id = element.attributeValue(DocumentConstants.ID);
				
				String caption = null;
				Element captionElement = (Element) element.selectSingleNode(DocumentConstants.CAPTION);
				if (captionElement != null) {
					caption = captionElement.getTextTrim();
				}
				
				wrapper.fieldCaptionMap.put(id, caption.isEmpty() ? null : caption);

				String valueId = null;
				Element valueElement = (Element) element.selectSingleNode(DocumentConstants.VALUE);
				if (valueElement != null) {
					valueId = valueElement.attributeValue(DocumentConstants.ID);
					String value = valueElement.getText();
					if (valueId != null && !valueId.isEmpty()) {
						wrapper.fieldValueMap.put(id, valueId);
						if (!valueId.equals(value) && !value.isEmpty()) {
							Map<String, String> valueCaptionMap = wrapper.fieldValueCaptionMap.get(id);
							
							if (null == valueCaptionMap) {
								valueCaptionMap = new HashMap<String, String>();
								wrapper.fieldValueCaptionMap.put(id, valueCaptionMap);
							}
							valueCaptionMap.put(valueId, value);
						}
					} else {
						wrapper.fieldValueMap.put(id, value);
					}
				}
			}
		}
		return wrapper;
	}
	
	@SuppressWarnings("serial")
	public static final class FieldWrapper implements Serializable {
		private HashMap<String, String> fieldCaptionMap = new HashMap<String, String>();
		private HashMap<String, String> fieldValueMap = new HashMap<String, String>();
		private HashMap<String, Map<String, String>> fieldValueCaptionMap = new HashMap<String, Map<String, String>>();
		
		protected FieldWrapper() {
		}
		
		public Map<String, String> getFieldCaptionMap() {
			return fieldCaptionMap;
		}

		public Map<String, String> getFieldValueMap() {
			return fieldValueMap;
		}

		public Map<String, Map<String, String>> getFieldValueCaptionMap() {
			return fieldValueCaptionMap;
		}
	}
}
