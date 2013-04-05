/**
 * 
 */
package org.hypothesis.application.collector.core;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.hypothesis.application.collector.ComponentMap;
import org.hypothesis.application.collector.QueueManager;
import org.hypothesis.application.collector.WindowMap;
import org.hypothesis.application.collector.evaluable.Expression;
import org.hypothesis.application.collector.events.ProcessEventManager;
import org.hypothesis.application.collector.events.ViewportEvent;
import org.hypothesis.application.collector.events.ViewportEventListener;
import org.hypothesis.application.collector.events.ViewportEventManager;
import org.hypothesis.application.collector.slide.ActionMap;
import org.hypothesis.application.collector.slide.HasActions;
import org.hypothesis.application.collector.slide.HasVariables;
import org.hypothesis.application.collector.slide.Variable;
import org.hypothesis.application.collector.slide.VariableMap;
import org.hypothesis.application.collector.ui.component.LayoutComponent;
import org.hypothesis.application.collector.ui.component.TimerLabel;
import org.hypothesis.application.collector.xml.SlideXmlFactory;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.Task;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideManager extends QueueManager<Task, Slide> implements
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

	private Expression inputExpression = null;

	private Expression outputExpression = null;

	private Object nextInputValue = null;
	private ComponentMap components = new ComponentMap();

	private List<Object> fields = new ArrayList<Object>();

	@SuppressWarnings("unused")
	private List<TimerLabel> timers = new ArrayList<TimerLabel>();

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

	public final ComponentMap getComponents() {
		return components;
	}

	public final ProcessEventManager getEventManager() {
		return eventManager;
	}

	public final List<Object> getFields() {
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
		return Utility.writeString(doc);
	}

	public String getSerializedOutputValue() {
		Document doc = SlideFactory.getInstatnce().createSlideOutput(this);
		return Utility.writeString(doc);
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

	public final WindowMap getWindows() {
		return windows;
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

}
