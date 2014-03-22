/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.Field;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.data.Validator;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField implements SlideComponent,
		Field {

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

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setValidators(element);

 	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonFieldProperties(this, element, properties,
				parentAlignment);

	}

	private void setValidators(Element element) {
		List<Validator> validators = ComponentFactory.createDateFieldValidators(element);
		for (Validator validator : validators) {
			addValidator(validator);
		}
		
		if (!validators.isEmpty()) {
			setImmediate(true);
		}
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	@Override
	public void readDataFromElement(Element element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.DATE_FIELD);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element captionElement = element.addElement(SlideXmlConstants.CAPTION);
		if (getCaption() != null) {
			captionElement.addText(getCaption());
		}
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		if (getValue() != null) {
			Date date = (Date) getValue();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			valueElement.addText(format.format(date));
		}
	}

}
