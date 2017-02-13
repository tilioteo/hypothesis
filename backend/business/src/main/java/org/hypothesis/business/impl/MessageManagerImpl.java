/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.MessageManager;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.interfaces.MessageService;
import org.hypothesis.event.data.Message;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MessageManagerImpl implements Serializable, MessageManager {

	@Inject
	private MessageService messageService;

	private DocumentReader reader = new XmlDocumentReader();

	/* (non-Javadoc)
	 * @see org.hypothesis.business.impl.MessageManager#createMessage(java.lang.String, java.lang.Long)
	 */
	@Override
	public Message createMessage(String uid, Long userId) {
		return Optional.ofNullable(messageService.findById(uid)).map(m -> reader.readString(m.getData()))
				.filter(DocumentUtility::isValidMessageDocument).map(m -> {
					final Message message = new Message(uid, userId);

					List<Element> properties = DocumentUtility.getMessagePropertyElements(m.root());

					Method method = null;
					try {
						method = message.getClass().getDeclaredMethod("setPropertyDefinition", String.class,
								Class.class);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (method != null) {
						method.setAccessible(true);

						final Method finalMethod = method;
						properties.stream()
								.filter(f -> StringUtils.isNotBlank(DocumentUtility.getName(f).orElse(null))
										&& StringUtils.isNotBlank(DocumentUtility.getType(f).orElse(null)))
								.forEach(e -> DocumentUtility.getName(e)
										.ifPresent(n -> DocumentUtility.getType(e).map(this::getClassFromType)
												.ifPresent(c -> tryInvokeMethod(finalMethod, message, n, c))));
					}

					return message;
				}).orElse(null);
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

	private void tryInvokeMethod(Method method, Message message, String name, Class<?> clazz) {
		try {
			// invoking method
			// message.setPropertyDefinition(name, clazz);
			method.invoke(message, name, clazz);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
