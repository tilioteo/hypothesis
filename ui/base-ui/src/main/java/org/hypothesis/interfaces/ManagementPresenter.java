/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ManagementPresenter extends ViewPresenter {

	public Component buildHeader();

	public Table buildTable();
	
	public void init();

}
