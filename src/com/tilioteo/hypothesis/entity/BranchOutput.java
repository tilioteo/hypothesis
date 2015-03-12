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
	private SimpleTest test;

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

	public BranchOutput(SimpleTest test, Branch branch) {
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

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.BRANCH_ID, nullable = false)
	public final Branch getBranch() {
		return branch;
	}

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.TEST_ID, nullable = false)
	public final SimpleTest getTest() {
		return test;
	}

	public final void setTest(SimpleTest test) {
		this.test = test;
	}

	@Column(name = EntityFieldConstants.XML_DATA)
	@Type(type="text")
	public final String getXmlData() {
		return xmlData;
	}

	public final void setXmlData(String data) {
		this.xmlData = data;
	}

	@Column(name = EntityFieldConstants.OUTPUT)
	public final String getOutput() {
		return output;
	}

	public final void setOutput(String output) {
		this.output = output;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		Branch branch = getBranch();
		SimpleTest test = getTest();
		String xmlData = getXmlData();
		String output = getOutput();
		
		final int prime = 5;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (branch != null ? branch.hashCode() : 0);
		result = prime * result + (output != null ? output.hashCode() : 0);
		result = prime * result + (test != null ? test.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BranchOutput)) {
			return false;
		}
		BranchOutput other = (BranchOutput) obj;

		Long id = getId();
		Long id2 = other.getId();
		Branch branch = getBranch();
		Branch branch2 = other.getBranch();
		SimpleTest test = getTest();
		SimpleTest test2 = other.getTest();
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		String output = getOutput();
		String output2 = other.getOutput();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (branch != null && !branch.equals(branch2)) {
			return false;
		} else if (branch2 != null) {
			return false;
		}
		if (output != null && !output.equals(output2)) {
			return false;
		} else if (output2 != null) {
			return false;
		}
		if (test != null && !test.equals(test2)) {
			return false;
		} else if (test2 != null) {
			return false;
		}
		if (xmlData != null && !xmlData.equals(xmlData2)) {
			return false;
		} else if (xmlData2 != null) {
			return false;
		}
		return true;
	}

	
}
