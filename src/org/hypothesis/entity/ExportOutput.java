/**
 * 
 */
package org.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.Type;
import org.hypothesis.common.SerializableIdObject;

/**
 * @author kamil
 *
 */
@Entity
@Subselect("SELECT * FROM TBL_SLIDE_OUTPUT")
@Synchronize("TBL_SLIDE_OUTPUT")
@Immutable
@Access(AccessType.PROPERTY)
public class ExportOutput extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8569011626108049869L;

	/**
	 * processing slide id
	 */
	private Long slideId;

	/**
	 * processing test
	 */
	private Long testId;

	/**
	 * saved data (form fields etc.)
	 */
	private String xmlData;

	/**
	 * output of slide
	 */
	private String output;

	protected ExportOutput() {
		super();
	}

	@Override
	@Id
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	@Column(name = "SLIDE_ID")
	public Long getSlideId() {
		return slideId;
	}

	protected void setSlideId(Long slideId) {
		this.slideId = slideId;
	}

	@Column(name = "TEST_ID")
	public Long getTestId() {
		return testId;
	}

	protected void setTestId(Long testId) {
		this.testId = testId;
	}

	@Column(name = "XML_DATA")
	@Type(type="text")
	public String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = "OUTPUT")
	public String getOutput() {
		return output;
	}

	protected void setOutput(String output) {
		this.output = output;
	}

}
