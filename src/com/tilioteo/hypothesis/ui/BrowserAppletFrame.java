/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.servlet.ServletUtil;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameClientRpc;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameServerRpc;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameState;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class BrowserAppletFrame extends BrowserFrame {
	
	private static Logger log = Logger.getLogger(BrowserAppletFrame.class);

	public static final String PARAM_EMBED_PAGE			=	"browserapplet.html";
	public static final String PARAM_APPLICATION_URL	=	"app_url";
	public static final String PARAM_PROCESS_APP		=	"process_app";
	public static final String PARAM_CLOSE_KEY			=	"close_key";
	public static final String PARAM_FULLSCREEN			=	"fs";
	
	private BrowserAppletFrameClientRpc clientRpc;
	private BrowserAppletFrameServerRpc rpc = new BrowserAppletFrameServerRpc() {
		@Override
		public void readyChecked(boolean readyState) {
			log.debug("BrowserAppletFrameServerRpc: readyChecked()");
			fireEvent(new ReadyCheckedEvent(BrowserAppletFrame.this, readyState));
		}
	};

	public BrowserAppletFrame() {
		super();
		registerRpc(rpc);
		clientRpc = getRpcProxy(BrowserAppletFrameClientRpc.class);

		setSource(new ThemeResource(String.format("%s?%s=%s&%s=%s&%s=%s", PARAM_EMBED_PAGE,
				PARAM_APPLICATION_URL, ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest()),
				PARAM_PROCESS_APP, "/process/",
				PARAM_CLOSE_KEY, "close.html")));
	}

	@Override
	protected BrowserAppletFrameState getState() {
		return (BrowserAppletFrameState) super.getState();
	}
	
	public void startBrowser(String token) {
		clientRpc.startBrowser(token);
	}

	public void checkReady() {
		clientRpc.checkReadyState();
	}

	public class ReadyCheckedEvent extends Component.Event {
		
		public static final String EVENT_ID = "readyChecked";

		private boolean ready;

		public ReadyCheckedEvent(Component source, boolean ready) {
			super(source);
			
			this.ready = ready;
		}
		
		public boolean isReady() {
			return ready;
		}
	}
	
	public interface ReadyCheckedListener extends Serializable {
		
		public static final Method READY_CHECKED_METHOD = ReflectTools
				.findMethod(ReadyCheckedListener.class, "readyChecked", ReadyCheckedEvent.class);

		public void readyChecked(ReadyCheckedEvent event);
	}

	public void addReadyCheckedListener(ReadyCheckedListener listener) {
		addListener(ReadyCheckedEvent.EVENT_ID, ReadyCheckedEvent.class, listener,
				ReadyCheckedListener.READY_CHECKED_METHOD);
	}

	public void removeReadyCheckedListener(ReadyCheckedListener listener) {
		removeListener(ReadyCheckedEvent.EVENT_ID, ReadyCheckedEvent.class, listener);
	}

}
