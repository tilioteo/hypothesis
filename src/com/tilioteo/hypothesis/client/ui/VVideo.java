/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tilioteo.hypothesis.client.MediaEvents.PauseEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PauseHandler;
import com.tilioteo.hypothesis.client.MediaEvents.PlayEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PlayHandler;

/**
 * @author kamil
 *
 */
public class VVideo extends com.vaadin.client.ui.VVideo {
	
	protected final VideoElement getVideoElement() {
		return (VideoElement) Element.as(getElement());
	}
	
	public double getCurrentTime() {
		return getVideoElement().getCurrentTime();
	}
	
	public void setCurrentTime(double time) {
		getVideoElement().setCurrentTime(time);
	}
	
	public HandlerRegistration addPauseHandler(PauseHandler handler) {
		return addBitlessDomHandler(handler, PauseEvent.TYPE);
	}

	public HandlerRegistration addPlayHandler(PlayHandler handler) {
		return addBitlessDomHandler(handler, PlayEvent.TYPE);
	}

	public HandlerRegistration addCanPlayThroughHandler(CanPlayThroughHandler handler) {
		return addBitlessDomHandler(handler, CanPlayThroughEvent.getType());
	}

	public HandlerRegistration addEndedHandler(EndedHandler handler) {
		return addBitlessDomHandler(handler, EndedEvent.getType());
	}

}
