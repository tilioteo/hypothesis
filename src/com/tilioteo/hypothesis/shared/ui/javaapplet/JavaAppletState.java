/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.javaapplet;

import java.util.HashMap;

import com.vaadin.shared.AbstractComponentState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class JavaAppletState extends AbstractComponentState {
	
	public String text = "Applet failed to run. No Java plug-in was found.";
	public String code;
	public String archive;
	public String jnlp_href;
	public boolean mayscript = false;

	public HashMap<String, String> java_arguments = new HashMap<String, String>();
}
