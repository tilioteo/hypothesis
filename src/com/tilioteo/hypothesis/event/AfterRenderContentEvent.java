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
public class AfterRenderContentEvent extends AbstractContentEvent {

	public AfterRenderContentEvent(LayoutComponent content) {
		super(content);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.AfterRender;
	}

}
