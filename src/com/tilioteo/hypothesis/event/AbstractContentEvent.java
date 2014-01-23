/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.LayoutComponent;

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
