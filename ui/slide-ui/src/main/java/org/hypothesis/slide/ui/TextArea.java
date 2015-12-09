/**
 * 
 */
package org.hypothesis.slide.ui;

import org.hypothesis.interfaces.Field;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TextArea extends com.vaadin.ui.TextArea implements Field {

	public TextArea() {
		super();
	}

	/**
	@Override
	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.TEXT_AREA);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element captionElement = element.addElement(SlideXmlConstants.CAPTION);
		if (getCaption() != null) {
			captionElement.addText(getCaption());
		}
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		valueElement.addText((String) getValue());
	}
	*/

    @Override
    public void setValue(String newValue) throws com.vaadin.data.Property.ReadOnlyException {
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
