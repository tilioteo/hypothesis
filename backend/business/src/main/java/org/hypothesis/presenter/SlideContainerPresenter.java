/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.Extension;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import org.hypothesis.business.*;
import org.hypothesis.evaluation.AbstractBaseAction;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.Message;
import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.event.model.*;
import org.hypothesis.eventbus.HasProcessEventBus;
import org.hypothesis.interfaces.*;
import org.hypothesis.push.Pushable;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.slide.ui.Window;
import org.hypothesis.ui.HypothesisUI;
import org.hypothesis.ui.SlideContainer;
import org.vaadin.special.ui.KeyAction;
import org.vaadin.special.ui.Timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class SlideContainerPresenter implements SlidePresenter, Evaluator, Broadcaster, Broadcaster.Listener, Pushable, HasProcessEventBus {

    public static final String COMPONENT_DATA = "ComponentData";
    private final ViewportEventManager viewportEventManager = new ViewportEventManager();
    private final MessageEventManager messageEventManager = new MessageEventManager();
    private final HashMap<String, Component> components = new HashMap<>();
    private final HashMap<String, Field> fields = new HashMap<>();
    private final HashMap<String, Window> windows = new HashMap<>();
    private final HashMap<String, Timer> timers = new HashMap<>();
    private final HashMap<String, Variable<?>> variables = new HashMap<>();
    private final HashMap<String, Action> actions = new HashMap<>();
    private final HashSet<KeyAction> keyActions = new HashSet<>();
    private final HashMap<Integer, ExchangeVariable> inputExpressions = new HashMap<>();
    private final HashMap<Integer, ExchangeVariable> outputExpressions = new HashMap<>();
    private final HashMap<Integer, ExchangeVariable> scoreExpressions = new HashMap<>();
    private final EventManager eventManager;
    private SlideContainer container;
    private HypothesisUI ui = null;
    private final MessageManager messageManager;

    private Long userId = null;

    public SlideContainerPresenter() {
        eventManager = new EventManager(this);
        messageManager = new MessageManager();
    }

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
        scoreExpressions.clear();
    }

    @Override
    public void attach(Component component, HasComponents parent, UI ui, VaadinSession session) {
        if (component instanceof SlideContainer) {
            viewportEventManager.setEnabled(true);
            messageEventManager.setEnabled(true);

            fireEvent(new ViewportEvent.Init(container));

            if (ui instanceof HypothesisUI) {
                this.ui = (HypothesisUI) ui;

                addWindows(ui);
                addTimers(this.ui);
                addKeyActions(ui);

                listenBroadcasting();
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
        this.ui = null;

        viewportEventManager.setEnabled(false);
        messageEventManager.setEnabled(false);

        unlistenBroadcasting();

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

    public void fireEvent(ProcessEvent event) {
        getBus().post(event);
    }

    public void fireEvent(ViewportEvent event) {
        viewportEventManager.fireEvent(event);
    }

    public void fireEvent(MessageEvent event) {
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
        return () -> getBus().post(new ActionEvent(action));
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

    public void setTimer(String id, Timer timer) {
        if (timer != null) {
            timers.put(id, timer);
        }
    }

    public void setWindow(String id, Window window) {
        if (window != null) {
            windows.put(id, window);

            if (ui != null) {
                window.setFutureUI(ui);
            }
        }
    }

    public void setComponent(String id, Component component) {
        if (component != null) {
            components.put(id, component);

            if (component instanceof Field) {
                fields.put(id, (Field) component);
            }
        }
    }

    public void setInputExpression(int id, IndexedExpression expression) {
        if (expression != null) {
            inputExpressions.put(id, expression);
        }
    }

    public void setOutputExpression(int id, IndexedExpression expression) {
        if (expression != null) {
            outputExpressions.put(id, expression);
        }
    }

    public void setScoreExpression(int id, IndexedExpression expression) {
        if (expression != null) {
            scoreExpressions.put(id, expression);
        }
    }

    public Component getComponent(String id) {
        return components.get(id);
    }

    public Component getTimer(String id) {
        return timers.get(id);
    }

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
        if (keyAction instanceof KeyAction) {
            keyActions.add((KeyAction) keyAction);
        }
    }

    public synchronized void addMessageListener(String uid, MessageEventListener listener) {
        messageEventManager.addListener(uid, listener);
    }

    public SlideContainer getSlideContainer() {
        return container;
    }

    public void setSlideContainer(SlideContainer container) {
        this.container = container;
    }

    private void addDocumentVariable() {
        Variable<Object> variable = new org.hypothesis.evaluation.Variable<>(ObjectConstants.DOCUMENT,
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

    public Message createMessage(String uid) {
        return messageManager.createMessage(uid, userId);
    }

    public void postMessage(String message) {
        broadcastOthers(message);
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
                    pushCommand(ui, () -> fireEvent(new MessageEvent(message)));
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
        Variable<Object> variable = new org.hypothesis.evaluation.Variable<>(ObjectConstants.NAVIGATOR,
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
    public Map<Integer, ExchangeVariable> getScores() {
        for (ExchangeVariable variable : scoreExpressions.values()) {
            variable.setVariables(variables);
        }

        return scoreExpressions;
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
    public HashMap<String, Field> getFields() {
        return fields;
    }

    @Override
    public String getSlideId() {
        if (container != null && container.getData() != null) {
            return container.getData().toString();
        }

        return null;
    }

}
