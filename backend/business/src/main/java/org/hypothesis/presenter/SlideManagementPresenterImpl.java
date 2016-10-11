/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;

import org.hypothesis.builder.SlideContainerFactoryDeferred;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.event.model.ActionEvent;
import org.hypothesis.event.model.AfterRenderContentEvent;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.interfaces.SlideManagementPresenter;
import org.hypothesis.slide.ui.Mask;
import org.hypothesis.ui.SlideContainer;
import org.vaadin.alump.fancylayouts.FancyNotifications;
import org.vaadin.alump.fancylayouts.gwt.client.shared.FancyNotificationsState.Position;
import org.vaadin.johan.Toolbox;
import org.vaadin.johan.Toolbox.ORIENTATION;

import com.vaadin.cdi.NormalViewScoped;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalViewScoped
public class SlideManagementPresenterImpl implements SlideManagementPresenter {

	private final DocumentReader reader = new XmlDocumentReader();

	private SlideContainer container;

	private final SlideContainerFactoryDeferred factory;

	private Mask mask;
	private final FancyNotifications notifications = new FancyNotifications();

	public SlideManagementPresenterImpl() {
		System.out.println("Construct " + getClass().getName());
		factory = new SlideContainerFactoryDeferred();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// nop
	}

	/*
	 * @Override public View createView() {
	 * 
	 * return new SlideManagementView(); }
	 */

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
			Button startButton = new Button("Start slide", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					event.getButton().setEnabled(false);
					startClicked();
				}
			});
			hl.addComponent(startButton);

			CssLayout tlLayout = new CssLayout(toolbox);
			tlLayout.setHeight("0px");
			topLayout.addComponent(tlLayout);
			// layout.setExpandRatio(tlLayout, 0f);

			toolbox.setContent(hl);

			topLayout.addComponent(notifications);

			/*
			 * sliderPanel = new SliderPanelBuilder(new Button("Start slide",
			 * new ClickListener() {
			 * 
			 * @Override public void buttonClick(ClickEvent event) {
			 * event.getButton().setEnabled(false); startClicked(); }
			 * })).expanded(false).mode(SliderMode.TOP).caption("Tools").
			 * autoCollapseSlider(true)
			 * .tabPosition(SliderTabPosition.BEGINNING).build();
			 * 
			 * layout.addComponent(sliderPanel);
			 * layout.setExpandRatio(sliderPanel, 0f);
			 */

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
		// sliderPanel.collapse();

		if (container != null && container.getPresenter() instanceof SlideContainerPresenterDeferred) {
			SlideContainerPresenterDeferred containerPresenter = (SlideContainerPresenterDeferred) container
					.getPresenter();

			containerPresenter.fireDeferred();
		}
	}

	/**
	 * Do on action event
	 * 
	 * @param event
	 */
	public void processActionEvent(@Observes ActionEvent event) {
		String title = event.getName() + " Id=" + event.getAction().getId();
		String detail = event.getAction().toString();
		notifications.showNotification(null, title, detail);
	}

	/**
	 * Do after render
	 * 
	 * @param event
	 */
	public void processAfterRender(@Observes AfterRenderContentEvent event) {
		notifications.showNotification(null, event.getName());
	}

	/**
	 * Do on component event
	 * 
	 * @param event
	 */
	public void processComponentEvent(@Observes ComponentEvent event) {
		String title = event.getTypeName() + " Id=" + event.getData().getId();
		String detail = event.getData().getEventName();
		notifications.showNotification(null, title, detail);
	}

	/**
	 * Do on finish slide
	 * 
	 * @param event
	 */
	public void processFinishSlide(@Observes FinishSlideEvent event) {
		mask.setColor("rgba(127,127,255,0.2)");
		mask.show();
		Notification.show("FINISH", Type.HUMANIZED_MESSAGE);
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
	}

}
