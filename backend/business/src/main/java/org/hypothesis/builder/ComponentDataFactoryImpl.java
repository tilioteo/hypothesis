/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.jackrabbit.util.ISO9075;
import org.hypothesis.business.ObjectConstants;
import org.hypothesis.business.Structured;
import org.hypothesis.data.DocumentWriter;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.ScoreData;
import org.hypothesis.event.data.ScoreData.Source;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.SlidePresenter;
import org.hypothesis.interfaces.Variable;
import org.hypothesis.slide.ui.ComboBox;
import org.hypothesis.slide.ui.DateField;
import org.hypothesis.slide.ui.SelectPanel;
import org.hypothesis.slide.ui.TextArea;
import org.hypothesis.slide.ui.TextField;
import org.vaadin.special.ui.SelectButton;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ComponentDataFactoryImpl implements ComponentDataFactory {

	@Override
	public String buildComponentData(ComponentData data, DocumentWriter writer) {
		Document document = DocumentFactory.createEventDataDocument();

		addEventHeader(document.root(), data);
		addEventData(document.root(), data);

		return writer.writeString(document);
	}

	private void addEventHeader(Element root, ComponentData data) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, data.getTypeName());

		if (isNotEmpty(data.getId())) {
			element.setAttribute(DocumentConstants.ID, data.getId());
		}

		if (isNotEmpty(data.getSender().getCaption())) {
			element.setText(data.getSender().getCaption());
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
						/*
						 * if (Double.class.isAssignableFrom(field.getType()) ||
						 * Float.class.isAssignableFrom(field.getType())) {
						 * 
						 * try { Double doubleValue = Double.valueOf(value);
						 * value = String.format(Locale.ROOT, "%g",
						 * doubleValue); } catch(NumberFormatException e) {} }
						 */

						String name;
						if (structured != null) {
							name = structured.value();
						} else {
							name = field.getName();
						}

						Element baseElement = ensureSubElement(root, DocumentConstants.SOURCE);

						String[] elementNames = name.split("/");
						boolean isAttribute = false;
						String attributeName = null;

						for (String elementName : elementNames) {
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
						} else if (isNotEmpty(attributeName)) {
							baseElement.setAttribute(attributeName, value);
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
		Element element = baseElement.selectElement(name);
		if (null == element) {
			element = baseElement.createChild(name);
		}

		return element;
	}

	@Override
	public String buildActionData(ActionEvent event, DocumentWriter writer) {
		Document document = DocumentFactory.createEventDataDocument();

		addActionEventHeader(document.root(), event);
		addActionOutputs(document.root(), event);

		return writer.writeString(document);
	}

	private void addActionEventHeader(Element root, ActionEvent event) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, DocumentConstants.ACTION);

		if (event.getAction() != null && event.getAction().getId() != null) {
			element.setAttribute(DocumentConstants.ID, event.getAction().getId());
		}
	}

	private void addActionOutputs(Element root, ActionEvent event) {
		if (event.getAction() instanceof Action) {
			Action action = (Action) event.getAction();
			Map<Integer, ExchangeVariable> outputs = action.getOutputs();

			if (!outputs.isEmpty()) {
				Element element = root.createChild(DocumentConstants.OUTPUT_VALUES);
				for (ExchangeVariable output : outputs.values()) {
					String indexString = "" + output.getIndex();
					Object value = output.getValue();

					if (value != null) {
						Element outputValueElement = element.createChild(DocumentConstants.OUTPUT_VALUE);
						outputValueElement.setAttribute(DocumentConstants.INDEX, indexString);
						writeOutputValue(outputValueElement, value);
					}
				}
			}
		}
	}

	@Override
	public String buildSlideContainerData(SlidePresenter presenter, DocumentWriter writer) {
		Document document = DocumentFactory.createEventDataDocument();

		addSlideContainerEventHeader(document.root(), presenter);
		addFields(document.root(), presenter);
		addVariables(document.root(), presenter);
		addOutputs(document.root(), presenter);
		addScores(document.root(), presenter);

		return writer.writeString(document);
	}

	private void addSlideContainerEventHeader(Element root, SlidePresenter presenter) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE, DocumentConstants.SLIDE);
		String id = presenter.getSlideId();

		if (isNotEmpty(id)) {
			element.setAttribute(DocumentConstants.ID, id);
		}

	}

	private void addFields(Element root, SlidePresenter presenter) {
		Map<String, org.hypothesis.interfaces.Field> fields = presenter.getFields();

		if (!fields.isEmpty()) {
			Element element = root.createChild(DocumentConstants.FIELDS);

			for (org.hypothesis.interfaces.Field field : fields.values()) {
				if (field instanceof AbstractComponent) {
					writeFieldData(element, (AbstractComponent) field);
				}
			}
		}
	}

	private void writeFieldData(Element element, AbstractComponent field) {
		String fieldType = getFieldType(field);
		if (isNotEmpty(fieldType)) {
			Element fieldElement = element.createChild(DocumentConstants.FIELD);
			fieldElement.setAttribute(DocumentConstants.TYPE, fieldType);
			fieldElement.setAttribute(DocumentConstants.ID, (String) field.getData());

			Element captionElement = fieldElement.createChild(DocumentConstants.CAPTION);
			String caption = field.getCaption();
			if (isNotEmpty(caption)) {
				captionElement.setText(caption);
			}

			writeValue(fieldElement, field);
		}
	}

	// TODO make it better way, maybe annotation
	private String getFieldType(AbstractComponent field) {
		if (field instanceof ComboBox) {
			return DocumentConstants.COMBOBOX;
		} else if (field instanceof DateField) {
			return DocumentConstants.DATE_FIELD;
		} else if (field instanceof SelectPanel) {
			return DocumentConstants.SELECT_PANEL;
		} else if (field instanceof TextArea) {
			return DocumentConstants.TEXT_AREA;
		} else if (field instanceof TextField) {
			return DocumentConstants.TEXT_FIELD;
		}

		return null;
	}

	// TODO make it better way
	private void writeValue(Element element, AbstractComponent field) {
		Element valueElement = element.createChild(DocumentConstants.VALUE);

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

			Collection<SelectButton> selectedButtons = selectPanel.getSelectedButtons();
			if (!selectedButtons.isEmpty()) {
				for (SelectButton selected : selectedButtons) {
					if (null == valueElement) {
						valueElement = element.createChild(DocumentConstants.VALUE);
					}
					valueElement.setAttribute(DocumentConstants.ID,
							String.format("%d", selectPanel.getChildIndex(selected) + 1));
					valueElement.setText(selected.getCaption());
					valueElement = null;
				}
			}
		} else if (field instanceof AbstractTextField) {
			valueElement.setText(((AbstractTextField) field).getValue());
		}
	}

	private void addVariables(Element root, SlidePresenter presenter) {
		Map<String, Variable<?>> variables = presenter.getVariables();

		if (!variables.isEmpty()) {
			Element element = root.createChild(DocumentConstants.VARIABLES);

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
		String typeName = DocumentConstants.OBJECT;
		StringBuilder valueString = new StringBuilder();

		if (type.equals(Integer.class)) {
			typeName = DocumentConstants.INTEGER;
			valueString = new StringBuilder(variable.getStringValue());

		} else if (type.equals(Double.class)) {
			typeName = DocumentConstants.FLOAT;
			valueString = new StringBuilder(variable.getStringValue());

		} else if (type.equals(Boolean.class)) {
			typeName = DocumentConstants.BOOLEAN;
			valueString = new StringBuilder(variable.getStringValue());

		} else if (type.equals(String.class)) {
			typeName = DocumentConstants.STRING;
			valueString = new StringBuilder(variable.getStringValue());

		} else if (type.equals(Object.class)) {
			Object value = variable.getValue();

			if (value != null && value.getClass() == ArrayList.class) {
				typeName = DocumentConstants.OBJECT_ARRAY;

				ArrayList<?> array = (ArrayList<?>) value;
				if (array.size() > 0) {
					Object testItem = null;
					for (Object o : array) {
						if (o != null) {
							testItem = o;
							break;
						}
					}

					if (testItem != null) {
						Class<?> itemType = testItem.getClass();

						if (itemType.equals(Integer.class)) {
							for (Integer item : (ArrayList<Integer>) value) {
								if (valueString.length() > 0) {
									valueString.append(DocumentConstants.STR_COMMA);
								}
								valueString.append(item.toString());
							}
							typeName = DocumentConstants.INTEGER_ARRAY;

						} else if (itemType.equals(Double.class)) {
							for (Double item : (ArrayList<Double>) value) {
								if (valueString.length() > 0) {
									valueString.append(DocumentConstants.STR_COMMA);
								}
								valueString.append(item.toString());
							}
							typeName = DocumentConstants.FLOAT_ARRAY;

						} else if (itemType.equals(String.class)) {
							for (String item : (ArrayList<String>) value) {
								if (valueString.length() > 0) {
									valueString.append(DocumentConstants.STR_COMMA);
								}
								valueString.append(item);
							}
							typeName = DocumentConstants.STRING_ARRAY;
						}
					}
				}
			}
		}

		if (!typeName.isEmpty()) {
			Element variableElement = element.createChild(DocumentConstants.VARIABLE);
			variableElement.setAttribute(DocumentConstants.ID, variable.getName());
			variableElement.setAttribute(DocumentConstants.TYPE, typeName);
			variableElement.setText(valueString.toString());
		}
	}

	private void addOutputs(Element root, SlidePresenter presenter) {
		Map<Integer, ExchangeVariable> outputs = presenter.getOutputs();

		if (!outputs.isEmpty()) {
			Element element = root.createChild(DocumentConstants.OUTPUT_VALUES);

			for (ExchangeVariable output : outputs.values()) {
				String indexString = "" + output.getIndex();
				Object value = output.getValue();

				if (value != null) {
					Element outputElement = element.createChild(DocumentConstants.OUTPUT_VALUE);
					outputElement.setAttribute(DocumentConstants.INDEX, indexString);
					writeOutputValue(outputElement, value);
				}
			}
		}
	}

	private void addScores(Element root, SlidePresenter presenter) {
		Map<Integer, ExchangeVariable> scores = presenter.getScores();

		if (!scores.isEmpty()) {
			Element element = root.createChild(DocumentConstants.SCORES);

			for (ExchangeVariable score : scores.values()) {
				String indexString = "" + score.getIndex();
				Object value = score.getValue();

				if (value != null) {
					Element scoreElement = element.createChild(DocumentConstants.SCORE);
					scoreElement.setAttribute(DocumentConstants.INDEX, indexString);
					writeOutputValue(scoreElement, value);
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
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.FLOAT);
			// use Locale.ROOT for locale neutral formating of decimals
			element.setText(String.format(Locale.ROOT, "%g", (Double) value));
		} else if (type == byte.class || type == int.class || type == short.class
				|| type.isAssignableFrom(Integer.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER);
			element.setText(value.toString());
		} else if (type == long.class || type.isAssignableFrom(Long.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER);
			element.setText(value.toString());
		} else if (type == boolean.class || type.isAssignableFrom(Boolean.class)) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.BOOLEAN);
			element.setText(value.toString());
		} else if (type.isAssignableFrom(String.class) || value instanceof String) {
			element.setAttribute(DocumentConstants.TYPE, DocumentConstants.STRING);
			element.setText((String) value);
		} else if (type == ArrayList.class) {
			ArrayList<?> array = (ArrayList<?>) value;
			if (array.size() > 0) {
				Class<?> itemType = array.get(0).getClass();
				String str;

				if (itemType.equals(Integer.class)) {
					StringBuilder strBuilder = new StringBuilder();
					for (Integer item : (ArrayList<Integer>) value) {
						if (strBuilder.length() > 0) {
							strBuilder.append(DocumentConstants.STR_COMMA);
						}
						strBuilder.append(item.toString());
					}
					str = strBuilder.toString();
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.INTEGER_ARRAY);
					element.setText(str);

				} else if (itemType.equals(Double.class)) {
					StringBuilder strBuilder = new StringBuilder();
					for (Double item : (ArrayList<Double>) value) {
						if (strBuilder.length() > 0) {
							strBuilder.append(DocumentConstants.STR_COMMA);
						}
						strBuilder.append(item.toString());
					}
					str = strBuilder.toString();
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.FLOAT_ARRAY);
					element.setText(str);

				} else if (itemType.equals(String.class)) {
					StringBuilder strBuilder = new StringBuilder();
					for (String item : (ArrayList<String>) value) {
						if (strBuilder.length() > 0) {
							strBuilder.append(DocumentConstants.STR_COMMA);
						}
						strBuilder.append(item);
					}
					str = strBuilder.toString();
					element.setAttribute(DocumentConstants.TYPE, DocumentConstants.STRING_ARRAY);
					element.setText(str);

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

	@Override
	public String buildScoreData(ScoreData data, DocumentWriter writer) {
		Document document = DocumentFactory.createScoreDataDocument();

		addScoreHeader(document.root(), data);
		addScoreOutputs(document.root(), data);

		return writer.writeString(document);
	}

	private void addScoreHeader(Element root, ScoreData data) {
		Element element = root.createChild(DocumentConstants.SOURCE);
		element.setAttribute(DocumentConstants.TYPE,
				data.getSource() == Source.SLIDE ? DocumentConstants.SLIDE : DocumentConstants.ACTION);

		if (isNotEmpty(data.getId())) {
			element.setAttribute(DocumentConstants.ID, data.getId());
		}
	}

	private void addScoreOutputs(Element root, ScoreData data) {
		Map<Integer, ExchangeVariable> scores = data.getScores();

		if (!scores.isEmpty()) {
			Element element = root.createChild(DocumentConstants.SCORES);
			for (ExchangeVariable score : scores.values()) {
				String indexString = "" + score.getIndex();
				Object value = score.getValue();

				if (value != null) {
					Element scoreElement = element.createChild(DocumentConstants.SCORE);
					scoreElement.setAttribute(DocumentConstants.INDEX, indexString);
					writeOutputValue(scoreElement, value);
				}
			}
		}
	}

}
