/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.Objects;

import org.apache.log4j.Logger;
import org.hypothesis.business.BranchController;
import org.hypothesis.common.IntSequence;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.evaluation.AbstractBasePath;
import org.hypothesis.evaluation.DefaultPath;
import org.hypothesis.evaluation.Expression;
import org.hypothesis.evaluation.Formula;
import org.hypothesis.evaluation.Nick;
import org.hypothesis.evaluation.Path;
import org.hypothesis.evaluation.Pattern;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BranchControllerFactoryImpl implements BranchControllerFactory {

	private static Logger log = Logger.getLogger(BranchControllerFactoryImpl.class);

	@Override
	public BranchController buildBranchController(String data, DocumentReader reader) {

		Document document = reader.readString(data);

		if (null == document) {
			log.warn("Branch document is NULL");
			return null;
		}

		if (DocumentUtility.isValidBranchDocument(document)) {
			return buildBranchController(document);
		}

		return null;
	}

	private BranchController buildBranchController(Document document) {
		BranchController controller = new BranchController();

		createPaths(document.root(), controller);

		return controller;
	}

	private void createPaths(Element rootElement, BranchController controller) {
		DocumentUtility.getPathElements(rootElement).stream().map(this::createAbstractBasePath).filter(Objects::nonNull)
				.forEach(controller::addPath);

		Element defaultPathElement = DocumentUtility.getDefaultPathElement(rootElement);
		AbstractBasePath path = createAbstractBasePath(defaultPathElement);
		if (path != null)
			controller.addPath(path);
	}

	private AbstractBasePath createAbstractBasePath(Element pathElement) {
		if (pathElement != null) {
			if (DocumentConstants.PATH.equals(pathElement.getName())) {
				return createPath(pathElement);
			} else if (DocumentConstants.DEFAULT_PATH.equals(pathElement.getName())) {
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
		Element branchKeyElement = DocumentUtility.getBranchKeyElement(pathElement);
		path.setBranchKey(DocumentUtility.getTrimmedText(branchKeyElement));
	}

	private Formula createFormula(Element element) {
		if (element != null) {
			Element subElement = DocumentUtility.getPatternElement(element);
			if (subElement != null)
				return createPattern(subElement);
		}

		return null;
	}

	private Pattern createPattern(Element subElement) {
		Pattern pattern = new Pattern();

		final IntSequence seq = new IntSequence(0);
		DocumentUtility.getNickElements(subElement).stream().map(this::createNick)
				.forEach(e -> pattern.addNick(seq.next(), e));

		return pattern;
	}

	private Nick createNick(Element nickElement) {
		Long slideId = DocumentUtility.getSlideId(nickElement);
		Nick nick = new Nick(slideId);

		Element expressionElement = DocumentUtility.getExpressionElement(nickElement);
		Expression expression = EvaluableUtility.createExpression(expressionElement);
		nick.setExpression(expression);

		return nick;
	}

}
