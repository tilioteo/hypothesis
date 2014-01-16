/**
 * 
 */
package org.hypothesis.entity;

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
import org.hypothesis.common.SerializableIdObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@Entity
@Table(name = "TBL_BRANCH_OUTPUT")
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
	private String data;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branchOutputGenerator")
	@SequenceGenerator(name = "branchOutputGenerator", sequenceName = "hbn_branch_output_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "BRANCH_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Branch getBranch() {
		return branch;
	}

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "TEST_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Test getTest() {
		return test;
	}

	public final void setTest(Test test) {
		this.test = test;
	}

	@Column(name = "DATA")
	@Type(type="text")
	public final String getData() {
		return data;
	}

	public final void setData(String data) {
		this.data = data;
	}

	@Column(name = "OUTPUT")
	public final String getOutput() {
		return output;
	}

	public final void setOutput(String output) {
		this.output = output;
	}

}
