/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.utility.XmlUtility;

import java.io.Serializable;
import java.util.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 * @deprecated
 */
@Deprecated
public final class SlideDataParser {

	private SlideDataParser() {
	}

	public static List<String> parseOutputValues(String xmlString) {
		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement()
					.selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.OUTPUT_VALUE));

			List<String> list = Arrays.asList(new String[10]);

			elements.forEach(e -> {
				String index = e.attributeValue(DocumentConstants.INDEX);

				try {
					int i = Integer.parseInt(index);
					String value = e.getTextTrim();
					if (!value.isEmpty() && i >= 1 && i <= list.size()) {
						list.set(i - 1, value);
					}
				} catch (NumberFormatException ex) {
				}
			});
			
			return list;
			
			}
		return Collections.emptyList();
		}

	public static FieldWrapper parseFields(String xmlString) {
		FieldWrapper wrapper = new FieldWrapper();

		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = doc.getRootElement()
					.selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.FIELD));
			elements.forEach(e -> {
				String id = e.attributeValue(DocumentConstants.ID);

				String caption = null;
				Element captionElement = (Element) e.selectSingleNode(DocumentConstants.CAPTION);
				if (captionElement != null) {
					caption = captionElement.getTextTrim();
				}

				wrapper.fieldCaptionMap.put(id, null == caption || caption.isEmpty() ? null : caption);

				String valueId;
				Element valueElement = (Element) e.selectSingleNode(DocumentConstants.VALUE);
				if (valueElement != null) {
					valueId = valueElement.attributeValue(DocumentConstants.ID);
					String value = valueElement.getText();
					if (StringUtils.isNotBlank(valueId)) {
						wrapper.fieldValueMap.put(id, valueId);
						if (!valueId.equals(value) && !value.isEmpty()) {
							Map<String, String> valueCaptionMap = wrapper.fieldValueCaptionMap.get(id);

							if (null == valueCaptionMap) {
								valueCaptionMap = new HashMap<>();
								wrapper.fieldValueCaptionMap.put(id, valueCaptionMap);
							}
							valueCaptionMap.put(valueId, value);
						}
					} else {
						wrapper.fieldValueMap.put(id, value);
					}
				}
			});
			}
		return wrapper;
	}

	@SuppressWarnings("serial")
	/**
	 * Helper class used to hold field names, captions and values associations
	 *
	 */
	public static final class FieldWrapper implements Serializable {
		private Map<String, String> fieldCaptionMap = new HashMap<>();
		private Map<String, String> fieldValueMap = new HashMap<>();
		private Map<String, Map<String, String>> fieldValueCaptionMap = new HashMap<>();

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
