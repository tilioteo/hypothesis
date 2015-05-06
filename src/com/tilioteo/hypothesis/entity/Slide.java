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

import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.interfaces.SlideEntity;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide
 * 
 */
@Entity
@Table(name = TableConstants.SLIDE_TABLE)
@Access(AccessType.PROPERTY)
public final class Slide extends SerializableIdObject implements SlideEntity {

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


	protected Slide() {
		super();
	}

	public Slide(SlideTemplate template) {
		this();
		this.template = template;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SLIDE_GENERATOR)
	@SequenceGenerator(name = TableConstants.SLIDE_GENERATOR, sequenceName = TableConstants.SLIDE_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	// TODO: debug only
	/*@Override
	public void setId(Long id) {
		this.id = id;
	}*/

	@ManyToOne(optional = false)
	@JoinColumn(name = FieldConstants.SLIDE_TEMPLATE_UID, nullable = false)
	public SlideTemplate getTemplate() {
		return template;
	}

	protected void setTemplate(SlideTemplate slideTemplate) {
		this.template = slideTemplate;
	}

	@Column(name = FieldConstants.XML_DATA, nullable = false)
	@Type(type="text")
	public String getXmlData() {
		return xmlData;
	}

	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * get the parent template's unique identificator
	 * @return
	 */
	@Transient
	public final String getTemplateUid() {
		return getTemplate() != null ? getTemplate().getUid() : null;
	}

	/**
	 * get the parent template's document
	 * @return
	 */
	@Transient
	public final String getTemplateXmlData() {
		return getTemplate() != null ? getTemplate().getXmlData() : null;
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

		Long id = getId();
		Long id2 = other.getId();
		SlideTemplate template = getTemplate();
		SlideTemplate template2 = other.getTemplate();
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		String note = getNote();
		String note2 = other.getNote();
		
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (template == null) {
			if (template2 != null) {
				return false;
			}
		} else if (!template.equals(template2)) {
			return false;
		}
		
		if (xmlData == null) {
			if (xmlData2 != null) {
				return false;
			}
		} else if (!xmlData.equals(xmlData2)) {
			return false;
		}
		
		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String xmlData = getXmlData();
		SlideTemplate template = getTemplate();
		String note = getNote();

		final int prime = 29;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result	+ (template != null ? template.hashCode() : 0);
		result = prime * result	+ (note != null ? note.hashCode() : 0);

		return result;
	}

}
