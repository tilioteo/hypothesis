/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Map;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public interface SlidePresenter {

	public void attach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	public void detach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	public void buildDone();

	public void viewDone();

	public Map<Integer, ExchangeVariable> getInputs();

	public Map<Integer, ExchangeVariable> getOutputs();

	public Map<String, Variable<?>> getVariables();

	public void setUserId(Long userId);

}
