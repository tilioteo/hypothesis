/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.ui;

import java.util.List;

import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.processing.ProcessingComponentFactory;
import com.tilioteo.hypothesis.plugin.processing.ProcessingUtility;
import com.tilioteo.hypothesis.plugin.processing.SlideXmlUtility;
import com.tilioteo.hypothesis.plugin.processing.event.ProcessingData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.ui.ComponentUtility;
import com.tilioteo.hypothesis.ui.Mask;
import com.tilioteo.hypothesis.ui.Maskable;
import com.tilioteo.hypothesis.ui.ParentAlignment;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Processing extends org.vaadin.tltv.vprocjs.ui.Processing implements SlideComponent, Maskable {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;
	
	public Processing() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public Processing(SlideManager slideManager) {
		this();
		setSlideManager(slideManager);
	}
	
	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {
		setProperties(element);
		setCallbacks(element);
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Processing specific properties
		ProcessingUtility.setProcessingProperties(this, element, properties);
	}

	private void setCallbacks(Element element) {
		List<Element> callbacks = ProcessingUtility.getCallbackElements(element);

		for (Element callback : callbacks) {
			setCallback(callback);
		}
	}

	private void setCallback(Element element) {
		final String name = SlideXmlUtility.getName(element);
		if (!Strings.isNullOrEmpty(name)) {
			String action = null;
			AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
			if (anonymousAction != null)
				action = anonymousAction.getId();

			if (!Strings.isNullOrEmpty(action)) {
				final String actionId = action;
				final List<String> argumentTypes = SlideXmlUtility.getArgumentTypes(element);
				
				JavaScript.getCurrent().addFunction(name, new JavaScriptFunction() {
					@Override
					public void call(JSONArray arguments) throws JSONException {
						ProcessingData data = new ProcessingData(Processing.this, slideManager);
						data.setName(name);
						data.setArgumentTypes(argumentTypes);
						data.setArguments(arguments);
						
						Command componentEvent = ProcessingComponentFactory.createCallbackEventCommand(data);
						Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

						Command.Executor.execute(componentEvent);
						Command.Executor.execute(action);
					}
				});
			}
		}
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		if (this.slideManager != slideManager) {
			this.slideManager = slideManager;
		}
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

}
