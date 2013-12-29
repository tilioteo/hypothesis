/**
 * 
 */
package org.hypothesis.application.collector.ui;

import org.hypothesis.application.CollectorApplication;
import org.hypothesis.application.collector.events.AbstractNotificationEvent;
import org.hypothesis.application.collector.events.AbstractProcessEvent;
import org.hypothesis.application.collector.events.AbstractRunningEvent;
import org.hypothesis.application.collector.events.AfterRenderContentEvent;
import org.hypothesis.application.collector.events.ErrorTestEvent;
import org.hypothesis.application.collector.events.FinishTestEvent;
import org.hypothesis.application.collector.events.ProcessEventListener;
import org.hypothesis.application.collector.events.ProcessEventManager;
import org.hypothesis.application.collector.events.RenderContentEvent;
import org.hypothesis.application.collector.ui.component.LayoutComponent;
import org.hypothesis.common.application.ui.AbstractMainWindow;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class MainWindow extends AbstractMainWindow<CollectorApplication> implements
		ProcessEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1056825527006682809L;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            - application
	 */
	public MainWindow(final CollectorApplication application) {
		super(application);
	}

	@Override
	protected Component createContent() {
		// default main layout
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSizeFull();
		return mainLayout;
	}

	public void handleEvent(AbstractProcessEvent event) {
		if (event instanceof RenderContentEvent) {
			removeAllComponents();

			LayoutComponent content = ((RenderContentEvent) event).getContent();
			// set slide component to window content
			// Alignment is ignored here
			if (content != null && content.getComponent() != null) {
				Component component = content.getComponent();
				setWindowContent(component);
				ProcessEventManager.get(getApplication()).fireEvent(
						new AfterRenderContentEvent(content));
			} else {
				ProcessEventManager.get(getApplication()).fireEvent(
						new ErrorTestEvent());
			}

		} else if (event instanceof FinishTestEvent) {
			removeAllComponents();

			setWindowContent(new FinishScreen(
					((FinishTestEvent) event).getTest()));

		} else if (event instanceof AbstractNotificationEvent) {
			showNotification(((AbstractNotificationEvent) event)
					.getNotification());
		}
	}

	@Override
	protected void init() {
		// TODO change caption
		setCaption(ApplicationMessages.get()
				.getString(Messages.TEXT_APP_TITLE/* TEXT_COLLECTOR_APP_TITLE */));
		ProcessEventManager.get(getApp()).addListener(this,
				RenderContentEvent.class, AbstractRunningEvent.class,
				AbstractNotificationEvent.class);
	}

	private void setWindowContent(Component component) {
		if (component instanceof ComponentContainer) {
			if (component.getParent() != null && component.getParent() != this)
				component.setParent(null);
			setContent((ComponentContainer) component);
		} else {
			addComponent(component);
		}
	}

}
