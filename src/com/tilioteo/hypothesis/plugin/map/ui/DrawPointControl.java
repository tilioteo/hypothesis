/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPointControl extends org.vaadin.maps.ui.control.DrawPointControl implements SlideComponent {

	public DrawPointControl() {
		super(null);
	}
	
	@Override
	public Alignment getAlignment() {
		return null;
	}

	@Override
	public void loadFromXml(Element element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		// TODO Auto-generated method stub
		
	}

}
