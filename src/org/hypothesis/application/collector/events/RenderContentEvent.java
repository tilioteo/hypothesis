/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.ui.component.LayoutComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RenderContentEvent extends AbstractContentEvent {

	public RenderContentEvent(LayoutComponent content) {
		super(content);
	}

	public String getName() {
		return ProcessEvents.RenderSlide;
	}
}
