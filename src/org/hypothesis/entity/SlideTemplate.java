/**
 * 
 */
package org.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.hibernate.annotations.Type;
import org.hypothesis.application.common.xml.SlideXmlConstants;
import org.hypothesis.common.SerializableUidObject;
import org.hypothesis.common.Strings;
import org.hypothesis.common.xml.Utility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide template
 * 
 */
@Entity
@Table(name = "TBL_SLIDE_TEMPLATE")
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
	@Column(name = "UID")
	public String getUid() {
		return super.getUid();
	}

	@Column(name = "XML_DATA", nullable = false)
	@Type(type="text")
	protected String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = "NOTE")
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
			document = Utility.readString(getXmlData());
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
			setXmlData(Utility.writeString(this.document));
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlideTemplate))
			return false;
		SlideTemplate other = (SlideTemplate) obj;
		if (getUid() == null) {
			if (other.getUid() != null)
				return false;
		} else if (!getUid().equals(other.getUid()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		if (getXmlData() == null) {
			if (other.getXmlData() != null)
				return false;
		} else if (!getXmlData().equals(other.getXmlData()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 3;
		int result = 1;
		// result = prime * result + ((getId() == null) ? 0 :
		// getId().hashCode());
		result = prime * result
				+ ((getUid() == null) ? 0 : getUid().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		result = prime
				* result
				+ ((getXmlData() == null) ? 0 : getXmlData().hashCode());
		return result;
	}

}
