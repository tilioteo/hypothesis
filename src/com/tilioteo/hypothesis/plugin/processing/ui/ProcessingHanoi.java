/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.ui;

import org.dom4j.Element;
import org.vaadin.tltv.vprocjs.ui.Processing;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.ComponentUtility;
import com.tilioteo.hypothesis.slide.ui.Mask;
import com.tilioteo.hypothesis.slide.ui.Maskable;
import com.tilioteo.hypothesis.slide.ui.ParentAlignment;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@JavaScript( {"processing-1.4.8.js"} )
public class ProcessingHanoi extends Processing implements SlideComponent, Maskable {

	private SlideFascia slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;
	
	public ProcessingHanoi() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public ProcessingHanoi(SlideFascia slideManager) {
		this();
		setSlideManager(slideManager);
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
		StringMap properties = com.tilioteo.hypothesis.dom.SlideXmlUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Processing specific properties
		//ProcessingUtility.setHanoiProperties(this, element, properties);
	}

	@Override
	public void setSlideManager(SlideFascia slideManager) {
		if (this.slideManager != slideManager) {
			this.slideManager = slideManager;
		}
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor("#808080");
		mask.show();
	}

	@Override
	public void mask(String color) {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor(color);
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

}
