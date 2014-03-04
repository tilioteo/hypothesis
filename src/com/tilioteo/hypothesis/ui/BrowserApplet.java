/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.servlet.ServletUtil;
import com.tilioteo.hypothesis.shared.ui.javaapplet.BrowserAppletClientRpc;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class BrowserApplet extends JavaApplet {
	
	public static final String PARAM_APPLICATION_URL	=	"application_url";
	public static final String PARAM_TOKEN				=	"token";
	public static final String PARAM_END_PARAMETER		=	"end_parameter";
	public static final String PARAM_MAXIMIZED			=	"maximized";
	
	private BrowserAppletClientRpc clientRpc;
	
	public BrowserApplet() {
		super();
		clientRpc = getRpcProxy(BrowserAppletClientRpc.class);
		
		String contextUrl = ServletUtil.getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest());
		setCode("com.tilioteo.hypothesis.browser.BrowserApplet");
		//setArchive(contextUrl + "/resource/browser-applet.jar");
		setJnlpHref(contextUrl + "/resource/browser.jnlp");
		setMayscript(true);
		
		setJavaArgument(PARAM_APPLICATION_URL, contextUrl + "/process/");
		setJavaArgument(PARAM_END_PARAMETER, "?close");
	}
	
	public void startBrowser(String token) {
		clientRpc.startBrowser(token);
	}

	public boolean isReady() {
		return false;
	}
}
