/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.tilioteo.common.collections.StringSet;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideComponentPlugin;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.processing.event.ProcessingEventTypes;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessingPlugin implements SlideComponentPlugin {

	private StringSet elements = new StringSet(
			SlideXmlConstants.PROCESSING,
			SlideXmlConstants.HANOI
			);

	private StringSet eventTypes = new StringSet(
			ProcessingEventTypes.Callback
			);
	
	private Map<String, Set<ValidParentGroup>> elementParentGroups = new HashMap<String, Set<ValidParentGroup>>();
	
	public ProcessingPlugin() {
		HashSet<ValidParentGroup> parentGroups = new HashSet<ValidParentGroup>();
		parentGroups.add(ValidParentGroup.CONTAINER);
		elementParentGroups.put(getNamespace() + ":" + SlideXmlConstants.PROCESSING, parentGroups);
		elementParentGroups.put(getNamespace() + ":" + SlideXmlConstants.HANOI, parentGroups);
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
	public SlideComponent createComponentFromElement(Element element, SlideFascia slideManager) {
		return ProcessingComponentFactory.createComponentFromElement(element, slideManager);
	}

}
