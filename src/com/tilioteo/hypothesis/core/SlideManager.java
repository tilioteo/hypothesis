/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Collection;

import org.dom4j.Document;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.event.ProcessEventManager;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.tilioteo.hypothesis.event.ViewportEventManager;
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
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.tilioteo.hypothesis.ui.Timer;
import com.tilioteo.hypothesis.ui.Window;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideManager extends ListManager<Task, Slide> implements
		HasVariables, HasActions {

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

	private ProcessEventManager eventManager;
	private SlideFactory slideFactory;

	private Document slideXml = null;
	private Slide lastSlide = null;
	private LayoutComponent viewport = null;
	private WindowMap windows = new WindowMap();

	private VariableMap variables = new VariableMap();
	private ActionMap actions = new ActionMap();
	private TimerMap timers = new TimerMap();

	private Expression inputExpression = null;
	private Expression outputExpression = null;

	private Object nextInputValue = null;
	private ComponentMap components = new ComponentMap();

	private FieldList fields = new FieldList();

	ViewportEventManager viewportEventManager = new ViewportEventManager();

	public SlideManager(ProcessEventManager eventManager) {
		slideFactory = SlideFactory.getInstatnce();
		this.eventManager = eventManager;
	}

	public void addViewportEventListener(
			Class<? extends ViewportEvent> eventClass,
			ViewportEventListener listener) {
		viewportEventManager.addListener(eventClass, listener);
	}
	
	

	@Override
	public void setListParent(Task parent) {
		super.setListParent(parent);
	}

	private void buildSlide() {
		Slide slide = super.current();
		if (lastSlide != slide) {
			if (slide != null) {
				this.slideXml = SlideXmlFactory.buildSlideXml(slide);
				slideFactory.createSlideControls(this);
				setInputValue(this.nextInputValue);
				fireEvent(new InitEvent(slide));
			}
			lastSlide = slide;
		}
	}

	private void clearListeners() {
		viewportEventManager.removeAllListeners();
	}

	private void clearSlideRelatives() {
		this.slideXml = null;
		this.viewport = null;
		this.components.clear();
		this.windows.clear();
		this.fields.clear();
		this.variables.clear();
		this.actions.clear();
		this.timers.clear();
		this.inputExpression = null;
		this.outputExpression = null;
		this.lastSlide = null;
		clearListeners();
	}

	@Override
	public Slide current() {
		buildSlide();
		return super.current();
	}

	public void fireEvent(ViewportEvent event) {
		viewportEventManager.fireEvent(event);
	}

	public final ActionMap getActions() {
		return actions;
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
		Document doc = SlideFactory.getInstatnce().createSlideData(this);
		return XmlUtility.writeString(doc);
	}

	public String getSerializedOutputValue() {
		Document doc = SlideFactory.getInstatnce().createSlideOutput(this);
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
		clearSlideRelatives();
		Slide nextSlide = super.next();

		// there is not next slide, then clear nextInputValue
		if (nextSlide == null)
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
	
}
