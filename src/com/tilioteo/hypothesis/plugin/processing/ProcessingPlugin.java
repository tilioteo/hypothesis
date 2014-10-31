/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringSet;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.extension.SlideComponentPlugin;
import com.tilioteo.hypothesis.ui.SlideComponent;

/**
 * @author kamil
 *
 */
public class ProcessingPlugin implements SlideComponentPlugin {

	private StringSet elements = new StringSet(
			SlideXmlConstants.PROCESSING
			);

	private StringSet eventTypes = new StringSet(
			//MapEventTypes.LayerLoad,
			);
	
	private Map<String, Set<ValidParentGroup>> elementParentGroups = new HashMap<String, Set<ValidParentGroup>>();
	
	public ProcessingPlugin() {
		HashSet<ValidParentGroup> parentGroups = new HashSet<ValidParentGroup>();
		parentGroups.add(ValidParentGroup.CONTAINER);
		elementParentGroups.put(getNamespace() + ":" + SlideXmlConstants.PROCESSING, parentGroups);
	}
	
	@Override
	public String getNamespace() {
		return SlideXmlConstants.NAMESPACE;
	}

	@Override
	public Set<String> getElements() {
		return elements;
	}

	@Override
	public Set<String> getEventTypes() {
		return eventTypes;
	}

	@Override
	public Map<String, Set<ValidParentGroup>> getElementParentGroups() {
		return elementParentGroups;
	}

	@Override
	public SlideComponent createComponentFromElement(Element element, SlideManager slideManager) {
		return ProcessingComponentFactory.createComponentFromElement(element, slideManager);
	}

}
