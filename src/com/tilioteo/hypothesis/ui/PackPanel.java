/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 *
 */
public class PackPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout verticalLayout = new VerticalLayout();

	public PackPanel() {
		setWidth("100%");
		setHeight("150px");
		
		setContent(verticalLayout);
		
	}
	
}
