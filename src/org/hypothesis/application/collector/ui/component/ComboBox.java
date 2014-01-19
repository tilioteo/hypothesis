/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.util.List;

import org.dom4j.Element;
import org.hypothesis.application.collector.XmlDataWriter;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.application.collector.xml.SlideXmlUtility;
import org.hypothesis.common.StringMap;
import org.hypothesis.common.Strings;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ComboBox extends com.vaadin.ui.ComboBox implements SlideComponent,
		XmlDataWriter {

	@SuppressWarnings("unused")
	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public ComboBox() {
		this.parentAlignment = new ParentAlignment();
	}

	public ComboBox(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	private void addItem(Element element) {
		String value = SlideXmlUtility.getValue(element);
		if (!Strings.isNullOrEmpty(value)) {
			super.addItem(value);

			String caption = SlideXmlUtility.getCaption(element);
			if (!Strings.isNullOrEmpty(caption))
				setItemCaption(value, caption);
		}
	}

	private void addItems(Element element) {
		List<Element> items = SlideUtility.getItemElements(element);
		for (Element item : items) {
			addItem(item);
		}

		setImmediate(true);
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		addItems(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonFieldProperties(this, element, properties,
				parentAlignment);

	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.COMBOBOX);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		if (getValue() != null)
			valueElement.addAttribute(SlideXmlConstants.VALUE,
					(String) getValue());
		valueElement.addText(getCaption());
	}

}
