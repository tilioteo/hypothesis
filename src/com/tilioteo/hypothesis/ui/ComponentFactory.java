/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
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
			else if (name.equals(SlideXmlConstants.RADIO_PANEL))
				component = ComponentFactory.<RadioPanel> createFromElement(
						RadioPanel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TEXTFIELD))
				component = ComponentFactory.<TextField> createFromElement(
						TextField.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TEXTAREA))
				component = ComponentFactory.<TextArea> createFromElement(
						TextArea.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.DATEFIELD))
				component = ComponentFactory.<DateField> createFromElement(
						DateField.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.COMBOBOX))
				component = ComponentFactory.<ComboBox> createFromElement(
						ComboBox.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.TIMERLABEL))
				component = ComponentFactory.<TimerLabel> createFromElement(
						TimerLabel.class, element, slideManager);
			else if (name.equals(SlideXmlConstants.LABEL))
				component = ComponentFactory.<Label> createFromElement(
						Label.class, element, slideManager);
			else
				component = createPluginComponent(element, slideManager);

			String id = SlideXmlUtility.getId(element);
			if (!Strings.isNullOrEmpty(id) && component != null) {
				slideManager.getComponents().put(id, component);
			}

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
		
		// get registered plugins

		// TODO not implemented yet
		return null;
	}

	private static void createVieportHandlers(Element documentRoot,
			SlideManager slideManager) {
		Element element = SlideXmlUtility.getVieportRootElement(documentRoot);
		if (element != null) {
			List<Element> handlers = SlideUtility.getHandlerElements(element);

			for (Element handler : handlers) {
				setViewportHandler(handler, slideManager);
			}
		}
	}

	public static void createViewportComponent(Element rootElement,
			SlideManager slideManager) {
		Element componentElement = SlideXmlUtility
				.getViewportInnerComponent(rootElement);
		LayoutComponent component = createComponentFromElement(
				componentElement, slideManager);

		createVieportHandlers(rootElement, slideManager);

		slideManager.setViewport(component);
	}

	public static void createWindows(Element rootElement,
			SlideManager slideManager) {
		List<Element> elements = SlideXmlUtility
				.getWindowsElements(rootElement);
		for (Element windowElement : elements) {
			String id = SlideXmlUtility.getId(windowElement);
			if (!Strings.isNullOrEmpty(id)) {
				Element element = SlideXmlUtility
						.getViewportOrWindowRootElement(windowElement);

				Window window = new Window(slideManager);
				window.loadFromXml(element);

				slideManager.getWindows().put(id, window);
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
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
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
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		slideManager.addViewportEventListener(SlideManager.ShowEvent.class,
				new ViewportEventListener() {
					@Override
					public void handleEvent(ViewportEvent event) {
						action.execute();
					}
				});
	}

}
