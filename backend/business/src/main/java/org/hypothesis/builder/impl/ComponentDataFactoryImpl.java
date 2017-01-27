/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.log4j.Logger;
import org.hypothesis.builder.ComponentDataFactory;
import org.hypothesis.builder.DocumentFactory;
import org.hypothesis.business.ObjectConstants;
import org.hypothesis.data.DocumentWriter;
import org.hypothesis.event.annotations.ElementPath;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.SlidePresenter;
import org.hypothesis.interfaces.Variable;
import org.hypothesis.slide.ui.ComboBox;
import org.hypothesis.slide.ui.DateField;
import org.hypothesis.slide.ui.SelectPanel;
import org.hypothesis.slide.ui.annotations.FieldType;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class ComponentDataFactoryImpl implements ComponentDataFactory {

	private static Logger log = Logger.getLogger(ComponentDataFactoryImpl.class);

	@Inject
	private DocumentFactory documentFactory;

	@Override
	public String buildComponentData(ComponentData data, DocumentWriter writer) {
		if (Objects.nonNull(data) && Objects.nonNull(writer)) {
			Document document = documentFactory.createEventDataDocument();

			addEventHeader(document.root(), data);
			addEventData(document.root(), data);

			return writer.writeString(document);
		}

		return null;
	}

	private void addEventHeader(Element root, ComponentData data) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, data.getTypeName());

		if (StringUtils.isNotEmpty(data.getId())) {
			element.setAttribute(DocumentConstants.ID, data.getId());
		}

		if (StringUtils.isNotEmpty(data.getEventName())) {
			element.setAttribute(DocumentConstants.NAME, data.getEventName());
		}

		if (StringUtils.isNotEmpty(data.getSender().getCaption())) {
			element.setText(data.getSender().getCaption());
		}
	}

	private void addEventData(Element root, ComponentData data) {
		// get fields for this class only
		List<Field> fields = Arrays.asList(data.getClass().getDeclaredFields());
		fields.stream().filter(f -> !Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(ElementPath.class)
				&& StringUtils.isNotEmpty(f.getAnnotation(ElementPath.class).value())).forEach(e -> {
					e.setAccessible(true);
					try {
						if (e.get(data) != null) {
							String value = e.get(data).toString();
							/*
							 * if
							 * (Double.class.isAssignableFrom(field.getType())
							 * || Float.class.isAssignableFrom(field.getType()))
							 * {
							 * 
							 * try { Double doubleValue = Double.valueOf(value);
							 * value = String.format(Locale.ROOT, "%g",
							 * doubleValue); } catch(NumberFormatException e) {}
							 * }
							 */
							String name = e.getAnnotation(ElementPath.class).value();

							Element baseElement = ensureSubElement(root, DocumentConstants.SOURCE);

							// FIXME use regexp Pattern and Matcher
							String[] elementNames = name.split("/");
							boolean isAttribute = false;
							String attributeName = null;

							for (int i = 0; i < elementNames.length; ++i) {
								String elementName = elementNames[i];

								if (elementName.startsWith("@")) {
									isAttribute = true;

									attributeName = formatXmlName(elementName.substring(1));
									break;
								} else {
									String[] parts = elementName.split("@");
									if (parts.length > 1) {
										isAttribute = true;

										elementName = formatXmlName(parts[0]);
										attributeName = formatXmlName(parts[1]);

										baseElement = ensureSubElement(baseElement, elementName);
										break;
									} else {
										elementName = formatXmlName(elementName);

										baseElement = ensureSubElement(baseElement, elementName);
									}
								}
							}

							if (!isAttribute) {
								baseElement.setText(value);
							} else if (StringUtils.isNotEmpty(attributeName)) {
								baseElement.setAttribute(attributeName, value);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
	}

	private String formatXmlName(String name) {
		return ISO9075.encode(name.substring(0, 1).toUpperCase() + name.substring(1));
	}

	private Element ensureSubElement(Element baseElement, String name) {
		Element element = baseElement.selectElement(name);
		if (null == element) {
			element = baseElement.createChild(name);
		}

		return element;
	}

	@Override
	public String buildActionData(ActionEvent event, DocumentWriter writer) {
		if (Objects.nonNull(event) && Objects.nonNull(writer)) {
			Document document = documentFactory.createEventDataDocument();

			addActionEventHeader(document.root(), event);
			addActionOutputs(document.root(), event);

			return writer.writeString(document);
		}

		return null;
	}

	private void addActionEventHeader(Element root, ActionEvent event) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, DocumentConstants.ACTION);

		if (event.getAction() != null && event.getAction().getId() != null) {
			element.setAttribute(DocumentConstants.ID, event.getAction().getId());
		}
	}

	// FIXME copy-paste code of addOutputs
	private void addActionOutputs(Element root, ActionEvent event) {
		Map<Integer, ExchangeVariable> outputs = event.getAction().getOutputs();

		if (!outputs.isEmpty()) {
			Element element = root.createChild(DocumentConstants.OUTPUT_VALUES);
			outputs.entrySet().stream().filter(f -> f.getValue().getValue() != null).forEach(e -> {
				Element outputValueElement = element.createChild(DocumentConstants.OUTPUT_VALUE);
				outputValueElement.setAttribute(DocumentConstants.INDEX, Integer.toString(e.getValue().getIndex()));
				writeOutputValue(outputValueElement, e.getValue().getValue());
			});
		}
	}

	@Override
	public String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer) {
		if (Objects.nonNull(presenter) && Objects.nonNull(writer)) {
			Document document = documentFactory.createEventDataDocument();

			addSlideContainerEventHeader(document.root(), presenter);
			addFields(document.root(), presenter);
			addVariables(document.root(), presenter);
			addOutputs(document.root(), presenter);

			return writer.writeString(document);
		}

		return null;
	}

	private void addSlideContainerEventHeader(Element root, SlidePresenter presenter) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, DocumentConstants.SLIDE);
		String id = presenter.getSlideId();

		if (StringUtils.isNotEmpty(id)) {
			element.setAttribute(DocumentConstants.ID, id);
		}

	}

	// FIXME copy-paste similar code to addVariables
	private void addFields(Element root, SlidePresenter presenter) {
		Map<String, org.hypothesis.interfaces.Field> fields = presenter.getFields();

		if (!fields.isEmpty()) {
			Element element = root.createChild(DocumentConstants.FIELDS);
			fields.values().stream().filter(f -> f instanceof AbstractComponent)
					.forEach(e -> writeFieldData(element, (AbstractComponent) e));
		}
	}

	private void writeFieldData(Element element, AbstractComponent field) {
		String fieldType = getFieldType(field);
		if (StringUtils.isNotEmpty(fieldType)) {
			Element fieldElement = element.createChild(DocumentConstants.FIELD);
			fieldElement.setAttribute(DocumentConstants.TYPE, fieldType);
			fieldElement.setAttribute(DocumentConstants.ID, (String) field.getData());

			Element captionElement = fieldElement.createChild(DocumentConstants.CAPTION);
			String caption = field.getCaption();
			if (StringUtils.isNotEmpty(caption)) {
				captionElement.setText(caption);
			}

			writeValue(fieldElement, field);
		}
	}

	private String getFieldType(AbstractComponent field) {
		FieldType fieldType = field.getClass().getAnnotation(FieldType.class);

		return fieldType != null ? fieldType.value() : null;

		/*
		 * if (field instanceof ComboBox) { return DocumentConstants.COMBOBOX; }
		 * else if (field instanceof DateField) { return
		 * DocumentConstants.DATE_FIELD; } else if (field instanceof
		 * SelectPanel) { return DocumentConstants.SELECT_PANEL; } else if
		 * (field instanceof TextArea) { return DocumentConstants.TEXT_AREA; }
		 * else if (field instanceof TextField) { return
		 * DocumentConstants.TEXT_FIELD; }
		 * 
		 * return null;
		 */
	}

	// TODO make it better way
	private void writeValue(Element element, AbstractComponent field) {
		final Element valueElement = element.createChild(DocumentConstants.VALUE);

		if (field instanceof ComboBox) {
			ComboBox comboBox = (ComboBox) field;

			if (comboBox.getValue() != null) {
				valueElement.setAttribute(DocumentConstants.ID, (String) comboBox.getValue());
				valueElement.setText(comboBox.getItemCaption(comboBox.getValue()));
			}
		} else if (field instanceof DateField) {
			DateField dateField = (DateField) field;

			if (dateField.getValue() != null) {
				Date date = dateField.getValue();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				valueElement.setText(format.format(date));
			}
		} else if (field instanceof SelectPanel) {
			SelectPanel selectPanel = (SelectPanel) field;

			selectPanel.getSelectedButtons().forEach(e -> {
				Element currentElement = valueElement != null ? valueElement
						: element.createChild(DocumentConstants.VALUE);
				currentElement.setAttribute(DocumentConstants.ID,
						String.format("%d", selectPanel.getChildIndex(e) + 1));
				currentElement.setText(e.getCaption());
			});
		} else if (field instanceof AbstractTextField) {
			valueElement.setText(((AbstractTextField) field).getValue());
		}
	}

	// FIXME copy-paste similar code to addFields
	private void addVariables(Element root, SlidePresenter presenter) {
		Map<String, Variable<?>> variables = presenter.getVariables();

		if (!variables.isEmpty()) {
			Element element = root.createChild(DocumentConstants.VARIABLES);
			variables.values().stream().filter(f -> {
				String name = f.getName();
				return !(name.equals(ObjectConstants.COMPONENT_DATA) || name.equals(ObjectConstants.NAVIGATOR)
						|| name.equals(ObjectConstants.DOCUMENT));
			}).forEach(e -> writeVariableData(element, e));
		}
	}

	// FIXME similar code to writeOutputValue
	private void writeVariableData(Element element, Variable<?> variable) {
		Class<?> type = variable.getType();
		String typeName = DocumentConstants.OBJECT;
		String valueString = "";

		if (type.equals(Integer.class)) {
			typeName = DocumentConstants.INTEGER;
			valueString = variable.getStringValue();

		} else if (type.equals(Double.class)) {
			typeName = DocumentConstants.FLOAT;
			valueString = variable.getStringValue();

		} else if (type.equals(Boolean.class)) {
			typeName = DocumentConstants.BOOLEAN;
			valueString = variable.getStringValue();

		} else if (type.equals(String.class)) {
			typeName = DocumentConstants.STRING;
			valueString = variable.getStringValue();

		} else if (type.equals(Object.class)) {
			Object value = variable.getValue();

			if (value != null && value.getClass() == ArrayList.class) {
				typeName = DocumentConstants.OBJECT_ARRAY;

				List<?> array = (List<?>) value;
				Object testItem = array.stream().filter(f -> f != null).findFirst().orElseGet(null);

				if (testItem != null) {
					Class<?> itemType = testItem.getClass();
					valueString = array.stream().map(String::valueOf)
							.collect(Collectors.joining(DocumentConstants.STR_COMMA));

					if (itemType.equals(Integer.class)) {
						typeName = DocumentConstants.INTEGER_ARRAY;

					} else if (itemType.equals(Double.class)) {
						typeName = DocumentConstants.FLOAT_ARRAY;

					} else if (itemType.equals(String.class)) {
						typeName = DocumentConstants.STRING_ARRAY;
					}
				}
			}
		}

		if (!typeName.isEmpty()) {
			Element variableElement = element.createChild(DocumentConstants.VARIABLE);
			variableElement.setAttribute(DocumentConstants.ID, variable.getName());
			variableElement.setAttribute(DocumentConstants.TYPE, typeName);
			variableElement.setText(valueString);
		}
	}

	// FIXME copy-paste code of addActionOutputs
	private void addOutputs(Element root, SlidePresenter presenter) {
		Map<Integer, ExchangeVariable> outputs = presenter.getOutputs();

		if (!outputs.isEmpty()) {
			Element element = root.createChild(DocumentConstants.OUTPUT_VALUES);
			outputs.entrySet().stream().filter(f -> f.getValue().getValue() != null).forEach(e -> {
				Element outputValueElement = element.createChild(DocumentConstants.OUTPUT_VALUE);
				outputValueElement.setAttribute(DocumentConstants.INDEX, Integer.toString(e.getValue().getIndex()));
				writeOutputValue(outputValueElement, e.getValue().getValue());
			});
		}
	}

	// FIXME similar code to writeVariableData
	private void writeOutputValue(Element element, Object value) {
		Class<?> type;
		if (value instanceof com.tilioteo.expressions.Variable) {
			com.tilioteo.expressions.Variable variable = (com.tilioteo.expressions.Variable) value;
			type = variable.getType();
			value = variable.getValue();
		} else {
			type = value.getClass();
		}

		if (type == double.class || type == float.class || type.isAssignableFrom(Double.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.FLOAT);
			// use Locale.ROOT for locale neutral formating of decimals
			element.setText(String.format(Locale.ROOT, "%g", ((Double) value).doubleValue()));
		} else if (type == byte.class || type == int.class || type == short.class
				|| type.isAssignableFrom(Integer.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER);
			element.setText(((Integer) value).toString());
		} else if (type == long.class || type.isAssignableFrom(Long.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER);
			element.setText(((Long) value).toString());
		} else if (type == boolean.class || type.isAssignableFrom(Boolean.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.BOOLEAN);
			element.setText(((Boolean) value).toString());
		} else if (type.isAssignableFrom(String.class) || value instanceof String) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.STRING);
			element.setText((String) value);
		} else if (type == ArrayList.class) {
			List<?> array = (List<?>) value;
			Object testItem = array.stream().filter(f -> f != null).findFirst().orElseGet(null);

			if (testItem != null) {
				Class<?> itemType = testItem.getClass();

				element.setText(
						array.stream().map(String::valueOf).collect(Collectors.joining(DocumentConstants.STR_COMMA)));

				if (itemType.equals(Integer.class)) {
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER_ARRAY);

				} else if (itemType.equals(Double.class)) {
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.FLOAT_ARRAY);

				} else if (itemType.equals(String.class)) {
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.STRING_ARRAY);

				} else {
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.OBJECT_ARRAY);
				}
			} else {
				element.setAttribute(DocumentConstants.TYPE, DocumentConstants.OBJECT_ARRAY);
			}
		} else {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.OBJECT);
		}
	}

}
