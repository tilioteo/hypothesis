/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import java.io.Serializable;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.StringSet;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessingUtility implements Serializable {

	public static List<Element> getCallbackElements(Element component) {
		return com.tilioteo.hypothesis.dom.SlideXmlUtility.getElementSubNodeChilds(component, SlideXmlConstants.CALLBACKS,
				new StringSet(new String[] {SlideXmlConstants.CALLBACK}));
	}

	public static void setProcessingProperties(Processing component, Element element, StringMap properties) {
		
		setCode(component, element);
	}

	private static void setCode(Processing component, Element element) {
		Element codeElement = SlideXmlUtility.getCodeElement(element);
		if (codeElement != null) {
			String code = codeElement.getTextTrim();
			if (!Strings.isNullOrEmpty(code)) {
				component.setProcessingCode(code);
			}
		}
	}
}
