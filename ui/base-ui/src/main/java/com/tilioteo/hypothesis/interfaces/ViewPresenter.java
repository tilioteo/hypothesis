/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * @author kamil
 *
 */
public interface ViewPresenter extends ComponentPresenter {

	public void enter(ViewChangeEvent event);

	public View createView();

}
