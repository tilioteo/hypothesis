/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.hypothesis.dom.BranchXmlConstants;
import com.tilioteo.hypothesis.dom.BranchXmlUtility;
import com.tilioteo.hypothesis.evaluable.ExpressionFactory;
import com.tilioteo.hypothesis.processing.Formula;
import com.tilioteo.hypothesis.processing.AbstractBasePath;
import com.tilioteo.hypothesis.processing.DefaultPath;
import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.Nick;
import com.tilioteo.hypothesis.processing.Pattern;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class BranchFactory implements Serializable {

	private static HashMap<BranchManager, BranchFactory> instances = new HashMap<BranchManager, BranchFactory>();
	
	public static BranchFactory getInstance(BranchManager branchManager) {
		BranchFactory branchFactory = instances.get(branchManager);
		
		if (null == branchFactory) {
			branchFactory = new BranchFactory(branchManager);
			instances.put(branchManager, branchFactory);
		}
		return branchFactory;
	}
	
	public static void remove(BranchManager branchManager) {
		instances.remove(branchManager);
	}

	private BranchManager branchManager = null;

	private BranchFactory(BranchManager branchManager) {
		this.branchManager = branchManager;
	}

	private AbstractBasePath createAbstractBasePath(Element pathElement) {
		if (pathElement != null) {
			if (BranchXmlConstants.PATH.equals(pathElement.getName())) {
				return createPath(pathElement);
			} else if (BranchXmlConstants.DEFAULT_PATH.equals(pathElement
					.getName())) {
				return createDefaultPath(pathElement);
			}
		}

		return null;
	}

	public void createBranchControls() {
		if (branchManager != null) {
			Document doc = branchManager.getBranchXml();
			if (BranchXmlUtility.isValidBranchXml(doc)) {
				createBranchPaths(doc.getRootElement());
			}
		}
	}

	private void createBranchPaths(Element rootElement) {
		List<Element> paths = BranchXmlUtility.getPathElements(rootElement);
		AbstractBasePath path = null;
		for (Element pathElement : paths) {
			path = createAbstractBasePath(pathElement);
			if (path != null)
				branchManager.addPath(path);
		}

		Element defaultPathElement = BranchXmlUtility
				.getDefaultPathElement(rootElement);
		path = createAbstractBasePath(defaultPathElement);
		if (path != null)
			branchManager.addPath(path);
	}

	private DefaultPath createDefaultPath(Element pathElement) {
		DefaultPath path = new DefaultPath();
		setBranchKey(path, pathElement);
		return path;
	}

	private Expression createExpression(Element element) {
		if (element != null
				&& element.getName().equals(BranchXmlConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(element
					.getTextTrim()));
		}
		return null;
	}

	private Formula createFormula(Element element) {
		if (element != null) {
			Element subElement = BranchXmlUtility.getPatternElement(element);
			if (subElement != null)
				return createPattern(subElement);
		}

		return null;
	}

	private Nick createNick(Element nickElement) {
		Long slideId = BranchXmlUtility.getSlideId(nickElement);
		Nick nick = new Nick(slideId);

		Expression expression = createExpression(BranchXmlUtility
				.getExpressionElement(nickElement));
		nick.setExpression(expression);

		return nick;
	}

	private Path createPath(Element pathElement) {
		Path path = new Path();
		setBranchKey(path, pathElement);
		path.setAbstractBaseFormula(createFormula(pathElement));

		return path;
	}

	private Pattern createPattern(Element subElement) {
		Pattern pattern = new Pattern();
		List<Element> nicks = BranchXmlUtility.getNickElements(subElement);
		int i = 0;
		for (Element nickElement : nicks) {
			Nick nick = createNick(nickElement);
			pattern.addNick(++i, nick);
		}

		return pattern;
	}

	private void setBranchKey(DefaultPath path, Element pathElement) {
		Element branchKeyElement = BranchXmlUtility
				.getBranchKeyElement(pathElement);
		path.setBranchKey(BranchXmlUtility.getTrimmedText(branchKeyElement));
	}

}
