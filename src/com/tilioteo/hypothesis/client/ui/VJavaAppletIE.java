/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ParamElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VJavaAppletIE extends Widget {
	
	public static final String APPLET_ELEMENT = "applet";
	public static final String ATTRIBUTE_CODE = "code";
	public static final String ATTRIBUTE_ARCHIVE = "archive";
	public static final String ATTRIBUTE_MAYSCRIPT = "MAYSCRIPT";
	public static final String PARAM_JNLP_HREF = "jnlp_href";
	public static final String PARAM_JAVA_ARGUMENTS = "java_arguments";

	private Element element;
	private ParamElement jnlpElement = null;
	private ParamElement argumentsElement = null;
	private Text textNode;
	
	public VJavaAppletIE() {
		element = DOM.createElement(APPLET_ELEMENT);
		
		textNode = Document.get().createTextNode("");
		element.appendChild(textNode);
		
		setElement(element);
	}
	
	public void setText(String text) {
		textNode.setData(text);
	}
	
	@Override
	public void setWidth(String width) {
		element.setAttribute("width", width);
	}
	
	@Override
	public void setHeight(String height) {
		element.setAttribute("height", height);
	}
	
	public void setCode(String code) {
		if (code != null) {
			element.setAttribute(ATTRIBUTE_CODE, code);
		} else {
			element.removeAttribute(ATTRIBUTE_CODE);
		}
	}
	
	public void setArchive(String archive) {
		if (archive != null) {
			element.setAttribute(ATTRIBUTE_ARCHIVE, archive);
		} else {
			element.removeAttribute(ATTRIBUTE_ARCHIVE);
		}
	}
	
	public void setJnlpHref(String jnlp) {
		if (jnlp != null) {
			if (null == jnlpElement) {
				jnlpElement = Document.get().createParamElement();
				jnlpElement.setName(PARAM_JNLP_HREF);
				element.insertBefore(jnlpElement, argumentsElement != null ? argumentsElement : textNode);
			}
			jnlpElement.setValue(jnlp);
		} else {
			if (jnlpElement != null) {
				element.removeChild(jnlpElement);
				jnlpElement = null;
			}
		}
	}
	
	public void setMayscript(Boolean value) {
		if (value != null && value) {
			element.setAttribute(ATTRIBUTE_MAYSCRIPT, "mayscript");
		} else {
			element.removeAttribute(ATTRIBUTE_MAYSCRIPT);
		}
	}

	public void setJavaArguments(Map<String, String> arguments) {
		updateArgumentsElement(arguments);
	}
	
	private void updateArgumentsElement(Map<String, String> argumentMap) {
		if (argumentMap.size() > 0) {
			if (null == argumentsElement) {
				argumentsElement = Document.get().createParamElement();
				argumentsElement.setName(PARAM_JAVA_ARGUMENTS);
				element.insertBefore(argumentsElement, textNode);
			}
			
			String arguments = "";
			for (String name : argumentMap.keySet()) {
				String value = "-D" + name + "=" + argumentMap.get(name);
				
				if (arguments.length() > 0) {
					arguments += " ";
				}
				arguments += value;
			}
			argumentsElement.setValue(arguments);
			
		} else if (argumentsElement != null) {
			element.removeChild(argumentsElement);
			argumentsElement = null;
		}
	}
}
