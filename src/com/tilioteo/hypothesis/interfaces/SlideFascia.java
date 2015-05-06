/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Map;

import org.dom4j.Document;

import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface SlideFascia extends HasVariables, HasActions {

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
	
	public Map<Integer, ExchangeVariable> getInputs();

	public Map<Integer, ExchangeVariable> getOutputs();
}
