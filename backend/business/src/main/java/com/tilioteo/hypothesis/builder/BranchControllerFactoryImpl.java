/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.util.List;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.business.BranchController;
import com.tilioteo.hypothesis.common.utility.DocumentUtility;
import com.tilioteo.hypothesis.common.utility.EvaluableUtility;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.evaluation.AbstractBasePath;
import com.tilioteo.hypothesis.evaluation.DefaultPath;
import com.tilioteo.hypothesis.evaluation.Expression;
import com.tilioteo.hypothesis.evaluation.Formula;
import com.tilioteo.hypothesis.evaluation.Nick;
import com.tilioteo.hypothesis.evaluation.Path;
import com.tilioteo.hypothesis.evaluation.Pattern;
import com.tilioteo.hypothesis.interfaces.Document;
import com.tilioteo.hypothesis.interfaces.DocumentConstants;
import com.tilioteo.hypothesis.interfaces.Element;

/**
 * @author kamil
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
		for (Element pathElement : paths) {
			path = createAbstractBasePath(pathElement);
			if (path != null)
				controller.addPath(path);
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
		int i = 0;
		for (Element nickElement : nicks) {
			Nick nick = createNick(nickElement);
			pattern.addNick(++i, nick);
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
