/**
 * 
 */
package org.vaadin.special.client.ui;

import org.vaadin.special.client.MediaEvents.PauseEvent;
import org.vaadin.special.client.MediaEvents.PauseHandler;
import org.vaadin.special.client.MediaEvents.PlayEvent;
import org.vaadin.special.client.MediaEvents.PlayHandler;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VAudio extends com.vaadin.client.ui.VAudio {

	protected final AudioElement getAudioElement() {
		return (AudioElement) Element.as(getElement());
	}
	
	public double getCurrentTime() {
		return getAudioElement().getCurrentTime();
	}
	
	public void setCurrentTime(double time) {
		getAudioElement().setCurrentTime(time);
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
