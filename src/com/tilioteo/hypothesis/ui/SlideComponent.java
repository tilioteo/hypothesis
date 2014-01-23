/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface SlideComponent extends Component {

	public Alignment getAlignment();

	public void loadFromXml(Element element);

	public void setSlideManager(SlideManager slideManager);

}
