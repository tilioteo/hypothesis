/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.service.MessageService;
import org.hypothesis.event.data.Message;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MessageManager implements Serializable {

	private MessageService messageService;

	private DocumentReader reader = new XmlDocumentReader();

	public MessageManager() {
		messageService = MessageService.newInstance();
	}

	public Message createMessage(String uid, Long userId) {
		org.hypothesis.data.model.Message entity = messageService.findMessageByUid(uid);

		if (entity != null) {
			Document document = reader.readString(entity.getData());
			if (DocumentUtility.isValidMessageDocument(document)) {
				List<Element> properties = DocumentUtility.getPropertyElements(document.root());

				if (properties != null) {
					Message message = new Message(uid, userId);

					Method method = null;
					try {
						method = message.getClass().getDeclaredMethod("setPropertyDefinition", String.class,
								Class.class);
					} catch (Throwable e) {
						e.printStackTrace();
					}

					if (method != null) {
						method.setAccessible(true);

						for (Element propertyElement : properties) {
							String name = DocumentUtility.getName(propertyElement);
							String type = DocumentUtility.getType(propertyElement);
							if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(type)) {
								return null;
							}
							Class<?> clazz;
							if (DocumentConstants.INTEGER.equalsIgnoreCase(type)) {
								clazz = Integer.class;
							} else if (DocumentConstants.BOOLEAN.equalsIgnoreCase(type)) {
								clazz = Boolean.class;
							} else if (DocumentConstants.FLOAT.equalsIgnoreCase(type)) {
								clazz = Double.class;
							} else if (DocumentConstants.STRING.equalsIgnoreCase(type)) {
								clazz = String.class;
							} else {
								return null;
							}

							// message.setPropertyDefinition(name, clazz);

							try {
								// invoking message.setPropertyDefinition(name,
								// clazz);
								method.invoke(message, name, clazz);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}

					return message;
				}
			}
		}

		return null;
	}
}
