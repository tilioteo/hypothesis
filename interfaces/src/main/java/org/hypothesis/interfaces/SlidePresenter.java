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

	void attach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	void detach(Component slideContainer, HasComponents parent, UI ui, VaadinSession session);

	void buildDone();

	void viewDone();

	Map<Integer, ExchangeVariable> getInputs();

	Map<Integer, ExchangeVariable> getOutputs();

	Map<String, Variable<?>> getVariables();

	Map<String, Field> getFields();

	void setUserId(Long userId);

	void handleEvent(Component component, String typeName, String eventName, Action action, ComponentEventCallback callback);

	void addViewportInitListener(ViewportEventListener viewportEventListener);

	void addViewportShowListener(ViewportEventListener viewportEventListener);

	void addViewportFinishListener(ViewportEventListener viewportEventListener);

	//public void addShortcutKey(Component shortcutKey);
	
	void addKeyAction(Extension keyAction);

	void addMessageListener(String uid, MessageEventListener messageEventListener);

	Component getComponent(String id);

	void setComponent(String id, Component component);

	String getSlideId();

}
