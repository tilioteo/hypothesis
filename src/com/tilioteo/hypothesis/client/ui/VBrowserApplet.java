/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author kamil
 *
 */
public class VBrowserApplet extends VJavaAppletIE {
	
	
	
	/*
	 * element id must be set to invoke applet's methods
	 */
	public void startBrowser(final String token) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				// fire browser started event
				startBrowser(getElement().getId(), token);
				// fire browser finished event
			}
		});
	}

	/*
	 * applet element must be selected by document.getElementById method
	 */
	private static native void startBrowser(String id, String token) /*-{
		appletElement = $doc.getElementById(id);
		if (appletElement && appletElement.startBrowser) { 
			appletElement.startBrowser(token);
		}
	}-*/;

}
