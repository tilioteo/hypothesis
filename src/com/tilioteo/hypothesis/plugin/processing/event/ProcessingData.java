/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.event;

import java.util.List;

import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.processing.SlideFactory;
import com.tilioteo.hypothesis.plugin.processing.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;

/**
 * @author kamil
 *
 */
public class ProcessingData extends AbstractComponentData<Processing> {
	
	private String name;
	private JSONArray arguments;
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
	
	public void setArguments(JSONArray arguments) {
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

			if (!arguments.isNull(index)) {
				try {
					if (type.equalsIgnoreCase(SlideXmlConstants.BOOLEAN)) {
						return arguments.getBoolean(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.FLOAT)) {
						return arguments.getDouble(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.INTEGER)) {
						return arguments.getInt(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.OBJECT)) {
						return arguments.get(index);
					} else if (type.equalsIgnoreCase(SlideXmlConstants.STRING)) {
						return arguments.getString(index);
					}
				} catch (JSONException e) {}
			}
		}
		return null;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeProcessingData(element, this);
	}

}
