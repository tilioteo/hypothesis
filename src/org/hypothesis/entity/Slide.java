/**
 * 
 */
package org.hypothesis.entity;

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
import org.hypothesis.application.common.xml.SlideXmlConstants;
import org.hypothesis.common.SerializableIdObject;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.core.AbstractSlideXmlException;
import org.hypothesis.core.InvalidSlideContentXmlException;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide
 *         Slide content depends on slide template and the uid must be equal
 */
@Entity
@Table(name = "TBL_SLIDE")
@Access(AccessType.PROPERTY)
public final class Slide extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6866522778488675162L;

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

	protected Slide() {
		super();
	}

	public Slide(SlideTemplate template) {
		this();
		this.template = template;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slideGenerator")
	@SequenceGenerator(name = "slideGenerator", sequenceName = "hbn_slide_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	// TODO: debug only
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "SLIDE_TEMPLATE_UID", nullable = false)
	public SlideTemplate getTemplate() {
		return template;
	}

	protected void setTemplate(SlideTemplate slideTemplate) {
		this.template = slideTemplate;
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

	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = Utility.readString(getXmlData());
		}
		return document;
	}

	public final void setDocument(Document document) throws AbstractSlideXmlException {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				this.xmlData = Utility.writeString(this.document);
			} else {
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
		return (doc != null	&&
				doc.getRootElement() != null &&
				doc.getRootElement().getName().equals(SlideXmlConstants.SLIDE_CONTENT) &&
				getTemplate() != null &&
				getTemplate().getUid() != null &&
				doc.getRootElement().attributeValue(SlideXmlConstants.TEMPLATE_UID).equals(getTemplate().getUid()));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Slide))
			return false;
		Slide other = (Slide) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		//if (getTemplate() == null) {
		//	if (other.getTemplate() != null)
		//		return false;
		//} else if (!getTemplate().equals(other.getTemplate()))
		//	return false;
		if (getXmlData() == null) {
			if (other.getXmlData() != null)
				return false;
		} else if (!getXmlData().equals(other.getXmlData()))
			return false;
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result	+ ((getXmlData() == null) ? 0 : getXmlData().hashCode());
		//result = prime * result	+ ((getTemplate() == null) ? 0 : getTemplate().hashCode());
		result = prime * result	+ ((getNote() == null) ? 0 : getNote().hashCode());
		return result;
	}

}
