/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import org.dom4j.Element;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface SlideComponent extends Component {

	public Alignment getAlignment();

	public void loadFromXml(Element element);

	public void setSlideManager(SlideFascia slideFascia);
	
}
