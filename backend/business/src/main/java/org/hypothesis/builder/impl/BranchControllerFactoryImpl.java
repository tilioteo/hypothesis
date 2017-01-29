/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hypothesis.builder.BranchControllerFactory;
import org.hypothesis.business.BranchController;
import org.hypothesis.business.impl.BranchControllerImpl;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Branch;
import org.hypothesis.evaluation.AbstractBasePath;
import org.hypothesis.evaluation.DefaultPath;
import org.hypothesis.evaluation.Formula;
import org.hypothesis.evaluation.Nick;
import org.hypothesis.evaluation.Path;
import org.hypothesis.evaluation.Pattern;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class BranchControllerFactoryImpl implements BranchControllerFactory {

	private static Logger log = Logger.getLogger(BranchControllerFactoryImpl.class);

	@Override
	public BranchController createController(Branch entity, DocumentReader reader) {
		if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getData())) {
			Document document = reader.readString(entity.getData());

			if (null == document) {
				log.warn("Branch document is NULL");
				return null;
			}

			if (DocumentUtility.isValidBranchDocument(document)) {
				return createBranchController(document);
			}
		}

		return null;
	}

	private BranchController createBranchController(Document document) {
		BranchController controller = new BranchControllerImpl();

		createPaths(document.root(), controller);

		return controller;
	}

	private void createPaths(Element rootElement, BranchController controller) {
		DocumentUtility.getPathElements(rootElement).stream().map(this::createAbstractBasePath).filter(Objects::nonNull)
				.forEach(controller::addPath);

		DocumentUtility.getDefaultPathElement(rootElement).map(this::createAbstractBasePath)
				.ifPresent(controller::addPath);
	}

	private AbstractBasePath createAbstractBasePath(Element pathElement) {
		return Optional.ofNullable(pathElement).map(m -> {
			if (DocumentConstants.PATH.equals(m.getName())) {
				return createPath(m);
			} else if (DocumentConstants.DEFAULT_PATH.equals(m.getName())) {
				return createDefaultPath(m);
			}
			return null;
		}).orElse(null);
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
		DocumentUtility.getBranchKeyElement(pathElement).flatMap(DocumentUtility::getTrimmedText)
				.filter(StringUtils::isNotEmpty).ifPresent(path::setBranchKey);
	}

	private Formula createFormula(Element element) {
		return DocumentUtility.getPatternElement(element).map(this::createPattern).orElse(null);
	}

	private Pattern createPattern(Element subElement) {
		Pattern pattern = new Pattern();

		final AtomicInteger seq = new AtomicInteger(0);
		DocumentUtility.getNickElements(subElement).stream().map(this::createNick).filter(Objects::nonNull)
				.forEach(e -> pattern.addNick(seq.incrementAndGet(), e));

		return pattern;
	}

	private Nick createNick(Element nickElement) {
		return DocumentUtility.getSlideId(nickElement).map(Nick::new).map(m -> {
			return DocumentUtility.getExpressionElement(nickElement).flatMap(EvaluableUtility::createExpression)
					.map(mm -> {
						m.setExpression(mm);
						return m;
					}).orElse(null);
		}).orElse(null);
	}

}
