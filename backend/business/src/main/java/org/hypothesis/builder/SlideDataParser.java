/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Deprecated
public class SlideDataParser {

	public static List<String> parseOutputValues(String xmlString) {
		List<String> list = new ArrayList<>();
		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			List<Element> elements = doc.getRootElement()
					.selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.OUTPUT_VALUE)).stream()//
					.map(Element.class::cast)//
					.collect(toList());

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
					if (!value.isEmpty() && i >= 1 && i <= list.size()) {
						list.set(i - 1, value);
					}
				} catch (NumberFormatException e) {
				}
			}
		}
		return list;
	}

	public static List<String> parseScores(String xmlString) {
		List<String> list = new ArrayList<>();
		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			List<Element> elements = doc.getRootElement()
					.selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.SCORE)).stream()//
					.map(Element.class::cast)//
					.collect(toList());

			for (Element element : elements) {
				String index = element.attributeValue(DocumentConstants.INDEX);

				try {
					int i = Integer.parseInt(index);
					String value = element.getTextTrim();
					if (!value.isEmpty() && i >= 1) {
						if (i >= list.size()) {
							for (int j = list.size(); j < i; ++j) {
								list.add(null);
							}
						}
						list.set(i - 1, value);
					}
				} catch (NumberFormatException e) {
				}
			}
		}
		return list;
	}

	public static FieldWrapper parseFields(String xmlString) {
		FieldWrapper wrapper = new FieldWrapper();

		Document doc = XmlUtility.readString(xmlString);
		if (doc != null) {
			List<Element> elements = doc.getRootElement()
					.selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.FIELD)).stream()//
					.map(Element.class::cast)//
					.collect(toList());
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
								valueCaptionMap = new HashMap<>();
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
		private final HashMap<String, String> fieldCaptionMap = new HashMap<>();
		private final HashMap<String, String> fieldValueMap = new HashMap<>();
		private final HashMap<String, Map<String, String>> fieldValueCaptionMap = new HashMap<>();

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
