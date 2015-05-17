/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author kamil
 *
 */
public class TaskXmlUtility {

	public static boolean isValidTaskXml(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(TaskXmlConstants.TASK));
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getNodesElements(Element documentRoot) {
		if (documentRoot != null) {
			List<Element> variables = documentRoot.selectNodes(String.format("%s//%s",
					TaskXmlConstants.NODES, TaskXmlConstants.NODE));

			return variables;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getVariablesElements(Element documentRoot) {
		if (documentRoot != null) {
			List<Element> variables = documentRoot.selectNodes(String.format("%s//%s",
					SlideXmlConstants.VARIABLES, SlideXmlConstants.VARIABLE));

			return variables;
		}

		return null;
	}

	public static String getSlideId(Element element) {
		return element.attributeValue(TaskXmlConstants.SLIDE_ID).trim();
	}

	public static Element getEvaluateElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(TaskXmlConstants.EVALUATE);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getActionsElements(Element documentRoot) {
		if (documentRoot != null) {
			List<Element> actions = documentRoot.selectNodes(String.format("%s//%s",
					SlideXmlConstants.ACTIONS, SlideXmlConstants.ACTION));

			return actions;
		}

		return null;
	}

}
