/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.util.List;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.business.TaskController;
import com.tilioteo.hypothesis.common.utility.DocumentUtility;
import com.tilioteo.hypothesis.common.utility.EvaluableUtility;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.evaluation.Node;
import com.tilioteo.hypothesis.interfaces.Document;
import com.tilioteo.hypothesis.interfaces.Element;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.Evaluator;

/**
 * @author kamil
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
		List<Element> nodes = DocumentUtility.getNodesElements(rootElement);
		for (Element nodeElement : nodes) {
			Node node = createNode(nodeElement, controller);
			if (node != null)
				controller.addNode(node.getSlideId(), node);
		}
	}

	private Node createNode(Element element, Evaluator evaluator) {
		Long slideId = DocumentUtility.getSlideId(element);
		if (slideId != null) {
			Node node = new Node(evaluator, slideId);
			Element evaluateElement = DocumentUtility.getEvaluateElement(element);

			if (evaluateElement != null) {
				List<Element> evaluables = evaluateElement.children();
				for (Element evaluableElement : evaluables) {
					Evaluable evaluable = EvaluableUtility.createEvaluable(evaluableElement, evaluator);
					if (evaluable != null) {
						node.add(evaluable);
					}
				}
			}
			return node;
		}

		return null;
	}

}
