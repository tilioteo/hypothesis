/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchXmlUtility {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(BranchXmlUtility.class);

	public static Element getBranchKeyElement(Element element) {
		if (element != null) {
			Node node = XmlUtility.findFirstNodeByName(element,
					BranchXmlConstants.BRANCH_KEY);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	public static Element getDefaultPathElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!BranchXmlConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = XmlUtility.findFirstNodeByName(documentRoot,
					BranchXmlConstants.DEFAULT_PATH);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	public static Element getExpressionElement(Element element) {
		if (element != null) {
			Node node = XmlUtility.findFirstNodeByName(element,
					BranchXmlConstants.EXPRESSION);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getNickElements(Element patternElement) {
		if (patternElement != null) {
			List<Element> nicks = patternElement
					.selectNodes(BranchXmlConstants.NICK);
			return nicks;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getPathElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BranchXmlConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> paths = documentRoot
					.selectNodes(BranchXmlConstants.PATH);
			return paths;
		}

		return null;
	}

	public static Element getPatternElement(Element element) {
		if (element != null) {
			Node node = XmlUtility.findFirstNodeByName(element,
					BranchXmlConstants.PATTERN);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	public static Long getSlideId(Element element) {
		if (element != null) {
			String idString = element
					.attributeValue(BranchXmlConstants.SLIDE_ID);
			if (!Strings.isNullOrEmpty(idString)) {
				try {
					Long id = Long.parseLong(idString);
					return id;
				} catch (NumberFormatException e) {
				}
			}
		}
		return null;
	}

	public static String getTrimmedText(Element element) {
		if (element != null)
			return element.getText().trim();
		else
			return null;
	}

	public static boolean isValidBranchXml(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(BranchXmlConstants.BRANCH));
	}

}
