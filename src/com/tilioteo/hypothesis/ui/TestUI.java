/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import com.tilioteo.hypothesis.broadcast.Broadcaster;
import com.tilioteo.hypothesis.broadcast.Broadcaster.BroadcastListener;
import com.tilioteo.hypothesis.slide.ui.VerticalLayout;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

/**
 * @author kamil
 *
 */
@SuppressWarnings({ "serial" })
@Theme("hypothesis")
@Push
public class TestUI extends HUI implements BroadcastListener /*HasEventBus*/ {

	@WebServlet(value = "/test/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TestUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	MBassador<TestEvent> eventBus = new MBassador<TestEvent>();
	
	@Override
	protected void init(VaadinRequest request) {
		super.init(request);

		/*
		HttpSession httpSession = ((WrappedHttpSession)request.getWrappedSession()).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		
		config = (HypothesisConfig)context.getBean(HypothesisConfig.class);
		
		config.getSecretKey();*/
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		setContent(verticalLayout);
		
		
		Broadcaster.register(this);
		
		Button button = new Button("Broadcast", new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				Broadcaster.broadcastExcept(TestUI.this, "Button pushed.");
			}
		});
		
		verticalLayout.addComponent(button);
		
	}

	@Handler
	public void onTestEvent(TestEvent event) {
		getUI().access(new Runnable() {
			@Override
			public void run() {
				Notification.show("Broadcast event recieved");
			}
		});
	}

	public static class TestEvent {
		
	}

	@Override
	public void receiveBroadcast(final String message) {
		// Must lock the session to execute logic safely
		access(new Runnable() {
			@Override
			public void run() {
				Notification.show("Broadcast event recieved: " + message);
			}
		});
	}
	
	@Override
	public void detach() {
		Broadcaster.unregister(this);
		super.detach();
	}
}
