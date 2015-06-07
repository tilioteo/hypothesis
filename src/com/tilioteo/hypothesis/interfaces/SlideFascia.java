/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;
import java.util.Map;

import org.dom4j.Document;

import com.tilioteo.hypothesis.event.MessageEventListener;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public interface SlideFascia extends HasVariables, HasActions, Serializable {

	public void registerComponent(String id, Component component);

	public SlideComponent getComponent(String id);

	public SlideEntity getSlide();

	public Map<String, Field> getFields();

	public SlideComponent getTimer(String id);

	public SlideComponent getWindow(String id);

	public boolean hasValidFields();

	public Document getSlideXml();

	public Component getViewportComponent();

	public void setViewportComponent(Component component);
	
	public void addViewportInitListener(ViewportEventListener listener);

	public void addViewportShowListener(ViewportEventListener listener);
	
	public void addMessageListener(String uid, MessageEventListener listener);
	
	public Map<Integer, ExchangeVariable> getInputs();

	public Map<Integer, ExchangeVariable> getOutputs();

	public Object createMessage(String uid);
	
	public void postMessage(String message);
	
	public UI getUI();
}
