/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.dom.MessageXmlConstants;
import com.tilioteo.hypothesis.dom.MessageXmlUtility;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.persistence.MessageService;

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
		com.tilioteo.hypothesis.entity.Message entity = messageService.findMessageByUid(uid);
		
		if (entity != null) {
			Document doc = XmlUtility.readString(entity.getXmlData());
			if (MessageXmlUtility.isValidMessageXml(doc)) {
				List<Element> properties = MessageXmlUtility.getPropertyElements(doc.getRootElement());
				Message message = new Message(uid, userId);
				
				for (Element propertyElement : properties) {
					String name = SlideXmlUtility.getName(propertyElement);
					String type = SlideXmlUtility.getType(propertyElement);
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
					
					message.setPropertyDefinition(name, clazz);
				}
					
				return message;
			}
		}
		
		return null;
	}
}
