/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.hypothesis.builder.TaskControllerFactory;
import com.tilioteo.hypothesis.business.TaskController;
import com.tilioteo.hypothesis.evaluation.Node;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.Evaluator;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TaskControllerXmlFactory implements TaskControllerFactory {

	private static Logger log = Logger.getLogger(TaskControllerXmlFactory.class);

	@Override
	public TaskController buildTaskController(String data) {
		Document document = null;

		try {
			document = XmlUtility.readString(data);
		} catch (Throwable e) {
		}

		if (null == document) {
			log.warn("Task document is NULL");
			return null;
		}

		if (XmlDocumentUtility.isValidTaskXml(document)) {
			return buildTaskController(document);
		}

		return null;
	}

	private TaskController buildTaskController(Document document) {
		TaskController controller = new TaskController();

		EvaluableXmlUtility.createActions(document.getRootElement(), controller);
		EvaluableXmlUtility.createVariables(document.getRootElement(), controller, null);

		createNodes(document.getRootElement(), controller);

		return controller;
	}

	private void createNodes(Element rootElement, TaskController controller) {
		List<Element> nodes = XmlDocumentUtility.getNodesElements(rootElement);
		for (Element nodeElement : nodes) {
			Node node = createNode(nodeElement, controller);
			if (node != null)
				controller.addNode(node.getSlideId(), node);
		}
	}

	@SuppressWarnings("unchecked")
	private Node createNode(Element element, Evaluator evaluator) {
		Long slideId = XmlDocumentUtility.getSlideId(element);
		if (slideId != null) {
			Node node = new Node(evaluator, slideId);
			Element evaluateElement = XmlDocumentUtility.getEvaluateElement(element);

			if (evaluateElement != null) {
				List<Element> evaluables = evaluateElement.elements();
				for (Element evaluableElement : evaluables) {
					Evaluable evaluable = EvaluableXmlUtility.createEvaluable(evaluableElement, evaluator);
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
