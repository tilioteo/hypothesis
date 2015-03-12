/**
 * 
 */
package com.tilioteo.hypothesis.extension;

import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.SlideComponent;

/**
 * @author kamil
 *
 */
public interface SlideComponentPlugin extends Plugin {
	
	public enum ValidParentGroup { VIEWPORT, PANEL, CONTAINER };

	/**
	 * XML namespace of plugin elements
	 * Plugins must have a namespace, the implemented method cannot return null or empty string.
	 * 
	 * @return Namespace name.
	 */
	public String getNamespace();
	
	/**
	 * XML root elements of components provided by plugin.
	 * 
	 * @return Set of element names
	 */
	public Set<String> getElements();
	
	public Set<String> getEventTypes();
	
	public Map<String, Set<ValidParentGroup>> getElementParentGroups();
	
	public SlideComponent createComponentFromElement(Element element, SlideManager slideManager);
}
