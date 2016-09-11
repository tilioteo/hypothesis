/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hypothesis.business.EventManager;
import org.hypothesis.business.MessageManager;
import org.hypothesis.business.ObjectConstants;
import org.hypothesis.business.SlideDocument;
import org.hypothesis.business.SlideNavigator;
import org.hypothesis.evaluation.AbstractBaseAction;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.Message;
import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.MessageEvent;
import org.hypothesis.event.model.MessageEventManager;
import org.hypothesis.event.model.ViewportEvent;
import org.hypothesis.event.model.ViewportEventManager;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.interfaces.Evaluator;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.Field;
import org.hypothesis.interfaces.MessageEventListener;
import org.hypothesis.interfaces.SlidePresenter;
import org.hypothesis.interfaces.Variable;
import org.hypothesis.interfaces.ViewportEventListener;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.servlet.BroadcastService.BroadcastListener;
import org.hypothesis.slide.ui.Window;
import org.hypothesis.ui.HypothesisUI;
import org.hypothesis.ui.SlideContainer;
import org.vaadin.special.ui.KeyAction;
import org.vaadin.special.ui.Timer;

import com.vaadin.server.Extension;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideContainerPresenter implements SlidePresenter, Evaluator, BroadcastListener {

	public static final String COMPONENT_DATA = "ComponentData";

	private SlideContainer container;

	private ProcessEventBus bus = null;
	private HypothesisUI ui = null;

	private final ViewportEventManager viewportEventManager = new ViewportEventManager();
	private final MessageEventManager messageEventManager = new MessageEventManager();

	private final HashMap<String, Component> components = new HashMap<>();
	private final HashMap<String, Field> fields = new HashMap<>();
	private final HashMap<String, Window> windows = new HashMap<>();
	private final HashMap<String, Timer> timers = new HashMap<>();

	private final HashMap<String, Variable<?>> variables = new HashMap<>();
	private final HashMap<String, Action> actions = new HashMap<>();

	private HashSet<KeyAction> keyActions = new HashSet<>();

	private final HashMap<Integer, ExchangeVariable> inputExpressions = new HashMap<>();
	private final HashMap<Integer, ExchangeVariable> outputExpressions = new HashMap<>();

	private final EventManager eventManager;
	private MessageManager messageManager = null;

	private Long userId = null;

	/**
	 * Constructor
	 */
	public SlideContainerPresenter() {
		eventManager = new EventManager(this);
		messageManager = new MessageManager();
	}

	/**
	 * Clear state
	 */
	public void clear() {
		container = null;

		viewportEventManager.removeAllListeners();
		messageEventManager.removeAllListeners();

		components.clear();
		fields.clear();
		windows.clear();
		timers.clear();

		variables.clear();
		actions.clear();

		keyActions.clear();

		inputExpressions.clear();
		outputExpressions.clear();
	}

	@Override
	public void attach(Component component, HasComponents parent, UI ui, VaadinSession session) {
		if (component instanceof SlideContainer) {
			viewportEventManager.setEnabled(true);
			messageEventManager.setEnabled(true);

			setProcessEventBus(ProcessEventBus.get(ui));

			fireEvent(new ViewportEvent.Init(container));

			if (ui instanceof HypothesisUI) {
				this.ui = (HypothesisUI) ui;

				addWindows(ui);
				addTimers(this.ui);
				addKeyActions(ui);

				BroadcastService.register(this);
			}
			fireEvent(new ViewportEvent.Show(component));
		}
	}

	private void addWindows(UI ui) {
		for (Window window : windows.values()) {
			window.setFutureUI(ui);
		}
	}

	private void addTimers(HypothesisUI hui) {
		for (Timer timer : timers.values()) {
			hui.addTimer(timer);
		}
	}

	private void addKeyActions(AbstractComponent component) {
		for (KeyAction keyAction : keyActions) {
			keyAction.extend(component);
		}
	}

	@Override
	public void detach(Component component, HasComponents parent, UI ui, VaadinSession session) {
		bus = null;
		this.ui = null;

		viewportEventManager.setEnabled(false);
		messageEventManager.setEnabled(false);

		BroadcastService.unregister(this);

		if (ui instanceof HypothesisUI) {
			HypothesisUI hui = (HypothesisUI) ui;
			hui.removeAllTimers();
			removeKeyActions();
			removeWindows();
		}
	}

	private void removeWindows() {
		for (Window window : windows.values()) {
			window.setFutureUI(null);
		}
	}

	private void removeKeyActions() {
		for (KeyAction keyAction : keyActions) {
			keyAction.remove();
		}
	}

	private void stopTimers() {
		// stop timers silently
		for (Timer timer : timers.values()) {
			timer.stop(true);
		}
	}

	private void closeWindows() {
		// remove close listeners to close windows silently
		for (Window window : windows.values()) {
			window.removeAllCloseListeners();
			window.close();
		}
	}

	@Override
	public Map<String, Variable<?>> getVariables() {
		return variables;
	}

	/**
	 * Post event to bus
	 * 
	 * @param event
	 */
	public void fireEvent(ProcessEvent event) {
		if (bus != null) {
			bus.post(event);
		}
	}

	/**
	 * Post event to bus
	 * 
	 * @param event
	 */
	public void fireEvent(ViewportEvent event) {
		viewportEventManager.fireEvent(event);
	}

	/**
	 * Post event to bus
	 * 
	 * @param event
	 */
	public synchronized void fireEvent(MessageEvent event) {
		messageEventManager.fireEvent(event);
	}

	public void setComponentData(ComponentData data) {
		Variable<?> variable = variables.get(COMPONENT_DATA);
		if (data != null) {
			if (null == variable) {
				variable = new org.hypothesis.evaluation.Variable<>(COMPONENT_DATA);
				variables.put(COMPONENT_DATA, variable);
			}
			variable.setRawValue(data);
		} else {
			variables.remove(COMPONENT_DATA);
		}

	}

	private Command createActionCommand(final Action action) {
		return new Command() {
			@Override
			public void execute() {
				if (bus != null) {
					bus.post(new ActionEvent(action));
				}
			}
		};
	}

	@Override
	public void setAction(String id, Action action) {
		if (action != null) {
			if (action instanceof AbstractBaseAction) {
				((AbstractBaseAction) action).setExecuteCommand(createActionCommand(action));
			}
			actions.put(id, action);
		}
	}

	@Override
	public Action getAction(String id) {
		return actions.get(id);
	}

	/**
	 * Register timer
	 * 
	 * @param id
	 * @param timer
	 */
	public void setTimer(String id, Timer timer) {
		if (timer != null) {
			timers.put(id, timer);
		}
	}

	/**
	 * Register window
	 * 
	 * @param id
	 * @param window
	 */
	public void setWindow(String id, Window window) {
		if (window != null) {
			windows.put(id, window);

			if (ui != null) {
				window.setFutureUI(ui);
			}
		}
	}

	@Override
	public void setComponent(String id, Component component) {
		if (component != null) {
			components.put(id, component);

			if (component instanceof Field) {
				fields.put(id, (Field) component);
			}
		}
	}

	/**
	 * Set input
	 * 
	 * @param id
	 * @param expression
	 */
	public void setInputExpression(int id, IndexedExpression expression) {
		if (expression != null) {
			inputExpressions.put(id, expression);
		}
	}

	/**
	 * Set output
	 * 
	 * @param id
	 * @param expression
	 */
	public void setOutputExpression(int id, IndexedExpression expression) {
		if (expression != null) {
			outputExpressions.put(id, expression);
		}
	}

	/**
	 * Get component by id
	 */
	public Component getComponent(String id) {
		return components.get(id);
	}

	/**
	 * Get timer by id
	 * 
	 * @param id
	 * @return
	 */
	public Component getTimer(String id) {
		return timers.get(id);
	}

	/**
	 * Get window by id
	 * 
	 * @param id
	 * @return
	 */
	public Component getWindow(String id) {
		return windows.get(id);
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void addViewportInitListener(ViewportEventListener listener) {
		viewportEventManager.addListener(ViewportEvent.Init.class, listener);
	}

	@Override
	public void addViewportShowListener(ViewportEventListener listener) {
		viewportEventManager.addListener(ViewportEvent.Show.class, listener);
	}

	@Override
	public void addViewportFinishListener(ViewportEventListener listener) {
		viewportEventManager.addListener(ViewportEvent.Finish.class, listener);
	}

	@Override
	public void addKeyAction(Extension keyAction) {
		if (keyAction != null && keyAction instanceof KeyAction) {
			keyActions.add((KeyAction) keyAction);
		}
	}

	@Override
	public synchronized void addMessageListener(String uid, MessageEventListener listener) {
		messageEventManager.addListener(uid, listener);
	}

	public void setSlideContainer(SlideContainer container) {
		this.container = container;
	}

	public SlideContainer getSlideContainer() {
		return container;
	}

	private void addDocumentVariable() {
		Variable<Object> variable = new org.hypothesis.evaluation.Variable<Object>(ObjectConstants.DOCUMENT,
				new SlideDocument(this));

		variables.put(variable.getName(), variable);
	}

	public boolean isValidSlide() {
		boolean valid = true;

		// validate fields
		for (Field field : fields.values()) {
			valid = valid && field.isValid();
		}

		return valid;
	}

	/**
	 * Create new message by uid
	 * 
	 * @param uid
	 * @return
	 */
	public Message createMessage(String uid) {
		return messageManager.createMessage(uid, userId);
	}

	/**
	 * Broadcast message
	 * 
	 * @param message
	 */
	public void postMessage(String message) {
		BroadcastService.broadcastExcept(this, message);
	}

	@Override
	public void receiveBroadcast(final String event) {
		if (ui != null && ui.getSession() != null) { // prevent from detached ui

			// deserialize received message
			final Message message = Message.fromJson(event);

			if (message != null) {
				Long receiverId = message.getReceiverId();

				if (null == userId || null == receiverId || receiverId.equals(userId)) {
					// ok - receive this message
					ui.access(new Runnable() {
						@Override
						public void run() {
							fireEvent(new MessageEvent(message));
							if (PushMode.MANUAL == ui.getPushConfiguration().getPushMode()) {
								try {
									ui.push();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		}
	}

	@Override
	public void buildDone() {
		addNavigatorVariable();
		addDocumentVariable();
	}

	private void addNavigatorVariable() {
		Variable<Object> variable = new org.hypothesis.evaluation.Variable<Object>(ObjectConstants.NAVIGATOR,
				new SlideNavigator(this));

		variables.put(variable.getName(), variable);
	}

	@Override
	public void viewDone() {
		stopTimers();
		closeWindows();

		fireEvent(new ViewportEvent.Finish(container));
	}

	@Override
	public Map<Integer, ExchangeVariable> getOutputs() {
		for (ExchangeVariable variable : outputExpressions.values()) {
			variable.setVariables(variables);
		}

		return outputExpressions;
	}

	@Override
	public void handleEvent(Component component, String typeName, String eventName, Action action,
			ComponentEventCallback callback) {
		eventManager.handleEvent(component, typeName, eventName, action, callback);
	}

	@Override
	public Map<Integer, ExchangeVariable> getInputs() {
		return inputExpressions;
	}

	@Override
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public Map<String, Field> getFields() {
		return fields;
	}

	@Override
	public String getSlideId() {
		if (container != null && container.getData() != null) {
			return container.getData().toString();
		}

		return null;
	}

	protected void setProcessEventBus(ProcessEventBus bus) {
		this.bus = bus;
	}

}
