/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import org.hypothesis.builder.SlideContainerFactoryDeferred;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.AfterRenderContentEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.eventbus.HasProcessEventBus;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.interfaces.SlideManagementPresenter;
import org.hypothesis.slide.ui.Mask;
import org.hypothesis.ui.SlideContainer;
import org.hypothesis.ui.view.SlideManagementView;
import org.vaadin.alump.fancylayouts.FancyNotifications;
import org.vaadin.alump.fancylayouts.gwt.client.shared.FancyNotificationsState.Position;
import org.vaadin.johan.Toolbox;
import org.vaadin.johan.Toolbox.ORIENTATION;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideManagementPresenterImpl extends AbstractViewPresenter
		implements SlideManagementPresenter, HasProcessEventBus {

	private final DocumentReader reader = new XmlDocumentReader();

	private SlideContainer container;

	private final ProcessEventBus bus;
	private final SlideContainerFactoryDeferred factory = new SlideContainerFactoryDeferred();

	private Mask mask;
	private final FancyNotifications notifications = new FancyNotifications();

	public SlideManagementPresenterImpl() {
		 bus = new ProcessEventBus();
		SessionManager.setProcessEventBus(bus);
	}

	@Override
	public void attach() {
		bus.register(this);
	}

	@Override
	public void detach() {
		bus.unregister(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public View createView() {

		return new SlideManagementView(this);
	}

	@Override
	public void showSlide(String template, String content) {
		notifications.setCloseTimeout(5000);
		notifications.setPosition(Position.BOTTOM_RIGHT);

		container = factory.buildSlideContainer(template, content, reader);
		if (container != null) {
			container.getPresenter().buildDone();

			mask = Mask.addToComponent(container);
			mask.setColor("rgba(127,255,127,0.2)");
			mask.show();

			Window window = new Window("Slide preview");

			CssLayout topLayout = new CssLayout();
			topLayout.setSizeFull();

			VerticalLayout layout = new VerticalLayout();
			layout.setSizeFull();
			topLayout.addComponent(layout);

			Toolbox toolbox = new Toolbox();
			toolbox.setOrientation(ORIENTATION.TOP_CENTER);
			toolbox.setOverflowSize(5);

			HorizontalLayout hl = new HorizontalLayout();
			hl.setSpacing(true);
			Button startButton = new Button("Start slide", e -> {
				e.getButton().setEnabled(false);
				startClicked();
			});
			hl.addComponent(startButton);

			CssLayout tlLayout = new CssLayout(toolbox);
			tlLayout.setHeight("0px");
			topLayout.addComponent(tlLayout);

			toolbox.setContent(hl);

			topLayout.addComponent(notifications);

			layout.addComponent(container);
			layout.setExpandRatio(container, 1f);

			window.setContent(topLayout);
			window.setSizeFull();
			window.setClosable(true);

			UI.getCurrent().addWindow(window);
		}
	}

	private void startClicked() {
		mask.hide();

		if (container != null && container.getPresenter() instanceof SlideContainerPresenterDeferred) {
			SlideContainerPresenterDeferred containerPresenter = (SlideContainerPresenterDeferred) container
					.getPresenter();

			containerPresenter.fireDeferred();
		}
	}

	@Override
	public ProcessEventBus getBus() {
		return bus;
	}

	@Handler
	public void processActionEvent(ActionEvent event) {
		String title = event.getName() + " Id=" + event.getAction().getId();
		String detail = event.getAction().toString();
		notifications.showNotification(null, title, detail);
	}

	@Handler
	public void processAfterRender(AfterRenderContentEvent event) {
		notifications.showNotification(null, event.getName());
	}

	@Handler
	public void processComponentEvent(ComponentEvent event) {
		String title = event.getTypeName() + " Id=" + event.getData().getId();
		String detail = event.getData().getEventName();
		notifications.showNotification(null, title, detail);
	}

	@Handler
	public void processFinishSlide(FinishSlideEvent event) {
		mask.setColor("rgba(127,127,255,0.2)");
		mask.show();
		Notification.show("FINISH", Type.HUMANIZED_MESSAGE);
	}

}
