/**
 * 
 */
package org.hypothesis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
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
public final class SlideTemplate extends SerializableUidObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4690193406502855227L;

	/**
	 * raw xml string of slide template
	 */
	private String templateXml;
	private String note;

	/**
	 * parsed dom document from xml
	 */
	private transient Document document = null;

	/*
	 * @Override
	 * 
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "slideTemplateGenerator")
	 * 
	 * @SequenceGenerator(name = "slideTemplateGenerator", sequenceName =
	 * "hbn_slide_template_seq", initialValue = 1, allocationSize = 1)
	 * 
	 * @Column(name = "ID") public Long getId() { return super.getId(); }
	 */

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlideTemplate))
			return false;
		SlideTemplate other = (SlideTemplate) obj;
		/*
		 * if (getId() == null) { if (other.getId() != null) return false; }
		 * else if (!getId().equals(other.getId())) return false;
		 */
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
		if (getTemplateXml() == null) {
			if (other.getTemplateXml() != null)
				return false;
		} else if (!getTemplateXml().equals(other.getTemplateXml()))
			return false;
		return true;
	}

	@Transient
	/**
	 * get parsed document
	 * @return
	 */
	public final Document getDocument() {
		if (document == null) {
			document = Utility.readString(getTemplateXml());
		}
		return document;
	}

	@Column(name = "NOTE")
	public final String getNote() {
		return note;
	}

	@Lob
	@Column(name = "TEMPLATE_XML", nullable = false)
	protected String getTemplateXml() {
		return templateXml;
	}

	@Override
	@Id
	@Column(name = "UID")
	public final String getUid() {
		return super.getUid();
	}

	@Override
	public final int hashCode() {
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
				+ ((getTemplateXml() == null) ? 0 : getTemplateXml().hashCode());
		return result;
	}

	private boolean isValidDocument(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName()
				.equals(SlideXmlConstants.SLIDE_TEMPLATE));
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

	public final void setNote(String note) {
		this.note = note;
	}

	protected void setTemplateXml(String templateXml) {
		this.templateXml = templateXml;
	}

	/**
	 * internal method to update xml
	 */
	private void updateTepmlateXmlAndUid() {
		if (this.document != null) {
			setTemplateXml(Utility.writeString(this.document));
			String uid = document.getRootElement().attributeValue(
					SlideXmlConstants.UID);
			setUid(Strings.isNullOrEmpty(uid) ? null : uid);
		} else {
			setTemplateXml(null);
			setUid(null);
		}
	}

}
