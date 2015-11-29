/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.vaadin.server.VaadinRequest;

/**
 * @author kamil
 *
 */
public interface UIPresenter extends ComponentPresenter {

	public void initialize(VaadinRequest request);

	public void close();

	public void refresh(VaadinRequest request);

}
