/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideEntity;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.interfaces.XmlDataWriter;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideData implements XmlDataWriter {

	private SlideFascia slideFascia;
	private SlideEntity slide;
	private String shortcutKey = null;

	public SlideData(SlideEntity slide, SlideFascia slideFascia) {
		this.slide = slide;
		this.slideFascia = slideFascia;
	}

	public final String getComponentId() {
		return slide != null ? slide.getId().toString() : null;
	}

	/*public Slide getSender() {
		return slide;
	}*/

	public final SlideFascia getSlideManager() {
		return slideFascia;
	}

	public String getShortcutKey() {
		return shortcutKey;
	}

	public void setShortcutKey(String shortcutKey) {
		this.shortcutKey = shortcutKey;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeSlideData(element, this);
	}
}
