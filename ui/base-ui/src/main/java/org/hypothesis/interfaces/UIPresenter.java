/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.server.VaadinRequest;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface UIPresenter extends ComponentPresenter {

	public void initialize(VaadinRequest request);

	public void close();

	public void refresh(VaadinRequest request);

}
