/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.interfaces.MessageService;
import org.hypothesis.event.data.Message;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MessageManager implements Serializable {

	@Inject
	private MessageService messageService;

	private DocumentReader reader = new XmlDocumentReader();

	/**
	 * Create new message object by provided uid
	 * 
	 * @param uid
	 *            message identifier to look for definition
	 * @param userId
	 *            user identifier passed into message
	 * @return new message object or null when message definition not found
	 */
	public Message createMessage(String uid, Long userId) {
		org.hypothesis.data.model.Message entity = messageService.findMessageByUid(uid);

		if (entity != null) {
			Document document = reader.readString(entity.getData());
			if (DocumentUtility.isValidMessageDocument(document)) {
				Message message = new Message(uid, userId);

				List<Element> properties = DocumentUtility.getMessagePropertyElements(document.root());

				Method method = null;
				try {
					method = message.getClass().getDeclaredMethod("setPropertyDefinition", String.class, Class.class);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (method != null) {
					method.setAccessible(true);

					final Method finalMethod = method;
					properties.stream()
							.filter(f -> StringUtils.isNotBlank(DocumentUtility.getName(f).orElse(null))
									&& StringUtils.isNotBlank(DocumentUtility.getType(f).orElse(null)))
							.forEach(e -> DocumentUtility.getName(e).ifPresent(
									n -> DocumentUtility.getType(e).map(this::getClassFromType).ifPresent(c -> {
									})));
				}

				return message;
			}
		}

		return null;

	}

	private Class<?> getClassFromType(String type) {
		switch (type.toLowerCase()) {
		case DocumentConstants.INTEGER:
			return Integer.class;
		case DocumentConstants.BOOLEAN:
			return Boolean.class;
		case DocumentConstants.FLOAT:
			return Double.class;
		case DocumentConstants.STRING:
			return String.class;
		default:
			return null;
		}
	}
	
	private void tryInvokeMethod(Method method, String message, String name, Class<?> clazz) {
		try {
			// invoking method
			// message.setPropertyDefinition(name,
			// clazz);
			method.invoke(message, name, clazz);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
