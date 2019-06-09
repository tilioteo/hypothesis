/**
 * 
 */
package org.hypothesis.data.dto;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
public class TemplateDto extends EntityDto<String> {

	private String note;
	private String data;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;
		TemplateDto other = (TemplateDto) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TemplateDto [id=" + getId() + ", note=" + note + ", data=" + data + "]";
	}

}
