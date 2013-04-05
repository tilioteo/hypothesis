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
public class AfterRenderContentEvent extends AbstractContentEvent {

	public AfterRenderContentEvent(LayoutComponent content) {
		super(content);
	}

	public String getName() {
		return ProcessEvents.AfterRender;
	}

}
