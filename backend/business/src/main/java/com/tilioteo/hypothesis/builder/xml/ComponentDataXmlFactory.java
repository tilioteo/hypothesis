/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.jackrabbit.util.ISO9075;
import org.dom4j.Document;
import org.dom4j.Element;
import org.vaadin.special.ui.SelectButton;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.builder.ComponentDataFactory;
import com.tilioteo.hypothesis.business.ObjectConstants;
import com.tilioteo.hypothesis.business.Structured;
import com.tilioteo.hypothesis.evaluation.Action;
import com.tilioteo.hypothesis.event.data.ComponentData;
import com.tilioteo.hypothesis.event.model.ActionEvent;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Variable;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.tilioteo.hypothesis.slide.ui.ComboBox;
import com.tilioteo.hypothesis.slide.ui.DateField;
import com.tilioteo.hypothesis.slide.ui.SelectPanel;
import com.tilioteo.hypothesis.slide.ui.TextArea;
import com.tilioteo.hypothesis.slide.ui.TextField;
import com.tilioteo.hypothesis.utility.XmlUtility;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ComponentDataXmlFactory implements ComponentDataFactory {

	@Override
	public String buildComponentData(ComponentData data) {
		Document document = createEventDataXml();

		addEventHeader(document.getRootElement(), data);
		addEventData(document.getRootElement(), data);

		return XmlUtility.writeString(document);
	}

	private Document createEventDataXml() {
		Document eventData = XmlUtility.createDocument();
		eventData.addElement(BuilderConstants.EVENT_DATA);
		
		return eventData;
	}

	private void addEventHeader(Element root, ComponentData data) {
		Element element = root.addElement(BuilderConstants.SOURCE);
		element.addAttribute(BuilderConstants.TYPE, data.getTypeName());

		if (!Strings.isNullOrEmpty(data.getId())) {
			element.addAttribute(BuilderConstants.ID, data.getId());
		}

		if (!Strings.isNullOrEmpty(data.getSender().getCaption())) {
			element.addText(data.getSender().getCaption());
		}
	}

	private void addEventData(Element root, ComponentData data) {
		// get fields for this class only
		Field[] fields = data.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				Structured structured = field.getAnnotation(Structured.class);
				if (structured != null && "".equals(structured.value())) {
					continue;
				}

				field.setAccessible(true);
				try {
					if (field.get(data) != null) {
						String value = field.get(data).toString();
						if (Double.class.isAssignableFrom(field.getType())
								|| Float.class.isAssignableFrom(field.getType())) {
							value = String.format(Locale.ROOT, "%g", field.getDouble(data));
						}

						String name;
						if (structured != null) {
							name = structured.value();
						} else {
							name = field.getName();
						}

						Element baseElement = ensureSubElement(root, BuilderConstants.SOURCE);

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
						} else if (!Strings.isNullOrEmpty(attributeName)) {
							baseElement.addAttribute(attributeName, value);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String formatXmlName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return ISO9075.encode(name);
	}

	private Element ensureSubElement(Element baseElement, String name) {
		Element element = (Element) baseElement.selectSingleNode(name);
		if (null == element) {
			element = baseElement.addElement(name);
		}
		
		return element;
	}

	@Override
	public String buildActionData(ActionEvent event) {
		Document document = createEventDataXml();

		addActionEventHeader(document.getRootElement(), event);
		addActionOutputs(document.getRootElement(), event);

		return XmlUtility.writeString(document);
	}

	private void addActionEventHeader(Element root, ActionEvent event) {
		Element element = root.addElement(BuilderConstants.SOURCE);
		element.addAttribute(BuilderConstants.TYPE, BuilderConstants.ACTION);

		if (event.getAction() != null && event.getAction().getId() != null) {
			element.addAttribute(BuilderConstants.ID, event.getAction().getId());
		}
	}

	private void addActionOutputs(Element root, ActionEvent event) {
		if (event.getAction() instanceof Action) {
			Action action = (Action) event.getAction();
			Map<Integer, ExchangeVariable> outputs = action.getOutputs();

			if (!outputs.isEmpty()) {
				Element element = root.addElement(BuilderConstants.OUTPUT_VALUES);
				for (ExchangeVariable output : outputs.values()) {
					String indexString = "" + output.getIndex();
					Object value = output.getValue();

					if (value != null) {
						Element outputValueElement = element.addElement(BuilderConstants.OUTPUT_VALUE);
						outputValueElement.addAttribute(BuilderConstants.INDEX, indexString);
						writeOutputValue(outputValueElement, value);
					}
				}
			}
		}
	}

	@Override
	public String buildSlideContainerData(SlideContainerPresenter presenter) {
		Document document = createEventDataXml();

		addSlideContainerEventHeader(document.getRootElement(), presenter);
		addFields(document.getRootElement(), presenter);
		addVariables(document.getRootElement(), presenter);
		addOutputs(document.getRootElement(), presenter);

		return XmlUtility.writeString(document);
	}

	private void addSlideContainerEventHeader(Element root, SlideContainerPresenter presenter) {
		Element element = root.addElement(BuilderConstants.SOURCE);
		element.addAttribute(BuilderConstants.TYPE, BuilderConstants.SLIDE);
		String id = null;
		if (presenter.getSlideContainer() instanceof AbstractComponent
				&& ((AbstractComponent) presenter.getSlideContainer()).getData() != null) {
			id = ((AbstractComponent) presenter.getSlideContainer()).getData().toString();
		}

		if (!Strings.isNullOrEmpty(id)) {
			element.addAttribute(BuilderConstants.ID, id);
		}

	}

	private void addFields(Element root, SlideContainerPresenter presenter) {
		Map<String, com.tilioteo.hypothesis.interfaces.Field> fields = presenter.getFields();

		if (!fields.isEmpty()) {
			Element element = root.addElement(BuilderConstants.FIELDS);

			for (com.tilioteo.hypothesis.interfaces.Field field : fields.values()) {
				if (field instanceof AbstractComponent) {
					writeFieldData(element, (AbstractComponent) field);
				}
			}
		}
	}

	private void writeFieldData(Element element, AbstractComponent field) {
		String fieldType = getFieldType(field);
		if (!Strings.isNullOrEmpty(fieldType)) {
			Element fieldElement = element.addElement(BuilderConstants.FIELD);
			fieldElement.addAttribute(BuilderConstants.TYPE, fieldType);
			fieldElement.addAttribute(BuilderConstants.ID, (String) field.getData());

			Element captionElement = fieldElement.addElement(BuilderConstants.CAPTION);
			String caption = field.getCaption();
			if (!Strings.isNullOrEmpty(caption)) {
				captionElement.addText(caption);
			}

			writeValue(fieldElement, field);
		}
	}

	// TODO make it better way, maybe annotation
	private String getFieldType(AbstractComponent field) {
		if (field instanceof ComboBox) {
			return BuilderConstants.COMBOBOX;
		} else if (field instanceof DateField) {
			return BuilderConstants.DATE_FIELD;
		} else if (field instanceof SelectPanel) {
			return BuilderConstants.SELECT_PANEL;
		} else if (field instanceof TextArea) {
			return BuilderConstants.TEXT_AREA;
		} else if (field instanceof TextField) {
			return BuilderConstants.TEXT_FIELD;
		}

		return null;
	}

	// TODO make it better way
	private void writeValue(Element element, AbstractComponent field) {
		Element valueElement = element.addElement(BuilderConstants.VALUE);

		if (field instanceof ComboBox) {
			ComboBox comboBox = (ComboBox) field;

			if (comboBox.getValue() != null) {
				valueElement.addAttribute(BuilderConstants.ID, (String) comboBox.getValue());
				valueElement.addText(comboBox.getItemCaption(comboBox.getValue()));
			}
		} else if (field instanceof DateField) {
			DateField dateField = (DateField) field;

			if (dateField.getValue() != null) {
				Date date = dateField.getValue();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				valueElement.addText(format.format(date));
			}
		} else if (field instanceof SelectPanel) {
			SelectPanel selectPanel = (SelectPanel) field;

			Collection<SelectButton> selectedButtons = selectPanel.getSelectedButtons();
			if (!selectedButtons.isEmpty()) {
				for (SelectButton selected : selectedButtons) {
					if (null == valueElement) {
						valueElement = element.addElement(BuilderConstants.VALUE);
					}
					valueElement.addAttribute(BuilderConstants.ID,
							String.format("%d", selectPanel.getChildIndex(selected) + 1));
					valueElement.addText(selected.getCaption());
					valueElement = null;
				}
			}
		} else if (field instanceof AbstractTextField) {
			valueElement.addText(((AbstractTextField) field).getValue());
		}
	}

	private void addVariables(Element root, SlideContainerPresenter presenter) {
		Map<String, Variable<?>> variables = presenter.getVariables();

		if (!variables.isEmpty()) {
			Element element = root.addElement(BuilderConstants.VARIABLES);

			for (Variable<?> variable : variables.values()) {
				String name = variable.getName();
				if (!(name.equals(ObjectConstants.COMPONENT_DATA) || name.equals(ObjectConstants.NAVIGATOR)
						|| name.equals(ObjectConstants.DOCUMENT))) {
					writeVariableData(element, variable);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void writeVariableData(Element element, Variable<?> variable) {
		Class<?> type = variable.getType();
		String typeName = BuilderConstants.OBJECT;
		String valueString = "";

		if (type.equals(Integer.class)) {
			typeName = BuilderConstants.INTEGER;
			valueString = variable.getStringValue();

		} else if (type.equals(Double.class)) {
			typeName = BuilderConstants.FLOAT;
			valueString = variable.getStringValue();

		} else if (type.equals(Boolean.class)) {
			typeName = BuilderConstants.BOOLEAN;
			valueString = variable.getStringValue();

		} else if (type.equals(String.class)) {
			typeName = BuilderConstants.STRING;
			valueString = variable.getStringValue();

		} else if (type.equals(Object.class)) {
			Object value = variable.getValue();

			if (value != null && value.getClass() == ArrayList.class) {
				typeName = BuilderConstants.OBJECT_ARRAY;

				ArrayList<?> array = (ArrayList<?>) value;
				if (array.size() > 0) {
					Object testItem = null;
					for (int i = 0; i < array.size(); ++i) {
						testItem = array.get(i);
						if (testItem != null) {
							break;
						}
					}

					if (testItem != null) {
						Class<?> itemType = testItem.getClass();

						if (itemType.equals(Integer.class)) {
							for (Integer item : (ArrayList<Integer>) value) {
								if (valueString.length() > 0) {
									valueString += BuilderConstants.STR_COMMA;
								}
								valueString += item.toString();
							}
							typeName = BuilderConstants.INTEGER_ARRAY;

						} else if (itemType.equals(Double.class)) {
							for (Double item : (ArrayList<Double>) value) {
								if (valueString.length() > 0) {
									valueString += BuilderConstants.STR_COMMA;
								}
								valueString += item.toString();
							}
							typeName = BuilderConstants.FLOAT_ARRAY;

						} else if (itemType.equals(String.class)) {
							for (String item : (ArrayList<String>) value) {
								if (valueString.length() > 0) {
									valueString += BuilderConstants.STR_COMMA;
								}
								valueString += item;
							}
							typeName = BuilderConstants.STRING_ARRAY;
						}
					}
				}
			}
		}

		if (!typeName.isEmpty()) {
			Element variableElement = element.addElement(BuilderConstants.VARIABLE);
			variableElement.addAttribute(BuilderConstants.ID, variable.getName());
			variableElement.addAttribute(BuilderConstants.TYPE, typeName);
			variableElement.addText(valueString);
		}
	}

	private void addOutputs(Element root, SlideContainerPresenter presenter) {
		Map<Integer, ExchangeVariable> outputs = presenter.getOutputs();

		if (!outputs.isEmpty()) {
			Element element = root.addElement(BuilderConstants.OUTPUT_VALUES);

			for (ExchangeVariable output : outputs.values()) {
				String indexString = "" + output.getIndex();
				Object value = output.getValue();

				if (value != null) {
					Element outputElement = element.addElement(BuilderConstants.OUTPUT_VALUE);
					outputElement.addAttribute(BuilderConstants.INDEX, indexString);
					writeOutputValue(outputElement, value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
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
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.FLOAT);
			// use Locale.ROOT for locale neutral formating of decimals
			element.addText(String.format(Locale.ROOT, "%g", ((Double) value).doubleValue()));
		} else if (type == byte.class || type == int.class || type == short.class
				|| type.isAssignableFrom(Integer.class)) {
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.INTEGER);
			element.addText(((Integer) value).toString());
		} else if (type == long.class || type.isAssignableFrom(Long.class)) {
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.INTEGER);
			element.addText(((Long) value).toString());
		} else if (type == boolean.class || type.isAssignableFrom(Boolean.class)) {
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.BOOLEAN);
			element.addText(((Boolean) value).toString());
		} else if (type.isAssignableFrom(String.class) || value instanceof String) {
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.STRING);
			element.addText((String) value);
		} else if (type == ArrayList.class) {
			ArrayList<?> array = (ArrayList<?>) value;
			if (array.size() > 0) {
				Class<?> itemType = array.get(0).getClass();
				String str = "";

				if (itemType.equals(Integer.class)) {
					for (Integer item : (ArrayList<Integer>) value) {
						if (str.length() > 0) {
							str += BuilderConstants.STR_COMMA;
						}
						str += item.toString();
					}
					element.addAttribute(BuilderConstants.TYPE, BuilderConstants.INTEGER_ARRAY);
					element.addText(str);

				} else if (itemType.equals(Double.class)) {
					for (Double item : (ArrayList<Double>) value) {
						if (str.length() > 0) {
							str += BuilderConstants.STR_COMMA;
						}
						str += item.toString();
					}
					element.addAttribute(BuilderConstants.TYPE, BuilderConstants.FLOAT_ARRAY);
					element.addText(str);

				} else if (itemType.equals(String.class)) {
					for (String item : (ArrayList<String>) value) {
						if (str.length() > 0) {
							str += BuilderConstants.STR_COMMA;
						}
						str += item;
					}
					element.addAttribute(BuilderConstants.TYPE, BuilderConstants.STRING_ARRAY);
					element.addText(str);

				} else {
					element.addAttribute(BuilderConstants.TYPE, BuilderConstants.OBJECT_ARRAY);
				}
			} else {
				element.addAttribute(BuilderConstants.TYPE, BuilderConstants.OBJECT_ARRAY);
			}
		} else {
			element.addAttribute(BuilderConstants.TYPE, BuilderConstants.OBJECT);
		}
	}

}
