/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface ExportPresenter extends ViewPresenter {

	public Component buildHeader();

	public Component buildContent();

}
