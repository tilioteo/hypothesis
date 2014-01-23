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
import com.tilioteo.hypothesis.core.XmlDataWriter;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
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

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		addItems(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonFieldProperties(this, element, properties,
				parentAlignment);

	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	@Override
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
