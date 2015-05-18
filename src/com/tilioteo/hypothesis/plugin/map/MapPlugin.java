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
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideComponentPlugin;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.event.MapEventTypes;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MapPlugin implements SlideComponentPlugin {
	
	private StringSet elements = new StringSet(
			SlideXmlConstants.MAP
			/*SlideXmlConstants.LAYERS,
			SlideXmlConstants.IMAGE_LAYER,
			SlideXmlConstants.WMS_LAYER,
			SlideXmlConstants.IMAGE_SEQUENCE_LAYER,
			SlideXmlConstants.FEATURE_LAYER,
			SlideXmlConstants.FEATURES,
			SlideXmlConstants.FEATURE,
			SlideXmlConstants.GEOMETRY,
			SlideXmlConstants.CONTROLS,
			SlideXmlConstants.DRAW_POINT,
			SlideXmlConstants.DRAW_PATH,
			SlideXmlConstants.DRAW_LINE,
			SlideXmlConstants.DRAW_POLYGON,
			SlideXmlConstants.PAN,
			SlideXmlConstants.ZOOM*/
			);
	
	private StringSet eventTypes = new StringSet(
			MapEventTypes.LayerLoad,
			MapEventTypes.LayerClick,
			MapEventTypes.FeatureClick,
			MapEventTypes.DrawPoint,
			MapEventTypes.DrawPath,
			MapEventTypes.DrawPolygon,
			MapEventTypes.PanStart,
			MapEventTypes.PanEnd,
			MapEventTypes.ZoomChange,
			MapEventTypes.DrawLine
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
	public SlideComponent createComponentFromElement(Element element, SlideFascia slideFascia) {
		return MapComponentFactory.createComponentFromElement(element, slideFascia);
	}

}
