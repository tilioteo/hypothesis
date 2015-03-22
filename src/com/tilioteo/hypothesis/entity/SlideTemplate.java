/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.XmlUtility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide template
 * 
 */
@Entity
@Table(name = EntityTableConstants.SLIDE_TEMPLATE_TABLE)
@Access(AccessType.PROPERTY)
public final class SlideTemplate extends SerializableUidObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4690193406502855227L;

	/**
	 * raw xml string of slide template
	 */
	private String xmlData;

	private String note;

	/**
	 * parsed dom document from xml
	 */
	private transient Document document = null;

	@Override
	@Id
	@Column(name = EntityFieldConstants.UID)
	public String getUid() {
		return super.getUid();
	}

	@Column(name = EntityFieldConstants.XML_DATA, nullable = false)
	@Type(type="text")
	protected String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = EntityFieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * get parsed document
	 * @return
	 */
	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = XmlUtility.readString(getXmlData());
		}
		return document;
	}

	public final void setDocument(Document document) {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				updateTepmlateXmlAndUid();
			} else {
				this.document = null;
				updateTepmlateXmlAndUid();
			}
		}
	}

	/**
	 * change unique identificator this method updates uid also in xml
	 * 
	 * @param uid
	 */
	public final void setNewUid(String uid) {
		if (isValidDocument(getDocument()) && this.uid != uid) {
			document.getRootElement().addAttribute(SlideXmlConstants.UID,
					Strings.isNullOrEmpty(uid) ? "" : uid);
			updateTepmlateXmlAndUid();
		}
	}

	private boolean isValidDocument(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName()
				.equals(SlideXmlConstants.SLIDE_TEMPLATE));
	}

	/**
	 * internal method to update xml
	 */
	private void updateTepmlateXmlAndUid() {
		if (this.document != null) {
			setXmlData(XmlUtility.writeString(this.document));
			String uid = document.getRootElement().attributeValue(
					SlideXmlConstants.UID);
			setUid(Strings.isNullOrEmpty(uid) ? null : uid);
		} else {
			setXmlData(null);
			setUid(null);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlideTemplate)) {
			return false;
		}
		SlideTemplate other = (SlideTemplate) obj;

		String uid = getUid();
		String uid2 = other.getUid();
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		String note = getNote();
		String note2 = other.getNote();
		
		if (uid != null && !uid.equals(uid2)) {
			return false;
		} else if (uid2 != null) {
			return false;
		}

		if (xmlData != null && !xmlData.equals(xmlData2)) {
			return false;
		} else if (xmlData2 != null) {
			return false;
		}

		if (note != null && !note.equals(note2)) {
			return false;
		} else if (note2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		String uid = getUid();
		String xmlData = getXmlData();
		String note = getNote();

		final int prime = 43;
		int result = 1;
		result = prime * result	+ (uid != null ? uid.hashCode() : 0);
		result = prime * result	+ (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result	+ (note != null ? note.hashCode() : 0);
		return result;
	}

}
