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
import org.hypothesis.common.Strings;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FormPanel extends Form implements Component {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public FormPanel() {
		super(new FormLayout());
		this.parentAlignment = new ParentAlignment();
	}

	public FormPanel(SlideManager slideManager) {
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
				if (component instanceof AbstractField) {
					if (!Strings
							.isNullOrEmpty((String) ((AbstractField) component)
									.getData()))
						addField(((AbstractField) component).getData(),
								(Field) component);
					else
						getLayout().addComponent(component);
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

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
