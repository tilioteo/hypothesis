/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.tilioteo.hypothesis.ui.MainScreen;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * @author kamil
 *
 */
public interface MainPresenter extends ComponentPresenter {

	public Component buildTopPanel();

	public Component buildMainPane();

	public Component buildBottomPanel();
	
	public ComponentContainer getContent();

	public MainScreen createScreen();

}
