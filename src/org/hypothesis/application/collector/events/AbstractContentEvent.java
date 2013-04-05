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
public abstract class AbstractContentEvent extends AbstractProcessEvent {

	protected AbstractContentEvent(LayoutComponent content) {
		super(content);
	}

	public LayoutComponent getContent() {
		return (LayoutComponent) getSource();
	}
}
