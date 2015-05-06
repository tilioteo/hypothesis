/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class Label extends com.vaadin.ui.Label implements SlideComponent {

	protected SlideFascia slideFascia;
	private ParentAlignment parentAlignment;

	public Label() {
		this.parentAlignment = new ParentAlignment();
		setContentMode(ContentMode.HTML);
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

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
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
