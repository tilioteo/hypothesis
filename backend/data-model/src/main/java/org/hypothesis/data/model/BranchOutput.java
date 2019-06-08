package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.BRANCH_OUTPUT_TABLE)
@Access(AccessType.PROPERTY)
public class BranchOutput implements Serializable, HasId<Long> {

	private Long id;

	/**
	 * processing branch
	 */
	private long branchId;

	/**
	 * processing test
	 */
	private long testId;

	/**
	 * saved data
	 */
	private String data;

	/**
	 * output of slide
	 */
	private String output;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_OUTPUT_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_OUTPUT_GENERATOR, sequenceName = TableConstants.BRANCH_OUTPUT_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.BRANCH_ID, nullable = false)
	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	@Column(name = FieldConstants.TEST_ID, nullable = false)
	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
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
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BranchOutput == false)
			return false;

		final BranchOutput other = (BranchOutput) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getBranchId(), other.getBranchId()))
			return false;
		if (!Objects.equals(getTestId(), other.getTestId()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(getOutput(), other.getOutput()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BranchOutput [id=" + id + ", branchId=" + branchId + ", testId=" + testId + ", output=" + output
				+ ", data=" + data + "]";
	}

}
