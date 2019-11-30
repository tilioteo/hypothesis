/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import net.engio.mbassy.listener.Handler;
import org.hypothesis.event.model.EventQueue;
import org.hypothesis.event.model.EventWrapper;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ComponentEventCallback;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class SlideContainerPresenterDeferred extends SlideContainerPresenter {

    private final EventQueue eventQueue;
    private boolean disableDeferred = false;

    public SlideContainerPresenterDeferred(EventQueue eventQueue) {
        super();

        this.eventQueue = eventQueue;
    }

    @Override
    public void handleEvent(Component component, String typeName, String eventName, Action action,
                            ComponentEventCallback callback) {

        if (!disableDeferred) {
            if (eventQueue != null) {
                eventQueue.add(new EventWrapper(component, typeName, eventName, action, callback));
            }
        } else {
            getEventManager().handleEvent(component, typeName, eventName, action, callback);
        }
    }

    public void fireDeferred() {
        if (!disableDeferred) {
            if (eventQueue != null) {
                for (EventWrapper eventWrapper : eventQueue.getList()) {
                    getEventManager().handleEvent(eventWrapper.component, eventWrapper.typeName, eventWrapper.eventName,
                            eventWrapper.action, eventWrapper.callback);
                }

                eventQueue.clear();
            }

            disableDeferred = true;
        }
    }

    @Override
    public void attach(Component component, HasComponents parent, UI ui, VaadinSession session) {
        super.attach(component, parent, ui, session);

        getBus().register(this);
    }

    @Override
    public void detach(Component component, HasComponents parent, UI ui, VaadinSession session) {
        getBus().unregister(this);

        super.detach(component, parent, ui, session);
    }

    @Handler
    public void processFinishSlide(FinishSlideEvent event) {
        viewDone();
    }
}
