/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FormLayout extends com.vaadin.ui.FormLayout implements SlideComponentContainer {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public FormLayout() {
		this.parentAlignment = new ParentAlignment();
	}

	public FormLayout(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	public void addXmlChilds(Element element) {
		List<Element> elements = SlideXmlUtility.getContainerComponents(
				element, SlideXmlConstants.VALID_CONTAINER_ELEMENTS);
		for (Element childElement : elements) {
			LayoutComponent layoutComponent = ComponentFactory
					.createComponentFromElement(childElement, slideManager);
			if (layoutComponent != null) {
				Component component = layoutComponent.getComponent();
				addComponent(component);
				setComponentAlignment(component, layoutComponent.getAlignment());

				/*if (component instanceof AbstractField) {
					if (!Strings.isNullOrEmpty((String) ((AbstractField<?>) component).getData())) {
						addField(((AbstractField<?>) component).getData(), (Field<?>) component);
					}
					else {
						getLayout().addComponent(component);
					}
				}*/
			}
		}
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		addXmlChilds(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
