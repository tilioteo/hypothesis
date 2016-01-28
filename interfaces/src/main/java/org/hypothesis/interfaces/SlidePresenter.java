/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.util.Map;

import com.vaadin.server.Extension;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlidePresenter extends Evaluator {

	public void attach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	public void detach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	public void buildDone();

	public void viewDone();

	public Map<Integer, ExchangeVariable> getInputs();

	public Map<Integer, ExchangeVariable> getOutputs();

	public Map<String, Variable<?>> getVariables();

	public Map<String, Field> getFields();

	public void setUserId(Long userId);

	public void handleEvent(Component component, String typeName, String eventName, Action action,
			ComponentEventCallback callback);

	public void addViewportInitListener(ViewportEventListener viewportEventListener);

	public void addViewportShowListener(ViewportEventListener viewportEventListener);

	//public void addShortcutKey(Component shortcutKey);
	
	public void addKeyAction(Extension keyAction);

	public void addMessageListener(String uid, MessageEventListener messageEventListener);

	public Component getComponent(String id);

	public void setComponent(String id, Component component);

	public String getSlideId();

}
