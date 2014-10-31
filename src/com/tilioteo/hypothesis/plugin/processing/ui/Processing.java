/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.processing.ProcessingUtility;
import com.tilioteo.hypothesis.ui.ComponentUtility;
import com.tilioteo.hypothesis.ui.Mask;
import com.tilioteo.hypothesis.ui.Maskable;
import com.tilioteo.hypothesis.ui.ParentAlignment;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Processing extends org.vaadin.tltv.vprocjs.ui.Processing implements SlideComponent, Maskable {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;
	
	public Processing() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public Processing(SlideManager slideManager) {
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
		setHandlers(element);
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Processing specific properties
		ProcessingUtility.setProcessingProperties(this, element, properties);
	}

	private void setHandlers(Element element) {
		// TODO
		/*List<Element> handlers = SlideUtility.getHandlerElements(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}*/
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		if (this.slideManager != slideManager) {
			//MapUtility.remove(this.slideManager);
			this.slideManager = slideManager;
			//MapUtility.newInstance(slideManager, this);
		}
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

}
