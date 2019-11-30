/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface UIPresenter extends ComponentPresenter {

	void initialize(VaadinRequest request);

	void refresh(VaadinRequest request);

	Locale getCurrentLocale();

	void cleanup();

}
