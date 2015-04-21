/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.shared.AnimType;
import org.vaadin.maps.server.Bounds;
import org.vaadin.maps.server.LonLat;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.MapContainer;
import org.vaadin.maps.ui.control.DrawPathControl;
import org.vaadin.maps.ui.control.DrawPointControl;
import org.vaadin.maps.ui.control.DrawPolygonControl;
import org.vaadin.maps.ui.control.PanControl;
import org.vaadin.maps.ui.control.ZoomControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer;
import org.vaadin.maps.ui.handler.PathHandler.FinishStrategy;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.ImageLayer;
import org.vaadin.maps.ui.layer.ImageSequenceLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;
import org.vaadin.maps.ui.layer.WMSLayer;
import org.vaadin.maps.ui.tile.ImageSequenceTile;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadEvent;
import org.vaadin.maps.ui.tile.WMSTile;
import org.vaadin.tltv.vprocjs.ui.Processing;
import org.vaadin.websocket.ui.WebSocket;
import org.vaadin.websocket.ui.WebSocket.CloseEvent;
import org.vaadin.websocket.ui.WebSocket.MessageEvent;
import org.vaadin.websocket.ui.WebSocket.OpenEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.intercom.HasEventBus;
import com.tilioteo.hypothesis.intercom.HasUIMap;
import com.tilioteo.hypothesis.intercom.UIMap;
import com.tilioteo.hypothesis.intercom.UIWrapper;
import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.tilioteo.hypothesis.ui.DeployJava.InstallLink;
import com.tilioteo.hypothesis.ui.DeployJava.JavaCheckedEvent;
import com.tilioteo.hypothesis.ui.DeployJava.JavaInfoPanel;
import com.tilioteo.hypothesis.ui.Image.LoadListener;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughEvent;
import com.tilioteo.hypothesis.ui.Media.StartEvent;
import com.tilioteo.hypothesis.ui.Media.StopEvent;
import com.tilioteo.hypothesis.ui.ShortcutKey.KeyPressEvent;
import com.tilioteo.hypothesis.ui.Video.ClickEvent;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

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
