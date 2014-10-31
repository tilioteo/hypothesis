/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;
import com.tilioteo.hypothesis.ui.ComponentFactory;
import com.tilioteo.hypothesis.ui.SlideComponent;

/**
 * @author kamil
 *
 */
public class ProcessingComponentFactory {

	public static SlideComponent createComponentFromElement(Element element, SlideManager slideManager) {
		if (element != null) {
			String name = element.getName();
			SlideComponent component = null;

			if (name.equals(SlideXmlConstants.PROCESSING))
				component = ComponentFactory.<Processing> createFromElement(
						Processing.class, element, slideManager);

			// TODO create other components

			String id = SlideXmlUtility.getId(element);
			slideManager.registerComponent(id, component);

			return component;
		}
		return null;
	}

}
