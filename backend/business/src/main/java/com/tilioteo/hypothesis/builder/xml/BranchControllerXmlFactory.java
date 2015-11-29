/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.expressions.ExpressionFactory;
import com.tilioteo.hypothesis.builder.BranchControllerFactory;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.business.BranchController;
import com.tilioteo.hypothesis.evaluation.AbstractBasePath;
import com.tilioteo.hypothesis.evaluation.DefaultPath;
import com.tilioteo.hypothesis.evaluation.Expression;
import com.tilioteo.hypothesis.evaluation.Formula;
import com.tilioteo.hypothesis.evaluation.Nick;
import com.tilioteo.hypothesis.evaluation.Path;
import com.tilioteo.hypothesis.evaluation.Pattern;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class BranchControllerXmlFactory implements BranchControllerFactory {

	private static Logger log = Logger.getLogger(BranchControllerXmlFactory.class);

	@Override
	public BranchController buildBranchController(String data) {

		Document document = null;
		try {
			document = XmlUtility.readString(data);
		} catch (Throwable e) {
		}

		if (null == document) {
			log.warn("Branch document is NULL");
			return null;
		}

		if (XmlDocumentUtility.isValidBranchXml(document)) {
			return buildBranchController(document);
		}

		return null;
	}

	private BranchController buildBranchController(Document document) {
		BranchController controller = new BranchController();

		createPaths(document.getRootElement(), controller);
		return null;
	}

	private void createPaths(Element rootElement, BranchController controller) {
		List<Element> paths = XmlDocumentUtility.getPathElements(rootElement);
		AbstractBasePath path = null;
		for (Element pathElement : paths) {
			path = createAbstractBasePath(pathElement);
			if (path != null)
				controller.addPath(path);
		}

		Element defaultPathElement = XmlDocumentUtility.getDefaultPathElement(rootElement);
		path = createAbstractBasePath(defaultPathElement);
		if (path != null)
			controller.addPath(path);
	}

	private AbstractBasePath createAbstractBasePath(Element pathElement) {
		if (pathElement != null) {
			if (BuilderConstants.PATH.equals(pathElement.getName())) {
				return createPath(pathElement);
			} else if (BuilderConstants.DEFAULT_PATH.equals(pathElement.getName())) {
				return createDefaultPath(pathElement);
			}
		}

		return null;
	}

	private Path createPath(Element pathElement) {
		Path path = new Path();
		setBranchKey(path, pathElement);
		path.setAbstractBaseFormula(createFormula(pathElement));

		return path;
	}

	private DefaultPath createDefaultPath(Element pathElement) {
		DefaultPath path = new DefaultPath();
		setBranchKey(path, pathElement);
		return path;
	}

	private void setBranchKey(DefaultPath path, Element pathElement) {
		Element branchKeyElement = XmlDocumentUtility.getBranchKeyElement(pathElement);
		path.setBranchKey(XmlDocumentUtility.getTrimmedText(branchKeyElement));
	}

	private Formula createFormula(Element element) {
		if (element != null) {
			Element subElement = XmlDocumentUtility.getPatternElement(element);
			if (subElement != null)
				return createPattern(subElement);
		}

		return null;
	}

	private Pattern createPattern(Element subElement) {
		Pattern pattern = new Pattern();
		List<Element> nicks = XmlDocumentUtility.getNickElements(subElement);
		int i = 0;
		for (Element nickElement : nicks) {
			Nick nick = createNick(nickElement);
			pattern.addNick(++i, nick);
		}

		return pattern;
	}

	private Nick createNick(Element nickElement) {
		Long slideId = XmlDocumentUtility.getSlideId(nickElement);
		Nick nick = new Nick(slideId);

		Expression expression = createExpression(XmlDocumentUtility.getExpressionElement(nickElement));
		nick.setExpression(expression);

		return nick;
	}

	private Expression createExpression(Element element) {
		if (element != null && element.getName().equals(BuilderConstants.EXPRESSION)) {
			return new Expression(ExpressionFactory.parseString(element.getTextTrim()));
		}
		return null;
	}

}
