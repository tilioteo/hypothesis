/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.BRANCH_OUTPUT_TABLE)
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
	private String data;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_OUTPUT_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_OUTPUT_GENERATOR, sequenceName = TableConstants.BRANCH_OUTPUT_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.BRANCH_ID, nullable = false)
	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.TEST_ID, nullable = false)
	public SimpleTest getTest() {
		return test;
	}

	public void setTest(SimpleTest test) {
		this.test = test;
	}

	@Column(name = FieldConstants.XML_DATA)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = FieldConstants.OUTPUT)
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		Branch branch = getBranch();
		SimpleTest test = getTest();
		String xmlData = getData();
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
		String xmlData = getData();
		String xmlData2 = other.getData();
		String output = getOutput();
		String output2 = other.getOutput();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (branch == null) {
			if (branch2 != null) {
				return false;
			}
		} else if (!branch.equals(branch2)) {
			return false;
		}

		if (output == null) {
			if (output2 != null) {
				return false;
			}
		} else if (!output.equals(output2)) {
			return false;
		}

		if (test == null) {
			if (test2 != null) {
				return false;
			}
		} else if (!test.equals(test2)) {
			return false;
		}

		if (xmlData == null) {
			if (xmlData2 != null) {
				return false;
			}
		} else if (!xmlData.equals(xmlData2)) {
			return false;
		}

		return true;
	}

}
