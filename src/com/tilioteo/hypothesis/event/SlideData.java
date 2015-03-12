/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.XmlDataWriter;
import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author kamil
 *
 */
public class SlideData implements XmlDataWriter {

	private SlideManager slideManager;
	private Slide sender;
	private String shortcutKey = null;

	public SlideData(Slide sender, SlideManager slideManager) {
		this.sender = sender;
		this.slideManager = slideManager;
	}

	public final String getComponentId() {
		return sender != null ? sender.getId().toString() : null;
	}

	public Slide getSender() {
		return sender;
	}

	public final SlideManager getSlideManager() {
		return slideManager;
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
