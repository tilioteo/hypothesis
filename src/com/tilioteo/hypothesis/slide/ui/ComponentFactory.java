/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.data.DateRangeValidator;
import org.vaadin.special.data.EmptyValidator;
import org.vaadin.special.data.IntegerValidator;
import org.vaadin.special.data.NumberRangeValidator;
import org.vaadin.special.data.NumberValidator;
import org.vaadin.special.data.SelectPanelEmptyValidator;
import org.vaadin.special.data.Validator;
import org.vaadin.special.ui.ShortcutKey;
import org.vaadin.special.ui.ShortcutKey.KeyPressEvent;
import org.vaadin.special.ui.ShortcutKey.KeyPressListener;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.ShortcutUtility;
import com.tilioteo.hypothesis.core.ShortcutUtility.ShortcutKeys;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.MessageEvent;
import com.tilioteo.hypothesis.event.MessageEventListener;
import com.tilioteo.hypothesis.event.SlideData;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideComponentPlugin;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ComponentFactory {

	public static LayoutComponent createComponentFromElement(Element element, SlideFascia slideFascia) {
		if (element != null) {
			String name = element.getName();
			SlideComponent component = null;

			if (name.equals(SlideXmlConstants.VERTICAL_LAYOUT))
				component = ComponentFactory
						.<VerticalLayout> createFromElement(
								VerticalLayout.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.HORIZONTAL_LAYOUT))
				component = ComponentFactory
						.<HorizontalLayout> createFromElement(
								HorizontalLayout.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.FORM_LAYOUT))
				component = ComponentFactory.<FormLayout> createFromElement(
						FormLayout.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.PANEL))
				component = ComponentFactory.<Panel> createFromElement(
						Panel.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.IMAGE))
				component = ComponentFactory.<Image> createFromElement(
						Image.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.VIDEO))
				component = ComponentFactory.<Video> createFromElement(
						Video.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.AUDIO))
				component = ComponentFactory.<Audio> createFromElement(
						Audio.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.BUTTON))
				component = ComponentFactory.<Button> createFromElement(
						Button.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.BUTTON_PANEL))
				component = ComponentFactory.<ButtonPanel> createFromElement(
						ButtonPanel.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.SELECT_PANEL))
				component = ComponentFactory.<SelectPanel> createFromElement(
						SelectPanel.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.TEXT_FIELD))
				component = ComponentFactory.<TextField> createFromElement(
						TextField.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.TEXT_AREA))
				component = ComponentFactory.<TextArea> createFromElement(
						TextArea.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.DATE_FIELD))
				component = ComponentFactory.<DateField> createFromElement(
						DateField.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.COMBOBOX))
				component = ComponentFactory.<ComboBox> createFromElement(
						ComboBox.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.TIMER_LABEL))
				component = ComponentFactory.<TimerLabel> createFromElement(
						TimerLabel.class, element, slideFascia);
			else if (name.equals(SlideXmlConstants.LABEL))
				component = ComponentFactory.<Label> createFromElement(
						Label.class, element, slideFascia);
			else
				component = createPluginComponent(element, slideFascia);

			String id = SlideXmlUtility.getId(element);
			slideFascia.registerComponent(id, component);

			return new LayoutComponent(component, component.getAlignment());
		}
		return null;
	}

	public static <T extends AbstractComponent & SlideComponent> T createFromElement(
			Class<T> clazz, Element element, SlideFascia slideFascia) {

		T component;
		try {
			component = clazz.newInstance();
			component.setSlideManager(slideFascia);
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

	private static SlideComponent createPluginComponent(Element element, SlideFascia slideFascia) {
		
		String namespace = element.getNamespacePrefix();
		if (namespace != null && !"".equals(namespace.trim())) {
			// find registered plugin
			SlideComponentPlugin componentPlugin = PluginManager.get().getComponentPlugin(namespace, element.getName());
			
			if (componentPlugin != null) {
				return componentPlugin.createComponentFromElement(element, slideFascia);
			}
		}

		return null;
	}

	private static void createViewportHandlers(Element rootElement, SlideFascia slideManager) {
		List<Element> elements = SlideXmlUtility.getComponentHandlers(rootElement);
		for (Element handler : elements) {
			setViewportHandler(handler, slideManager);
		}
	}

	public static void createViewportComponent(Element rootElement, SlideFascia slideFascia) {
		Element componentElement = SlideXmlUtility.getViewportInnerComponent(rootElement);
		LayoutComponent component = createComponentFromElement(componentElement, slideFascia);

		createViewportHandlers(rootElement, slideFascia);

		slideFascia.setViewportComponent(component.getComponent());
	}

	public static void createWindows(Element rootElement, SlideFascia slideFascia) {
		List<Element> elements = SlideXmlUtility.getWindowsElements(rootElement);
		for (Element windowElement : elements) {
			String id = SlideXmlUtility.getId(windowElement);
			if (!Strings.isNullOrEmpty(id)) {
				Element element = SlideXmlUtility.getViewportOrWindowRootElement(windowElement);

				Window window = new Window();
				window.setSlideManager(slideFascia);
				window.loadFromXml(element);

				slideFascia.registerComponent(id, window);
			}
		}
	}

	public static void createTimers(Element rootElement, SlideFascia slideFascia) {
		List<Element> elements = SlideXmlUtility.getTimersElements(rootElement);
		for (Element element : elements) {
			String id = SlideXmlUtility.getId(element);
			if (!Strings.isNullOrEmpty(id)) {

				Timer timer = new Timer();
				timer.setSlideManager(slideFascia);
				timer.loadFromXml(element);

				slideFascia.registerComponent(id, timer);
			}
		}
	}

	private static void setViewportHandler(Element element,	SlideFascia slideManager) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.INIT)) {
				setViewportInitHandler(action, slideManager);
			} else if (name.equals(SlideXmlConstants.SHOW)) {
				setViewportShowHandler(action, slideManager);
			} else if (name.equals(SlideXmlConstants.SHORTCUT)) {
				String key = SlideXmlUtility.getKey(element);
				setViewportShortcutHandler(action, key, slideManager);
			} else if (name.equals(SlideXmlConstants.MESSAGE)) {
				String uid = SlideXmlUtility.getUid(element);
				setViewportMessageHandler(action, uid, slideManager);
			}

			// TODO add other event handlers
		}
	}

	@SuppressWarnings("serial")
	private static void setViewportInitHandler(final String actionId, final SlideFascia slideManager) {
		slideManager.addViewportInitListener(new ViewportEventListener() {
			@Override
			public void handleEvent(ViewportEvent event) {
				SlideData data = new SlideData(slideManager.getSlide(), slideManager);
				Command componentEvent = CommandFactory.createSlideInitEventCommand(data, event.getTimestamp());
				Command action = CommandFactory.createSlideActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	@SuppressWarnings("serial")
	private static void setViewportShowHandler(final String actionId, final SlideFascia slideManager) {
		slideManager.addViewportShowListener(new ViewportEventListener() {
			@Override
			public void handleEvent(ViewportEvent event) {
				SlideData data = new SlideData(slideManager.getSlide(), slideManager);
				Command componentEvent = CommandFactory.createSlideShowEventCommand(data, event.getTimestamp());
				Command action = CommandFactory.createSlideActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}
	
	@SuppressWarnings("serial")
	private static void setViewportShortcutHandler(final String actionId, String key, final SlideFascia slideManager) {
		ShortcutKeys shortcutKeys = ShortcutUtility.parseShortcut(key);
		if (shortcutKeys != null) { 
			ShortcutKey shortcutKey = new ShortcutKey(shortcutKeys.getKeyCode(), shortcutKeys.getModifiers());
			final String shortcut = shortcutKey.toString();
			shortcutKey.addKeyPressListener(new KeyPressListener() {
				@Override
				public void keyPress(KeyPressEvent event) {
					SlideData data = new SlideData(slideManager.getSlide(), slideManager);
					data.setShortcutKey(shortcut);
					Command componentEvent = CommandFactory.createSlideShortcutKeyEventCommand(data);
					Command action = CommandFactory.createSlideActionCommand(slideManager, actionId, data);

					Command.Executor.execute(componentEvent);
					Command.Executor.execute(action);
				}
			});
			
			slideManager.registerComponent(null, shortcutKey);
		}
	}

	@SuppressWarnings("serial")
	private static void setViewportMessageHandler(final String actionId, String uid, final SlideFascia slideManager) {
		if (!Strings.isNullOrEmpty(uid)) {
			slideManager.addMessageListener(uid, new MessageEventListener() {
				@Override
				public void handleEvent(MessageEvent event) {
					SlideData data = new SlideData(slideManager.getSlide(), slideManager);
					data.setMessage(event.getMessage());
					Command componentEvent = CommandFactory.createMessageEventCommand(data, event.getTimestamp());
					Command action = CommandFactory.createSlideActionCommand(slideManager, actionId, data);
	
					Command.Executor.execute(componentEvent);
					Command.Executor.execute(action);
				}
			});
		}
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
		List<Element> validatorElements = SlideXmlUtility.getComponentValidators(element);
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
		List<Element> validatorElements = SlideXmlUtility.getComponentValidators(element);
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
		List<Element> validatorElements = SlideXmlUtility.getComponentValidators(element);
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
		List<Element> validatorElements = SlideXmlUtility.getComponentValidators(element);
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
