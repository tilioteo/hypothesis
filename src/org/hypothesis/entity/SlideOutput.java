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

import org.hibernate.annotations.Type;
import org.hypothesis.common.SerializableIdObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@Entity
@Table(name = "TBL_SLIDE_OUTPUT")
@Access(AccessType.PROPERTY)
public final class SlideOutput extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5950921628523616483L;

	/**
	 * processing slide
	 */
	private Slide slide;

	/**
	 * processing test
	 */
	private Test test;

	/**
	 * saved data (form fields etc.)
	 */
	private String xmlData;

	/**
	 * output of slide
	 */
	private String output;

	protected SlideOutput() {
		super();
	}

	public SlideOutput(Test test, Slide slide) {
		this();
		this.test = test;
		this.slide = slide;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slideOutputGenerator")
	@SequenceGenerator(name = "slideOutputGenerator", sequenceName = "hbn_slide_output_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = "SLIDE_ID", nullable = false)
	public final Slide getSlide() {
		return slide;
	}

	public final void setSlide(Slide slide) {
		this.slide = slide;
	}

	@ManyToOne
	@JoinColumn(name = "TEST_ID", nullable = false)
	public final Test getTest() {
		return test;
	}

	public final void setTest(Test test) {
		this.test = test;
	}

	@Column(name = "XML_DATA")
	@Type(type="text")
	public final String getXmlData() {
		return xmlData;
	}

	public final void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = "OUTPUT")
	public final String getOutput() {
		return output;
	}

	public final void setOutput(String output) {
		this.output = output;
	}

}
