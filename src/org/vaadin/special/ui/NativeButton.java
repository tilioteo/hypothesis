/**
 * 
 */
package org.vaadin.special.ui;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class NativeButton extends Button {

	public NativeButton() {
		super();
	}

	public NativeButton(String caption) {
		super(caption);
	}

	public NativeButton(String caption, ClickListener listener) {
		super(caption, listener);
	}

}
