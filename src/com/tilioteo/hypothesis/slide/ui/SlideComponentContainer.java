/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.interfaces.SlideComponent;

/**
 * @author kamil
 *
 */
public interface SlideComponentContainer extends SlideComponent {
	
	void addXmlChilds(Element element);

}
