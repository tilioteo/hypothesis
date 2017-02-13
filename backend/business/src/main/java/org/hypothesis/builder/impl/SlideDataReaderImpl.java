/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.builder.SlideDataReader;
import org.hypothesis.common.Interval;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideDataReaderImpl implements SlideDataReader {

	@Override
	public List<String> getOutputValues(String text, DocumentReader reader) {
		if (Objects.nonNull(text) && Objects.nonNull(reader)) {
			Document doc = reader.readString(text);

			if (Objects.nonNull(doc)) {
				List<Element> elements = doc.root().selectElements(DocumentConstants.OUTPUT_VALUE);
				// .selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.OUTPUT_VALUE));

				List<String> list = Arrays.asList(new String[10]);
				final Interval<Integer> interval = new Interval<>(1, list.size());

				elements.forEach(e -> {
					Integer index = e.getAttributeAsInteger(DocumentConstants.INDEX);
					if (interval.contains(index)) {
						list.set(index - 1, e.getText());
					}
				});

				return list;
			}
		}

		return Collections.emptyList();
	}

	@Override
	public FieldWrapper getFields(String text, DocumentReader reader) {
		FieldWrapperImpl wrapper = new FieldWrapperImpl();

		if (Objects.nonNull(text) && Objects.nonNull(reader)) {
			Document doc = reader.readString(text);

			if (Objects.nonNull(doc)) {
				List<Element> elements = doc.root().selectElements(DocumentConstants.FIELD);
				// .selectNodes(String.format(XmlUtility.DESCENDANT_FMT, DocumentConstants.FIELD));
				elements.forEach(e -> {
					final String id = e.getAttribute(DocumentConstants.ID);
					if (StringUtils.isNotBlank(id)) {
						wrapper.fieldCaptionMap.put(id, Optional.ofNullable(e.selectElement(DocumentConstants.CAPTION))
								.map(Element::getTextTrim).orElse(null));
					}

					Optional.ofNullable(e.selectElement(DocumentConstants.VALUE)).ifPresent(i -> {
						String valueId = i.getAttribute(DocumentConstants.ID);
						String value = i.getText();
						if (StringUtils.isNotBlank(valueId)) {
							wrapper.fieldValueMap.put(id, valueId);
							if (!valueId.equals(value) && StringUtils.isNotBlank(value)) {
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
					});
				});
			}
		}
		return wrapper;
	}

	/**
	 * Helper class used to hold field names, captions and values associations
	 *
	 */
	private static final class FieldWrapperImpl implements FieldWrapper {
		private Map<String, String> fieldCaptionMap = new HashMap<>();
		private Map<String, String> fieldValueMap = new HashMap<>();
		private Map<String, Map<String, String>> fieldValueCaptionMap = new HashMap<>();

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
