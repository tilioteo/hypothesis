/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.List;

import org.apache.log4j.Logger;
import org.hypothesis.business.TaskController;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.Evaluable;
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
		List<Element> nodes = DocumentUtility.getNodesElements(rootElement);
		
		if (nodes != null) {
			for (Element nodeElement : nodes) {
				Node node = createNode(nodeElement, controller);
				if (node != null)
					controller.addNode(node.getSlideId(), node);
			}
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
