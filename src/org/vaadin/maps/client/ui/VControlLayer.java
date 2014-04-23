/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 *
 */
public class VControlLayer extends VAbstractLayer {

	/** Class name, prefix in styling */
	public static final String CLASSNAME = "v-controllayer";

	public VControlLayer() {
		super();
		setStylePrimaryName(CLASSNAME);
		setVisible(false);
		//setSize("0px", "0px");
	}

}
