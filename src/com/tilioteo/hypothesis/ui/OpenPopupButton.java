/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.model.UrlConsumer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class OpenPopupButton extends org.vaadin.button.ui.OpenPopupButton implements UrlConsumer {

	public OpenPopupButton(String caption) {
		super(caption);
	}

}
