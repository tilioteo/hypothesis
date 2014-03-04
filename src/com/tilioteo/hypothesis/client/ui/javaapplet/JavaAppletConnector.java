/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.javaapplet;

import com.tilioteo.hypothesis.client.ui.VJavaAppletIE;
import com.tilioteo.hypothesis.shared.ui.javaapplet.JavaAppletState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.JavaApplet.class)
public class JavaAppletConnector extends AbstractComponentConnector {

	@Override
	public VJavaAppletIE getWidget() {
		return (VJavaAppletIE) super.getWidget();
	}

	@Override
	public JavaAppletState getState() {
		return (JavaAppletState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		getWidget().setText(getState().text);
		getWidget().setCode(getState().code);
		getWidget().setArchive(getState().archive);
		getWidget().setJnlpHref(getState().jnlp_href);
		getWidget().setMayscript(getState().mayscript);
		getWidget().setJavaArguments(getState().java_arguments);
	}

	
}
