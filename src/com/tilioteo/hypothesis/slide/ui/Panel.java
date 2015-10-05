/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Panel extends com.vaadin.ui.Panel implements SlideComponentContainer, Maskable {

	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;
	private Mask mask = null;

	public Panel() {
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

				if (elements.size() == 1 && component instanceof Layout) {
					setContent((Layout) component);
				} else {
					GridLayout gridLayout = new GridLayout(1, 1);
					gridLayout.setSizeFull();
					setContent(gridLayout);
					gridLayout.addComponent(component);
					gridLayout.setComponentAlignment(component,
							layoutComponent.getAlignment());
				}
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

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// set Panel specific properties
		// defaults to true
		boolean border = properties.getBoolean(SlideXmlConstants.BORDER, true);
		if (!border) {
			addStyleName("borderless");
		}
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor("#808080");
		mask.show();
	}

	@Override
	public void mask(String color) {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor(color);
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

}
