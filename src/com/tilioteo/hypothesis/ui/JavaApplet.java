/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.shared.ui.javaapplet.JavaAppletState;
import com.vaadin.ui.AbstractComponent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class JavaApplet extends AbstractComponent {
	
	public JavaApplet() {
		super();
	}

	@Override
	public void attach() {
		super.attach();

		setId(getConnectorId());
	}

	@Override
	protected JavaAppletState getState() {
		return (JavaAppletState) super.getState();
	}
	
	public String getText() {
		return getState().text;
	}
	
	public void setText(String text) {
		getState().text = text;
	}
	
	public String getCode() {
		return getState().code;
	}
	
	public void setCode(String code) {
		getState().code = code;
	}
	
	public String getArchive() {
		return getState().archive;
	}
	
	public void setArchive(String archive) {
		getState().archive = archive;
	}
	
	public String getJnlpHref() {
		return getState().jnlp_href;
	}
	
	public void setJnlpHref(String jnlp) {
		getState().jnlp_href = jnlp;
	}
	
	public boolean getMayscript() {
		return getState().mayscript;
	}
	
	public void setMayscript(boolean value) {
		getState().mayscript = value;
	}
	
	public String getJavaArgument(String name) {
		return getState().java_arguments.get(name);
	}
	
	public void setJavaArgument(String name, String value) {
		getState().java_arguments.put(name, value);
	}
	
}
