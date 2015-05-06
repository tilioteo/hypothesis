/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.vaadin.special.ui.ShortcutKey;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlFactory;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.event.ViewportEvent;
import com.tilioteo.hypothesis.event.ViewportEventListener;
import com.tilioteo.hypothesis.event.ViewportEventManager;
import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Field;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.ActionMap;
import com.tilioteo.hypothesis.processing.ComponentMap;
import com.tilioteo.hypothesis.processing.FieldMap;
import com.tilioteo.hypothesis.processing.ExchangeVariableMap;
import com.tilioteo.hypothesis.processing.TimerMap;
import com.tilioteo.hypothesis.processing.Variable;
import com.tilioteo.hypothesis.processing.VariableMap;
import com.tilioteo.hypothesis.processing.WindowMap;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.Window;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideManager extends ListManager<Task, Slide> implements SlideFascia {
	
	private static Logger log = Logger.getLogger(SlideFascia.class);

	private ViewportEventManager viewportEventManager = new ViewportEventManager();

	private SlideFactory slideFactory;

	private FieldMap fields = new FieldMap();
	private ComponentMap components = new ComponentMap();
	private WindowMap windows = new WindowMap();
	private TimerMap timers = new TimerMap();
	private VariableMap variables = new VariableMap();
	private ActionMap actions = new ActionMap();
	private HashSet<ShortcutKey> shortcuts = new HashSet<ShortcutKey>();
	private ExchangeVariableMap outputValues = new ExchangeVariableMap(); 
	private ExchangeVariableMap inputValues = new ExchangeVariableMap(); 
	private HashMap<Integer, Object> nextInputValues = new HashMap<Integer, Object>();

	private Document slideXml = null;
	private Slide current = null;
	private Component viewportComponent = null;


	public SlideManager() {
		slideFactory = SlideFactory.getInstance(this);
	}

	@Override
	public void addViewportInitListener(ViewportEventListener listener) {
		viewportEventManager.addListener(ViewportEvent.Init.class, listener);
	}
	
	@Override
	public void addViewportShowListener(ViewportEventListener listener) {
		viewportEventManager.addListener(ViewportEvent.Show.class, listener);
	}
	
	private void buildSlide() {
		log.debug("buildSlide");
		clearSlideRelatives();
		if (current != null) {
			slideXml = SlideXmlFactory.buildSlideXml(current);
			slideFactory.createSlideControls();
			setInputValues();
			fireEvent(new ViewportEvent.Init(current));
		}
	}

	private void clearListeners() {
		viewportEventManager.removeAllListeners();
	}
	
	private void clearSlideRelatives() {
		log.debug("clearSlideRelatives");
		
		slideXml = null;
		viewportComponent = null;
		
		components.clear();
		windows.clear();
		fields.clear();
		variables.clear();
		actions.clear();
		timers.clear();
		shortcuts.clear();
		outputValues.clear();
		inputValues.clear();
		
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

	@Override
	public final void setAction(String id, Action action) {
		actions.put(id, action);
	}
	
	@Override
	public final Action getAction(String id) {
		return actions.get(id);
	}

	@Override
	public final FieldMap getFields() {
		return fields;
	}

	@Override
	public final Map<Integer, ExchangeVariable> getInputs() {
		return inputValues;
	}

	@Override
	public final Map<Integer, ExchangeVariable> getOutputs() {
		outputValues.setVariables(variables);
		return outputValues;
	}

	public String getSerializedSlideData() {
		Document doc = slideFactory.createSlideData();
		return XmlUtility.writeString(doc);
	}

	@Override
	public final Document getSlideXml() {
		return slideXml;
	}

	@Override
	public final VariableMap getVariables() {
		return variables;
	}
	
	@Override
	public final Component getViewportComponent() {
		return viewportComponent;
	}
	
	@Override
	public Slide next() {
		// save output value for next slide
		saveOutputValuesForNext();
		Slide next = super.next();

		// there is not another next slide, then clear nextInputValue
		if (next == null) {
			nextInputValues.clear();
		}

		return current();
	}

	private void saveOutputValuesForNext() {
		nextInputValues.clear();
		
		Map<Integer, ExchangeVariable> map = getOutputs();
		for (Integer index : map.keySet()) {
			ExchangeVariable outputValueExpression = outputValues.get(index);
			Object value = outputValueExpression.getValue();
			if (value != null) {
				nextInputValues.put(index, value);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void setInputValues() {
		for (ExchangeVariable inputValueExpression : inputValues.values()) {
			if (inputValueExpression != null && inputValueExpression instanceof IndexedExpression) {
				IndexedExpression inputExpression = (IndexedExpression)inputValueExpression;
				if (inputExpression.getExpression() != null) {
					int index = inputExpression.getIndex();
					String name = inputExpression.getExpression().getSimpleVariableName();
					Object value = nextInputValues.get(index);
					if (name != null && value != null) {
						com.tilioteo.hypothesis.interfaces.Variable<?> variable = this.variables.get(name);
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
		}
	}

	@Override
	public final void setViewportComponent(Component component) {
		this.viewportComponent = component;
	}

	@Override
	public final void registerComponent(String id, Component component) {
		if (!Strings.isNullOrEmpty(id)) {
			if (component instanceof Window) {
				windows.put(id, (Window) component);
			} else if (component instanceof Timer) {
				timers.put(id, (Timer) component);
			} else if (component instanceof SlideComponent) {
				components.put(id, (SlideComponent) component);
			}

			if (component instanceof Field) {
				fields.put(id, (Field) component);
			}
		} else if (component instanceof ShortcutKey) {
			shortcuts.add((ShortcutKey) component);
		}
	}
	
	@Override
	public final SlideComponent getComponent(String id) {
		return components.get(id);
	}
	
	@Override
	public final Timer getTimer(String id) {
		return timers.get(id);
	}
	
	public final Collection<Timer> getTimers() {
		return timers.values();
	}
	
	public Collection<ShortcutKey> getShortcutKeys() {
		return shortcuts;
	}

	@Override
	public final Window getWindow(String id) {
		return windows.get(id);
	}
	
	@Override
	public final boolean hasValidFields() {
		boolean valid = true;
		
		for (Field field : fields.values()) {
			valid = valid && field.isValid();
		}
		
		return valid;
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
	
	@Override
	public Slide getSlide() {
		return current;
	}

}
