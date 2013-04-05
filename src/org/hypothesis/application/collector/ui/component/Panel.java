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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Panel extends com.vaadin.ui.Panel implements Component {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public Panel() {
		this.parentAlignment = new ParentAlignment();
	}

	public Panel(SlideManager slideManager) {
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

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		addChilds(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// set Panel specific properties
		// defaults to true
		boolean border = properties.getBoolean(SlideXmlConstants.BORDER, true);
		if (!border)
			setStyleName(Reindeer.PANEL_LIGHT);

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
