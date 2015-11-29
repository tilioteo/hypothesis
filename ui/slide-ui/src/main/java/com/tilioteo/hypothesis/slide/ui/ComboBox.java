/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import com.tilioteo.hypothesis.interfaces.Field;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ComboBox extends com.vaadin.ui.ComboBox implements Field {

	public ComboBox() {
		super();
	}

	/**
	@Override
	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.COMBOBOX);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element captionElement = element.addElement(SlideXmlConstants.CAPTION);
		if (getCaption() != null) {
			captionElement.addText(getCaption());
		}
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		if (getValue() != null) {
			valueElement.addAttribute(SlideXmlConstants.ID, (String) getValue());
			valueElement.addText(getItemCaption(getValue()));
		}
	}
	*/

    @Override
    public void setValue(Object newValue) throws com.vaadin.data.Property.ReadOnlyException {
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
