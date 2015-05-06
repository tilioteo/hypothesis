/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.tilioteo.hypothesis.intercom.HasEventBus;
import com.tilioteo.hypothesis.intercom.HasUIMap;
import com.tilioteo.hypothesis.intercom.UIMap;
import com.tilioteo.hypothesis.intercom.UIWrapper;
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
@SuppressWarnings({ "serial", "unused" })
@Theme("hypothesis")
@Push
public class TestUI extends HUI implements HasEventBus {

	@WebServlet(value = "/test/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TestUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends VaadinServlet implements HasUIMap {
		
		private UIMap uiMap;

		@Override
		public void init(ServletConfig servletConfig) throws ServletException {
			super.init(servletConfig);
			
			uiMap = new UIMap();
		}
		
		@Override
		public UIMap getUIMap() {
			return uiMap;
		}
	}
	
	EventBus eventBus = new EventBus(new SubscriberExceptionHandler() {
		@Override
		public void handleException(Throwable e, SubscriberExceptionContext s) {
			// TODO Auto-generated method stub
			
		}
	});
	
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
		
		
		UIMap uiMap = HasUIMap.Getter.getCurrentUIMap();
		if (uiMap != null) {
			uiMap.extractFromRequest(request);
			
			Collection<UIWrapper> uiWrappers = uiMap.values();
			
			for (UIWrapper uiWrapper : uiWrappers) {
				Class<?> clazz = uiWrapper.getUi().getClass();
				String s = clazz.getName();
			}
		}
		
		eventBus.register(this);
		
		Button button = new Button("Broadcast", new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				HasUIMap.Broadcaster.post(new TestEvent());
			}
		});
		
		verticalLayout.addComponent(button);
		
	}

	@Override
	public void post(Object event) {
		eventBus.post(event);
	}
	
	@Subscribe
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
}
