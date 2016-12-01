/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.SLIDE_ORDER_TABLE)
@org.hibernate.annotations.Table(appliesTo = TableConstants.SLIDE_ORDER_TABLE, indexes = {
		@Index(name = "IX_TEST_TASK", columnNames = { FieldConstants.TEST_ID, FieldConstants.TASK_ID }) })
@Access(AccessType.PROPERTY)
public final class SlideOrder extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5052970377982950760L;

	/**
	 * processing test
	 */
	private SimpleTest test;

	private Task task;

	private String data;

	protected SlideOrder() {
		super();
	}

	public SlideOrder(SimpleTest test, Task task) {
		this.test = test;
		this.task = task;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SLIDE_ORDER_GENERATOR)
	@SequenceGenerator(name = TableConstants.SLIDE_ORDER_GENERATOR, sequenceName = TableConstants.SLIDE_ORDER_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.TEST_ID, nullable = false)
	public SimpleTest getTest() {
		return test;
	}

	public void setTest(SimpleTest test) {
		this.test = test;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.TASK_ID, nullable = false)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Column(name = FieldConstants.XML_DATA)
	@Type(type = "text")
	protected String getData() {
		return data;
	}

	protected void setData(String data) {
		this.data = data;
	}

	@Transient
	public List<Integer> getOrder() {
		LinkedList<Integer> list = new LinkedList<>();

		if (data != null) {
			Arrays.stream(data.split(",")).map(m -> {
				try {
					return (Integer) Integer.parseInt(m);
				} catch (NumberFormatException ex) {
					return null;
				}
			}).filter(Objects::nonNull).forEach(list::add);
		}

		return list;
	}

	public void setOrder(List<Integer> list) {
		if (list != null) {
			data = list.stream().map(m -> m.toString()).collect(Collectors.joining(","));
		} else {
			data = null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlideOrder)) {
			return false;
		}
		SlideOrder other = (SlideOrder) obj;

		Long id = getId();
		Long id2 = other.getId();
		SimpleTest test = getTest();
		SimpleTest test2 = other.getTest();
		Task task = getTask();
		Task task2 = other.getTask();
		String xmlData = getData();
		String xmlData2 = other.getData();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (test == null) {
			if (test2 != null) {
				return false;
			}
		} else if (!test.equals(test2)) {
			return false;
		}

		if (task == null) {
			if (task2 != null) {
				return false;
			}
		} else if (!task.equals(task2)) {
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

	@Override
	public int hashCode() {
		Long id = getId();
		SimpleTest test = getTest();
		Task task = getTask();
		String xmlData = getData();

		final int prime = 37;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (test != null ? test.hashCode() : 0);
		result = prime * result + (task != null ? task.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		return result;
	}

}
