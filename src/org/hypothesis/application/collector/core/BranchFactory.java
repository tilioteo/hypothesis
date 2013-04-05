/**
 * 
 */
package org.hypothesis.application.collector.core;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.application.collector.evaluable.Expression;
import org.hypothesis.application.collector.xml.BranchXmlConstants;
import org.hypothesis.application.collector.xml.BranchXmlUtility;
import org.hypothesis.common.expression.ExpressionFactory;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchFactory {

	private static BranchFactory instance = null;

	public static BranchFactory getInstance() {
		if (instance == null)
			instance = new BranchFactory();

		return instance;
	}

	private BranchManager branchManager = null;

	private BranchFactory() {
		super();
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

	public void createBranchControls(BranchManager branchManager) {
		this.branchManager = branchManager;

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

	private AbstractBaseFormula createAbstractBaseFormula(Element element) {
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
		path.setAbstractBaseFormula(createAbstractBaseFormula(pathElement));

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
