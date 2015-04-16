/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.LinkedList;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 * @author kamil
 *
 */
@Entity
@Table(name = TableConstants.SLIDE_ORDER_TABLE)
@org.hibernate.annotations.Table(appliesTo = TableConstants.SLIDE_ORDER_TABLE,
indexes = { @Index(name = "IX_TEST_TASK", columnNames = { FieldConstants.TEST_ID, FieldConstants.TASK_ID })})
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
	
	private String xmlData;

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
	@Type(type="text")
	protected String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String data) {
		this.xmlData = data;
	}
	
	@Transient
	public final List<Integer> getOrder() {
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		if (xmlData != null) {
			String[] strings = xmlData.split(",");
			
			for (String string : strings) {
				Integer item = Integer.getInteger(string);
				if (item != null) {
					list.add(item);
				}
			}
		}
		
		return list;
	}
	
	public final void setOrder(List<Integer> list) {
		if (list != null) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (Integer item : list) {
				if (item != null) {
					if (first) {
						first = false;
					} else {
						builder.append(",");
					}
					builder.append(item);
				}
			}
			xmlData = builder.toString();
			
		} else {
			xmlData = null;
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
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		
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
		String xmlData = getXmlData();

		final int prime = 37;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (test != null ? test.hashCode() : 0);
		result = prime * result + (task != null ? task.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		return result;
	}

}
