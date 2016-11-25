/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.util.List;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlideComponentPlugin extends Plugin {

	enum ValidParentGroup {
		VIEWPORT, PANEL, CONTAINER
	}

	/**
	 * XML namespace of plugin elements Plugins must have a namespace, the
	 * implemented method cannot return null or empty string.
	 * 
	 * @return Namespace name.
	 */
	String getNamespace();

	/**
	 * XML root elements of components provided by plugin.
	 * 
	 * @return List of element names
	 */
	List<String> getElements();

	List<String> getEventTypes();

	Map<String, List<ValidParentGroup>> getElementParentGroups();

	ComponentWrapper createComponentFromElement(Element element, SlidePresenter presenter);
}
