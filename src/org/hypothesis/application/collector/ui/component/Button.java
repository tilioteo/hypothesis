/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.util.List;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.CommandFactory;
import org.hypothesis.application.collector.core.SlideFactory;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.events.Command;
import org.hypothesis.application.collector.slide.AbstractBaseAction;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.common.StringMap;
import org.hypothesis.common.Strings;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
public class Button extends com.vaadin.ui.NativeButton implements
		SlideComponent {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public Button() {
		this.parentAlignment = new ParentAlignment();
	}

	public Button(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

	}

	private void setClickHandler(String actionId) {
		final Command componentEvent = CommandFactory
				.createButtonClickEventCommand(this, slideManager);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				componentEvent.execute();
				action.execute();
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
				.createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideUtility.getHandlerElements(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// set Button specific properties
		// TODO in future set dynamic css
	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
