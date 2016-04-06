/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common;

import org.hypothesis.interfaces.AlignmentWrapper;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
