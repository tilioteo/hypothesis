/**
 * 
 */
package com.tilioteo.hypothesis.extension;

import java.util.Set;

/**
 * @author kamil
 *
 */
public interface SlideComponentPlugin extends Plugin {

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
	
	
}
