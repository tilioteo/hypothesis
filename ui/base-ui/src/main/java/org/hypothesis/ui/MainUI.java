/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hypothesis.cdi.Main;
import org.hypothesis.interfaces.UIPresenter;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Title("Hypothesis")
@Theme("hypothesis")
@CDIUI("")
public class MainUI extends HypothesisUI {
	
	@Inject
	@Main
	private UIPresenter presenter; 
	
	public MainUI() {
		System.out.println("Construct " + getClass().getName());
	}
	
	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
		
		setPresenter(presenter);
		presenter.setUI(this);
	}
	
}
