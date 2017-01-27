/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hypothesis.builder.TaskControllerFactory;
import org.hypothesis.business.TaskController;
import org.hypothesis.business.impl.TaskControllerImpl;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.model.Task;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.Evaluator;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UIScoped
public class TaskControllerFactoryImpl implements TaskControllerFactory {

	private static Logger log = Logger.getLogger(TaskControllerFactoryImpl.class);

	@Override
	public TaskController createController(Task entity, DocumentReader reader) {
		if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getData())) {
			Document document = reader.readString(entity.getData());

			if (null == document) {
				log.warn("Task document is NULL");
				return null;
			}

			if (DocumentUtility.isValidTaskDocument(document)) {
				return createTaskController(document);
			}
		}

		return null;
	}

	private TaskController createTaskController(Document document) {
		TaskController controller = new TaskControllerImpl();

		EvaluableUtility.createActions(document.root(), controller);
		EvaluableUtility.createVariables(document.root(), controller, null);

		createNodes(document.root(), controller);

		return controller;
	}

	private void createNodes(Element rootElement, TaskController controller) {
		DocumentUtility.getNodesElements(rootElement).stream().map(m -> createNode(m, controller).orElse(null))
				.filter(Objects::nonNull).forEach(e -> controller.addNode(e.getSlideId(), e));
	}

	private Optional<Node> createNode(Element element, Evaluator evaluator) {
		return Optional.ofNullable(element).map(m -> DocumentUtility.getSlideId(m).orElse(null)).map(m -> {
			Node node = new Node(evaluator, m);
			DocumentUtility.getEvaluateElement(element)
					.ifPresent(e -> e.children().stream()
							.map(mm -> EvaluableUtility.createEvaluable(mm, evaluator).orElse(null))
							.filter(Objects::nonNull).forEach(node::add));
			return node;
		});
	}

}
