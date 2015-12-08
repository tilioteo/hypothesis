/**
 * 
 */
package com.tilioteo.hypothesis.common;

import com.tilioteo.hypothesis.interfaces.AlignmentWrapper;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AlignmentWrapperImpl implements AlignmentWrapper {

	private Alignment alignment = null;

	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	@Override
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

}
