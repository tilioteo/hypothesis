/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.util.HashSet;
import java.util.Set;

import com.tilioteo.hypothesis.extension.SlideComponentPlugin;

/**
 * @author kamil
 *
 */
public class MapPlugin implements SlideComponentPlugin {
	
	@Override
	public String getNamespace() {
		return SlideXmlConstants.NAMESPACE;
	}
	
	@Override
	public Set<String> getElements() {
		HashSet<String> elements = new HashSet<String>();
		elements.add(SlideXmlConstants.MAP);
		/*elements.add(SlideXmlConstants.LAYERS);
		elements.add(SlideXmlConstants.IMAGELAYER);
		elements.add(SlideXmlConstants.FEATURELAYER);
		elements.add(SlideXmlConstants.FEATURES);
		elements.add(SlideXmlConstants.FEATURE);
		elements.add(SlideXmlConstants.GEOMETRY);
		elements.add(SlideXmlConstants.CONTROLS);
		elements.add(SlideXmlConstants.DRAW_POINT);
		elements.add(SlideXmlConstants.DRAW_PATH);*/
		return elements;
	}
	
	

}
