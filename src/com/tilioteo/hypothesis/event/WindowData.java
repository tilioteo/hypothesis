/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Window;

/**
 * @author kamil
 *
 */
public class WindowData extends AbstractComponentData<Window> {

	public WindowData(Window sender, SlideManager slideManager) {
		super(sender, slideManager);
	}
	
	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeWindowData(element, this);
	}

}
