/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.dom.AbstractSlideXmlException;
import com.tilioteo.hypothesis.dom.InvalidSlideContentXmlException;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.XmlUtility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide content Slide content depends on slide
 *         template and the uid must be equal
 * 
 */
@Entity
@Table(name = "TBL_SLIDE_CONTENT")
@Access(AccessType.PROPERTY)
public final class SlideContent extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6388844756238553760L;

	/**
	 * the parent slide template
	 */
	private SlideTemplate template;

	/**
	 * raw xml string of slide content
	 */
	private String contentXml;

	private String note;

	/**
	 * parsed dom document from xml
	 */
	private transient Document document = null;

	protected SlideContent() {
		super();
	}

	public SlideContent(SlideTemplate template) {
		this();
		this.template = template;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slideContentGenerator")
	@SequenceGenerator(name = "slideContentGenerator", sequenceName = "hbn_slide_content_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinColumn(name = "SLIDE_TEMPLATE_UID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final SlideTemplate getTemplate() {
		return template;
	}

	protected void setTemplate(SlideTemplate slideTemplate) {
		this.template = slideTemplate;
	}

	@Column(name = "CONTENT_XML", nullable = false)
	@Type(type="text")
	protected String getContentXml() {
		return contentXml;
	}

	protected void setContentXml(String contentXml) {
		this.contentXml = contentXml;
	}

	@Column(name = "NOTE")
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = XmlUtility.readString(getContentXml());
		}
		return document;
	}

	public final void setDocument(Document document)
			throws AbstractSlideXmlException {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				this.contentXml = XmlUtility.writeString(this.document);
			} else {
				/*
				 * this.document = null; this.contentXml = null;
				 */
				throw new InvalidSlideContentXmlException();
			}
			// getTemplateUid();
		}
	}

	@Transient
	/**
	 * get the parent template's unique identificator
	 * @return
	 */
	public final String getTemplateUid() {
		return getTemplate() != null ? getTemplate().getUid() : null;
	}

	@Transient
	/**
	 * get the parent template's document
	 * @return
	 */
	public final Document getTemplateDocument() {
		return getTemplate() != null ? getTemplate().getDocument() : null;
	}

	/**
	 * this method checks the validity of slide document against the template
	 * document slide and template must have equal uid
	 * 
	 * @param doc
	 * @return
	 */
	private boolean isValidDocument(Document doc) {
		return (doc != null
				&& doc.getRootElement() != null
				&& doc.getRootElement().getName()
						.equals(SlideXmlConstants.SLIDE_CONTENT)
				&& getTemplate() != null && getTemplate().getUid() != null && doc
				.getRootElement()
				.attributeValue(SlideXmlConstants.TEMPLATE_UID)
				.equals(getTemplate().getUid()));
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlideContent))
			return false;
		SlideContent other = (SlideContent) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getContentXml() == null) {
			if (other.getContentXml() != null)
				return false;
		} else if (!getContentXml().equals(other.getContentXml()))
			return false;
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 13;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getContentXml() == null) ? 0 : getContentXml().hashCode());
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		return result;
	}

}
