/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;
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
@Table(name = EntityTableConstants.SLIDE_CONTENT_TABLE)
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
	private String xmlData;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.SLIDE_CONTENT_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.SLIDE_CONTENT_GENERATOR, sequenceName = EntityTableConstants.SLIDE_CONTENT_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityFieldConstants.SLIDE_TEMPLATE_UID, nullable = false)
	public final SlideTemplate getTemplate() {
		return template;
	}

	protected void setTemplate(SlideTemplate slideTemplate) {
		this.template = slideTemplate;
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
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = XmlUtility.readString(getXmlData());
		}
		return document;
	}

	public final void setDocument(Document document)
			throws AbstractSlideXmlException {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				this.xmlData = XmlUtility.writeString(this.document);
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlideContent)) {
			return false;
		}
		SlideContent other = (SlideContent) obj;

		Long id = getId();
		Long id2 = other.getId();
		SlideTemplate template = getTemplate();
		SlideTemplate template2 = other.getTemplate();
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		String note = getNote();
		String note2 = other.getNote();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (template != null && !template.equals(template2)) {
			return false;
		} else if (template2 != null) {
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
	public final int hashCode() {
		Long id = getId();
		SlideTemplate template = getTemplate();
		String xmlData = getXmlData();
		String note = getNote();

		final int prime = 31;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (template != null ? template.hashCode() : 0);
		result = prime * result	+ (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		return result;
	}

}
