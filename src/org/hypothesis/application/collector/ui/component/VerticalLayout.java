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
public class VerticalLayout extends com.vaadin.ui.VerticalLayout implements
		SlideComponent {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public VerticalLayout() {
		this.parentAlignment = new ParentAlignment();
	}

	public VerticalLayout(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	private void addChilds(Element element) {
		List<Element> elements = SlideXmlUtility.getContainerComponents(
				element, SlideXmlConstants.VALID_CONTAINER_ELEMENTS);
		for (Element childElement : elements) {
			LayoutComponent layoutComponent = ComponentFactory
					.createComponentFromElement(childElement, slideManager);
			if (layoutComponent != null) {
				SlideComponent component = layoutComponent.getComponent();

				addComponent(component);
				setComponentAlignment(component, layoutComponent.getAlignment());

				float ratio = 1.0f;
				if (component.getWidthUnits() == Sizeable.UNITS_PERCENTAGE) {
					ratio = component.getHeight() / 100;
					component.setHeight("100%");
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
