/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.data.DateRangeValidator;
import com.tilioteo.hypothesis.data.EmptyValidator;
import com.tilioteo.hypothesis.data.IntegerValidator;
import com.tilioteo.hypothesis.data.NumberRangeValidator;
import com.tilioteo.hypothesis.data.NumberValidator;
import com.tilioteo.hypothesis.data.SelectPanelEmptyValidator;
import com.tilioteo.hypothesis.data.Validator;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.extension.SlideComponentPlugin;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ComponentFactory {

	public static LayoutComponent createComponentFromElement(Element element,
			SlideManager slideManager) {
		if (element != null) {
			String name = element.getName();
			SlideComponent component = null;

			if (name.equals(SlideXmlConstants.VERTICAL_LAYOUT))
				component = ComponentFactory
						.<VerticalLayout> createFromElement(
								VerticalLayout.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.HORIZONTAL_LAYOUT))
				component = ComponentFactory
						.<HorizontalLayout> createFromElement(
								HorizontalLayout.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.FORM_LAYOUT))
				component = ComponentFactory.<FormLayout> createFromElement(
						FormLayout.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.PANEL))
				component = ComponentFactory.<Panel> createFromElement(
						Panel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.IMAGE))
				component = ComponentFactory.<Image> createFromElement(
						Image.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.BUTTON))
				component = ComponentFactory.<Button> createFromElement(
						Button.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.BUTTON_PANEL))
				component = ComponentFactory.<ButtonPanel> createFromElement(
						ButtonPanel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.SELECT_PANEL))
				component = ComponentFactory.<SelectPanel> createFromElement(
						SelectPanel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TEXT_FIELD))
				component = ComponentFactory.<TextField> createFromElement(
						TextField.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TEXT_AREA))
				component = ComponentFactory.<TextArea> createFromElement(
						TextArea.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.DATE_FIELD))
				component = ComponentFactory.<DateField> createFromElement(
						DateField.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.COMBOBOX))
				component = ComponentFactory.<ComboBox> createFromElement(
						ComboBox.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TIMER_LABEL))
				component = ComponentFactory.<TimerLabel> createFromElement(
						TimerLabel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.LABEL))
				component = ComponentFactory.<Label> createFromElement(
						Label.class, element, slideManager);
			else
				component = createPluginComponent(element, slideManager);

			String id = SlideXmlUtility.getId(element);
			slideManager.registerComponent(id, component);

			return new LayoutComponent(component, component.getAlignment());
		}
		return null;
	}

	public static <T extends AbstractComponent & SlideComponent> T createFromElement(
			Class<T> clazz, Element element, SlideManager slideManager) {

		T component;
		try {
			component = clazz.newInstance();
			component.setSlideManager(slideManager);
			component.loadFromXml(element);
			return component;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static SlideComponent createPluginComponent(Element element,
			SlideManager slideManager) {
		
		String namespace = element.getNamespacePrefix();
		if (namespace != null && !"".equals(namespace.trim())) {
			// find registered plugin
			SlideComponentPlugin componentPlugin = PluginManager.get().getComponentPlugin(namespace, element.getName());
			
			if (componentPlugin != null) {
				return componentPlugin.createComponentFromElement(element, slideManager);
			}
		}

		return null;
	}

	private static void createViewportHandlers(Element rootElement,
			SlideManager slideManager) {
		List<Element> elements = SlideUtility.getHandlerElements(rootElement);
		for (Element handler : elements) {
			setViewportHandler(handler, slideManager);
		}
	}

	public static void createViewportComponent(Element rootElement,
			SlideManager slideManager) {
		Element componentElement = SlideXmlUtility.getViewportInnerComponent(rootElement);
		LayoutComponent component = createComponentFromElement(componentElement, slideManager);

		createViewportHandlers(rootElement, slideManager);

		slideManager.setViewport(component);
	}

	public static void createWindows(Element rootElement,
			SlideManager slideManager) {
		List<Element> elements = SlideXmlUtility.getWindowsElements(rootElement);
		for (Element windowElement : elements) {
			String id = SlideXmlUtility.getId(windowElement);
			if (!Strings.isNullOrEmpty(id)) {
				Element element = SlideXmlUtility.getViewportOrWindowRootElement(windowElement);

				Window window = new Window(slideManager);
				window.loadFromXml(element);

				slideManager.registerWindow(id, window);
			}
		}
	}

	public static void createTimers(Element rootElement,
			SlideManager slideManager) {
		List<Element> elements = SlideXmlUtility.getTimersElements(rootElement);
		for (Element element : elements) {
			String id = SlideXmlUtility.getId(element);
			if (!Strings.isNullOrEmpty(id)) {

				Timer timer = new Timer(slideManager);
				timer.loadFromXml(element);

				slideManager.registerTimer(id, timer);
			}
		}
	}

	private static void setViewportHandler(Element element,
			SlideManager slideManager) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
				.createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.INIT)) {
				setViewportInitHandler(action, slideManager);
			} else if (name.equals(SlideXmlConstants.SHOW)) {
				setViewportShowHandler(action, slideManager);
			}

			// TODO add other event handlers
		}
	}

	private static void setViewportInitHandler(String actionId,
			SlideManager slideManager) {
		final Command action = CommandFactory.createActionCommand(slideManager, actionId, null);
		slideManager.addViewportEventListener(SlideManager.InitEvent.class,
				new ViewportEventListener() {
					@Override
					public void handleEvent(ViewportEvent event) {
						action.execute();
					}
				});
	}

	private static void setViewportShowHandler(String actionId,
			SlideManager slideManager) {
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId, null);
		slideManager.addViewportEventListener(SlideManager.ShowEvent.class,
				new ViewportEventListener() {
					@Override
					public void handleEvent(ViewportEvent event) {
						action.execute();
					}
				});
	}
	
	public static EmptyValidator createEmptyValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		return new EmptyValidator(message);
	}
	
	public static IntegerValidator createIntegerValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		return new IntegerValidator(message);
	}
	
	public static NumberValidator createNumberValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		return new NumberValidator(message);
	}
	
	public static NumberRangeValidator createNumberRangeValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		Double minValue = SlideXmlUtility.getNumberValidatorMinValue(element);
		Double maxValue = SlideXmlUtility.getNumberValidatorMaxValue(element);
		
		if (minValue != null || maxValue != null) {
			return new NumberRangeValidator(message, minValue, maxValue);
		}
		return null;
	}

	public static DateRangeValidator createDateRangeValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		Date minValue = SlideXmlUtility.getDateValidatorMinValue(element, "yyyy-MM-dd");
		Date maxValue = SlideXmlUtility.getDateValidatorMaxValue(element, "yyyy-MM-dd");
		
		if (minValue != null || maxValue != null) {
			return new DateRangeValidator(message, minValue, maxValue);
		}
		return null;
	}
	
	public static List<Validator> createTextFieldValidators(Element element) {
		List<Element> validatorElements = SlideUtility.getValidatorElements(element);
		List<Validator> validators = new ArrayList<Validator>();
		
		for (Element validatorElement : validatorElements) {
			Validator validator = null;
			String name = validatorElement.getName();
			
			if (name.equals(SlideXmlConstants.EMPTY)) {
				validator = createEmptyValidator(validatorElement);
			} else if (name.equals(SlideXmlConstants.INTEGER)) {
				validator = createIntegerValidator(validatorElement);
			} else if (name.equals(SlideXmlConstants.NUMBER)) {
				validator = createNumberValidator(validatorElement);
			} else if (name.equals(SlideXmlConstants.RANGE)) {
				validator = createNumberRangeValidator(validatorElement);
			}
			
			if (validator != null) {
				validators.add(validator);
			}
		}
		
		return validators;
	}

	public static List<Validator> createComboBoxValidators(Element element) {
		List<Element> validatorElements = SlideUtility.getValidatorElements(element);
		List<Validator> validators = new ArrayList<Validator>();
		
		for (Element validatorElement : validatorElements) {
			Validator validator = null;
			String name = validatorElement.getName();
			
			if (name.equals(SlideXmlConstants.EMPTY)) {
				validator = createEmptyValidator(validatorElement);
			}
			
			if (validator != null) {
				validators.add(validator);
			}
		}
		
		return validators;
	}

	public static List<Validator> createDateFieldValidators(Element element) {
		List<Element> validatorElements = SlideUtility.getValidatorElements(element);
		List<Validator> validators = new ArrayList<Validator>();
		
		for (Element validatorElement : validatorElements) {
			Validator validator = null;
			String name = validatorElement.getName();
			
			if (name.equals(SlideXmlConstants.EMPTY)) {
				validator = createEmptyValidator(validatorElement);
			} else if (name.equals(SlideXmlConstants.RANGE)) {
				validator = createDateRangeValidator(validatorElement);
			}
			
			if (validator != null) {
				validators.add(validator);
			}
		}
		
		return validators;
	}

	public static List<Validator> createSelectPanelValidators(Element element) {
		List<Element> validatorElements = SlideUtility.getValidatorElements(element);
		List<Validator> validators = new ArrayList<Validator>();
		
		for (Element validatorElement : validatorElements) {
			Validator validator = null;
			String name = validatorElement.getName();
			
			if (name.equals(SlideXmlConstants.EMPTY)) {
				validator = createSelectPanelEmptyValidator(validatorElement);
			}
			
			if (validator != null) {
				validators.add(validator);
			}
		}
		
		return validators;
	}

	public static SelectPanelEmptyValidator createSelectPanelEmptyValidator(Element element) {
		// TODO add default validator message
		String message = SlideXmlUtility.getValidatorMessage(element, "");
		
		return new SelectPanelEmptyValidator(message);
	}

}
