/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;

/**
 * @author kamil
 *
 */
public class ProcessingUtility {

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
