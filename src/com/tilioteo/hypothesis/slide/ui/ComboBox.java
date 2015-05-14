/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.data.Validator;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.Field;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ComboBox extends com.vaadin.ui.ComboBox implements SlideComponent, Field {

	@SuppressWarnings("unused")
	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;

	public ComboBox() {
		this.parentAlignment = new ParentAlignment();
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
		List<Element> items = SlideXmlUtility.getComponentItems(element);
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
		setValidators(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonFieldProperties(this, element, properties,
				parentAlignment);

	}

	private void setValidators(Element element) {
		List<Validator> validators = ComponentFactory.createComboBoxValidators(element);
		for (Validator validator : validators) {
			addValidator(validator);
		}
		
		if (!validators.isEmpty()) {
			setImmediate(true);
		}
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	@Override
	public void readDataFromElement(Element element) {
		// TODO Auto-generated method stub
		
	}

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
