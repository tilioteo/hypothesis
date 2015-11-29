/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * @author kamil
 *
 */
public interface ManagementPresenter extends ViewPresenter {

	public Component buildHeader();

	public Table buildTable();

}
