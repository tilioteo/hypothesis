/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.common.utility.DocumentUtility;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.data.XmlDocumentReader;
import com.tilioteo.hypothesis.data.service.MessageService;
import com.tilioteo.hypothesis.event.data.Message;
import com.tilioteo.hypothesis.interfaces.Document;
import com.tilioteo.hypothesis.interfaces.DocumentConstants;
import com.tilioteo.hypothesis.interfaces.Element;

/**
 * @author kamil
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
		com.tilioteo.hypothesis.data.model.Message entity = messageService.findMessageByUid(uid);

		if (entity != null) {
			Document document = reader.readString(entity.getData());
			if (DocumentUtility.isValidMessageDocument(document)) {
				List<Element> properties = DocumentUtility.getPropertyElements(document.root());
				Message message = new Message(uid, userId);

				Method method = null;
				try {
					method = message.getClass().getDeclaredMethod("setPropertyDefinition", String.class, Class.class);
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

		return null;
	}
}
