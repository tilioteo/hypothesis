/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.ParamElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VJavaApplet extends Widget {

	public static final String CLASSNAME = "v-javaapplet";
	
	public static final String OBJECT_MIME_TYPE = "application/x-java-applet";
	public static final String PARAM_CODE = "code";
	public static final String PARAM_ARCHIVE = "archive";
	public static final String PARAM_JNLP_HREF = "jnlp_href";
	public static final String PARAM_MAYSCRIPT = "mayscript";
	public static final String PARAM_JAVA_ARGUMENTS = "java_arguments";
	
	private ObjectElement element;
	private ParamElement codeElement;
	private ParamElement archiveElement = null;
	private ParamElement jnlpElement = null;
	private ParamElement mayscriptElement = null;
	private ParamElement argumentsElement = null;
	private Text textNode;
	
	public VJavaApplet() {
		element = Document.get().createObjectElement();
		element.setType(OBJECT_MIME_TYPE);
		
		codeElement = Document.get().createParamElement();
		codeElement.setName(PARAM_CODE);
		element.appendChild(codeElement);
		
		textNode = Document.get().createTextNode("");
		element.appendChild(textNode);
		
		setElement(element);
		setStyleName(CLASSNAME);
	}
	
	public void setText(String text) {
		textNode.setData(text);
	}
	
	@Override
	public void setWidth(String width) {
		element.setWidth(width);
	}
	
	@Override
	public void setHeight(String height) {
		element.setHeight(height);
	}
	
	public void setCode(String code) {
		codeElement.setValue(code);
	}
	
	public void setArchive(String archive) {
		if (archive != null) {
			if (null == archiveElement) {
				archiveElement = Document.get().createParamElement();
				archiveElement.setName(PARAM_ARCHIVE);
				element.insertAfter(archiveElement, codeElement);
			}
			archiveElement.setValue(archive);
		} else {
			if (archiveElement != null) {
				element.removeChild(archiveElement);
				archiveElement = null;
			}
		}
	}
	
	public void setJnlpHref(String jnlp) {
		if (jnlp != null) {
			if (null == jnlpElement) {
				jnlpElement = Document.get().createParamElement();
				jnlpElement.setName(PARAM_JNLP_HREF);
				element.insertAfter(jnlpElement, archiveElement != null ? archiveElement : codeElement);
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
			if (null == mayscriptElement) {
				mayscriptElement = Document.get().createParamElement();
				mayscriptElement.setName(PARAM_MAYSCRIPT);
				element.insertBefore(mayscriptElement, argumentsElement != null ? argumentsElement : textNode);
			}
			mayscriptElement.setValue("true");
		} else {
			if (mayscriptElement != null) {
				element.removeChild(mayscriptElement);
				mayscriptElement = null;
			}
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
