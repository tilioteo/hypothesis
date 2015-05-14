/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Collection;

import org.vaadin.special.ui.ShortcutKey;

import com.tilioteo.hypothesis.slide.ui.Timer;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RenderContentEvent extends AbstractContentEvent {

	Collection<Timer> timers;
	Collection<ShortcutKey> shortcutKeys;
	
	public RenderContentEvent(Component component, Collection<Timer> timers, Collection<ShortcutKey> shortcutKeys) {
		this(component, timers, shortcutKeys, null);
	}

	public RenderContentEvent(Component component, Collection<Timer> timers, Collection<ShortcutKey> shortcutKeys, ErrorHandler errorHandler) {
		super(component, errorHandler);
		this.timers = timers;
		this.shortcutKeys = shortcutKeys;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.RenderSlide;
	}
	
	public Collection<Timer> getTimers() {
		return timers;
	}
	
	public Collection<ShortcutKey> getShortcutKeys() {
		return shortcutKeys;
	}
}
