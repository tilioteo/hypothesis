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
public class TaskDto extends EntityDto<Long> {

	private String name;
	private String note;
	private boolean randomized;
	private String data;
	private List<SlideDto> slides;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isRandomized() {
		return randomized;
	}

	public void setRandomized(boolean randomized) {
		this.randomized = randomized;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<SlideDto> getSlides() {
		return slides;
	}

	public void setSlides(List<SlideDto> slides) {
		this.slides = slides;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + (randomized ? 1231 : 1237);
		result = prime * result + ((slides == null) ? 0 : slides.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}

		TaskDto other = (TaskDto) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
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
		if (randomized != other.randomized)
			return false;
		if (slides == null) {
			if (other.slides != null)
				return false;
		} else if (!slides.equals(other.slides))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskDto [id=" + getId() + "name=" + name + ", note=" + note + ", randomized=" + randomized + ", data="
				+ data + ", slides=" + slides + "]";
	}

}
