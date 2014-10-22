/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.ProcessEventManager;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.tilioteo.hypothesis.event.ViewportEventManager;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.ActionMap;
import com.tilioteo.hypothesis.processing.ComponentMap;
import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.FieldList;
import com.tilioteo.hypothesis.processing.HasActions;
import com.tilioteo.hypothesis.processing.HasVariables;
import com.tilioteo.hypothesis.processing.TimerMap;
import com.tilioteo.hypothesis.processing.Variable;
import com.tilioteo.hypothesis.processing.VariableMap;
import com.tilioteo.hypothesis.processing.WindowMap;
import com.tilioteo.hypothesis.ui.LayoutComponent;
import com.tilioteo.hypothesis.ui.ShortcutKey;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.tilioteo.hypothesis.ui.Timer;
import com.tilioteo.hypothesis.ui.Window;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideManager extends ListManager<Task, Slide> implements
		HasVariables, HasActions {
	
	private static Logger log = Logger.getLogger(SlideManager.class);

	@SuppressWarnings("serial")
	public static class InitEvent extends ViewportEvent {
		public InitEvent(Object source) {
			super(source);
		}
	}
	@SuppressWarnings("serial")
	public static class ShowEvent extends ViewportEvent {
		public ShowEvent(Object source) {
			super(source);
		}
	}

	private ViewportEventManager viewportEventManager = new ViewportEventManager();

	private ProcessEventManager eventManager;
	private SlideFactory slideFactory;

	private FieldList fields = new FieldList();
	private ComponentMap components = new ComponentMap();
	private WindowMap windows = new WindowMap();
	private TimerMap timers = new TimerMap();
	private VariableMap variables = new VariableMap();
	private ActionMap actions = new ActionMap();
	private HashSet<ShortcutKey> shortcuts = new HashSet<ShortcutKey>();

	private Expression inputExpression = null;
	private Expression outputExpression = null;

	private Object nextInputValue = null;

	private Document slideXml = null;
	private Slide current = null;
	private LayoutComponent viewport = null;


	public SlideManager(ProcessEventManager eventManager) {
		slideFactory = SlideFactory.getInstance(this);
		this.eventManager = eventManager;
	}

	public void addViewportEventListener(Class<? extends ViewportEvent> eventClass,	ViewportEventListener listener) {
		viewportEventManager.addListener(eventClass, listener);
	}
	
	private void buildSlide() {
		log.debug("buildSlide");
		clearSlideRelatives();
		if (current != null) {
			slideXml = SlideXmlFactory.buildSlideXml(current);
			slideFactory.createSlideControls();
			setInputValue(nextInputValue);
			fireEvent(new InitEvent(current));
		}
	}

	private void clearListeners() {
		viewportEventManager.removeAllListeners();
	}
	
	private void clearSlideRelatives() {
		log.debug("clearSlideRelatives");
		
		slideXml = null;
		viewport = null;
		
		components.clear();
		windows.clear();
		fields.clear();
		variables.clear();
		actions.clear();
		timers.clear();
		shortcuts.clear();
		
		inputExpression = null;
		outputExpression = null;
		
		clearListeners();
	}

	@Override
	public Slide current() {
		Slide slide = super.current();
		if (current != slide) {
			current = slide;

			buildSlide();
		}
		
		return current;
	}

	public void fireEvent(ViewportEvent event) {
		viewportEventManager.fireEvent(event);
	}

	/*public final ActionMap getActions() {
		return actions;
	}*/
	
	@Override
	public final void setAction(String id, AbstractBaseAction action) {
		actions.put(id, action);
	}
	
	@Override
	public final AbstractBaseAction getAction(String id) {
		return actions.get(id);
	}

	public final ProcessEventManager getEventManager() {
		return eventManager;
	}

	public final FieldList getFields() {
		return fields;
	}

	public final Object getOutputValue() {
		if (outputExpression != null) {
			outputExpression.setVariables(variables);
			return outputExpression.getValue();
		}
		return null;
	}

	public String getSerializedData() {
		Document doc = slideFactory.createSlideData();
		return XmlUtility.writeString(doc);
	}

	public String getSerializedOutputValue() {
		Document doc = slideFactory.createSlideOutput();
		return XmlUtility.writeString(doc);
	}

	public final Document getSlideXml() {
		return slideXml;
	}

	public final VariableMap getVariables() {
		return variables;
	}
	
	public final LayoutComponent getViewport() {
		return viewport;
	}
	
	@Override
	public Slide next() {
		// save output value for next slide
		nextInputValue = getOutputValue();
		Slide next = super.next();

		// there is not another next slide, then clear nextInputValue
		if (next == null)
			nextInputValue = null;

		return current();
	}

	protected final void setInputExpression(Expression expression) {
		this.inputExpression = expression;
	}

	@SuppressWarnings("rawtypes")
	private void setInputValue(Object value) {
		if (inputExpression != null && value != null) {
			String name = inputExpression.getSimpleVariableName();
			if (name != null) {
				Variable<?> variable = this.variables.get(name);
				if (variable != null)
					variable.setRawValue(value);
				else {
					variable = new Variable(name);
					variable.setRawValue(value);
					this.variables.put(variable);
				}
			}
		}
	}

	protected final void setOutputExpression(Expression expression) {
		this.outputExpression = expression;
	}

	public final void setViewport(LayoutComponent component) {
		this.viewport = component;
	}

	public final void registerComponent(String id, SlideComponent component) {
		if (!Strings.isNullOrEmpty(id)) {
			components.put(id, component);
		}
		if (component instanceof Field) {
			fields.add((Field) component);
		}
	}
	
	public final SlideComponent getComponent(String id) {
		return components.get(id);
	}
	
	public final void registerTimer(String id, Timer timer) {
		if (!Strings.isNullOrEmpty(id)) {
			timers.put(id, timer);
		}
	}
	
	public final Timer getTimer(String id) {
		return timers.get(id);
	}
	
	public final Collection<Timer> getTimers() {
		return timers.values();
	}
	
	public final void registerShortcutKey(ShortcutKey shortcutKey) {
		shortcuts.add(shortcutKey);
	}
	
	public Collection<ShortcutKey> getShortcutKeys() {
		return shortcuts;
	}

	public final void registerWindow(String id, Window window) {
		if (!Strings.isNullOrEmpty(id)) {
			windows.put(id, window);
		}
	}
	
	public final Window getWindow(String id) {
		return windows.get(id);
	}
	
	public final boolean hasValidFields() {
		boolean valid = true;
		
		for (Field field : fields) {
			valid = valid && field.isValid();
		}
		
		return valid;
	}

	public void addComponentDataVariable(AbstractComponentData<?> data) {
		Variable<?> variable = (Variable<?>)variables.get(SlideXmlConstants.COMPONENT_DATA);
		if (null == variable) {
			variable = new Variable<Object>(SlideXmlConstants.COMPONENT_DATA);
			variables.put(SlideXmlConstants.COMPONENT_DATA, variable);
		}
		variable.setRawValue(data);
	}
	
	public void clearComponentDataVariable() {
		Variable<?> variable = (Variable<?>)variables.get(SlideXmlConstants.COMPONENT_DATA);
		if (variable != null) {
			variable.setRawValue(null);
		}
	}

	public void finishSlide() {
		// stop timers silently
		for (String key : timers.keySet()) {
			Timer timer = timers.get(key);
			timer.stop(true);
		}
		
		// remove close listeners to close windows silently
		for (String key : windows.keySet()) {
			Window window = windows.get(key);
			window.removeAllCloseListeners();
			window.close();
		}
		
	}
}
