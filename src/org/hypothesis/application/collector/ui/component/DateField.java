/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Element;
import org.hypothesis.application.collector.XmlDataWriter;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.common.StringMap;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField implements SlideComponent,
		XmlDataWriter {

	@SuppressWarnings("unused")
	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public DateField() {
		this.parentAlignment = new ParentAlignment();
	}

	public DateField(SlideManager slideManager) {
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
		element.addAttribute(SlideXmlConstants.TYPE,
				SlideXmlConstants.DATEFIELD);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		if (getValue() != null) {
			Date date = (Date) getValue();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			valueElement.addText(format.format(date));
		}
	}

}
