package org.hypothesis.data.dto;

import java.util.List;

@SuppressWarnings("serial")
public class PackSetDto extends EntityDto<Long> {

	private String name;

	private List<PackDto> packs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PackDto> getPacks() {
		return packs;
	}

	public void setPacks(List<PackDto> packs) {
		this.packs = packs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((packs == null) ? 0 : packs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackSetDto other = (PackSetDto) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (packs == null) {
			if (other.packs != null)
				return false;
		} else if (!packs.equals(other.packs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PackSetDto [id=" + getId() + "name=" + name + ", packs=" + packs + "]";
	}

}
