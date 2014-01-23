/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.core.XmlDataWriter;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TextArea extends com.vaadin.ui.TextArea implements SlideComponent,
		XmlDataWriter {

	@SuppressWarnings("unused")
	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public TextArea() {
		this.parentAlignment = new ParentAlignment();
	}

	public TextArea(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	public void loadFromXml(Element element) {

		setProperties(element);

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
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.TEXTAREA);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		valueElement.addText((String) getValue());
	}

}
