/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringSet;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.extension.SlideComponentPlugin;
import com.tilioteo.hypothesis.plugin.map.event.MapEventTypes;
import com.tilioteo.hypothesis.ui.SlideComponent;

/**
 * @author kamil
 *
 */
public class MapPlugin implements SlideComponentPlugin {
	
	private StringSet elements = new StringSet(
			SlideXmlConstants.MAP
			/*SlideXmlConstants.LAYERS,
			SlideXmlConstants.IMAGELAYER,
			SlideXmlConstants.FEATURELAYER,
			SlideXmlConstants.FEATURES,
			SlideXmlConstants.FEATURE,
			SlideXmlConstants.GEOMETRY,
			SlideXmlConstants.CONTROLS,
			SlideXmlConstants.DRAW_POINT,
			SlideXmlConstants.DRAW_PATH*/
			);
	
	private StringSet eventTypes = new StringSet(
			MapEventTypes.LayerLoad,
			MapEventTypes.LayerClick,
			MapEventTypes.FeatureClick,
			MapEventTypes.DrawPoint,
			MapEventTypes.DrawPath
			);
	
	private Map<String, Set<ValidParentGroup>> elementParentGroups = new HashMap<String, Set<ValidParentGroup>>();
	
	public MapPlugin() {
		HashSet<ValidParentGroup> parentGroups = new HashSet<ValidParentGroup>();
		parentGroups.add(ValidParentGroup.CONTAINER);
		elementParentGroups.put(getNamespace() + ":" + SlideXmlConstants.MAP, parentGroups);
	}
	
	@Override
	public String getNamespace() {
		return SlideXmlConstants.NAMESPACE;
	}
	
	@Override
	public Set<String> getElements() {
		return elements;
	}
	
	public Set<String> getEventTypes() {
		return eventTypes;
	}

	@Override
	public Map<String, Set<ValidParentGroup>> getElementParentGroups() {
		return elementParentGroups;
	}

	@Override
	public SlideComponent createComponentFromElement(Element element, SlideManager slideManager) {
		return MapComponentFactory.createComponentFromElement(element, slideManager);
	}

}
