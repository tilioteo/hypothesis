/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class VerticalLayout extends com.vaadin.ui.VerticalLayout implements
		SlideComponentContainer, Maskable {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;

	public VerticalLayout() {
		this.parentAlignment = new ParentAlignment();
	}

	public VerticalLayout(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	@Override
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

				float ratio = 1.0f;
				if (component.getWidthUnits() == Unit.PERCENTAGE) {
					ratio = component.getHeight() / 100;
					component.setHeight("100%");
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
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonLayoutProperties(this, element, properties,
				parentAlignment);
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
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
