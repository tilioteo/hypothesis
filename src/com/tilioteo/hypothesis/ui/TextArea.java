/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.Field;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TextArea extends com.vaadin.ui.TextArea implements SlideComponent,
		Field {

	private ParentAlignment parentAlignment;

	public TextArea() {
		this.parentAlignment = new ParentAlignment();
	}

	public TextArea(SlideManager slideManager) {
		this();
		//this.slideManager = slideManager;
	}

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonFieldProperties(this, element, properties,
				parentAlignment);

	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		// nop
	}

	@Override
	public void readDataFromElement(Element element) {
		// TODO Auto-generated method stub
		
	}

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
