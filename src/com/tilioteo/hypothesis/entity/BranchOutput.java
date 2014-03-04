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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@Entity
@Table(name = EntityTableConstants.BRANCH_OUTPUT_TABLE)
@Access(AccessType.PROPERTY)
public final class BranchOutput extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8648019934698732389L;

	/**
	 * processing branch
	 */
	private Branch branch;

	/**
	 * processing test
	 */
	private Test test;

	/**
	 * saved data
	 */
	private String xmlData;

	/**
	 * output of slide
	 */
	private String output;

	protected BranchOutput() {
		super();
	}

	public BranchOutput(Test test, Branch branch) {
		this();
		this.test = test;
		this.branch = branch;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.BRANCH_OUTPUT_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.BRANCH_OUTPUT_GENERATOR, sequenceName = EntityTableConstants.BRANCH_OUTPUT_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.BRANCH_ID, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Branch getBranch() {
		return branch;
	}

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.TEST_ID, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Test getTest() {
		return test;
	}

	public final void setTest(Test test) {
		this.test = test;
	}

	@Column(name = EntityFieldConstants.XML_DATA)
	@Type(type="text")
	public final String getData() {
		return xmlData;
	}

	public final void setData(String data) {
		this.xmlData = data;
	}

	@Column(name = EntityFieldConstants.OUTPUT)
	public final String getOutput() {
		return output;
	}

	public final void setOutput(String output) {
		this.output = output;
	}

}
