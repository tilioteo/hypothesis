/**
 * 
 */
package org.hypothesis.data.dto;

import java.util.List;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
public class PackDto extends EntityDto<Long> {

	private String name;
	private String description;
	private String note;
	private Boolean published = false;
	private List<BranchDto> branches;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public List<BranchDto> getBranches() {
		return branches;
	}

	public void setBranches(List<BranchDto> branches) {
		this.branches = branches;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((branches == null) ? 0 : branches.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((published == null) ? 0 : published.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;
		PackDto other = (PackDto) obj;
		if (branches == null) {
			if (other.branches != null)
				return false;
		} else if (!branches.equals(other.branches))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (published == null) {
			if (other.published != null)
				return false;
		} else if (!published.equals(other.published))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PackDto [id=" + getId() + "name=" + name + ", description=" + description + ", note=" + note
				+ ", published=" + published + ", branches=" + branches + "]";
	}

}
