/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Collection;

import com.tilioteo.hypothesis.ui.LayoutComponent;
import com.tilioteo.hypothesis.ui.ShortcutKey;
import com.tilioteo.hypothesis.ui.Timer;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RenderContentEvent extends AbstractContentEvent {

	Collection<Timer> timers;
	Collection<ShortcutKey> shortcutKeys;
	
	public RenderContentEvent(LayoutComponent content, Collection<Timer> timers, Collection<ShortcutKey> shortcutKeys) {
		super(content);
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
