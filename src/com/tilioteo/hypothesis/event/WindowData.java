/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.Window;

/**
 * @author kamil
 *
 */
public class WindowData extends AbstractComponentData<Window> {

	public WindowData(Window sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}
	
	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeWindowData(element, this);
	}

}
