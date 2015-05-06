/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FormLayout extends com.vaadin.ui.FormLayout implements SlideComponentContainer {

	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;

	public FormLayout() {
		this.parentAlignment = new ParentAlignment();
	}

	@Override
	public void addXmlChilds(Element element) {
		List<Element> elements = SlideXmlUtility.getContainerComponents(
				element, SlideXmlConstants.VALID_CONTAINER_ELEMENTS);
		for (Element childElement : elements) {
			LayoutComponent layoutComponent = ComponentFactory
					.createComponentFromElement(childElement, slideFascia);
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

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		addXmlChilds(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

}
