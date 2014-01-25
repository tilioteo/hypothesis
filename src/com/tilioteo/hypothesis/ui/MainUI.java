/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.servlet.HibernateServlet;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState.Direction;
import com.tilioteo.hypothesis.ui.Timer.StopEvent;
import com.tilioteo.hypothesis.ui.Timer.StopListener;
import com.tilioteo.hypothesis.ui.Timer.UpdateEvent;
import com.tilioteo.hypothesis.ui.Timer.UpdateListener;
import com.tilioteo.hypothesis.ui.view.PacksView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class MainUI extends HUI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateServlet {
	}

	private Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Hypothesis");

		/*navigator = new Navigator(this, this);

		navigator.addView("/packs", PacksView.class);

		navigator.navigateTo("/packs");*/
		
		VerticalLayout verticalLayout = new VerticalLayout();
		setContent(verticalLayout);
		
		Timer timer = new Timer();
		addTimer(timer);
		timer.setDirection(Direction.DOWN);
		
		TimerLabel timerLabel = new TimerLabel();
		//timerLabel.setTimeFormat("kk:mm:ss.S");
		timerLabel.setTimer(timer);
		
		verticalLayout.addComponent(timerLabel);
		
		timer.addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				Notification.show("Time out!", Type.HUMANIZED_MESSAGE);
			}
		});
		
		timer.addUpdateListener(2000, new UpdateListener() {
			@Override
			public void update(UpdateEvent event) {
				Notification.show(String.format("%d miliseconds, interval %d ms", event.getTime(), event.getInterval()));
			}
		});
		
		/*timer.addUpdateListener(5000, new UpdateListener() {
			@Override
			public void update(UpdateEvent event) {
				Notification.show(String.format("%d miliseconds, interval %d ms", event.getTime(), event.getInterval()));
			}
		});*/
		
		timer.start(30000);

	}

}