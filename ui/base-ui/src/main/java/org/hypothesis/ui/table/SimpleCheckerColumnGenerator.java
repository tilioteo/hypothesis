package org.hypothesis.ui.table;

@SuppressWarnings("serial")
public class SimpleCheckerColumnGenerator extends AbstractSimpleCheckerColumnGenerator {

	public SimpleCheckerColumnGenerator(String stateField, String buttonCaption) {
		super(stateField, buttonCaption);
	}

	@Override
	public void onStateChanged(Object itemId, boolean checked) {
		// noop
	}

}
