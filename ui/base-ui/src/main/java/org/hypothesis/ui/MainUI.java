/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import javax.inject.Inject;

import org.hypothesis.cdi.Main;
import org.hypothesis.interfaces.UIPresenter;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;

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

	public MainUI() {
		System.out.println("Construct MainUI");
	}

	@Override
	@Inject
	public void setPresenter(@Main UIPresenter presenter) {
		presenter.setUI(this);
		super.setPresenter(presenter);
	}
	
}
