/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.Objects;

import org.apache.log4j.Logger;
import org.hypothesis.business.TaskController;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.Evaluator;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskControllerFactoryImpl implements TaskControllerFactory {

	private static Logger log = Logger.getLogger(TaskControllerFactoryImpl.class);

	@Override
	public TaskController buildTaskController(String data, DocumentReader reader) {
		Document document = reader.readString(data);

		if (null == document) {
			log.warn("Task document is NULL");
			return null;
		}

		if (DocumentUtility.isValidTaskDocument(document)) {
			return buildTaskController(document);
		}

		return null;
	}

	private TaskController buildTaskController(Document document) {
		TaskController controller = new TaskController();

		EvaluableUtility.createActions(document.root(), controller);
		EvaluableUtility.createVariables(document.root(), controller, null);

		createNodes(document.root(), controller);

		return controller;
	}

	private void createNodes(Element rootElement, TaskController controller) {
		DocumentUtility.getNodesElements(rootElement).stream().map(m -> createNode(m, controller))
				.filter(Objects::nonNull).forEach(e -> controller.addNode(e.getSlideId(), e));
	}

	private Node createNode(Element element, Evaluator evaluator) {
		Long slideId = DocumentUtility.getSlideId(element);
		if (null == slideId) {
			return null;
		}

		Node node = new Node(evaluator, slideId);
		Element evaluateElement = DocumentUtility.getEvaluateElement(element);

		if (evaluateElement != null) {
			evaluateElement.children().stream().map(m -> EvaluableUtility.createEvaluable(m, evaluator))
					.filter(Objects::nonNull).forEach(node::add);
		}
		return node;
	}

}
