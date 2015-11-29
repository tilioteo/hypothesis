/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.Date;

import com.tilioteo.hypothesis.interfaces.Field;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField implements Field {

	public DateField() {
		super();
	}

	/**
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
	*/

    @Override
    public void setValue(Date newValue) throws com.vaadin.data.Property.ReadOnlyException {
        boolean readOnly = false;
    	if (isReadOnly()) {
    		readOnly = true;
    		setReadOnly(false);
    	}
    	super.setValue(newValue);
    	
    	if (readOnly) {
    		setReadOnly(true);
    	}
    }

}
