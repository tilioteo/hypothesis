/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.builder.xml.XmlDocumentUtility;
import com.tilioteo.hypothesis.data.service.MessageService;
import com.tilioteo.hypothesis.event.data.Message;
import com.tilioteo.hypothesis.messaging.MessageXmlConstants;
import com.tilioteo.hypothesis.messaging.MessageXmlUtility;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MessageManager implements Serializable {

	private MessageService messageService;

	public MessageManager() {
		messageService = MessageService.newInstance();
	}

	public Message createMessage(String uid, Long userId) {
		com.tilioteo.hypothesis.data.model.Message entity = messageService.findMessageByUid(uid);

		if (entity != null) {
			Document doc = XmlUtility.readString(entity.getData());
			if (MessageXmlUtility.isValidMessageXml(doc)) {
				List<Element> properties = MessageXmlUtility.getPropertyElements(doc.getRootElement());
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
						String name = XmlDocumentUtility.getName(propertyElement);
						String type = XmlDocumentUtility.getType(propertyElement);
						if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(type)) {
							return null;
						}
						Class<?> clazz;
						if (MessageXmlConstants.INTEGER.equalsIgnoreCase(type)) {
							clazz = Integer.class;
						} else if (MessageXmlConstants.BOOLEAN.equalsIgnoreCase(type)) {
							clazz = Boolean.class;
						} else if (MessageXmlConstants.FLOAT.equalsIgnoreCase(type)) {
							clazz = Double.class;
						} else if (MessageXmlConstants.STRING.equalsIgnoreCase(type)) {
							clazz = String.class;
						} else {
							return null;
						}

						// message.setPropertyDefinition(name, clazz);

						try {
							// invoking message.setPropertyDefinition(name, clazz);
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
