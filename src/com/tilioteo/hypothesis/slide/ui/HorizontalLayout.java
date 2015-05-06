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
public class HorizontalLayout extends com.vaadin.ui.HorizontalLayout implements
		SlideComponentContainer, Maskable {

	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;
	private Mask mask = null;

	public HorizontalLayout() {
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

				float ratio = 1.0f;
				if (component.getWidthUnits() == Unit.PERCENTAGE) {
					ratio = component.getWidth() / 100;
					component.setWidth("100%");
				}

				setExpandRatio(component, ratio);
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

		ComponentUtility.setCommonLayoutProperties(this, element, properties,
				parentAlignment);
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
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}
	
}
