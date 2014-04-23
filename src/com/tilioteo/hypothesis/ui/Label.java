/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class Label extends com.vaadin.ui.Label implements SlideComponent {

	protected SlideManager slideManager;
	private ParentAlignment parentAlignment;

	public Label() {
		this.parentAlignment = new ParentAlignment();
		setContentMode(ContentMode.HTML);
	}

	public Label(SlideManager slideManager) {
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

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}
	
	@Override
	public String getCaption() {
		return getValue();
	}
	
	@Override
	public void setCaption(String caption) {
		setValue(caption);
	}

}
