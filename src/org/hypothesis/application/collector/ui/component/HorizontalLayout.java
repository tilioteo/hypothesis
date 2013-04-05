/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.util.List;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.application.collector.xml.SlideXmlUtility;
import org.hypothesis.common.StringMap;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class HorizontalLayout extends com.vaadin.ui.HorizontalLayout implements
		Component {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public HorizontalLayout() {
		this.parentAlignment = new ParentAlignment();
	}

	public HorizontalLayout(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	private void addChilds(Element element) {
		List<Element> elements = SlideXmlUtility.getContainerComponents(
				element, SlideXmlConstants.VALID_CONTAINER_ELEMENTS);
		for (Element element2 : elements) {
			LayoutComponent layoutComponent = ComponentFactory
					.createComponentFromElement(element2, slideManager);
			if (layoutComponent != null) {
				Component component = layoutComponent.getComponent();

				addComponent(component);
				setComponentAlignment(component, layoutComponent.getAlignment());

				float ratio = 1.0f;
				if (component.getWidthUnits() == Sizeable.UNITS_PERCENTAGE) {
					ratio = component.getWidth();
					component.setWidth("100%");
				}

				setExpandRatio(component, ratio);
			}
		}
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		addChilds(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonLayoutProperties(this, element, properties,
				parentAlignment);
	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
