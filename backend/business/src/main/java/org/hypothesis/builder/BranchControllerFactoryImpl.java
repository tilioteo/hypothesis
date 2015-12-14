/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.List;

import org.apache.log4j.Logger;
import org.hypothesis.business.BranchController;
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
		return null;
	}

	private void createPaths(Element rootElement, BranchController controller) {
		List<Element> paths = DocumentUtility.getPathElements(rootElement);
		AbstractBasePath path = null;

		if (paths != null) {
			for (Element pathElement : paths) {
				path = createAbstractBasePath(pathElement);
				if (path != null)
					controller.addPath(path);
			}
		}

		Element defaultPathElement = DocumentUtility.getDefaultPathElement(rootElement);
		path = createAbstractBasePath(defaultPathElement);
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
		List<Element> nicks = DocumentUtility.getNickElements(subElement);

		if (nicks != null) {
			int i = 0;
			for (Element nickElement : nicks) {
				Nick nick = createNick(nickElement);
				pattern.addNick(++i, nick);
			}
		}

		return pattern;
	}

	private Nick createNick(Element nickElement) {
		Long slideId = DocumentUtility.getSlideId(nickElement);
		Nick nick = new Nick(slideId);

		Expression expression = EvaluableUtility.createExpression(nickElement);
		nick.setExpression(expression);

		return nick;
	}

}
