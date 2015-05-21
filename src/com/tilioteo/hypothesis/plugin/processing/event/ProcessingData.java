/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.event;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.processing.SlideFactory;
import com.tilioteo.hypothesis.plugin.processing.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;

import elemental.json.JsonArray;
import elemental.json.JsonException;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessingData extends AbstractComponentData<Processing> {
	
	private String name;
	private JsonArray arguments;
	private List<String> argumentTypes;

	public ProcessingData(Processing sender, SlideFascia slideManager) {
		super(sender, slideManager);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setArguments(JsonArray arguments) {
		this.arguments = arguments;
	}
	
	public void setArgumentTypes(List<String> argumentTypes) {
		this.argumentTypes = argumentTypes;
	}
	
	public int getArgumentCount() {
		return argumentTypes != null ? argumentTypes.size() : 0;
	}
	
	public Object getArgument(int index) {
		if (argumentTypes != null && arguments != null && index >= 0 && index < argumentTypes.size()) {
			String type = argumentTypes.get(index);

			if (arguments.get(index) != null) {
				try {
					if (type.equalsIgnoreCase(SlideXmlConstants.BOOLEAN)) {
						return arguments.getBoolean(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.FLOAT)) {
						return arguments.getNumber(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.INTEGER)) {
						return (int)arguments.getNumber(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.OBJECT)) {
						return arguments.get(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.STRING)) {
						return arguments.getString(index);
					}
				} catch (JsonException e) {}
			}
		}
		return null;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeProcessingData(element, this);
	}

}
