/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.server.Extension;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

import java.util.Map;

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

	Map<String, Field> getFields();

	void setUserId(Long userId);

	void handleEvent(Component component, String typeName, String eventName, Action action, ComponentEventCallback callback);

	void addViewportInitListener(ViewportEventListener viewportEventListener);

	void addViewportShowListener(ViewportEventListener viewportEventListener);

	void addViewportFinishListener(ViewportEventListener viewportEventListener);

	void addKeyAction(Extension keyAction);

	void addMessageListener(String uid, MessageEventListener messageEventListener);

	Component getComponent(String id);

	void setComponent(String id, Component component);

	String getSlideId();

}
